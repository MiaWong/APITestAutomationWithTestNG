package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;
import org.testng.annotations.Test;

/**
 * Created by aaniyath on 18-10-2016.
 */
public class Search extends SuiteContext
{
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310CANRoundTripOnAirportSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        performRegressionTest(randomGuid, scenario, "2310");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310CANRoundTripOffAirportSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        performRegressionTest(randomGuid, scenario, "2310");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310USARoundTripOnAirportSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OnAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        performRegressionTest(randomGuid, scenario, "2310");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310USARoundTripOffAirportSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        performRegressionTest(randomGuid, scenario, "2310");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310CANOnewayOnAirportSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        performRegressionTest(randomGuid, scenario, "2310");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310USAOnewayOnAirportSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_Oneway_OnAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        performRegressionTest(randomGuid, scenario, "2310");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2310DiscountSearch() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport,scenario.getScenarionName());
        performRegressionTestForDiscountSearch(randomGuid, scenario, "2310");
    }

    private void performRegressionTest(String randomGuid, TestScenario scenarios, String tuid) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, randomGuid);
        SearchHelper.searchRegressionVerification( searchVerificationInput, spooferTransport, scenarios, randomGuid, logger,true);
    }

    private void performRegressionTestForDiscountSearch(String randomGuid, TestScenario scenarios, String tuid) throws Exception
    {
        final SearchVerificationInput verificationInput = SearchHelper.searchWithDiscountCode(httpClient, scenarios, tuid, randomGuid);
        SearchHelper.discountSearchVerification(verificationInput, spooferTransport, scenarios, randomGuid, logger,true);
    }
}
