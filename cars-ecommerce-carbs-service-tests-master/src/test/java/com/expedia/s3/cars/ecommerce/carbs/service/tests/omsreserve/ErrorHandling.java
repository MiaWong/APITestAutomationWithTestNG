package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by v-mechen on 11/9/2018.
 */
public class ErrorHandling extends SuiteCommon {
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs420102Error10192() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        testErrorHandling(testScenario, "420102", false, "EH_10192_2");

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs281058PreparePurchaseAfterCommit() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        testErrorHandling(testScenario, "281058", true, "");

    }

    private void testErrorHandling(TestScenario scenario, String tuid, boolean sendPreparePurchaseAfterCommit, String specialEquipCode) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);
        if(StringUtils.isNotBlank(specialEquipCode))
        {
            testData.setSpecialEquipmentEnumType(specialEquipCode);
        }

        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(scenario, "746076", guid, httpClient, false);

        //OMS reserve for scenario: sendPreparePurchaseAfterCommit
        if(sendPreparePurchaseAfterCommit) {
            CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);
            final PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(),
                    carOMSReqAndRespObj.getPreparePurchaseRequestType());
            //Verify error in second preparePurchase response
            verifyError(preparePurchaseResponse, "5011", "Received request in state = COMMITTED state should be CREATED or PENDINGCOMMIT");
            //Cancel
            final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
            CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
        }
        else {
            //GetOrder
            CarbsOMReserveRequestSender.CarBSGetOrderProcessSend(testData, carOMSReqAndRespObj);
            //Create record
            CarbsOMReserveRequestSender.CarBSCreateRecordSend(testData, carOMSReqAndRespObj);

            //PreparePurchase which will cause error returned
            final PreparePurchaseRequest preparePurchaseRequest = carOMSReqAndRespObj.createPreparePurchaseRequest(testData);
            final PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(), preparePurchaseRequest);
            //Verify error in preparePurchase response
            verifyError(preparePurchaseResponse, "5002", "No PNR was returned for the reserve");
        }

    }

    private void verifyError(PreparePurchaseResponseType response,String errorID, String errorDesc ) throws DataAccessException {

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
