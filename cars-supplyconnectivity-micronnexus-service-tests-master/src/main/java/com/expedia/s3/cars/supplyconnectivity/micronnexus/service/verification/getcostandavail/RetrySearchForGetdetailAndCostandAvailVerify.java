package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.w3c.dom.Document;

import java.util.Arrays;

/**
 * Created by fehu on 1/19/2017.
 */
public class RetrySearchForGetdetailAndCostandAvailVerify implements IVerification<BasicVerificationInput,BasicVerificationContext> {


    @Override
    public String getName() {
        return RetrySearchForGetdetailAndCostandAvailVerify.class.getSimpleName();
    }

    @Override
    public VerificationResult verify(BasicVerificationInput input, BasicVerificationContext verificationContext) {

        final Document doc = verificationContext.getSpooferTransactions();
        if(null != doc.getElementsByTagName("VehAvailRateRS") && null != doc.getElementsByTagName("VehRateRuleRS"))
        {
            return new VerificationResult("RetrySearchForGetdetailAndCostandAvailVerify",true, Arrays.asList("success"));
        }

        return new VerificationResult("RetrySearchForGetdetailAndCostandAvailVerify", false, Arrays.asList("failed"));

    }
}
