package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 11/27/2018.
 */
public class RetrieveLocation extends SuiteCommon {

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs432293CopyDropOffLocationFromPickup() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_GBR_10111_1012_GDSP_Standalone_OffAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "432293", PojoXmlUtil.getRandomGuid());
        testRetrieveLocation(testData);
    }

    @SuppressWarnings("CPD-START")
    private void testRetrieveLocation(TestData testData) throws Exception{
        //booking with shop
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, false);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        verifyDropoffLocationIsFromPickupLocation(carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType());
    }

    public static void verifyDropoffLocationIsFromPickupLocation(RetrieveResponseType retrieveResponse)  {
        final CarProductType rspCar =  retrieveResponse.getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation()
                .getCarProduct();
        final CarLocationType pickLocation = rspCar.getCarPickupLocation();
        final CarLocationType dropLocation = rspCar.getCarDropOffLocation();
        final StringBuilder erroMsg = new StringBuilder();
        //CarShuttleCategoryCode
        final List<String> ignoreList = new ArrayList<>();
        ignoreList.add("carShuttleCategoryCode");
        final boolean compared = CompareUtil.compareObject(pickLocation, dropLocation,ignoreList, erroMsg);
        if (!compared) {
            org.testng.Assert.fail(String.format("CarDropOffLocation is not same as CarPickupLocation, compare error: %s!", erroMsg.toString()));
        }
    }

}
