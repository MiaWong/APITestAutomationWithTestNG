package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.verification.errorhandlingverification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by v-mechen on 12/7/2016.
 */

public class ErrorHandlingVerification implements IGetReservationVerification
{
    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);

        final ChainedVerification<GetReservationVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyErrorIsReturnedCorrectly()
                ));

        return verifications.verify(input, verificationContext);
    }

}
