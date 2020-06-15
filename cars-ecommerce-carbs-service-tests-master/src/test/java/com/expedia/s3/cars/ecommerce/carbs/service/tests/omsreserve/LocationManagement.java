package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyLocationManagementCase;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;

public class LocationManagement extends SuiteCommon
{
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1080231CarBSOMSLocationManagement() throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        executeCarBSOMSLocationManagement(scenario, "547777", randomGuid);
    }

    private void executeCarBSOMSLocationManagement(TestScenario scenario, String tuid, String guid) throws Exception
    {
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final String clientCode = carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_1.getValue()).get(0).getClientCode();
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);
        testData.setClientCode(clientCode);

        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg
                (testData);

        //Retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(scenario,guid, httpClient, carbsOMRetrieveReqAndRespGenerator, false);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenario, omsCancelReqAndRespObj, guid, httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        // Verify GetOrderProcess, PreparePurchase, Retrieve and PrepareChange Responses should have CarVendorLocationID and other 3 components in pickup and dropoff location keys
        VerifyLocationManagementCase.verifyAllResponse(carbsOMRetrieveReqAndRespGenerator);
    }
}
