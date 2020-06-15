package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationListType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.basic.VerifyGetReservationBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by miawang on 12/6/2016.
 */
public class GetReservationResponseBasicVerification implements IGetReservationVerification {
    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetReservationResponseType::getCarReservationList)
                .map(CarReservationListType::getCarReservation)
                .map(reservation -> reservation == null ? null : reservation.get(0))
                .map(CarReservationType::getCarProduct)
                .isPresent());

        //invoke a chain of driver age verifications and return the result...
        ChainedVerification<GetReservationVerificationInput, BasicVerificationContext> verifications = null;

        verifications = new ChainedVerification<>(getName(), Arrays.asList(new VerifyGetReservationBasic()));
        return verifications.verify(input, verificationContext);
    }
}
