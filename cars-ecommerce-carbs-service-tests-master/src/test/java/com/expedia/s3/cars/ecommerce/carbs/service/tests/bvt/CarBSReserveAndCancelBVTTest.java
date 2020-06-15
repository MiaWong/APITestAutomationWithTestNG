package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.cancel.CarBSCancelVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.reserve.CarBSReserveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveRequestType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
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
public class CarBSReserveAndCancelBVTTest extends SuiteCommon{


    @Test(groups = {"bvt"})
    public void casss11005ReservationSanityTest() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        testCarbsReserve(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "11005", randomGuid);

    }

    private void testCarbsReserve(TestScenario scenarios, String tuid, String guid) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), tuid);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(),httpClient,request);
        //verification
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getCostandAvail
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid, httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, scenarios, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);
        carbsSearchRequestGenerator.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);

        //firstReserve
        final CarECommerceReserveRequestType firstReserveRequestType = carbsSearchRequestGenerator.createCarbsFirstReserveRequest();
        final CarECommerceReserveResponseType firstServeResponseType = CarbsRequestSender.getCarbsReserveResponse(guid, httpClient, firstReserveRequestType);
        CarBSReserveVerifier.isCarbsReserveWorksVerifier(guid, scenarios, firstReserveRequestType, firstServeResponseType);
        //secondReserve
        carbsSearchRequestGenerator.setReserveResponseType(firstServeResponseType);
        final CarECommerceReserveRequestType secondReserveRequestType = carbsSearchRequestGenerator.createCarbsSecondReserveRequest(firstServeResponseType);
        final CarECommerceReserveResponseType secondReserveResponseType = CarbsRequestSender.getCarbsReserveResponse(guid, httpClient, secondReserveRequestType);
        CarBSReserveVerifier.isCarbsReserveWorksVerifier(guid, scenarios, secondReserveRequestType, secondReserveResponseType);

        //cancel
        final CarECommerceCancelRequestType cancelRequestType = carbsSearchRequestGenerator.createCarbsCancelRequest();
        final  CarECommerceCancelResponseType cancelResponseType = CarbsRequestSender.getCarbsCancelResponse(guid, httpClient, cancelRequestType);
        CarBSCancelVerifier.isCarbsCancelWorksVerifier(guid, scenarios, cancelRequestType, cancelResponseType);

    }


}
