package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import org.testng.Assert;

public class VerifyOffaiportLocationReturnedMatchedReq {

    private VerifyOffaiportLocationReturnedMatchedReq() {

    }

    public static void verifyLocationReturned(CarECommerceSearchRequestType request, CarECommerceSearchResponseType response) {
        for (final CarSearchResultType carSearchResult : response.getCarSearchResultList().getCarSearchResult()) {
            boolean locationMatched = false;
            final CarECommerceSearchCriteriaType carECommerceSearchCriteria = VerificationUtils.getMatchedSearchCriteria(carSearchResult, request);
            final CarCatalogKeyType rspCarCatalogKey = carSearchResult.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey();

            if(rspCarCatalogKey.getCarPickupLocationKey().getLocationCode().equals(carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getLocationCode())
                    && rspCarCatalogKey.getCarPickupLocationKey().getSupplierRawText().equals(carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getSupplierRawText())
                    && rspCarCatalogKey.getCarPickupLocationKey().getCarLocationCategoryCode().equals(carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarLocationCategoryCode())
                    && rspCarCatalogKey.getCarDropOffLocationKey().getLocationCode().equals(carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getLocationCode())
                    && rspCarCatalogKey.getCarDropOffLocationKey().getSupplierRawText().equals(carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getSupplierRawText())
                    && rspCarCatalogKey.getCarDropOffLocationKey().getCarLocationCategoryCode().equals(carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarLocationCategoryCode())
                    )
            {
                locationMatched = true;
            }

            if(!locationMatched)
            {
                Assert.fail("Locaton code in response is not matching request, CarSearchResult sequence: " + carSearchResult.getSequence());
            }
        }
    }
}