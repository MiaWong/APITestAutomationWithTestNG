package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.basic.VerifyGetCostAndAvailabilityBasic;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by wixie on 11/3/16.
 */
public class GetCostAndAvailCarRateDetailVerification implements IGetCostAndAvailabilityVerification
{
    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getRequest());
        Assert.assertNotNull(input.getRequest().getCarProductList());
        Assert.assertNotNull(input.getRequest().getCarProductList().getCarProduct());
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarProductList());
        Assert.assertNotNull(input.getResponse().getCarProductList().getCarProduct());

        input.getResponse().getCarProductList().getCarProduct().forEach(carProduct ->
        {
            Assert.assertNotNull(carProduct.getCarRateDetail());
            Assert.assertNotNull(carProduct.getCarRateDetail().getCarAdditionalFeesList());
            Assert.assertNotNull(carProduct.getCarRateDetail().getCarCoveragesCostList());
            Assert.assertNotNull(carProduct.getCarVehicleOptionList());
        });

        //  invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetCostAndAvailabilityBasic()
                ));

        return verifications.verify(input, verificationContext);
    }
}
