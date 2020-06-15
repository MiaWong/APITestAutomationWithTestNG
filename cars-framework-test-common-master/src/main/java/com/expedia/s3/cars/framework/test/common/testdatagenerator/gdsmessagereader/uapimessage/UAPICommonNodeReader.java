package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.basetypes.defn.v4.*;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.e3.data.persontypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.timetypes.defn.v4.DateTimeRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yyang4 on 12/5/2016.
 */
@SuppressWarnings("PMD")
public class UAPICommonNodeReader {

    private UAPICommonNodeReader() {
    }

    public static Long uapiMessageSystemID = 7L;

    // <summary>
    // Read CarVehicle from UAPI request and response
    // </summary>
    // <param name="domainValueMapHelper"></param>
    // <param name="vehicleNode"></param>
    // <returns></returns>
    @SuppressWarnings("CPD-START")
    public static CarVehicleType readCarVehicle(DataSource scsDataSource, Node vehicleNode, boolean isSearch) throws DataAccessException {
        final CarVehicleType vehicleType = new CarVehicleType();
        final Node vehicleClassNode = vehicleNode.getAttributes().getNamedItem("VehicleClass");
        if (!CompareUtil.isObjEmpty(vehicleClassNode)) {
            final String carCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_CATEGORY, "", vehicleClassNode.getNodeValue());
            vehicleType.setCarCategoryCode(Long.valueOf(carCategoryCode));
        }
        if (!isSearch) {
            final Node doorCountNode = vehicleNode.getAttributes().getNamedItem("DoorCount");
            final Node categoryNode = vehicleNode.getAttributes().getNamedItem("Category");
            final Node transmissionTypeNode = vehicleNode.getAttributes().getNamedItem("TransmissionType");
            final Node airConditioningNode = vehicleNode.getAttributes().getNamedItem("AirConditioning");
            final Node fuelTypeNode = vehicleNode.getAttributes().getNamedItem("FuelType");
            if (CompareUtil.isObjEmpty(doorCountNode) && !CompareUtil.isObjEmpty(categoryNode)) {
                final String carTypeCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_TYPE, "", categoryNode.getNodeValue());
                vehicleType.setCarTypeCode(CompareUtil.isObjEmpty(carTypeCode) ? 0 : Long.valueOf(carTypeCode));
            } else if (!CompareUtil.isObjEmpty(doorCountNode)) {
                final String carTypeCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_TYPE, "", doorCountNode.getNodeValue());
                vehicleType.setCarTypeCode(Long.valueOf(carTypeCode));
            }
            if (!CompareUtil.isObjEmpty(categoryNode)) {
                final String carTransmissionDriveCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_TRANSMISSIOND_DRIVE, "", transmissionTypeNode.getNodeValue());
                vehicleType.setCarTransmissionDriveCode(Long.valueOf(carTransmissionDriveCode));
            }
            if (!CompareUtil.isObjEmpty(airConditioningNode)) {
                String airCondEx = airConditioningNode.getNodeValue();
                final String fuelTypeEx = CompareUtil.isObjEmpty(fuelTypeNode) ? "*" : fuelTypeNode.getNodeValue();
                //true is mapped to *:1 DB table - [ExternalSupplyServiceDomainValueMap] false is mapped to * DB table - [ExternalSupplyServiceDomainValueMap]
                airCondEx = "true".equals(airCondEx) ? fuelTypeEx + ":1" : fuelTypeEx + ":0";
                final String carFuelACCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_FUEL_AIR_CONDITION, "", airCondEx);
                vehicleType.setCarFuelACCode(Long.valueOf(carFuelACCode));
            }

        }
        return vehicleType;
    }

    //read vendorsupplier ids
    public static VendorSupplierIDListType readVendorSupplierIDList(DataSource scsDataSource, List<Node> permittedVendorsList) throws DataAccessException {
        final VendorSupplierIDListType supplierIDListType = new VendorSupplierIDListType();
        final List<Long> supplierIDList = new ArrayList<Long>();
        if (!CompareUtil.isObjEmpty(permittedVendorsList)) {
            for (final Node node : permittedVendorsList) {
                final Node codeNode = node.getAttributes().getNamedItem("Code");
                if (!CompareUtil.isObjEmpty(codeNode)) {
                    supplierIDList.add(Long.valueOf(readDomainValue(scsDataSource, 0, 0, CommonConstantManager.DomainType.CAR_VENDOR, "", codeNode.getNodeValue())));
                }
            }
        }
        supplierIDListType.setVendorSupplierID(supplierIDList);
        return supplierIDListType;
    }

    // <summary>
    // Read location and date from UAPI request and response
    // </summary>
    // <param name="domainValueMapHelper"></param>
    // <param name="carInventoryKey"></param>
    // <param name="dateLocationNode"></param>
    public static void readDateLocation(DataSource scsDataSource, CarInventoryKeyType carInventoryKey, Node dateLocationNode) throws DataAccessException {
        //Read date
        if (!CompareUtil.isObjEmpty(dateLocationNode)) {
            final String pickDT_S = dateLocationNode.getAttributes().getNamedItem("PickupDateTime").getNodeValue();
            final String returnDT_S = dateLocationNode.getAttributes().getNamedItem("ReturnDateTime").getNodeValue();
            carInventoryKey.setCarPickUpDateTime(new DateTime(pickDT_S.length() > 19 ? pickDT_S.substring(0, 19) : pickDT_S));
            carInventoryKey.setCarDropOffDateTime(new DateTime(returnDT_S.length() > 19 ? returnDT_S.substring(0, 19) : returnDT_S));
            //Read location
            //Try read from dateLocationNode attributes
            final CarCatalogKeyType carCatalogKey = CompareUtil.isObjEmpty(carInventoryKey.getCarCatalogKey()) ? new CarCatalogKeyType() : carInventoryKey.getCarCatalogKey();
            final CarLocationKeyType pickUpLocationKey = CompareUtil.isObjEmpty(carCatalogKey.getCarPickupLocationKey()) ? new CarLocationKeyType() : carCatalogKey.getCarPickupLocationKey();
            final CarLocationKeyType dropOffLocationKey = CompareUtil.isObjEmpty(carCatalogKey.getCarDropOffLocationKey()) ? new CarLocationKeyType() : carCatalogKey.getCarDropOffLocationKey();
            carCatalogKey.setCarPickupLocationKey(pickUpLocationKey);
            carCatalogKey.setCarDropOffLocationKey(dropOffLocationKey);
            carInventoryKey.setCarCatalogKey(carCatalogKey);
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocation"))) {
                pickUpLocationKey.setLocationCode(dateLocationNode.getAttributes().getNamedItem("PickupLocation").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocationType"))) {
                final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", dateLocationNode.getAttributes().getNamedItem("PickupLocationType").getNodeValue());
                pickUpLocationKey.setCarLocationCategoryCode(carLocationCategoryCode);
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocationNumber"))) {
                pickUpLocationKey.setSupplierRawText(dateLocationNode.getAttributes().getNamedItem("PickupLocationNumber").getNodeValue());
                if (!CompareUtil.isObjEmpty(pickUpLocationKey.getSupplierRawText()) && pickUpLocationKey.getSupplierRawText().length() == 2) {
                    pickUpLocationKey.setSupplierRawText("0" + carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText());
                }
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocation"))) {
                dropOffLocationKey.setLocationCode(dateLocationNode.getAttributes().getNamedItem("ReturnLocation").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocationType"))) {
                final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", dateLocationNode.getAttributes().getNamedItem("ReturnLocationType").getNodeValue());
                dropOffLocationKey.setCarLocationCategoryCode(carLocationCategoryCode);
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber"))) {
                dropOffLocationKey.setSupplierRawText(dateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber").getNodeValue());
                if (!StringUtils.isEmpty(dropOffLocationKey.getSupplierRawText()) && dropOffLocationKey.getSupplierRawText().length() == 2) {
                    dropOffLocationKey.setSupplierRawText("0" + carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText());
                }
            }

            for (int i = 0; i < dateLocationNode.getChildNodes().getLength(); i++) {
                final Node childNode = dateLocationNode.getChildNodes().item(i);
                final CarLocationKeyType locationKey = new CarLocationKeyType();
                locationKey.setLocationCode(childNode.getAttributes().getNamedItem("LocationCode").getNodeValue());
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("LocationType"))) {
                    final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", childNode.getAttributes().getNamedItem("LocationType").getNodeValue());
                    locationKey.setCarLocationCategoryCode(carLocationCategoryCode);
                }
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("VendorLocationID"))) {
                    locationKey.setSupplierRawText(childNode.getAttributes().getNamedItem("VendorLocationID").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(locationKey.getSupplierRawText()) && locationKey.getSupplierRawText().length() == 2) {
                    locationKey.setSupplierRawText("0" + locationKey.getSupplierRawText());
                }
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("Type")) && "Pickup".equals(childNode.getAttributes().getNamedItem("Type").getNodeValue())) {
                    carInventoryKey.getCarCatalogKey().setCarPickupLocationKey(locationKey);
                } else {
                    carInventoryKey.getCarCatalogKey().setCarDropOffLocationKey(locationKey);
                }
            }

            //Copy the pickup to dropoff if no dropoff location
            if (CompareUtil.isObjEmpty(carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey()) || CompareUtil.isObjEmpty(carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getLocationCode())) {
                carInventoryKey.getCarCatalogKey().setCarDropOffLocationKey(carInventoryKey.getCarCatalogKey().getCarPickupLocationKey());
            }
        }
    }

    //only for off airport to verify the carVendorLocationId
    public static void readCarVendorLocationId(DataSource scsDataSource, CarLocationKeyType pickUpLocation, CarLocationKeyType dropOffLocation, Long supplierId) throws DataAccessException {
        final String pickUpExternalDomainValue = pickUpLocation.getLocationCode() + pickUpLocation.getCarLocationCategoryCode() + pickUpLocation.getSupplierRawText();
        final String dropOffExternalDomainValue = dropOffLocation.getLocationCode() + dropOffLocation.getCarLocationCategoryCode() + dropOffLocation.getSupplierRawText();
        final String pickUpLocationId = readDomainValue(scsDataSource, supplierId, 0, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, "", pickUpExternalDomainValue);
        final String dropOffLocationId = readDomainValue(scsDataSource, supplierId, 0, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, "", dropOffExternalDomainValue);
        if (!CompareUtil.isObjEmpty(pickUpLocationId)) {
            pickUpLocation.setCarVendorLocationID(Long.valueOf(pickUpLocationId));
        }
        if (!CompareUtil.isObjEmpty(dropOffLocationId)) {
            dropOffLocation.setCarVendorLocationID(Long.valueOf(dropOffLocationId));
        }

    }

    // <summary>
    // Read location and date from UAPI search request
    // </summary>
    // <param name="domainValueMapHelper"></param>
    // <param name="carSearchCriteria"></param>
    // <param name="dateLocationNode"></param>
    public static void readDateLocationForCarSearchCriteria(DataSource scsDataSource, CarSearchCriteriaType carSearchCriteria, Node dateLocationNode) throws DataAccessException {
        //Read date
        final String pickDT_S = dateLocationNode.getAttributes().getNamedItem("PickupDateTime").getNodeValue();
        final String returnDT_S = dateLocationNode.getAttributes().getNamedItem("ReturnDateTime").getNodeValue();
        final CarTransportationSegmentType carTransportationSegment = CompareUtil.isObjEmpty(carSearchCriteria.getCarTransportationSegment()) ? new CarTransportationSegmentType() : carSearchCriteria.getCarTransportationSegment();
        final SegmentDateTimeRangeType segmentDateTimeRange = CompareUtil.isObjEmpty(carTransportationSegment.getSegmentDateTimeRange()) ? new SegmentDateTimeRangeType() : carTransportationSegment.getSegmentDateTimeRange();
        final DateTimeRangeType dateTimeRangeStart = CompareUtil.isObjEmpty(segmentDateTimeRange.getStartDateTimeRange()) ? new DateTimeRangeType() : segmentDateTimeRange.getStartDateTimeRange();
        final DateTimeRangeType dateTimeRangeEnd = CompareUtil.isObjEmpty(segmentDateTimeRange.getEndDateTimeRange()) ? new DateTimeRangeType() : segmentDateTimeRange.getEndDateTimeRange();
        dateTimeRangeStart.setMaxDateTime(new DateTime(pickDT_S.length() > 19 ? pickDT_S.substring(0, 19) : pickDT_S));
        dateTimeRangeStart.setMinDateTime(new DateTime(pickDT_S.length() > 19 ? pickDT_S.substring(0, 19) : pickDT_S));
        dateTimeRangeEnd.setMaxDateTime(new DateTime(returnDT_S.length() > 19 ? returnDT_S.substring(0, 19) : returnDT_S));
        dateTimeRangeEnd.setMinDateTime(new DateTime(returnDT_S.length() > 19 ? returnDT_S.substring(0, 19) : returnDT_S));
        segmentDateTimeRange.setStartDateTimeRange(dateTimeRangeStart);
        segmentDateTimeRange.setEndDateTimeRange(dateTimeRangeEnd);
        carTransportationSegment.setSegmentDateTimeRange(segmentDateTimeRange);
        carSearchCriteria.setCarTransportationSegment(carTransportationSegment);

        //Read location
        //Try read from dateLocationNode attributes
        final CarLocationKeyType carLocationKeyStart = CompareUtil.isObjEmpty(carTransportationSegment.getStartCarLocationKey()) ? new CarLocationKeyType() : carTransportationSegment.getStartCarLocationKey();
        carTransportationSegment.setStartCarLocationKey(carLocationKeyStart);
        if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocation"))) {
            carLocationKeyStart.setLocationCode(dateLocationNode.getAttributes().getNamedItem("PickupLocation").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocationType"))) {
            final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", dateLocationNode.getAttributes().getNamedItem("PickupLocationType").getNodeValue());
            carLocationKeyStart.setCarLocationCategoryCode(carLocationCategoryCode);
        }
        if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocationNumber"))) {
            carLocationKeyStart.setSupplierRawText(dateLocationNode.getAttributes().getNamedItem("PickupLocationNumber").getNodeValue());
            if (carLocationKeyStart.getSupplierRawText().length() == 2) {
                carLocationKeyStart.setSupplierRawText("0" + carSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getSupplierRawText());
            }
        }

        //For onAirport, we may send LocatioType as "Airport"(mapped to A in DB) - remove it
        if("A".equals(carLocationKeyStart.getCarLocationCategoryCode()) && StringUtils.isEmpty(carLocationKeyStart.getSupplierRawText()))
        {
            carLocationKeyStart.setCarLocationCategoryCode(null);
        }
        final CarLocationKeyType carLocationKeyEnd = CompareUtil.isObjEmpty(carTransportationSegment.getEndCarLocationKey()) ? new CarLocationKeyType() : carTransportationSegment.getEndCarLocationKey();
        carTransportationSegment.setEndCarLocationKey(carLocationKeyEnd);
        if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocation"))) {
            carLocationKeyEnd.setLocationCode(dateLocationNode.getAttributes().getNamedItem("ReturnLocation").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocationType"))) {
            final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", dateLocationNode.getAttributes().getNamedItem("ReturnLocationType").getNodeValue());
            carLocationKeyEnd.setCarLocationCategoryCode(carLocationCategoryCode);
        }
        if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber"))) {
            carLocationKeyEnd.setSupplierRawText(dateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber").getNodeValue());
            if (carLocationKeyEnd.getSupplierRawText().length() == 2) {
                carLocationKeyEnd.setSupplierRawText("0" + carSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getSupplierRawText());
            }
        }
        //For onAirport, we may send LocatioType as "Airport"(mapped to A in DB) - remove it
        if("A".equals(carLocationKeyEnd.getCarLocationCategoryCode()) && StringUtils.isEmpty(carLocationKeyEnd.getSupplierRawText()))
        {
            carLocationKeyEnd.setCarLocationCategoryCode(null);
        }
        for (int i = 0; i < dateLocationNode.getChildNodes().getLength(); i++) {
            final Node childNode = dateLocationNode.getChildNodes().item(i);
            final CarLocationKeyType locationKey = new CarLocationKeyType();
            locationKey.setLocationCode(childNode.getAttributes().getNamedItem("LocationCode").getNodeValue());
            if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("LocationType"))) {
                final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", childNode.getAttributes().getNamedItem("LocationType").getNodeValue());
                locationKey.setCarLocationCategoryCode(carLocationCategoryCode);
            }
            if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("VendorLocationID"))) {
                locationKey.setSupplierRawText(childNode.getAttributes().getNamedItem("VendorLocationID").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(locationKey.getSupplierRawText()) && locationKey.getSupplierRawText().length() == 2) {
                locationKey.setSupplierRawText("0" + locationKey.getSupplierRawText());
            }
            //For onAirport, we may send LocatioType as "Airport"(mapped to A in DB) - remove it
            if("A".equals(locationKey.getCarLocationCategoryCode()) && StringUtils.isEmpty(locationKey.getSupplierRawText()))
            {
                locationKey.setCarLocationCategoryCode(null);
            }
            if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("Type")) && "Pickup".equals(childNode.getAttributes().getNamedItem("Type").getNodeValue())) {
                carSearchCriteria.getCarTransportationSegment().setStartCarLocationKey(locationKey);
            } else {
                carSearchCriteria.getCarTransportationSegment().setEndCarLocationKey(locationKey);
            }
        }
        if (CompareUtil.isObjEmpty(carSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getSupplierRawText()) && "A".equals(carSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarLocationCategoryCode())) {
            carLocationKeyStart.setCarLocationCategoryCode(null);
        }

        //Copy the pickup to dropoff if no dropoff location
        if (CompareUtil.isObjEmpty(carSearchCriteria.getCarTransportationSegment().getEndCarLocationKey()) || CompareUtil.isObjEmpty(carSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getLocationCode())) {
            carSearchCriteria.getCarTransportationSegment().setEndCarLocationKey(carSearchCriteria.getCarTransportationSegment().getStartCarLocationKey());
        }
        if (CompareUtil.isObjEmpty(carSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getSupplierRawText()) && "A".equals(carSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarLocationCategoryCode())) {
            carSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().setCarLocationCategoryCode(null);
        }

    }

    public static Map<String, CarInventoryKeyType> readDateLocationForMultiVendorLocation(DataSource carsInventoryDs,DataSource scsDataSource, Node dateLocationNode) throws DataAccessException {
        final Map<String, CarInventoryKeyType> carInventoryKeyMap = new HashMap<String, CarInventoryKeyType>();
        final CarInventoryKeyType carInventoryKeyCommon = new CarInventoryKeyType();
        if (!CompareUtil.isObjEmpty(dateLocationNode)) {
            final String pickDT_S = dateLocationNode.getAttributes().getNamedItem("PickupDateTime").getNodeValue();
            final String returnDT_S = dateLocationNode.getAttributes().getNamedItem("ReturnDateTime").getNodeValue();
            carInventoryKeyCommon.setCarPickUpDateTime(new DateTime(pickDT_S.length() > 19 ? pickDT_S.substring(0, 19) : pickDT_S));
            carInventoryKeyCommon.setCarDropOffDateTime(new DateTime(returnDT_S.length() > 19 ? returnDT_S.substring(0, 19) : returnDT_S));
            //Read location
            //Try read from dateLocationNode attributes
            final CarCatalogKeyType carCatalogKey = new CarCatalogKeyType();
            final CarLocationKeyType pickUpLocationKey = new CarLocationKeyType();
            final CarLocationKeyType dropOffLocationKey = new CarLocationKeyType();
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocation"))) {
                pickUpLocationKey.setLocationCode(dateLocationNode.getAttributes().getNamedItem("PickupLocation").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocationType"))) {
                final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", dateLocationNode.getAttributes().getNamedItem("PickupLocationType").getNodeValue());
                pickUpLocationKey.setCarLocationCategoryCode(carLocationCategoryCode);
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("PickupLocationNumber"))) {
                pickUpLocationKey.setSupplierRawText(dateLocationNode.getAttributes().getNamedItem("PickupLocationNumber").getNodeValue());
                if (!StringUtils.isEmpty(carInventoryKeyCommon.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText()) && carInventoryKeyCommon.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText().length() == 2) {
                    pickUpLocationKey.setSupplierRawText("0" + carInventoryKeyCommon.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText());
                }
            }
            if(!CompareUtil.isObjEmpty(pickUpLocationKey.getLocationCode()) && !CompareUtil.isObjEmpty(pickUpLocationKey.getCarLocationCategoryCode()) && !CompareUtil.isObjEmpty(pickUpLocationKey.getSupplierRawText())){
                final String vendorLocationID = readDomainValue(scsDataSource,0,0, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION,"",pickUpLocationKey.getLocationCode()+pickUpLocationKey.getCarLocationCategoryCode()+pickUpLocationKey.getSupplierRawText());
                if(!CompareUtil.isObjEmpty(vendorLocationID)){
                    pickUpLocationKey.setCarVendorLocationID(Long.valueOf(vendorLocationID));
                }
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocation"))) {
                dropOffLocationKey.setLocationCode(dateLocationNode.getAttributes().getNamedItem("ReturnLocation").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocationType"))) {
                final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", dateLocationNode.getAttributes().getNamedItem("ReturnLocationType").getNodeValue());
                dropOffLocationKey.setCarLocationCategoryCode(carLocationCategoryCode);
            }
            if (!CompareUtil.isObjEmpty(dateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber"))) {
                dropOffLocationKey.setSupplierRawText(dateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber").getNodeValue());
                if (!StringUtils.isEmpty(carInventoryKeyCommon.getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText()) && carInventoryKeyCommon.getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText().length() == 2) {
                    dropOffLocationKey.setSupplierRawText("0" + carInventoryKeyCommon.getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText());
                }
            }
            if(!CompareUtil.isObjEmpty(dropOffLocationKey.getLocationCode()) && !CompareUtil.isObjEmpty(dropOffLocationKey.getCarLocationCategoryCode()) && !CompareUtil.isObjEmpty(dropOffLocationKey.getSupplierRawText())){
                final String vendorLocationID = readDomainValue(scsDataSource,0,0, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION,"",dropOffLocationKey.getLocationCode()+dropOffLocationKey.getCarLocationCategoryCode()+dropOffLocationKey.getSupplierRawText());
                if(!CompareUtil.isObjEmpty(vendorLocationID)){
                    dropOffLocationKey.setCarVendorLocationID(Long.valueOf(vendorLocationID));
                }
            }
            carCatalogKey.setCarPickupLocationKey(pickUpLocationKey);
            carCatalogKey.setCarDropOffLocationKey(dropOffLocationKey);
            carInventoryKeyCommon.setCarCatalogKey(carCatalogKey);

            for (int i = 0; i < dateLocationNode.getChildNodes().getLength(); i++) {
                final Node childNode = dateLocationNode.getChildNodes().item(i);
                CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
                final String key = childNode.getAttributes().getNamedItem("Key").getNodeValue();
                if (carInventoryKeyMap.containsKey(key)) {
                    carInventoryKey = carInventoryKeyMap.get(key);
                } else {
                    carInventoryKey.setCarPickUpDateTime(carInventoryKeyCommon.getCarPickUpDateTime());
                    carInventoryKey.setCarDropOffDateTime(carInventoryKeyCommon.getCarDropOffDateTime());
                    carInventoryKey.setCarCatalogKey(new CarCatalogKeyType());
                    carInventoryKeyMap.put(key, carInventoryKey);
                }
                final CarLocationKeyType locationKey = new CarLocationKeyType();
                locationKey.setLocationCode(childNode.getAttributes().getNamedItem("LocationCode").getNodeValue());
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("LocationType"))) {
                    final String carLocationCategoryCode = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", childNode.getAttributes().getNamedItem("LocationType").getNodeValue());
                    locationKey.setCarLocationCategoryCode(carLocationCategoryCode);
                }
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("VendorLocationID"))) {
                    locationKey.setSupplierRawText(childNode.getAttributes().getNamedItem("VendorLocationID").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("Type")) && "Return".equals(childNode.getAttributes().getNamedItem("Type")) && StringUtils.isEmpty(locationKey.getSupplierRawText())) {
                    locationKey.setSupplierRawText(carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText());
                }

                if (!CompareUtil.isObjEmpty(locationKey.getSupplierRawText()) && locationKey.getSupplierRawText().length() == 2) {
                    locationKey.setSupplierRawText("0" + locationKey.getSupplierRawText());
                }
                if(!CompareUtil.isObjEmpty(locationKey.getLocationCode()) && !CompareUtil.isObjEmpty(locationKey.getCarLocationCategoryCode()) && !CompareUtil.isObjEmpty(locationKey.getSupplierRawText())){
                    final Long vendorSupplierID = readSupplierIDByVendorCode(carsInventoryDs, childNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
                    final String vendorLocationID = readDomainValue(scsDataSource,vendorSupplierID,0, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION,"",locationKey.getLocationCode()+locationKey.getCarLocationCategoryCode()+locationKey.getSupplierRawText());
                    if(!CompareUtil.isObjEmpty(vendorLocationID)){
                        locationKey.setCarVendorLocationID(Long.valueOf(vendorLocationID));
                    }
                }
                if (!CompareUtil.isObjEmpty(childNode.getAttributes().getNamedItem("Type")) && "Pickup".equals(childNode.getAttributes().getNamedItem("Type").getNodeValue())) {
                    carInventoryKey.getCarCatalogKey().setCarPickupLocationKey(locationKey);
                } else {
                    carInventoryKey.getCarCatalogKey().setCarDropOffLocationKey(locationKey);
                }
            }
        }
        return carInventoryKeyMap;
    }


    /// <summary>
    /// Read CarMileage from VehicleRate and SupplierRate
    /// </summary>
    /// <param name="vehicleRateNode"></param>
    /// <param name="supplierRateNode"></param>
    /// <returns></returns>
    public static CarMileageType readCarMileage(Node vehicleRateNode, Node supplierRateNode) {
        final CarMileageType carMileage = new CarMileageType();
        carMileage.setFreeDistance(CompareUtil.isObjEmpty(carMileage.getFreeDistance()) ? new DistanceType() : carMileage.getFreeDistance());
        carMileage.setExtraCostPerDistance(CompareUtil.isObjEmpty(carMileage.getExtraCostPerDistance()) ? new CostPerDistanceType() : carMileage.getExtraCostPerDistance());
        carMileage.getExtraCostPerDistance().setDistance(CompareUtil.isObjEmpty(carMileage.getExtraCostPerDistance().getDistance()) ? new DistanceType() : carMileage.getExtraCostPerDistance().getDistance());
        carMileage.getExtraCostPerDistance().setCostCurrencyAmount(CompareUtil.isObjEmpty(carMileage.getExtraCostPerDistance().getCostCurrencyAmount()) ? new CurrencyAmountType() : carMileage.getExtraCostPerDistance().getCostCurrencyAmount());
        carMileage.getExtraCostPerDistance().getCostCurrencyAmount().setAmount(CompareUtil.isObjEmpty(carMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount()) ? new AmountType() : carMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount());
        if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("UnlimitedMileage")) && Boolean.parseBoolean(vehicleRateNode.getAttributes().getNamedItem("UnlimitedMileage").getNodeValue())) {
            carMileage.getFreeDistance().setDistanceUnitCount(-1);
        } else {
            ///Update by Qiuhua for the case: No UnlimitedMileage, and also no MileageAllowance
            ///	<vehicle:Vehicle AirConditioning="false" Category="RegularCabPickup" Location="NorthOfCityCenter" TransmissionType="Automatic" VehicleClass="Standard" VendorCode="ZL" VendorLocationKey="WDp9EAdwQSmO6sRp8ZK+Ug==">
            ///<vehicle:VehicleRate RateAvailability="Call" RateCategory="Standard" RateCode="DR01" RateGuaranteed="true" RatePeriod="Weekly" RateSource="Source" Units="KM">
            ///<vehicle:SupplierRate EstimatedTotalAmount="CAD3905.94" ExtraMileageCharge="CAD2.00" RateForPeriod="CAD3465.00"></vehicle:SupplierRate>
            ///<vehicle:VehicleRateDescription>
            ///<vehicle:Text>3/4 TON REG CAB PKP AUTO 2WD OR SIMILAR</vehicle:Text>
            ///</vehicle:VehicleRateDescription>
            ///<vehicle:RateHostIndicator InventoryToken="NNWXS" RateToken="W2"></vehicle:RateHostIndicator>
            ///</vehicle:VehicleRate>
            ///</vehicle:Vehicle>
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("MileageAllowance"))) {
                carMileage.getFreeDistance().setDistanceUnitCount(Integer.parseInt(vehicleRateNode.getAttributes().getNamedItem("MileageAllowance").getNodeValue()));
            }
            carMileage.getFreeDistance().setDistanceUnit(vehicleRateNode.getAttributes().getNamedItem("Units").getNodeValue());
            carMileage.getExtraCostPerDistance().getDistance().setDistanceUnitCount(1);
            carMileage.getExtraCostPerDistance().getDistance().setDistanceUnit(vehicleRateNode.getAttributes().getNamedItem("Units").getNodeValue());
            carMileage.getExtraCostPerDistance().getCostCurrencyAmount().setCurrencyCode(supplierRateNode.getAttributes().getNamedItem("ExtraMileageCharge").getNodeValue().substring(0, 3));
            final String extraDisAmount = supplierRateNode.getAttributes().getNamedItem("ExtraMileageCharge").getNodeValue().substring(3);
            carMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimal(Integer.parseInt(extraDisAmount.replace(".", "")));
            carMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimalPlaceCount(extraDisAmount.contains(".") ? extraDisAmount.split("\\.")[1].length() : 0);
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RatePeriod"))) {
                carMileage.setFreeDistanceRatePeriodCode(vehicleRateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue());
            }
        }
        return carMileage;
    }

    // <summary>
    // Read CostList
    // </summary>
    // <param name="domainValueMapHelper"></param>
    // <param name="supplierRateNode"></param>
    // <param name="ratePeriodCode"></param>
    // LegacyFinanceKey values refer to wiki: https://confluence/display/SSG/Maserati+SCS+Costs
    // <returns></returns>
    public static CostListType readCostList(Node supplierRateNode, String ratePeriodCode, long rentalDays) {
        final CostListType costListType = new CostListType();
        final List<CostType> costTypeList = new ArrayList<CostType>();

        //Get currencyCode
        final String currencyCode = supplierRateNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(0, 3);

        //get base/baseRateTotal/Total and then calculate base count/MiscBase/Misc
        final double baseRateTotal = CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("BaseRate")) ? -1 : Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("BaseRate").getNodeValue().substring(3));
        double baseRate = Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(3));
        final double totalRate = Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("EstimatedTotalAmount").getNodeValue().substring(3));
        double dropoffCharge = 0;
        if (!CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("DropOffCharge"))) {
            dropoffCharge = Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("DropOffCharge").getNodeValue().substring(3));
        }

        //Calculate week count and month count based on rental days
        final int weekCount = (int)Math.floor((double) rentalDays / 7);
        final int monthCount = (int)Math.floor((double) rentalDays / 30);

        int baseCount = 1;
        if (baseRateTotal > 0 && !"Trip".equals(ratePeriodCode)) {
            baseCount = (int)Math.floor(baseRateTotal / baseRate);
        } else if (baseRateTotal > 0) {
            //According to bug 1047743, when ratePeriodCode is "Trip", baseRateTotal should be taken as base
            baseRate = baseRateTotal;
        }
        if ("Daily".equals(ratePeriodCode) && rentalDays > 0) {
            baseCount = (int)rentalDays;
        } else if ("Weekly".equals(ratePeriodCode) && weekCount > 0) {
            baseCount = weekCount;
        } else if ("Monthly".equals(ratePeriodCode) && monthCount > 0) {
            baseCount = monthCount;
        }

        final double baseTotal_Calculated = baseRateTotal > 0 ? baseRateTotal : baseRate * baseCount;
        final double misc = totalRate - baseTotal_Calculated - dropoffCharge;

        //Base
        final int baseLegacyMonetaryCalculationID = CommonEnumManager.BaseCostLegacyMonetaryCalculationID.valueOf(ratePeriodCode).getBaseCostLegacyMonetaryCalculationID();
        final int baseDecimalPC = String.format("%.3f",baseRate).contains(".") ? String.format("%.3f",baseRate).split("\\.")[1].length() : 0;
        final int baseAmountDecimal = (int) (baseRate * Math.pow(10, baseDecimalPC));
        if (baseRateTotal > 0) {
            baseCount = (int)((baseRateTotal + 0.01) / baseRate);
        } else {
            baseCount = (int)((totalRate - misc - dropoffCharge + 0.01) / baseRate);
        }
        if (baseCount == 0) {
            baseCount = 1;
        }
        costTypeList.add(buildCost(baseAmountDecimal, baseDecimalPC, currencyCode, "Base", ratePeriodCode, (long) baseCount, 1, 1,
                baseLegacyMonetaryCalculationID, "", false));

        //Misc
        if (misc != 0) {
            final int decimalPC = String.format("%.3f",misc).contains(".") ? String.format("%.3f",misc).split("\\.")[1].length() : 0;
            final int amountDecimal = (int) (misc * Math.pow(10, decimalPC));
            if (amountDecimal != 0) {
                costTypeList.add(buildCost(amountDecimal, decimalPC, currencyCode, "Misc", "Trip", 1, 8, 1, 6, "", false));
            }

        }

        //DropOffCharge
        if (dropoffCharge != 0) {
            final int decimalPC = String.format("%.3f",dropoffCharge).contains(".") ? String.format("%.3f",dropoffCharge).split("\\.")[1].length() : 0;
            final int amountDecimal = (int) (dropoffCharge * Math.pow(10, decimalPC));
            costTypeList.add(buildCost(amountDecimal, decimalPC, currencyCode, "Fee", "Trip", 1, 18, 1, 27, "", false));
        }

        //Total
        final int totalDecimalPC = String.format("%.3f",totalRate).contains(".") ? String.format("%.3f",totalRate).split("\\.")[1].length() : 0;
        final int totalAmountDecimal = (int) (totalRate * Math.pow(10, totalDecimalPC));
        costTypeList.add(buildCost(totalAmountDecimal, totalDecimalPC, currencyCode, "Total", "Total", 1, 0, 0, 0, "", false));


        final double miscBase = totalRate - baseRate * baseCount - misc - dropoffCharge;
        if (Math.abs(miscBase) > 0.001) {
            final int decimalPCForMiscBase = String.format("%.3f",miscBase).contains(".") ? String.format("%.3f",miscBase).split("\\.")[1].length() : 0;
            final int amountDecimalForMiscBase = (int) (miscBase * Math.pow(10, decimalPCForMiscBase));
            if (amountDecimalForMiscBase != 0) {
                costTypeList.add(buildCost(amountDecimalForMiscBase, decimalPCForMiscBase, currencyCode, "MiscBase", "Trip", 1, 8, 1, 6, "", false));
            }
        }
        costListType.setCost(costTypeList);
        return costListType;
    }

    /// <summary>
    /// Build costList for reserve
    /// </summary>
    /// <param name="domainValueMapHelper"></param>
    /// <param name="vehicleRate"></param>
    /// <returns></returns>
    public static CostListType readCostListReserve(DataSource scsDataSource, Node vehicleRate) throws DataAccessException {
        final CostListType costListType = new CostListType();
        final List<CostType> costTypeList = new ArrayList<CostType>();
        if (vehicleRate != null) {
            //Get RatePeriod
            String ratePeriod = "";
            if (!CompareUtil.isObjEmpty(vehicleRate.getAttributes().getNamedItem("RatePeriod")) && !CompareUtil.isObjEmpty(vehicleRate.getAttributes().getNamedItem("RatePeriod").getNodeValue())) {
                ratePeriod = readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.RATE_PERIOD, "", vehicleRate.getAttributes().getNamedItem("RatePeriod").getNodeValue());
            }
            ratePeriod = CompareUtil.isObjEmpty(ratePeriod) ? "Trip" : ratePeriod;

            //SupplierRate to get base value
            final Node supplierRate = PojoXmlUtil.getNodeByTagName(vehicleRate, "SupplierRate");
            String baseCurrency = null;
            if (!CompareUtil.isObjEmpty(supplierRate) && !CompareUtil.isObjEmpty(supplierRate.getAttributes().getNamedItem("RateForPeriod").getNodeValue())) {
                final double baseRate = Double.parseDouble(supplierRate.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(3));
                baseCurrency = supplierRate.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(0, 3);
                //Build base Cost
                final int baseLegacyMonetaryCalculationID = CommonEnumManager.BaseCostLegacyMonetaryCalculationID.valueOf(ratePeriod).getBaseCostLegacyMonetaryCalculationID();
                final int baseDecimalPC = String.format("%.3f",baseRate).contains(".") ? String.format("%.3f",baseRate).split("\\.")[1].length() : 0;
                final int baseAmountDecimal = (int) (baseRate * Math.pow(10, baseDecimalPC));
                costTypeList.add(buildCost(baseAmountDecimal, baseDecimalPC, baseCurrency, "Base", ratePeriod, 0, 1, 1, baseLegacyMonetaryCalculationID, "", false));
            }

            //Get total rate from VehicleRateDescription
            final Node vehicleRateDescription = PojoXmlUtil.getNodeByTagName(vehicleRate, "VehicleRateDescription");
            if (!CompareUtil.isObjEmpty(vehicleRateDescription)) {
                final String totalText = PojoXmlUtil.getNodeByTagName(vehicleRate, "Text").getTextContent();
                final String[] totalTextList = totalText.split(" ");
                String totalCurrency = "";
                double totalRate = 0;
                for (int i = 0; i < totalTextList.length; i++) {
                    //Different format may returned from VCRR response
                    //E.g: 192.23 USD
                    if (totalTextList[i].length() > 0 && CompareUtil.isDouble(totalTextList[i])) {
                        totalRate = Double.parseDouble(totalTextList[i]);
                        //It is for 'EST TOTAL CHARGE 2718.83'
                        if (totalTextList.length >= i + 2) {
                            totalCurrency = totalTextList[i + 1];
                        } else {
                            totalCurrency = baseCurrency;
                        }
                        break;
                    } else if (totalTextList[i].length() >= 3 && CompareUtil.isDouble(totalTextList[i].substring(3))) {
                        //USD192.23
                        totalRate = Double.parseDouble(totalTextList[i].substring(3));
                        totalCurrency = totalTextList[i].substring(0, 3);
                        break;
                    } else if (totalTextList[i].length() > 3 && CompareUtil.isDouble(totalTextList[i].substring(0, totalTextList[i].length() - 3))) {
                        totalRate = Double.parseDouble(totalTextList[i].substring(0, totalTextList[i].length() - 3));
                        //192.23USD
                        totalCurrency = totalTextList[i].substring(totalTextList[i].length() - 3);
                        break;
                    }
                }

                //<vehicle:Text>TOTAL RENTAL CHARGE EUR 107.22 FOR 3 DYINCL LOCATION SURCHARGE,HSS MAND TAXES,LAF 2.70 </vehicle:Text>
                totalCurrency = baseCurrency;
                //Total
                final int totalDecimalPC = String.format("%.3f",totalRate).contains(".") ? String.format("%.3f",totalRate).split("\\.")[1].length() : 0;
                final int totalAmountDecimal = (int) (totalRate * Math.pow(10, totalDecimalPC));
                costTypeList.add(buildCost(totalAmountDecimal, totalDecimalPC, totalCurrency, "Total", "Total", 1, 0, 0, 0, "", false));
            }
        }
        costListType.setCost(costTypeList);
        return costListType;
    }


    public static CostListType buildCostListGetDetails(Node supplierRateNode, List<Node> vehicleChargeList, String ratePeriodCode, int numberOfPeriods, boolean needSpoofer) {
        final CostListType costList = new CostListType();
        final List<CostType> costTypeList = new ArrayList<CostType>();
        //Get currencyCode
        String currencyCode = "USD";
        if (CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("RateForPeriod")) && !CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("EstimatedTotalAmount"))) {
            currencyCode = supplierRateNode.getAttributes().getNamedItem("EstimatedTotalAmount").getNodeValue().substring(0, 3);
        } else if (!CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("RateForPeriod"))) {
            currencyCode = supplierRateNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(0, 3);
        }

        /// other fee calculate
        double otherTaxAndFee = 0;
        for (final Node chargeNode : vehicleChargeList) {
            final String catagoryCode = chargeNode.getAttributes().getNamedItem("Category").getNodeValue();
            if ("Tax".equals(catagoryCode) || "Surcharge".equals(catagoryCode)) {
                //No charge already be Included in Total
                if (!CompareUtil.isObjEmpty(chargeNode.getAttributes().getNamedItem("Type")) && !"NoCharge".equals(chargeNode.getAttributes().getNamedItem("Type").getNodeName().trim())) {
                    otherTaxAndFee += Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeNode, "Amount").getTextContent().substring(3));
                }
            }
        }

        // discountAmount
        double discountAmount = 0;
        if (!CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("DiscountAmount"))) {
            discountAmount = (-1) * Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("DiscountAmount").getNodeValue().substring(3));//discount is negative
        }

        //get base/baseRateTotal/Total and then calculate base count/MiscBase/Misc
        //Base on bug 1050281, MiscBase only return in Search and GetCostAndAvail, not in Getdetails
        final double baseRateTotal = CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("BaseRate")) ? -1 : Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("BaseRate").getNodeValue().substring(3));
        double baseRate = CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("RateForPeriod")) ? 0 : Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(3));
        //LUCust has baseRate =0 but baseRateTotal >0 scenario Edit by Qiuhua at 6/28/2015
        if (baseRate == 0 && baseRateTotal > 0) {
            baseRate = baseRateTotal;
        }
        final double totalRate = Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("EstimatedTotalAmount").getNodeValue().substring(3));
        double dropoffCharge = 0;
        if (!CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("DropOffCharge"))) {
            dropoffCharge = Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("DropOffCharge").getNodeValue().substring(3));
        }
        //uint baseCount = baseRateTotal > 0 ? (uint)Math.Floor(baseRateTotal / baseRate) : 1;
        final int baseCount = numberOfPeriods; // we have basecount return in VRUR response, needn't caculate it
        //double miscBase = baseRateTotal > 0 ? baseRateTotal - baseRate * baseCount : 0;

        //Surcharge has not Attributes = "Type", Edit by Qiuhua at 6/28/2015
        final double mandatoryChargeTotal = CompareUtil.isObjEmpty(supplierRateNode.getAttributes().getNamedItem("MandatoryChargeTotal")) ? 0 : Double.parseDouble(supplierRateNode.getAttributes().getNamedItem("MandatoryChargeTotal").getNodeValue().substring(3));
        if (otherTaxAndFee == 0 && mandatoryChargeTotal > 0) {
            otherTaxAndFee = mandatoryChargeTotal;
            if (dropoffCharge > 0) {
                otherTaxAndFee = otherTaxAndFee - dropoffCharge;
            }
        }
        //double misc = totalRate - baseRate * baseCount - dropoffCharge - otherTaxAndFee - discountAmount;
        //double misc = totalRate - baseRate * baseCount - otherTaxAndFee - discountAmount;
        double misc = 0;
        if (needSpoofer) {
            misc = totalRate - baseRate * baseCount - dropoffCharge - otherTaxAndFee - discountAmount;
        } else {
            misc = totalRate - mandatoryChargeTotal - baseRate * baseCount;
        }


        //Base
        if (Math.abs(baseRate) > 0.001) {
            final int baseLegacyMonetaryCalculationID = CommonEnumManager.BaseCostLegacyMonetaryCalculationID.valueOf(ratePeriodCode).getBaseCostLegacyMonetaryCalculationID();
            final int baseDecimalPC = String.format("%.3f",baseRate).contains(".") ? String.format("%.3f",baseRate).split("\\.")[1].length() : 0;
            final int baseAmountDecimal = (int) (baseRate * Math.pow(10, baseDecimalPC));
            costTypeList.add(buildCost(baseAmountDecimal, baseDecimalPC, currencyCode, "Base", ratePeriodCode, baseCount, 1, 1, baseLegacyMonetaryCalculationID, "Base", false));
        }

        //Misc
        if (Math.abs(misc) > 0.001) {
            final int decimalPC = String.format("%.3f",misc).contains(".") ? String.format("%.3f",misc).split("\\.")[1].length() : 0;
            final int amountDecimal = (int) (misc * Math.pow(10, decimalPC));
            if (amountDecimal != 0) {
                costTypeList.add(buildCost(amountDecimal, decimalPC, currencyCode, "Misc", "Trip", 1, 8, 1, 6, "Misc", false));
            }
        }


        //TotalDiscount
        if (Math.abs(discountAmount) > 0.001) {
            final int decimalPC = String.format("%.3f",discountAmount).contains(".") ? String.format("%.3f",discountAmount).split("\\.")[1].length() : 0;
            final int amountDecimal = (int) (discountAmount * Math.pow(10, decimalPC));
            if (amountDecimal != 0) {
                costTypeList.add(buildCost(amountDecimal, decimalPC, currencyCode, "TotalDiscount", "Trip", 1, 0, 0, 0, "TotalDiscount", false));
            }
        }

        //DropOffCharge
        if (dropoffCharge != 0) {
            final int decimalPC = String.format("%.3f",dropoffCharge).contains(".") ? String.format("%.3f",dropoffCharge).split("\\.")[1].length() : 0;
            final int amountDecimal = (int) (dropoffCharge * Math.pow(10, decimalPC));
            costTypeList.add(buildCost(amountDecimal, decimalPC, currencyCode, "Fee", "Trip", 1, 18, 1, 27, "Drop Charge", false));
        }

        //Total
        final int totalDecimalPC = String.format("%.3f",totalRate).contains(".") ? String.format("%.3f",totalRate).split("\\.")[1].length() : 0;
        final int totalAmountDecimal = (int) (totalRate * Math.pow(10, totalDecimalPC));
        costTypeList.add(buildCost(totalAmountDecimal, totalDecimalPC, currencyCode, "Total", "Total", 1, 0, 0, 0, "Total", false));

        costList.setCost(costTypeList);
        return costList;
    }

    /// <summary>
    /// Read branch code from uAPI request attribute - TargetBranch
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public static String readBranchCode(Node xmlDoc) {
        String branchCode = null;
        if (!CompareUtil.isObjEmpty(xmlDoc) && !CompareUtil.isObjEmpty(xmlDoc.getAttributes().getNamedItem("TargetBranch"))) {
            branchCode = xmlDoc.getAttributes().getNamedItem("TargetBranch").getNodeValue();
        }
        return branchCode;
    }

    public static CostType buildCost(int amountDecimal, int decimalPlaceCount, String currencyCode, String financeCategoryCode,
                                     String financeApplicationCode, long financeApplicationUnitCount, int legacyMonetaryClassID,
                                     int legacyMonetaryCalculationSystemID, int legacyMonetaryCalculationID, String description,
                                     boolean buildZeroUnitCount) {
        final CostType cost = new CostType();
        final MultiplierOrAmountType multiplierOrAmountType = new MultiplierOrAmountType();
        cost.setMultiplierOrAmount(multiplierOrAmountType);
        if (CompareUtil.isObjEmpty(currencyCode)) {
            if (CompareUtil.isObjEmpty(multiplierOrAmountType.getMultiplier())) {
                multiplierOrAmountType.setMultiplier(new MultiplierType());
            }
            multiplierOrAmountType.getMultiplier().setDecimal(amountDecimal);
            multiplierOrAmountType.getMultiplier().setDecimalPlaceCount(decimalPlaceCount);
        } else {
            if (CompareUtil.isObjEmpty(multiplierOrAmountType.getCurrencyAmount())) {
                multiplierOrAmountType.setCurrencyAmount(new CurrencyAmountType());
            }
            if (CompareUtil.isObjEmpty(multiplierOrAmountType.getCurrencyAmount().getAmount())) {
                multiplierOrAmountType.getCurrencyAmount().setAmount(new AmountType());
            }
            multiplierOrAmountType.getCurrencyAmount().setCurrencyCode(currencyCode);
            multiplierOrAmountType.getCurrencyAmount().getAmount().setDecimal(amountDecimal);
            multiplierOrAmountType.getCurrencyAmount().getAmount().setDecimalPlaceCount(decimalPlaceCount);
        }

        cost.setFinanceCategoryCode(financeCategoryCode);
        cost.setFinanceApplicationCode(financeApplicationCode);
        if (financeApplicationUnitCount != 0 || buildZeroUnitCount) {
            cost.setFinanceApplicationUnitCount(financeApplicationUnitCount);
        }
        if (buildZeroUnitCount && financeApplicationUnitCount == 0) {
            cost.setFinanceApplicationCode("Included");
        }

        if (CompareUtil.isObjEmpty(cost.getLegacyFinanceKey())) {
            cost.setLegacyFinanceKey(new LegacyFinanceKeyType());
        }
        cost.getLegacyFinanceKey().setLegacyMonetaryClassID(legacyMonetaryClassID);
        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(legacyMonetaryCalculationSystemID);
        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(legacyMonetaryCalculationID);
        if (!CompareUtil.isObjEmpty(description)) {
            cost.setDescriptionRawText(description);
        }
        return cost;
    }

    // <summary>
    // Build TravelerList
    // </summary>
    // <param name="travelerNodeList"></param>
    // <returns></returns>
    public static TravelerListType readTravelerList(List<Node> travelerNodeList) {
        final TravelerListType travelerListType = new TravelerListType();
        final List<TravelerType> travelerTypeList = new ArrayList<TravelerType>();
        for (final Node travelerNode : travelerNodeList) {
            final TravelerType travelerType = new TravelerType();
            //Name
            final Node travNameNode = PojoXmlUtil.getNodeByTagName(travelerNode, "BookingTravelerName");
            final PersonType person = new PersonType();
            final PersonNameType personNameType = new PersonNameType();
            if (!CompareUtil.isObjEmpty(travNameNode.getAttributes().getNamedItem("First"))) {
                personNameType.setFirstName(travNameNode.getAttributes().getNamedItem("First").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(travNameNode.getAttributes().getNamedItem("Last"))) {
                personNameType.setLastName(travNameNode.getAttributes().getNamedItem("Last").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(travNameNode.getAttributes().getNamedItem("Prefix"))) {
                personNameType.setPersonalTitle(travNameNode.getAttributes().getNamedItem("Prefix").getNodeValue());
            }
            person.setPersonName(personNameType);
            travelerType.setPerson(person);

            //Contact info
            final ContactInformationType contactInformationType = new ContactInformationType();
            //Phone number
            final PhoneListType phoneListType = new PhoneListType();
            final List<PhoneType> phoneTypeList = new ArrayList<PhoneType>();
            final List<Node> phoneNumNodeList = PojoXmlUtil.getNodesByTagName(travelerNode, "PhoneNumber");
            for (final Node phoneNumNode : phoneNumNodeList) {
                final PhoneType phoneType = new PhoneType();
                if (!CompareUtil.isObjEmpty(phoneNumNode.getAttributes().getNamedItem("AreaCode"))) {
                    phoneType.setPhoneAreaCode(phoneNumNode.getAttributes().getNamedItem("AreaCode").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(phoneNumNode.getAttributes().getNamedItem("CountryCode"))) {
                    phoneType.setPhoneCountryCode(phoneNumNode.getAttributes().getNamedItem("CountryCode").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(phoneNumNode.getAttributes().getNamedItem("Number"))) {
                    phoneType.setPhoneNumber(phoneNumNode.getAttributes().getNamedItem("Number").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(phoneNumNode.getAttributes().getNamedItem("Extension"))) {
                    phoneType.setPhoneExtensionNumber(phoneNumNode.getAttributes().getNamedItem("Extension").getNodeValue());
                }

                phoneTypeList.add(phoneType);
            }
            phoneListType.setPhone(phoneTypeList);
            contactInformationType.setPhoneList(phoneListType);
            //eMail
            final EmailAddressEntryListType emailAddressEntryListType = new EmailAddressEntryListType();
            final List<EmailAddressEntryType> emailAddressEntryTypeList = new ArrayList<EmailAddressEntryType>();
            final List<Node> emailNodeList = PojoXmlUtil.getNodesByTagName(travelerNode, "Email");
            for (final Node emailNode : emailNodeList) {
                final EmailAddressEntryType emailAddressEntryType = new EmailAddressEntryType();
                if (!CompareUtil.isObjEmpty(emailNode.getAttributes().getNamedItem("EmailID"))) {
                    emailAddressEntryType.setEmailAddress(emailNode.getAttributes().getNamedItem("EmailID").getNodeValue());
                }
                emailAddressEntryTypeList.add(emailAddressEntryType);
            }
            emailAddressEntryListType.setEmailAddressEntry(emailAddressEntryTypeList);
            contactInformationType.setEmailAddressEntryList(emailAddressEntryListType);

            travelerType.setContactInformation(contactInformationType);

            //LoyaltyNumber
            final LoyaltyProgramListType loyaltyProgramListType = new LoyaltyProgramListType();
            final List<LoyaltyProgramType> loyaltyProgramTypeList = new ArrayList<LoyaltyProgramType>();
            final List<Node> loyaltyCardNL = PojoXmlUtil.getNodesByTagName(travelerNode, "LoyaltyCard");
            for (final Node loyaltyCardN : loyaltyCardNL) {
                final LoyaltyProgramType loyaltyProgramType = new LoyaltyProgramType();
                if (!CompareUtil.isObjEmpty(loyaltyCardN.getAttributes().getNamedItem("SupplierType"))) {
                    if ("Vehicle".equals(loyaltyCardN.getAttributes().getNamedItem("SupplierType").getNodeValue())) {
                        loyaltyProgramType.setLoyaltyProgramCategoryCode("Car");
                    }
                    if ("Air".equals(loyaltyCardN.getAttributes().getNamedItem("SupplierType").getNodeValue())) {
                        loyaltyProgramType.setLoyaltyProgramCategoryCode("Air");
                    }
                }
                if (!CompareUtil.isObjEmpty(loyaltyCardN.getAttributes().getNamedItem("SupplierCode"))) {
                    loyaltyProgramType.setLoyaltyProgramCode(loyaltyCardN.getAttributes().getNamedItem("SupplierCode").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(loyaltyCardN.getAttributes().getNamedItem("CardNumber"))) {
                    loyaltyProgramType.setLoyaltyProgramMembershipCode(loyaltyCardN.getAttributes().getNamedItem("CardNumber").getNodeValue());
                }
                loyaltyProgramTypeList.add(loyaltyProgramType);
            }
            loyaltyProgramListType.setLoyaltyProgram(loyaltyProgramTypeList);
            travelerType.setLoyaltyProgramList(loyaltyProgramListType);

            travelerTypeList.add(travelerType);
        }
        travelerListType.setTraveler(travelerTypeList);
        return travelerListType;
    }

    // <summary>
    // Read CarReservationRemarkList
    // </summary>
    // <param name="generalRemarkNL"></param>
    // <returns></returns>
    public static CarReservationRemarkListType readCarReservationRemarkList(List<Node> generalRemarkNL) {
        final CarReservationRemarkListType carReservationRemarkListType = new CarReservationRemarkListType();
        final List<CarReservationRemarkType> carReservationRemarkTypeList = new ArrayList<CarReservationRemarkType>();
        for (final Node generalRemarkN : generalRemarkNL) {
            final CarReservationRemarkType remark = new CarReservationRemarkType();
            //CategoryCode
            if (!CompareUtil.isObjEmpty(generalRemarkN.getAttributes().getNamedItem("Category"))) {
                remark.setCarReservationRemarkCategoryCode(generalRemarkN.getAttributes().getNamedItem("Category").getNodeValue());
            }
            //RemarkData
            final Node remarkDN = PojoXmlUtil.getNodeByTagName(generalRemarkN, "RemarkData");
            remark.setCarReservationRemarkText(remarkDN.getTextContent());

            carReservationRemarkTypeList.add(remark);
        }
        carReservationRemarkListType.setCarReservationRemark(carReservationRemarkTypeList);
        return carReservationRemarkListType;
    }

    /// <summary>
    /// Read ReferenceList from ProviderReservationInfo and VehicleReservation node
    /// </summary>
    /// <param name="providerReservationInfo"></param>
    /// <param name="vehicleReservation"></param>
    /// <returns></returns>
    public static ReferenceListType readReferenceList(Node providerReservationInfo, Node vehicleReservation) {
        final ReferenceListType referenceListType = new ReferenceListType();
        final List<ReferenceType> referenceTypeList = new ArrayList<ReferenceType>();
        //PNR from providerReservationInfo
        if (!CompareUtil.isObjEmpty(providerReservationInfo) && !CompareUtil.isObjEmpty(providerReservationInfo.getAttributes().getNamedItem("LocatorCode"))) {
            final ReferenceType referenceType = new ReferenceType();
            referenceType.setReferenceCode(providerReservationInfo.getAttributes().getNamedItem("LocatorCode").getNodeValue());
            referenceType.setReferenceCategoryCode("PNR");
            referenceTypeList.add(referenceType);
        }

        //Vendor from vehicleReservation/BookingConfirmation
        if (!CompareUtil.isObjEmpty(vehicleReservation) && !CompareUtil.isObjEmpty(vehicleReservation.getAttributes().getNamedItem("BookingConfirmation"))) {
            final ReferenceType referenceType = new ReferenceType();
            referenceType.setReferenceCode(vehicleReservation.getAttributes().getNamedItem("BookingConfirmation").getNodeValue());
            referenceType.setReferenceCategoryCode("Vendor");
            referenceTypeList.add(referenceType);
        }

        //Segment from vehicleReservation/BookingConfirmation
        if (!CompareUtil.isObjEmpty(vehicleReservation) && !CompareUtil.isObjEmpty(vehicleReservation.getAttributes().getNamedItem("TravelOrder"))) {
            final ReferenceType referenceType = new ReferenceType();
            referenceType.setReferenceCode(vehicleReservation.getAttributes().getNamedItem("TravelOrder").getNodeValue());
            referenceType.setReferenceCategoryCode("Segment");
            referenceTypeList.add(referenceType);
        }
        referenceListType.setReference(referenceTypeList);
        return referenceListType;
    }

    //<com:Guarantee Type="Guarantee" xmlns:com="http://www.travelport.com/schema/common_v30_0">
    //    <com:CreditCard ExpDate="2015-12" Number="5105781454975390" Type="CA"></com:CreditCard>
    //</com:Guarantee>
    /// <summary>
    /// Read CCInfo
    /// </summary>
    /// <param name="FormOfPayment"></param>
    /// <returns></returns>
    public static String readCCInfo(Node xmlDoc) {
        final Node creditCard = PojoXmlUtil.getNodeByTagName(xmlDoc, "CreditCard");
        String creditCardInfo = null;
        //Combince type_number_ExpDate to one string, like CAXXXXXXXXXXXX5390EXP2014-12
        if (!CompareUtil.isObjEmpty(creditCard)) {
            creditCardInfo = creditCard.getAttributes().getNamedItem("Type").getNodeValue() + creditCard.getAttributes().getNamedItem("Number").getNodeValue()
                    + "EXP" + creditCard.getAttributes().getNamedItem("ExpDate").getNodeValue();
        }
        return creditCardInfo;
    }

    /// <summary>
    /// read voucher
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public static String readVoucher(Node xmlDoc) {
        //Read voucher from Voucher node   <veh:Voucher Number="039ZRK1" VoucherType="RegularVoucher"></veh:Voucher>
        final Node voucher = PojoXmlUtil.getNodeByTagName(xmlDoc, "Voucher");
        if (!CompareUtil.isObjEmpty(voucher)) {
            return voucher.getAttributes().getNamedItem("Number").getNodeValue();
        }
        return null;
    }

    /// <summary>
    /// read BillingNumber
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public static String readBillingNumber(Node xmlDoc) {
        //Read voucher from Voucher node   <veh:Voucher Number="039ZRK1" VoucherType="RegularVoucher"></veh:Voucher>
        final Node paymentInformation = PojoXmlUtil.getNodeByTagName(xmlDoc, "PaymentInformation");
        if (!CompareUtil.isObjEmpty(paymentInformation) && !CompareUtil.isObjEmpty(paymentInformation.getAttributes().getNamedItem("BillingNumber"))) {
            return paymentInformation.getAttributes().getNamedItem("BillingNumber").getNodeValue();
        }
        return null;
    }

    /// <summary>
    /// Read SpecialEquipmentList
    /// </summary>
    /// <param name="domainValueMapHelper"></param>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public static CarSpecialEquipmentListType readSpecialEquipmentList(DataSource scsDataSource, Node xmlDoc) throws DataAccessException {
        final CarSpecialEquipmentListType specialEqupListType = new CarSpecialEquipmentListType();
        final List<CarSpecialEquipmentType> specialEquipmentTypeList = new ArrayList<CarSpecialEquipmentType>();
        //<com:SpecialEquipment Type="InfantSeat" xmlns:com="http://www.travelport.com/schema/common_v32_0"></com:SpecialEquipment>
        //<com:SpecialEquipment Type="NavigationalSystem" xmlns:com="http://www.travelport.com/schema/common_v32_0"></com:SpecialEquipment>
        final List<Node> speEquipList = PojoXmlUtil.getNodesByTagName(xmlDoc, "SpecialEquipment");
        for (final Node node : speEquipList) {
            final CarSpecialEquipmentType carSpeEquipType = new CarSpecialEquipmentType();
            if (!CompareUtil.isObjEmpty(node.getAttributes().getNamedItem("Type"))) {
                carSpeEquipType.setCarSpecialEquipmentCode(readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_SPECIAL_EQUIPMENT, "", node.getAttributes().getNamedItem("Type").getNodeValue()));
            }
            specialEquipmentTypeList.add(carSpeEquipType);
        }
        specialEqupListType.setCarSpecialEquipment(specialEquipmentTypeList);
        return specialEqupListType;
    }

    /// <summary>
    /// read AdvisoryTextList from SellMessage
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public static AdvisoryTextListType readAdvisoryTextList(Node xmlDoc) {
        final String sellMessage = PojoXmlUtil.getNodeByTagName(xmlDoc, "SellMessage") == null ? "" : PojoXmlUtil.getNodeByTagName(xmlDoc, "SellMessage").getTextContent();
        if (!CompareUtil.isObjEmpty(sellMessage)) {
            final AdvisoryTextListType advisoryTextListType = new AdvisoryTextListType();
            final List<String> textList = new ArrayList<String>();
            textList.add(sellMessage);
            advisoryTextListType.setAdvisoryText(textList);
            return advisoryTextListType;
        }
        return null;
    }

    // <summary>
    //  Query ExternalSupplyServiceDomainValueMap table to get mapping info
    // </summary>
    public static String readDomainValue(DataSource scsDataSource, long supplierID, long messageSystemID, String domainType, String domainValue, String externalDomainValue) throws DataAccessException {
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        final List<ExternalSupplyServiceDomainValueMap> domainValueMaps = scsHelper.getExternalSupplyServiceDomainValueMap(supplierID, messageSystemID, domainType, domainValue, externalDomainValue);
        return CompareUtil.isObjEmpty(domainValueMaps) ? "" : domainValueMaps.get(0).getDomainValue();
    }

    public static String readExternalDomainValue(DataSource scsDataSource, long supplierID, long messageSystemID, String domainType, String domainValue, String externalDomainValue) throws DataAccessException {
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        final List<ExternalSupplyServiceDomainValueMap> domainValueMaps = scsHelper.getExternalSupplyServiceDomainValueMap(supplierID, messageSystemID, domainType, domainValue, externalDomainValue);
        return CompareUtil.isObjEmpty(domainValueMaps) ? "" : domainValueMaps.get(0).getExternalDomainValue();
    }

    @SuppressWarnings("CPD-END")
    public static Long readSupplierIDByVendorCode(DataSource carsInventoryDs, String vendorCode) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        final List<CarVendor> carVendorList = inventoryHelper.getCarVendorList(vendorCode);
        return CompareUtil.isObjEmpty(carVendorList) ? 0 : Long.valueOf(carVendorList.get(0).getSupplierID());
    }


}
