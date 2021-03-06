package com.expedia.s3.cars.framework.test.common.execution.scenarios;

import java.math.BigDecimal;

/**
 * Created by sswaminathan on 8/4/16.
 */
public enum CommonScenarios
{
    Worldspan_US_GDSP_Package_USLocation_OnAirport(new TestScenario("Worldspan_US_GDSP_Package_USLocation_OnAirport",
            "USA","10111","1010","USD","SEA","SEA", true, PurchaseType.HCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_GDSP_FHCPackage_USLocation_OnAirport(new TestScenario("Worldspan_US_GDSP_FHCPackage_USLocation_OnAirport",
            "USA","10111","1010","USD","JFK","JFK", true, PurchaseType.FHCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_GDSP_HCPackage_nonUSLocation_OnAirport(new TestScenario("Worldspan_US_GDSP_HCPackage_nonUSLocation_OnAirport",
            "USA","10111","1010","USD","YVR","YVR", true, PurchaseType.HCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_Agency_Standalone_USLocation_OnAirport(new TestScenario("Worldspan_US_Agency_Standalone_USLocation_OnAirport",
            "USA","10111","1010","USD","SEA","SEA", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Package_USLocation_OnAirport(new TestScenario("Worldspan_US_Agency_Package_USLocation_OnAirport",
            "USA","10111","1010","USD","SEA","SEA", true, PurchaseType.HCPackage, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_FHCBundle_nonUSLocation_OnAirport(new TestScenario("Worldspan_US_Agency_FHCBundle_nonUSLocation_OnAirport",
            "USA","10111","1010","USD","LHR","LHR", true, PurchaseType.FHCBundle, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_HCBundle_USLocation_OnAirport(new TestScenario("Worldspan_US_Agency_HCBundle_USLocation_OnAirport",
            "USA","10111","1010","USD","SEA","SEA", true, PurchaseType.HCBundle, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_GDSP_Package_USLocation_OnAirport_EAN(new TestScenario("Worldspan_US_GDSP_Package_USLocation_OnAirport_EAN",
            "USA","10112","3050","USD","JFK","JFK", true, PurchaseType.HCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_Agency_Standalone_WithCC_OnAirport(new TestScenario("Worldspan_US_Agency_Standalone_WithCC_OnAirport",
            "USA","10111","1010","USD","MIA","MIA", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Bundle_OnAirport(new TestScenario("Worldspan_US_Agency_Standalone_WithCC_OnAirport",
            "USA","10111","1010","USD","MIA","MIA", true, PurchaseType.FCBundle, BusinessModel.Agency.getValue(),1)),
    Worldspan_UK_Merchant_Package_OnAirport(new TestScenario("Worldspan_US_Agency_Standalone_WithCC_OnAirport",
            "GBR","10111","1050","GBP","MEX","MEX", true, PurchaseType.FHCPackage, BusinessModel.Merchant.getValue(),1)),
    Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport(new TestScenario("Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport",
            "GBR","10111","1050","GBP","LHR","LHR", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_UK_GDSP_Standalone_UKLocation_PMI_OnAirport(new TestScenario("Worldspan_UK_GDSP_Package_UKLocation_OnAirport",
            "GBR","10111","1050","GBP","PMI","PMI", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_UK_GDSP_Package_UKLocation_OnAirport(new TestScenario("Worldspan_UK_GDSP_Package_UKLocation_OnAirport",
            "GBR","10111","1050","GBP","LHR","LHR", true, PurchaseType.FCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport(new TestScenario("Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport",
            "GBR","10111","1050","GBP","JFK","JFK", true, PurchaseType.FCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_UK_GDSP_FHCPackage_nonUKLocation_OnAirport(new TestScenario("Worldspan_UK_GDSP_FHCPackage_nonUKLocation_OnAirport",
            "GBR","10111","1050","GBP","JFK","JFK", true, PurchaseType.FHCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport(new TestScenario("Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport",
            "GBR","10111","1050","GBP","LHR","LHR", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),

    Worldspan_UK_Merchant_Package_nonUKLocation_OnAirport(new TestScenario("Worldspan_UK_Merchant_Package_nonUKLocation_OnAirport",
            "GBR","10111","1050","GBP","AGP","AGP", true, PurchaseType.FCPackage, BusinessModel.Merchant.getValue(),1)),

    Worldspan_UK_GDSP_Standalone_nonUKLocation_OneWay_OnAirport(new TestScenario("Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport",
            "GBR","10111","1050","GBP","SEA","SFO", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),

    Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport(new TestScenario("Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport",
            "GBR","10111","1050","GBP","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.Merchant.getValue(),1)),
    Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport(new TestScenario("Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport",
            "USA","10111","1010","USD","LHR","LHR", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport(new TestScenario("Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport",
            "USA","10111","1010","USD","LAS","SFO", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_UK_Merchant_Standalone_nonUKLocation_OffAirport(new TestScenario("Worldspan_UK_Merchant_Standalone_nonUKLocation_OffAirport",
            "GBR","10111","1050","GBP","AGP","AGP", false, PurchaseType.CarOnly, BusinessModel.Merchant.getValue(),1)),
    Worldspan_US_Agency_Standalone_USLocation_OffAirport(new TestScenario("Worldspan_US_Agency_Standalone_USLocation_OffAirport",
            "USA","10111","1010","USD","SEA","SEA", false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway(new TestScenario("Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway",
            "USA","10111","1010","USD","LHR","EDI", false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport_Oneway(new TestScenario("Worldspan_US_Agency_Standalone_USLocation_OffAirport",
            "GBR","10111","1050","GBP","LHR","MAN", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_CA_Agency_Standalone_MEX_OnAirport(new TestScenario("Worldspan_CA_Agency_Standalone_MEX_OnAirport",
            "CAN","10111","1040","CAD","MEX","MEX", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_FR_GDSP_Standalone_nonFRLocation_OnAirport(new TestScenario("Worldspan_FR_GDSP_Standalone_nonFRLocation_OnAirport",
            "FRA","10111","1060","EUR","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_FR_Merchant_Standalone_nonFRLocation_OnAirport(new TestScenario("Worldspan_FR_Merchant_Standalone_nonFRLocation_OnAirport",
            "FRA","10111","1060","EUR","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.Merchant.getValue(),1)),
    Worldspan_MX_Agency_Standalone_OnAirport_oneway(new TestScenario("Worldspan_MX_Agency_Standalone_OffAirport_oneway",
            "MEX","10111","1230","CAD","LAS","EDI", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_GBR_Agency_Standalone_OnAirport_oneway(new TestScenario("Worldspan_GBR_Agency_Standalone_OnAirport_oneway",
            "GBR","10111","1050","GBP","LAS","EDI", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_GBR_GDSP_Standalone_OnAirport_oneway(new TestScenario("Worldspan_GBR_Agency_Standalone_OnAirport_oneway",
            "GBR","10111","1050","GBP","LAS","EDI", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_FR_GDSP_Standalone_nonFRLocation_oneway(new TestScenario("Worldspan_FR_GDSP_Standalone_nonFRLocation_oneway",
            "FRA","10111","1060","EUR","LAS","LAX", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_FR_Agency_Standalone_nonFRLocation_oneway(new TestScenario("Worldspan_FR_Agency_Standalone_nonFRLocation_oneway",
            "FRA","10111","1060","EUR","LAS","LAX", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_IND_10111_1185_Agency_Standalone_OnAirport(new TestScenario("Worldspan_IND_10111_1185_Agency_Standalone_OnAirport",
            "IND","10111","1185","INR","LAS","LAS", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_CA_Agency_Standalone_CALocation_OnAirport(new TestScenario("Worldspan_CA_Agency_Standalone_CALocation_OnAirport",
            "CAN","10111","1040","CAD","YVR","YVR", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_CA_Agency_Standalone_MCO_LAX_OnAirport(new TestScenario("Worldspan_CA_Agency_Standalone_MCO_LAX_OnAirport",
            "CAN","10111","1040","CAD","MCO","LAX", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_CA_GDSP_Package_USLocation_OnAirport(new TestScenario("Worldspan_CA_GDSP_Package_USLocation_OnAirport",
            "CAN","10111","1040","CAD","JFK","JFK", true, PurchaseType.FHCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport(new TestScenario("Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport",
            "FRA","10111","1060","EUR","SEA","SEA", true, PurchaseType.FHCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_BRA_1161_GDSP_Standalone_OnAirport(new TestScenario("Worldspan_BRA_1161_GDSP_Standalone_OnAirport",
            "BRA","10111","1161","USD","USA","USA", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_Travelocity_CA_GDSP_Standalone_nonCALocation_OffAirport(new TestScenario("Worldspan_Travelocity_CA_GDSP_Standalone_nonCALocation_OffAirport",
            "CAN","10111","1256","CAD","YVR","YVR", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_DEU_10111_1055_GDSP_Standalone_OnAirport(new TestScenario("Worldspan_DEU_10111_1055_GDSP_Standalone_OnAirport",
            "DEU","10111","1055","EUR","SEA","SEA", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_FR_Merchant_Package_nonFRLocation_OnAirport(new TestScenario("Worldspan_FR_Merchant_Package_nonFRLocation_OnAirport",
            "FRA","10111","1060","EUR","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.Merchant.getValue(),1)),
    Worldspan_AUT_1070_Merchant_Package_OnAirport(new TestScenario("Worldspan_AUT_1070_Merchant_Package_OnAirport",
            "AUT","10111","1070","EUR","INN","INN", true, PurchaseType.FHCPackage, BusinessModel.Merchant.getValue(),1)),
    Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport_Oneway(new TestScenario("Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport_Oneway",
            "GBR","10111","1050","GBP","PMI","AGP", true, PurchaseType.CarOnly, BusinessModel.Merchant.getValue(),1)),
    Worldspan_DE_Merchant_Standalone_OnAirport(new TestScenario("Worldspan_DE_Merchant_Standalone_OnAirport",
            "DEU","10111","1055","EUR","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.Merchant.getValue(),1)),
    Worldspan_CA_GDSP_Standalone_CALocation_OnAirport(new TestScenario("Worldspan_CA_GDSP_Standalone_CALocation_OnAirport",
            "CAN","10111","1040","CAD","YVR","YVR", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_CA_GDSP_Package_CALocation_OnAirport(new TestScenario("Worldspan_CA_GDSP_Package_CALocation_OnAirport",
            "CAN","10111","1040","CAD","YVR","YVR", true, PurchaseType.HCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_GDSP_Package_nonUSLocation_OnAirport(new TestScenario("Worldspan_US_GDSP_Package_nonUSLocation_OnAirport",
            "USA","10111","1010","USD","YVR","YVR", true, PurchaseType.FCPackage, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_Agency_Standalone_USLocation_OffAirport_oneway(new TestScenario("Worldspan_US_Agency_Standalone_USLocation_OffAirport_oneway",
            "USA","10111","1010","USD","SEA","LAS", false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway(new TestScenario("Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway",
            "GBR","10111","1050","GBP","SFO","SEA", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_UK_GDSP_Standalone_nonUKLocation_OffAirport(new TestScenario("Worldspan_UK_GDSP_Standalone_nonUKLocation_OffAirport",
            "GBR","10111","1050","GBP","MEX","MEX", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_CA_GDSP_Standalone_nonCALocation_OnAirport(new TestScenario("Worldspan_CA_GDSP_Standalone_nonCALocation_OnAirport",
            "CAN","2","0","CAD","LHR","LHR", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Standalone_USLocation_OnAirport_oneway(new TestScenario("Worldspan_US_Agency_Standalone_USLocation_OnAirport_oneway",
            "USA","10111","1010","USD","SEA","LAS", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport_oneway(new TestScenario("Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport_oneway",
            "USA","10111","1010","USD","LHR","EDI", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Bandle_LAS_OnAirport(new TestScenario("Worldspan_US_Agency_Bandle_LAS_OnAirport",
            "USA","10111","1010","USD","LAS","LAS", true, PurchaseType.FCBundle, BusinessModel.Agency.getValue(),1)),
    Worldspan_US_Agency_Standalone_LAS_OnAirport(new TestScenario("Worldspan_US_Agency_Bandle_LAS_OnAirport",
            "USA","10111","1010","USD","LAS","LAS", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_Travelocity_US_Agency_Standalone_USLocation_OnAirport_OneWay(new TestScenario("Worldspan_Travelocity_US_Agency_Standalone_USLocation_OnAirport_OneWay",
            "USA","10111","1255","CAD","SEA","SFO", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_Travelocity_CA_Agency_Standalone_CALocation_OnAirport_OneWay(new TestScenario("Worldspan_Travelocity_CA_Agency_Standalone_CALocation_OnAirport_OneWay",
            "CAN","10111","1256","CAD","YYZ","YVR", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_Travelocity_CA_Agency_Standalone_nonCALocation_OnAirport_Roundtrip(new TestScenario("Worldspan_Travelocity_CA_Agency_Standalone_nonCALocation_OnAirport_Roundtrip",
            "CAN","10111","1256","CAD","SEA","SEA", true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1)),
    Worldspan_GBR_10111_1012_GDSP_Standalone_OffAirport(new TestScenario("Worldspan_Travelocity_CA_Agency_Standalone_CALocation_OnAirport_OneWay",
            "GBR","10111","1012","GBP","LHR","LHR", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1)),
    Worldspan_US_Agency_Standalone_US_LatLong_oneway(new TestScenario("Worldspan_US_Agency_Standalone_US_LatLong_oneway",
            "USA", "10111", "1010", "USD", new BigDecimal("47.4428209"), new BigDecimal("-122.2988014"), 10, false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1 )),
    Worldspan_GBR_GDSP_Standalone_UK_LatLong_oneway(new TestScenario("Worldspan_GBR_GDSP_Standalone_UK_LatLong_oneway",
            "GBR", "10111", "1050", "GBP", new BigDecimal("55.943330"), new BigDecimal("-3.358280"), 10, false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),1 )),
    Worldspan_US_Agency_Standalone_US_LatLong_startLocationIndex_LocationCount(new TestScenario("Worldspan_US_Agency_Standalone_US_LatLong_startLocationIndex_LocationCount",
            "USA", "10111", "1010", "USD", new BigDecimal("47.4428214"), new BigDecimal("-122.2988017"), 1, 3, 33, false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1 )),
    Worldspan_CA_Agency_Standalone_LatLong_oneway(new TestScenario("Worldspan_GBR_GDSP_Standalone_UK_LatLong_oneway",
            "CAN", "10111", "1040", "CAD", new BigDecimal("49.1948100"), new BigDecimal("-123.1778100"), 101, false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1 )),






    MN_GBR_Standalone_OneWay_OffAirport_AGP(new TestScenario(" MN_GBR_Standalone_OneWay_OffAirport_AGP",
            "GBR","10111","1050","GBP","AGP","ALC", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Standalone_RoundTrip_OnAirport_AGP(new TestScenario("MN_GBR_Standalone_RoundTrip_OnAirport_AGP",
            "GBR","10111","1050","GBP","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Standalone_RoundTrip_OnAirport_PMO(new TestScenario("MN_GBR_Standalone_RoundTrip_OnAirport_PMO",
            "GBR","10111","1050","GBP","PMO","PMO", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Standalone_RoundTrip_OnAirport_LHR(new TestScenario("MN_GBR_Standalone_RoundTrip_OnAirport_LHR",
            "GBR","10111","1050","GBP","LHR","LHR", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Standalone_RoundTrip_OffAirport_LHR(new TestScenario("MN_GBR_Standalone_RoundTrip_OffAirport_LHR",
            "GBR","10111","1050","GBP","LHR","LHR", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Package_RoundTrip_OnAirport_LHR(new TestScenario("MN_GBR_Package_RoundTrip_OnAirport_LHR",
            "GBR","10111","1050","GBP","LHR","LHR", true, PurchaseType.HCPackage, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Package_RoundTrip_OnAirport_CDG(new TestScenario("MN_GBR_Package_RoundTrip_OnAirport_CDG",
            "GBR","10111","1050","GBP","CDG","CDG", true, PurchaseType.FHCPackage, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Standalone_Oneway_OnAirport_AGP(new TestScenario("MN_GBR_Standalone_Oneway_OnAirport_AGP",
            "GBR","10111","1050","GBP","AGP","PMI", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_FRA_Standalone_RoundTrip_OnAirport_CDG(new TestScenario("MN_FRA_Standalone_RoundTrip_OnAirport_CDG",
            "FRA","10111","1060","EUR","CDG","CDG", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_FRA_Standalone_RoundTrip_OnAirport_LHR(new TestScenario("MN_FRA_Standalone_RoundTrip_OnAirport_LHR",
            "FRA","10111","1060","EUR","LHR","LHR", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MicronNexus_FR_GDSP_Package_nonFRLocation_OnAirport(new TestScenario("MicronNexus_FR_GDSP_Package_nonFRLocation_OnAirport",
            "FRA","10111","1060","EUR","LHR","LHR", true, PurchaseType.FCPackage, BusinessModel.GDSP.getValue(), 3)),
    MN_FRA_Standalone_Oneway_OnAirport_AGP(new TestScenario("MN_FRA_Standalone_Oneway_OnAirport_AGP",
            "FRA","10111","1060","EUR","AGP","ALC", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_FRA_Standalone_RoundTrip_OffAirport_AGP(new TestScenario("MN_FRA_Standalone_RoundTrip_OffAirport_AGP",
            "FRA","10111","1060","EUR","AGP","AGP", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_ESP_Standalone_RoundTrip_OnAirport_PMO(new TestScenario("MN_ESP_Standalone_RoundTrip_OnAirport_PMO",
            "ESP","10111","1109","EUR","PMO","PMO", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_ESP_Standalone_RoundTrip_OnAirport_ACE(new TestScenario("MN_ESP_Standalone_RoundTrip_OnAirport_ACE",
            "ESP","10111","1109","EUR","ACE","ACE", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_ITA_Standalone_RoundTrip_OnAirport_PMO(new TestScenario("MN_ITA_Standalone_RoundTrip_OnAirport_PMO",
            "ITA","10111","1065","EUR","PMO","PMO", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_UK_GDSP_Package_nonUKLocation_OnAirport(new TestScenario("MN_UK_GDSP_Package_nonUKLocation_OnAirport",
            "GBR","10111","1050","GBP","AGP","AGP", true, PurchaseType.FCPackage, BusinessModel.GDSP.getValue(),3)),
    MN_UK_GDSP_Standalone_nonUKLocation_OnAirport(new TestScenario("MN_UK_GDSP_Standalone_nonUKLocation_OnAirport",
            "GBR","10111","1050","GBP","AGP","AGP", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),3)),
    MN_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR(new TestScenario("MN_GBR_Standalone_RoundTrip_OnAirport_LHR",
            "GBR","10116","6095","GBP","LHR","LHR",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),3)),
    MN_GBR_Standalone_RoundTrip_OnAirport_PMI(new TestScenario("MN_GBR_Standalone_RoundTrip_OnAirport_PMI",
            "GBR","10111","1050","GBP","PMI","PMI", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),
    MN_GBR_Standalone_RoundTrip_OffAirport_AGP(new TestScenario(" MN_GBR_Standalone_RoundTrip_OffAirport_AGP",
            "GBR","10111","1050","GBP","AGP","AGP", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 3)),



    Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS(new TestScenario("Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS",
            "FRA","10116","6045","EUR","LYS","LYS",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX",
            "GBR","10116","6095","GBP","LAX","LAX",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR",
            "GBR","10116","6095","GBP","LHR","LHR",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_US_Agency_Standalone_RoundTrip_OnAirport_LHR(new TestScenario("Amadeus_US_Agency_Standalone_RoundTrip_OnAirport_LHR",
            "USA","10115","6020","USD","LHR","LHR",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LCY(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LCY",
            "GBR","10116","6095","GBP","LCY","LCY",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_NCL(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_NCL",
            "GBR","10116","6095","GBP","NCL","NCL",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_BCN(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_BCN",
            "GBR","10116","6095","EUR","BCN","BCN",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_PAR(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_PAR",
            "GBR","10116","6095","GBP","PAR","PAR",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_CDG(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_CDG",
            "GBR","10116","6095","GBP","CDG","CDG",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_MAD(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_MAD",
            "ITA","10116","6090","EUR","MAD","MAD",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_LYS(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_LYS",
            "ITA","10116","6090","EUR","LYS","LYS",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_BCN(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_BCN",
            "ITA","10116","6090","EUR","BCN","BCN",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN(new TestScenario("Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN",
            "ESP","10116","6060","EUR","BCN","BCN",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG",
            "FRA","10116","6045","EUR","CDG","CDG",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_URO(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_URO",
            "FRA","10116","6045","EUR","URO","URO",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_NCL(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_NCL",
            "FRA","10116","6045","EUR","NCL","NCL",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS",
            "FRA","10116","6045","EUR","NCE","LYS",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI",
            "FRA","10116","6045","EUR","LHR","EDI",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_BCN_VCE(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_BCN_VCE",
            "FRA","10116","6045","EUR","BCN","VCE",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_EDI(new TestScenario("Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_EDI",
            "GBR","10116","6095","GBP","LHR","EDI",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_LCY(new TestScenario("Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_LCY",
            "GBR","10116","6095","GBP","LHR","LCY",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_OneWay_OnAirport_LYS_CDG(new TestScenario("Amadeus_ITA_Agency_Standalone_OneWay_OnAirport_LYS_CDG",
            "ITA","10116","6090","EUR","LYS","CDG",true, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_MAN(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_MAN",
            "GBR","10116","6095","GBP","MAN","MAN",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LHR(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LHR",
            "GBR","10116","6095","GBP","LHR","LHR",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC",
            "GBR","10116","6095","GBP","VLC","VLC",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LYS(new TestScenario("Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LYS",
            "GBR","10116","6095","GBP","LYS","LYS",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_VCE(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_VCE",
            "ITA","10116","6090","EUR","VCE","VCE",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_MAD(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_MAD",
            "ITA","10116","6090","EUR","MAD","MAD",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_VLC(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_VLC",
            "ITA","10116","6090","EUR","VLC","VLC",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCL(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCL",
            "FRA","10116","6045","EUR","NCL","NCL",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS",
            "FRA","10116","6045","EUR","LYS","LYS",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCE(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCE",
            "FRA","10116","6045","EUR","NCE","NCE",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG(new TestScenario("Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG",
            "FRA","10116","6045","EUR","CDG","CDG",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_MAN_EDI(new TestScenario("Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_MAN_EDI",
            "GBR","10116","6095","GBP","MAN","EDI",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_NCE_LYS(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_NCE_LYS",
            "FRA","10116","6045","EUR","NCE","LYS",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_NCE_MRS(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_NCE_MRS",
            "FRA","10116","6045","EUR","NCE","MRS",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_LYS_NCE(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_LYS_NCE",
            "FRA","10116","6045","EUR","LYS","NCE",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_CDG_PAR(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_CDG_PAR",
            "FRA","10116","6045","EUR","CDG","PAR",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG(new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG",
            "FRA","10116","6045","EUR","PAR","CDG",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_OneWay_OffAirport_MAD_VLC(new TestScenario("Amadeus_ITA_Agency_Standalone_OneWay_OffAirport_MAD_VLC",
            "ITA","10116","6090","EUR","MAD","VLC",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_OneWay_OffAirport_LYS_CDG(new TestScenario("Amadeus_ITA_Agency_Standalone_OneWay_OffAirport_LYS_CDG",
            "ITA","10116","6090","EUR","LYS","CDG",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_LYS(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_LYS",
            "ITA","10116","6090","EUR","LYS","LYS",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_BCN(new TestScenario("Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_BCN",
            "ITA","10116","6090","EUR","BCN","BCN",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_LHR_EDI(new TestScenario("Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_LHR_EDI",
            "GBR","10116","6095","GBP","LHR","EDI",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6)),
    Amadeus_GBR_Agency_Standalone_UK_LatLong_oneway(new TestScenario("Amadeus_GBR_Agency_Standalone_UK_LatLong_oneway",
            "GBR", "10111", "1050", "GBP", new BigDecimal("55.943330"), new BigDecimal("-3.358280"), 10, false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1 )),




    GBR_Standalone_RoundTrip_OnAirport_TiSCS( new TestScenario("GBR_Standalone_RoundTrip_OnAirport_TiSCS",
            "GBR", "10111", "1050", "GBP", "LHR", "LHR", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 7)),
    GBR_Standalone_RoundTrip_OnAirport_TiSCS_CDG( new TestScenario("GBR_Standalone_RoundTrip_OnAirport_TiSCS",
            "GBR", "10111", "1050", "GBP", "CDG", "CDG", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 7)),
    DEU_Standalone_OneWay_OnAirport_CERTTEST1_YOUNGDRIVER( new TestScenario("DEU_Standalone_OneWay_OnAirport_CERTTEST1_YOUNGDRIVER",
            "DEU","10111","1055","EUR","NCE","LYS",true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    FRA_Standalone_OneWay_OnAirport_CERTTEST2_SPECIALEQUIP( new TestScenario("FRA_Standalone_OneWay_OnAirport_CERTTEST2_SPECIALEQUIP",
            "GBR","10111","1050","GBP","LHR","MAN",true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    GBR_Standalone_OneWay_OnAirport_CERTTEST3(new TestScenario("GBR_Standalone_OneWay_OnAirport_CERTTEST3",
            "GBR","10111","1050","GBP","LHR","MAN",true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    TiSCS_GBR_Standalone_OneWay_OnAirport_CDG(new TestScenario("TiSCS_GBR_Standalone_OneWay_OnAirport_CDG",
            "GBR", "10111", "1050", "GBP", "CDG", "ORY", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 7)),
    TiSCS_GBR_Standalone_OneWay_OffAirport_CDG(new TestScenario("TiSCS_GBR_Standalone_OneWay_OffAirport_CDG",
            "GBR", "10111", "1050", "GBP", "CDG", "ORY", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 7)),
    TiSCS_FRA_Standalone_OneWay_OnAirport_CDG(new TestScenario("TiSCS_FRA_Standalone_OneWay_OnAirport_CDG",
            "FRA","10111","1060", "EUR", "CDG", "ORY", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 7)),
    TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR(new TestScenario("TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR",
            "GBR", "10111", "1050", "GBP", "LHR", "LHR", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 7)),
    TisSCS_FRA_Standalone_Roundtrip_OnAirport_CDG( new TestScenario("TisSCS_FRA_Standalone_Roundtrip_OnAirport_CDG",
            "FRA","10111","1060","EUR","CDG","CDG",true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR( new TestScenario("TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR",
            "FRA","10111","1060","EUR","LHR","LHR",true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR( new TestScenario("TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR",
            "FRA","10111","1060","EUR","LHR","LHR",false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    TisSCS_FRA_Package_Roundtrip_OnAirport_CDG( new TestScenario("TisSCS_FRA_Package_Roundtrip_OnAirport_CDG",
            "FRA","10111","1060","EUR","CDG","CDG",true, PurchaseType.HCPackage, BusinessModel.GDSP.getValue(),7)),
    GBR_Package_OneWay_OnAirport_YOUNGDRIVER( new TestScenario("DEU_Package_OneWay_OnAirport_YOUNGDRIVER",
            "GBR", "10111", "1050", "GBP", "LHR","LHR",true, PurchaseType.FCPackage, BusinessModel.GDSP.getValue(),7)),
    GBR_StandAlone_OneWay_OnAirport_YOUNGDRIVER( new TestScenario("DEU_StandAlone_OneWay_OnAirport_YOUNGDRIVER",
            "GBR", "10111", "1050", "GBP", "LHR","LHR",true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),
    TisSCS_GBR_Standalone_Roundtrip_OffAirport_LHR( new TestScenario("TisSCS_GBR_Standalone_Roundtrip_OffAirport_LHR",
            "GBR","10111","1050","GBP","LHR","LHR",false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(),7)),

    Sabre_CAN_Standalone_RoundTrip_OFFAirport_LAS( new TestScenario("Sabre_CAN_Standalone_RoundTrip_OFFAirport_LAS",
            "CAN", "10111", "4035", "CAD", "LAS", "LAS", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_CAN_Standalone_Oneway_OnAirport_LAS( new TestScenario("Sabre_CAN_Standalone_Oneway_OnAirport_LAS",
            "CAN", "10111", "4035", "CAD", "LAS", "SEA", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW( new TestScenario("Sabre_CAN_Standalone_RoundTrip_OnAirport_YOW",
            "CAN", "10111", "4035", "CAD", "YOW", "YOW", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC( new TestScenario("Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC",
            "CAN", "10111", "4035", "CAD", "YYC", "YYC", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_CAN_Standalone_Oneway_OnAirport_YYR( new TestScenario("Sabre_CAN_Standalone_Oneway_OnAirport_YYR",
            "CAN", "10111", "4035", "CAD", "YYC", "YYR", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_CAN_Standalone_Oneway_OnAirport_YOW( new TestScenario("Sabre_CAN_Standalone_Oneway_OnAirport_YOW",
            "CAN", "10111", "4035", "CAD", "YOW", "YYC", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_USA_Standalone_RoundTrip_OnAirport_LAS(new TestScenario("Sabre_USA_Standalone_RoundTrip_OnAirport_LAS",
            "USA", "10111", "4033", "USD", "LAS", "LAS", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_USA_Standalone_RoundTrip_OffAirport_LAS(new TestScenario("Sabre_USA_Standalone_RoundTrip_OffAirport_LAS",
            "USA", "10111", "4033", "USD", "LAS", "LAS", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_USA_Standalone_Oneway_OnAirport_LAS(new TestScenario("Sabre_USA_Standalone_Oneway_OnAirport_LAS",
            "USA", "10111", "4033", "USD", "LAS", "LAS", true, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8)),
    Sabre_USA_Standalone_Oneway_OffAirport_SEA(new TestScenario("Sabre_USA_Standalone_Oneway_OffAirport_SEA",
            "USA", "10111", "4033", "USD", "SEA", "LAS", false, PurchaseType.CarOnly, BusinessModel.GDSP.getValue(), 8));

    private final TestScenario testScenario;

    CommonScenarios(TestScenario testScenario)
    {
        this.testScenario = testScenario;
    }

    public TestScenario getTestScenario()
    {
        return testScenario;
    }
}
