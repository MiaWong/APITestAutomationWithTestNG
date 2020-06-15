package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.gdsmapverification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by v-mechen on 1/8/2017.
 */

public class GDSMapVerification implements IGetDetailsVerification
{
    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);

        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyTVRRReqestIsSentCorrectly(), new VerifyTVRRRspIsReturnedCorrectly()
                ));

        return verifications.verify(input, verificationContext);
    }

}
