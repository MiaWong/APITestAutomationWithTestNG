package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;
import static org.testng.Assert.assertNotNull;

/**
 * Created by v-mechen on 8/18/2016.
 */
public class DetailsResponseNotEmptyVerification implements IGetDetailsVerification
{
    @Override
    public String getName()
    {
        return getClass().getName();
    }

    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        assertNotNull(input);
        //verify car products will fail Assert (which will throw an AssertionError) if unsuccessful, since there's no
        verifyCarProductReturned(input.getResponse());
        return new IVerification.VerificationResult(getName(), true, Arrays.asList("Success"));
    }

    @SuppressWarnings("PMD")
    private void verifyCarProductReturned(CarSupplyConnectivityGetDetailsResponseType response)
    {
        //verify car product returned
        StringBuilder errorMsg = new StringBuilder();
        boolean matchedCarReturned = false;
        if (null == response)
        {
            errorMsg.append("Search Response is null.");
        }
        if (null == response.getCarProductList() || response.getCarProductList().getCarProduct().size() == 0)
        {
            errorMsg.append("No Cars return in SCS GetDetails response.");
        }

        if (null != response.getErrorCollection())
        {
            List<String> descriptionRawTextList = getXmlFieldValue(response.getErrorCollection(), "DescriptionRawText");
            if (descriptionRawTextList.size() > 0)
            {
                errorMsg.append("ErrorCollection is present in response");
                descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
            }
        }
        if (errorMsg.toString().trim().length() > 0)
        {
            Assert.fail(errorMsg.toString());
        }

    }
}
