package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.gdsmapverification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by v-mechen on 1/8/2017.
 */

public class GDSMapVerification implements IGetCostAndAvailabilityVerification
{
    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);

        final ChainedVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyTVARReqestIsSentCorrectly(), new VerifyTVARRspIsReturnedCorrectly()
                ));

        return verifications.verify(input, verificationContext);
    }

}
