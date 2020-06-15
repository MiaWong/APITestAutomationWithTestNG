package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.v5;

import com.expedia.cars.schema.common.v1.CarOfferContext;
import com.expedia.cars.schema.common.v1.ProductCategoryCodeList;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarClassificationIDList;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarTransportationSegment;
import com.expedia.cars.schema.ecommerce.shopping.v1.RentalOptions;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchCriteria;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchCriteriaList;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchStrategy;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.v5.common.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SuppressWarnings("PMD")
public class SearchRequestGenerator
{
    private static final String MESSAGE_NAME = "CarSearchRequest";

    public static CarSearchRequest createSearchRequest(TestData testData, String userGUID)
    {
        final String currencyCode = testData.getScenarios().getSupplierCurrencyCode();
        final CarSearchRequest searchRequest = new CarSearchRequest();

        searchRequest.setMessageInfo(CommonRequestGenerator.createMessageInfo(
                MESSAGE_NAME, userGUID, testData));
        searchRequest.setTraceInfo(CommonRequestGenerator.createTraceInfo());
        searchRequest.setSearchStrategy(createSearchStrategy(currencyCode));
        searchRequest.setSearchCriteriaList(createSearchCriteriaList(testData));

        return searchRequest;
    }

    public static SearchStrategy createSearchStrategy(String currencyCode)
    {
        final SearchStrategy searchStrategy = new SearchStrategy();
        searchStrategy.setRequestedCurrencyCode(currencyCode);
        searchStrategy.setPopulateReferencePrices(false);
        searchStrategy.setPopulateUpgradeMap(true);

        return searchStrategy;
    }

    public static SearchCriteriaList createSearchCriteriaList(TestData testData)
    {
        final SearchCriteriaList searchCriteriaList = new SearchCriteriaList();

        //for on airport car request
        if (testData.getScenarios().isOnAirPort())
        {
            final CarTransportationSegment carTransportationSegment =
                    CommonRequestGenerator.createCarTransportationSegment(testData);
            final SearchCriteria searchCriteria = createSearchCriteria(carTransportationSegment, testData);
            searchCriteriaList.getSearchCriteria().add(searchCriteria);
        }

        return searchCriteriaList;
    }

    public static SearchCriteria createSearchCriteria(CarTransportationSegment carTransportationSegment,
                                                      TestData testData)
    {
        final SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCarTransportationSegment(carTransportationSegment);
        final CarClassificationIDList carClassificationIDList = createCarClassificationIDList(Arrays.asList(1l));
        searchCriteria.setCarClassificationIDList(carClassificationIDList);
        final CarOfferContext carOfferContext = createCarOfferContext(testData);
        searchCriteria.setCarOfferContext(carOfferContext);

        final RentalOptions rentalOptions = new RentalOptions();
        rentalOptions.setSmoking(false);
        rentalOptions.setPrePaidFuel(false);
        rentalOptions.setUnlimitedMileage(false);
        searchCriteria.setRentalOptions(rentalOptions);


        return searchCriteria;
    }

    public static CarClassificationIDList createCarClassificationIDList(List<Long> carClassifcationIDs)
    {
        final CarClassificationIDList carClassificationIDList = new CarClassificationIDList();
        carClassificationIDList.getCarClassificationID().addAll(carClassifcationIDs);
        return carClassificationIDList;
    }

    public static CarOfferContext createCarOfferContext(TestData testData)
    {
        final CarOfferContext carOfferContext = new CarOfferContext();

        final CarOfferContext.BundlingInfo bundlingInfo = new CarOfferContext.BundlingInfo();
        bundlingInfo.setHotelPurchaseOption("1");
        bundlingInfo.setCrossSell(false);

        final ProductCategoryCodeList productCategoryCodeList = new ProductCategoryCodeList();
        productCategoryCodeList.getProductCategoryCode().addAll(
                getProductCategoryCodeList(testData.getScenarios().getPurchaseType().getPurchaseTypeMask()));
        bundlingInfo.setProductCategoryCodeList(productCategoryCodeList);
        bundlingInfo.setPackage(getPackageBoolean(testData.getScenarios().getPurchaseType().getPurchaseTypeMask()));
        carOfferContext.setBundlingInfo(bundlingInfo);

        carOfferContext.setPointOfSaleKey(CommonRequestGenerator.createPointOfSaleKey(testData.getScenarios()));
        carOfferContext.setPastPurchaseList(null);
        carOfferContext.setTargetTotalPrice(null);
        carOfferContext.setUserEntryPointQualifiers(null);
        carOfferContext.setUserGroupQualifiers(null);

        return carOfferContext;
    }

    public static Boolean getPackageBoolean(int purchaseTypeMask)
    {
        if (PurchaseType.FCPackage.getPurchaseTypeMask() == purchaseTypeMask
                || PurchaseType.FHCPackage.getPurchaseTypeMask() == purchaseTypeMask
                || PurchaseType.HCPackage.getPurchaseTypeMask() == purchaseTypeMask
                || PurchaseType.TCPackage.getPurchaseTypeMask() == purchaseTypeMask
                || PurchaseType.THCPackage.getPurchaseTypeMask() == purchaseTypeMask)
        {
            return true;
        }

        return false;
    }

    public static List<String> getProductCategoryCodeList(int purchaseTypeMask)
    {
        final List<String> productCategoryCodeList = new ArrayList<>();
        productCategoryCodeList.add("Car");

        if (PurchaseType.FCPackage.getPurchaseTypeMask() == purchaseTypeMask)
        {
            productCategoryCodeList.add("Air");
        }
        else if (PurchaseType.FHCPackage.getPurchaseTypeMask() == purchaseTypeMask)
        {
            productCategoryCodeList.add("Air");
            productCategoryCodeList.add("Hotel");
        }
        else if (PurchaseType.HCPackage.getPurchaseTypeMask() == purchaseTypeMask)
        {
            productCategoryCodeList.add("Hotel");
        }
        else if (PurchaseType.TCPackage.getPurchaseTypeMask() == purchaseTypeMask)
        {
            productCategoryCodeList.add("Train");
        }
        else if (PurchaseType.THCPackage.getPurchaseTypeMask() == purchaseTypeMask)
        {
            productCategoryCodeList.add("Train");
            productCategoryCodeList.add("Hotel");
        }

        return productCategoryCodeList;
    }
}
