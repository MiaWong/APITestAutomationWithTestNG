package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.RollbackPreparePurchaseVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

import java.util.Arrays;

/**
 * Created by fehu on 11/13/2016.
 */
public class RollbackPreparePurchaseBasicVerification implements IVerification<RollbackPreparePurchaseVerificationInput, BasicVerificationContext> {
    private static final String MESSAGR_NO_CORRECT_IN_RESPONSE = "Send RollbackPreparePurchase message failed. ResponseStatus not right in response!";
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(RollbackPreparePurchaseVerificationInput input, BasicVerificationContext basicVerificationContext) {
        if (!input.getResponse().getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS)) {
            return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CORRECT_IN_RESPONSE));
        }
        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    @Override
    public boolean shouldVerify(RollbackPreparePurchaseVerificationInput input, BasicVerificationContext verificationContext) {
        return true;
    }


}
