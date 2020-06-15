package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.getreservation;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandlingValue;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.TP95CommonVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiErrorMappingGetReservationVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapGetReservationVerification;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.SQLException;

import static com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport;

/**
 * Created by yyang4 on 10/15/2017.
 */
public class UapiMappingGetReservation extends SuiteCommon {
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046617UAPIGetReservationSCSMappingAgencyDaily() throws Exception {
        final String tuid = "1046617";
        final TestScenario testScenario = CommonScenarios.Worldspan_Travelocity_CA_Agency_Standalone_CALocation_OnAirport_OneWay.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days2);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046616UAPIGetReservationSCSMappingAgency4Days() throws Exception {
        final String tuid = "1046616";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days4);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046639UAPIGetReservationSCSMappingOneway() throws Exception {
        final String tuid = "1046639";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MCO_LAX_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046640UAPIGetReservationSCSMappingOffairportOneway() throws Exception {
        final String tuid = "1046640";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046638UAPIGetReservationSCSMappingWeekly() throws Exception {
        final String tuid = "1046638";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046641UAPIGetReservationSCSMappingWeeklyExtra() throws Exception {
        final String tuid = "1046641";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays12);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046642UAPIGetReservationSCSMappingGDSPPackage() throws Exception {
        final String tuid = "1046642";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046643UAPIGetReservationSCSMappingGDSPStandalone() throws Exception {
        final String tuid = "1046643";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays);
        testCarSCSGetReservationUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046621UAPIGetReservationSCSMappingInvalidPNR() throws Exception {
        final String tuid = "1046621";
        final TestScenario testScenario = Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSGetReservationUAPIErrorMapping(testScenario, tuid, testData, spooferTransport, guid, ErrorHandlingValue.ErrorMapping_uAPI_InvalidPNR);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //merge cases 1046618,1046620
    public void casss1046618UAPIGetReservationSCSMappingEmptyPNR() throws Exception {
        final String tuid = "1046618";
        final TestScenario testScenario = Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSGetReservationUAPIErrorMapping(testScenario, tuid, testData, spooferTransport, guid, ErrorHandlingValue.ErrorMapping_uAPI_EmptyPNRCode_GetReservation);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1046630TP95TestGetReservationUAPIAgency() throws Exception {
        final String tuid = "1046630";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_CALocation_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1046631TP95TestGetReservationUAPIAgency() throws Exception {
        final String tuid = "1046631";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_GDSP_Standalone_CALocation_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1046632TP95TestGetReservationUAPIAgency() throws Exception {
        final String tuid = "1046632";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss1046633TP95TestGetReservationUAPIAgency() throws Exception {
        final String tuid = "1046633";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss492083SpecialEquipmentTestUAPIAgency() throws Exception {
        final String tuid = "492083";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        doSpecialEquipmentTest(testScenario, tuid, ReserveDefaultValue.VAILDSPECIALEQUIPMENTLISTANDEMPTYVEHICLEOPTIONLIST);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss494914SpecialEquipmentTestUAPIAgency() throws Exception {
        final String tuid = "494914";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        doSpecialEquipmentTest(testScenario, tuid, ReserveDefaultValue.CARSPECIALEQUIPMENTLIST_MASERATIDOMIANVALUE_CSI);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss492081SpecialEquipmentTestUAPIAgency() throws Exception {
        final String tuid = "492081";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
        doSpecialEquipmentTest(testScenario, tuid, ReserveDefaultValue.CarSpecialEquipmentList_MaseratiDomianValue_CSI_NVS);
    }

    // <summary>
    // Basic method for UAPI cost&Avail mapping
    // </summary>
    public void testCarSCSGetReservationUAPIMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid) throws Exception {
        doCommonTest(testScenario,tuid,testData,spooferTransport,guid,null,false);
    }

    public void testCarSCSGetReservationUAPIErrorMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid, ErrorHandlingValue errHandle) throws Exception {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        //3,Cancel the first
        requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testScenario, guid, logger);


        //4.getReservation
        final CarSupplyConnectivityGetReservationRequestType getReservationRequest = requestGenerator4.createGetReservationRequest();

        for (ReferenceType reference : getReservationRequest.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference()) {
            if ("PNR".equals(reference.getReferenceCategoryCode())) {
                if ("ErrorMapping_uAPI_EmptyPNRCode_GetReservation".equals(errHandle.toString())) {
                    reference.setReferenceCategoryCode("");
                } else if ("ErrorMapping_uAPI_EmptyPNRValue_GetReservation".equals(errHandle.toString())) {
                    reference.setReferenceCode("");
                } else if ("ErrorMapping_uAPI_InvalidPNR".equals(errHandle.toString())) {
                    spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "InvalidPNR").build(), guid);
                    reference.setReferenceCode(reference.getReferenceCode() + "56");
                }
            }
        }

        getReservationRequest.getAuditLogTrackingData().getLogonUserKey().setUserID(Long.valueOf(tuid + "0"));
        final GetReservationVerificationInput getReservationVerificationInput = TransportHelper.sendReceive(httpClient,
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequest, guid);
        requestGenerator4.setGetReservationReq(getReservationRequest);
        requestGenerator4.setGetReservationResp(getReservationVerificationInput.getResponse());
        System.out.println("getReservationRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationRequest)));
        System.out.println("getReservationResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));

        UapiErrorMappingGetReservationVerification.errorMappingVerifier(getReservationVerificationInput.getResponse().getErrorCollection(), errHandle.getErrorHandling());

    }

    public void doTP95Test(TestScenario testScenario, String tuid) throws Exception {
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
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        //3.getReservation
        final GetReservationVerificationInput getReservationVerificationInput = ExecutionHelper.getReservation(httpClient, requestGenerator4, guid);
        System.out.println("getReservationRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
        System.out.println("getReservationResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));
        TP95CommonVerification.tp95PerfMetricsGetReservationVerify(testData, getReservationVerificationInput);
        //4,Cancel the reserve
        requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testScenario, guid, logger);

    }

    public void doSpecialEquipmentTest(TestScenario testScenario, String tuid, ReserveDefaultValue reserveDefaultValue) throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setSpooferTransport(spooferTransport);
        doCommonTest(testScenario,tuid,testData,spooferTransport,guid,reserveDefaultValue,true);
    }


    public void doCommonTest(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid, ReserveDefaultValue reserveDefaultValue, boolean needSpecialEquip) throws Exception {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        if (needSpecialEquip) {
            final PosConfigHelper helper = new PosConfigHelper(DataSourceHelper.getWSCSDataSourse());
            try {
                if (helper.checkPosConfigFeatureEnable(testScenario, "0", "Reserve.suppressSpecialEquipment/enable", SettingsProvider.ENVIRONMENT_NAME)) {
                    requestGenerator4.buildSpecialEquipmentList(reserveRequest, reserveDefaultValue);
                }
            }catch (SQLException e){

            }
        }
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);
        requestGenerator4.setReserveReq(reserveVerificationInput.getRequest());
        requestGenerator4.setReserveResp(reserveVerificationInput.getResponse());
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        //3.getReservation
        final GetReservationVerificationInput getReservationVerificationInput = ExecutionHelper.getReservation(httpClient, requestGenerator4, guid);
        System.out.println("getReservationRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
        System.out.println("getReservationResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getreservationVerify(getReservationVerificationInput, spooferTransport, testScenario, guid, logger);
        UapiMapGetReservationVerification.uapiMapVerifierWSCSGetReservation(basicVerificationContext, getReservationVerificationInput, reserveVerificationInput.getResponse(), DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient);

        //4,Cancel the reserve
        requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testScenario, guid, logger);

        //verify sepcial equipment
        if(needSpecialEquip){
            UapiMapGetReservationVerification.VerifySpecialEquipmentForWSPN(basicVerificationContext,reserveVerificationInput,getReservationVerificationInput.getResponse(),reserveDefaultValue);
        }
    }


}
