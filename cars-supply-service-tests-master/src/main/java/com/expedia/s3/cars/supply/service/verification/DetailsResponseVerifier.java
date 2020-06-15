package com.expedia.s3.cars.supply.service.verification;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsResponseType;
import org.testng.Assert;

/**
 * Created by yyang4 on 8/24/2016.
 */
public class DetailsResponseVerifier implements IVerification {


    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }

    public static void verifyReturn(CarSupplyGetDetailsResponseType response, TestScenario scenarios) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();
        if (null == response) {
            errorMsg.append("No data return in GetDetails response.");
        }
        if (null == response.getCarProductList() || response.getCarProductList().getCarProduct() == null || response.getCarProductList().getCarProduct().isEmpty()) {
            errorMsg.append("No CarProduct return in GetDetails response.");
        }
        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }


    }

}
