package com.expedia.s3.cars.framework.test.common.execution.requestgeneration;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.basetypes.defn.v4.UserKeyType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.messagetypes.defn.v4.MessageInfoType;
import com.expedia.e3.data.placetypes.defn.v4.LanguageType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.e3.data.timetypes.defn.v4.DateTimeRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.RequestDateTimeHandler;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestTimeData;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by asharma1 on 10/7/2016.
 */
@SuppressWarnings("PMD")
public class CommonRequestGenerator {
    public MessageInfoType createMessageInfoType(String messageName, String messageVersion) {
        final MessageInfoType messageInfoType = new MessageInfoType();
        messageInfoType.setCreateDateTime(DateTime.getInstanceByDateTime(new Date()));
        messageInfoType.setMessageGUID(UUID.randomUUID().toString());
        messageInfoType.setMessageNameString(messageName);
        messageInfoType.setMessageVersion(messageVersion);
        return messageInfoType;
    }

    public AuditLogTrackingDataType createAuditLogTrackingDataType(TestData testData) throws DataAccessException {
        final AuditLogTrackingDataType auditLog = new AuditLogTrackingDataType();
        final  Map auditTestData = (null == testData.getScenarios()) ? TestDataUtil.getAuditTestData(testData.getLocationSearchTestScenario().getCompanyCode()
                , testData.getLocationSearchTestScenario().getJurisdictionCountryCode(), testData.getLocationSearchTestScenario().getManagementUnitCode())
                 : TestDataUtil.getAuditTestData(testData.getScenarios().getCompanyCode()
                , testData.getScenarios().getJurisdictionCountryCode(), testData.getScenarios().getManagementUnitCode())
                ;

        auditLog.setAuditLogForceDownstreamTransaction(testData.isForceDownstream());
        auditLog.setAuditLogForceLogging(testData.isForceLogging());
        auditLog.setAuditLogGUID(UUID.randomUUID().toString());

        final UserKeyType userKeyType = new UserKeyType();
        userKeyType.setUserID(Long.valueOf(testData.getTuid()));
        auditLog.setTravelerUserKey(userKeyType);
        auditLog.setLogonUserKey(userKeyType);

        auditLog.setAuditLogLanguageId(Long.parseLong((String)auditTestData.get("LanguageId")));
        auditLog.setAuditLogEAPID(Long.parseLong((String)auditTestData.get("EAPID")));
        auditLog.setAuditLogGPID(0L);

        //set auditLogTPID
        final Long auditLogTPID = Long.parseLong((String)auditTestData.get("TPID"));
        auditLog.setAuditLogTPID(auditLogTPID.equals(0L) ? 3L : auditLogTPID);

        return auditLog;
    }

    public PointOfSaleKeyType createPointOfSaleKeyType(TestScenario testScenario) {
        final PointOfSaleKeyType pos = new PointOfSaleKeyType();
        pos.setJurisdictionCountryCode(testScenario.getJurisdictionCountryCode());
        pos.setCompanyCode(testScenario.getCompanyCode());
        pos.setManagementUnitCode(testScenario.getManagementUnitCode());
        return pos;
    }

    public PointOfSaleKeyType createPointOfSaleKeyType(String jurisdictionCountryCode, String companyCode, String managementUnitCode) {
        final PointOfSaleKeyType pos = new PointOfSaleKeyType();
        pos.setJurisdictionCountryCode(jurisdictionCountryCode);
        pos.setCompanyCode(companyCode);
        pos.setManagementUnitCode(managementUnitCode);
        return pos;
    }

    public LanguageType createLanguage(TestScenario testScenario) {
        final LanguageType language = new LanguageType();
        switch (testScenario.getJurisdictionCountryCode()) {
            case "USA":
                language.setLanguageCode("en");
                language.setCountryAlpha2Code("US");
                break;
            case "GBR":
                language.setLanguageCode("en");
                language.setCountryAlpha2Code("GB");
                break;
            case "FRA":
                language.setLanguageCode("fr");
                language.setCountryAlpha2Code("FR");
                break;
            case "DEU":
                language.setLanguageCode("de");
                language.setCountryAlpha2Code("DE");
                break;
            case "CAN":
                language.setLanguageCode("en");
                language.setCountryAlpha2Code("CA");
                break;
            default:
                language.setLanguageCode("en");
                language.setCountryAlpha2Code("US");
                break;
        }
        return language;
    }

    public CarLocationKeyType createCarLocationKeyType(String locationCode) {
        // TODO add support for off-airport

        final CarLocationKeyType carLocationKeyType = new CarLocationKeyType();
        carLocationKeyType.setLocationCode(locationCode);
        return carLocationKeyType;
    }

    public CarLocationSearchType createCarLocationSearchType(TestScenario scenario) {
        final CarLocationSearchType carLocationSearchType = new CarLocationSearchType();
        final AmountType latitudeAmount = new AmountType();
        final AmountType longitudeAmount = new AmountType();
        final DistanceType radius = new DistanceType();
        if (null != scenario.getLatitude())
        {
            final String lat = scenario.getLatitude() + "";
            latitudeAmount.setDecimal(Integer.parseInt(lat.split("\\.")[0] + lat.split("\\.")[1]));
            latitudeAmount.setDecimalPlaceCount(Long.valueOf(lat.split("\\.")[1].length() + ""));
            carLocationSearchType.setLatitudeAmount(latitudeAmount);
        }

        if (null != scenario.getLongitude())
        {
            final String lon = scenario.getLongitude() + "";
            longitudeAmount.setDecimal(Integer.parseInt(lon.split("\\.")[0] + lon.split("\\.")[1]));
            longitudeAmount.setDecimalPlaceCount(Long.valueOf(lon.split("\\.")[1].length() + ""));
            carLocationSearchType.setLongitudeAmount(longitudeAmount);
        }

        if (0 != scenario.getRadius())
        {
            radius.setDistanceUnitCount(scenario.getRadius());
            radius.setDistanceUnit("MI");
            carLocationSearchType.setRadiusDistance(radius);
        }

        if (0L != scenario.getStartLocationIndex())
        {
            carLocationSearchType.setStartLocationIndex(scenario.getStartLocationIndex());
        }

        if (0L != scenario.getLocationCount())
        {
            carLocationSearchType.setLocationCount(scenario.getLocationCount());
        }
        return carLocationSearchType;
    }

    public static SegmentDateTimeRangeType createSegmentDateTimeRangeType(TestData testData) {
        SegmentDateTimeRangeType segmentDateTimeRangeType = new SegmentDateTimeRangeType();
        if(null ==testData.getUseDays()) {
            // TODO currently added basic datetime range. We need to add support for different type of datetime range, that is used in regression cases.
            final DateTimeRangeType rangeType = new DateTimeRangeType();
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if(testData.isMerchantBoolean()){
                calendar.add(Calendar.DATE, RequestDefaultValues.SEARCH_START_INVERVAL_DAYS_MERCHANT);
            }else {
                calendar.add(Calendar.DATE, RequestDefaultValues.SEARCH_START_INTERVAL_DAYS);
            }
            calendar.set(Calendar.HOUR, 12);
            DateTime dateTime = DateTime.getInstanceByDateTime(calendar.getTime());
            rangeType.setMinDateTime(dateTime);
            rangeType.setMaxDateTime(dateTime);
            segmentDateTimeRangeType.setStartDateTimeRange(rangeType);


            final DateTimeRangeType rangeTypeEndDate = new DateTimeRangeType();
            calendar.add(Calendar.DATE, RequestDefaultValues.SEARCH_USE_DAYS);   // TODO days are not adding up
            dateTime = DateTime.getInstanceByDateTime(calendar.getTime());
            rangeTypeEndDate.setMinDateTime(dateTime);
            rangeTypeEndDate.setMaxDateTime(dateTime);
            segmentDateTimeRangeType.setEndDateTimeRange(rangeTypeEndDate);
        }
        else
        {
            final RequestDateTimeHandler dateTimeHandler = new RequestDateTimeHandler();
            segmentDateTimeRangeType = dateTimeHandler.dateTimeSpecialHandler(new TestTimeData(testData.getUseDays(), testData.isMerchantBoolean(), testData.isWeekendBoolean(), testData.isExtraHours()));
        }

        return segmentDateTimeRangeType;
    }

    public CarTransportationSegmentType createCarTransportationSegmentType(TestData testData) {
        final CarTransportationSegmentType carTransportation = new CarTransportationSegmentType();

        carTransportation.setStartCarLocationKey(createCarLocationKeyType(testData.getScenarios().getPickupLocationCode()));
        carTransportation.setEndCarLocationKey(createCarLocationKeyType(testData.getScenarios().getDropOffLocationCode()));
        carTransportation.setSegmentDateTimeRange(createSegmentDateTimeRangeType(testData));

        return carTransportation;
    }

    //this is use to create search Criteria multiple location
    public CarTransportationSegmentType createCarTransportationSegmentType(TestData testData, TestScenario scenario) {
        final CarTransportationSegmentType carTransportation = new CarTransportationSegmentType();

        carTransportation.setStartCarLocationKey(createCarLocationKeyType(scenario.getPickupLocationCode()));
        carTransportation.setEndCarLocationKey(createCarLocationKeyType(scenario.getDropOffLocationCode()));
        carTransportation.setSegmentDateTimeRange(createSegmentDateTimeRangeType(testData));

        return carTransportation;
    }

    public CarTransportationSegmentType createCarTransportationSegmentType(CarVendorLocation carStartVendor,
                                                                           CarVendorLocation carEndVendor, TestData testData) {
        final CarTransportationSegmentType carTransportationSegmentType = new CarTransportationSegmentType();

        final CarLocationKeyType startLocationKeyType = new CarLocationKeyType();
        final CarLocationKeyType endLocationKeyType = new CarLocationKeyType();

        startLocationKeyType.setCarVendorLocationID(carStartVendor.getCarVendorLocationID());
        startLocationKeyType.setLocationCode(carStartVendor.getLocationCode());
        startLocationKeyType.setCarLocationCategoryCode(carStartVendor.getCarLocationCategoryCode());
        startLocationKeyType.setSupplierRawText(carStartVendor.getSupplierRawText());
        if (null != testData.getTestScenarioSpecialHandleParam()) {
            if (null != testData.getTestScenarioSpecialHandleParam().getPickupOnAirport() &&
                    testData.getTestScenarioSpecialHandleParam().getPickupOnAirport().equals(Boolean.TRUE.toString())) {
                startLocationKeyType.setCarVendorLocationID(null);
                startLocationKeyType.setCarLocationCategoryCode(null);
                startLocationKeyType.setSupplierRawText(null);
            }
        }


        endLocationKeyType.setCarVendorLocationID(carEndVendor.getCarVendorLocationID());
        endLocationKeyType.setLocationCode(carEndVendor.getLocationCode());
        endLocationKeyType.setCarLocationCategoryCode(carEndVendor.getCarLocationCategoryCode());
        endLocationKeyType.setSupplierRawText(carEndVendor.getSupplierRawText());
        if (null != testData.getTestScenarioSpecialHandleParam()) {
            if (null != testData.getTestScenarioSpecialHandleParam().getDropOffOnAirport() &&
                    testData.getTestScenarioSpecialHandleParam().getDropOffOnAirport().equals(Boolean.TRUE.toString())) {
                endLocationKeyType.setCarVendorLocationID(null);
                endLocationKeyType.setCarLocationCategoryCode(null);
                endLocationKeyType.setSupplierRawText(null);
            }
        }

        carTransportationSegmentType.setStartCarLocationKey(startLocationKeyType);
        carTransportationSegmentType.setEndCarLocationKey(endLocationKeyType);
        carTransportationSegmentType.setSegmentDateTimeRange(createSegmentDateTimeRangeType(testData));
        return carTransportationSegmentType;
    }

    public CarVehicleListType createCarVehicleListType(int carVehicleCount, long carFuelACCode, long carTransmissionDriveCode, long carTypeCode) {
        final CarVehicleListType carVehicleListType = new CarVehicleListType();
        final List<CarVehicleType> carVehicle = new ArrayList<CarVehicleType>();
        if(carVehicleCount == 0)
        {
            final CarVehicleType carVehicleType = new CarVehicleType();
            carVehicleType.setCarCategoryCode(0L);
            carVehicleType.setCarFuelACCode(carFuelACCode);
            carVehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
            carVehicleType.setCarTypeCode(carTypeCode);
            carVehicle.add(carVehicleType);
            carVehicleListType.setCarVehicle(carVehicle);
            return carVehicleListType;
        }
        for (int i = 1; i < carVehicleCount; i++) {
            final CarVehicleType carVehicleType = new CarVehicleType();
            carVehicleType.setCarCategoryCode((long) i);
            carVehicleType.setCarFuelACCode(carFuelACCode);
            carVehicleType.setCarTransmissionDriveCode(carTransmissionDriveCode);
            carVehicleType.setCarTypeCode(carTypeCode);
            carVehicle.add(carVehicleType);
        }
        carVehicleListType.setCarVehicle(carVehicle);
        return carVehicleListType;
    }

    //------------------------------handle CDCodes------------------------------------------
    public List<CarRateOverrideType> createCDCodes(CarRate carRate) throws DataAccessException {
        List<CarRateOverrideType> carRateOverride = null;
        if (null != carRate) {
            if(!StringUtils.isEmpty(carRate.getCdCode())) {
                carRateOverride = new ArrayList<>();
                final String[] cdCodesArray = carRate.getCdCode().split(",");
                for (final String cdCode : cdCodesArray) {
                    long supplierID = 0;
                    if (cdCode.contains("-")) {
                        supplierID = Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(cdCode.split("-")[0]));
                    } else {
                        supplierID = Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(cdCode));
                    }

                    // Mutiple CD code with the same vendor
                    final String cdString = cdCode.split("-")[1];
                    final CarRateOverrideType cdnode = new CarRateOverrideType();
                    cdnode.setVendorSupplierID(supplierID);
                    if (null != cdString && !cdString.equals("null")) {
                        cdnode.setCorporateDiscountCode(cdString);
                    }
                    carRateOverride.add(cdnode);
                }
            }

        }
        return carRateOverride;
    }

    public CarRateType createCarRate(TestData testData) throws DataAccessException {
        CarRateType carRateType = null;
        if (!CompareUtil.isObjEmpty(testData.getCarRate().getCdCode())) {
            final String[] cdCodesArray = testData.getCarRate().getCdCode().split(",");
            if (!CompareUtil.isObjEmpty(cdCodesArray)) {
                carRateType = new CarRateType();
                if (cdCodesArray[0].contains("-")) {
                    carRateType.setCorporateDiscountCode(cdCodesArray[0].split("-")[1]);
                } else {
                    carRateType.setCorporateDiscountCode(cdCodesArray[0]);
                }

            }
        }
        if (!CompareUtil.isObjEmpty(testData.getCarRate().getPromoCode())) {
            carRateType = new CarRateType();
            carRateType.setPromoCode(testData.getCarRate().getPromoCode());
        }
        if (!CompareUtil.isObjEmpty(testData.getCarRate().getLoyaltyNum())) {
            carRateType = new CarRateType();
            final LoyaltyProgramType loyaltyProgramType = new LoyaltyProgramType();
            if (!testData.getCarRate().getLoyaltyNum().contains("Air")) {
                loyaltyProgramType.setLoyaltyProgramCategoryCode("Car");
                if (!CompareUtil.isObjEmpty(testData.getTestScenarioSpecialHandleParam().getVendorCode())) {
                    loyaltyProgramType.setLoyaltyProgramCode(testData.getTestScenarioSpecialHandleParam().getVendorCode());
                }
                if (testData.getCarRate().getLoyaltyNum().contains("-")) {
                    loyaltyProgramType.setLoyaltyProgramMembershipCode(testData.getCarRate().getLoyaltyNum().split("-")[1]);
                } else {
                    loyaltyProgramType.setLoyaltyProgramMembershipCode(testData.getCarRate().getLoyaltyNum());
                }
            }
            carRateType.setLoyaltyProgram(loyaltyProgramType);
        }
        return carRateType;
    }
    //------------------------------handle CDCodes------------------------------------------
    public List<CarRateOverrideType> createCDCodes(DataSource dataSource, CarRate carRate) throws DataAccessException {
        List<CarRateOverrideType> carRateOverride = null;
        if (null != carRate && !StringUtils.isEmpty(carRate.getCdCode())) {
            carRateOverride = new ArrayList<>();
            final String[] cdCodesArray = carRate.getCdCode().split(",");

            final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);

            for (final String cdCode : cdCodesArray) {
                long supplierID = 0;
                if (cdCode.contains("-")) {
                    supplierID = Long.parseLong(carsInventoryDataSource.getCarVendorList(cdCode.split("-")[0]).get(0).getSupplierID());
                } else {
                    supplierID = Long.parseLong(carsInventoryDataSource.getCarVendorList(cdCode).get(0).getSupplierID());
                }

                // Mutiple CD code with the same vendor
                final String cdString = cdCode.split("-")[1];
                final CarRateOverrideType cdnode = new CarRateOverrideType();
                cdnode.setVendorSupplierID(supplierID);
                if (null != cdString && !cdString.equals("null")) {
                    cdnode.setCorporateDiscountCode(cdString);
                }
                carRateOverride.add(cdnode);
            }
        }
        return carRateOverride;
    }


}
