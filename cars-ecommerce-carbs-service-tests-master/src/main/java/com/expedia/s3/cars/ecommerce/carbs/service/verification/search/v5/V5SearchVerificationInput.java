package com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5;

import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

public class V5SearchVerificationInput extends BasicVerificationInput<CarSearchRequest, CarSearchResponse>
{
    public V5SearchVerificationInput(CarSearchRequest request, CarSearchResponse response)
    {
        super(request, response);
    }
}
