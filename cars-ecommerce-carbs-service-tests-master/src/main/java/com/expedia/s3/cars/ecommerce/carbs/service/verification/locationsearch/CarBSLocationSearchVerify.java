package com.expedia.s3.cars.ecommerce.carbs.service.verification.locationsearch;

import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

/**
 * Created by fehu on 3/21/2017.
 */
public class CarBSLocationSearchVerify {


    private CarBSLocationSearchVerify() {
    }

    public static void verifyBSLocationSearchResponse(CarLocationIataSearchResponse bsResponse)
    {

        if (null != bsResponse.getErrorCollection() && CollectionUtils.isNotEmpty(bsResponse.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError())
                 || CollectionUtils.isNotEmpty(bsResponse.getErrorCollection().getFieldRequiredErrorList().getFieldRequiredError())) {
            Assert.fail("there is some error in response");
        }
    }
}
