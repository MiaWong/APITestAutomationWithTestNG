package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by jiyu on 8/29/16.
 */
public class VerifyReservedSpecialEquipment implements IReserveVerification
{
    private static final String MESSAGR_NO_RESERVE_RESULT_IN_RESPONSE = "No valid car product in Reserve Result response.";

    private static final String MESSAGE_SUCCESS = "Success : Special Equipment is booked!";
    private static final String STATE_CODE_BOOKING_SUCCESS = "Booked";

    private static final String MESSAGE_NO_SPECIALEQUIPMENT_IN_RESERVE_REQUEST = "No Special Equipment in Reserve Request.";
    private static final String MESSAGE_NO_SPECIALEQUIPMENT_IN_RESERVE_RESPONSE = "No Special Equipment in Reserve Response.";
    private static final String MESSAGE_UNMATCHED_SPECIALEQUIPMENT_IN_RESERVE_RESPONSE = "Not matched Special Equipment in Reserve Response.";

    private String verifySpecialEquipmentCompare(CarSupplyConnectivityReserveRequestType request, CarSupplyConnectivityReserveResponseType response)
    {
        if (!Optional.ofNullable(request)
                .map(CarSupplyConnectivityReserveRequestType::getCarSpecialEquipmentList)
                .map(CarSpecialEquipmentListType::getCarSpecialEquipment)
                .isPresent()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_RESERVE_REQUEST;
        }
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityReserveResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .map(CarProductType::getCarVehicleOptionList)
                .map(CarVehicleOptionListType::getCarVehicleOption)
                .isPresent()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_RESERVE_RESPONSE;
        }

        final HashMap<String, CarVehicleOptionType> sepListInResponse = new HashMap<>();
        for (final CarVehicleOptionType sepRS : response.getCarReservation().getCarProduct().getCarVehicleOptionList().getCarVehicleOption()) {
            sepListInResponse.put(sepRS.getCarSpecialEquipmentCode(), sepRS);
        }
    /*
        final HashMap<String, CarSpecialEquipmentType> sepListInResponse = new HashMap<>();
        for (final CarSpecialEquipmentType sepRS : response.getCarReservation().getCarSpecialEquipmentList().getCarSpecialEquipment())
        {
            sepListInResponse.put(sepRS.getCarSpecialEquipmentCode(), sepRS);
        }
    */
        for (final CarSpecialEquipmentType sepRQ : request.getCarSpecialEquipmentList().getCarSpecialEquipment())
        {
            if (!sepListInResponse.containsKey(sepRQ.getCarSpecialEquipmentCode())) {
                return MESSAGE_UNMATCHED_SPECIALEQUIPMENT_IN_RESERVE_RESPONSE;
            }
        }
        return null;
    }

    private String verifyBasic(CarSupplyConnectivityReserveResponseType response)
    {
        if (!response.getCarReservation().getBookingStateCode().equals(STATE_CODE_BOOKING_SUCCESS)) {
            return MESSAGR_NO_RESERVE_RESULT_IN_RESPONSE;
        }

        return null;
    }

    private String verifyExtra(CarSupplyConnectivityReserveRequestType request,
                               CarSupplyConnectivityReserveResponseType response)
    {
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityReserveResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .map(CarProductType::getCarVehicleOptionList)
                .map(CarVehicleOptionListType::getCarVehicleOption)
                .isPresent()) {
            return MESSAGE_NO_SPECIALEQUIPMENT_IN_RESERVE_RESPONSE;
        }

        return verifySpecialEquipmentCompare(request, response);
    }

    @SuppressWarnings("CPD-START")
    @Override
    public IVerification.VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        String errorMessage = "";

        errorMessage = verifyBasic(input.getResponse());
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
    public boolean shouldVerify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        return (Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityReserveRequestType::getCarProduct)
                .isPresent() &&
                Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityReserveResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent());
    }
}
