package com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryListType;
import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.basetypes.defn.v4.UserKeyType;
import com.expedia.e3.data.basetypes.defn.v4.VendorSupplierIDListType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.messagetypes.defn.v4.MessageInfoType;
import com.expedia.e3.data.placetypes.defn.v4.LanguageType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.e3.data.timetypes.defn.v4.DateTimeRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.SupplySubsets;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CCSRSearchRequestTestData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CarConnectivitySearchCriteria;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.*;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.expedia.s3.cars.framework.test.common.utils.CompareUtil.isObjEmpty;


/**
 * Created by asharma1 on 10/5/2016.
 */
@SuppressWarnings("all")
public class SCSSearchRequestGenerator {
    private static final String MESSAGE_VERSION = "4.1.0";
    private static final String MESSAGE_NAME = "CarSupplyConnectivitySearchRequest";
    private static final Pattern PATTERN_SEVEN = Pattern.compile( "^[A-Z]{4}\\d{3}$");
    private static final Pattern PATTERN_SIX = Pattern.compile( "^[A-Z]{4}\\d{2}$");


    private final CarsSCSDataSource scsDataSource;

    public SCSSearchRequestGenerator(DataSource dataSource)
    {
        this.scsDataSource = new CarsSCSDataSource(dataSource);
    }

    public CarSupplyConnectivitySearchRequestType createSearchRequest(TestData testData) throws DataAccessException {
        return createCommonRequest(testData, 0, 0);
    }

    public CarSupplyConnectivitySearchRequestType createSpecialSearchRequest(TestData testData,
                                                                             double radius) throws DataAccessException {
        return createCommonRequest(testData, radius, 0);
    }
    public CarSupplyConnectivitySearchRequestType createSpecialSearchRequest(TestData testData,
                                                                             int carVehicleCount) throws DataAccessException {
        return createCommonRequest(testData,0, carVehicleCount);
    }

    //
    //  test hook : convert key info from CarSupplyConnectivitySearchRequestType to CCSRSearchRequestTestData
    //
    public static CarSupplyConnectivitySearchRequestType postImportCCSRConverter(CCSRSearchRequestTestData searchRQ, TestData testData)
    {
        CarSupplyConnectivitySearchRequestType searchRequestType = new CarSupplyConnectivitySearchRequestType();

        final MessageInfoType messageInfoType = new MessageInfoType();
        messageInfoType.setCreateDateTime(DateTime.getInstanceByDateTime(new Date()));
        messageInfoType.setMessageGUID(UUID.randomUUID().toString());
        messageInfoType.setMessageNameString(MESSAGE_NAME);
        messageInfoType.setMessageVersion(MESSAGE_VERSION);
        searchRequestType.setMessageInfo(messageInfoType);

        //  setAuditLogTrackingData
        final AuditLogTrackingData auditLogTrackingData = searchRQ.getAuditLogTrackingData();
        final AuditLogTrackingDataType auditLog = new AuditLogTrackingDataType();
        //  auditLog.setAuditLogForceDownstreamTransaction(true);
        //  auditLog.setAuditLogForceLogging(true);
        auditLog.setAuditLogForceDownstreamTransaction(auditLogTrackingData.isAuditLogForceDownstreamTransaction());
        auditLog.setAuditLogForceLogging(auditLogTrackingData.isAuditLogForceLogging());
        auditLog.setAuditLogGUID(UUID.randomUUID().toString());

        final UserKeyType userKeyType = new UserKeyType();
        userKeyType.setUserID(Long.valueOf(testData.getTuid()));
        auditLog.setTravelerUserKey(userKeyType);
        auditLog.setLogonUserKey(userKeyType);

        // TODO set dynamic values as needed
        auditLog.setAuditLogLanguageId(Long.parseLong(auditLogTrackingData.getAuditLogLanguageId()));
        auditLog.setAuditLogEAPID(Long.parseLong(auditLogTrackingData.getAuditLogEAPID()));
        auditLog.setAuditLogGPID(Long.parseLong(auditLogTrackingData.getAuditLogGPID()));

        //  set TPID : no more DB access
        auditLog.setAuditLogTPID(Long.parseLong(auditLogTrackingData.getAuditLogTPID()));

        searchRequestType.setAuditLogTrackingData(auditLog);
        //  setPointOfSaleKey
        final PointOfSaleKey pointOfSaleKey = searchRQ.getPointOfSaleKey();
        final PointOfSaleKeyType pos = new PointOfSaleKeyType();
        pos.setJurisdictionCountryCode(pointOfSaleKey.getJurisdictionCountryCode());
        pos.setCompanyCode(pointOfSaleKey.getCompanyCode());
        pos.setManagementUnitCode(pointOfSaleKey.getManagementUnitCode());
        searchRequestType.setPointOfSaleKey(pos);

        //  setLanguage
        final Language language = searchRQ.getLanguage();
        final LanguageType languageType = new LanguageType();
        languageType.setLanguageCode((language.getLanguageCode() == null || language.getLanguageCode() == "null")? "en" : language.getLanguageCode());
        languageType.setCountryAlpha2Code((language.getCountryAlpha2Code() == null || language.getCountryAlpha2Code() == "null") ? "US" : language.getCountryAlpha2Code());
        searchRequestType.setLanguage(languageType);

        //  setCarSearchStrategy
        final CarSearchStrategy carSearchStrategy = searchRQ.getCarSearchStrategy();
        final CarSearchStrategyType carSearchStrategyType = new CarSearchStrategyType();
        carSearchStrategyType.setPricingVisibilityBoolean(carSearchStrategy.isPricingVisibilityBoolean());
        carSearchStrategyType.setPackageBoolean(carSearchStrategy.isPackageBoolean());
        searchRequestType.setCarSearchStrategy(carSearchStrategyType);

        //  setCarSearchCriteriaList
        final List<CarConnectivitySearchCriteria> carSearchCriteriaList = searchRQ.getCarSearchCriteriaList();
        CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        List<CarSearchCriteriaType> carSearchCriteriaTypeList = new ArrayList<CarSearchCriteriaType>();
        for (CarConnectivitySearchCriteria carConnectivitySearchCriteria : carSearchCriteriaList)
        {
            //  declare each criteria
            CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();
            //  sequence
            carSearchCriteriaType.setSequence(carConnectivitySearchCriteria.getSequence());

            //  SupplySubsetID
            final SupplySubsetIDEntryListType supplySubsetIDEntryListType = new SupplySubsetIDEntryListType();
            final List<SupplySubsetIDEntryType> supplySubsetIDEntryList = new ArrayList<SupplySubsetIDEntryType>();
            for ( String id : carConnectivitySearchCriteria.getSupplySubsetIDEntryList()) {
                final SupplySubsetIDEntryType supplySubsetIDEntry = new SupplySubsetIDEntryType();
                supplySubsetIDEntry.setSequence(carConnectivitySearchCriteria.getSequence());
                supplySubsetIDEntry.setSupplySubsetID(Long.parseLong(id));
                supplySubsetIDEntryList.add(supplySubsetIDEntry);
            }
            supplySubsetIDEntryListType.setSupplySubsetIDEntry(supplySubsetIDEntryList);
            carSearchCriteriaType.setSupplySubsetIDEntryList(supplySubsetIDEntryListType);

            //  CarTransportationSegment
            final CarTransportationSegmentType carTransportationSegmentType = new CarTransportationSegmentType();
            //  start location
            CarLocationKeyType startLocationType = new CarLocationKeyType();
            //  startLocationType.setCarLocationCategoryCode(carConnectivitySearchCriteria.getCarTransportSegment().getStartCarLocationCodeKey().getCarLocationCategoryCode());
            //  startLocationType.setCarVendorLocationID((long)carConnectivitySearchCriteria.getCarTransportSegment().getStartCarLocationCodeKey().getCarVendorLocationID());
            startLocationType.setLocationCode(carConnectivitySearchCriteria.getCarTransportSegment().getStartCarLocationCodeKey().getLocationCode());
            //  startLocationType.setSupplierRawText(carConnectivitySearchCriteria.getCarTransportSegment().getStartCarLocationCodeKey().getSupplierRawText());
            carTransportationSegmentType.setStartCarLocationKey(startLocationType);
            //  end location
            CarLocationKeyType endLocationType = new CarLocationKeyType();
            //  endLocationType.setCarLocationCategoryCode(carConnectivitySearchCriteria.getCarTransportSegment().getEndCarLocationCodeKey().getCarLocationCategoryCode());
            //  endLocationType.setCarVendorLocationID((long)carConnectivitySearchCriteria.getCarTransportSegment().getEndCarLocationCodeKey().getCarVendorLocationID());
            endLocationType.setLocationCode(carConnectivitySearchCriteria.getCarTransportSegment().getEndCarLocationCodeKey().getLocationCode());
            //  endLocationType.setSupplierRawText(carConnectivitySearchCriteria.getCarTransportSegment().getEndCarLocationCodeKey().getSupplierRawText());
            carTransportationSegmentType.setEndCarLocationKey(endLocationType);
            //  time range
            Calendar cal = Calendar.getInstance();
            final SegmentDateTimeRangeType segmentDataTimeRangeType = new SegmentDateTimeRangeType();

            cal.add(Calendar.DATE, 20);
            final DateTimeRangeType startTime = new DateTimeRangeType();
            startTime.setMinDateTime(DateTime.getInstanceByDateTime(cal.getTime()));
            startTime.setMaxDateTime(DateTime.getInstanceByDateTime(cal.getTime()));
            segmentDataTimeRangeType.setStartDateTimeRange(startTime);

            cal.add(Calendar.DATE, 5);
            final DateTimeRangeType endTime = new DateTimeRangeType();
            endTime.setMinDateTime(DateTime.getInstanceByDateTime(cal.getTime()));
            endTime.setMaxDateTime(DateTime.getInstanceByDateTime(cal.getTime()));
            segmentDataTimeRangeType.setEndDateTimeRange(endTime);

            carTransportationSegmentType.setSegmentDateTimeRange(segmentDataTimeRangeType);
            carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);

            //  CarVehicleList
            final CarVehicleListType carVehicleListType = new CarVehicleListType();
            final List<CarVehicleType> carVehicleTypeList = new ArrayList<CarVehicleType>();
            for ( CarVehicle carVehicle : carConnectivitySearchCriteria.getCarVehicleList()) {
                final CarVehicleType carVehicleType = new CarVehicleType();
                carVehicleType.setCarCategoryCode((long)carVehicle.getCarCategoryCode());
                carVehicleType.setCarFuelACCode((long)carVehicle.getCarFuelACCode());
                carVehicleType.setCarTransmissionDriveCode((long)carVehicle.getCarTransmissionDriveCode());
                carVehicleType.setCarTypeCode((long)carVehicle.getCarTypeCode());
                carVehicleTypeList.add(carVehicleType);
            }
            carVehicleListType.setCarVehicle(carVehicleTypeList);
            carSearchCriteriaType.setCarVehicleList(carVehicleListType);

            //  vendorSupplierIDList
            final VendorSupplierIDListType vendorSupplierIDListType = new VendorSupplierIDListType();
            final List<Long> vendorSupplierIDList = new ArrayList<Long>();
            for ( String id : carConnectivitySearchCriteria.getVendorSupplierIDList()) {
                vendorSupplierIDList.add(Long.parseLong(id));
            }
            vendorSupplierIDListType.setVendorSupplierID(vendorSupplierIDList);
            carSearchCriteriaType.setVendorSupplierIDList(vendorSupplierIDListType);

            //  MISC : "CarRate":null,
            CarRateType carRateType = null;
         /*
            if (carConnectivitySearchCriteria.getCarRate() != null)
            {
                carRateType = new CarRateType();

                CarRate carRate = carConnectivitySearchCriteria.getCarRate();

                carRateType.setCarRateQualifierCode(carRate.getCarRateQualifier());
                carRateType.setRateCode(carRate.getRateCode());
                carRateType.setCarAgreementID(carRate.getCarAgreementID());
                carRateType.setCarVendorAgreementCode(carRate.getCarVendorAgreement());
                carRateType.setCorporateDiscountCode(carRate.getCorporateDiscountCode());

                carRateType.setPromoCode(carRate.getPromoteCode());
                carRateType.setRateCategoryCode(carRate.getRateCategoryCode());
                carRateType.setRatePeriodCode(carRate.getRatePeriodCode());

                LoyaltyProgramType loyaltyProgramType = null;
                if (carRate.getLoyaltyProgram() != null) {
                    loyaltyProgramType = new LoyaltyProgramType();

                    loyaltyProgramType.setLoyaltyProgramCategoryCode(carRate.getLoyaltyProgram().getLoyaltyProgramCategoryCode());
                    loyaltyProgramType.setLoyaltyProgramCode(carRate.getLoyaltyProgram().getLoyaltyProgramCode());
                //  loyaltyProgramType.setLoyaltyProgramMembershipCode(carRate.getLoyaltyProgram().getLoyaltyProgramMembershipCode());
                    loyaltyProgramType.setSequence(carRate.getLoyaltyProgram().getSequence());
                }
                carRateType.setLoyaltyProgram(loyaltyProgramType);
            }
         */
            carSearchCriteriaType.setCarRate(carRateType);

            //  MISC
            //      "CurrencyCode":"EUR",
            //      "SmokingBoolean":false,
            //      "PrePaidFuelBoolean":false,
            //      "UnlimitedMileageBoolean":false,
            //      "PackageBoolean":true
            carSearchCriteriaType.setCurrencyCode(carConnectivitySearchCriteria.getCurrencyCode());
            carSearchCriteriaType.setSmokingBoolean(carConnectivitySearchCriteria.isSmokingBoolean());
            carSearchCriteriaType.setPrePaidFuelBoolean(carConnectivitySearchCriteria.isPrePaidFuelBoolean());
            carSearchCriteriaType.setUnlimitedMileageBoolean(carConnectivitySearchCriteria.isUnlimitedMileageBoolean());
            carSearchCriteriaType.setPackageBoolean(carConnectivitySearchCriteria.isPackageBoolean());

            //  set each criteria into list
            carSearchCriteriaTypeList.add(carSearchCriteriaType);
        }
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypeList);
        searchRequestType.setCarSearchCriteriaList(carSearchCriteriaListType);

        return searchRequestType;
    }


    public static CCSRSearchRequestTestData preExportCCSRConverter(CarSupplyConnectivitySearchRequestType searchRequestType)
    {
        CCSRSearchRequestTestData searchRQ = new CCSRSearchRequestTestData();

        //  setAuditLogTrackingData
        AuditLogTrackingData auditLogTrackingData = new AuditLogTrackingData();
        auditLogTrackingData.setLogonUserID(String.valueOf(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        auditLogTrackingData.setTravelerUserID(String.valueOf(searchRequestType.getAuditLogTrackingData().getTravelerUserKey().getUserID()));;
        auditLogTrackingData.setAuditLogGUID(searchRequestType.getAuditLogTrackingData().getAuditLogGUID());;
        auditLogTrackingData.setAuditLogTPID(String.valueOf(searchRequestType.getAuditLogTrackingData().getAuditLogTPID()));
        auditLogTrackingData.setAuditLogEAPID(String.valueOf(searchRequestType.getAuditLogTrackingData().getAuditLogEAPID()));
        auditLogTrackingData.setAuditLogGPID(String.valueOf(searchRequestType.getAuditLogTrackingData().getAuditLogGPID()));
        auditLogTrackingData.setAuditLogLanguageId(String.valueOf(searchRequestType.getAuditLogTrackingData().getAuditLogLanguageId()));
        auditLogTrackingData.setAuditLogForceLogging(searchRequestType.getAuditLogTrackingData().getAuditLogForceLogging());
        auditLogTrackingData.setAuditLogForceDownstreamTransaction(searchRequestType.getAuditLogTrackingData().getAuditLogForceDownstreamTransaction());
        searchRQ.setAuditLogTrackingData(auditLogTrackingData);

        //  setPointOfSaleKey
        PointOfSaleKey pointOfSaleKey = new PointOfSaleKey();
        pointOfSaleKey.setJurisdictionCountryCode(searchRequestType.getPointOfSaleKey().getJurisdictionCountryCode());
        pointOfSaleKey.setCompanyCode(searchRequestType.getPointOfSaleKey().getCompanyCode());
        pointOfSaleKey.setManagementUnitCode(searchRequestType.getPointOfSaleKey().getManagementUnitCode());
        searchRQ.setPointOfSaleKey(pointOfSaleKey);

        //  setLanguage
        Language language = new Language();
        if (searchRequestType.getLanguage() != null) {
            language.setLanguageCode(searchRequestType.getLanguage().getLanguageCode());
            language.setCountryAlpha2Code(searchRequestType.getLanguage().getCountryAlpha2Code());
        }
        searchRQ.setLanguage(language);

        //  setCarSearchStrategy
        CarSearchStrategy carSearchStrategy = new CarSearchStrategy();
        carSearchStrategy.setPricingVisibilityBoolean(searchRequestType.getCarSearchStrategy().getPricingVisibilityBoolean());
        carSearchStrategy.setPackageBoolean(searchRequestType.getCarSearchStrategy().getPackageBoolean());
        searchRQ.setCarSearchStrategy(carSearchStrategy);

        //  setCarSearchCriteriaList
        List<CarConnectivitySearchCriteria> carSearchCriteriaList = new ArrayList<CarConnectivitySearchCriteria>();
        for (CarSearchCriteriaType carSearchCriteriaType : searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria())
        {
            CarConnectivitySearchCriteria carSearchCriteria = new CarConnectivitySearchCriteria();

            //  supplySubsetID
            List<String> supplySubsetIDEntryList = new ArrayList<String>();
            for (SupplySubsetIDEntryType idEntry : carSearchCriteriaType.getSupplySubsetIDEntryList().getSupplySubsetIDEntry()) {
                supplySubsetIDEntryList.add(String.valueOf(idEntry.getSupplySubsetID()));
            }
            //  transport segmmene
            CarTransportationSegment carTransportSegment = new CarTransportationSegment();

            CarLocationKey startCarLocationCodeKey = new CarLocationKey();
            //  startCarLocationCodeKey.setCarVendorLocationID(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getCarVendorLocationID().intValue());
            startCarLocationCodeKey.setLocationCode(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getLocationCode());
            startCarLocationCodeKey.setCarLocationCategoryCode(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getCarLocationCategoryCode());
            startCarLocationCodeKey.setSupplierRawText(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getSupplierRawText());
            carTransportSegment.setStartCarLocationCodeKey(startCarLocationCodeKey);

            CarLocationKey endCarLocationCodeKey = new CarLocationKey();

            //  endCarLocationCodeKey.setCarVendorLocationID(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getCarVendorLocationID().intValue());
            endCarLocationCodeKey.setLocationCode(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getLocationCode());
            endCarLocationCodeKey.setCarLocationCategoryCode(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getCarLocationCategoryCode());
            endCarLocationCodeKey.setSupplierRawText(carSearchCriteriaType.getCarTransportationSegment().getStartCarLocationKey().getSupplierRawText());
            carTransportSegment.setEndCarLocationCodeKey(endCarLocationCodeKey);

            carTransportSegment.setStartDateTime(carSearchCriteriaType.getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().getMinDateTime().toString());
            carTransportSegment.setEndDateTime(carSearchCriteriaType.getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().getMinDateTime().toString());

            //  car vehicle list
            List<CarVehicle> carVehicleList = new ArrayList<CarVehicle>();
            for (CarVehicleType carVehicleType : carSearchCriteriaType.getCarVehicleList().getCarVehicle()) {
                CarVehicle carVehicle = new CarVehicle();
                carVehicle.setCarCategoryCode(carVehicleType.getCarCategoryCode().intValue());
                carVehicle.setCarFuelACCode(carVehicleType.getCarFuelACCode().intValue());
                carVehicle.setCarTransmissionDriveCode(carVehicleType.getCarTransmissionDriveCode().intValue());
                carVehicle.setCarFuelACCode(carVehicleType.getCarFuelACCode().intValue());

                carVehicleList.add(carVehicle);
            }
            //  vendor supplier id list
            List<String> vendorSupplierIDList = new ArrayList<String>();
            for (long longVal : carSearchCriteriaType.getVendorSupplierIDList().getVendorSupplierID()) {
                vendorSupplierIDList.add(String.valueOf(longVal));
            }

            //  set criteria
            carSearchCriteria.setSupplySubsetIDEntryList(supplySubsetIDEntryList);
            carSearchCriteria.setCarTransportSegment(carTransportSegment);
            carSearchCriteria.setCarVehicleList(carVehicleList);
            carSearchCriteria.setVendorSupplierIDList(vendorSupplierIDList);

            carSearchCriteria.setSequence((int)carSearchCriteriaType.getSequence());
            carSearchCriteria.setCurrencyCode(carSearchCriteriaType.getCurrencyCode());
            //  Attention : CarRate is class not a string
            CarRate carRate = null;
            if (carSearchCriteriaType.getCarRate() != null)
            {
                carRate = new CarRate();
                carRate.setCarRateQualifier(carSearchCriteriaType.getCarRate().getCarRateQualifierCode());
                carRate.setCarAgreementID(carSearchCriteriaType.getCarRate().getCarAgreementID());
                carRate.setCarVendorAgreement(carSearchCriteriaType.getCarRate().getCarVendorAgreementCode());
                carRate.setCorporateDiscountCode(carSearchCriteriaType.getCarRate().getCorporateDiscountCode());

                carRate.setPromoteCode(carSearchCriteriaType.getCarRate().getPromoCode()) ;
                carRate.setRateCategoryCode(carSearchCriteriaType.getCarRate().getRateCategoryCode());
                carRate.setRatePeriodCode(carSearchCriteriaType.getCarRate().getRatePeriodCode());

                LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
                if (carSearchCriteriaType.getCarRate().getLoyaltyProgram() != null)
                {
                    loyaltyProgram.setLoyaltyProgramCategoryCode(carSearchCriteriaType.getCarRate().getLoyaltyProgram().getLoyaltyProgramCategoryCode());
                    loyaltyProgram.setLoyaltyProgramCode(carSearchCriteriaType.getCarRate().getLoyaltyProgram().getLoyaltyProgramCode());
                    //  loyaltyProgram.getLoyaltyProgramMembershipCode(carSearchCriteriaType.getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode());
                    loyaltyProgram.setSequence(carSearchCriteriaType.getCarRate().getLoyaltyProgram().getSequence());
                }
                carRate.setLoyaltyProgram(loyaltyProgram);
            }
            carSearchCriteria.setCarRate(carRate);
            carSearchCriteria.setSmokingBoolean(carSearchCriteriaType.getSmokingBoolean());
            carSearchCriteria.setPrePaidFuelBoolean(carSearchCriteriaType.getPrePaidFuelBoolean());
            carSearchCriteria.setUnlimitedMileageBoolean(carSearchCriteriaType.getUnlimitedMileageBoolean());
            carSearchCriteria.setPackageBoolean(carSearchCriteriaType.getPackageBoolean());

            //  add into criteria list
            carSearchCriteriaList.add(carSearchCriteria);
        }
        searchRQ.setCarSearchCriteriaList(carSearchCriteriaList);

        return searchRQ;
    }

    private CarSupplyConnectivitySearchRequestType createCommonRequest(TestData parameters, double radius, int carVehicleCount) throws DataAccessException{
        final CarSupplyConnectivitySearchRequestType searchRequestType = new CarSupplyConnectivitySearchRequestType();
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        searchRequestType.setLanguage(commonRequestGenerator.createLanguage(parameters.getScenarios()));
        searchRequestType.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
        searchRequestType.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(parameters));
        searchRequestType.setPointOfSaleKey(commonRequestGenerator.createPointOfSaleKeyType(parameters.getScenarios()));
        searchRequestType.setCarSearchStrategy(CommonDataTypesGenerator.createCarSearchStrategy(parameters.getScenarios()));
        final CarSearchCriteriaListType carSearchCriteriaList = new CarSearchCriteriaListType();

        if(parameters.getScenarios().isOnAirPort()) {
            createCarSearchCriteriaList(parameters, parameters.getScenarios(), carVehicleCount, carSearchCriteriaList);
            if(null != parameters.getTestScenarioSpecialHandleParam() && null != parameters.getTestScenarioSpecialHandleParam().getMultiplePickAndDropLocationScenario())
            {
                createCarSearchCriteriaList(parameters, parameters.getTestScenarioSpecialHandleParam().getMultiplePickAndDropLocationScenario(),
                        carVehicleCount, carSearchCriteriaList);
            }
        }else{
            if(6 == parameters.getScenarios().getServiceProviderID()|| parameters.getSpecialTestCasesParam().isSpecialOffAirPort()){//amadeus
                createCarSearchCriteriaListOffAirPortSpecial(parameters, parameters.getScenarios(), carVehicleCount, carSearchCriteriaList);
                if(null != parameters.getTestScenarioSpecialHandleParam() && null != parameters.getTestScenarioSpecialHandleParam().getMultiplePickAndDropLocationScenario())
                {
                    createCarSearchCriteriaListOffAirPortSpecial(parameters,
                            parameters.getTestScenarioSpecialHandleParam().getMultiplePickAndDropLocationScenario(),
                            carVehicleCount, carSearchCriteriaList);
                }
            }else {
                searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaListOffAirPort(parameters, radius,carVehicleCount));
                return searchRequestType;
            }
        }
        searchRequestType.setCarSearchCriteriaList(carSearchCriteriaList);

        return searchRequestType;
    }

    public static SupplySubsetIDEntryListType createSupplySubsetIDEntryListType(long supplySubsetID) {
        final SupplySubsetIDEntryType supplySubsetIDEntryType = new SupplySubsetIDEntryType();
        supplySubsetIDEntryType.setSupplySubsetID(supplySubsetID);

        final List<SupplySubsetIDEntryType> supplySubsetIDEntryTypes = new ArrayList<SupplySubsetIDEntryType>();
        supplySubsetIDEntryTypes.add(supplySubsetIDEntryType);

        final SupplySubsetIDEntryListType supplySubsetIDEntryListType = new SupplySubsetIDEntryListType();
        supplySubsetIDEntryListType.setSupplySubsetIDEntry(supplySubsetIDEntryTypes);

        return supplySubsetIDEntryListType;
    }

    public static VendorSupplierIDListType createVendorSupplierIDListType(List<Long> vendorSupplierIDs) {
        final VendorSupplierIDListType vendorSupplierIDListType = new VendorSupplierIDListType();
        vendorSupplierIDListType.setVendorSupplierID(vendorSupplierIDs);
        return vendorSupplierIDListType;
    }

    /*
     * on Airport SearchCriteriaList
     */
    private void createCarSearchCriteriaList(TestData testData, TestScenario scenario, int carVehicleCount, CarSearchCriteriaListType carSearchCriteriaList) throws DataAccessException
    {
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarTransportationSegmentType carTransportationSegmentType = commonRequestGenerator.createCarTransportationSegmentType(testData, scenario);
        final CarVehicleListType carVehicleListType = commonRequestGenerator.createCarVehicleListType(carVehicleCount, 0L, 0L, 0L);
        final CarRateType carRateType = commonRequestGenerator.createCarRate(testData);
        final List<CarRateOverrideType> cdCodes = commonRequestGenerator.createCDCodes(testData.getCarRate());

       /*final List<SupplySubset> supplySubsets = carsInventoryHelper.getSupplierSubsets(testData.getScenarios(),false);
        final List<SupplySubset> supplySubsetsDetails = carsInventoryHelper.getSupplierSubsets(testData.getScenarios(),true);
*/
        List<SupplySubsets> supplySubsets = null;
        if (testData.getScenarios().isStandalone())
        {
            if (StringUtil.isNotBlank(testData.getSpecificSupplySubset()))
            {
                supplySubsets = TestDataUtil.getSupplySubIDs(testData.getSpecificSupplySubset());
            } else
            {
                supplySubsets = TestDataUtil.getSupplySubIDs(getProviderString(testData) + "StandaloneOnAirport");
            }
        } else
        {
            if (StringUtil.isNotBlank(testData.getSpecificSupplySubset()))
            {
                supplySubsets = TestDataUtil.getSupplySubIDs(testData.getSpecificSupplySubset());
            } else
            {
                supplySubsets = TestDataUtil.getSupplySubIDs(getProviderString(testData) + "PackageOnAirport");
            }
        }

        String currencyCode = testData.getScenarios().getSupplierCurrencyCode();

        List<CarSearchCriteriaType> carSearchCriteriaTypes = null;

        if(null == carSearchCriteriaList.getCarSearchCriteria())
        {
            carSearchCriteriaTypes = new ArrayList<CarSearchCriteriaType>();
        }
        else
        {
            carSearchCriteriaTypes = carSearchCriteriaList.getCarSearchCriteria();
        }
        long sequence = 101L;

        if (!CollectionUtils.isEmpty(supplySubsets))
        {
            for (final SupplySubsets supplySubset : supplySubsets)
            {

                final CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();
                carSearchCriteriaType.setSequence(sequence);
                carSearchCriteriaType.setSupplySubsetIDEntryList(createSupplySubsetIDEntryListType(supplySubset.getSupplySubsetID()));
                carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);
                carSearchCriteriaType.setCarVehicleList(carVehicleListType);
                if (!isObjEmpty(carRateType))
                {
                    carSearchCriteriaType.setCarRate(carRateType);
                }
                if (null != cdCodes)
                {
                    if (null == carSearchCriteriaType.getCarRateOverrideList())
                    {
                        carSearchCriteriaType.setCarRateOverrideList(new CarRateOverrideListType());
                    }
                    carSearchCriteriaType.getCarRateOverrideList().setCarRateOverride(cdCodes);
                }
                // set vendorSupplierIDList
               /* final List<Long> vendorSupplierIDs = new ArrayList<Long>();
                for(final SupplySubset detail : supplySubsetsDetails){
                    if(supplySubset.getSupplySubsetID() == detail.getSupplySubsetID()){
                        vendorSupplierIDs.add((long)detail.getSupplierID());
                    }
                }*/
                carSearchCriteriaType.setVendorSupplierIDList(createVendorSupplierIDListType(supplySubset.getSupplierID()));
                //set currencyCode
                carSearchCriteriaType.setCurrencyCode(currencyCode);
                carSearchCriteriaType.setSmokingBoolean(false);
                carSearchCriteriaType.setPrePaidFuelBoolean(false);
                carSearchCriteriaType.setUnlimitedMileageBoolean(false);
                carSearchCriteriaType.setPackageBoolean(!testData.getScenarios().isStandalone());
                carSearchCriteriaTypes.add(carSearchCriteriaType);
                sequence++;
            }
        }

        carSearchCriteriaList.setCarSearchCriteria(carSearchCriteriaTypes);

        //priority : TestScenarioSpecialHandleParam.VendorSupplierIDList > TestScenarioSpecialHandleParam.VendorSupplierID > vendorCode
        specifiedVendorIDs4CarSearchCriteriaByTestData(carSearchCriteriaTypes, testData);
    }

    private String getProviderString(TestData testData) {
        String providerString = "";
        if (1 == testData.getScenarios().getServiceProviderID())
        {
            providerString = "WorldSpan";
        }
        else if (6 == testData.getScenarios().getServiceProviderID())
        {
            providerString = "Amadeus";
        }
        else if (3 == testData.getScenarios().getServiceProviderID())
        {
            providerString = "Micronexus";
        }
        else if (7 == testData.getScenarios().getServiceProviderID())
        {
            providerString = "Titanium";
        }
        else if (8 == testData.getScenarios().getServiceProviderID())
        {
            providerString = "Sabre";
        }
        return providerString;
    }

   /* private List<CarVendorLocation> getFilterCarLocationList(String airPortCode, List<SupplySubset> supplySubsets,double radius)throws DataAccessException{
        final List<CarVendorLocation> filterCarLocationKeyList = new ArrayList<CarVendorLocation>();
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(dataSource);
        final Airport airport = carsInventoryHelper.getAirport(airPortCode);
        final List<CarVendorLocation> carVendorLocations = carsInventoryHelper.getCarLocationList(airPortCode,supplySubsets);
        radius = radius > 0? radius : 10;
        if(!CollectionUtils.isEmpty(carVendorLocations)){
            for(final CarVendorLocation carVendorLocation : carVendorLocations){
                final double calcRadius = calculateDistanceLatLon(airport.getLatitude(),airport.getLongitude(),carVendorLocation.getLatitude(),carVendorLocation.getLongitude());
                if(calcRadius < radius){
                    carVendorLocation.setDistance(calcRadius);
                    filterCarLocationKeyList.add(carVendorLocation);
                }
            }
        }
        return filterCarLocationKeyList;
    }

    private double  calculateDistanceLatLon(double airportLat, double airportLon, double locationLat, double locationLon){
        final double radLat1 = airportLat * Math.PI / 180.0;
        final double radLat2 = locationLat * Math.PI / 180.0;
        final double radLon1 = airportLon * Math.PI / 180.0;
        final double radLon2 = locationLon * Math.PI / 180.0;
        final double a = radLat1 - radLat2;
        final double b = radLon1 - radLon2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        s = (Math.round(s * 10000) / 1000) / 1.6; //Convert the unit to MI: 1MI is about 1.6KM
        return s;

    }*/

    /**
     * generate for LocationInfo for OffAirport ,per supplySubsetID default set less than or equal to 5 location list
     * @param testScenario
     * @return
     * @throws DataAccessException
     */
    private List<List<ExternalSupplyServiceDomainValueMap>>  filterVendorLocationListWithOnwayBool(TestScenario testScenario, int coutSupplySubsetID)throws DataAccessException {
        final List<List<ExternalSupplyServiceDomainValueMap>> resultList = new ArrayList<List<ExternalSupplyServiceDomainValueMap>>();
        //  final List<CarVendorLocation> carStartLocationList = getFilterCarLocationList(testScenario.getPickupLocationCode(),supplySubsets,radius);
        // final List<CarVendorLocation> carEndLocationList = getFilterCarLocationList(testScenario.getDropOffLocationCode(),supplySubsets,radius);
        List<ExternalSupplyServiceDomainValueMap> carStartLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation",testScenario.getPickupLocationCode()+"%");
        List<ExternalSupplyServiceDomainValueMap> carEndLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation",testScenario.getDropOffLocationCode()+"%");


        List<ExternalSupplyServiceDomainValueMap> carStratLocationListAll = new ArrayList<>();
        List<ExternalSupplyServiceDomainValueMap> carEndLocationListAll = new ArrayList<>();
        //trip way
        if(testScenario.getPickupLocationCode().equals(testScenario.getDropOffLocationCode())){
            if (coutSupplySubsetID == 0 && carStartLocationList.size() <= 5)
            {
                carStratLocationListAll = carStartLocationList;
                carEndLocationListAll = carEndLocationList;
            }
            else if(carStartLocationList.size() >= 5*coutSupplySubsetID)
            {
                carStratLocationListAll = carStartLocationList.subList(0 + 5 * coutSupplySubsetID , 5 + 5 * coutSupplySubsetID);
                carEndLocationListAll = carEndLocationList.subList(0 + 5 * coutSupplySubsetID , 5 + 5 * coutSupplySubsetID);
            }

        }else{//one way
            for(final ExternalSupplyServiceDomainValueMap carStartVendor : carStartLocationList.subList(0 + 5 * coutSupplySubsetID , 5 + 5 * coutSupplySubsetID)) {
                if (carStratLocationListAll.size() >=5)
                {
                    break;
                }
                for (final ExternalSupplyServiceDomainValueMap carEndVendor : carEndLocationList) {
                    if (carStartVendor.getSupplierID() == carEndVendor.getSupplierID()) {
                        carStratLocationListAll.add(carStartVendor);
                        carEndLocationListAll.add(carEndVendor);

                    }
                    if (carStratLocationListAll.size() >=5)
                    {
                        break;
                    }
                }
            }
        }
        resultList.add(carStratLocationListAll);
        resultList.add(carEndLocationListAll);
        return resultList;
    }


    private CarSearchCriteriaListType createCarSearchCriteriaListOffAirPort(TestData testData, double radius, int carVehicleCount) throws DataAccessException
    {
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarVehicleListType carVehicleListType = commonRequestGenerator.createCarVehicleListType(carVehicleCount, 0L, 0L, 0L);
        final CarRateType carRateType = commonRequestGenerator.createCarRate(testData);
        final List<CarRateOverrideType> cdCodes = commonRequestGenerator.createCDCodes(testData.getCarRate());

     /*   final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(dataSource);
        final List<SupplySubset> supplySubsets = carsInventoryHelper.getSupplierSubsets(testData.getScenarios(),true);
     */

        List<SupplySubsets> supplySubsets = null;

        supplySubsets = getSupplySubsetss(testData);
        String currencyCode = testData.getScenarios().getSupplierCurrencyCode();

        long sequence = 1;
        final CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        final List<CarSearchCriteriaType> carSearchCriteriaTypes = new ArrayList<CarSearchCriteriaType>();
        for (int j = 0; supplySubsets.size() > j; j++)
        {
            final List<List<ExternalSupplyServiceDomainValueMap>> filterList = filterVendorLocationListWithOnwayBool(testData.getScenarios(), j);
            final List<ExternalSupplyServiceDomainValueMap> carStratLocationListAll = filterList.get(0);
            final List<ExternalSupplyServiceDomainValueMap> carEndLocationListAll = filterList.get(1);
            for (int i = 0; i < carStratLocationListAll.size(); i++)
            {
                final ExternalSupplyServiceDomainValueMap carStartVendor = carStratLocationListAll.get(i);
                final ExternalSupplyServiceDomainValueMap carEndVendor = carEndLocationListAll.get(i);

                final CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();

                carSearchCriteriaType.setSupplySubsetIDEntryList(createSupplySubsetIDEntryListType(supplySubsets.get(j).getSupplySubsetID()));
                // set vendorSupplierIDList
                final List<Long> vendorSupplierIDs = new ArrayList<Long>();
                vendorSupplierIDs.add(carStartVendor.getSupplierID());
                carSearchCriteriaType.setVendorSupplierIDList(createVendorSupplierIDListType(vendorSupplierIDs));

                //set carTransportationSegment
                final CarTransportationSegmentType carTransportationSegmentType =
                        createCarTransportationSegmentType(carStartVendor, carEndVendor, testData);
                carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);
                if (!isObjEmpty(carRateType))
                {
                    carSearchCriteriaType.setCarRate(carRateType);
                }
                if (null != cdCodes)
                {
                    if (null == carSearchCriteriaType.getCarRateOverrideList())
                    {
                        carSearchCriteriaType.setCarRateOverrideList(new CarRateOverrideListType());
                    }
                    carSearchCriteriaType.getCarRateOverrideList().setCarRateOverride(cdCodes);
                }

                //set other items
                carSearchCriteriaType.setSequence(sequence * 100 + 1);
                carSearchCriteriaType.setCarVehicleList(carVehicleListType);
                carSearchCriteriaType.setCurrencyCode(currencyCode);
                carSearchCriteriaType.setSmokingBoolean(false);
                carSearchCriteriaType.setPrePaidFuelBoolean(false);
                carSearchCriteriaType.setUnlimitedMileageBoolean(false);
                carSearchCriteriaType.setPackageBoolean(!testData.getScenarios().isStandalone());
                carSearchCriteriaTypes.add(carSearchCriteriaType);
                sequence++;
            }
        }
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypes);

        //priority : TestScenarioSpecialHandleParam.VendorSupplierIDList > TestScenarioSpecialHandleParam.VendorSupplierID > vendorCode
        specifiedVendorIDs4CarSearchCriteriaByTestData(carSearchCriteriaTypes, testData);

        return carSearchCriteriaListType;
    }

    /**
     * get the supplySubset from default json file, This design for Less DB
     * @param testData
     * @return
     */
    private List<SupplySubsets> getSupplySubsetss(TestData testData) {
        List<SupplySubsets> supplySubsets;
        if(testData.getScenarios().isStandalone())
        {
            if(StringUtil.isNotBlank(testData.getSpecificSupplySubset()))
            {
                supplySubsets = TestDataUtil.getSupplySubIDs(testData.getSpecificSupplySubset());
            }else {
                supplySubsets = TestDataUtil.getSupplySubIDs(getProviderString(testData) + "StandaloneOffAirport");
            }
        }
        else
        {
            if(StringUtil.isNotBlank(testData.getSpecificSupplySubset()))
            {
                supplySubsets = TestDataUtil.getSupplySubIDs(testData.getSpecificSupplySubset());
            }else {
                supplySubsets = TestDataUtil.getSupplySubIDs(getProviderString(testData) + "PackageOffAirport");
            }
        }
        return supplySubsets;
    }

    /**
     * get locationInfo for OffAirport special GDS
     * @param supplySubsets
     * @param testScenario
     * @param param
     * @return
     * @throws DataAccessException
     */
    private List<List<ExternalSupplyServiceDomainValueMap>> getFilterCarLocationListSpecial(TestScenario testScenario,
                                                                                            TestData param) throws DataAccessException
    {
        final List<List<ExternalSupplyServiceDomainValueMap>> resultList = new ArrayList<List<ExternalSupplyServiceDomainValueMap>>();
        // the search criteria list per vendor no more than 5
        int maxVendorCount = 5;
        if (null != param.getTestScenarioSpecialHandleParam() && param.getTestScenarioSpecialHandleParam().getSearchCriteriaCount() > 0)
        {
            maxVendorCount = param.getTestScenarioSpecialHandleParam().getSearchCriteriaCount();
        }

        //List<CarVendorLocation> startLocationList = carsInventoryHelper.getCarLocationList(testScenario.getPickupLocationCode(),supplySubsets);
        List<ExternalSupplyServiceDomainValueMap> startLocationList = getExternalSupplyServiceDomainValueMapsForPick(testScenario.getPickupLocationCode(), param);
        List<ExternalSupplyServiceDomainValueMap> endLocationList = getExternalSupplyServiceDomainValueMapsForDrop(testScenario.getDropOffLocationCode(), param);

        List<ExternalSupplyServiceDomainValueMap> startLocationFilterList = new ArrayList<>();
        List<ExternalSupplyServiceDomainValueMap> endLocationFilterList = new ArrayList<>();
        List<String> commonLocationList = new ArrayList<>();

        //Mia : i think below logic is for roundtrip, add below condition is to made the code more efficientive, if the logic is wrong please remove.
        // && testScenario.getPickupLocationCode().equals(testScenario.getDropOffLocationCode())
        if (CollectionUtils.isNotEmpty(startLocationList) && testScenario.getPickupLocationCode().equals(testScenario.getDropOffLocationCode()))
        {
            List<String> domainValues = new ArrayList<>();
            for (final ExternalSupplyServiceDomainValueMap startVendorLocation : startLocationList)
            {
                for (final ExternalSupplyServiceDomainValueMap endVendorLocation : endLocationList)
                {
                    if (startVendorLocation.getDomainValue().equals(endVendorLocation.getDomainValue()))
                    {
                        String domainValue = startVendorLocation.getDomainValue();
                        //filter  repeated location
                        if (!domainValues.contains(domainValue))
                        {
                            startLocationFilterList.add(startVendorLocation);
                            commonLocationList.add(startVendorLocation.getDomainValue());
                            endLocationFilterList.add(endVendorLocation);
                            domainValues.add(domainValue);
                        }
                        break;
                    }
                }
                if (startLocationFilterList.size() >= maxVendorCount && endLocationFilterList.size() >= maxVendorCount)
                {
                    break;
                }
            }
        }

        //for on airport
        if (null != param.getTestScenarioSpecialHandleParam() && null != param.getTestScenarioSpecialHandleParam().getPickupOnAirport()
                && param.getTestScenarioSpecialHandleParam().getPickupOnAirport().equals(Boolean.TRUE.toString()))
        {
            for (final ExternalSupplyServiceDomainValueMap endVendorLocation : endLocationList)
            {
                if (!commonLocationList.contains(endVendorLocation.getDomainValue()))
                {
                    startLocationFilterList.add(startLocationList.get(0));
                    endLocationFilterList.add(endVendorLocation);
                    if (endLocationFilterList.size() >= maxVendorCount)
                    {
                        break;
                    }
                }
            }
        }
        //for one way
        else if (startLocationFilterList.size() < maxVendorCount)
        {
            for (final ExternalSupplyServiceDomainValueMap startVendorLocation : startLocationList)
            {
                if (!commonLocationList.contains(startVendorLocation.getDomainValue()))
                {
                    for (final ExternalSupplyServiceDomainValueMap endVendorLocation : endLocationList)
                    {
                        if (!commonLocationList.contains(endVendorLocation.getDomainValue()) &&
                                startVendorLocation.getSupplierID() == endVendorLocation.getSupplierID())
                        {
                            startLocationFilterList.add(startVendorLocation);
                            endLocationFilterList.add(endVendorLocation);
                            commonLocationList.add(startVendorLocation.getExternalDomainValue());
                            commonLocationList.add(endVendorLocation.getExternalDomainValue());
                            break;
                        }
                    }
                }
                if (startLocationFilterList.size() >= maxVendorCount)
                {
                    break;
                }
            }
        }
        resultList.add(startLocationFilterList);
        resultList.add(endLocationFilterList);
        return resultList;
    }

    private List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMapsForDrop(String  dropOffLocationCode, TestData param) throws DataAccessException {
        List<ExternalSupplyServiceDomainValueMap> endLocationList = null;
        if(null != param.getTestScenarioSpecialHandleParam() && null != param.getTestScenarioSpecialHandleParam().getDropOffCarVendorLocationCode())
        {
            String endExtValue = dropOffLocationCode + param.getTestScenarioSpecialHandleParam().getDropOffCarVendorLocationCode();
            endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation", endExtValue);
            if(CollectionUtils.isEmpty(endLocationList))
            {
                //need to check if  type like 'SEAT01' exist.
                endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation", endExtValue.substring(0,4)+ endExtValue.substring(5,endExtValue.length()));
            }
        }

        if(null != param.getTestScenarioSpecialHandleParam()&& param.getTestScenarioSpecialHandleParam().getVendorSupplierID() > 0)
        {
            endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierID(),
                    0, "CarVendorLocation","", dropOffLocationCode+"%");

        }

        if(null != param.getTestScenarioSpecialHandleParam()&& null != param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs()
                && !param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs().isEmpty())
        {
            endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs(),
                    0, "CarVendorLocation","", dropOffLocationCode+"%");

        }

        if (null != param.getTestScenarioSpecialHandleParam()&& param.getTestScenarioSpecialHandleParam().getVendorSupplierID() > 0
                && null != param.getTestScenarioSpecialHandleParam().getDropOffCarVendorLocationCode())
        {
            String endExtValue = dropOffLocationCode + param.getTestScenarioSpecialHandleParam().getDropOffCarVendorLocationCode();
            endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierID(),
                    0, "CarVendorLocation","", endExtValue);
            if(CollectionUtils.isEmpty(endLocationList))
            {
                //need to check if  type like 'MADT01' exist.
                endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierID(),
                        0, "CarVendorLocation","", endExtValue.substring(0,4)+ endExtValue.substring(5,endExtValue.length()));
            }
        }

        if (null != param.getTestScenarioSpecialHandleParam()&& null != param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs()
                && !param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs().isEmpty()
                && null != param.getTestScenarioSpecialHandleParam().getDropOffCarVendorLocationCode())
        {
            String endExtValue = dropOffLocationCode + param.getTestScenarioSpecialHandleParam().getDropOffCarVendorLocationCode();
            endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs(),
                    0, "CarVendorLocation","", endExtValue);
            if(CollectionUtils.isEmpty(endLocationList))
            {
                //need to check if  type like 'MADT01' exist.
                endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs(),
                        0, "CarVendorLocation","", endExtValue.substring(0,4)+ endExtValue.substring(5,endExtValue.length()));
            }
        }

        if(null == endLocationList)
        {
            endLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation", dropOffLocationCode+"%");

        }

        if (null != param.getTestScenarioSpecialHandleParam() && null != param.getTestScenarioSpecialHandleParam().getDropOffOnAirport()
                && param.getTestScenarioSpecialHandleParam().getDropOffOnAirport().equals(Boolean.TRUE.toString()))
        {
            List<ExternalSupplyServiceDomainValueMap> endLocationListTemp = new ArrayList<>();
            List<Long> vendorlist = new ArrayList<>();
            for (ExternalSupplyServiceDomainValueMap endLoc : endLocationList)
            {
                if(!vendorlist.contains(endLoc.getSupplierID()))
                {
                    vendorlist.add(endLoc.getSupplierID());

                    endLoc.setExternalDomainValue(param.getScenarios().getDropOffLocationCode());
                    //to filter duplicate loc logic to use
                    endLoc.setDomainValue(endLoc.getSupplierID() + param.getScenarios().getDropOffLocationCode());
                    endLocationListTemp.add(endLoc);
                }

                endLocationList = new ArrayList<>();
                StringBuffer onLocs = new StringBuffer();
                for(ExternalSupplyServiceDomainValueMap onAirportLoc : endLocationListTemp)
                {
                    if(!onLocs.toString().contains(onAirportLoc.getDomainValue()))
                    {
                        endLocationList.add(onAirportLoc);
                        onLocs.append("##" + onAirportLoc.getDomainValue());
                    }
                }
            }
        }
        return endLocationList;
    }

    private List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMapsForPick(String pickUpLocation, TestData param) throws DataAccessException
    {
        List<ExternalSupplyServiceDomainValueMap> startLocationList = null;
        if (null != param.getTestScenarioSpecialHandleParam() && null != param.getTestScenarioSpecialHandleParam().getPickUpCarVendorLocationCode())
        {
            String extValue = pickUpLocation + param.getTestScenarioSpecialHandleParam().getPickUpCarVendorLocationCode();
            startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation", extValue);
            if (CollectionUtils.isEmpty(startLocationList))
            {
                //need to check if  type like 'SEAT01' exist.
                startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation", extValue.substring(0, 4) + extValue.substring(5, extValue.length()));
            }
        }
        if (null != param.getTestScenarioSpecialHandleParam() && param.getTestScenarioSpecialHandleParam().getVendorSupplierID() > 0)
        {
            startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierID(),
                    0, "CarVendorLocation", "", pickUpLocation + "%");

        }

        if(null != param.getTestScenarioSpecialHandleParam()&& null != param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs()
                && !param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs().isEmpty())
        {
            startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs(),
                    0, "CarVendorLocation","", pickUpLocation+"%");

        }

        if (null != param.getTestScenarioSpecialHandleParam() && param.getTestScenarioSpecialHandleParam().getVendorSupplierID() > 0
                && null != param.getTestScenarioSpecialHandleParam().getPickUpCarVendorLocationCode())
        {
            String extValue = pickUpLocation + param.getTestScenarioSpecialHandleParam().getPickUpCarVendorLocationCode();
            startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierID(),
                    0, "CarVendorLocation", "", extValue);
            if (CollectionUtils.isEmpty(startLocationList))
            {
                //need to check if  type like 'MADT01' exist.
                startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierID(),
                        0, "CarVendorLocation", "", extValue.substring(0, 4) + extValue.substring(5, extValue
                                .length()));
            }
        }

        if (null != param.getTestScenarioSpecialHandleParam() && null != param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs()
                && !param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs().isEmpty()
                && null != param.getTestScenarioSpecialHandleParam().getPickUpCarVendorLocationCode())
        {
            String extValue = pickUpLocation + param.getTestScenarioSpecialHandleParam().getPickUpCarVendorLocationCode();
            startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs(),
                    0, "CarVendorLocation", "", extValue);
            if (CollectionUtils.isEmpty(startLocationList))
            {
                //need to check if  type like 'MADT01' exist.
                startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap(param.getTestScenarioSpecialHandleParam().getVendorSupplierIDs(),
                        0, "CarVendorLocation", "", extValue.substring(0, 4) + extValue.substring(5, extValue
                                .length()));
            }
        }

        if (null == startLocationList)
        {
            startLocationList = scsDataSource.getExternalSupplyServiceDomainValueMap("CarVendorLocation", pickUpLocation + "%");
        }

        if (null != param.getTestScenarioSpecialHandleParam() && null != param.getTestScenarioSpecialHandleParam().getPickupOnAirport()
                && param.getTestScenarioSpecialHandleParam().getPickupOnAirport().equals(Boolean.TRUE.toString()))
        {
            List<ExternalSupplyServiceDomainValueMap> startLocationListTemp = new ArrayList<>();
            List<Long> vendorlist = new ArrayList<>();
            for (ExternalSupplyServiceDomainValueMap startloc : startLocationList)
            {
                if(!vendorlist.contains(startloc.getSupplierID()))
                {
                    vendorlist.add(startloc.getSupplierID());

                    startloc.setExternalDomainValue(param.getScenarios().getPickupLocationCode());
                    //to filter duplicate loc logic to use
                    startloc.setDomainValue(startloc.getSupplierID() + param.getScenarios().getPickupLocationCode());
                    startLocationListTemp.add(startloc);
                }
            }

            startLocationList = new ArrayList<>();
            StringBuffer onLocs = new StringBuffer();
            for(ExternalSupplyServiceDomainValueMap onAirportLoc : startLocationListTemp)
            {
                if(!onLocs.toString().contains(onAirportLoc.getDomainValue()))
                {
                    startLocationList.add(onAirportLoc);
                    onLocs.append("##" + onAirportLoc.getDomainValue());
                }
            }
        }
        return startLocationList;
    }

    /**
     * Off Airport SearchCriteriaList Special GDS
     * @param testData
     * @return
     * @throws DataAccessException
     */
    private void createCarSearchCriteriaListOffAirPortSpecial(TestData testData, TestScenario scenario, int carVehicleCount,
                                                              CarSearchCriteriaListType carSearchCriteriaList) throws DataAccessException {
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarVehicleListType carVehicleListType = commonRequestGenerator.createCarVehicleListType(carVehicleCount,0L,0L,0L);
        final CarRateType carRateType = commonRequestGenerator.createCarRate(testData);
        final List<CarRateOverrideType> cdCodes = commonRequestGenerator.createCDCodes(testData.getCarRate());

       /* final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(dataSource);
          List<SupplySubset> supplySubsets = carsInventoryHelper.getSupplierSubsets(testData.getScenarios(),true);
*/
        //get the supplySubSetID and SupplierID from json file
        List<SupplySubsets> supplySubsets = null;
        supplySubsets = getSupplySubsetss(testData);

        String currencyCode = testData.getScenarios().getSupplierCurrencyCode();

        List<CarSearchCriteriaType> carSearchCriteriaTypes = null;
        if(null == carSearchCriteriaList.getCarSearchCriteria())
        {
            carSearchCriteriaTypes = new ArrayList<CarSearchCriteriaType>();
        }
        else {
            carSearchCriteriaTypes = carSearchCriteriaList.getCarSearchCriteria();
        }

        long sequence = 101;
        if(CollectionUtils.isNotEmpty(supplySubsets)) {
            for (final SupplySubsets subset : supplySubsets) {
                final List<List<ExternalSupplyServiceDomainValueMap>> resultList = getFilterCarLocationListSpecial(scenario, testData);

                final List<ExternalSupplyServiceDomainValueMap> startVendorLocationList = resultList.get(0);
                final List<ExternalSupplyServiceDomainValueMap> endVendorLocationList = resultList.get(1);
                if (CollectionUtils.isNotEmpty(startVendorLocationList)) {
                    for (int i = 0; i < startVendorLocationList.size(); i++) {
                        final CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();
                        carSearchCriteriaType.setSupplySubsetIDEntryList(createSupplySubsetIDEntryListType(subset.getSupplySubsetID()));
                        // default set vendorSupplierIDList
                        final List<Long> vendorSupplierIDs = new ArrayList<Long>();

                        //vendor list special handle is in the end of this method.
                        if (null != testData.getTestScenarioSpecialHandleParam() && null != testData.getTestScenarioSpecialHandleParam().getPickupOnAirport()
                                && testData.getTestScenarioSpecialHandleParam().getPickupOnAirport().equals(Boolean.TRUE.toString()))
                        {
                            vendorSupplierIDs.add(endVendorLocationList.get(i).getSupplierID());
                        }
                        else
                        {
                            vendorSupplierIDs.add(startVendorLocationList.get(i).getSupplierID());
                        }
                        carSearchCriteriaType.setVendorSupplierIDList(createVendorSupplierIDListType(vendorSupplierIDs));

                        //set carTransportationSegment
                        final CarTransportationSegmentType carTransportationSegmentType = createCarTransportationSegmentType(
                                startVendorLocationList.get(i), endVendorLocationList.get(i), testData);
                        carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);
                        if(!isObjEmpty(carRateType)){
                            carSearchCriteriaType.setCarRate(carRateType);
                        }
                        if (null != cdCodes) {
                            if (null == carSearchCriteriaType.getCarRateOverrideList()) {
                                carSearchCriteriaType.setCarRateOverrideList(new CarRateOverrideListType());
                            }
                            carSearchCriteriaType.getCarRateOverrideList().setCarRateOverride(cdCodes);
                        }

                        //set other items
                        carSearchCriteriaType.setSequence(sequence);
                        carSearchCriteriaType.setCarVehicleList(carVehicleListType);
                        carSearchCriteriaType.setCurrencyCode(currencyCode);
                        carSearchCriteriaType.setSmokingBoolean(false);
                        carSearchCriteriaType.setPrePaidFuelBoolean(false);
                        carSearchCriteriaType.setUnlimitedMileageBoolean(false);
                        carSearchCriteriaType.setPackageBoolean(!testData.getScenarios().isStandalone());
                        carSearchCriteriaTypes.add(carSearchCriteriaType);
                        sequence++;
                    }
                }
            }
        }
        carSearchCriteriaList.setCarSearchCriteria(carSearchCriteriaTypes);

        //priority : TestScenarioSpecialHandleParam.VendorSupplierIDList > TestScenarioSpecialHandleParam.VendorSupplierID > vendorCode
        specifiedVendorIDs4CarSearchCriteriaByTestData(carSearchCriteriaTypes, testData);
    }

    public CarTransportationSegmentType createCarTransportationSegmentType(ExternalSupplyServiceDomainValueMap carStartVendor,
                                                                           ExternalSupplyServiceDomainValueMap carEndVendor, TestData testData)

    {
        final CarTransportationSegmentType carTransportationSegmentType = new CarTransportationSegmentType();

        final CarLocationKeyType startLocationKeyType = new CarLocationKeyType();
        final CarLocationKeyType endLocationKeyType = new CarLocationKeyType();

        //SpecialHandle Pickup OnAirport
        startLocationKeyType.setLocationCode(testData.getScenarios().getPickupLocationCode());
        if (null != testData.getTestScenarioSpecialHandleParam() &&
                null != testData.getTestScenarioSpecialHandleParam().getPickupOnAirport() &&
                testData.getTestScenarioSpecialHandleParam().getPickupOnAirport().equals(Boolean.TRUE.toString()))
        {
            startLocationKeyType.setCarVendorLocationID(null);
            startLocationKeyType.setCarLocationCategoryCode(null);
            startLocationKeyType.setSupplierRawText(null);
        } else
        {
            startLocationKeyType.setCarVendorLocationID(Long.parseLong(carStartVendor.getDomainValue()));

            //when externalDomainValue's value style like 'SEAT001' or 'SDUBO01', set the value for locationCode/CarLocationCategoryCode/SupplierRawText
            Matcher startM = PATTERN_SEVEN.matcher(carStartVendor.getExternalDomainValue());
            Matcher startL = PATTERN_SIX.matcher(carStartVendor.getExternalDomainValue());
            if (startM.matches())
            {
                startLocationKeyType.setCarLocationCategoryCode(carStartVendor.getExternalDomainValue().substring(3, 4));
                startLocationKeyType.setSupplierRawText(carStartVendor.getExternalDomainValue().substring(4, carStartVendor.getExternalDomainValue().length()));
            }
            if (startL.matches())
            {
                startLocationKeyType.setCarLocationCategoryCode(carStartVendor.getExternalDomainValue().substring(3, 4));
                startLocationKeyType.setSupplierRawText( "0" + carStartVendor.getExternalDomainValue().substring(4, carStartVendor.getExternalDomainValue().length()));
            }
        }

        //SpecialHandle DropOff OnAirport
        endLocationKeyType.setLocationCode(testData.getScenarios().getDropOffLocationCode());
        if (null != testData.getTestScenarioSpecialHandleParam() &&
                null != testData.getTestScenarioSpecialHandleParam().getDropOffOnAirport() &&
                testData.getTestScenarioSpecialHandleParam().getDropOffOnAirport().equals(Boolean.TRUE.toString()))
        {
            endLocationKeyType.setCarVendorLocationID(null);
            endLocationKeyType.setCarLocationCategoryCode(null);
            endLocationKeyType.setSupplierRawText(null);
        } else
        {
            endLocationKeyType.setCarVendorLocationID(Long.parseLong(carEndVendor.getDomainValue()));

            Matcher endM = PATTERN_SEVEN.matcher(carEndVendor.getExternalDomainValue());
            Matcher endL = PATTERN_SIX.matcher(carEndVendor.getExternalDomainValue());
            if (endM.matches())
            {
                endLocationKeyType.setCarLocationCategoryCode(carEndVendor.getExternalDomainValue().substring(3, 4));
                endLocationKeyType.setSupplierRawText(carEndVendor.getExternalDomainValue().substring(4, carEndVendor.getExternalDomainValue().length()));
            }
            if (endL.matches())
            {
                endLocationKeyType.setCarLocationCategoryCode(carEndVendor.getExternalDomainValue().substring(3, 4));
                endLocationKeyType.setSupplierRawText("0" + carEndVendor.getExternalDomainValue().substring(4, carEndVendor.getExternalDomainValue().length()));
            }
        }

        carTransportationSegmentType.setStartCarLocationKey(startLocationKeyType);
        carTransportationSegmentType.setEndCarLocationKey(endLocationKeyType);
        carTransportationSegmentType.setSegmentDateTimeRange(CommonRequestGenerator.createSegmentDateTimeRangeType(testData));
        return carTransportationSegmentType;
    }

    //priority : TestScenarioSpecialHandleParam.VendorSupplierIDList > TestScenarioSpecialHandleParam.VendorSupplierID > vendorCode
    private void specifiedVendorIDs4CarSearchCriteriaByTestData(List<CarSearchCriteriaType> scsCarSearchCriteria, TestData testData) throws DataAccessException
    {
        //only create searchCriteria for set vendorCode
        if (null != testData.getTestScenarioSpecialHandleParam())
        {
            if (!isObjEmpty(testData.getTestScenarioSpecialHandleParam().getVendorSupplierIDs()))
            {
                // set vendorSupplierIDList
                final List<Long> vendorSupplierIDs = new ArrayList<Long>();
                vendorSupplierIDs.addAll(testData.getTestScenarioSpecialHandleParam().getVendorSupplierIDs());
                for (CarSearchCriteriaType carSearchCriteriaType : scsCarSearchCriteria)
                {
                    carSearchCriteriaType.setVendorSupplierIDList(createVendorSupplierIDListType(vendorSupplierIDs));
                }
            }
            //this is for TestScenarioSpecialHandleParam VendorSupplierID
            else if (testData.getTestScenarioSpecialHandleParam().getVendorSupplierID() > 0)
            {
                // set vendorSupplierIDList
                final List<Long> vendorSupplierIDs = new ArrayList<Long>();
                vendorSupplierIDs.add(testData.getTestScenarioSpecialHandleParam().getVendorSupplierID());
                for (CarSearchCriteriaType carSearchCriteriaType : scsCarSearchCriteria)
                {
                    carSearchCriteriaType.setVendorSupplierIDList(createVendorSupplierIDListType(vendorSupplierIDs));
                }
            } else if (!isObjEmpty(testData.getTestScenarioSpecialHandleParam().getVendorCode()))
            {
                final List<ExternalSupplyServiceDomainValueMap> valueMaps =
                        scsDataSource.getExternalSupplyServiceDomainValueMap(CommonConstantManager.DomainType.CAR_VENDOR,
                                testData.getTestScenarioSpecialHandleParam().getVendorCode());
                final String supplierId = CompareUtil.isObjEmpty(valueMaps) ? "" : valueMaps.get(0).getDomainValue();
                if (!StringUtils.isEmpty(supplierId))
                {
                    // set vendorSupplierIDList
                    final List<Long> vendorSupplierIDs = new ArrayList<Long>();
                    vendorSupplierIDs.add(Long.parseLong(supplierId));
                    for (CarSearchCriteriaType carSearchCriteriaType : scsCarSearchCriteria)
                    {
                        carSearchCriteriaType.setVendorSupplierIDList(createVendorSupplierIDListType(vendorSupplierIDs));
                    }
                }
            }
        }
    }
}