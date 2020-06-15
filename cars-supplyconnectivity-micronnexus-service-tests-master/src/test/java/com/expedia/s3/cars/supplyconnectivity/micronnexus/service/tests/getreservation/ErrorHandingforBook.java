package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getreservation;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic.VerifyCancelBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.customized.VerifyCancelPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.basic.VerifyGetReservationBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.customized.VerifyGetReservationPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.basic.VerifyReserveBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized.VerifyReservePerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
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
 * Created by fehu on 8/22/2017.
 */
public class ErrorHandingforBook extends SuiteCommon {


    //@Test(groups = {TestGroup.BOOKING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test135352MNXErrorHanding() throws Exception {
        testBasicMNXErrorHandingBooking(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "ReferenceIDExpired", 135352);
    }

    // Test CarMNSCS GetReservation: Invalid LastNam
    //@Test(groups = {TestGroup.BOOKING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test148206MNXErrorHanding() throws Exception {
        testBasicMNXErrorHandingBooking(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "InvalidLastName", 148206);
    }

    // Test CarMNSCS GetReservation: Invalid PNR
   // @Test(groups = {TestGroup.BOOKING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test148207MNXErrorHanding() throws Exception {
        testBasicMNXErrorHandingBooking(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "InvalidPNR", 148207);
    }

    //Verify that MN cancelling failed when trying to cancel an expired MN booking
   // @Test(groups = {TestGroup.BOOKING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void test146965MNXErrorHanding() throws Exception {
        testBasicMNXErrorHandingBooking(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "CancelAnExpiredMNCar", 146965);
    }

    private void testBasicMNXErrorHandingBooking(TestScenario scenario, String errorType, int tuid) throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), guid, spooferTransport);

        //Search
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport, logger, SettingsProvider.CARMNSCSDATASOURCE);
        SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());

        //Reserve
        final CarSupplyConnectivityReserveRequestType reserveRequestType = requestGenerator.createReserveRequest();
        if ("ReferenceIDExpired".equals(errorType)) {
            guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ErrorHanding_ReferenceIDExpired");
            reserveRequestType.getCarProduct().getCarInventoryKey().getCarRate().setCarRateQualifierCode("12345678");
        }
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequestType, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        IVerification.VerificationResult resultForReserve = null;

        if ("ReferenceIDExpired".equals(errorType)) {
            if (null != reserveVerificationInput.getResponse().getErrorCollection().getCarTypeNotAvailableError()
                    && (reserveVerificationInput.getResponse().getErrorCollection().getCarProductNotAvailableError().getDescriptionRawText() == null
                    || (reserveVerificationInput.getResponse().getErrorCollection().getCarProductNotAvailableError().getDescriptionRawText() != null
                    && !reserveVerificationInput.getResponse().getErrorCollection().getCarProductNotAvailableError().getDescriptionRawText().contains(
                    "The requested car is no longer available.")))) {
                Assert.fail("Expected error for referenceID expired does not return from CarBS Reserve response!");
            }

            //errorHanding perfMetrics verify
            perfMetricsVerify(guid, testData, reserveVerificationInput);
            return;
        } else {
            resultForReserve = verifyResultForReserve(scenario, spooferTransport, guid, reserveVerificationInput);
        }

        //getReservation
        if (null != resultForReserve && resultForReserve.isPassed()) {
           getReservationVerify(scenario, errorType, spooferTransport, guid, testData, requestGenerator);
    }
        //cancel
        if (null != resultForReserve && resultForReserve.isPassed()) {
            cancelVerify(errorType, spooferTransport, guid, testData, requestGenerator);

        }
    }

    private void cancelVerify(String errorType, SpooferTransport spooferTransport, String guid, TestData testData, SCSRequestGenerator requestGenerator) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator.createCancelRequest();
        if ("CancelAnExpiredMNCar".equals(errorType)) {
            guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ErrorHanding_CancelAnExpiredMNCar");
            for (ReferenceType reference : cancelRequestType.getCarReservation().getReferenceList().getReference()) {
                if ("PNR".equals(reference.getReferenceCategoryCode())) {
                    //Set ReferenceCode to unavailable number
                    reference.setReferenceCode("11111111");
                }
            }
        }
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, cancelRequestType, guid);

        if ("CancelAnExpiredMNCar".equals(errorType)) {
            if (cancelVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList() != null
                    && (cancelVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText() == null
                    || (cancelVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText() != null
                    && !cancelVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText().contains(
                    "Invalid PNR locator in request")))) {
                Assert.fail("Expected error do not be returned in MNSCS cancel response!");
            }

            //errorHanding perfMetrics verify
            perfMetricsVerify(guid, testData, cancelVerificationInput);
        } else {
            verifyResultForCancel(testData, cancelVerificationInput);
        }
    }

    private void getReservationVerify(TestScenario scenario, String errorType, SpooferTransport spooferTransport, String guid, TestData testData, SCSRequestGenerator requestGenerator) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
        final CarSupplyConnectivityGetReservationRequestType getReservationRequestType = requestGenerator.createGetReservationRequest();
        if ("InvalidLastName".equals(errorType)) {
            guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ErrorHanding_invalidLastName");
            for (TravelerType travelerType : getReservationRequestType.getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler()) {
                travelerType.getPerson().getPersonName().setLastName("Test");
            }
        } else if ("InvalidPNR".equals(errorType)) {
            guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ErrorHanding_invalidPNR");
            for (ReferenceType referenceType : getReservationRequestType.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference()) {
                if (referenceType.getReferenceCategoryCode().equals("PNR")) {
                    referenceType.setReferenceCode("49UAXN");
                }
            }

        }
        final GetReservationVerificationInput getReservationVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequestType, guid);
        if ("InvalidLastName".equals(errorType) || "InvalidPNR".equals(errorType)) {
            if (getReservationVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList() != null
                    && (getReservationVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText() == null
                    || (getReservationVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText() != null
                    && !getReservationVerificationInput.getResponse().getErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText().contains(
                    "Invalid PNR locator in request")))) {
                Assert.fail("Expected error do not be returned in MNSCS getReservation response!");
            }
            //errorHanding perfMetrics verify
            perfMetricsVerify(guid, testData, getReservationVerificationInput);
        } else {
            verifyResultForGetReservation(scenario, spooferTransport, guid, getReservationVerificationInput);
        }
    }

    private void perfMetricsVerify(String guid, TestData testData, BasicVerificationInput verificationInput) throws InterruptedException, IOException {
        Thread.sleep(10000);

        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + guid + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime()/1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(guid);
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, guid,
                testData.getScenarios());
        //Reserve
        if (verificationInput instanceof  ReserveVerificationInput) {
            final VerifyReservePerfmetricsLogging reserveVerifier = new VerifyReservePerfmetricsLogging();
            reserveVerifier.verify((ReserveVerificationInput) verificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.RESERVEREQUEST, splunkResult);
        }else if (verificationInput instanceof  GetReservationVerificationInput)
        {
            //GetReservation
            final VerifyGetReservationPerfmetricsLogging getReservationVerifier = new VerifyGetReservationPerfmetricsLogging();
            getReservationVerifier.verify((GetReservationVerificationInput)verificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.GETRESERVATIONREQUEST, splunkResult );

        }else if(verificationInput instanceof  CancelVerificationInput)
        {
            //Cancel
            final VerifyCancelPerfmetricsLogging cancelVerifier = new VerifyCancelPerfmetricsLogging();
            cancelVerifier.verify((CancelVerificationInput)verificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.CANCELREQUEST, splunkResult );
        }

    }

    private void verifyResultForCancel(TestData testData, CancelVerificationInput cancelVerificationInput) {
        final BasicVerificationContext verificationContexts = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());
        final VerifyCancelBasic verifier = new VerifyCancelBasic();
        final IVerification.VerificationResult resultForCancel = verifier.verify(cancelVerificationInput, verificationContexts);
        if (!resultForCancel.isPassed()) {
            Assert.fail(resultForCancel.toString());
        }
    }

    private void verifyResultForGetReservation(TestScenario scenario, SpooferTransport spooferTransport, String guid, GetReservationVerificationInput getReservationVerificationInput) throws IOException {
        final VerifyGetReservationBasic verify = new VerifyGetReservationBasic();
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(guid), guid, scenario);
        final IVerification.VerificationResult resultForGetReservation = verify.verify(getReservationVerificationInput, verificationContext);
        if (!resultForGetReservation.isPassed()) {
            Assert.fail(resultForGetReservation.toString());
        }
    }

    private IVerification.VerificationResult  verifyResultForReserve(TestScenario scenario, SpooferTransport spooferTransport, String guid, ReserveVerificationInput reserveVerificationInput) throws IOException {

        final VerifyReserveBasic verification = new VerifyReserveBasic();
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(guid), guid, scenario);
        final IVerification.VerificationResult verificationResult = verification.verify(reserveVerificationInput, basicVerificationContext);
        if (!verificationResult.isPassed()) {
            Assert.fail(verificationResult.toString());
        }
         return verificationResult;
    }

}
