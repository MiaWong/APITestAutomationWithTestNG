package com.expedia.s3.cars.ecommerce.carbs.service.verification.omgetchangedetail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailRequestType;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailResponseType;

/**
 * Created by fehu on 11/14/2016.
 */
public class GetChangeDetailsVerificationInput extends BasicVerificationInput<GetChangeDetailRequestType, GetChangeDetailResponseType> {
    public GetChangeDetailsVerificationInput(GetChangeDetailRequestType getChangeDetailRequestType, GetChangeDetailResponseType getChangeDetailResponseType) {
        super(getChangeDetailRequestType, getChangeDetailResponseType);
    }
}
