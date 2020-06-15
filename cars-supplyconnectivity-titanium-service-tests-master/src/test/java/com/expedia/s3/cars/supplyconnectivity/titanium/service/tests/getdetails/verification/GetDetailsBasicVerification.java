package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification;


import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyDriverAgeReceivedInResponse;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * Created by asharma1 on 8/12/2016.
 */
public class GetDetailsBasicVerification implements IGetDetailsVerification
{
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Validate.notNull(input, "GetDetailsBasicVerification input must not be %s", null);

        if(!Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetDetailsRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent())
        {
            Assert.fail("No CarProudct in GetDetails reuqest!");
        }

        if(!Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent())
        {
            {
                Assert.fail("No CarProudct in GetDetails response!");
            }
        }

        //invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetDetailsBasic(), new VerifyDriverAgeReceivedInResponse()
                ));

        return verifications.verify(input, verificationContext);
    }
}
