package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic.VerifyCancelBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.customized.VerifyCancelledSpecialEquipment;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jiyu on 8/30/16.
 */
public class CancelResponseVerification implements ICancelVerification
{
    private final boolean isSpecialEquipmentRequired;
    private boolean isSpecialEquipmentInUse() { return isSpecialEquipmentRequired; }

    public CancelResponseVerification(boolean isUsingSpecialEquipment) {
        this.isSpecialEquipmentRequired = isUsingSpecialEquipment;
    }

    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityCancelResponseType::getCarReservation)
                .isPresent());

        //invoke a chain of driver age verifications and return the result...
        ChainedVerification<CancelVerificationInput, BasicVerificationContext> verifications = null;
        if (isSpecialEquipmentInUse()) {
            verifications = new ChainedVerification<>(getName(), Arrays.asList(new VerifyCancelBasic(), new VerifyCancelledSpecialEquipment()));
        }
        else {
            verifications = new ChainedVerification<>(getName(), Arrays.asList(new VerifyCancelBasic()));
        }

        return verifications.verify(input, verificationContext);
    }
}
