package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

/**
 * Created by sswaminathan on 8/15/16.
 */
public interface IReserveVerification extends IVerification<ReserveVerificationInput, BasicVerificationContext>
{
    @Override
    default String getName()
    {
        return getClass().getSimpleName();
    }
}
