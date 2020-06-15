package com.expedia.s3.cars.ecommerce.carbs.service.verification.cancel;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.VerificationHelper;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarbsCancelResponseBasicVerification implements IVerification<CancelVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_CANCEL_RESULT_IN_RESPONSE = "No valid car product or state code in Cancel Result response.";

    private static final String MESSAGE_SUCCESS = "Success in Cancellation";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";
    private static final String STATE_CODE_BOOKING_CANCEL_SUCCESS = "Cancelled";

    private String verifyBasicResponse(CarECommerceCancelResponseType response) {
        Assert.assertNotNull(response, "cancel response should not be null");
        Assert.assertNotNull(response.getCarReservation(),"CarReservation in cancel response should not be null");
        Assert.assertNotNull(response.getCarReservation().getBookingStateCode(),"BookingstateCode in in cancel response should not be null");

        if (!response.getCarReservation().getBookingStateCode().equals(STATE_CODE_BOOKING_CANCEL_SUCCESS)) {
            return MESSAGR_NO_CANCEL_RESULT_IN_RESPONSE;
        }
        return null;

    }

    private String basicVerify(CarECommerceCancelResponseType response) {
       final String errorMsg = verifyBasicResponse(response);
        if (null != errorMsg) {
            return errorMsg;
        }

        return null;
    }

    private String extraVerify(CarECommerceCancelResponseType response) {
        if (null != response.getCancelErrorCollection()) {
            return VerificationHelper.verifyExtra(PojoXmlUtil.getXmlFieldValue(response.getCancelErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }

        return null;
    }


    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext) {
        String errorMessage = "";

        errorMessage = basicVerify(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = extraVerify(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
