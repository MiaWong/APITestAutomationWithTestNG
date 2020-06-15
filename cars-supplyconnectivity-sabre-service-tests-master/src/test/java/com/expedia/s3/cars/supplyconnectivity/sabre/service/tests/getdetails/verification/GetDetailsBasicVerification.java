package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;

import org.testng.Assert;

import java.util.Arrays;

public class GetDetailsBasicVerification implements IGetDetailsVerification
{

    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception
    {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarProductList());
        Assert.assertNotNull(input.getResponse().getCarProductList().getCarProduct());

        //invoke a chain of basic getdetails verifications and return the result...
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetDetailsBasic()
                ));

        return verifications.verify(input, verificationContext);
    }

}
