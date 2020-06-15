package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;

/**
 * Created by fehu on 11/10/2016.
 */
public class GetOrderProcessVerificationInput extends BasicVerificationInput<GetOrderProcessRequest, GetOrderProcessResponseType> {
    public GetOrderProcessVerificationInput(GetOrderProcessRequest getOrderProcessRequest, GetOrderProcessResponseType getOrderProcessResponseType) {
        super(getOrderProcessRequest, getOrderProcessResponseType);
    }
}
