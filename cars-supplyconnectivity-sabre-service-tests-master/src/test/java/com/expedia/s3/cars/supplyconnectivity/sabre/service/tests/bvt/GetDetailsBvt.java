package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.bvt;

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
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.RetryAnalyzer;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;

import org.testng.annotations.Test;

public class GetDetailsBvt extends SuiteContext {

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.BVT})
    public void casss2775OnAirportGetDetailsSanityTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW.getTestScenario();
        testDetails(Utils.generateRandomGuid(), scenario, "2775");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.BVT})
    public void casss2775OffAirportGetDetailsSanityTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        testDetails(Utils.generateRandomGuid(), scenario, "2775");
    }
    private void testDetails(String randomGuid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        //1,search
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, randomGuid);

        final String newRandomGuid = Utils.generateRandomGuid();

        //2. Generate Details request with a random product from Search Response
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createDetailsRequest(), newRandomGuid);
        GetDetailsHelper.getDetailsVerification(getDetailsVerificationInput, spooferTransport, scenarios, newRandomGuid, logger, false);
    }
}
