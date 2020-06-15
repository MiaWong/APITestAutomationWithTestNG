package com.expedia.s3.cars.ecommerce.carbs.service.verification.getreservation;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.VerificationHelper;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarbsGetReservationResponseBasicVerification implements IVerification<GetReservationVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE = "No valid car product in GetReservation Result response.";
    private static final String MESSAGE_INVALID_GETRESERVATION_RESPONSE = "GetReservation response is not valid";
    private static final String MESSAGE_SUCCESS = "Success in GetReservation";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";


    private String verifyBasicResponse(CarECommerceGetReservationResponseType response) {

        if (null == response || null == response.getCarReservationList())
        {
            return "response is null or CarReservationList is null";
        }

        if (CollectionUtils.isEmpty(response.getCarReservationList().getCarReservation())){
            return MESSAGE_INVALID_GETRESERVATION_RESPONSE;
        }

        if (null == response.getCarReservationList().getCarReservation().get(0).getCarProduct())
        {
            return MESSAGR_NO_GETRESERVATION_RESULT_IN_RESPONSE;
        }

            return null;
 }

    private String verifyBasic(CarECommerceGetReservationResponseType response) {
        String errorMsg = "";

        errorMsg = verifyBasicResponse(response);
        if (null != errorMsg) {
            return errorMsg;
        }

        return errorMsg;
    }

    private String verifyExtra(CarECommerceGetReservationResponseType response) {
         if (null != response.getGetReservationErrorCollection()) {
            return VerificationHelper.verifyExtra(getXmlFieldValue(response.getGetReservationErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }

        return null;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext) {
        String errorMessage = "";

        errorMessage = verifyBasic(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = verifyExtra(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

}
