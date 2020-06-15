package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.basic.VerifyReserveBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized.VerifyReservedSpecialEquipment;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jiyu on 8/29/16.
 */
public class ReserveResponseVerification implements IReserveVerification
{
    private final boolean isSpecialEquipmentRequired;
    private boolean isSpecialEquipmentInUse() { return isSpecialEquipmentRequired; }

    public ReserveResponseVerification(boolean isUsingSpecialEquipment) {
        this.isSpecialEquipmentRequired = isUsingSpecialEquipment;
    }

    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityReserveResponseType::getCarReservation)
                .map(CarReservationType::getCarProduct)
                .isPresent());

        //invoke a chain of driver age verifications and return the result...
        ChainedVerification<ReserveVerificationInput, BasicVerificationContext> verifications = null;
        if (isSpecialEquipmentInUse()) {
            verifications = new ChainedVerification<>(getName(), Arrays.asList(new VerifyReserveBasic(), new VerifyReservedSpecialEquipment()));
        }
        else {
            verifications = new ChainedVerification<>(getName(), Arrays.asList(new VerifyReserveBasic()));
        }

        return verifications.verify(input, verificationContext);
    }
}
