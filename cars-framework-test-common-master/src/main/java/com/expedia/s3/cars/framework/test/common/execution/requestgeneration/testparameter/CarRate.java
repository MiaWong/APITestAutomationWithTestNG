package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

/**
 * Created by miawang on 12/19/2016.
 */
public class CarRate {
    //if there is multiple cd code, use "," to connect the cd codes.
    //example: EP-51354174,EP-null,SX-5020105,SX-null,ZI-N865556,ZI-null,ZE-null,ZE-3456789
    private String cdCode;
    private String rateCode;
    private String promoCode;
    private String loyaltyNum;

    public String getCdCode() {
        return cdCode;
    }

    public void setCdCode(String cdCode) {
        this.cdCode = cdCode;
    }

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getLoyaltyNum() {
        return loyaltyNum;
    }

    public void setLoyaltyNum(String loyaltyNum) {
        this.loyaltyNum = loyaltyNum;
    }
}
