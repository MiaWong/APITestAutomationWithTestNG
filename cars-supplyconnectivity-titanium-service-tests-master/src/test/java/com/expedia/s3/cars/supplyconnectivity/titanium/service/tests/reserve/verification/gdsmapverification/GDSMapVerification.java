package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.gdsmapverification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by v-mechen on 1/8/2017.
 */

public class GDSMapVerification implements IReserveVerification
{
    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);

        final ChainedVerification<ReserveVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyTVRSReqestIsSentCorrectly(), new VerifyTVRSRspIsReturnedCorrectly()
                ));

        return verifications.verify(input, verificationContext);
    }

}
