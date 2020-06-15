package com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail;

import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetDetailsVerificationInput extends BasicVerificationInput<CarECommerceGetDetailsRequestType, CarECommerceGetDetailsResponseType> {
    public GetDetailsVerificationInput(CarECommerceGetDetailsRequestType request, CarECommerceGetDetailsResponseType response) {
        super(request, response);
    }
}
