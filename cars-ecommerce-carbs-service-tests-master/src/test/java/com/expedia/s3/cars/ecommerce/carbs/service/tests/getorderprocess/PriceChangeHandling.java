package com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess;

import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.CommonTestHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.StatusCodeCategoryType;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 10/25/2018.
 */
public class PriceChangeHandling extends SuiteCommon {

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs370938GetOrderProcessBusinessErrorAmadeus() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        testPriceChange(testScenario, "370938", "", CarCommonEnumManager.ClientID.ClientID_3);
    }

    private void testPriceChange(TestScenario scenario, String tuid, String spooferScenarioName,
                               CarCommonEnumManager.ClientID clientID) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        if (StringUtils.isNotBlank(spooferScenarioName)) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenarioName).build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);
        testData.setClientCode(CommonTestHelper.getClientCode(clientID));

        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(scenario, tuid, guid, httpClient, false);

        // Build and send GetOrderProcess request to CarBS (Verify if the product can be reserved).
        final GetOrderProcessRequest getOrderProcessRequest = carOMSReqAndRespObj.createGetOrderProcessRequest(testData);

        //Set request total to different value
        for(final PriceType price : getOrderProcessRequest.getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct().getPriceList().getPrice())
        {
            if("Total".equals(price.getFinanceCategoryCode()))
            {
                price.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(0);
            }
        }
        final GetOrderProcessResponseType getOrderProcessResponse = CarbsOMServiceSender.sendGetOrderProcessResponse(testData.getGuid(), testData.getHttpClient(), getOrderProcessRequest);

        //Verify business error returned in response
        if(!StatusCodeCategoryType.BUSINESS_ERROR.equals(getOrderProcessResponse.getResponseStatus().getStatusCodeCategory()))
        {
            Assert.fail("Business error is not returned in response!");
        }
    }
}
