package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.LatitudeAndLongitude;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
@SuppressWarnings("PMD")
public class VerifyReturnedLatLongInSearchResponse implements IVerification<SearchVerificationInput, BasicVerificationContext> {

    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext, Map<String, Object> testParams) {
        String errorMsg = "";

        final List<LatitudeAndLongitude> expectedList = (List<LatitudeAndLongitude>) testParams.get("latLongList");
        final List<LatitudeAndLongitude> returnedList = new ArrayList<>();

        final CarProductListType productList = searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().get(0).getCarProductList();

        if (null != productList)
        {
            for (final CarProductType carProduct : productList.getCarProduct())
            {
                final long lat = carProduct.getCarPickupLocation().getLatLong().getLatitudeAmount().getDecimal();
                final long latCount = carProduct.getCarPickupLocation().getLatLong().getLatitudeAmount().getDecimalPlaceCount();
                final double latitude = lat / Math.pow(10,latCount);

                final long lon = carProduct.getCarPickupLocation().getLatLong().getLongitudeAmount().getDecimal();
                final long lonCount = carProduct.getCarPickupLocation().getLatLong().getLongitudeAmount().getDecimalPlaceCount();
                final double longitude = lon / Math.pow(10,lonCount);
                if (returnedList.isEmpty())
                {
                    returnedList.add(new LatitudeAndLongitude(latitude, longitude));
                }
                else
                {
                    boolean existValue = false;
                    for (final LatitudeAndLongitude key : returnedList)
                    {
                        if ((key.getLatitude() == latitude) && (key.getLongitude() == longitude))
                        {
                            existValue = true;
                            continue;
                        }
                    }
                    if (!existValue)
                    {
                        returnedList.add(new LatitudeAndLongitude(latitude, longitude));
                    }
                }
            }
        }

        for (final LatitudeAndLongitude latAndLong : returnedList)
        {
            boolean isCorrect = false;
            for (final LatitudeAndLongitude latAndLongInResponse : expectedList)
            {
                if (latAndLong.getLatitude() == (latAndLongInResponse.getLatitude())
                        && latAndLong.getLongitude() ==(latAndLongInResponse.getLongitude()))
                {
                    isCorrect = true;
                    break;
                }
            }
            if (!isCorrect)
            {
                errorMsg = "latitude =[" + latAndLong.getLatitude() + "] and longitude =[" + latAndLong.getLongitude() +
                        "] is not corrected for given search with latitude and longitude";
            return  new VerificationResult(getName(), false, Arrays.asList(errorMsg));
            }
        }
        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext) {
        return null;
    }
}
