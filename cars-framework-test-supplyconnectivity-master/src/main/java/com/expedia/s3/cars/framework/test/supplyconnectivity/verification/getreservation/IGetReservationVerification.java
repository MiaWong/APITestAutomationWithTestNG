package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

/**
 * Created by asharma1 on 8/12/2016.
 */
public interface IGetReservationVerification extends IVerification<GetReservationVerificationInput,BasicVerificationContext>
{
    @Override
    default String getName()
    {
        return getClass().getSimpleName();
    }
}