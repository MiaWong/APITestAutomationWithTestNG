package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5.basic;

import com.expedia.cars.schema.common.v1.CarOfferList;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchResult;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchResultList;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5.ISearchVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;

import java.util.Arrays;
import java.util.Optional;
@SuppressWarnings("PMD")
public class VerifySearchResponseNotEmpty implements ISearchVerification
{
    public static final String MESSAGE_SUCCESS = "Success";
    private static final String MESSAGE_NO_CAR_OFFER_IN_SEARCH_RESPONSE = "No valid car offer in search response.";

    @Override
    public boolean shouldVerify(V5SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(V5SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        //verify search results exist
        if(!Optional.ofNullable(input.getResponse())
                .map(CarSearchResponse::getSearchResultList)
                .map(SearchResultList::getSearchResult)
                .isPresent())
        {
            return new VerificationResult(getName(), false, Arrays.asList("no CarSearchResult in response!"));
        }

        String errorMessage = verifyCarOfferReturned(input.getResponse());
        if (null != errorMessage && !errorMessage.equals("null"))
        {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

    private String verifyCarOfferReturned(CarSearchResponse response)
    {
        boolean hasCarOffer = Optional.ofNullable(response)
                .map(CarSearchResponse::getSearchResultList)
                .map(SearchResultList::getSearchResult)
                .map(results -> results == null ? null : results.get(0))
                .map(SearchResult::getCarOfferList)
                .map(CarOfferList::getCarOffer)
                .map(offers -> offers == null ? null : offers.get(0))
                .isPresent();

        return (hasCarOffer) ? null : MESSAGE_NO_CAR_OFFER_IN_SEARCH_RESPONSE;
    }
}
