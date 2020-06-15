package com.expedia.s3.cars.supply.service.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchResponseType;
import org.testng.Assert;

/**
 * Created by yyang4 on 8/22/2016.
 */
public class SearchResponseVerifier {


    private SearchResponseVerifier() {
    }


    public static void verifyReturn(CarSupplySearchResponseType response, TestScenario scenarios) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();
        final boolean matchedCarReturned = verifyResponse(response, errorMsg);
        if (!matchedCarReturned) {
            errorMsg.append("No expected CarProduct returned in CarSS response for BusinessModelID='").append(scenarios.getBusinessModel()).append("' and ServiceProviderID='").append(scenarios.getServiceProviderID()).append("'.");
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

    }


    public static boolean verifyResponse(CarSupplySearchResponseType response, StringBuilder errorMsg) {
        boolean matchedCarReturned = false;
        if (null == response) {
            errorMsg.append("No data return in response.");
        } else if (null == response.getCarSearchResultList() || response.getCarSearchResultList().getCarSearchResult().isEmpty()) {
            errorMsg.append("No SearchResult return in response.");
        } else {
            for (final CarSearchResultType result : response.getCarSearchResultList().getCarSearchResult()) {
                if (null != result.getCarProductList()
                        && null != result.getCarProductList().getCarProduct()
                        && result.getCarProductList().getCarProduct().size() > 0) {
                    matchedCarReturned = true;
                    break;
                }
            }
        }
        return matchedCarReturned;

    }

}
