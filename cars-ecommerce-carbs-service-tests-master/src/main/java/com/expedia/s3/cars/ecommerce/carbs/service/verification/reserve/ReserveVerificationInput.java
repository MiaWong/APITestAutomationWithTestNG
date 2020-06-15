package com.expedia.s3.cars.ecommerce.carbs.service.verification.reserve;

import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveRequestType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class ReserveVerificationInput extends BasicVerificationInput<CarECommerceReserveRequestType, CarECommerceReserveResponseType> {
    public ReserveVerificationInput(CarECommerceReserveRequestType request, CarECommerceReserveResponseType response) {
        super(request, response);
    }
}
