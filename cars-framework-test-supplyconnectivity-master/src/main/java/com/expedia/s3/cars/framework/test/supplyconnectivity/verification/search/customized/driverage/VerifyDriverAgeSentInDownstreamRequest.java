package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.DriverAgeCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.Arrays;

/**
 * Created by jiyu on 8/24/16.
 */
public class VerifyDriverAgeSentInDownstreamRequest extends DriverAgeCase
{
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        Long expectedDriverAge = input.getRequest().getCarSearchStrategy().getDriverAgeYearCount();

        if (expectedDriverAge == null) {
            //the default driver age is 35
            expectedDriverAge = 35L;
        }

        try {
            return verifyDriverAgeInDownstream(verificationContext.getSpooferTransactions(), expectedDriverAge);
        }
        catch (XPathExpressionException ex) {

            //  PMD : New exception is thrown in catch block, original stack trace may be lost if don't pass original exception
            throw new IllegalArgumentException("The downstream request doesn't contain driver age.", ex);
        }
    }

    //TODO: a utils class that will produce VerificationResult remarks based on expected value, actual value etc
    private VerificationResult verifyDriverAgeInDownstream(Document spooferTransactions, Long expectedDriverAge)
            throws XPathExpressionException
    {
        final Node driverAgeNode = evaluateXpath(DRIVER_AGE_XPATH, spooferTransactions);
        return new VerificationResult(getName(), driverAgeNode.getNodeValue().equals(expectedDriverAge.toString()),
                Arrays.asList("Expected Driver age = " + expectedDriverAge
                        + "; actual in downstream request = " + driverAgeNode.getNodeValue()));
    }
}