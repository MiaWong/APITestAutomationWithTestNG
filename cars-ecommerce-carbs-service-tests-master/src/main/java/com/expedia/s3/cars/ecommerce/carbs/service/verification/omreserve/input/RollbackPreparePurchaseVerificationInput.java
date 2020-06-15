package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.RollbackPreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.RollbackPreparePurchaseResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class RollbackPreparePurchaseVerificationInput extends BasicVerificationInput<RollbackPreparePurchaseRequest, RollbackPreparePurchaseResponseType> {
    public RollbackPreparePurchaseVerificationInput(RollbackPreparePurchaseRequest rollbackPreparePurchaseRequest, RollbackPreparePurchaseResponseType rollbackPreparePurchaseResponseType) {
        super(rollbackPreparePurchaseRequest, rollbackPreparePurchaseResponseType);
    }
}
