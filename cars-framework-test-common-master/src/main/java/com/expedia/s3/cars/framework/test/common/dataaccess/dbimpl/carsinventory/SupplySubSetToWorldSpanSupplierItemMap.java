package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by yyang4 on 12/12/2016.
 */
public class SupplySubSetToWorldSpanSupplierItemMap {
    private Long supplySubsetID;
    private String iataAgencyCode;
    private String iataOverrideBooking;
    private String corporateDiscountCode;
    private boolean corporateDiscountCodeRequiredInShopping;
    private String corporateDiscountCodeRequiredInBooking;
    private String itNumber;
    private String rateCode;
    private String prepaidBool;

    public Long getSupplySubsetID() {
        return supplySubsetID;
    }

    public void setSupplySubsetID(Long supplySubsetID) {
        this.supplySubsetID = supplySubsetID;
    }

    public String getIataAgencyCode() {
        return iataAgencyCode;
    }

    public void setIataAgencyCode(String iataAgencyCode) {
        this.iataAgencyCode = iataAgencyCode;
    }

    public String getCorporateDiscountCode() {
        return corporateDiscountCode;
    }

    public void setCorporateDiscountCode(String corporateDiscountCode) {
        this.corporateDiscountCode = corporateDiscountCode;
    }

    public boolean isCorporateDiscountCodeRequiredInShopping() {
        return corporateDiscountCodeRequiredInShopping;
    }

    public void setCorporateDiscountCodeRequiredInShopping(boolean corporateDiscountCodeRequiredInShopping) {
        this.corporateDiscountCodeRequiredInShopping = corporateDiscountCodeRequiredInShopping;
    }

    public String getItNumber() {
        return itNumber;
    }

    public void setItNumber(String itNumber) {
        this.itNumber = itNumber;
    }

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getCorporateDiscountCodeRequiredInBooking() {
        return corporateDiscountCodeRequiredInBooking;
    }

    public void setCorporateDiscountCodeRequiredInBooking(String corporateDiscountCodeRequiredInBooking) {
        this.corporateDiscountCodeRequiredInBooking = corporateDiscountCodeRequiredInBooking;
    }

    public String getIataOverrideBooking() {
        return iataOverrideBooking;
    }

    public void setIataOverrideBooking(String iataOverrideBooking) {
        this.iataOverrideBooking = iataOverrideBooking;
    }

    public String getPrepaidBool()
    {
        return prepaidBool;
    }

    public void setPrepaidBool(String prepaidBool)
    {
        this.prepaidBool = prepaidBool;
    }
}
