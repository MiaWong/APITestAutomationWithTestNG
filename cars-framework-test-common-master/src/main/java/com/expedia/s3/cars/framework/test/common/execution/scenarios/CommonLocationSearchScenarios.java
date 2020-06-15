package com.expedia.s3.cars.framework.test.common.execution.scenarios;

import java.math.BigDecimal;

/**
 * Created by v-mechen on 9/6/2017.
 */
public enum CommonLocationSearchScenarios {

    Amadeus_FRA_LatLong_10M_DeliveryCollectionOutOfOfficeTrue_NeedDetail(new LocationSearchTestScenario("Amadeus_FRA_LatLong_10M_DeliveryCollectionOutOfOfficeTrue_NeedDetail",
            "FRA", "10116", "6045",  new BigDecimal("59.41355"), new BigDecimal("5.27678"), 10, "MI", "true", "true", null, "true", true, null)),

    Amadeus_FRA_LatLong_IATA(new LocationSearchTestScenario("Amadeus_FRA_LatLong_10M_DeliveryCollectionOutOfOfficeTrue_NeedDetail",
                                                                         "FRA", "10116", "6045",  new BigDecimal("59.41355"), new BigDecimal("5.27678"), 10, "MI", "true", "true", null, "true", true, "LHR")),

    Amadeus_ITA_IATA_NullSearchFilter_NoDetail(new LocationSearchTestScenario("Amadeus_ITA_IATA_NullSearchFilter_NoDetail",
            "ITA", "10116", "6090",  "MIL", null, null, null, null, false, true, true)),

    Amadeus_FRA_IATA_DEL_F_COL_F_OHR_F_VIC_F(new LocationSearchTestScenario("Amadeus_FRA_IATA_DEL_T_COL_F_OHR_T_VIC_T",
            "FRA", "10116", "6045",  "JFK", "false", "false", "false", "false", true, false, false)),

    Amadeus_FRA_IATA_DEL_T_COL_F_OHR_T_VIC_T(new LocationSearchTestScenario("Amadeus_FRA_IATA_DEL_T_COL_F_OHR_T_VIC_T",
            "FRA", "10116", "6045",  "JFK", "true", "false", "true", "true", true, false, false)),

    Amadues_ITA_LatLong_KM_DEL_T_COL_F_OHR_F(new LocationSearchTestScenario("Amadues_ITA_LatLong_KM_DEL_T_COL_F_OHR_F",
            "ITA", "10116", "6090", new BigDecimal("59.41355"), new BigDecimal("5.27678"), 5, "KM", "false", "true", "false", true,false)),

    Amadeus_FRA_LatLong_MI_NoFilter(new LocationSearchTestScenario("Amadeus_FRA_LatLong_MI","FRA","10116","6045", new BigDecimal("59.41355"), new BigDecimal("5.27678"),
            20, "MI", null, null, null, true,false)),

    US_100Mi(new LocationSearchTestScenario("US_100Mi", new BigDecimal("47.604543"), new BigDecimal("-122.332472"), 100, "MI", "false", "", "", false,true,false,true)),

    CarBS_Location_LatLong_MI(new LocationSearchTestScenario("CarBS_Location_LatLong_MI","FRA","10116","6045", new BigDecimal("59.41355"), new BigDecimal("5.27678"), 20, "MI", "true", "true", "true", true,false)),

    CarBS_Location_IATA_Airport_DELT_COLT_OHRT(new LocationSearchTestScenario("CarBS_Location_IATA_Airport_DELT_COLT_OHRT","FRA","10116","6045", "NCE", "", "true", "true", "true", true,false)),

    CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP(new LocationSearchTestScenario("CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP","FRA","10116","6045", "LHR","", "true", "true", "true", true,false)),

    CarBS_Location_LatLong_DELF_COLT_OHRT(new LocationSearchTestScenario("CarBS_Location_LatLong_DELF_COLT_OHRT","FRA","10116","6045", new BigDecimal("43.48012"), new BigDecimal("-1.52326"), 25, "MI", "false", "true", "true", true,false)),

    CarBS_Location_IATA_Airport_Vicinity_ON(new LocationSearchTestScenario("CarBS_Location_LatLong_DELF_COLT_OHRT","FRA","10116","6045", "NCE", "true", "", "", "", true,false));
    private final LocationSearchTestScenario testScenario;

    CommonLocationSearchScenarios(LocationSearchTestScenario testScenario)
    {
        this.testScenario = testScenario;
    }

    public LocationSearchTestScenario getTestScenario()
    {
        return testScenario;
    }

}
