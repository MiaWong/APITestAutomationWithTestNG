package com.expedia.s3.cars.framework.test.common;

/**
 * Created by sswaminathan on 8/5/16.
 */
public class TestGroup
{
    private TestGroup()
    {
        //nop
    }

    public static final String BVT = "bvt";
    public static final String SHOPPING_BVT = "shoppingbvt";
    public static final String REGRESSION = "regression";
    public static final String SHOPPING_REGRESSION = "shoppingRegression";
    public static final String BOOKING_REGRESSION = "bookingRegression";
    public static final String BOOKING_REGRESSION_AMADEUS = "bookingRegressionAmadeus";

    public static final String PERFMETRICS_REGRESSION = "perfmetricsRegression";

    /**
     * LIVE is a subset of the test cases; only those that don't require overrides, or transactions from spoofer can also be run against live.
     * Mark all such test cases as "live_shopping" or "live_booking"
     */
    public static final String SHOPPING_REGRESSION_LIVE = "shoppingRegressionLive";
    public static final String BOOKING_REGRESSION_LIVE = "bookingRegressionLive";


}
