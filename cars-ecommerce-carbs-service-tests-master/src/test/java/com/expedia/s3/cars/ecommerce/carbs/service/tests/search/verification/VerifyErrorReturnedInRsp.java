package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import org.testng.Assert;

public class VerifyErrorReturnedInRsp {

    private VerifyErrorReturnedInRsp() {

    }

    public static void verifyCarProductNotAvailableErrorReturned(CarECommerceSearchResponseType response) {
        if(null == response.getSearchErrorCollection() || null == response.getSearchErrorCollection().getCarProductNotAvailableError()
                || null == response.getSearchErrorCollection().getCarProductNotAvailableError())
        {
            Assert.fail("CarProductNotAvailableError is not returned in response!");
        }
        //<ns1:DescriptionRawText>The following conditions were met: 1) No results were returned, 2) None of the actionable fields were erroneous.  Aborting search retry.</ns1:DescriptionRawText>
        if(!response.getSearchErrorCollection().getCarProductNotAvailableError().getDescriptionRawText().equals("The following conditions were met: 1) No results were returned, 2) None of the actionable fields were erroneous.  Aborting search retry."))
        {
            Assert.fail("Error message is not returned: The following conditions were met: 1) No results were returned, 2) None of the actionable fields were erroneous.  Aborting search retry.");
        }


    }
}