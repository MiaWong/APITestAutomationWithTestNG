package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionListType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4
        .CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4
        .CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiyu on 8/25/16.
 */
public final class PropertyResetHelper
{
    //  special equipment strings used in cert test scenario
    public static final String STRING_SPECIAL_EQUIPMENT_GPS = "NavigationalSystem";
    public static final String STRING_SPECIAL_EQUIPMENT_TODDLER_SEAT = "ToddlerChildSeat";


    private PropertyResetHelper() {}

    //  set special equipment : GPS + toddler seat etc.
    public static void setSpecialEquipment(CarSupplyConnectivityReserveRequestType reserveRequest, List<String> specialEquipmentList)
    {
        List<String> sepList = specialEquipmentList;
        if (sepList == null || sepList.isEmpty()) {
            sepList = new ArrayList<>();
            sepList.add(STRING_SPECIAL_EQUIPMENT_GPS);
            sepList.add(STRING_SPECIAL_EQUIPMENT_TODDLER_SEAT);
        }
        reserveRequest.setCarSpecialEquipmentList(CommonDataTypesGenerator.createSpecialEquipmentList(sepList));

        // Mapping special equipment to car vehicle optionlist as well to see if it gets mapped in XSLT
        final CarVehicleOptionListType specialEquipList = new CarVehicleOptionListType();

        final List<CarVehicleOptionType> listOfSpecialEquipment = new ArrayList<CarVehicleOptionType>();
        for (final String specialEquipment : sepList) {
            final CarVehicleOptionType carVehicleOptionType = new CarVehicleOptionType();
            carVehicleOptionType.setCarSpecialEquipmentCode(specialEquipment);
            listOfSpecialEquipment.add(carVehicleOptionType);
        }
        specialEquipList.setCarVehicleOption(listOfSpecialEquipment);
        reserveRequest.getCarProduct().setCarVehicleOptionList(specialEquipList);
    }

    public static void setSpecialEquipment(SCSRequestGenerator requestGenerator,
                                           CarSupplyConnectivityGetReservationRequestType retrieveRequest)
    {
        retrieveRequest.getCarReservationList().getCarReservation().get(0).setCarSpecialEquipmentList(
                requestGenerator.getReserveReq().getCarSpecialEquipmentList());
    }

    public static void setSpecialEquipment(SCSRequestGenerator requestGenerator,
                                           CarSupplyConnectivityCancelRequestType cancelRequest)
    {
        cancelRequest.getCarReservation().setCarSpecialEquipmentList(
                requestGenerator.getReserveReq().getCarSpecialEquipmentList());
    }

    //  set filter in search request for car search : like Alamo 14/6,10,1,1 or 7,1,1,1 or Enterprise 15/4,10,1,1
    public static CarSupplyConnectivitySearchRequestType setCarSearchFilter(CarSupplyConnectivitySearchRequestType searchRequest,
                                                                            long carCategoryCode,
                                                                            long carTypeCode,
                                                                            long carTransmissionDriveCode,
                                                                            long carFuelACCode) throws IOException
    {
        for ( final CarSearchCriteriaType criteria : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {

            for ( final CarVehicleType vehicleType : criteria.getCarVehicleList().getCarVehicle()) {
                //  vehicleType.setCarCategoryCode(carCategoryCode);
                vehicleType.setCarTypeCode(carTypeCode);
                vehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
                vehicleType.setCarFuelACCode(carFuelACCode);
            }
        }

        return searchRequest;
    }

    public static CarSupplyConnectivitySearchRequestType setCarSearchFilter(CarSupplyConnectivitySearchRequestType searchRequest,
                                                                            long vendorSupplierID,
                                                                            long carCategoryCode,
                                                                            long carTypeCode,
                                                                            long carTransmissionDriveCode,
                                                                            long carFuelACCode) throws IOException
    {
        final List<CarSearchCriteriaType> carSearchCriteriaClone =
                new ArrayList<CarSearchCriteriaType>(searchRequest.getCarSearchCriteriaList().getCarSearchCriteria());

        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().clear();

        for ( final CarSearchCriteriaType criteria : carSearchCriteriaClone) {

            if ( criteria.getVendorSupplierIDList().getVendorSupplierID().get(0) == vendorSupplierID) {
                criteria.getCarVehicleList().getCarVehicle().clear();
                final List<CarVehicleType> vehicle = new ArrayList<CarVehicleType>();
                final CarVehicleType vehicleType = new CarVehicleType();
                vehicleType.setCarCategoryCode(carCategoryCode);
                vehicleType.setCarTypeCode(carTypeCode);
                vehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
                vehicleType.setCarFuelACCode(carFuelACCode);
                vehicle.add(vehicleType);
                criteria.getCarVehicleList().setCarVehicle(vehicle);

                CarSearchCriteriaType dcCriteria = new CarSearchCriteriaType();
                dcCriteria = criteria;
                searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().add(dcCriteria);
                break;
            }
        }

        return searchRequest;
    }

    //  car product filter for GetDetails
    public static CarSupplyConnectivityGetDetailsRequestType setCarGetDetailsFilter(CarSupplyConnectivityGetDetailsRequestType getDetailsRequest,
                                                                                    long vendorSupplierID,
                                                                                    long carCategoryCode,
                                                                                    long carTypeCode,
                                                                                    long carTransmissionDriveCode,
                                                                                    long carFuelACCode) throws IOException
    {
        final CarCatalogKeyType carCatalog = getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey();
        carCatalog.setVendorSupplierID(vendorSupplierID);

        final CarVehicleType vehicleType = carCatalog.getCarVehicle();
        vehicleType.setCarCategoryCode(carCategoryCode);
        vehicleType.setCarTypeCode(carTypeCode);
        vehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
        vehicleType.setCarFuelACCode(carFuelACCode);

        getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().setCarCatalogKey(carCatalog);
        return getDetailsRequest;
    }

    //  car product filter for GetCostAndAvail
    public static CarSupplyConnectivityGetCostAndAvailabilityRequestType setCarGetCostAndAvailFilter(CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest,
                                                                                                     long vendorSupplierID,
                                                                                                     long carCategoryCode,
                                                                                                     long carTypeCode,
                                                                                                     long carTransmissionDriveCode,
                                                                                                     long carFuelACCode) throws IOException
    {
        final CarCatalogKeyType carCatalog = costandavailRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey();
        carCatalog.setVendorSupplierID(vendorSupplierID);

        final CarVehicleType vehicleType = carCatalog.getCarVehicle();
        vehicleType.setCarCategoryCode(carCategoryCode);
        vehicleType.setCarTypeCode(carTypeCode);
        vehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
        vehicleType.setCarFuelACCode(carFuelACCode);

        costandavailRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().setCarCatalogKey(carCatalog);
        return costandavailRequest;
    }

    //  car product filter for Reserve
    public static CarSupplyConnectivityReserveRequestType setCarReserveFilter(CarSupplyConnectivityReserveRequestType reserveRequest,
                                                                              long vendorSupplierID,
                                                                              long carCategoryCode,
                                                                              long carTypeCode,
                                                                              long carTransmissionDriveCode,
                                                                              long carFuelACCode) throws IOException
    {
        final CarCatalogKeyType carCatalog = reserveRequest.getCarProduct().getCarInventoryKey().getCarCatalogKey();
        carCatalog.setVendorSupplierID(vendorSupplierID);

        final CarVehicleType vehicleType = carCatalog.getCarVehicle();
        vehicleType.setCarCategoryCode(carCategoryCode);
        vehicleType.setCarTypeCode(carTypeCode);
        vehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
        vehicleType.setCarFuelACCode(carFuelACCode);

        reserveRequest.getCarProduct().getCarInventoryKey().setCarCatalogKey(carCatalog);
        return reserveRequest;
    }

    //  car product filter for GetReservation
    public static CarSupplyConnectivityGetReservationRequestType setCarGetReservationFilter(CarSupplyConnectivityGetReservationRequestType retrieveRequest,
                                                                                            long vendorSupplierID,
                                                                                            long carCategoryCode,
                                                                                            long carTypeCode,
                                                                                            long carTransmissionDriveCode,
                                                                                            long carFuelACCode) throws IOException
    {
        final CarCatalogKeyType carCatalog = retrieveRequest.getCarReservationList().getCarReservation().get(0).getCarProduct().getCarInventoryKey().getCarCatalogKey();
        carCatalog.setVendorSupplierID(vendorSupplierID);

        final CarVehicleType vehicleType = carCatalog.getCarVehicle();
        vehicleType.setCarCategoryCode(carCategoryCode);
        vehicleType.setCarTypeCode(carTypeCode);
        vehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
        vehicleType.setCarFuelACCode(carFuelACCode);

        retrieveRequest.getCarReservationList().getCarReservation().get(0).getCarProduct().getCarInventoryKey().setCarCatalogKey(carCatalog);
        return retrieveRequest;
    }

}

