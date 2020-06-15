package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification.gdsmapverification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by v-mechen on 1/8/2017.
 */

public class GDSMapVerification implements ICancelVerification
{
    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);

        final ChainedVerification<CancelVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyTVCRReqestIsSentCorrectly(), new VerifyTVCRRspIsReturnedCorrectly()
                ));

        return verifications.verify(input, verificationContext);
    }

}
