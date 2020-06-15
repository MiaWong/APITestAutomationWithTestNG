package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarSearchCriteria;

import java.util.List;

/**
 * Created by jiyu on 1/12/17.
 */
public class CarConnectivitySearchCriteria extends CarSearchCriteria
{
    List<String> supplySubsetIDEntryList;
    List<String> vendorSupplierIDList;

    //  getters
    public List<String> getSupplySubsetIDEntryList() { return supplySubsetIDEntryList; }
    public List<String> getVendorSupplierIDList() { return vendorSupplierIDList; }

    //  setters
    public void setSupplySubsetIDEntryList(List<String> supplySubsetIDEntryList) { this.supplySubsetIDEntryList = supplySubsetIDEntryList; }
    public void setVendorSupplierIDList(List<String> vendorSupplierIDList) { this.vendorSupplierIDList = vendorSupplierIDList; }

}
