package com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail;

import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetCostAndAvailabilityVerificationInput extends BasicVerificationInput
        <CarECommerceGetCostAndAvailabilityRequestType, CarECommerceGetCostAndAvailabilityResponseType> {
    public GetCostAndAvailabilityVerificationInput(CarECommerceGetCostAndAvailabilityRequestType request,
                                                   CarECommerceGetCostAndAvailabilityResponseType response) {
        super(request, response);
    }
}
