package com.expedia.s3.cars.ecommerce.carbs.service.verification.getreservation;

import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationRequestType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetReservationVerificationInput extends BasicVerificationInput<CarECommerceGetReservationRequestType, CarECommerceGetReservationResponseType> {
    public GetReservationVerificationInput(CarECommerceGetReservationRequestType request, CarECommerceGetReservationResponseType response) {
        super(request, response);
    }
}
