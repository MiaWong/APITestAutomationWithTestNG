package com.expedia.s3.cars.ecommerce.carbs.service.verification.reserve;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.VerificationHelper;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

import java.util.Arrays;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarbsReserveResponseBasicVefification implements IVerification<ReserveVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_RESERVE_RESULT_IN_REQUEST = "No valid car product in Reserve request.";

    private static final String MESSAGE_SUCCESS = "Success";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";

    @SuppressWarnings("PMD")
    private String verifyExtra(CarECommerceReserveResponseType response) {

       if (null == response || null == response.getCarReservation() || null == response.getCarReservation().getCarProduct())
       {
         return MESSAGR_NO_RESERVE_RESULT_IN_REQUEST;
       }

        if (null != response.getReserveErrorCollection()) {
            if (null != response.getReserveErrorCollection().getDownstreamServiceTimeoutError() &&
                    null != response.getReserveErrorCollection().getDownstreamServiceTimeoutError())
            {  return VerificationHelper.verifyExtra(getXmlFieldValue(response.getReserveErrorCollection().
                        getDownstreamServiceTimeoutError(), TAG_DESCRIPTION_RAW_TEXT));}
            else if (null != response.getReserveErrorCollection().getReferenceInvalidErrorList() &&
                    null != response.getReserveErrorCollection().getReferenceInvalidErrorList().getReferenceInvalidError()) {
                return VerificationHelper.verifyExtra(getXmlFieldValue(response.getReserveErrorCollection().
                        getReferenceInvalidErrorList().getReferenceInvalidError(), TAG_DESCRIPTION_RAW_TEXT));
            }
       }

        return null;
    }

    @SuppressWarnings("CPD-START")
    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) {
        String errorMessage = "";
         errorMessage = verifyExtra(input.getResponse());
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
