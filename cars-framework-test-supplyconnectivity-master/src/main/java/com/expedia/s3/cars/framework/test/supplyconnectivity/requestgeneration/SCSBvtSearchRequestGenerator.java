package com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration;

import com.expedia.e3.data.basetypes.defn.v4.UserKeyType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mpaudel on 5/18/16.
 */
@SuppressWarnings("PMD")
public class SCSBvtSearchRequestGenerator {
    private static final String MESSAGE_VERSION = "4.1.0";
    private static final String MESSAGE_NAME = "CarSupplyConnectivitySearchRequest";
    private static final long AUDIT_LOG_LANGUAGE_ID = 1036L;

    private final String splitStr = "\\$";

    public CarSupplyConnectivitySearchRequestType createSearchRequest(TestScenario scenarios, String tuid, String supplySubsetID, String offAirportLocationList,
                                                                      String vendorList) {
        final CarSupplyConnectivitySearchRequestType searchRequestType = new CarSupplyConnectivitySearchRequestType();
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();

        searchRequestType.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
        searchRequestType.setAuditLogTrackingData(createAuditLogTrackingDataType(tuid));
        searchRequestType.setPointOfSaleKey(commonRequestGenerator.createPointOfSaleKeyType(scenarios));
        searchRequestType.setCarSearchStrategy(CommonDataTypesGenerator.createCarSearchStrategy(scenarios));
        searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaList(scenarios, supplySubsetID, offAirportLocationList, vendorList));
//        if(scenarios.isOnAirPort()) {
//            searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaList(parameters));
//        }else{
//            if(6 == parameters.getScenarios().getServiceProviderID()){//amadeus
//                searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaListOffAirPortSpecial(parameters));
//            }else {
//                searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaListOffAirPort(parameters, radius));
//            }
//        }

        return searchRequestType;
    }

    private AuditLogTrackingDataType createAuditLogTrackingDataType(String tuid) {
        final AuditLogTrackingDataType auditLog = new AuditLogTrackingDataType();

        auditLog.setAuditLogForceDownstreamTransaction(true);
        auditLog.setAuditLogForceLogging(true);
        auditLog.setAuditLogGUID(UUID.randomUUID().toString());

        final UserKeyType userKeyType = new UserKeyType();
        userKeyType.setUserID(Long.valueOf(tuid));
        auditLog.setTravelerUserKey(userKeyType);
        auditLog.setLogonUserKey(userKeyType);

        auditLog.setAuditLogLanguageId(AUDIT_LOG_LANGUAGE_ID);
        auditLog.setAuditLogEAPID(0L);
        auditLog.setAuditLogGPID(0L);
        auditLog.setAuditLogTPID(60003L);

        return auditLog;
    }

    private CarSearchCriteriaListType createCarSearchCriteriaList(TestScenario scenarios, String supplySubsetID, String offAirportLocationList, String vendorList) {
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarVehicleListType carVehicleListType = commonRequestGenerator.createCarVehicleListType(0, 0L, 0L, 0L);

        String currencyCode = "USD";

        final List<CarSearchCriteriaType> carSearchCriteriaTypes = new ArrayList<CarSearchCriteriaType>();

        long sequence = 101L;
        if (scenarios.isOnAirPort()) {
            carSearchCriteriaTypes.add(buildCarSearchCriteriaType(scenarios, currencyCode, sequence, supplySubsetID,
                    null, vendorList, carVehicleListType));
        } else {
            List<String> locationList = new ArrayList<>();
            String[] offAirportLocation = offAirportLocationList.split(splitStr);
            for (int i = 0; i < offAirportLocation.length; i++) {
                locationList.add(offAirportLocation[i]);
            }

            for (String locationCategoryCodeAndSupplierRawText : locationList) {
                carSearchCriteriaTypes.add(buildCarSearchCriteriaType(scenarios, currencyCode, sequence, supplySubsetID,
                        locationCategoryCodeAndSupplierRawText, vendorList, carVehicleListType));
                sequence++;
            }
        }

        final CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypes);
        return carSearchCriteriaListType;
    }

    private CarSearchCriteriaType buildCarSearchCriteriaType(TestScenario scenarios, String currencyCode, long sequence,
                                                             String supplySubsetID, String locationCategoryCodeAndSupplierRawText,
                                                             String vendorList, CarVehicleListType carVehicleListType) {
        //CarTransportationSegmentType
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarTransportationSegmentType carTransportationSegmentType = commonRequestGenerator.createCarTransportationSegmentType
                (new TestData(null, scenarios, null, null));
        if (!scenarios.isOnAirPort()) {
            carTransportationSegmentType.getStartCarLocationKey().setCarLocationCategoryCode(locationCategoryCodeAndSupplierRawText.substring(0, 1));
            carTransportationSegmentType.getStartCarLocationKey().setSupplierRawText(locationCategoryCodeAndSupplierRawText.substring(1, 4));

            carTransportationSegmentType.getEndCarLocationKey().setCarLocationCategoryCode(locationCategoryCodeAndSupplierRawText.substring(0, 1));
            carTransportationSegmentType.getEndCarLocationKey().setSupplierRawText(locationCategoryCodeAndSupplierRawText.substring(1, 4));
            if (locationCategoryCodeAndSupplierRawText.length() > 4) {
                String[] locArray = locationCategoryCodeAndSupplierRawText.split(",");
                carTransportationSegmentType.getStartCarLocationKey().setCarVendorLocationID(Long.parseLong(locArray[1]));
                carTransportationSegmentType.getEndCarLocationKey().setCarVendorLocationID(Long.parseLong(locArray[2]));
            }
        }

        final CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();
        carSearchCriteriaType.setSequence(sequence);
        carSearchCriteriaType.setSupplySubsetIDEntryList(SCSSearchRequestGenerator.createSupplySubsetIDEntryListType(Long.parseLong(supplySubsetID)));
        carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);
        carSearchCriteriaType.setCarVehicleList(carVehicleListType);
        if (null == carSearchCriteriaType.getCarRateOverrideList()) {
            carSearchCriteriaType.setCarRateOverrideList(new CarRateOverrideListType());
        }

        //CDCodes
        List<CarRateOverrideType> cdCodes = new ArrayList<CarRateOverrideType>();
        CarRateOverrideType cdCode = new CarRateOverrideType();
        cdCode.setVendorSupplierID(35L);
        cdCode.setCorporateDiscountCode("5020105");
        cdCodes.add(cdCode);

        cdCode = new CarRateOverrideType();
        cdCode.setVendorSupplierID(40L);
        cdCode.setCorporateDiscountCode("3456789");
        cdCodes.add(cdCode);

        cdCode = new CarRateOverrideType();
        cdCode.setVendorSupplierID(41L);
        cdCode.setCorporateDiscountCode("N865556");
        cdCodes.add(cdCode);

        carSearchCriteriaType.getCarRateOverrideList().setCarRateOverride(cdCodes);

        //VendorSupplierIDList
        List<Long> vendorSupplierIDs = new ArrayList<Long>();
        String[] vendorListArray = vendorList.split(splitStr);
        for (int i = 0; i < vendorListArray.length; i++) {
            vendorSupplierIDs.add(Long.parseLong(vendorListArray[i]));
        }

        carSearchCriteriaType.setVendorSupplierIDList(SCSSearchRequestGenerator.createVendorSupplierIDListType(vendorSupplierIDs));

        //set currencyCode
        carSearchCriteriaType.setCurrencyCode(currencyCode);
        carSearchCriteriaType.setSmokingBoolean(false);
        carSearchCriteriaType.setPrePaidFuelBoolean(false);
        carSearchCriteriaType.setUnlimitedMileageBoolean(false);
        carSearchCriteriaType.setPackageBoolean(!scenarios.isStandalone());

        carSearchCriteriaType.setCarRate(new CarRateType());
        return carSearchCriteriaType;
    }
}