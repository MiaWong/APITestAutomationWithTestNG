package com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.OmCancelVerifier;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import expedia.om.supply.messages.defn.v1.*;
import org.eclipse.jetty.client.HttpClient;

import java.io.IOException;

/**
 * Created by fehu on 8/30/2016.
 */
@SuppressWarnings("PMD")
public class CarbsOMCancelRequestSender {

    private CarbsOMCancelRequestSender() {
    }

    public static CarbsOMCancelReqAndRespGenerator omsCancelSend(TestScenario scenario, CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj, String guid, HttpClient httpClient, String messageType) throws IOException, DataAccessException {
        StringBuffer errorMsg = new StringBuffer();

        //  1.Build and send GetChangeProcess request to CarBS .(Get ChangeContextID)
        // -----------------------------------------------------------
        switch (messageType) {
            case "GetChangeProcess":
            case "PrepareChange":
            case "CommitPrepareChange":
            case "RollbackPrepareChange": {
                errorMsg.append(carBSGetChangeProcessSend(scenario, guid, httpClient, omsCancelReqAndRespObj));
                CommonUtil.notNullErrorMsg(String.valueOf(errorMsg));
                break;
            }
        }
        // -----------------------------------------------------------
        //  2.Build and send PrepareChange request to CarBS .
        // -----------------------------------------------------------
        switch (messageType) {
            case "PrepareChange":
            case "CommitPrepareChange":
            case "RollbackPrepareChange": {
              final String errorMsg_prepareChange = carBSPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (errorMsg_prepareChange.length() > 0) {
                    messageType = CarCommonEnumManager.OMCancelMessageType.PrepareChangeError.toString();
                    errorMsg .append(errorMsg_prepareChange);
                }
                break;
            }
        }
        // -----------------------------------------------------------
        //  3.Build and send CommitPrepareChange request to CarBS .
        // -----------------------------------------------------------
        switch (messageType) {
            case "CommitPrepareChange": {
                String errorMsg_commitPrepareChange = carBSCommitPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj);
                if (errorMsg_commitPrepareChange.length() > 0) {
                    messageType = CarCommonEnumManager.OMCancelMessageType.CommitPrepareChangeError.toString();
                    errorMsg.append(errorMsg_commitPrepareChange);
                }
                break;
            }
        }

        // -----------------------------------------------------------
        //  4.Build and send RollbackPrepareChange request to CarBS .
        // -----------------------------------------------------------
        switch (messageType) {
            case "GetChangeProcess":
            case "PrepareChange":
            case "PrepareChangeError":
            case "CommitPrepareChangeError":
            case "RollbackPrepareChange": {
                errorMsg.append(carBSRollbackPrepareChangeSend(scenario, guid, httpClient, omsCancelReqAndRespObj));
                break;
            }
        }


        CommonUtil.notNullErrorMsg(String.valueOf(errorMsg));
        return omsCancelReqAndRespObj;
    }

    public static String carBSGetChangeProcessSend(TestScenario scenario, String guid, HttpClient httpClient,
                                                   CarbsOMCancelReqAndRespGenerator carbsOMReserveReqGenerater) throws IOException, DataAccessException {
        String errorMsg = "";
        final GetChangeProcessRequest getChangeProcessRequest = carbsOMReserveReqGenerater.createGetChangeProcessRequest();
        //Get getChangeProcess response
        final GetChangeProcessResponseType getChangeProcessResponse = CarbsOMServiceSender.sendGetChangeProcessResponse(guid, httpClient, getChangeProcessRequest);
        if(null != getChangeProcessRequest.getChangeTargetData() &&
                null != getChangeProcessRequest.getChangeTargetData().getCarOfferData() &&
                !getChangeProcessRequest.getChangeTargetData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean())
        {
            OmCancelVerifier.isGetChangeProcessWorksVerifier(guid, scenario, getChangeProcessRequest, getChangeProcessResponse);
        }
        carbsOMReserveReqGenerater.setGetChangeProcessResponseType(getChangeProcessResponse);
        //errorMsg = CarBSOMCancelVerifierForNewNamespace.isSuccessfulGetChangeMessageVerifier(getChangeProcessResponse);

        return errorMsg;
    }

    public static String carBSPrepareChangeSend(TestScenario scenario, String guid, HttpClient httpClient, CarbsOMCancelReqAndRespGenerator carbsOMCancelReqAndRespGenerator) throws IOException, DataAccessException {
        String errorMsg = "";


        final PrepareChangeRequest prepareChangeRequest = carbsOMCancelReqAndRespGenerator.createPrepareChangeRequest();
        //Get PrepareChange response
        final PrepareChangeResponseType prepareChangeResponse = CarbsOMServiceSender.sendPrepareChangeResponse(guid, httpClient, prepareChangeRequest);
        if(null != prepareChangeRequest.getChangeTargetData() &&
                null != prepareChangeRequest.getChangeTargetData().getCarOfferData() &&
                !prepareChangeRequest.getChangeTargetData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean())
        {
            OmCancelVerifier.isPrepareChangeWorksVerifier(guid, scenario, prepareChangeRequest, prepareChangeResponse);
        }
        carbsOMCancelReqAndRespGenerator.setPrepareChangeResponseType(prepareChangeResponse);
        //Verify the PrepareChange response, get the error message then return it

        //   errorMsg = CarBSOMCancelVerifierForNewNamespace.isSuccessfulPrepareChangeMessageVerifier(prepareChangeResponse);

        return errorMsg;
    }

    public static String carBSCommitPrepareChangeSend(TestScenario scenario, String guid, HttpClient httpClient, CarbsOMCancelReqAndRespGenerator carbsOMCancelReqAndRespGenerator) throws IOException, DataAccessException {
        String errorMsg = "";

        final CommitPrepareChangeRequest commitPrepareChangeRequest = carbsOMCancelReqAndRespGenerator.createCommitPrepareChangeRequest();
        //Get the commitPrepareChange response
        final CommitPrepareChangeResponseType commitPrepareChangeResponse = CarbsOMServiceSender.sendCommitPrepareChangeResponse(guid, httpClient, commitPrepareChangeRequest);
        if(null != carbsOMCancelReqAndRespGenerator.getPrepareChangeRequestType().getChangeTargetData() &&
                null != carbsOMCancelReqAndRespGenerator.getPrepareChangeRequestType().getChangeTargetData().getCarOfferData() &&
                !carbsOMCancelReqAndRespGenerator.getPrepareChangeRequestType().getChangeTargetData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean())
        {
            OmCancelVerifier.isCommitPrepareChangeWorksVerifier(guid, scenario, commitPrepareChangeRequest, commitPrepareChangeResponse);
        }
        carbsOMCancelReqAndRespGenerator.setCommitPrepareChangeResponseType(commitPrepareChangeResponse);

        // errorMsg = CarBSOMCancelVerifierForNewNamespace.isSuccessfulCommitPrepareChangeMessageVerifier(commitPrepareChangeResponse);
        return errorMsg;
    }

    public static String carBSRollbackPrepareChangeSend(TestScenario scenario, String guid, HttpClient httpClient, CarbsOMCancelReqAndRespGenerator carbsOMCancelReqAndRespGenerator) throws IOException, DataAccessException {
        String errorMsg = "";
        //Build RollbackPrepareChange request for happy case scenario
        final RollbackPrepareChangeRequest rollbackPrepareChangeRequest = carbsOMCancelReqAndRespGenerator.createRollbackPrepareChangeRequest();
        //Get the RollbackPrepareChange response
        final RollbackPrepareChangeResponseType rollbackPrepareChangeResponse = CarbsOMServiceSender.sendRollbackPrepareChangeResponse(guid, httpClient, rollbackPrepareChangeRequest);
        OmCancelVerifier.isRollbackPrepareChangeWorksVerifier(guid, scenario, rollbackPrepareChangeRequest, rollbackPrepareChangeResponse);
        carbsOMCancelReqAndRespGenerator.setRollbackPrepareChangeResponseType(rollbackPrepareChangeResponse);
        // errorMsg = CarBSOMCancelVerifierForNewNamespace.isSuccessfulRollbackPrepareChangeMessageVerifier(rollbackPrepareChangeResponse);


        return errorMsg;
    }

}
