package com.expedia.s3.cars.framework.test.common.splunkaccess.reporttool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by axiang on 3/23/2017.
 */
public class SplunkDataReportMapping {

    public Map<String, String> fieldMapping(){

        final Map<String, String> fieldMap = new HashMap<>();
        // carBS_Search
        fieldMap.put("com.expedia.s3.cars.ecommerce.carbs-Search", "CarBS Search");
        // CarSS Search
        fieldMap.put("com.expedia.s3.cars.supply.supplyservice-Search", "CarSS Search");
        // AmadeusSCS Search
        fieldMap.put("cars-supplyconnectivity-amadeus-service-Search", "AmadeusSCS Search");
        // WorldspanSCS Search
        fieldMap.put("cars-worldspan-supplyconnectivity-service-Search", "WorldspanSCS Search");
        // ExpediaSCS Search
        fieldMap.put("com.expedia.s3.cars.supplyconnectivity.expedia-Search", "ExpediaSCS Search");
        // MicronNexusSCS Search
        fieldMap.put("cars-supplyconnectivity-micronnexus-service-Search", "MicronNexusSCS Search");
        // TitaniumSCS Search
        fieldMap.put("cars-supplyconnectivity-titanium-service-Search", "TitaniumSCS Search");
        // CarBS GetDetails
        fieldMap.put("com.expedia.s3.cars.ecommerce.carbs-GetDetails", "CarBS GetDetails");
        // CarSS GetDetails
        fieldMap.put("com.expedia.s3.cars.supply.supplyservice-GetDetails", "CarSS GetDetails");
        // AmadeusSCS GetDetails
        fieldMap.put("cars-supplyconnectivity-amadeus-service-GetDetails", "AmadeusSCS GetDetails");
        //WorldspanSCS GetDetails
        fieldMap.put("cars-worldspan-supplyconnectivity-service-GetDetails", "WorldspanSCS GetDetails");
        //MicronNexusSCS GetDetails
        fieldMap.put("cars-supplyconnectivity-micronnexus-service-GetDetails", "MicronNexusSCS GetDetails");
        // TitaniumSCS GetDetails
        fieldMap.put("cars-supplyconnectivity-titanium-service-GetDetails", "TitaniumSCS GetDetails");
        // CarBS GetCostAndAvail
        fieldMap.put("com.expedia.s3.cars.ecommerce.carbs-GetCostAndAvailability", "CarBS GetCostAndAvail");
        // CarSS GetCostAndAvail
        fieldMap.put("com.expedia.s3.cars.supply.supplyservice-GetCostAndAvailability", "CarSS GetCostAndAvail");
        // AmadeusSCS GetCostAndAvail
        fieldMap.put("cars-supplyconnectivity-amadeus-service-GetCostAndAvailability", "AmadeusSCS GetCostAndAvail");
        // WorldspanSCS GetCostAndAvail
        fieldMap.put("cars-worldspan-supplyconnectivity-service-GetCostAndAvailability", "WorldspanSCS GetCostAndAvail");
        // MicronNexusSCS GetCostAndAvail
        fieldMap.put("cars-supplyconnectivity-micronnexus-service-GetCostAndAvailability", "MicronNexusSCS GetCostAndAvail");
        // TitaniumSCS GetCostAndAvail
        fieldMap.put("cars-supplyconnectivity-titanium-service-GetCostAndAvailability", "TitaniumSCS GetCostAndAvail");

        return fieldMap;
    }
}
