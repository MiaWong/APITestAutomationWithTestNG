package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.CreateRecordRequest;
import expedia.om.supply.messages.defn.v1.CreateRecordResponseType;

/**
 * Created by fehu on 11/10/2016.
 */
public class CreateRecordVerificationInput extends BasicVerificationInput<CreateRecordRequest, CreateRecordResponseType> {
    public CreateRecordVerificationInput(CreateRecordRequest createRecordRequest, CreateRecordResponseType createRecordResponseType) {
        super(createRecordRequest, createRecordResponseType);
    }
}
