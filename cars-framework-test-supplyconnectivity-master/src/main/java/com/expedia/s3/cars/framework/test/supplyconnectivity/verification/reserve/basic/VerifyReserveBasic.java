package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.basic;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
//import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;

import java.util.Arrays;
import java.util.Optional;

//import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;

/**
 * Created by jiyu on 8/29/16.
 */
public class VerifyReserveBasic implements IReserveVerification
{
    private static final String MESSAGR_NO_RESERVE_RESULT_IN_REQUEST = "No valid car product in Reserve request.";
    private static final String MESSAGR_NO_RESERVE_RESULT_IN_RESPONSE = "No valid car product in Reserve Result response.";

    private static final String MESSAGE_SUCCESS = "Success";
    //private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";
    private static final String STATE_CODE_BOOKING_SUCCESS = "Booked";
    private static final String STATE_CODE_RESERVED = "Reserved";


    /*private String verifyBasicCarCateloguCompare(CarSupplyConnectivityReserveRequestType request, CarSupplyConnectivityReserveResponseType response)
    {
        final CarProductType carInRequest = request.getCarProduct();
        final CarProductType carInResponse = response.getCarReservation().getCarProduct();

        return VerificationHelper.verifyBasicCarCateloguCompare(
                carInRequest.getCarInventoryKey().getCarCatalogKey(),
                carInResponse.getCarInventoryKey().getCarCatalogKey());
    }*/

    private String verifyBasic(CarSupplyConnectivityReserveResponseType response)
    {
        if (!response.getCarReservation().getBookingStateCode().equals(STATE_CODE_BOOKING_SUCCESS)
                && !response.getCarReservation().getBookingStateCode().equals(STATE_CODE_RESERVED)) {
            return MESSAGR_NO_RESERVE_RESULT_IN_RESPONSE;
        }

        return null;
       // return verifyBasicCarCateloguCompare(request, response);

    }

    private String verifyExtra(CarSupplyConnectivityReserveResponseType response)
    {
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityReserveResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return MESSAGR_NO_RESERVE_RESULT_IN_REQUEST;
        }

        //Meichun: no need to verify error, if car is returned and book succeed then we should be fine, sometimes there is error with car booked - like eVoucher failure
        /*if (null != response.getErrorCollection()) {
            return VerificationHelper.verifyExtra(getXmlFieldValue(response.getErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }*/

        return null;
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

        errorMessage = verifyExtra(input.getResponse());
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
