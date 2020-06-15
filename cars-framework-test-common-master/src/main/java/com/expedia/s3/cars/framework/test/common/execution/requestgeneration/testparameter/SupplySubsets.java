package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

import java.util.List;

/**
 * Created by fehu on 7/12/2017.
 */
public class SupplySubsets {

        private long supplySubsetID;
        private int carBusinessModelID;
        private List<Long> supplierID;

        public long getSupplySubsetID()
        {
            return supplySubsetID;
        }

        public void setSupplySubsetID(long supplySubsetID)
        {
            this.supplySubsetID = supplySubsetID;
        }

        public int getCarBusinessModelID()
        {
            return carBusinessModelID;
        }

        public void setCarBusinessModelID(int carBussinessModelID)
        {
            carBusinessModelID = carBussinessModelID;
        }

    public List<Long> getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(List<Long> supplierID) {
        this.supplierID = supplierID;
    }
}
