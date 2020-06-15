package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.GetOrderProcessVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import expedia.om.supply.messages.defn.v1.StatusCodeCategoryType;

import java.util.Arrays;

/**
 * Created by fehu on 11/10/2016.
 */
@SuppressWarnings("PMD")
public class GetOrderProcessBasicVerification implements IVerification<GetOrderProcessVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_ORDERPRODUCT_IN_RESPONSE = "Send GetOrderProcess message failed. No CarProduct in response!";
    private static final String MESSAGE_SUCCESS = "Success";
    private static final String MESSAGR_NO_SUCEESS_RESULT_IN_RESPONSE = "Verify GetOrderProcess  ResponseStatus failed";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(GetOrderProcessVerificationInput input, BasicVerificationContext basicVerificationContext) {
        String errorMsg = "";
        if(null == input.getResponse())
        {
            errorMsg = "Verify GetOrderProcess message failed. GetOrderProcessResponse is null.";
            return new VerificationResult(getName(), false, Arrays.asList(errorMsg));

        }
        else if (null == input.getResponse().getResponseStatus()
                || (!StatusCodeCategoryType.SUCCESS.equals(input.getResponse().getResponseStatus().getStatusCodeCategory())))
        {
            errorMsg = "Send GetOrderProcess message failed. " + input.getResponse().getResponseStatus().getStatusMessage();
            return new VerificationResult(getName(), false, Arrays.asList(errorMsg));

        }

       if (!input.getResponse().getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS))

           return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_SUCEESS_RESULT_IN_RESPONSE));


        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

}