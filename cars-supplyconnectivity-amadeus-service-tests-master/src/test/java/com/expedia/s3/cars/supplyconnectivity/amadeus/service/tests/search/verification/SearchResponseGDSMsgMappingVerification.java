package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Created by miawang on 8/30/2016.
 */
public class SearchResponseGDSMsgMappingVerification implements ISearchVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        List<String> ignoreList = new ArrayList<>();
        ignoreList.add(CarTags.SUPPLY_SUBSET_ID);
        ignoreList.add(CarTags.CAR_POLICY_LIST);

        if (remarks.size() > 0)
        {
            return new VerificationResult(getName(), isPassed, remarks);
        } else
        {
            try
            {
                String posCurrency = input.getRequest().getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCurrencyCode();
                List<CarProductType> searchResultCarList = new ArrayList<>();
                Map<String, String> exsitingGDSCarSIPPMap = new HashMap<String, String>();
                for (CarSearchResultType searchResult : input.getResponse().getCarSearchResultList().getCarSearchResult())
                {
                    if (null != searchResult && null != searchResult.getCarProductList() && null != searchResult.getCarProductList().getCarProduct())
                    {
                        //to filter duplicate car.
                        for (CarProductType rspCar : searchResult.getCarProductList().getCarProduct())
                        {
                            //Verify PointOfSupplyCurrencyCode returned in response
                            String posuCurrency = CostPriceCalculator.getCostPosuCurrencyCode(rspCar.getCostList(), posCurrency);
                            if(!rspCar.getPointOfSupplyCurrencyCode().equals(posuCurrency))
                            {
                                remarks.add(String.format("PointOfSupplyCurrencyCode in ascs search response(%s) is not equal to expected value(%s)",
                                        rspCar.getPointOfSupplyCurrencyCode(), posuCurrency));
                            }
                            if (!isDuplicateCar(rspCar, exsitingGDSCarSIPPMap))
                            {
                                searchResultCarList.add(rspCar);
                            }
                        }
                    }
                }
                CarProductComparator.isCarProductListEqual(GetGdsMsg(verificationContext,
                        input.getRequest().getCarSearchCriteriaList(), remarks), searchResultCarList,
                        0, remarks, ignoreList);
            } catch (DataAccessException e)
            {
                e.printStackTrace();
            }

            if (remarks.size() > 0)
            {
                remarks.add("SearchGDSMsgMap Response compare with GDS Msg verification failed.");
                return new VerificationResult(this.getName(), isPassed, remarks);
            }
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private List<CarProductType> GetGdsMsg(BasicVerificationContext verificationContext, CarSearchCriteriaListType searchCriteriaList, ArrayList remarks) throws DataAccessException
    {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        NodeList acaqRspNodeList = ASCSGDSMsgReadHelper.getSpecifyNodeListFromSpoofer(verificationContext, "Car_AvailabilityReply");

        if (acaqRspNodeList != null)
        {
            List<CarProductType> gdsCarList = new ArrayList<>();
            Map<String, String> exsitingGDSCarSIPPMap = new HashMap<String, String>();
            for(int i = 0; i < acaqRspNodeList.getLength(); i++)
            {
                ACAQRsp acaqRsp = new ACAQRsp(acaqRspNodeList.item(i), searchCriteriaList, scsDataSource);
                if (null != acaqRsp.getGdsCarProductList())
                {
                    //to filter duplicate car.
                    for (CarProductType gdsCar:acaqRsp.getGdsCarProductList())
                    {
                        if(!isDuplicateCar(gdsCar, exsitingGDSCarSIPPMap))
                        {
                            gdsCarList.add(gdsCar);
                        }
                    }
                }
            }
            return gdsCarList;
        } else
        {
            remarks.add("Not Find ACAQ Response In GDS Msg");
        }

        return null;
    }

    private boolean isDuplicateCar(CarProductType gdsCar, Map<String, String> exsitingGDSCarSIPPMap)
    {
        //to filter duplicate car.
        boolean isDuplicate = true;
        String gdsCarSIPP =
                //vendor + SIPP
                gdsCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() + "^"
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode().toString() + "^"
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode().toString() + "^"
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode().toString() + "^"
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode().toString() + "^"

                //PickupLocation
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode()
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarLocationCategoryCode()
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText()

                //Drop-off Location
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode()
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarLocationCategoryCode()
                + gdsCar.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText();
        if (!exsitingGDSCarSIPPMap.containsKey(gdsCarSIPP))
        {
            exsitingGDSCarSIPPMap.put(gdsCarSIPP, gdsCarSIPP);
            isDuplicate = false;
        }
        return isDuplicate;
    }
}
