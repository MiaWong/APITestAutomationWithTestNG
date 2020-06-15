package com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 11/20/2018.
 */
public class ErrorHandling extends SuiteCommon {

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs443342GetOrderProcessCarProductNotAvailableError() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        testError(testScenario, "443342", "ErrorMap1", "CarProductNotAvailableError");
    }

    private void testError(TestScenario scenario, String tuid, String spooferScenarioName, String errorType) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        if (StringUtils.isNotBlank(spooferScenarioName)) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenarioName).build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);

        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(scenario, tuid, guid, httpClient, false);

        // Build and send GetOrderProcess request to CarBS (Verify if the product can be reserved).
        final GetOrderProcessRequest getOrderProcessRequest = carOMSReqAndRespObj.createGetOrderProcessRequest(testData);
        final GetOrderProcessResponseType getOrderProcessResponse = CarbsOMServiceSender.sendGetOrderProcessResponse(testData.getGuid(), testData.getHttpClient(), getOrderProcessRequest);

        //Verify error
        if(CollectionUtils.isEmpty(PojoXmlUtil.getXmlFieldValue(getOrderProcessResponse, errorType))){
            org.testng.Assert.fail(String.format("%s should be returned!", errorType));
        }
    }
}
