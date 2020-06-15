package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.cancel.CarBSCancelVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getreservation.CarBSGetReservationVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.reserve.CarBSReserveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationRequestType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationResponseType;
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
@SuppressWarnings("PMD")
public class CarBSGetResevationBVTTest extends SuiteCommon{


    @Test(groups = {"bvt"})
    public void CASSS_100012_getReservation_Sanity_Test() throws IOException, DataAccessException {
        final String randomGuid= PojoXmlUtil.getRandomGuid();
        testCarbsGetReservation(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "100012", randomGuid);

    }

    private void testCarbsGetReservation(TestScenario scenarios, String tuid, String guid) throws IOException, DataAccessException {

        final TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), tuid);
        final  CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(),httpClient,request);
        //verification
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //reserve
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request,response,testData);
        //firstReserve
        final CarECommerceReserveRequestType firstReserveRequestType = carbsSearchRequestGenerator.createCarbsFirstReserveRequest();
        final  CarECommerceReserveResponseType firstServeResponseType = CarbsRequestSender.getCarbsReserveResponse(guid,httpClient,firstReserveRequestType);
        CarBSReserveVerifier.isCarbsReserveWorksVerifier(guid, scenarios, firstReserveRequestType, firstServeResponseType);
        //secondReserve
        carbsSearchRequestGenerator.setReserveResponseType(firstServeResponseType);
        final CarECommerceReserveRequestType secondReserveRequestType = carbsSearchRequestGenerator.createCarbsSecondReserveRequest(firstServeResponseType);
        final CarECommerceReserveResponseType secondReserveResponseType = CarbsRequestSender.getCarbsReserveResponse(guid,httpClient,secondReserveRequestType);
        CarBSReserveVerifier.isCarbsReserveWorksVerifier(guid, scenarios, secondReserveRequestType, secondReserveResponseType);

        //getReservation
        final  CarECommerceGetReservationRequestType getReservationRequestType = carbsSearchRequestGenerator.createCarbsGetReservationRequest();
        final CarECommerceGetReservationResponseType getReservationResponseType = CarbsRequestSender.getCarbsGetReservationResponse(guid,httpClient,getReservationRequestType);
        CarBSGetReservationVerifier.isCarbsGetReservationWorksVerifier(guid, scenarios, getReservationRequestType, getReservationResponseType);


        //cancel
        final CarECommerceCancelRequestType cancelRequestType = carbsSearchRequestGenerator.createCarbsCancelRequest();
        final CarECommerceCancelResponseType cancelResponseType = CarbsRequestSender.getCarbsCancelResponse(guid,httpClient,cancelRequestType);
        CarBSCancelVerifier.isCarbsCancelWorksVerifier(guid, scenarios, cancelRequestType, cancelResponseType);
    }

}
