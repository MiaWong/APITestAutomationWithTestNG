package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;

/**
 * Created by v-mechen on 8/31/2018.
 */
public class VerificationUtils {
    private VerificationUtils(){

    }

    public static CarECommerceSearchCriteriaType getMatchedSearchCriteria(CarSearchResultType carSearchResult, CarECommerceSearchRequestType request)
    {
        for (final CarECommerceSearchCriteriaType carECommerceSearchCriteria : request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria()) {
            if (carECommerceSearchCriteria.getSequence() == carSearchResult.getSequence()) {
                return carECommerceSearchCriteria;
            }
        }

        return null;
    }
}
