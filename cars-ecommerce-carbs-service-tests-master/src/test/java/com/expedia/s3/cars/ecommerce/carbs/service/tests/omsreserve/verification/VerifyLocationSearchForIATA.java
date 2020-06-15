package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsLocationSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.testng.Assert;

public class VerifyLocationSearchForIATA {

    private VerifyLocationSearchForIATA(){

    }
    public static void shouldLocationReturned(CarbsLocationSearchRequestGenerator requestGenerator) {

        final CarLocationIataSearchResponse carLocationIataSearchResponse = requestGenerator.getCarLocationIataSearchResponse();
        if (CompareUtil.isObjEmpty(carLocationIataSearchResponse)) {
            Assert.fail("CarLocationIataSearchResponse return null.");
        }
        if (carLocationIataSearchResponse.getCarLocationList().getCarLocation().size() <= 0) {
            Assert.fail("There is no location returned in location search response.");
        }
    }
}
