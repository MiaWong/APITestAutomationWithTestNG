package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.basic;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationListType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;

import java.util.Arrays;
import java.util.Optional;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;

/**
 * Created by jiyu on 8/30/16.
 */
public class VerifyGetReservationBasic implements IGetReservationVerification
{
    private static final String MESSAGR_NO_GETRESERVATION_RESULT_IN_REQUEST = "No valid car product in GetReservation request.";
    private static final String MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE = "No valid car product in GetReservation Result response.";
    private static final String MESSAGE_INVALID_GETRESERVATION_RESPONSE = "GetReservation response is not valid";

    private static final String MESSAGE_SUCCESS = "Success in GetReservation";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";

    private String verifyBasicCarCatelogCompare(CarSupplyConnectivityGetReservationRequestType request, CarSupplyConnectivityGetReservationResponseType response)
    {
        final CarProductType carInRequest = request.getCarReservationList().getCarReservation().get(0).getCarProduct();
        final CarProductType carInResponse = response.getCarReservationList().getCarReservation().get(0).getCarProduct();

        return VerificationHelper.verifyBasicCarCateloguCompare(
                carInRequest.getCarInventoryKey().getCarCatalogKey(),
                carInResponse.getCarInventoryKey().getCarCatalogKey());
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
        return Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent()
                ? null : MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE;
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

        return verifyBasicCarCatelogCompare(request, response);
    }

    private String verifyExtra(CarSupplyConnectivityGetReservationResponseType response)
    {
        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent()) {
            return MESSAGE_INVALID_GETRESERVATION_RESPONSE;
        }

        if (null != response.getErrorCollection()) {
            return VerificationHelper.verifyExtra(getXmlFieldValue(response.getErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }

        return null;
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

        errorMessage = verifyExtra(input.getResponse());
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

    @SuppressWarnings("CPD-END")
    @Override
    public boolean shouldVerify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
    {
        return (Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetReservationRequestType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservations -> reservations == null ? null : reservations.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent() &&
                Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservations -> reservations == null ? null : reservations.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent());
    }
}
