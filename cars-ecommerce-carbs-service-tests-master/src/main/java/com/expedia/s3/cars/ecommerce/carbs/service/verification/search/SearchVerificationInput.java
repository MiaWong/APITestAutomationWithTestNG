package com.expedia.s3.cars.ecommerce.carbs.service.verification.search;

import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class SearchVerificationInput extends BasicVerificationInput<CarECommerceSearchRequestType, CarECommerceSearchResponseType> {
    public SearchVerificationInput(CarECommerceSearchRequestType request, CarECommerceSearchResponseType response) {
        super(request, response);
    }
}
