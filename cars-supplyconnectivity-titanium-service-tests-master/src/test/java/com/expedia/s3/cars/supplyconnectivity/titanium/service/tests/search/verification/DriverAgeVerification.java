package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification;

import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;

import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage.VerifyDriverAgeReceivedInResponse;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage.VerifyDriverAgeSentInDownstreamRequest;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage.VerifyDriverAgeSurChargeInScsResponse;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage.VerifyDriverAgeErrorMessageValidation;
import org.testng.Assert;


import java.util.Arrays;
import java.util.Optional;

/**
 * This invokes a chain of verifications needed for driver age
 * Created by asharma1 on 8/10/2016.
 */
public class DriverAgeVerification implements ISearchVerification
{
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertTrue(Optional.ofNullable(input.getRequest()).map(CarSupplyConnectivitySearchRequestType::getCarSearchStrategy).isPresent());

        //invoke a chain of driver age verifications and return the result...
        final ChainedVerification<SearchVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyDriverAgeSentInDownstreamRequest()
                        , new VerifyDriverAgeReceivedInResponse()
                        , new VerifyDriverAgeErrorMessageValidation()
                        , new VerifyDriverAgeSurChargeInScsResponse()
                ));

        return verifications.verify(input, verificationContext);
    }


}
