package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.search;

import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.VerifySearchErrorAnalysisLogging;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 10/27/2016.
 */
public class ErrorAnalysisForSearch extends SuiteCommon {


    //@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void TFS_1056966_MSCS_ErrorAnalysisLogging_EnableON_OneError() throws Exception {
        //ErrorAnalysisForGetCostandAvail.Logging/enable on: Verify error logging is correct - MNSCS search - one error - no xPath
        VerifyErrorAnalysisLogged(CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "1056966", CommonEnumManager.ErrorHandlingType.InvalidPickupLocation, "ErrorAnalysis_oneError");
    }

    //@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void TFS_1056975_MSCS_ErrorAnalysisLogging_EnableON_TwoError() throws Exception {
        //ErrorAnalysisForGetCostandAvail.Logging/enable on: Verify error logging is correct - MNSCS search - two errors in MN - no xPath
        VerifyErrorAnalysisLogged(CommonScenarios.MN_GBR_Standalone_OneWay_OffAirport_AGP.getTestScenario(), "1056975", CommonEnumManager.ErrorHandlingType.InvalidLocationOneWay, "ErrorAnalysis_twoError");
    }

    //@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void TFS_1057623_MSCS_ErrorAnalysisLogging_EnableON_TwoError() throws Exception {
        //spoofer only
        //ErrorAnalysisForGetCostandAvail.Logging/enable on: Verify error logging is correct - MNSCS search - an error from MN is mapped to two different errors in SCS
        VerifyErrorAnalysisLogged(CommonScenarios.MN_GBR_Package_RoundTrip_OnAirport_CDG.getTestScenario(), "1057623", CommonEnumManager.ErrorHandlingType.InvalidPickupLocation, "ErrorAnalysis_invalidLocation");
    }

    public void VerifyErrorAnalysisLogged(TestScenario scenarioName, String tuid,
                                          CommonEnumManager.ErrorHandlingType errorHandlingType, String templateSpooferName) throws Exception {

        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, templateSpooferName);
        final TestData testData = new TestData(httpClient, scenarioName, String.valueOf(tuid), guid, spooferTransport);

        //Search
        final SCSSearchRequestGenerator requestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        final CarSupplyConnectivitySearchRequestType searchRequest = requestGenerator.createSearchRequest(testData);
        if (CommonEnumManager.ErrorHandlingType.InvalidPickupLocation.equals(errorHandlingType)) {
            searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getStartCarLocationKey().setLocationCode("AAA");
        } else if (CommonEnumManager.ErrorHandlingType.InvalidLocationOneWay.equals(errorHandlingType)) {
            searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getStartCarLocationKey().setLocationCode("AAA");
            searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getEndCarLocationKey().setLocationCode("ABC");
        }
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(),
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());


        //Get splunk data
        Thread.sleep(10000);
        final Date endTime = new Date();
        String serviceName="cars-supplyconnectivity-micronnexus-service";
        final String splunkQuery = "index=app sourcetype=" +serviceName + " host=" + serviceName + "ip*" + " OriginatingGUID=" + testData.getGuid() + " ActionType=Search LogType=ErrorAnalysis TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60) + "| stats values(ErrorReturnCode),values(ErrorText),values(DownstreamMessageType),values(TPID),values(TUID),values(POSJurisdiction),values(POSManagementUnit),values(POSCompany),values(RequestSupplierIDs),values(FieldXPath),values(ActionType),values(LogType)";
        // Splunk host address
        final String hostName = "https://splunk.us-west-2.test.expedia.com";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = spooferTransport.retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - Search
        final VerifySearchErrorAnalysisLogging searchVerifier = new VerifySearchErrorAnalysisLogging();
        searchVerifier.verify(searchVerificationInput, verificationContext, splunkResult,testData);

    }

}
