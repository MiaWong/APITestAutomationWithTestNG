package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import org.springframework.util.StringUtils;
import org.testng.Assert;

public class VerifyRequestCDCodeReturnedInRsp {

    private VerifyRequestCDCodeReturnedInRsp() {

    }

    public static void verifyCDCodeReturned(CarECommerceSearchRequestType request, CarECommerceSearchResponseType response) {
        final CarECommerceSearchCriteriaType requestSearchCriteria = request.getCarECommerceSearchCriteriaList().
                getCarECommerceSearchCriteria().get(0);
        for (final CarRateOverrideType carRateOverride : requestSearchCriteria.getCarRateOverrideList().getCarRateOverride()) {
            boolean vendorWithCDReturned = false;
            String cdCode = carRateOverride.getCorporateDiscountCode();
            //DB default CD code is 51354111 - SELECT * FROM [CarAmadeusSCS_STT05].[dbo].[SupplierItemMap]
            if(StringUtils.isEmpty(cdCode))
            {
                cdCode = "51354111";
            }
            final long vendorSupplierID = carRateOverride.getVendorSupplierID();
            for (final CarProductType carProduct : response.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct()) {
                if (cdCode.equals(carProduct.getCarInventoryKey().getCarRate().getCorporateDiscountCode())
                        && vendorSupplierID == carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()) {
                    vendorWithCDReturned = true;
                    break;
                }
            }
            if (!vendorWithCDReturned) {
                Assert.fail("Request CD code is not returned in response(supplierID/CDCode): " + vendorSupplierID + "/" + cdCode);
            }
        }
    }
}