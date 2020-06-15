package com.expedia.www.cars.bugatti.service.tests.verification;


import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;

/**
 * Created by miawang on 3/21/2017.
 */
public class SearchVerificationInput
        extends BasicVerificationInput<CarSearchRequest, CarSearchResponse>
{
    public SearchVerificationInput(CarSearchRequest carSearchRequest, CarSearchResponse carSearchResponse)
    {
        super(carSearchRequest, carSearchResponse);
    }
}
