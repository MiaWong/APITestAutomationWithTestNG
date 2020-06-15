package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;

import java.util.List;
import java.util.Locale;


/**
 * Created by v-mechen on 9/5/2018.
 */
public class BookingVerificationUtils {
    private  BookingVerificationUtils()
    {

    }
    public static String getBookingID(PreparePurchaseResponseType response)
    {
        return response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getLegacyItineraryBookingDirectoryRow().getBookingID().toString();
    }

    public static String getBookingItemID(PreparePurchaseResponseType response)
    {
        return response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getSupplyRecordLocator().getBookingItemID().toString();
    }

    public static String findReferenceCodeForCategory(List<ReferenceType> referenceList, String referenceCategoryCode)
    {
        String referenceCode = null;

        for (final ReferenceType ref : referenceList)
        {

            if (null != ref && null != ref.getReferenceCategoryCode() && ref.getReferenceCategoryCode()
                    .toLowerCase(Locale.US).equalsIgnoreCase(referenceCategoryCode))
            {
                referenceCode = ref.getReferenceCode();
            }
        }

        return referenceCode;
    }

}
