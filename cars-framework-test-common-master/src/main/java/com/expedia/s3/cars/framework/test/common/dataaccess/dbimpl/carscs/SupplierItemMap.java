package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs;

/**
 * Created by v-mechen on 1/23/2017.
 */
public class SupplierItemMap {

    private long supplySubsetID;
    private String itemKey;
    private String itemValue;

    public long getSupplySubsetID() {
        return supplySubsetID;
    }

    public void setSupplySubsetID(long supplySubsetID) {
        this.supplySubsetID = supplySubsetID;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
}
