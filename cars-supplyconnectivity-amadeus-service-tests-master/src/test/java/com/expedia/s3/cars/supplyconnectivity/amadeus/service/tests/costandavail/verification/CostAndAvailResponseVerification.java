package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.verification;


import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.basic.VerifyGetCostAndAvailabilityBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;


/**
 * Created by miawang on 12/6/2016.
 */
public class CostAndAvailResponseVerification implements IGetCostAndAvailabilityVerification
{
    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws Exception
    {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarProductList());
        Assert.assertNotNull(input.getResponse().getCarProductList().getCarProduct());

        //invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetCostAndAvailabilityBasic()
                ));

        return verifications.verify(input, verificationContext);
    }
}