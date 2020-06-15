package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification;

//import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.log4j.Logger;
//import org.springframework.util.StringUtils;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by miawang on 4/27/2018.
 */
public class SearchRequestOptimizationVerification implements ISearchVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        try
        {
            Map<String, List<String>> vendorLocMapFromGdsMsg = getVendorLocMapFromGdsMsg(verificationContext, remarks);
            Map<String, List<String>> vendorLocMapFromSCSSearchCriteria = getVendorLocMapFromSCSSearchCriteria(searchVerificationInput, remarks);

            compareLocSendInGDSReqAndSCSReq(vendorLocMapFromGdsMsg, vendorLocMapFromSCSSearchCriteria, remarks);
        } catch (DataAccessException e)
        {
            e.printStackTrace();
            remarks.add("Search Request Optimization verification failed, throw exception : " + e.getMessage());
        }

        if (remarks.size() > 0)
        {
            remarks.add("Search Request Optimization verification failed.\n");
            return new VerificationResult(this.getName(), isPassed, remarks);
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private Map<String, List<String>> getVendorLocMapFromGdsMsg(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException
    {
        Map<String, List<String>> vendorLocMap = null;
        NodeList acaqReqNodeList = ASCSGDSMsgReadHelper.getSpecifyNodeListFromSpoofer(verificationContext, "Car_Availability");


        if (acaqReqNodeList != null)
        {
            vendorLocMap = new HashMap<>();
            for (int i = 0; i < acaqReqNodeList.getLength(); i++)
            {
                ACAQReq acaqReq = new ACAQReq();
                acaqReq.buildVendorLocMap(acaqReqNodeList.item(i));
                addlocToVendorLocMapNoDuplicate(vendorLocMap, acaqReq.getVendorLocMap());
                //vendorLocMap.putAll(acaqReq.getVendorLocMap());
            }

        } else
        {
            remarks.add("Not Find ACAQ Request In GDS Msg");
        }

        return vendorLocMap;
    }

    private void addlocToVendorLocMapNoDuplicate(Map<String, List<String>> vendorLocMap, Map<String, List<String>> gdsLocations)
    {
        for (Map.Entry<String, List<String>> entry : gdsLocations.entrySet())
        {
            if (vendorLocMap.containsKey(entry.getKey()))
            {
                List<String> locationsForVendor = vendorLocMap.get(entry.getKey());
                for(String gdsLocation : entry.getValue()) {
                    if (!locationsForVendor.contains(gdsLocation)) {
                        locationsForVendor.add(gdsLocation);
                    }
                }
                vendorLocMap.remove(entry.getKey());
                vendorLocMap.put(entry.getKey(), locationsForVendor);
            }
            else
            {
                vendorLocMap.put(entry.getKey(), entry.getValue());
            }
        }

    }

    private Map<String, List<String>> getVendorLocMapFromSCSSearchCriteria(SearchVerificationInput searchVerificationInput, ArrayList remarks) throws DataAccessException
    {
        if (null != searchVerificationInput.getRequest().getCarSearchCriteriaList()
                && null != searchVerificationInput.getRequest().getCarSearchCriteriaList().getCarSearchCriteria()
                && !searchVerificationInput.getRequest().getCarSearchCriteriaList().getCarSearchCriteria().isEmpty())
        {
            CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);
            Map<String, List<String>> vendorLocMap = new HashMap<>();

            for (CarSearchCriteriaType searchCriteria : searchVerificationInput.getRequest().getCarSearchCriteriaList().getCarSearchCriteria())
            {
                AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();

                for (Long supplierID : searchCriteria.getVendorSupplierIDList().getVendorSupplierID())
                {
                    final List<ExternalSupplyServiceDomainValueMap> valueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap
                            (0, 0, CommonConstantManager.DomainType.CAR_VENDOR, supplierID.toString(), null);

                    final String vendorCode = CompareUtil.isObjEmpty(valueMaps) ? "" : valueMaps.get(0).getExternalDomainValue();

                    //add this is to fix that the location code is not same as the location id corresponding location code.
                    /*String locations = null;

                    Long locId = searchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarVendorLocationID();
                    if (null != locId && locId > 0)
                    {
                        locations = getFullLocByLocID(scsDataSource, locId.toString(), supplierID);

                    } else
                    {
                        locations = commonNodeReader.buildLocStr(searchCriteria.getCarTransportationSegment().getStartCarLocationKey());
                    }*/
                    //Meichun 20180723 - we already build GDS location info from SCS database based on external domain map, no need ot query form ID
                    String locations = commonNodeReader.buildLocStr(searchCriteria.getCarTransportationSegment().getStartCarLocationKey());

                    if(null != locations && !locations.trim().isEmpty())
                    {
                        /*locId = searchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarVendorLocationID();
                        if (null != locId && locId > 0)
                        {
                            locations = locations + getFullLocByLocID(scsDataSource, locId.toString(), supplierID);
                        } else
                        {
                            locations = locations + commonNodeReader.buildLocStr(searchCriteria.getCarTransportationSegment().getEndCarLocationKey());
                        }*/
                        locations = locations + commonNodeReader.buildLocStr(searchCriteria.getCarTransportationSegment().getEndCarLocationKey());

                        commonNodeReader.addlocToVendorLocMapNoDuplicate(vendorLocMap, vendorCode, locations);
                    }
                }
            }

            return vendorLocMap;
        } else
        {
            remarks.add("The search Criteria is Empty.");
        }

        return null;
    }

    private void compareLocSendInGDSReqAndSCSReq(Map<String, List<String>> vendorLocMapFromGdsMsg,
                                                 Map<String, List<String>> vendorLocMapFromSCSSearchCriteria, ArrayList remarks)
    {
        for (Map.Entry<String, List<String>> entry : vendorLocMapFromSCSSearchCriteria.entrySet())
        {
            List<String> locs = vendorLocMapFromGdsMsg.get(entry.getKey());
            if (null != locs && !locs.isEmpty())
            {
                StringBuilder errorMsg = new StringBuilder();
                boolean isEqual = CompareUtil.compareObject(entry.getValue(), locs, null, errorMsg);


                if (!isEqual)
                {
                    remarks.add("Vendor : " + entry.getKey() + " location send in Search request : " + entry.getValue().toString()
                            + ", but not same as send in GDS Request : " + locs.toString() + "\n");
                }
            } else
            {
                remarks.add("Vendor : " + entry.getKey() + " Exist in Search request, but not send in GDS Request.\n");
            }
        }
    }

    /*private String getFullLocByLocID(CarsSCSDataSource scsDataSource, String carVendorLocationID, Long supplierId) throws DataAccessException
    {
        final List<ExternalSupplyServiceDomainValueMap> valueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap
                (supplierId, 0, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, carVendorLocationID, null);
        String locations = CompareUtil.isObjEmpty(valueMaps) ? "" : valueMaps.get(0).getExternalDomainValue();
        if (locations.length() == 6)
        {
            locations = locations.substring(0, 4) + "0" + locations.substring(4);
        }

        return locations;
    }*/

}