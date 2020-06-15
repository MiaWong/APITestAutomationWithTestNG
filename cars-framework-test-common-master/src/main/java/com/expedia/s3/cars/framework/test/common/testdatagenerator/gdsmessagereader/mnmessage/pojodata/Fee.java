package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import java.math.BigDecimal;

/**
 * Created by fehu on 12/28/2016.
 */
public class Fee {
    //Fee for Location service charge(LSC)
    private BigDecimal lscFee;
    //CurrencyCode of Fee for Location service charge(LSC)
    private String lscFeeCurrencyCode;
    //Description of Fee for Location service charge(LSC)
    private String lscFeeDescription;
    //Fee for TAX(TAX)
    private BigDecimal taxFee;
    //CurrencyCode of Fee for TAX(TAX)
    private String taxFeeCurrencyCode;
    //Description of Fee for TAX(TAX)
    private String taxFeeDescription;

    public BigDecimal getLscFee() {
        return lscFee;
    }

    public void setLscFee(BigDecimal lscFee) {
        this.lscFee = lscFee;
    }

    public String getLscFeeCurrencyCode() {
        return lscFeeCurrencyCode;
    }

    public void setLscFeeCurrencyCode(String lscFeeCurrencyCode) {
        this.lscFeeCurrencyCode = lscFeeCurrencyCode;
    }

    public String getLscFeeDescription() {
        return lscFeeDescription;
    }

    public void setLscFeeDescription(String lscFeeDescription) {
        this.lscFeeDescription = lscFeeDescription;
    }

    public BigDecimal getTaxFee() {
        return taxFee;
    }

    public void setTaxFee(BigDecimal taxFee) {
        this.taxFee = taxFee;
    }

    public String getTaxFeeCurrencyCode() {
        return taxFeeCurrencyCode;
    }

    public void setTaxFeeCurrencyCode(String taxFeeCurrencyCode) {
        this.taxFeeCurrencyCode = taxFeeCurrencyCode;
    }

    public String getTaxFeeDescription() {
        return taxFeeDescription;
    }

    public void setTaxFeeDescription(String taxFeeDescription) {
        this.taxFeeDescription = taxFeeDescription;
    }
}
