package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.cesrobject;

/**
 * Created by jiyu on 1/12/17.
 */
public class CarECommerceSearchStrategy
{
    private boolean crossSellBoolean;
    private boolean needReferencePricesBoolean;
    private boolean needPublishedBoolean;
    private boolean needMerchantBoolean;
    private boolean needUpgradeMapBoolean;
    private Long hotelPurchaseOption;
    private Long purchaseTypeMask;

    //  getters
    public boolean isCrossSellBoolean() { return this.crossSellBoolean; }
    public boolean isNeedReferencePricesBoolean() { return this.needReferencePricesBoolean; }
    public boolean isNeedPublishedBoolean() { return this.needPublishedBoolean; }
    public boolean isNeedMerchantBoolean() { return this.needMerchantBoolean; }
    public boolean isNeedUpgradeMapBoolean() { return this.needUpgradeMapBoolean; }
    public Long getHotelPurchaseOption() { return this.hotelPurchaseOption; }
    public Long getPurchaseTypeMask() { return this.purchaseTypeMask; }

    //  setters
    public void setCrossSellBoolean() { this.crossSellBoolean = crossSellBoolean; }
    public void setNeedReferencePricesBoolean() { this.needReferencePricesBoolean = needReferencePricesBoolean; }
    public void setNeedPublishedBoolean() { this.needPublishedBoolean = needPublishedBoolean; }
    public void setNeedMerchantBoolean() { this.needMerchantBoolean = needMerchantBoolean; }
    public void setNeedUpgradeMapBoolean() { this.needUpgradeMapBoolean = needUpgradeMapBoolean; }
    public void setHotelPurchaseOption() { this.hotelPurchaseOption = hotelPurchaseOption; }
    public void setPurchaseTypeMask() { this.purchaseTypeMask = hotelPurchaseOption; }

}
