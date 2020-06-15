package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyRequestCDCodeReturnedInRsp;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.UUID;

/**
  * Created by Meichun on 7/17/2018.
  */
public class MultipleCDCodeAndIATAGEOTest extends SuiteCommon
{
    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "132165"})
    public void tfs132165USMultipleCDCode() throws Exception
    {
        doTestForNonEgenciaCar(CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario(), "132165");
    }

    //No harm to non-Egenica car, and request CD code under CarRateOverride should not be returned.
    public void doTestForNonEgenciaCar(TestScenario scenarios, String tuid) throws Exception
    {
        final String randomGuid = UUID.randomUUID().toString();
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        //create search request
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        //Override CD code: multiple vendors, one with multiple CD code, one with only one CD code, one with duplicate CD code
        setCDCodes(request);
        //Send request with purchase type
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        //BVT verification
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);
        //Verify respnse doesn't include any CD code under CarRateOverrideList
        final String responseS = PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response));
        if(responseS.contains("TestCD"))
        {
            Assert.fail("CD code under CarRateOverrideList shold not be returned in US response!");
        }
    }

    //Override CD code: multiple vendors, one with multiple CD code, one with only one CD code, one with duplicate CD code
    private void setCDCodes(CarECommerceSearchRequestType request)
    {
        final CarRateOverrideListType carRateOverrideList = new CarRateOverrideListType();
        carRateOverrideList.setCarRateOverride(new ArrayList<CarRateOverrideType>());
        addCarRateOverride(carRateOverrideList, 6l, "ALTestCD");
        addCarRateOverride(carRateOverrideList, 6l, "ALTestCD1");
        addCarRateOverride(carRateOverrideList, 14l, "EPTestCD");
        addCarRateOverride(carRateOverrideList, 15l, "ETTestCD");
        addCarRateOverride(carRateOverrideList, 15l, "ETTestCD");
        request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).setCarRateOverrideList(carRateOverrideList);
    }

    private void addCarRateOverride(CarRateOverrideListType carRateOverrideList, Long supplierID, String cdCode)
    {
        final CarRateOverrideType carRateOverride = new CarRateOverrideType();
        carRateOverride.setVendorSupplierID(supplierID);
        carRateOverride.setCorporateDiscountCode(cdCode);
        carRateOverrideList.getCarRateOverride().add(carRateOverride);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs131626CarBSEgenciaMultipleCD() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario(), "131626",
                CommonEnumManager.VendorCDCodeType.MulCDSingleVendor, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs132163CarBSEgenciaSingleCD() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(), "132163",
                CommonEnumManager.VendorCDCodeType.SingleCD, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161824CarBSEgenciaSingleCD() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario(), "161824",
                CommonEnumManager.VendorCDCodeType.VendorsWithAndWithoutCD, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282037CarBSEgenciaIATAToGEO() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "282037",
                CommonEnumManager.VendorCDCodeType.NULL, true, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282042CarBSEgenciaIATAToGEOOneWay() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(), "282042",
                CommonEnumManager.VendorCDCodeType.NULL, true, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282309CarBSEgenciaGEOToIATAOneWay() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(), "282309",
                CommonEnumManager.VendorCDCodeType.NULL, false, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282336CarBSEgenciaGEOToIATAMultiCD() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(), "282336",
                CommonEnumManager.VendorCDCodeType.MulCDSingleVendor, false, true);
    }

    //For Egencia car with multiple CD code, CD code should be returned.
    public void doTestForEgenciaCar(TestScenario scenarios, String tuid, CommonEnumManager.VendorCDCodeType vendorCDCodeType,
                                    boolean setPickupIATA, boolean setDropoffIATA) throws Exception
    {
        final String randomGuid = UUID.randomUUID().toString();
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);

        //create search request
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        //Override CD code
        setCDCodes(request, vendorCDCodeType);

        //Set IATA
        if(setPickupIATA)
        {
            CarbsSearchRequestGenerator.setPickupLocationAsIATA(request);
        }
        if(setDropoffIATA)
        {
            CarbsSearchRequestGenerator.setDropoffLocationAsIATA(request);
        }

        //filter SearchCriteria per vendor for offairport - max 5 SearchCriteria Per Vendor
        if(!testData.getScenarios().isOnAirPort())
        {
            CarbsSearchRequestGenerator.filterSearchCriteriaPerVendorForAmadeus(request);
        }

        //Send request with purchase type
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        //BVT verification - car with expected providerID should be returned
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);
        //Verify respnse has requested CD code
        VerifyRequestCDCodeReturnedInRsp.verifyCDCodeReturned(request, response);
    }

    private void setCDCodes(CarECommerceSearchRequestType request, CommonEnumManager.VendorCDCodeType vendorCDCodeType)
    {
        //Override CD code
        final CarRateOverrideListType carRateOverrideList = new CarRateOverrideListType();
        carRateOverrideList.setCarRateOverride(new ArrayList<>());
        request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).setCarRateOverrideList(carRateOverrideList);
        if (vendorCDCodeType.equals(CommonEnumManager.VendorCDCodeType.MulCDSingleVendor))
        {
            if(null == request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getVendorSupplierIDList()
                    || null == request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getVendorSupplierIDList().getVendorSupplierID()
                    || request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getVendorSupplierIDList().getVendorSupplierID().isEmpty())
            {
                addCarRateOverride(carRateOverrideList, 14l, "51354174");
                addCarRateOverride(carRateOverrideList, 14l, "676186");

            }
            else {
                final long supplierID = request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getVendorSupplierIDList().getVendorSupplierID().get(0);
                addCarRateOverride(carRateOverrideList, supplierID, "51354174");
                addCarRateOverride(carRateOverrideList, supplierID, "676186");
            }
        }
        else if (vendorCDCodeType.equals(CommonEnumManager.VendorCDCodeType.MulVendorHaveSingleCD))
        {
            addCarRateOverride(carRateOverrideList, 14l, "51354174");
            addCarRateOverride(carRateOverrideList, 6l, "51354174");
        }
        else if (vendorCDCodeType.equals(CommonEnumManager.VendorCDCodeType.VendorsWithAndWithoutCD))
        {
            addCarRateOverride(carRateOverrideList, 14l, "51354174");
            addCarRateOverride(carRateOverrideList, 6l, "");
        }
        else if(vendorCDCodeType.equals(CommonEnumManager.VendorCDCodeType.SingleCD))
        {
            addCarRateOverride(carRateOverrideList, 14l, "51354174");
        }
    }



}