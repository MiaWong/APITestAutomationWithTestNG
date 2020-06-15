package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;

import org.testng.annotations.Test;

/**
 * Created by vmohan on 11/24/2016.
 */
public class GetCostAndAvailBvt extends SuiteContext{

    @SuppressWarnings("CPD-START")
    private void testCostAndAvail(TestScenario scenarios, String tuid, String guid) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //1,search
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);

        final String newRandomGuid = Utils.generateRandomGuid();

        //2.getCostAndAvail
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCostAndAvailRequest(), guid);
        CostAndAvailHelper.getCostAndAvailabilityVerification(getCostAndAvailabilityVerificationInput, spooferTransport, scenarios, newRandomGuid, logger, false);

    }
    @SuppressWarnings("CPD-END")

    @Test(groups = {"bvt"})
    public void casss3217USACostAndAvailOffAirportTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        testCostAndAvail(scenario, "3217", Utils.generateRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss3217USACostAndAvailOnAirportTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OnAirport_LAS.getTestScenario();
        testCostAndAvail(scenario, "3217123", Utils.generateRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss3217CANCostAndAvailOffAirportTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        testCostAndAvail(scenario, "3217", Utils.generateRandomGuid());
    }

    @Test(groups = {"bvt"})
    public void casss3217CANCostAndAvailOnAirportTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario();
        testCostAndAvail(scenario, "3217456", Utils.generateRandomGuid());
    }


}
