package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

/**
 * An IVerification interface for SCS Search tests (for simplicity)
 * Created by asharma1 on 8/10/2016.
 */
public interface ISearchVerification
    extends IVerification<SearchVerificationInput,BasicVerificationContext>
{
    @Override
    default String getName()
    {
        return getClass().getSimpleName();
    }
}
