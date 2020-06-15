package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyPhoneListReceivedInResponse;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by jiyu on 9/15/16.
 */
public class GetDetailsPhoneListVerification implements IGetDetailsVerification
{
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getRequest());
        Assert.assertNotNull(input.getRequest().getCarProductList());
        Assert.assertNotNull(input.getRequest().getCarProductList().getCarProduct());
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarProductList());
        Assert.assertNotNull(input.getResponse().getCarProductList().getCarProduct());

        //  invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetDetailsBasic(), new VerifyPhoneListReceivedInResponse()
                ));

        return verifications.verify(input, verificationContext);
    }
}
