package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationListType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jiyu on 8/31/16.
 */
public class VerifyReservedSpecialEquipment implements IGetReservationVerification
{
    private static final String MESSAGR_NO_GETRESERVATION_RESULT_IN_REQUEST = "No valid car product in GetReservation request.";
    private static final String MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE = "No valid car product in GetReservation Result response.";

    private static final String MESSAGE_SUCCESS = "Success : Special Equipment is booked!";
    private static final String STATE_CODE_BOOKING_SUCCESS = "Booked";

    private static final String MESSAGE_NO_SPECIALEQUIPMENT_IN_GETRESERVATION_REQUEST = "No Special Equipment in GetReservation Request.";
    private static final String MESSAGE_NO_SPECIALEQUIPMENT_IN_GETRESERVATION_RESPONSE = "No Special Equipment in GetReservation Response.";
    private static final String MESSAGE_UNMATCHED_SPECIALEQUIPMENT_IN_GETRESERVATION_RESPONSE = "Not matched Special Equipment in GetReservation Response.";

    private String verifySpecialEquipmentCompare(CarSupplyConnectivityGetReservationRequestType request, CarSupplyConnectivityGetReservationResponseType response)
    {
        if (!Optional.ofNullable(request)
                .map(CarSupplyConnectivityGetReservationRequestType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_GETRESERVATION_REQUEST;
        }

        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_GETRESERVATION_RESPONSE;
        }

        final CarSpecialEquipmentListType sepInRequest = request.getCarReservationList().getCarReservation().get(0).getCarSpecialEquipmentList();
        final CarSpecialEquipmentListType sepInResponse = response.getCarReservationList().getCarReservation().get(0).getCarSpecialEquipmentList();
        if (sepInRequest.getCarSpecialEquipment().size() != sepInResponse.getCarSpecialEquipment().size()) {
            return MESSAGE_UNMATCHED_SPECIALEQUIPMENT_IN_GETRESERVATION_RESPONSE;
        }

        return null;
    }

    private String verifyBasicRequest(CarSupplyConnectivityGetReservationRequestType request)
    {
        return Optional.ofNullable(request)
                .map(CarSupplyConnectivityGetReservationRequestType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent()
                ? null : MESSAGR_NO_GETRESERVATION_RESULT_IN_REQUEST;
    }

    private String verifyBasicResponse(CarSupplyConnectivityGetReservationResponseType response)
    {
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE;
        }

        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getBookingStateCode)
                .isPresent() ||
                !response.getCarReservationList().getCarReservation().get(0).getBookingStateCode().equals(STATE_CODE_BOOKING_SUCCESS)) {
            return MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE;
        }

        return null;
    }

    private String verifyBasic(CarSupplyConnectivityGetReservationRequestType request, CarSupplyConnectivityGetReservationResponseType response)
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

    private String verifyExtra(CarSupplyConnectivityGetReservationRequestType request,
                               CarSupplyConnectivityGetReservationResponseType response)
    {
        if (!Optional.ofNullable(request)
                .map(CarSupplyConnectivityGetReservationRequestType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent())
        {
            return MESSAGR_NO_GETRESERVATION_RESULT_IN_REQUEST;
        }

        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent())
        {
            return MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE;
        }

        if (Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent()) {
            return verifySpecialEquipmentCompare(request, response);
        }
        else {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_GETRESERVATION_RESPONSE;
        }
    }

    @SuppressWarnings("CPD-START")
    @Override
    public IVerification.VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
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
    public boolean shouldVerify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
    {
        if (!Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetReservationRequestType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return false;
        }

        if (!Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return false;
        }

        if (Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getBookingStateCode)
                .isPresent()) {
            return input.getResponse().getCarReservationList().getCarReservation().get(0).getBookingStateCode().equals(STATE_CODE_BOOKING_SUCCESS);
        }
        else {
            return false;
        }
    }

}
