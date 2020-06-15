package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic.VerifyCancelBasic;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by miawang on 12/22/2016.
 */
public class CancelResponseBasicVerification implements ICancelVerification {

    @Override
    public VerificationResult verify(CancelVerificationInput cancelVerificationInput, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(cancelVerificationInput);

        final ChainedVerification<CancelVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyCancelBasic()
                ));

        return verifications.verify(cancelVerificationInput, verificationContext);
    }
}
