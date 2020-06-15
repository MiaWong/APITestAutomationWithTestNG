package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic.VerifyCancelBasic;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by jiyu on 8/30/16.
 */
public class CancelResponseBasicVerification implements ICancelVerification {
    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(null != input);
        Assert.assertTrue(null != input.getResponse());
        Assert.assertTrue(null != input.getResponse().getCarReservation());

        //invoke a chain of driver age verifications and return the result...
        ChainedVerification<CancelVerificationInput, BasicVerificationContext> verifications = null;
        verifications = new ChainedVerification<>(getName(), Arrays.asList(new VerifyCancelBasic()));

        return verifications.verify(input, verificationContext);
    }
}
