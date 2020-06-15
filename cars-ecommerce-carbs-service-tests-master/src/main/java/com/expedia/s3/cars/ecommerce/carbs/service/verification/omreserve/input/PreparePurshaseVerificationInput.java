package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class PreparePurshaseVerificationInput extends BasicVerificationInput<PreparePurchaseRequest, PreparePurchaseResponseType> {

    public PreparePurshaseVerificationInput(PreparePurchaseRequest preparePurchaseRequest, PreparePurchaseResponseType preparePurchaseResponseType) {
        super(preparePurchaseRequest, preparePurchaseResponseType);
    }
}
