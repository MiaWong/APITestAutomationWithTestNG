package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve.verification.VerifyEnhancedBookingLoggingInRetrieve;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 9/20/2018.
 */
public class EnhanceBookLogRetrieve extends SuiteCommon {
    Logger logger = Logger.getLogger(getClass());

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs428111EnhanceBookLogRetrieveWorldspanBillingNumber() throws Exception
    {
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "428111", PojoXmlUtil.getRandomGuid());
        testData.setBillingNumber("111111");

        testEnhancedBookingLoggingInRetrieve(testData, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs460318EnhanceBookLogRetrieveTwice() throws Exception
    {
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "460318", PojoXmlUtil.getRandomGuid());

        testEnhancedBookingLoggingInRetrieve(testData, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1080132OMSRetrieveTitaniumStandaloneUKOffAirportSendRetrieveTwice() throws Exception {
        final TestScenario testScenario = CommonScenarios.TisSCS_GBR_Standalone_Roundtrip_OffAirport_LHR.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "1080132", PojoXmlUtil.getRandomGuid());
        testEnhancedBookingLoggingInRetrieve(testData, true);
    }

    private static void testEnhancedBookingLoggingInRetrieve(TestData testData, boolean retrieveTwice) throws Exception
    {
        //booking with shop
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, false);
        //Send retrieve again before cancel for retrieveTwice scenario
        if(retrieveTwice){
            CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, false);
        }
        RetrieveVerificationInput retrieveVerificationInput = new RetrieveVerificationInput(carbsOMRetrieveReqAndRespGenerator.getRetrieveRequestType(),
                carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType());
        retrieveVerificationInput.setCarProductType(carbsOMReserveReqAndRespGenerator.getSelectCarProduct());

        //Verify EBL in retrieve
        StringBuilder errorMsg = VerifyEnhancedBookingLoggingInRetrieve.verifyEnhancedBookingLoggingInRetrieve(retrieveVerificationInput, testData.getScenarios().getSupplierCurrencyCode(), false);


        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        if(!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }

        //Send retrieve again
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, true);
        retrieveVerificationInput = new RetrieveVerificationInput(carbsOMRetrieveReqAndRespGenerator.getRetrieveRequestType(),
                carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType());
        retrieveVerificationInput.setCarProductType(carbsOMReserveReqAndRespGenerator.getSelectCarProduct());

        //Verify EBL in retrieve
        errorMsg = VerifyEnhancedBookingLoggingInRetrieve.verifyEnhancedBookingLoggingInRetrieve(retrieveVerificationInput, testData.getScenarios().getSupplierCurrencyCode(), true);
        if(!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }

    }
}
