package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.bvt;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSBvtSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.CancelResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.GetReservationVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.ReserveVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fehu on 8/4/2016.
 */
public class GetReservation extends SuiteCommon {

    @Test(groups = {TestGroup.BVT}, priority = 0)
    //@Ignore("need to figure out why this is not working")
    public void tfs_208939_getReservation_BVT_Test() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        testGetReservation(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(), "208939", PojoXmlUtil.getRandomGuid());
    }

    private void testGetReservation(TestScenario scenarios, String tuid, String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        //1,search
        SCSBvtSearchRequestGenerator bvtSearchRequestGenerator = new SCSBvtSearchRequestGenerator();
        CarSupplyConnectivitySearchRequestType request = bvtSearchRequestGenerator.createSearchRequest(scenarios, tuid,
                "11684", SettingsProvider.BVTTEST_OFFAIRPORTLOCATIONLIST, SettingsProvider.BVTTEST_VENDORLIST);
        CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(guid, httpClient, request);

        //2,reserve
        SCSRequestGenerator requestGenerator = new SCSRequestGenerator(request, response);
        CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        CarSupplyConnectivityReserveResponseType reserveResponse = AmadeusSCSRequestSender.getSCSReserveResponse
                (guid, httpClient, reserveRequest);
        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("Reserve Request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
            logger.debug("Reserve Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveResponse)));
        }
        ReserveVerifier.isReserveWorksVerifier(reserveResponse);

        //3,getReservation
        requestGenerator.setReserveResp(reserveResponse);
        CarSupplyConnectivityGetReservationRequestType getReservationRequest = requestGenerator.createGetReservationRequest();
        CarSupplyConnectivityGetReservationResponseType getReservationResponse = AmadeusSCSRequestSender.
                getSCSReservationResponse(guid, httpClient, getReservationRequest);
        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("GetReservation Request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationRequest)));
            logger.debug("GetReservation Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationResponse)));
        }
        GetReservationVerifier.isGetReservationWorksVerifier(getReservationResponse);

        //4,Cancel
        CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator.createCancelRequest();
        CarSupplyConnectivityCancelResponseType cancelResponse = AmadeusSCSRequestSender.getSCSCancelResponse(guid, httpClient, cancelRequestType);
        CancelResponseVerifier.isCancelWorksVerifier(cancelResponse);
    }
}