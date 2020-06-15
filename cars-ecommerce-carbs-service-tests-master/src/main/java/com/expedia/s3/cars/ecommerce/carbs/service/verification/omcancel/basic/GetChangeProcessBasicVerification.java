package com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.basic;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input.GetChangeProcessVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import expedia.om.supply.messages.defn.v1.GetChangeProcessResponseType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by fehu on 11/13/2016.
 */
public class GetChangeProcessBasicVerification implements IVerification<GetChangeProcessVerificationInput, BasicVerificationContext> {
    private static final String MESSAGR_NO_CORRECT_IN_RESPONSE = "Send Cancel---RollbackPrepareChange message failed. response status not right in response!";
    private static final String MESSAGR_NO_CHANGECONTEXTID_IN_RESPONSE = "Send Cancel---RollbackPrepareChange message failed. ChangeContextId is null in response!";
    private static final String MESSAGR_NO_CHANGEORDERPROCESS_IN_RESPONSE = "Send Cancel---RollbackPrepareChange message failed. changeOrderProcess is null in response!";
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(GetChangeProcessVerificationInput input, BasicVerificationContext basicVerificationContext) {
        if (!input.getResponse().getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS)) {
            return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CORRECT_IN_RESPONSE));
        }
        if (!Optional.ofNullable(input.getResponse())
                .map(GetChangeProcessResponseType::getChangeContextID)
                .isPresent()) {
            return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CHANGECONTEXTID_IN_RESPONSE));
        }
        if (!Optional.ofNullable(input.getResponse())
                .map(GetChangeProcessResponseType::getChangeOrderProcess)
                .isPresent()) {
            return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CHANGEORDERPROCESS_IN_RESPONSE));
        }
        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }
}
