package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

import java.util.Arrays;

/**
 * Created by fehu on 11/13/2016.
 */
public class PreparePurchaseBasicVerification implements IVerification<PreparePurshaseVerificationInput, BasicVerificationContext> {
    private static final String MESSAGR_NO_CORRECT_IN_RESPONSE = "Send PreparePurchsse message failed. response status not right in response!";
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(PreparePurshaseVerificationInput input, BasicVerificationContext basicVerificationContext) {
        if (!input.getResponse().getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS)) {
            return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CORRECT_IN_RESPONSE, input.getResponse().getResponseStatus().getStatusCodeCategory().value(), input.getResponse().getResponseStatus().getStatusMessage()));
        }return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }
}