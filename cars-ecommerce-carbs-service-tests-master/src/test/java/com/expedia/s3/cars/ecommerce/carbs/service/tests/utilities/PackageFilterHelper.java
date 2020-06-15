package com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities;

import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;

public class PackageFilterHelper {

    private static boolean isSecondTime;
    private PackageFilterHelper()
    {

    }

    public static void tfs452726PackageCheapestFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("Cheapest");
            request.getCarECommerceSearchStrategy().setResultSetCount(1L);
            request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).setPassengerCount(4L);

        }
        else
        {
            request.setOptimizationStrategyCode("4");
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }

    public static void tfs452891PackageNumericFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("3");
            request.getCarECommerceSearchStrategy().setResultSetCount(1L);
        }
        else
        {
            request.setOptimizationStrategyCode("3");
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }

    public static void tfs453786PackageSavingFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("Savings");
            request.getCarECommerceSearchStrategy().setResultSetCount(-1L);
            request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).setPassengerCount(2L);

        }
        else
        {
            request.setOptimizationStrategyCode("2");
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }

    public static void tfs453731PackageMarginFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("Margin");
            request.getCarECommerceSearchStrategy().setResultSetCount(1L);
            request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).setPassengerCount(4L);

        }
        else
        {
            request.setOptimizationStrategyCode("4");
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }

    public static void tfs452875PackageCheapestFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("Cheapest");
            request.getCarECommerceSearchStrategy().setResultSetCount(5L);
        }
        else
        {
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }

    public static void tfs453718PackageCapacityFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("Capacity");
            request.getCarECommerceSearchStrategy().setResultSetCount(3L);
            request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).setPassengerCount(3L);

        }
        else
        {
            request.setOptimizationStrategyCode("3");
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }

    public static void tfs453696PackageOldCheapestNbestFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.setOptimizationStrategyCode("4");
            request.getCarECommerceSearchStrategy().setResultSetCount(-1L);
            request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(false);
            request.setClientCode("ZCS52L");
        }
        else
        {
            request.setOptimizationStrategyCode("4");
            request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
            request.getAuditLogTrackingData().setAuditLogForceDownstreamTransaction(true);
            isSecondTime = true;
        }
    }
    public static void tfs453702PackageOldCheapestNoOptFilter(CarECommerceSearchRequestType request)
    {
        if (isSecondTime)
        {
            request.getCarECommerceSearchStrategy().setResultSetCount(-1L);
            request.setClientCode("ZCS52L");
        }
        else
        {
            isSecondTime = true;
        }
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
    }
}
