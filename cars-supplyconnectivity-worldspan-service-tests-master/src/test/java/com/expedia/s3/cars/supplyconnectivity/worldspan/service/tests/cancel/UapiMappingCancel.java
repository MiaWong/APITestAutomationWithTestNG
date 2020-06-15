package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.cancel;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandlingValue;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.TP95CommonVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiErrorMappingCancelVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapCancleVerification;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Created by yyang4 on 10/19/2017.
 */
public class UapiMappingCancel extends SuiteCommon {
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1009232UAPICancelSCSMapping() throws Exception {
        final String tuid = "1009232";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSCancelUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1009233UAPICancelSCSMappingCancelledPNR() throws Exception {
        final String tuid = "1009233";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSCancelUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1044953UAPICancelSCSMappingEmptyPNR() throws Exception {
        final String tuid = "1044953";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSCancelErrorMapping(testScenario, tuid, testData, spooferTransport, guid, "EmptyPNRCode", ErrorHandlingValue.ErrorMapping_uAPI_EmptyPNRCode);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1044954UAPICancelSCSMapping() throws Exception {
        final String tuid = "1044954";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSCancelErrorMapping(testScenario, tuid, testData, spooferTransport, guid, "EmptyPNRValue", ErrorHandlingValue.ErrorMapping_uAPI_EmptyPNRValue);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10449545UAPICancelSCSMappingInvalidPNR() throws Exception {
        final String tuid = "1044955";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSCancelErrorMapping(testScenario, tuid, testData, spooferTransport, guid, "InvalidPNR", ErrorHandlingValue.ErrorMapping_uAPI_InvalidPNR);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046153UAPICancelSCSMappingPNRUnavailable() throws Exception {
        final String tuid = "1046153";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSCancelErrorMapping(testScenario, tuid, testData, spooferTransport, guid, "PNRUnavailable", ErrorHandlingValue.ErrorMapping_uAPI_InvalidAddress);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1009234TP95TestCancelUAPIAgency()throws Exception {
        final String tuid = "1009234";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1009235TP95TestCancelUAPIGDSP()throws Exception {

        final String tuid = "1009235";
        final TestScenario testScenario = CommonScenarios.Worldspan_GBR_10111_1012_GDSP_Standalone_OffAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1009237TP95TestCancelUAPIAgency()throws Exception {
        final String tuid = "1009237";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1009238TP95TestCancelUAPIGDSPPackage()throws Exception {
        final String tuid = "1009238";
        final TestScenario testScenario = CommonScenarios.Worldspan_FR_Merchant_Package_nonFRLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    public void testCarSCSCancelUAPIMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid) throws Exception {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        ExecutionHelper.reserveVerify(verificationInput, spooferTransport, testScenario, guid, logger);


        //3,Cancel the reserve
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(verificationInput.getResponse());
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testScenario, guid, logger);


        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        UapiMapCancleVerification.VerifyWSCSCancelForuAPI(basicVerificationContext, cancelVerificationInput, DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient);
    }

    public void testCarSCSCancelErrorMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid, String unhappyCaseType, ErrorHandlingValue errHandle) throws Exception {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        ExecutionHelper.reserveVerify(verificationInput, spooferTransport, testScenario, guid, logger);


        //3,Cancel the reserve
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(verificationInput.getResponse());
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator4.createCancelRequest();
        for (ReferenceType reference : cancelRequestType.getCarReservation().getReferenceList().getReference()) {
            if ("PNR".equals(reference.getReferenceCategoryCode())) {
                if ("EmptyPNRCode".equals(unhappyCaseType)) {
                    reference.setReferenceCategoryCode("");
                } else if ("EmptyPNRValue".equals(unhappyCaseType)) {
                    reference.setReferenceCode("");
                } else if ("InvalidPNR".equals(unhappyCaseType)) {
                    spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "InvalidPNR").build(), guid);
                    reference.setReferenceCode(reference.getReferenceCode() + "56");
                } else if ("PNRUnavailable".equals(unhappyCaseType)) {
                    spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "PNRUnavailable").build(), guid);
                }
            }
        }



        cancelRequestType.getAuditLogTrackingData().getLogonUserKey().setUserID(Long.valueOf(tuid + "0"));
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);


        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        UapiErrorMappingCancelVerification.errorMappingVerifier(cancelVerificationInput.getResponse().getErrorCollection(), errHandle.getErrorHandling());
    }
    public void doTP95Test(TestScenario testScenario,String tuid)throws Exception{
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setSpooferTransport(spooferTransport);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        ExecutionHelper.reserveVerify(verificationInput, spooferTransport, testScenario, guid, logger);


        //3,Cancel the reserve
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(verificationInput.getResponse());
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testScenario, guid, logger);


        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        UapiMapCancleVerification.VerifyWSCSCancelForuAPI(basicVerificationContext, cancelVerificationInput, DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient);

        TP95CommonVerification.tp95PerfMetricsCancelVerify(testData,cancelVerificationInput);
    }
}
