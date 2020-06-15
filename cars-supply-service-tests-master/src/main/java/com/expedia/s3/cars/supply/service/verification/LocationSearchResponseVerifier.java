package com.expedia.s3.cars.supply.service.verification;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.supply.messages.location.search.defn.v1.CarSupplyLocationSearchResponse;
import org.testng.Assert;

/**
 * Created by yyang4 on 8/22/2016.
 */
public class LocationSearchResponseVerifier {


    private LocationSearchResponseVerifier() {
    }


    public static void verifyReturn(CarSupplyLocationSearchResponse response, LocationSearchTestScenario scenarios) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();
        //Verify location returned when only iata or long/lat/radius sent in request
        if(null == scenarios.getIataCode()|| null == scenarios.getDistanceUnit()) {
            verifyLocationReturned(response, errorMsg);
        }
        //Verify erro returned when both iata and long/lat/radius sent in request
        else
        {
            verifyErrorReturnedForIataLatLong(response, errorMsg);
        }


        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }


    }

    public static void verifyErrorReturnedForIataLatLong(CarSupplyLocationSearchResponse response, StringBuilder errorMsg) {
        if (null == response || null ==  response.getErrorCollection() || null == response.getErrorCollection().getFieldInvalidErrorList()
                || response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError().isEmpty()) {
            errorMsg.append("FieldInvalidError is not returned when both IATA and lat/long/radius exist in request.");
        } else if (!response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError().get(0).getFieldKey().getXPath().equals("CarSupplyLocationSearchRequestSearchCriteria/Iata")
                || !response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError().get(0).getDescriptionRawText().equals("Only Iata or Latitude and Longitude can be present in the request")) {
            errorMsg.append("XPath 'CarSupplyLocationSearchRequestSearchCriteria/Iata' and DescriptionRawText 'Only Iata or Latitude and Longitude can be present in the request' should be returned in response.");
        }

    }

    public static void verifyLocationReturned(CarSupplyLocationSearchResponse response, StringBuilder errorMsg) {
        if (null == response) {
            errorMsg.append("Response is null.");
        } else if (null == response.getCarLocationList() || response.getCarLocationList().getCarLocation().isEmpty()) {
            errorMsg.append("No Location return in response.");
        }

    }

}
