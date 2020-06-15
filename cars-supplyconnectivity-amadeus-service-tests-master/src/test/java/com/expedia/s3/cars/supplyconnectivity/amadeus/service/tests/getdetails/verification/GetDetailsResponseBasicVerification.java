package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by miawang on 12/22/2016.
 */
public class GetDetailsResponseBasicVerification implements IGetDetailsVerification {

    @Override
    public VerificationResult verify(GetDetailsVerificationInput getDetailsVerificationInput, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(getDetailsVerificationInput);

        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetDetailsBasic()
                ));

        return verifications.verify(getDetailsVerificationInput, verificationContext);
    }
}
