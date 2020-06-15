package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import expedia.om.supply.messages.defn.v1.RetrieveRequest;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by v-mechen on 11/26/2018.
 */
public class ErrorHandling extends SuiteCommon {

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282305RetrieveInvalidLocator() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "282305", PojoXmlUtil.getRandomGuid());
        testRetrieveError(testData, 1111111, "5029", "get state failed for crl: 1111111");
    }

    @SuppressWarnings("CPD-START")
    private void testRetrieveError(TestData testData, long recordLocator,String errorID, String errorDesc) throws Exception{
        //booking with shop
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        final RetrieveRequest retrieveRequest = carbsOMRetrieveReqAndRespGenerator.createRetrieveRequest();
        retrieveRequest.setRecordLocator(recordLocator);
        final RetrieveResponseType response = CarbsOMServiceSender.sendRetrieveResponse(testData.getGuid(), httpClient,retrieveRequest);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //Verify error
        verifyError(response, errorID, errorDesc);

    }

    private void verifyError(RetrieveResponseType response,String errorID, String errorDesc){
        //Verify errorID returned
        final List<String> errorIDList = PojoXmlUtil.getXmlFieldValue(response, "ErrorID");
        if(CollectionUtils.isEmpty(errorIDList) || !errorIDList.get(0).equals(errorID)){
            Assert.fail("Expected ErrorID is not returned:" + errorID);
        }

        //ErrorDescription
        //Verify ErrorDescription returned
        final List<String> errorDescList = PojoXmlUtil.getXmlFieldValue(response, "ErrorDescription");
        if(CollectionUtils.isEmpty(errorDescList) || !errorDescList.get(0).contains(errorDesc)){
            Assert.fail("Expected ErrorDescription is not returned:" + errorDesc);
        }
    }



}
