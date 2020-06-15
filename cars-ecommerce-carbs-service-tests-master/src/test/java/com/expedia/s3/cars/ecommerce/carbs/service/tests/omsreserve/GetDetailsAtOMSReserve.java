package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.GDSDetailsRequestSentVerifier;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 10/24/2018.
 */
public class GetDetailsAtOMSReserve  extends SuiteCommon {

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs321785AmadeusGetDetailsAtOMSReserve() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        testGetDetailsAtOMSReserve(testScenario, "321785", "");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs349525AmadeusGetDetailsFailAtOMSReserve() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario();
        testGetDetailsAtOMSReserve(testScenario, "349525", "Amadues_GetDetailsFail");
    }

    private void testGetDetailsAtOMSReserve(TestScenario scenario, String tuid, String spooferScenarioName) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        if(StringUtils.isNotBlank(spooferScenarioName)) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenarioName).build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);

        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(scenario, tuid, guid, httpClient, false);
        //OMS reserve
        CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //Verify getDetails is sent
        final GDSDetailsRequestSentVerifier verifier = new GDSDetailsRequestSentVerifier();
        verifier.verifyGDSDetailsRequestSent(spooferTransport, testData.getGuid());



    }

    }
