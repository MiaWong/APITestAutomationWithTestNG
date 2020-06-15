package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/13/17.
 */
public class CarRate
{
    //if there is multiple cd code, use "," to connect the cd codes.
    //example: EP-51354174,EP-null,SX-5020105,SX-null,ZI-N865556,ZI-null,ZE-null,ZE-3456789
    private String carRateQualifier;
    private String rateCode;
    private Long carAgreementID;
    private String carVendorAgreement;
    private String corporateDiscountCode;
    private LoyaltyProgram loyaltyProgram;
    private String promoteCode;
    private String rateCategoryCode;
    private String ratePeriodCode;

    //  getters
    public String getCarRateQualifier() { return carRateQualifier;}
    public String getRateCode() { return rateCode; }
    public Long getCarAgreementID() { return carAgreementID;}
    public String getCarVendorAgreement() {return carVendorAgreement;}
    public String getCorporateDiscountCode() {return corporateDiscountCode;}
    public LoyaltyProgram getLoyaltyProgram() {return loyaltyProgram;}
    public String getPromoteCode() {return promoteCode;}
    public String getRateCategoryCode() {return rateCategoryCode;}
    public String getRatePeriodCode() {return ratePeriodCode;}


    //  setters
    public void setCarRateQualifier(String carRateQualifier) { this.carRateQualifier = carRateQualifier;}
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public void setCarAgreementID(Long carAgreementID) { this.carAgreementID =  carAgreementID;}
    public void setCarVendorAgreement(String carVendorAgreement) {this.carVendorAgreement = carVendorAgreement;}
    public void setCorporateDiscountCode(String corporateDiscountCode) {this.corporateDiscountCode =  corporateDiscountCode;}
    public void setLoyaltyProgram(LoyaltyProgram loyaltyProgram) {this.loyaltyProgram = loyaltyProgram;}
    public void setPromoteCode(String promoteCode) {this.promoteCode = promoteCode;}
    public void setRateCategoryCode(String rateCategoryCode) {this.rateCategoryCode = rateCategoryCode;}
    public void setRatePeriodCode(String ratePeriodCode) { this.ratePeriodCode = ratePeriodCode;}
}
