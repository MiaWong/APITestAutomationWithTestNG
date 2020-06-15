package com.expedia.s3.cars.ecommerce.carbs.service.tests.cancel;

import com.expedia.om.supply.messages.v1.StatusCodeCategoryType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonTypeGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.cancel.verification.VerifyOMSCancel;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.OmCancelVerifier;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.GetChangeProcessRequest;
import expedia.om.supply.messages.defn.v1.GetChangeProcessResponseType;
import expedia.om.supply.messages.defn.v1.PrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.PrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeResponseType;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Created by fehu on 9/20/2018.
 */
@SuppressWarnings("PMD")
public class OMSCancel extends SuiteCommon {

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs829794tfs889558tfs889563tfs895145OMSCancelAmadeusCar() throws Exception
    {
        testOMSCancel(CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "829794");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs889556tfs889561tfs895143OMSCancelWorldspanGDSPCar() throws Exception
    {
        testOMSCancel(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "889556");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs829792GetChangeProcessMNCar() throws Exception
    {
        testOMSCancel(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "829792");
    }

    public void testOMSCancel(TestScenario scenarioName, String tuid) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenarioName, tuid, guid);

        //reserve with shop messages
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //create CarbsOMCancelReqAndRespGenerator
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);

        //Build and send GetChangeProcess request to CarBS
        final StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(CarbsOMCancelRequestSender.carBSGetChangeProcessSend(testData.getScenarios(), testData.getGuid(), httpClient,
                omsCancelReqAndRespObj));

        //Verify TotalAmountWithTax for GetChangeProcess
        errorMsg.append(VerifyOMSCancel.verifyTotalAmountWithTaxForGetChangeProcess(omsCancelReqAndRespObj.getGetChangeProcessResponseType(), testData.getScenarios().getBusinessModel()));

        //Send PrepareChange request
        final String errorMsg_prepareChange = CarbsOMCancelRequestSender.carBSPrepareChangeSend(testData.getScenarios(), testData.getGuid(),
                httpClient, omsCancelReqAndRespObj);
        if (errorMsg_prepareChange.length() > 0) {
            errorMsg .append(errorMsg_prepareChange);
        }

        //Verify cancel logging for PrepareChange
        final String bookingItemID = BookingVerificationUtils.getBookingItemID(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
        errorMsg.append(VerifyOMSCancel.verifyBookingAmountCancelLog(bookingItemID, true, false));
        errorMsg.append(VerifyOMSCancel.verifyBookingItemCancelLog(bookingItemID, true, false));

        //Verify BookingAmount list in PrepareChange response
        errorMsg.append(VerifyOMSCancel.verifyBookingAmounInPrepareChangeRsp(omsCancelReqAndRespObj.getPrepareChangeResponseType(), bookingItemID));

        //Verify GDS request is not sent for PrepareChange
        Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        errorMsg.append(VerifyOMSCancel.verifyGDSCancelRequestSent(spooferDoc, true));

        //Rollback change
        errorMsg.append(CarbsOMCancelRequestSender.carBSRollbackPrepareChangeSend(testData.getScenarios(), guid, httpClient, omsCancelReqAndRespObj));

        //Verify cancel logging for Rollback change
        errorMsg.append(VerifyOMSCancel.verifyBookingAmountCancelLog(bookingItemID, false, true));
        errorMsg.append(VerifyOMSCancel.verifyBookingItemCancelLog(bookingItemID, false, true));

        //Prepare change again and commit change
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient,"CommitPrepareChange");

        //Verify booking looging for commit change
        errorMsg.append(VerifyOMSCancel.verifyBookingAmountCancelLog(bookingItemID, false, false));
        errorMsg.append(VerifyOMSCancel.verifyBookingItemCancelLog(bookingItemID, false, false));

        //Verify GDS request is sent for commit change
        spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        errorMsg.append(VerifyOMSCancel.verifyGDSCancelRequestSent(spooferDoc, false));

        //fail test case if error exist
        CommonUtil.notNullErrorMsg(String.valueOf(errorMsg));
    }

    //Verify that error received in PrepareChange response when sending a PrepareChange request with invalid Changed Target Data sent from CarBS  on UK site
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs189319PrepareChangeErrorHandling() throws Exception
    {
        testOMSCancelErrorHandling(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "189319", "PrepareChangeWithInvalidTargetData", StatusCodeCategoryType.INVALID_INPUT);
    }
    //CommitPrepareChange with invalid Context_ID
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs198456PrepareChangeErrorHandling() throws Exception
    {
        testOMSCancelErrorHandling(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "198456", "CommitPrepareChangeWithInvalidContextID", StatusCodeCategoryType.INVALID_INPUT);
    }

    //RollbackPrepareChange request without Context_ID
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs198462PrepareChangeErrorHandling() throws Exception
    {
        testOMSCancelErrorHandling(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "198462", "RollbackPrepareWithoutContextID", StatusCodeCategoryType.INVALID_INPUT);
    }
    //CarBS Cancel for Amadues Car - Verify cancel rollback when preparechange failed
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs269651PrepareChangeErrorHandling() throws Exception
    {
        testOMSCancelErrorHandling(CommonScenarios.Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(), "269651", "PrepareChangeFailed", StatusCodeCategoryType.INVALID_INPUT);
    }

    public void testOMSCancelErrorHandling(TestScenario scenario, String tuid,  String errorType,
                                           StatusCodeCategoryType expectedStatusCodeCategory) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);

        //reserve with shop messages
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //create CarbsOMCancelReqAndRespGenerator
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);

        //Build and send GetChangeProcess request to CarBS
        switch (errorType)
        {

            case "PrepareChangeWithInvalidTargetData":
            {
                //1.Send GetChangeProcess request
                String errorMsg = CarbsOMCancelRequestSender.carBSGetChangeProcessSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                //Send invalid PrepareChange request, get error message returned in PrepareChange response
                final PrepareChangeRequest prepareChangeRequest = omsCancelReqAndRespObj.createPrepareChangeRequest();
                prepareChangeRequest.getChangeTargetData().setCarOfferData(null);
                //Get PrepareChange response
                final PrepareChangeResponseType prepareChangeResponse = CarbsOMServiceSender.sendPrepareChangeResponse(guid, httpClient, prepareChangeRequest);
                errorMsg = VerifyOMSCancel.isExpectedErrorExistsInPrepareChangeMessageVerifier(prepareChangeResponse, expectedStatusCodeCategory);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                break;
            }
            case "CommitPrepareChangeWithInvalidContextID":
            {
                //1.Send GetChangeProcess request
               String errorMsg = CarbsOMCancelRequestSender.carBSGetChangeProcessSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                //2.Send PrepareChange request
                final String errorMsg_prepareChange = CarbsOMCancelRequestSender.carBSPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (StringUtil.isNotBlank(errorMsg_prepareChange))
                {
                    //Rollback if PrepareChange failed
                    String rollbackPrepareChangeErrorMessage = CarbsOMCancelRequestSender.carBSRollbackPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                    if (StringUtil.isBlank(rollbackPrepareChangeErrorMessage))
                    {
                        Assert.fail("Can not complete unhappy scenario due to PrepareChange failed, and had already rollback!");
                    }
                    else
                    {
                        Assert.fail("Can not complete unhappy scenario due to PrepareChange failed, and send RollbackPrepareChange also failed!");
                    }
                }
                //Send invalid CommitPrepareChange request

                final CommitPrepareChangeRequest commitPrepareChangeRequest = omsCancelReqAndRespObj.createCommitPrepareChangeRequest();
                commitPrepareChangeRequest.setChangeContextID("0");
                //Get the commitPrepareChange response
                final CommitPrepareChangeResponseType commitPrepareChangeResponse = CarbsOMServiceSender.sendCommitPrepareChangeResponse(guid, httpClient, commitPrepareChangeRequest);
                errorMsg = VerifyOMSCancel.isExpectedErrorExistsInCommitPrepareChangeMessageVerifier(commitPrepareChangeResponse, expectedStatusCodeCategory);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                break;
            }

            case "RollbackPrepareWithoutContextID":
            {
                //1.Send GetChangeProcess request
                String errorMsg = CarbsOMCancelRequestSender.carBSGetChangeProcessSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                //Send invalid RollbackPrepareChange request
                final RollbackPrepareChangeRequest rollbackPrepareChangeRequest = omsCancelReqAndRespObj.createRollbackPrepareChangeRequest();
                rollbackPrepareChangeRequest.setChangeContextID(null);
                //Get the RollbackPrepareChange response
                final RollbackPrepareChangeResponseType rollbackPrepareChangeResponse = CarbsOMServiceSender.sendRollbackPrepareChangeResponse(guid, httpClient, rollbackPrepareChangeRequest);
                errorMsg = VerifyOMSCancel.isExpectedErrorExistsInRollbackPrepareChangeMessageVerifier(rollbackPrepareChangeResponse, expectedStatusCodeCategory);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                break;
            }

            case "PrepareChangeFailed":
            {
                //1.Send GetChangeProcess request
                String errorMsg = CarbsOMCancelRequestSender.carBSGetChangeProcessSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                //Send invalid PrepareChange request, get error message returned in PrepareChange response
                final PrepareChangeRequest prepareChangeRequest = omsCancelReqAndRespObj.createPrepareChangeRequest();
                prepareChangeRequest.getChangeTargetData().setCarOfferData(null);
                //Get PrepareChange response
                final PrepareChangeResponseType prepareChangeResponse = CarbsOMServiceSender.sendPrepareChangeResponse(guid, httpClient, prepareChangeRequest);
                errorMsg = VerifyOMSCancel.isExpectedErrorExistsInPrepareChangeMessageVerifier(prepareChangeResponse, expectedStatusCodeCategory);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                errorMsg = CarbsOMCancelRequestSender.carBSRollbackPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (StringUtil.isNotBlank(errorMsg))
                {
                    Assert.fail(errorMsg);
                }
                break;
            }
        }

        //Prepare change again and commit change
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient,"CommitPrepareChange");

    }

    //RollbackPrepareChange with TUID in  PointOfSaleCustomerIdentifier
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1075865OMSAgentIndicatorSuppor() throws Exception
    {
        testOMSAgentIndicatorSupport(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1075865", "");
    }

    //RollbackPrepareChange with  different TUID in PointOfSaleLogonUserIdentifie & PointOfSaleCustomerIdentifier
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1075869OMSAgentIndicatorSuppor() throws Exception
    {
        testOMSAgentIndicatorSupport(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1075869", "107582");
    }

    public void testOMSAgentIndicatorSupport(TestScenario scenario, String tuid, String logUserID) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);

        //reserve with shop messages
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        String bookingItemID = BookingVerificationUtils.getBookingItemID(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
        //create CarbsOMCancelReqAndRespGenerator
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);

        if(StringUtil.isBlank(logUserID))
        {
            String errorMsg = CarbsOMCancelRequestSender.carBSGetChangeProcessSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
            if (StringUtil.isNotBlank(errorMsg))
            {
                Assert.fail(errorMsg);
            }
            errorMsg = CarbsOMCancelRequestSender.carBSPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);

            if (StringUtil.isNotBlank(errorMsg))
            {
                Assert.fail(errorMsg);
            }
            VerifyOMSCancel.verifyBookingAmountTUID(bookingItemID
            , omsCancelReqAndRespObj.getPrepareChangeRequestType().getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getUserID());
        }
        else
        {
            final GetChangeProcessRequest getChangeProcessRequest = omsCancelReqAndRespObj.createGetChangeProcessRequest();
            getChangeProcessRequest.setPointOfSaleLogonUserIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(Long.parseLong(logUserID)));
            //Get getChangeProcess response
            final GetChangeProcessResponseType getChangeProcessResponse = CarbsOMServiceSender.sendGetChangeProcessResponse(guid, httpClient, getChangeProcessRequest);
            if(null != getChangeProcessRequest.getChangeTargetData() &&
                    null != getChangeProcessRequest.getChangeTargetData().getCarOfferData() &&
                    !getChangeProcessRequest.getChangeTargetData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean())
            {
                OmCancelVerifier.isGetChangeProcessWorksVerifier(guid, scenario, getChangeProcessRequest, getChangeProcessResponse);
            }
            omsCancelReqAndRespObj.setGetChangeProcessResponseType(getChangeProcessResponse);


            final PrepareChangeRequest prepareChangeRequest = omsCancelReqAndRespObj.createPrepareChangeRequest();
            prepareChangeRequest.setPointOfSaleLogonUserIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(Long.parseLong(logUserID)));

            //Get PrepareChange response
            final PrepareChangeResponseType prepareChangeResponse = CarbsOMServiceSender.sendPrepareChangeResponse(guid, httpClient, prepareChangeRequest);
            if(null != prepareChangeRequest.getChangeTargetData() &&
                    null != prepareChangeRequest.getChangeTargetData().getCarOfferData() &&
                    !prepareChangeRequest.getChangeTargetData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean())
            {
                OmCancelVerifier.isPrepareChangeWorksVerifier(guid, scenario, prepareChangeRequest, prepareChangeResponse);
            }
            omsCancelReqAndRespObj.setPrepareChangeResponseType(prepareChangeResponse);

            VerifyOMSCancel.verifyBookingAmountTUID(bookingItemID
                    , Long.parseLong(logUserID));

        }

        String errorMsg = CarbsOMCancelRequestSender.carBSRollbackPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
        if (StringUtil.isNotBlank(errorMsg))
        {
            Assert.fail(errorMsg);
        }
        VerifyOMSCancel.verifyBookingItemTUID(bookingItemID, CarCommonEnumManager.OMCancelMessageType.RollbackPrepareChange);

    }

}
