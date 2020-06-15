package com.expedia.s3.cars.framework.test.common.execution.scenarios;

import java.math.BigDecimal;

/**
 * Created by sswaminathan on 8/4/16.
 */
public class TestScenario
{
    private final String scenarionName;
    private final String jurisdictionCountryCode;
    private final String companyCode;
    private final String managementUnitCode;
    private final String supplierCurrencyCode;
    private String pickupLocationCode;
    private String dropOffLocationCode;
    private final boolean onAirPort;
    private final PurchaseType purchaseType;
    private final int businessModel;
    private final int serviceProviderID;
    private BigDecimal latitude;
    private BigDecimal  longitude;
    private int radius;
    private long startLocationIndex;
    private long locationCount;

    public TestScenario(String scenarionName, String jurisdictionCountryCode,
                        String companyCode, String managementUnitCode,
                        String supplierCurrencyCode, String pickupLocationCode,
                        String dropOffLocationCode, boolean isOnAirPort,
                        PurchaseType purchaseType, int businessModel, int serviceProviderID)
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.supplierCurrencyCode = supplierCurrencyCode;
        this.pickupLocationCode = pickupLocationCode;
        this.dropOffLocationCode = dropOffLocationCode;
        this.onAirPort = isOnAirPort;
        this.purchaseType = purchaseType;
        this.businessModel = businessModel;
        this.serviceProviderID = serviceProviderID;
    }

    public TestScenario(String scenarionName, String jurisdictionCountryCode,
                        String companyCode, String managementUnitCode, String supplierCurrencyCode,
                        BigDecimal latitude, BigDecimal longitude, int radius, boolean isOnAirPort,
                        PurchaseType purchaseType, int businessModel, int serviceProviderID)
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.supplierCurrencyCode = supplierCurrencyCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.onAirPort = isOnAirPort;
        this.purchaseType = purchaseType;
        this.businessModel = businessModel;
        this.serviceProviderID = serviceProviderID;
    }

    public TestScenario(String scenarionName, String jurisdictionCountryCode,
                        String companyCode, String managementUnitCode, String supplierCurrencyCode,
                        BigDecimal latitude, BigDecimal longitude, int radius, long startLocationIndex,
                        long locationCount, boolean isOnAirPort, PurchaseType purchaseType,
                        int businessModel, int serviceProviderID)
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.supplierCurrencyCode = supplierCurrencyCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.startLocationIndex = startLocationIndex;
        this.locationCount = locationCount;
        this.onAirPort = isOnAirPort;
        this.purchaseType = purchaseType;
        this.businessModel = businessModel;
        this.serviceProviderID = serviceProviderID;
    }

    public String getScenarionName()
    {
        return scenarionName;
    }

    public String getJurisdictionCountryCode()
    {
        return jurisdictionCountryCode;
    }

    public String getCompanyCode()
    {
        return companyCode;
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

    public boolean isOnAirPort()
    {
        return onAirPort;
    }

    public PurchaseType getPurchaseType()
    {
        return purchaseType;
    }

    public int getBusinessModel()
    {
        return businessModel;
    }

    public int getServiceProviderID()
    {
        return serviceProviderID;
    }

    public boolean isOneWay()
    {
        return this.pickupLocationCode != this.dropOffLocationCode;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public int getRadius()
    {
        return radius;
    }

    public long getStartLocationIndex()
    {
        return startLocationIndex;
    }

    public long getLocationCount()
    {
        return locationCount;
    }

    public boolean isStandalone()
    {
        return (this.purchaseType == PurchaseType.CarOnly) || (this.purchaseType == PurchaseType.HCBundle) || (this.purchaseType == PurchaseType.FCBundle);
    }

    public int getFlightOption()
    {
        if ((this.purchaseType == PurchaseType.FCPackage) || (this.purchaseType == PurchaseType.FCBundle) || (this.purchaseType == PurchaseType.FHCBundle) || (this.purchaseType == PurchaseType.FHCPackage))
        {
            return 2;
        }
        else
        {
            return 1;   // 1 means no flight
        }
    }

    public int getHotelOption()
    {
        //"1" means no hotel, "2,3" means hotel included(Merchant, Agency hotel all should be included)

        if ((this.purchaseType == PurchaseType.HCBundle) || (this.purchaseType == PurchaseType.HCPackage) || (this.purchaseType == PurchaseType.FHCBundle) || (this.purchaseType == PurchaseType.FHCPackage) || (this.purchaseType == PurchaseType.THCPackage))
        {
            //TODO for returning 2,3
            return 2;
        }
        else
        {
            return 1;   // 1 means no hotel
        }
    }
}