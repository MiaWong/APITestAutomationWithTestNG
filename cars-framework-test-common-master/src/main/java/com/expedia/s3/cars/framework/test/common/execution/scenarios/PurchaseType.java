package com.expedia.s3.cars.framework.test.common.execution.scenarios;

/**
 * Created by asharma1 on 9/27/2016.
 */
public enum PurchaseType
{
    CarOnly(128),
    HCPackage(4),
    FCPackage(8),
    FHCPackage(16),
    TCPackage(256),
    FCBundle(512),
    HCBundle(1024),
    THCPackage(4096),
    FHCBundle(8192);

    private int purchaseTypeMask;

    PurchaseType(int purchaseTypeMask)
    {
        this.purchaseTypeMask = purchaseTypeMask;
    }

    public int getPurchaseTypeMask()
    {
        return purchaseTypeMask;
    }
}
