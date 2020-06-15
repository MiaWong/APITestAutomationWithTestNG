package com.expedia.s3.cars.framework.test.common.execution.scenarios;

import java.math.BigDecimal;

/**
 * Created by sswaminathan on 8/4/16.
 */
@SuppressWarnings("PMD")
public class LocationSearchTestScenario
{
    private String scenarionName;
    private String jurisdictionCountryCode;
    private String companyCode;
    private String managementUnitCode;
    private BigDecimal latitude;
    private BigDecimal  longitude;
    private int radius;
    public String distanceUnit;
    public String deliveryBoolean;
    public String collectionBoolean;
    public String airportVicinityBoolean;
    public String outOfOfficeHourBoolean;
    public boolean includeLocationDetails;
    public String iataCode;
    public String clientCode;
    public Long tuid;
    public Long tpid;
    public Long eapid;
    public boolean nullSearchFilter;
    public boolean nullIncludeLocation;
    public boolean nullAuditLogTracking;


    public LocationSearchTestScenario(String scenarionName, String jurisdictionCountryCode,
                        String companyCode, String managementUnitCode,
                        BigDecimal latitude, BigDecimal longitude, int radius, String distanceUnit,
                        String deliveryBoolean, String collectionBoolean, String airportVicinityBoolean,
                                  String outOfOfficeHourBoolean, boolean includeLocationDetails , String iataCode   )
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.distanceUnit = distanceUnit;
        this.deliveryBoolean = deliveryBoolean;
        this.collectionBoolean = collectionBoolean;
        this.airportVicinityBoolean = airportVicinityBoolean;
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
        this.includeLocationDetails = includeLocationDetails;
        this.iataCode = iataCode;
    }

    public LocationSearchTestScenario(String scenarionName,BigDecimal latitude, BigDecimal longitude, int radius,String distanceUnit,
                                      String deliveryBoolean, String collectionBoolean,String outOfOfficeHourBoolean,
                                      boolean includeLocationDetails ,boolean nullIncludeLocation,boolean nullSearchFilter,boolean nullAuditLogTracking)
    {
        this.scenarionName = scenarionName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.distanceUnit = distanceUnit;
        this.deliveryBoolean = deliveryBoolean;
        this.collectionBoolean = collectionBoolean;
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
        this.includeLocationDetails = includeLocationDetails;
        this.nullIncludeLocation = nullIncludeLocation;
        this.nullSearchFilter = nullSearchFilter;
        this.nullAuditLogTracking = nullAuditLogTracking;
    }


    public LocationSearchTestScenario(String scenarionName,String jurisdictionCountryCode,
                                      String companyCode, String managementUnitCode,String iataCode,String airportVicinityBoolean,
                                      String collectionBoolean, String deliveryBoolean, String outOfOfficeHourBoolean,
                                     boolean includeLocationDetails ,boolean nullIncludeLocation)
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.iataCode = iataCode;
        this.airportVicinityBoolean = airportVicinityBoolean;
        this.deliveryBoolean = deliveryBoolean;
        this.collectionBoolean = collectionBoolean;
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
        this.includeLocationDetails = includeLocationDetails;
        this.nullIncludeLocation = nullIncludeLocation;
    }

    public LocationSearchTestScenario(String scenarionName,String jurisdictionCountryCode,
                                      String companyCode, String managementUnitCode,String iataCode,String airportVicinityBoolean,
                                      String collectionBoolean, String deliveryBoolean, String outOfOfficeHourBoolean,
                                      boolean includeLocationDetails ,boolean nullIncludeLocation, boolean nullSearchFilter)
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.iataCode = iataCode;
        this.airportVicinityBoolean = airportVicinityBoolean;
        this.deliveryBoolean = deliveryBoolean;
        this.collectionBoolean = collectionBoolean;
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
        this.includeLocationDetails = includeLocationDetails;
        this.nullIncludeLocation = nullIncludeLocation;
        this.nullSearchFilter = nullSearchFilter;
    }

    public LocationSearchTestScenario(String scenarionName,String jurisdictionCountryCode,
                                      String companyCode, String managementUnitCode,BigDecimal latitude, BigDecimal longitude, int radius,String distanceUnit,
                                      String collectionBoolean, String deliveryBoolean, String outOfOfficeHourBoolean,
                                      boolean includeLocationDetails ,boolean nullIncludeLocation)
    {
        this.scenarionName = scenarionName;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.companyCode = companyCode;
        this.managementUnitCode = managementUnitCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.distanceUnit = distanceUnit;
        this.deliveryBoolean = deliveryBoolean;
        this.collectionBoolean = collectionBoolean;
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
        this.includeLocationDetails = includeLocationDetails;
        this.nullIncludeLocation = nullIncludeLocation;
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

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public int geRadius()
    {
        return radius;
    }

    public String getDistanceUnit()
    {
        return distanceUnit;
    }

    public String getDeliveryBoolean()
    {
        return deliveryBoolean;
    }

    public String getCollectionBoolean()
    {
        return collectionBoolean;
    }

    public String getAirportVicinityBoolean()
    {
        return airportVicinityBoolean;
    }

    public String getOutOfOfficeHourBoolean()
    {
        return outOfOfficeHourBoolean;
    }

    public boolean isIncludeLocationDetails()
    {
        return includeLocationDetails;
    }

    public String getIataCode()
    {
        return iataCode;
    }

    public int getRadius() {
        return radius;
    }

    public String getClientCode() {
        return clientCode;
    }


    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public Long getTuid() {
        return tuid;
    }

    public void setTuid(Long tuid) {
        this.tuid = tuid;
    }

    public Long getTpid() {
        return tpid;
    }

    public void setTpid(Long tpid) {
        this.tpid = tpid;
    }

    public boolean isNullSearchFilter() {
        return nullSearchFilter;
    }

    public void setNullSearchFilter(boolean nullSearchFilter) {
        this.nullSearchFilter = nullSearchFilter;
    }

    public boolean isNullAuditLogTracking() {
        return nullAuditLogTracking;
    }

    public void setNullAuditLogTracking(boolean nullAuditLogTracking) {
        this.nullAuditLogTracking = nullAuditLogTracking;
    }

    public boolean isNullIncludeLocation() {
        return nullIncludeLocation;
    }

    public void setNullIncludeLocation(boolean nullIncludeLocation) {
        this.nullIncludeLocation = nullIncludeLocation;
    }

    public void setScenarionName(String scenarionName) {
        this.scenarionName = scenarionName;
    }

    public void setJurisdictionCountryCode(String jurisdictionCountryCode) {
        this.jurisdictionCountryCode = jurisdictionCountryCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public void setManagementUnitCode(String managementUnitCode) {
        this.managementUnitCode = managementUnitCode;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public void setDeliveryBoolean(String deliveryBoolean) {
        this.deliveryBoolean = deliveryBoolean;
    }

    public void setCollectionBoolean(String collectionBoolean) {
        this.collectionBoolean = collectionBoolean;
    }

    public void setAirportVicinityBoolean(String airportVicinityBoolean) {
        this.airportVicinityBoolean = airportVicinityBoolean;
    }

    public void setOutOfOfficeHourBoolean(String outOfOfficeHourBoolean) {
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
    }

    public void setIncludeLocationDetails(boolean includeLocationDetails) {
        this.includeLocationDetails = includeLocationDetails;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public Long getEapid() {
        return eapid;
    }

    public void setEapid(Long eapid) {
        this.eapid = eapid;
    }
}