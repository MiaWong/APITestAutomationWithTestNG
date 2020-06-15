package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jiyu on 8/31/16.
 */
public class VerifyCancelledSpecialEquipment implements ICancelVerification
{
    private static final String MESSAGR_NO_CANCEL_RESULT_IN_REQUEST = "No valid car product in Cancel request.";
    private static final String MESSAGR_NO_CANCEL_RESULT_IN_RESPONSE = "No valid car product in Cancel Result response.";

    private static final String MESSAGE_SUCCESS = "Success : Special Equipment is booked!";
    private static final String STATE_CODE_BOOKING_SUCCESS = "Booked";
    private static final String STATE_CODE_CANCEL_SUCCESS = "Cancelled";

    private static final String MESSAGR_NO_PRODUCT_RESULT_IN_CANCEL_REQUEST = "No valid car product in Cancel request.";
    private static final String MESSAGR_NO_PRODUCT_RESULT_IN_CANCEL_RESPONSE = "No valid car product in Cancel Result response.";

    private static final String MESSAGE_NO_SPECIALEQUIPMENT_IN_CANCEL_REQUEST = "No Special Equipment in Cancel Request.";
    private static final String MESSAGE_NO_SPECIALEQUIPMENT_IN_CANCEL_RESPONSE = "No Special Equipment in Cancel Response.";
    private static final String MESSAGE_UNMATCHED_SPECIALEQUIPMENT_IN_CANCEL_RESPONSE = "Not matched Special Equipment in Cancel Response.";

    private String verifySpecialEquipmentCompare(CarSupplyConnectivityCancelRequestType request, CarSupplyConnectivityCancelResponseType response)
    {

        if (!Optional.ofNullable(request)
                .map(CarSupplyConnectivityCancelRequestType::getCarReservation)
                .map(CarReservationType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent() ||
                request.getCarReservation().getCarSpecialEquipmentList().getCarSpecialEquipment().isEmpty()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_CANCEL_REQUEST;
        }

        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent() ||
            response.getCarReservation().getCarSpecialEquipmentList().getCarSpecialEquipment().isEmpty()    ) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_CANCEL_RESPONSE;
        }

        final CarSpecialEquipmentListType sepInRequest = request.getCarReservation().getCarSpecialEquipmentList();
        final CarSpecialEquipmentListType sepInResponse = response.getCarReservation().getCarSpecialEquipmentList();
        //  now it is buggy in spoofer templates, change "!=" to ">" for timebeing
        if (sepInRequest.getCarSpecialEquipment().size() > sepInResponse.getCarSpecialEquipment().size()) {
            return MESSAGE_UNMATCHED_SPECIALEQUIPMENT_IN_CANCEL_RESPONSE;
        }

        return null;
    }

    private String verifyBasicRequest(CarSupplyConnectivityCancelRequestType request)
    {
        if (!Optional.ofNullable(request)
                        .map(CarSupplyConnectivityCancelRequestType::getCarReservation)
                        .map(CarReservationType::getCarProduct)
                        .isPresent() ||
                !Optional.ofNullable(request)
                        .map(CarSupplyConnectivityCancelRequestType::getCarReservation)
                        .map(CarReservationType::getBookingStateCode)
                        .isPresent() ||
                !request.getCarReservation().getBookingStateCode().equals(STATE_CODE_BOOKING_SUCCESS)) {
            return MESSAGR_NO_CANCEL_RESULT_IN_REQUEST;
        }

        return null;
    }

    private String verifyBasicResponse(CarSupplyConnectivityCancelResponseType response)
    {
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent() ||
            !Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getBookingStateCode)
                .isPresent() ||
            !response.getCarReservation().getBookingStateCode().equals(STATE_CODE_CANCEL_SUCCESS)) {
            return MESSAGR_NO_CANCEL_RESULT_IN_RESPONSE;
        }

        return null;
    }

    private String verifyBasic(CarSupplyConnectivityCancelRequestType request, CarSupplyConnectivityCancelResponseType response)
    {
        String errorMsg = "";

        errorMsg = verifyBasicRequest(request);
        if (null != errorMsg) {
            return errorMsg;
        }

        errorMsg = verifyBasicResponse(response);
        if (null != errorMsg) {
            return errorMsg;
        }

        return null;
    }

    private String verifyExtra(CarSupplyConnectivityCancelRequestType request,
                               CarSupplyConnectivityCancelResponseType response)
    {
        if (!Optional.ofNullable(request)
                .map(CarSupplyConnectivityCancelRequestType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return MESSAGR_NO_PRODUCT_RESULT_IN_CANCEL_REQUEST;
        }
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return MESSAGR_NO_PRODUCT_RESULT_IN_CANCEL_RESPONSE;
        }

        //  turn code off till getting correct PricedList from Spoofer reserve response
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_CANCEL_RESPONSE;
        }

        return verifySpecialEquipmentCompare(request, response);
    }

    @SuppressWarnings("CPD-START")
    @Override
    public IVerification.VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext)
    {
        String errorMessage = "";

        errorMessage = verifyBasic(input.getRequest(), input.getResponse());
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = verifyExtra(input.getRequest(), input.getResponse());
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

    @SuppressWarnings("CPD-END")
    @Override
    public boolean shouldVerify(CancelVerificationInput input, BasicVerificationContext verificationContext)
    {
        return (Optional.ofNullable(input.getRequest())
                        .map(CarSupplyConnectivityCancelRequestType::getCarReservation)
                        .map(CarReservationType::getBookingStateCode)
                        .isPresent() &&
                Optional.ofNullable(input.getResponse())
                        .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                        .map(CarReservationType::getBookingStateCode)
                        .isPresent());

    }
}
