package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.RetryAnalyzer;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;
import org.testng.annotations.Test;

/**
 * Created by vmohan on 03-11-2016.
 */

public class GetDetails extends SuiteContext {

    @SuppressWarnings("CPD-START")
    //  with car filter
    private void testBasicGetDetails(TestScenario scenarios,
                                String tuid,
                                String guid) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        //final String newGuid = Utils.setSpooferOverride(spooferTransport,scenarios.getScenarionName());

        final GetDetailsVerificationInput getDetailsVerificationInput =
                GetDetailsHelper.getDetails(httpClient,
                        requestGenerator,
                        guid);

        //  getDetails verification
        GetDetailsHelper.getDetailsVerification(getDetailsVerificationInput,
                spooferTransport,
                scenarios,
                guid,
                logger,
                true);

    }


    //  filter version for conditionalCost
    private void testGetDetails4ConditionalCostList(TestScenario scenarios,
                                                    String tuid,
                                                    String guid) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final String newGuid = Utils.setSpooferOverride(spooferTransport,scenarios.getScenarionName());

        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator, newGuid);

        GetDetailsHelper.getDetailsConditionalCostListVerification(  getDetailsVerificationInput,
                spooferTransport,
                scenarios,
                newGuid,
                logger);

    }


    //  filter version for conditionalCost
    private void testGetDetails4PhoneList(TestScenario scenarios,
                                          String tuid,
                                          String guid) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final String newGuid = Utils.setSpooferOverride(spooferTransport,scenarios.getScenarionName());

        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator, newGuid);

        GetDetailsHelper.getDetailsPhoneListVerification(getDetailsVerificationInput,
                spooferTransport,
                scenarios,
                newGuid,
                logger);

    }
    @SuppressWarnings("CPD-END")

    //==========================================================================================================
    //  E2E cert test cases
    //==========================================================================================================

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088GetDetailOnewayBasicValidation() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_Oneway_OnAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testBasicGetDetails( scenario, "3088", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088GetDetailRoundTripBasicValidation() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testBasicGetDetails( scenario, "3088", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088GetDetailOnAirportBasicValidation() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testBasicGetDetails( scenario, "3088", randomGuid);
    }

    //  get details for conditonal cost list
    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088GetDetailsRoundTripConditionalCostList() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetDetails4ConditionalCostList( scenario, "3088", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088USAGetDetailsOffAirportConditionalCostList() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetDetails4ConditionalCostList( scenario, "3088", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss308USAGetDetailsOnAirportConditionalCostList() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_Oneway_OnAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetDetails4ConditionalCostList( scenario, "3088", randomGuid);
    }

    //  get details for phone list
    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088USAGetDetailsPhoneList() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetDetails4PhoneList( scenario, "3088", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088CANGetDetailsPhoneList() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetDetails4PhoneList( scenario, "3088", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3088USAGetDetailsOnAirportPhoneList() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OnAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        testGetDetails4PhoneList( scenario, "3088", randomGuid);
    }
}
