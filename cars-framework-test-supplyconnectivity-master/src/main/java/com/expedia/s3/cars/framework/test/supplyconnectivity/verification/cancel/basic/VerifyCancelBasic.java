package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;

import java.util.Arrays;
import java.util.Optional;

//import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;


/**
 * Created by jiyu on 8/31/16.
 */
public class VerifyCancelBasic implements ICancelVerification
{
    private static final String MESSAGR_NO_CANCEL_RESULT_IN_REQUEST = "No valid car product or state code in Cancel request.";
    private static final String MESSAGR_NO_CANCEL_RESULT_IN_RESPONSE = "No valid car product or state code in Cancel Result response.";
    private static final String MESSAGE_INVALID_CANCEL_RESPONSE = "Cancel response is not valid";

    private static final String MESSAGE_SUCCESS = "Success in Cancellation";
    //private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";
    private static final String STATE_CODE_BOOKING_SUCCESS = "Booked";
    private static final String STATE_CODE_RESERVED = "Reserved";

    private static final String STATE_CODE_BOOKING_CANCEL_SUCCESS = "Cancelled";

    private String verifyBasicCarCatelogCompare(CarSupplyConnectivityCancelRequestType request, CarSupplyConnectivityCancelResponseType response)
    {
        final CarProductType carInRequest = request.getCarReservation().getCarProduct();
        final CarProductType carInResponse = response.getCarReservation().getCarProduct();

        return VerificationHelper.verifyBasicCarCateloguCompare(
                carInRequest.getCarInventoryKey().getCarCatalogKey(),
                carInResponse.getCarInventoryKey().getCarCatalogKey());
    }

    private String verifyBasicRequest(CarSupplyConnectivityCancelRequestType request)
    {
        if (Optional.ofNullable(request)
                .map(CarSupplyConnectivityCancelRequestType::getCarReservation)
                .map(CarReservationType::getBookingStateCode)
                .isPresent() &&
                (request.getCarReservation().getBookingStateCode().equals(STATE_CODE_BOOKING_SUCCESS)
                 || request.getCarReservation().getBookingStateCode().equals(STATE_CODE_RESERVED))) {
            return null;
        }
        else {
            return MESSAGR_NO_CANCEL_RESULT_IN_REQUEST;
        }

    }

    private String verifyBasicResponse(CarSupplyConnectivityCancelResponseType response)
    {
        if (Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getBookingStateCode)
                .isPresent() &&
            response.getCarReservation().getBookingStateCode().equals(STATE_CODE_BOOKING_CANCEL_SUCCESS)) {
            return null;
        }
        else
        {
            return MESSAGR_NO_CANCEL_RESULT_IN_RESPONSE;
        }

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

        return verifyBasicCarCatelogCompare(request, response);
    }

    private String verifyExtra(CarSupplyConnectivityCancelResponseType response)
    {
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .map(CarReservationType::getBookingStateCode)
                .isPresent()) {
            return MESSAGE_INVALID_CANCEL_RESPONSE;
        }

        //Meichun: if cancel is successful, then we should be good for BVT test
        /*if (null != response.getErrorCollection()) {
            return VerificationHelper.verifyExtra(getXmlFieldValue(response.getErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }*/

        return null;
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

        errorMessage = verifyExtra(input.getResponse());
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
