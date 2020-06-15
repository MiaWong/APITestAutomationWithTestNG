package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 11/15/2018.
 * use the class to include test with only BVT verification - requests may be different
 */
public class CarOMSReserveWithBasicVerification extends SuiteCommon {

    //
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs455585MNMultiTraveler() throws Exception {
        final TestScenario testScenario = CommonScenarios.MicronNexus_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, "455585", guid);
        testData.setNeedMultiTraveler(true);
        testOMSReserve(testData);

    }

    //455767
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs455767tfs746036WorldspantGDSPPaymentInstrumentIDMultiTraveler() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, "455767", guid);
        testData.setNeedMultiTraveler(true);
        testData.setSpecialTest("CCCard");
        testData.setNeedPaymentInstrumentToken(true);
        testOMSReserve(testData);
    }

    //Worldspan_Travelocity_US_Agency_Standalone_USLocation_OnAirport_OneWay
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs434234tfs497499TravelocityUSLocaleSupport() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_Travelocity_US_Agency_Standalone_USLocation_OnAirport_OneWay.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, "434234", guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testData.setSetHyphenInLanguage(true);
        testOMSReserve(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs455677tfs436811TravelocityCAAirLoyaltyNoProgramCategoryCode() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_Travelocity_CA_Agency_Standalone_nonCALocation_OnAirport_Roundtrip.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, "455677", guid);
        testData.setCarSpecialEquipmentCode("InfantChildSeat,LeftHandControl");
        ExecutionHelper.setCarRateOfTestData(testData, true, "AirNoCategory-987654321", "");
        testData.setNeedTravelerLoyalty(true);
        testOMSReserve(testData);
    }

    //Worldspan_GBR_10111_1012_GDSP_Standalone_OffAirport
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs501919tfs434365BarclaysSiteAirLoyalty() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_GBR_10111_1012_GDSP_Standalone_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, "501919", guid);
        ExecutionHelper.setCarRateOfTestData(testData, true, "Air-987654321", "");
        testData.setNeedTravelerLoyalty(true);
        testOMSReserve(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //For test case 889573, carbs cancel should succeed when GDS cancel fail
    public void tfs498399tfs889573MNLanguageWtihHyphenGDSCancelFail() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "MN_CancelFail");
        final TestData testData = new TestData(httpClient, testScenario, "498399", guid);
        testData.setSetHyphenInLanguage(true);
        testOMSReserve(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs978300WorldspanGDSCancelFail() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_CALocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PNRUnavailable");
        final TestData testData = new TestData(httpClient, testScenario, "978300", guid);
        testOMSReserve(testData);
    }

    private void testOMSReserve(TestData testData) throws Exception {
        //OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //OMS retrieve
        final CarbsOMRetrieveReqAndRespGenerator retrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData, httpClient, retrieveReqAndRespGenerator, false);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }

}
