package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.search;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARRsp;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.search.mapverify.SearchMapVerification;
import org.testng.annotations.Test;

/**
 * Created by ankimaheshwar on 29/07/2018.
 */
public class CostPrice extends SuiteCommon{

    // Test MicronNexus Daily search, verify that CostList is correct in MNSCS response compared with VAR message
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test115493StandaloneDailySearchForCostList() throws Exception {
        testCostListOfMNXSearch(CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_PMI.getTestScenario(), "CostList", "115493", CommonEnumManager.TimeDuration.Days3, false);
    }

    // Test MicronNexus Weekly search, verify that CostList is correct in MNSCS response compared with VAR message
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test115795StandaloneWeeklySearchForCostList() throws Exception {
        testCostListOfMNXSearch(CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "CostList", "115795", CommonEnumManager.TimeDuration.WeeklyDays6, false);
    }

    // Test MicronNexus Daily search, verify that CostList is correct in MNSCS response compared with VAR message
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test126208StandaloneMonthlySearchForCostList() throws Exception {
        testCostListOfMNXSearch(CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "CostList", "126208", CommonEnumManager.TimeDuration.Mounthly, false);
    }

    public void testCostListOfMNXSearch(TestScenario scenario, String verifyType, String tuid, CommonEnumManager.TimeDuration timeDuration, boolean extraHours) throws Exception {

        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String searchGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, timeDuration,scenario, tuid, searchGuid, extraHours);
        //request and response
        final CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, searchGuid);
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(searchGuid), searchGuid, testData.getScenarios());
        //validating the response
        final VARRsp varRsp = new VARRsp(verificationContext.getSpooferTransactions().getElementsByTagName("Response").item(0), new CarsSCSDataSource(SettingsProvider.CARMNSCSDATASOURCE));
        SearchMapVerification.AssertMNSCSSearchMessage(searchVerificationInput.getResponse(), varRsp, verifyType);
    }
}
