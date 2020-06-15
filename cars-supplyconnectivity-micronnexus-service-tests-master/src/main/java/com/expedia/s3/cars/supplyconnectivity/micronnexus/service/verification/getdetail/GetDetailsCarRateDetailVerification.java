package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyConditionalCostListInResponse;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by wixie on 11/2/16.
 */
public class GetDetailsCarRateDetailVerification implements IGetDetailsVerification
{
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception {

        input.getResponse().getCarProductList().getCarProduct().forEach(carProduct ->
        {
            Assert.assertNotNull(carProduct.getCarRateDetail());
            Assert.assertNotNull(carProduct.getCarRateDetail().getCarAdditionalFeesList());
            Assert.assertNotNull(carProduct.getCarRateDetail().getCarCoveragesCostList());
            Assert.assertNotNull(carProduct.getCarVehicleOptionList());
        });

        //  invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetDetailsBasic(), new VerifyConditionalCostListInResponse()
                ));

        return verifications.verify(input, verificationContext);
    }
}