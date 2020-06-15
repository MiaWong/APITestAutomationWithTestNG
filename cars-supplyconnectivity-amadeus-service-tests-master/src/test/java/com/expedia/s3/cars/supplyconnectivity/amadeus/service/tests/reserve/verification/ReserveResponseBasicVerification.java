package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.basic.VerifyReserveBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by miawang on 12/22/2016.
 */
public class ReserveResponseBasicVerification implements IReserveVerification {

    @Override
    public VerificationResult verify(ReserveVerificationInput reserveVerificationInput,
                                     BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(Optional.ofNullable(reserveVerificationInput.getResponse())
                .map(CarSupplyConnectivityReserveResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent());

        final ChainedVerification<ReserveVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyReserveBasic()
                ));

        return verifications.verify(reserveVerificationInput, verificationContext);
    }
}
