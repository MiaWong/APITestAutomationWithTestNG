package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import org.testng.Assert;

public class VerifyDeliveryCollectionInRsp {
    private VerifyDeliveryCollectionInRsp()
    {

    }

    public static void verifyDeliveryCollection(CarECommerceSearchRequestType request, CarECommerceSearchResponseType response) {
        for (final CarSearchResultType carSearchResult : response.getCarSearchResultList().getCarSearchResult()) {
            final CarECommerceSearchCriteriaType carECommerceSearchCriteria = VerificationUtils.getMatchedSearchCriteria(carSearchResult, request);
            final CarCatalogKeyType carCatalogKeyRsp = carSearchResult.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey();

            if(!((null == carCatalogKeyRsp.getCarPickupLocationKey().getDeliveryBoolean()
                    && null == carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getDeliveryBoolean())
                    || carCatalogKeyRsp.getCarPickupLocationKey().getDeliveryBoolean().equals(
                    carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getDeliveryBoolean())))
            {
                Assert.fail("DeliveryBoolean in response is not matching request, CarSearchResult sequence: " + carSearchResult.getSequence());
            }


            if(!((null == carCatalogKeyRsp.getCarDropOffLocationKey().getCollectionBoolean()
                    && null == carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCollectionBoolean())
                    || carCatalogKeyRsp.getCarDropOffLocationKey().getCollectionBoolean().equals(
                    carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCollectionBoolean())))
            {
                Assert.fail("CollectionBoolean in response is not matching request, CarSearchResult sequence: " + carSearchResult.getSequence());
            }
        }

    }


}