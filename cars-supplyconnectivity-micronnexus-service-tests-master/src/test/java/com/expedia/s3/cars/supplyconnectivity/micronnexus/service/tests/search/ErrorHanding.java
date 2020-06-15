package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandlingValue;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.errorhandlingverify.ErrorHandlingVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.VerifySearchPerfmetricsLogging;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 12/29/2016.
 */
public class ErrorHanding extends SuiteCommon {


     // Test MicronNexus error search, verify that CarTypeNotAvailableErrorType returned in MNSCS response
    // @Test(groups = {TestGroup.SHOPPING_REGRESSION})
     @Test(groups = {"splunkRegression"})
    public void test93521CarTypeNotAvailableErrorTypeMNXErrorSearch() throws IOException, DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        testBasicMNXErrorSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),"CarTypeNotAvailableError", 93521);
    }


    // Test MicronNexus error search, verify that FieldInvalidErrorListType with error message "The XML-Request is incorrect" returned in MNSCS response
    //@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test93515FieldInvalidErrorListTypeMNXErrorSearch() throws IOException, DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        testBasicMNXErrorSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "FieldInvalidError", 93515);
    }


    //Test MicronNexus error search, verify that FieldRequiredErrorListType returned in MNSCS response
    //@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test93516FieldRequiredErrorListTypeMNXErrorSearch() throws IOException, DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        testBasicMNXErrorSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "FieldRequiredError", 93516);
    }



    //Test MicronNexus error search, verify that RentalOutOfRangeErrorType returned in MNSCS response
   // @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test93519RentalOutOfRangeErrorTypeMNXErrorSearch() throws IOException, DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        testBasicMNXErrorSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "RentalOutOfRangeError", 93519);
    }



    public void testBasicMNXErrorSearch(TestScenario scenario, String errorType, int tuid) throws IOException, DataAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport,"MicronNexus_Search_Error");
        TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), guid, spooferTransport);
        CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);

        IVerification.VerificationResult result = new IVerification.VerificationResult("", true, null);
        //Need reset request to reconstruct error search request according to different error type
        if (errorType.equals(ErrorHandlingValue.ErrorHandling_search_CarTypeNotAvailable.getErrorHandling().getErrorType()))
        {
            //Set PickupLocation and DropoffLocation to PMD(A locationCode that not in the PickupCountry)
            for (CarSearchCriteriaType criteria :searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria())
            {
                criteria.getCarTransportationSegment().getStartCarLocationKey().setLocationCode(ErrorHandlingValue.ErrorHandling_search_CarTypeNotAvailable.getErrorHandling().getInvalidValue());
                criteria.getCarTransportationSegment().getEndCarLocationKey().setLocationCode(ErrorHandlingValue.ErrorHandling_search_CarTypeNotAvailable.getErrorHandling().getInvalidValue());
            }
            verificationResult(testData, errorType, searchRequestType, ErrorHandlingValue.ErrorHandling_search_CarTypeNotAvailable.getErrorHandling().getErrormessage());


        }
       else if (errorType.equals(ErrorHandlingValue.ErrorHandling_search_FieldInvalidError.getErrorHandling().getErrorType()))
        {
            //Set PickupLocation and DropoffLocation to AAA(An invalid LocationCode)
            for (CarSearchCriteriaType criteria : searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria())
            {
                criteria.getCarTransportationSegment().getStartCarLocationKey().setLocationCode(ErrorHandlingValue.ErrorHandling_search_FieldInvalidError.getErrorHandling().getInvalidValue());
                criteria.getCarTransportationSegment().getEndCarLocationKey().setLocationCode(ErrorHandlingValue.ErrorHandling_search_FieldInvalidError.getErrorHandling().getInvalidValue());
            }

            //verify
            verificationResult(testData, errorType, searchRequestType, ErrorHandlingValue.ErrorHandling_search_FieldInvalidError.getErrorHandling().getErrormessage());

        }

        else if (errorType.equals(ErrorHandlingValue.ErrorHandling_search_FieldRequiredError.getErrorHandling().getErrorType()))
        {
            //Remove CurrencyCode node from request
            searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).setCurrencyCode(null);

            //verify
            verificationResult(testData, errorType, searchRequestType, ErrorHandlingValue.ErrorHandling_search_FieldRequiredError.getErrorHandling().getErrormessage());

        }

        else if (errorType.equals(ErrorHandlingValue.ErrorHandling_search_RentalOutOfRangeError.getErrorHandling().getErrorType()))
        {
            //Set PickupDateTime = DropoffDateTime
            searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().setMinDateTime(
                    searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().getMinDateTime());
            searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().setMaxDateTime(
                    searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().getMaxDateTime());
            //verify
            verificationResult(testData, errorType, searchRequestType, ErrorHandlingValue.ErrorHandling_search_RentalOutOfRangeError.getErrorHandling().getErrormessage());

        }

    }

    private void verificationResult(TestData testData, String errorType, CarSupplyConnectivitySearchRequestType searchRequestType, String message) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //Send Request

        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, testData.getGuid());
        //verify
        IVerification.VerificationResult result = ErrorHandlingVerification.isExpectMessageVerification(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse()),errorType, message);
        if(!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        try {
            Thread.sleep(10000);

        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime()/1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunk.us-west-2.test.expedia.com";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - Search
        final VerifySearchPerfmetricsLogging searchVerifier = new VerifySearchPerfmetricsLogging();
        searchVerifier.verify(searchVerificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.COSTAVAILREQUEST, splunkResult );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
}
