package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiyu on 8/25/16.
 */
public final class PropertyResetHelper {
    //  special equipment strings used in cert test scenario
    private static final String STRING_SPECIAL_EQUIPMENT_GPS = "NavigationalSystem";
    private static final String STRING_SPECIAL_EQUIPMENT_TODDLER_SEAT = "ToddlerChildSeat";


    private PropertyResetHelper() {
    }

    //  set drive age
    public static CarSupplyConnectivitySearchRequestType setDriverAge(CarSupplyConnectivitySearchRequestType request, long driverAgeYearCount) {
        final CarSearchStrategyType carSearchStrategyType = request.getCarSearchStrategy();
        carSearchStrategyType.setDriverAgeYearCount(driverAgeYearCount);
        request.setCarSearchStrategy(carSearchStrategyType);

        return request;
    }

    //  set special equipment : GPS + toddler seat etc.
    public static void setSpecialEquipment(CarSupplyConnectivityReserveRequestType reserveRequest, List<String> specialEquipmentList) {
        List<String> sepList = specialEquipmentList;
        if (sepList == null || sepList.isEmpty()) {
            sepList = new ArrayList<>();
            sepList.add(STRING_SPECIAL_EQUIPMENT_GPS);
            sepList.add(STRING_SPECIAL_EQUIPMENT_TODDLER_SEAT);
        }

        reserveRequest.setCarSpecialEquipmentList(CommonDataTypesGenerator.createSpecialEquipmentList(sepList));
    }

    public static void setSpecialEquipment(SCSRequestGenerator requestGenerator,
                                           CarSupplyConnectivityGetReservationRequestType retrieveRequest) {
        retrieveRequest.getCarReservationList().getCarReservation().get(0).setCarSpecialEquipmentList(
                requestGenerator.getReserveReq().getCarSpecialEquipmentList());
    }

    public static void setSpecialEquipment(SCSRequestGenerator requestGenerator,
                                           CarSupplyConnectivityCancelRequestType cancelRequest) {
        cancelRequest.getCarReservation().setCarSpecialEquipmentList(
                requestGenerator.getReserveReq().getCarSpecialEquipmentList());
    }

    //  set filter in search request for car search : like Alamo 14/6,10,1,1 or 7,1,1,1 or Enterprise 15/4,10,1,1
    public static CarSupplyConnectivitySearchRequestType setCarSearchFilter(CarSupplyConnectivitySearchRequestType searchRequest,
                                                                            long vendorSupplierID,
                                                                            long carCategoryCode,
                                                                            long carTypeCode,
                                                                            long carTransmissionDriveCode,
                                                                            long carFuelACCode) throws IOException {
        final List<CarSearchCriteriaType> carSearchCriteriaClone =
                new ArrayList<CarSearchCriteriaType>(searchRequest.getCarSearchCriteriaList().getCarSearchCriteria());

        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().clear();

        for (final CarSearchCriteriaType criteria : carSearchCriteriaClone) {

            if (criteria.getVendorSupplierIDList().getVendorSupplierID().get(0) == vendorSupplierID) {
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
                                                                                    long carFuelACCode) throws IOException {
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
                                                                                                     long carFuelACCode) throws IOException {
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
                                                                              long carFuelACCode) throws IOException {
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
                                                                                            long carFuelACCode) throws IOException {
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

    //  filter request search list
    public static CarSupplyConnectivitySearchRequestType filterReqSearchList(CarSupplyConnectivitySearchRequestType request, DataSource dataSource) throws DataAccessException {
        final CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        final List<CarSearchCriteriaType> carSearchCriteriaTypeList = new ArrayList<CarSearchCriteriaType>();
        if(!CollectionUtils.isEmpty(request.getCarSearchCriteriaList().getCarSearchCriteria())) {
            for (CarSearchCriteriaType criteriaType : request.getCarSearchCriteriaList().getCarSearchCriteria()) {
                final long supplierId = criteriaType.getVendorSupplierIDList().getVendorSupplierID().get(0);
                final String domainType = "CarVendorLocation";
                final CarLocationKeyType startCarLocation = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
                final CarLocationKeyType endCarLocation = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
                final String startDomainValue = String.valueOf(startCarLocation.getCarVendorLocationID());
                final String endDomainValue = String.valueOf(endCarLocation.getCarVendorLocationID());
                final String startExternalDomainValue= startCarLocation.getLocationCode() + startCarLocation.getCarLocationCategoryCode() + startCarLocation.getSupplierRawText();
                final String endExternalDomainValue = endCarLocation.getLocationCode() + endCarLocation.getCarLocationCategoryCode() + endCarLocation.getSupplierRawText();
                final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(dataSource);
                final List<ExternalSupplyServiceDomainValueMap> startMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(supplierId,0,domainType,startDomainValue,startExternalDomainValue);
                final List<ExternalSupplyServiceDomainValueMap> endMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(supplierId,0,domainType,endDomainValue,endExternalDomainValue);
                if(!CollectionUtils.isEmpty(startMapList) && !CollectionUtils.isEmpty(endMapList)){
                    carSearchCriteriaTypeList.add(criteriaType);
                }

            }
        }
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypeList);
        request.setCarSearchCriteriaList(carSearchCriteriaListType);
        return request;
    }

}
