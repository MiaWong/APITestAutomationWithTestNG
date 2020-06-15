package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by fehu on 8/19/2016.
 */
public class CarBSGetCostAndAvailBVTTest extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void casss100012getCostAndAvailSanityTest() throws IOException, DataAccessException {
       final String randomGuid= PojoXmlUtil.getRandomGuid();
        testCarbsGetCostAndAvail(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "100012", randomGuid);

    }

    private void testCarbsGetCostAndAvail(TestScenario scenarios, String tuid, String guid) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //search
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), tuid);
        final  CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid,httpClient,request);
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getCostAndAvail
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request,response,testData);
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid,httpClient,getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, scenarios, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);
    }

    @Test(groups = {"bvt"})
    public void casss1091054testCarBSGetCostAndAvailOneWayGDSP() throws IOException, DataAccessException {
        testCarbsGetCostAndAvail(CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario(), "1091054", PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss1091055testCarBSGetCostAndAvailRoundTripGDSP() throws IOException, DataAccessException {
        testCarbsGetCostAndAvail(CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario(), "1091055", PojoXmlUtil.getRandomGuid());
    }
}
