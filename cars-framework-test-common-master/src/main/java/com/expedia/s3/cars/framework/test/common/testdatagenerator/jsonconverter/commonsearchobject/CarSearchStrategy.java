package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class CarSearchStrategy //implements Serializable
{
    //@SerializedName("PricingVisibilityBoolean")
    boolean pricingVisibilityBoolean;
    //@SerializedName("PackageBoolean")
    boolean packageBoolean;

    //  getters
    public boolean isPricingVisibilityBoolean() { return this.pricingVisibilityBoolean; }
    public boolean isPackageBoolean() { return this.packageBoolean; }

    //  setters
    public void setPricingVisibilityBoolean(boolean pricingVisibilityBoolean) { this.pricingVisibilityBoolean = pricingVisibilityBoolean; }
    public void setPackageBoolean(boolean packageBoolean) { this.packageBoolean = packageBoolean; }

}