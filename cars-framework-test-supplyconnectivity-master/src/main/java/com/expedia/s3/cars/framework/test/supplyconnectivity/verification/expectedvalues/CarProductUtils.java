package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import org.springframework.util.StringUtils;

/**
 * Created by v-mechen on 7/11/2017.
 */
public class CarProductUtils {
    private CarProductUtils()
    {
    }

    /*
    For perfMetrics logging
     */
    public static String getVendorIDs(CarProductType car)
    {
        if(null == car || null == car.getCarInventoryKey() || null == car.getCarInventoryKey().getCarCatalogKey())
        {
            return null;
        }
        final String vendorIDs = car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 0 ? null :
                String.valueOf(car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
        return vendorIDs;
    }

    public static String getPickupLocationCategoryFilter(CarProductType car)
    {
        String pickupLocationCategoryFilter = car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().
                getCarLocationCategoryCode();
        if (StringUtils.isEmpty(pickupLocationCategoryFilter))
        {
            pickupLocationCategoryFilter = "All";
        }
        return pickupLocationCategoryFilter;
    }

    public static String getDropoffLocationCategoryFilter(CarProductType car)
    {
        String dropoffLocationCategoryFilter = car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().
                getCarLocationCategoryCode();
        if (StringUtils.isEmpty(dropoffLocationCategoryFilter))
        {
            dropoffLocationCategoryFilter = "All";
        }
        return dropoffLocationCategoryFilter;
    }

    public static String getCarCount(CarProductType car)
    {
        final String carCount = (car == null || car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 0) ?
                null : "1";
        return carCount;
    }

    public static String getRequestCDCodes(CarProductType reqCar)
    {
        final String requestCDCodes = (null == reqCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode()) ? "null" :
                reqCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode();
        //if (!(reqCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID() > 0) &&
        // (StringUtils.isEmpty(pickupLocations)))
        //{
        //requestCDCodes = null;
        //}
        return requestCDCodes;
    }

    public static String getResponseCDCodes(CarProductType rspCar, String isSuccessful)
    {
        String responseCDCodes = (null == rspCar || null == rspCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode()) ? "null" :
                rspCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode();
        if ("false".equals(isSuccessful))
        {
            responseCDCodes = null;
        }

        return responseCDCodes;
    }

    public static String getTotalCost(CarProductType car) {
        String totalCost = null;
        if(null == car || null == car.getCostList() || null == car.getCostList().getCost())
        {
            return totalCost;
        }
        final CostListType reqCostList = car.getCostList();
        for (int i = 0; i < reqCostList.getCost().size(); i++) {
            if (reqCostList.getCost().get(i).getFinanceCategoryCode().equals("Total")
                    //DataLog only logged the cost when its currency=POSUCurrency,
                    //and if not add this condition to judgment, it will has error: the same key has already been added, when there are Xchanger in costlist.
                    && reqCostList.getCost().get(i).getDescriptionRawText().contains("Total rate (requested currency)")) {
                totalCost = String.valueOf(CostPriceCalculator.calculateCostAmount(reqCostList.getCost().get(i), 0, true));
                //requestTotalCostCurrency = reqCostList.getCost().get(i).getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();

            }
        }
        return totalCost;
    }

    public static String getTotalCostCurrency(CarProductType car) {
        String totalCostCurrency = null;
        if(null == car || null == car.getCostList() || null == car.getCostList().getCost())
        {
            return totalCostCurrency;
        }
        final CostListType reqCostList = car.getCostList();
        for (int i = 0; i < reqCostList.getCost().size(); i++) {
            if (reqCostList.getCost().get(i).getFinanceCategoryCode().equals("Total")
                    //DataLog only logged the cost when its currency=POSUCurrency,
                    //and if not add this condition to judgment, it will has error: the same key has already been added, when there are Xchanger in costlist.
                    && reqCostList.getCost().get(i).getDescriptionRawText().contains("Total rate (requested currency)")) {

                totalCostCurrency = reqCostList.getCost().get(i).getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();

            }
        }
        return totalCostCurrency;
    }

}
