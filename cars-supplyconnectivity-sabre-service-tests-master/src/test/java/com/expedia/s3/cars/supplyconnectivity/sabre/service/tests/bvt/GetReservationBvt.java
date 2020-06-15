package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation
        .GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.cancel.CancelHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getreservation.GetReservationHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;

import org.testng.annotations.Test;

/**
 * Created by vmohan on 3/1/2017.
 */
public class GetReservationBvt extends SuiteContext {

    @SuppressWarnings("CPD-START")

    private void testGetReservation(TestScenario scenarios, String tuid, String guid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //1,search
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData, 50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);

        //2. getDetails
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        String newguid = null;
        newguid = Utils.generateRandomGuid();
        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createDetailsRequest(), newguid);
        GetDetailsHelper.getDetailsVerification(getDetailsVerificationInput, spooferTransport, scenarios, newguid, logger, false);

        //3.getCostAndAvail
        newguid = Utils.generateRandomGuid();
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCostAndAvailRequest(), newguid);
        CostAndAvailHelper.getCostAndAvailabilityVerification(getCostAndAvailabilityVerificationInput, spooferTransport, scenarios, newguid, logger, false);

        //4.Reserve
        newguid = Utils.generateRandomGuid();
        final ReserveVerificationInput reserveVerificationInput = ReserveHelper.reserve(httpClient,requestGenerator,newguid,true);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        ReserveHelper.reserveVerify(reserveVerificationInput, spooferTransport, scenarios, newguid, false, logger, false);

        //4.getReservation
        newguid = Utils.generateRandomGuid();
        final GetReservationVerificationInput getReservationVerificationInput = GetReservationHelper.retrieveReservation( httpClient,requestGenerator,newguid,false);
        GetReservationHelper.getreservationVerify(getReservationVerificationInput, spooferTransport, scenarios, newguid, false, logger, false);

        //5.cancel
        newguid = Utils.generateRandomGuid();
        final CancelVerificationInput cancelVerificationInput = CancelHelper.cancel(httpClient,requestGenerator,newguid,false);
        CancelHelper.cancelVerify(cancelVerificationInput, spooferTransport, scenarios, newguid, false, logger, false);

    }

    @SuppressWarnings("CPD-END")
    @Test(groups = {"bvt"})
    public void casss4460USAgetReservationOffAirportTest() throws Exception {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        testGetReservation(scenario, "4460", Utils.generateRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss4460USAgetReservationOnAirportTest() throws Exception {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OnAirport_LAS.getTestScenario();
        testGetReservation(scenario, "4460", Utils.generateRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss4460CANgetReservationOffAirportTest() throws Exception {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        testGetReservation(scenario, "4460", Utils.generateRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss4460CANgetReservationOnAirportTest() throws Exception {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario();
        testGetReservation(scenario, "4460", Utils.generateRandomGuid());
    }

}
