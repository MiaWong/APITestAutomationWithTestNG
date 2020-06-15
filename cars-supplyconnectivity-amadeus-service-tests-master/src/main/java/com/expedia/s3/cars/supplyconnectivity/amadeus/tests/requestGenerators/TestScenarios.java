package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators;

/**
 * Created by mpaudel on 5/20/16.
 */
public enum  TestScenarios
{
    // search scenarios
    GBR_Standalone_RoundTrip_OnAirport("GBR", "10116", "6095", "GBP", "LHR", "LHR", true, 128, 1, 6, "MAN"),
    GBR_Standalone_RoundTrip_OnAirport_LAX("GBR","10116","6095","GBP","LAX","LAX",true,128,1,6,"MAN"),
    ESP_Standalone_RoundTrip_OnAirport("ESP","10116","6060","EUR","BCN","BCN",true,128,1,6,"BCN"),
    ITA_Standalone_OneWay_OffAirport ("ITA","10116","6090","EUR","VCE","VCE",true,128,1,6,"TRN"),
    FRA_Standalone_RoundTrip_OffAirport("FRA","10116","6045","EUR","NCL","NCL",false,128,1,6,"BCN"),
    // location search scenarios
    HERTZ_LOCATION_SUPPLIER_SEARCH("GBR", "10116", "6095", null, 40),
    CDG_LOCATION_SEARCH("FRA", "10116", "6045", "CDG", 0);

    private final String jurisdictionCountryCode;
    private final String companyCode;
    private final String managementUnitCode;
    private final String supplierCurrencyCode;
    private final String pickupLocationCode;
    private final String dropOffLocationCode;
    private final String backupPickupLocationList;
    private final boolean isOnAirport;
    private final int purchaseTypeMask;
    private final int businessModel;
    private final int serviceProviderID;
    private final int supplierID;


    TestScenarios(String jurisdictionCountryCode,
            String companyCode,
            String managementUnitCode,
            String supplierCurrencyCode,
            String pickupLocationCode,
            String dropOffLocationCode,
            boolean isOnAirPort,
            int purchaseTypeMask,
            int businessModel,
            int serviceProviderID,
            String backupPickupLocationList)
    {
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.supplierCurrencyCode = supplierCurrencyCode;
        this.pickupLocationCode = pickupLocationCode;
        this.dropOffLocationCode = dropOffLocationCode;
        this.isOnAirport = isOnAirPort;
        this.purchaseTypeMask = purchaseTypeMask;
        this.businessModel = businessModel;

        this.serviceProviderID = serviceProviderID;
        this.backupPickupLocationList = backupPickupLocationList;
        this.supplierID = 40;
    }

    TestScenarios(String jurisdictionCountryCode,
                  String companyCode,
                  String managementUnitCode,
                  String pickupLocationCode,
                  int supplierID)
    {
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.supplierCurrencyCode = null;
        this.pickupLocationCode = pickupLocationCode;
        this.dropOffLocationCode = null;
        this.isOnAirport = true;
        this.purchaseTypeMask = 128;
        this.businessModel = 1;
        this.serviceProviderID = 6;
        this.backupPickupLocationList = null;
        this.supplierID = supplierID;
    }

    public String getCompanyCode()
    {
        return companyCode;
    }

    public String getJurisdictionCountryCode()
    {
        return jurisdictionCountryCode;
    }

    public String getManagementUnitCode()
    {
        return managementUnitCode;
    }

    public String getSupplierCurrencyCode()
    {
        return supplierCurrencyCode;
    }

    public String getPickupLocationCode()
    {
        return pickupLocationCode;
    }

    public String getDropOffLocationCode()
    {
        return dropOffLocationCode;
    }

    public String getBackup_PickupLocationList()
    {
        return backupPickupLocationList;
    }

    public boolean isOnAirPort()
    {
        return isOnAirport;
    }

    public int getPurchaseTypeMask()
    {
        return purchaseTypeMask;
    }

    public int getBusinessModel()
    {
        return businessModel;
    }

    public int getServiceProviderID()
    {
        return serviceProviderID;
    }

    public int getSupplierID()
    {
        return supplierID;
    }
}

