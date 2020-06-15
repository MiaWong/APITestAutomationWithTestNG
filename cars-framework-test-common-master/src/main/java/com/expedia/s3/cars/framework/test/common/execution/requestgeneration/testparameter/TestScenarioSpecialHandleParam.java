package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;

import java.util.List;
import java.util.Map;

/**
 * Created by miawang on 12/28/2016.
 */
public class TestScenarioSpecialHandleParam {
    //these two parameter is use to handle pickup and dropoff location:
    // it may have this situation we need off airport pickuplocation but on airport drop-off location.
    private String pickupOnAirport;
    private String dropOffOnAirport;

    //Format : T001
    private String pickUpCarVendorLocationCode;
    private String dropOffCarVendorLocationCode;

    //if set vendorCode or vendorSupplierID, then the search Criteria is only with this vendor
    private String vendorCode;
    private long vendorSupplierID;

    //if set vendorSupplierIDs, then the search Criteria is only with these vendor
    private List<Long> vendorSupplierIDs;

    //it may have we only need on criteria situation.
    private int searchCriteriaCount;

    private boolean hertzPrepayTestCase;

    private TestScenario multiplePickAndDropLocationScenario;

    private Map<String, AddressType> deliveryAndCollectionAddress;

   private  boolean deliveryAvailable;

   private boolean collectionAvailable;

   private String deliveryPlaceID;

   private String collectionPlaceID;

    public String getPickupOnAirport() {
        return pickupOnAirport;
    }

    public void setPickupOnAirport(String pickupOnAirport) {
        this.pickupOnAirport = pickupOnAirport;
    }

    public String getDropOffOnAirport() {
        return dropOffOnAirport;
    }

    public void setDropOffOnAirport(String dropOffOnAirport) {
        this.dropOffOnAirport = dropOffOnAirport;
    }

    public int getSearchCriteriaCount() {
        return searchCriteriaCount;
    }

    public void setSearchCriteriaCount(int searchCriteriaCount) {
        this.searchCriteriaCount = searchCriteriaCount;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public long getVendorSupplierID() {
        return vendorSupplierID;
    }

    public void setVendorSupplierID(long vendorSupplierID) {
        this.vendorSupplierID = vendorSupplierID;
    }

    public List<Long> getVendorSupplierIDs()
    {
        return vendorSupplierIDs;
    }

    public void setVendorSupplierIDs(List<Long> vendorSupplierIDs)
    {
        this.vendorSupplierIDs = vendorSupplierIDs;
    }

    public String getPickUpCarVendorLocationCode() {
        return pickUpCarVendorLocationCode;
    }

    public void setPickUpCarVendorLocationCode(String pickUpCarVendorLocationCode) {
        this.pickUpCarVendorLocationCode = pickUpCarVendorLocationCode;
    }

    public String getDropOffCarVendorLocationCode() {
        return dropOffCarVendorLocationCode;
    }

    public void setDropOffCarVendorLocationCode(String dropOffCarVendorLocationCode) {
        this.dropOffCarVendorLocationCode = dropOffCarVendorLocationCode;
    }

    public boolean isHertzPrepayTestCase()
    {
        return hertzPrepayTestCase;
    }

    public void setHertzPrepayTestCase(boolean hertzPrepayTestCase)
    {
        this.hertzPrepayTestCase = hertzPrepayTestCase;
    }

    public TestScenario getMultiplePickAndDropLocationScenario()
    {
        return multiplePickAndDropLocationScenario;
    }

    public void setMultiplePickAndDropLocationScenario(TestScenario multiplePickAndDropLocationScenario)
    {
        this.multiplePickAndDropLocationScenario = multiplePickAndDropLocationScenario;
    }
    public Map<String, AddressType> getDeliveryAndCollectionAddress() {
        return deliveryAndCollectionAddress;
    }

    public void setDeliveryAndCollectionAddress(Map<String, AddressType> deliveryAndCollectionAddress) {
        this.deliveryAndCollectionAddress = deliveryAndCollectionAddress;
    }

    public boolean isDeliveryAvailable() {
        return deliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        this.deliveryAvailable = deliveryAvailable;
    }

    public boolean isCollectionAvailable() {
        return collectionAvailable;
    }

    public void setCollectionAvailable(boolean collectionAvailable) {
        this.collectionAvailable = collectionAvailable;
    }

    public String getDeliveryPlaceID() {
        return deliveryPlaceID;
    }

    public void setDeliveryPlaceID(String deliveryPlaceID) {
        this.deliveryPlaceID = deliveryPlaceID;
    }

    public String getCollectionPlaceID() {
        return collectionPlaceID;
    }

    public void setCollectionPlaceID(String collectionPlaceID) {
        this.collectionPlaceID = collectionPlaceID;
    }
}
