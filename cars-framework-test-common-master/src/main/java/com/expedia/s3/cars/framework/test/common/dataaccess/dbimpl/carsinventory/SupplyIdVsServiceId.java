package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

public class SupplyIdVsServiceId {
    long supplySubsetID,supplyConnectivityServiceID;

    public long getSupplySubsetID() {
        return supplySubsetID;
    }

    public void setSupplySubsetID(long supplySubsetID) {
        this.supplySubsetID = supplySubsetID;
    }

    public long getSupplyConnectivityServiceID() {
        return supplyConnectivityServiceID;
    }

    public void setSupplyConnectivityServiceID(long supplyConnectivityServiceID) {
        this.supplyConnectivityServiceID = supplyConnectivityServiceID;
    }
}
