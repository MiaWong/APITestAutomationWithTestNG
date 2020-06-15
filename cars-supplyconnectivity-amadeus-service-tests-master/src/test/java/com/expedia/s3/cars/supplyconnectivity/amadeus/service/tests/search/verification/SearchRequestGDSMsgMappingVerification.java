package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.SupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification.IsCDCodePassInGDSRequestCorrectCommonVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by miawang on 8/30/2016.
 *
 * 2017-10-12 add automation for verify cd code in search request CASSS-7027 - Amadeus SCS XSLT support for CD code(Shopping/Booking)
 */
public class SearchRequestGDSMsgMappingVerification implements ISearchVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Override
    public IVerification.VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        try
        {
            //verify cd code send in gds request is correct.
            //cd code in GDS request
            Map<Long, String> cdCodeSendInAcaqReq = getCdCodesFromGdsMsg(verificationContext, remarks);

            //cd code in request
            Map<Long, String> cdCodeInReqMap = new HashMap();

            for (CarSearchCriteriaType searchCriteria : searchVerificationInput.getRequest().getCarSearchCriteriaList().getCarSearchCriteria())
            {
                final List<CarRateOverrideType> discountcodes = null == searchCriteria.getCarRateOverrideList() ? null : searchCriteria.getCarRateOverrideList().getCarRateOverride();

                if (null != discountcodes && discountcodes.isEmpty())
                {
                    for (CarRateOverrideType carRate : discountcodes)
                    {
                        if (null != carRate.getCorporateDiscountCode())
                        {
                            if (!cdCodeInReqMap.containsKey(carRate.getVendorSupplierID()))
                            {
                                cdCodeInReqMap.put(carRate.getVendorSupplierID(), carRate.getCorporateDiscountCode());
                            }
                        }
                    }
                }
            }

            //get cd code from database.
            String cdCodeConfigured = null;
            CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);
            List<SupplierItemMap> supplierItems = scsDataSource.getSupplierItemMap(searchVerificationInput.getRequest().getCarSearchCriteriaList().
                    getCarSearchCriteria().get(0).getSupplySubsetIDEntryList().getSupplySubsetIDEntry().get(0).getSupplySubsetID());
            for (SupplierItemMap supplierItem : supplierItems)
            {
                if (supplierItem.getItemKey().equals("CorporateDiscountCode"))
                {
                    cdCodeConfigured = supplierItem.getItemValue();
                }
            }

            for (Map.Entry<Long, String> entry : cdCodeInReqMap.entrySet())
            {
                long vendorSupplierIDInRequest = entry.getKey();
                String cdCodeInReq = entry.getValue();

                IsCDCodePassInGDSRequestCorrectCommonVerifier cdCodePassCommonVerifier = new IsCDCodePassInGDSRequestCorrectCommonVerifier();
                cdCodePassCommonVerifier.verifyCdCodeInGDSRequest(cdCodeInReq, cdCodeConfigured, vendorSupplierIDInRequest, cdCodeSendInAcaqReq, remarks);
            }

        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }

        if (remarks.size() > 0)
        {
            remarks.add("SearchGDSMsgMap Request GDS Msg verification failed.\n");
            return new IVerification.VerificationResult(this.getName(), isPassed, remarks);
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new IVerification.VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private Map<Long, String> getCdCodesFromGdsMsg(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException
    {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        NodeList acaqReqNodeList = ASCSGDSMsgReadHelper.getSpecifyNodeListFromSpoofer(verificationContext, "Car_Availability");

        if (acaqReqNodeList != null)
        {
            Map<Long, String> vendorDiscountNumsMap = new HashMap();
            for(int i = 0; i < acaqReqNodeList.getLength(); i++)
            {
                ACAQReq acaqReq = new ACAQReq(acaqReqNodeList.item(i), scsDataSource);
                vendorDiscountNumsMap.putAll(acaqReq.getVendorDiscountNumsMap());
            }

            return vendorDiscountNumsMap;
        } else
        {
            remarks.add("Not Find ACAQ Request In GDS Msg");
        }

        return null;
    }
}