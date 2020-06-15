package com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.GetChangeProcessRequest;
import expedia.om.supply.messages.defn.v1.GetChangeProcessResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class GetChangeProcessVerificationInput extends BasicVerificationInput<GetChangeProcessRequest, GetChangeProcessResponseType> {
    public GetChangeProcessVerificationInput(GetChangeProcessRequest getChangeProcessRequest, GetChangeProcessResponseType getChangeProcessResponseType) {
        super(getChangeProcessRequest, getChangeProcessResponseType);
    }
}
