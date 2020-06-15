package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;

import org.testng.annotations.Test;

public class SearchBvt extends SuiteContext
{
    @Test(groups = {TestGroup.BVT})
    public void casss816onAirportSearchBVT() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW.getTestScenario();
        performBVTTest(Utils.generateRandomGuid(), scenario, "816");
    }

    @Test(groups = {TestGroup.BVT})
    public void casss816OffAirportSearchBVT() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        performBVTTest(Utils.generateRandomGuid(), scenario, "816");
    }

    private void performBVTTest(String randomGuid, TestScenario scenarios, String tuid)  throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        //1,search
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData,50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, randomGuid);
        SearchHelper.searchVerification(searchVerificationInput,spooferTransport,scenarios,randomGuid,logger,false);
    }
}
