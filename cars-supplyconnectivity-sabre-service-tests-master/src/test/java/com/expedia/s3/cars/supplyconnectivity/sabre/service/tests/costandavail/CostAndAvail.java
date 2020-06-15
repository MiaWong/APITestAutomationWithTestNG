package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.costandavail;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;
import org.testng.annotations.Test;

/**
 * Created by vmohan on 11/24/16.
 */
public class CostAndAvail extends SuiteContext
{
    @SuppressWarnings("CPD-START")
    private void testGetCostAndAvailability(TestScenario scenarios,
                                            String tuid,
                                            String guid) throws Exception
    {

        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final String newGuid = Utils.setSpooferOverride(spooferTransport,scenarios.getScenarionName());
        final GetCostAndAvailabilityVerificationInput costandavailVerificationInput =
                CostAndAvailHelper.getCostAndAvailability( httpClient,
                        requestGenerator,
                        newGuid);

        CostAndAvailHelper.getCostAndAvailabilityVerification( costandavailVerificationInput,
                spooferTransport,
                scenarios,
                newGuid,
                logger,
                true);
    }
    @SuppressWarnings("CPD-END")

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3218CANGetCostAndAvailability() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetCostAndAvailability( scenario, "3218", randomGuid);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3218USAGetCostAndAvailability() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetCostAndAvailability( scenario, "3218", randomGuid);
    }

}
