package com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * Created by sswaminathan on 8/8/16.
 */
public class SearchRequestGeneratorTests
{
    @Test
    public void testSearchRequestGenerationWorks()
    {
        final CarSupplyConnectivitySearchRequestType searchRequest = SearchRequestGenerator.createSearchRequest(CommonScenarios.Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(), "1234");

        Assert.notNull(searchRequest);
    }
}
