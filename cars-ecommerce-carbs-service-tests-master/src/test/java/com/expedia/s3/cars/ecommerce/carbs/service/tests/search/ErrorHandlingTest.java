package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyErrorReturnedInRsp;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.UUID;

/**
  * Created by Meichun on 7/17/2018.
  */
public class ErrorHandlingTest extends SuiteCommon
{

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs177320CarBSSearchEgenciaCarProductNotAvailableError() throws Exception
    {
        egenciaCarErrorHandlingTest(CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LHR.getTestScenario(), "177320",
                "EC_418");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs177321CarBSSearchEgenciaCarProductNotAvailableErrorSCSRateNotAvailable() throws Exception
    {
        egenciaCarErrorHandlingTest(CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LHR.getTestScenario(), "177321",
                "EC_902");
    }

    //For Egencia car with multiple CD code, CD code should be returned.
    public void egenciaCarErrorHandlingTest(TestScenario scenarios, String tuid, String invalidCDCode) throws Exception
    {
        final String randomGuid = UUID.randomUUID().toString();
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);

        //create search request
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        //Override CD code
        setCDCode(request, invalidCDCode);

        //filter SearchCriteria per vendor for offairport - max 5 SearchCriteria Per Vendor
        if(!testData.getScenarios().isOnAirPort())
        {
            CarbsSearchRequestGenerator.filterSearchCriteriaPerVendorForAmadeus(request);
        }

        //Send request with purchase type
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);

        //Verify error returned in response
        VerifyErrorReturnedInRsp.verifyCarProductNotAvailableErrorReturned(response);
    }

    private void setCDCode(CarECommerceSearchRequestType request, String invalidCDCode)
    {
        //Override CD code
        for(final CarECommerceSearchCriteriaType carECommerceSearchCriteria : request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria()) {
            final CarRateOverrideListType carRateOverrideList = new CarRateOverrideListType();
            carRateOverrideList.setCarRateOverride(new ArrayList<>());
            carECommerceSearchCriteria.setCarRateOverrideList(carRateOverrideList);
            final long supplierID = carECommerceSearchCriteria.getVendorSupplierIDList().getVendorSupplierID().get(0);
            addCarRateOverride(carRateOverrideList, supplierID, invalidCDCode);
        }
    }

    private void addCarRateOverride(CarRateOverrideListType carRateOverrideList, Long supplierID, String cdCode)
    {
        final CarRateOverrideType carRateOverride = new CarRateOverrideType();
        carRateOverride.setVendorSupplierID(supplierID);
        carRateOverride.setCorporateDiscountCode(cdCode);
        carRateOverrideList.getCarRateOverride().add(carRateOverride);
    }



}