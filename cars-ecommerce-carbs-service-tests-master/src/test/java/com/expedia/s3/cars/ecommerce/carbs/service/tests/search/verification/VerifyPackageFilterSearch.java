package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.PackageSearchFilterObject;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("PMD")
public class VerifyPackageFilterSearch {

    private VerifyPackageFilterSearch() {

    }

    public static void verifyReturnedCarAccordingFilter(SearchVerificationInput filterSearchInput, CarECommerceSearchResponseType defaultSearchResponsse) throws DataAccessException {

        final CarECommerceSearchRequestType filterSearchRequet = filterSearchInput.getRequest();
        final CarECommerceSearchResponseType filterSearchResponse = filterSearchInput.getResponse();
        final StringBuilder error = new StringBuilder();
        for (int i = 0; i < filterSearchResponse.getCarSearchResultList().getCarSearchResult().size(); i++) {
            //Get expected car product list
            final List<PackageSearchFilterObject> carsBeforeFilter = getBasicFilterObjectFromProducts(defaultSearchResponsse.getCarSearchResultList().getCarSearchResult().get(i).getCarProductList().getCarProduct(),
                    filterSearchRequet.getOptimizationStrategyCode(), filterSearchRequet.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(i).getCurrencyCode());

            List<PackageSearchFilterObject> expFilteredCars = new ArrayList<>();
            if (!StringUtils.isEmpty(filterSearchRequet.getOptimizationStrategyCode()) && CarCommonEnumManager.PackageSearchFilterType.Savings.name().equals(filterSearchRequet.getOptimizationStrategyCode()))
            {
                //To get the n cars with the highest savings
                expFilteredCars = getHighestSavingsCarByClassFromProductList(carsBeforeFilter, filterSearchRequet.getCarECommerceSearchStrategy().getResultSetCount());

            }
            else if (!StringUtils.isEmpty(filterSearchRequet.getOptimizationStrategyCode()) && CarCommonEnumManager.PackageSearchFilterType.Margin.name().equals(filterSearchRequet.getOptimizationStrategyCode()))
            {
                //To get the n cheapest cars with the most margin or commission amount per class
                expFilteredCars = getMaxMarginCarByClassFromProductList(carsBeforeFilter, filterSearchRequet.getCarECommerceSearchStrategy().getResultSetCount());
            }
            else if (!StringUtils.isEmpty(filterSearchRequet.getOptimizationStrategyCode()) && CarCommonEnumManager.PackageSearchFilterType.Capacity.name().equals(filterSearchRequet.getOptimizationStrategyCode()))
            {
                //To get the n cheapest car by capacity per class
                expFilteredCars = getCapacityCheapestCarFromProductList(carsBeforeFilter, filterSearchRequet.getCarECommerceSearchStrategy().getResultSetCount(), filterSearchRequet.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(i).getPassengerCount());
            }
            else
            {
                 //To get the n cheapest car by class
                 expFilteredCars = getCheapestCarByClassFromProductList(carsBeforeFilter, filterSearchRequet.getCarECommerceSearchStrategy().getResultSetCount());
            }
            error.append(compareProductListInSearchResult(filterSearchResponse.getCarSearchResultList().getCarSearchResult().get(i).getCarProductList().getCarProduct(), expFilteredCars));

        }

        if (!StringUtils.isEmpty(error.toString()))
        {
            Assert.fail("failed because in response there is no expected value like " + error);
        }
    }

    public static String compareProductListInSearchResult(List<CarProductType> actualCarProductList, List<PackageSearchFilterObject> expectedCarProducts)
    {
        final StringBuilder errorMsg = new StringBuilder();
        if (actualCarProductList.size() != expectedCarProducts.size())
        {
            errorMsg.append(String.format("The actual CarProductList count=%s is not the expected count=%s", actualCarProductList.size(), expectedCarProducts.size()));
        }
        for (int i = 0; i < expectedCarProducts.size(); i++)
        {
            boolean expCarExist = false;
            for (final CarProductType actCar: actualCarProductList)
            {
                if (actCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == expectedCarProducts.get(i).getVendorSupplierID()
                        && actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == expectedCarProducts.get(i).getCarCategoryCode()
                        && actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode() == expectedCarProducts.get(i).getCarTypeCode()
                        && actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode() == expectedCarProducts.get(i).getCarTransmissionDriveCode()
                        && actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode() == expectedCarProducts.get(i).getCarFuelACCode()
                        )
                {
                    expCarExist = true;
                    break;
                }
            }
            if (!expCarExist)
            {
                errorMsg.append(String.format("The actual car of sequence ={0}: VendorSupplierID/CarCategoryCode/CarTypeCode/CarTransmissionDriveCode/CarFuelACCode={1}/{2}/{3}/{4}/{5} is not the equal car={6}/{7}/{8}/{9}/{10}.\r\n",
                        i, actualCarProductList.get(i).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(),
                        actualCarProductList.get(i).getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode(),
                        actualCarProductList.get(i).getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode(),
                        actualCarProductList.get(i).getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode(),
                        actualCarProductList.get(i).getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode(),
                        expectedCarProducts.get(i).getVendorSupplierID(), expectedCarProducts.get(i).getCarCategoryCode(), expectedCarProducts.get(i).getCarTypeCode(),expectedCarProducts.get(i).getCarTransmissionDriveCode(),  expectedCarProducts.get(i).getCarFuelACCode()));
            }
        }
        return errorMsg.toString();
    }

    public static List<PackageSearchFilterObject> getBasicFilterObjectFromProducts(List<CarProductType> carProducts, String optimizationStrategyCode, String currencyCode) throws DataAccessException {
        final List<PackageSearchFilterObject> result = new ArrayList<>();
        final CarsInventoryHelper carsInventory = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        for (final CarProductType carProduct: carProducts)
        {
            final CarCatalogKeyType carCatalogKey = carProduct.getCarInventoryKey().getCarCatalogKey();
            final CarVehicleType carVehicle = carCatalogKey.getCarVehicle();
            final PackageSearchFilterObject filterObject = new PackageSearchFilterObject(carCatalogKey.getVendorSupplierID(),
                    carVehicle.getCarCategoryCode(), carVehicle.getCarTypeCode(), carVehicle.getCarTransmissionDriveCode(), carVehicle.getCarFuelACCode());
            // To get total price
            filterObject.setTotalPrice(CostPriceCalculator.getPriceValueByFinanceCategoryCode(carProduct.getPriceList(), currencyCode, "Total",null));
            if (!StringUtils.isEmpty(optimizationStrategyCode) && CarCommonEnumManager.PackageSearchFilterType.Savings.name().equals(optimizationStrategyCode))
            {
                //To get the car's Savings amount
                if (!StringUtils.isEmpty(carProduct.getTotalReferencePrice()) && !StringUtils.isEmpty(carProduct.getTotalReferencePrice().getMultiplierOrAmount())
                        && !StringUtils.isEmpty(carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount())
                        && !StringUtils.isEmpty(carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())
                        && carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().length() > 0)
                {
                    final double totalRefPrice = CostPriceCalculator.calculateAmountFromCurrencyAmountOrMultiplier(carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount(), null, 0);
                    filterObject.setSavings(totalRefPrice - filterObject.getTotalPrice());
                }
                else
                {
                    filterObject.setSavings(0);
                }

            }
            else if (!StringUtils.isEmpty(optimizationStrategyCode) && CarCommonEnumManager.PackageSearchFilterType.Margin.name().equals(optimizationStrategyCode))
            {
                //To get the car's max margin/commission amount - try get the margin first, then MN dynamic commission, last one is CarItem commission in DB
                filterObject.setMargin(CostPriceCalculator.getPriceAmountByFinanceCategoryCode(carProduct.getPriceList(), currencyCode, "MaxMarginAmt", 4, null, false));
                final double dynamicCommission = CostPriceCalculator.getCostAmountByFinanceCategoryCode(carProduct.getCostList(), currencyCode, "Commission", 4, null, null,null,false);
                final double transactionFee = CostPriceCalculator.getCostAmountByFinanceCategoryCode(carProduct.getCostList(), currencyCode, "ProviderTransactionFees", 4, null, null, null, false);
                //If no markup and dynamic commission exist, get the dynamic commission as margin
                if (filterObject.getMargin() == 0 && dynamicCommission > 0)
                {
                    filterObject.setMargin(dynamicCommission - transactionFee);
                }
                //If still no margin, get the commission in DB as margin
                if (filterObject.getMargin() == 0) {
                    final BigDecimal commissionPct = carsInventory.getCommissionDetailsByCarItemIDAndAirportCode(carProduct.getCarInventoryKey().getCarItemID(), carProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode());
                    filterObject.setMargin(filterObject.getTotalPrice() * commissionPct.doubleValue());
                }
            }
            else if (!StringUtils.isEmpty(optimizationStrategyCode) && CarCommonEnumManager.PackageSearchFilterType.Capacity.name().equals(optimizationStrategyCode))
            {
                filterObject.setCapacity(carProduct.getCarCatalogMakeModel().getCarCapacityAdultCount());

            }
            result.add(filterObject);
        }
        return result;
    }

    public static List<PackageSearchFilterObject> getCapacityCheapestCarFromProductList(List<PackageSearchFilterObject> carProductList, long resultSetCount, long passengerCount)
    {

        List<PackageSearchFilterObject> result = new ArrayList<>();

        //filter out the cars can't hold the requested passenger count
        for (final PackageSearchFilterObject car: carProductList)
        {
            if (car.getCapacity() >= passengerCount)
            {
                result.add(car);
            }
        }
        //If no car left, return all the cars
        if (result.isEmpty())
        {
            result = carProductList;
        }
        //Get the n cheapest cars after capacity filter
        result = getCheapestCarByClassFromProductList(result, resultSetCount);
        return result;
    }

    public static List<PackageSearchFilterObject> getCheapestCarByClassFromProductList(List<PackageSearchFilterObject> carProductList, long resultSetCount)
    {
        final Map<Long, PackageSearchFilterObject> carFilteredByClass = new HashMap<>();
        carFilteredByClass.put(carProductList.get(0).getCarCategoryCode(), carProductList.get(0));
        for (int j = 1; j < carProductList.size(); j++)
        {
            if (!carFilteredByClass.containsKey(carProductList.get(j).getCarCategoryCode()))
            {
                carFilteredByClass.put(carProductList.get(j).getCarCategoryCode(), carProductList.get(j));
            }
            else if (carFilteredByClass.get(carProductList.get(j).getCarCategoryCode()).getTotalPrice() - carProductList.get(j).getTotalPrice() > 0)
            {
                carFilteredByClass.put(carProductList.get(j).getCarCategoryCode(), carProductList.get(j));
            }
        }

        return getOutputAfterSortingAndFiltering(carFilteredByClass, CarCommonEnumManager.PackageSearchFilterType.Cheapest.name(), resultSetCount);
    }

    // To get the n cars with the highest savings
    public static List<PackageSearchFilterObject> getHighestSavingsCarByClassFromProductList(List<PackageSearchFilterObject> carProductList, long resultSetCount)
    {
        final Map<Long, PackageSearchFilterObject> carFilteredByClass = new HashMap<>();
        carFilteredByClass.put(carProductList.get(0).getCarCategoryCode(), carProductList.get(0));
        for (int i = 0; i < carProductList.size(); i++)
        {
            if (carFilteredByClass.containsKey(carProductList.get(i).getCarCategoryCode()))
            {
                if (carFilteredByClass.get(carProductList.get(i).getCarCategoryCode()).getSavings() - carProductList.get(i).getSavings() < 0)
                {
                    carFilteredByClass.put(carProductList.get(i).getCarCategoryCode(), carProductList.get(i));
                }
                else if (carFilteredByClass.get(carProductList.get(i).getCarCategoryCode()).getSavings() - carProductList.get(i).getSavings() == 0
                        && carFilteredByClass.get(carProductList.get(i).getCarCategoryCode()).getTotalPrice() - carProductList.get(i).getTotalPrice() > 0)
                {
                    carFilteredByClass.put(carProductList.get(i).getCarCategoryCode(), carProductList.get(i));
                }

            }
            else
            {
                carFilteredByClass.put(carProductList.get(i).getCarCategoryCode(), carProductList.get(i));
            }
        }

        List<PackageSearchFilterObject> result = getOutputAfterSortingAndFiltering(carFilteredByClass, CarCommonEnumManager.PackageSearchFilterType.Savings.name(), resultSetCount);
        return result;
    }

    //Get max margin car by class from ProductList
    public static List<PackageSearchFilterObject> getMaxMarginCarByClassFromProductList(List<PackageSearchFilterObject> carProductList, long resultSetCount)
    {
        final Map<Long, PackageSearchFilterObject> carFilteredByClass = new HashMap<>();
        carFilteredByClass.put(carProductList.get(0).getCarCategoryCode(), carProductList.get(0));
        for (final PackageSearchFilterObject carBeforeFilter : carProductList)
        {
            if (carFilteredByClass.containsKey(carBeforeFilter.getCarCategoryCode()))
            {
                if (carBeforeFilter.getMargin() > carFilteredByClass.get(carBeforeFilter.getCarCategoryCode()).getMargin() ||
                        (carBeforeFilter.getMargin() == carFilteredByClass.get(carBeforeFilter.getCarCategoryCode()).getMargin()
                                && carBeforeFilter.getTotalPrice() < carFilteredByClass.get(carBeforeFilter.getCarCategoryCode()).getTotalPrice()))
                {
                    carFilteredByClass.put(carBeforeFilter.getCarCategoryCode(), carBeforeFilter);
                }

            }
            else
            {
                carFilteredByClass.put(carBeforeFilter.getCarCategoryCode(), carBeforeFilter);
            }
        }
        return getOutputAfterSortingAndFiltering(carFilteredByClass, CarCommonEnumManager.PackageSearchFilterType.Margin.name(), resultSetCount);

    }

    private static List<PackageSearchFilterObject> getOutputAfterSortingAndFiltering(Map<Long, PackageSearchFilterObject> carFilteredByClass, String type, long resultSetCount)
    {
        final List<PackageSearchFilterObject> result = new ArrayList<>();
        final Set<Map.Entry<Long, PackageSearchFilterObject>> set = carFilteredByClass.entrySet();
        final List<Map.Entry<Long, PackageSearchFilterObject>> list = new ArrayList<>(set);
        Collections.sort(list, new Comparator<Map.Entry<Long, PackageSearchFilterObject>>()
        {
            @Override
            public int compare(Map.Entry<Long, PackageSearchFilterObject> o1, Map.Entry<Long, PackageSearchFilterObject> o2) {
                if (CarCommonEnumManager.PackageSearchFilterType.Savings.name().equals(type))
                {
                    return Double.compare(o1.getValue().getSavings(),o2.getValue().getSavings());
                }
                return Double.compare(o1.getValue().getTotalPrice(),o2.getValue().getTotalPrice()); // For all other cases
            }
        } );
        //If ResultSetCount = n >= 1, it will return n car for alll carType
        if (resultSetCount >=1 && list.size() >= resultSetCount)
        {
            for (int i = 0; i < resultSetCount; i++)
            {
                result.add(list.get(i).getValue());
            }
            //For the last car which should be returned, if there is one more car with same margin, get the car with higher car type if total price is the same or less
            if (CarCommonEnumManager.PackageSearchFilterType.Margin.name().equals(type))
            {
                if (list.size() > resultSetCount && list.get((int)resultSetCount).getValue().getMargin() == list.get((int)resultSetCount -1).getValue().getMargin())
                {
                    if (list.get((int)resultSetCount).getValue().getTotalPrice() <= list.get((int)resultSetCount-1).getValue().getTotalPrice())
                    {
                        result.add(((int)resultSetCount-1),list.get((int)resultSetCount).getValue());
                    }
                }
            }
            else if (CarCommonEnumManager.PackageSearchFilterType.Savings.name().equals(type))
            {
                if (list.size() > resultSetCount && list.get((int)resultSetCount).getValue().getSavings() == list.get((int)resultSetCount -1).getValue().getSavings())
                {
                    if (list.get((int)resultSetCount).getValue().getTotalPrice() <= list.get((int)resultSetCount-1).getValue().getTotalPrice())
                    {
                        result.add(((int)resultSetCount-1),list.get((int)resultSetCount).getValue());
                    }
                }
            }
        }
        //If ResultSetCount = -1, it will return one car for each carType
        else {
            for(final Map.Entry<Long, PackageSearchFilterObject> entry:list)
            {
                result.add(entry.getValue());
            }
        }
        return result;
    }
}

