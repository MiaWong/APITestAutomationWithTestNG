package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by jiyu on 8/25/16.
 */
public abstract class DriverAgeCase implements ISearchVerification
{
    public static final String DRIVER_AGE_XPATH = "//SpoofedTransactions/Transaction/Request/*[local-name()='OTA_VehAvailRateRQ']/*[local-name()='VehAvailRQCore']/*[local-name()='DriverType']/@Age";

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return isValidDriverAgePresent(input);
    }

    public static Node evaluateXpath(String xpath, Node node) throws XPathExpressionException
    {
        return (Node) XPathFactory.newInstance().newXPath().compile(xpath).evaluate(node, XPathConstants.NODE);
    }

    public static boolean isValidDriverAgePresent(SearchVerificationInput input)
    {
        return (input.getRequest().getCarSearchStrategy().getDriverAgeYearCount() == null)
                || (input.getRequest().getCarSearchStrategy().getDriverAgeYearCount() >= 18);
    }

}
