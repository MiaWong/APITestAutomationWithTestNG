package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMGetChangeDetailReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMGetChangeDetailSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyPriceInGetChangeDetail;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;

public class GetChangeDetail  extends SuiteCommon
{

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs318403GetChangeDetailsAmadeusCarStandaloneRoundTrip() throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "Amadues_Standalone_OneWay").build(), randomGuid);
        executeOMSGetChangeDetail(scenario, "457777", randomGuid, true);
    }

    /*
    In this case we will pass LegecySiteKey in SiteMessageInfo as null, so that CARBS will set TPID as zero
    */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1033747GetChangeDetailsMNGDSPStandaloneOneWayWithoutTPIDFRLocation() throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "MicroNexus_ThriftyFrance_Standalone_FRPOS_FRLOCATION").build(), randomGuid);
        executeOMSGetChangeDetail(scenario, "557777", randomGuid, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs320714GetChangeDetailWordspanCarAgencyStandaloneOnewayDaily() throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport_oneway.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "USAgencyStandaloneLatLong").build(), randomGuid);
        executeOMSGetChangeDetail(scenario, "557777", randomGuid, true);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs320717GetChangeDetailWordspanCarGDSPStandaloneRoundtripDailyUK() throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "ExtraHourPrice").build(), randomGuid);
        executeOMSGetChangeDetail(scenario, "657777", randomGuid, true);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs323428GetChangeDetailMNGDSPStandaloneRoundTripWeeklyUK() throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.MN_GBR_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "MN_differentOriginalCurrency").build(), randomGuid);
        executeOMSGetChangeDetail(scenario, "757777", randomGuid, true);
    }

    private void executeOMSGetChangeDetail(TestScenario scenario, String tuid, String guid, boolean withTPID) throws Exception
    {
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final String clientCode = carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode();
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);
        testData.setClientCode(clientCode);

        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg
                (testData);
        //getChangeDetail
        final CarbsOMGetChangeDetailReqAndRespGenerator carbsOMGetChangeDetailReqAndRespGenerator = new CarbsOMGetChangeDetailReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMGetChangeDetailSender.carBSOMGetChangeDetailSend(scenario, guid, httpClient, carbsOMGetChangeDetailReqAndRespGenerator, withTPID);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenario, omsCancelReqAndRespObj, guid, httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        final String actualTotalPrice = carbsOMGetChangeDetailReqAndRespGenerator.getGetChangeDetailResponseType().getChangeDetailTargetData().getTotalAmountWithTax().getSimpleAmount();
        final String actualCurrencyCode = carbsOMGetChangeDetailReqAndRespGenerator.getGetChangeDetailResponseType().getChangeDetailTargetData().getTotalAmountWithTax().getCurrencyCode();

        VerifyPriceInGetChangeDetail.totalPriceForOMSGetChangeDetailVerifier(actualTotalPrice, actualCurrencyCode, carbsOMReserveReqAndRespGenerator);
    }

}
