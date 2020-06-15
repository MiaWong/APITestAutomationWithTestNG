package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyDeliveryCollectionInRsp;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by Meichun on 7/17/2018.
 */
public class SearchWithDeliveryCollectionTest extends SuiteCommon
{

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs180163CarBSEgenciaSearchDeliveryTrueCollectionTrue() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LHR.getTestScenario(), "180163",
                CarCommonEnumManager.CollectionSet.HasCollection, CarCommonEnumManager.DeliverySet.HasDelivery, CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs180165CarBSEgenciaSearchDeliveryTrueCollectionFalse() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(), "180165",
                CarCommonEnumManager.CollectionSet.NonExist, CarCommonEnumManager.DeliverySet.HasDelivery, CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs180208CarBSEgenciaSearchDeliveryFalseCollectionTrue() throws Exception
    {
        doTestForEgenciaCar(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(), "180208",
                CarCommonEnumManager.CollectionSet.HasCollection, CarCommonEnumManager.DeliverySet.NonExist, CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist);
    }

    //For Egencia car with multiple CD code, CD code should be returned.
    public void doTestForEgenciaCar(TestScenario scenarios, String tuid, CarCommonEnumManager.CollectionSet collection, CarCommonEnumManager.DeliverySet delivery,
                                    CarCommonEnumManager.OutOfOfficeHourBooleanSet outOfOfficeHourBoolean) throws Exception
    {
        final String randomGuid = UUID.randomUUID().toString();
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        testData.setClientCode("W0DFCJ");

        //create search request
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        //Set delivery/collection
        CarbsSearchRequestGenerator.setDeliveryCollectionOutOfOffice(request, collection, delivery, outOfOfficeHourBoolean);

        //filter SearchCriteria per vendor for offairport - max 5 SearchCriteria Per Vendor
        if(!testData.getScenarios().isOnAirPort())
        {
            CarbsSearchRequestGenerator.filterSearchCriteriaPerVendorForAmadeus(request);
        }

        //Send request with purchase type
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        //BVT verification - car with expected providerID should be returned
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);
        //Verify Delivery/Collection
        VerifyDeliveryCollectionInRsp.verifyDeliveryCollection(request, response);

    }


}
