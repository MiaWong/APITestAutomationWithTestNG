package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

public interface ISearchVerification extends IVerification<V5SearchVerificationInput, BasicVerificationContext>
{
    @Override
    default String getName()
    {
        return getClass().getSimpleName();
    }
}
