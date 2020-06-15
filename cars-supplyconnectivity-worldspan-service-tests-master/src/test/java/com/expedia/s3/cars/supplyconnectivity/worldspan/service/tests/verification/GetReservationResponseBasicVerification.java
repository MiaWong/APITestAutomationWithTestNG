package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.basic.VerifyGetReservationBasic;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by jiyu on 8/30/16.
 */
public class GetReservationResponseBasicVerification implements IGetReservationVerification {
    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(null != input);
        Assert.assertTrue(null != input.getResponse());
        Assert.assertTrue(null != input.getResponse().getCarReservationList());
        Assert.assertTrue(null != input.getResponse().getCarReservationList().getCarReservation());
        Assert.assertTrue(!input.getResponse().getCarReservationList().getCarReservation().isEmpty());
        Assert.assertTrue(null != input.getResponse().getCarReservationList().getCarReservation().get(0).getCarProduct());

        //invoke a chain of driver age verifications and return the result...
        ChainedVerification<GetReservationVerificationInput, BasicVerificationContext> verifications =
                new ChainedVerification<>(getName(), Arrays.asList(new VerifyGetReservationBasic()));
        return verifications.verify(input, verificationContext);
    }
}
