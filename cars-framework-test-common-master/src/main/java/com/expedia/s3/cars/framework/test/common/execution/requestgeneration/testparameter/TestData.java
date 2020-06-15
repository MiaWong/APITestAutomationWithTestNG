package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager.TimeDuration;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.eclipse.jetty.client.HttpClient;

/**
 * Created by miawang on 12/7/2016.
 */

@SuppressWarnings("PMD")
public class TestData
{
    //these may should add in this class, get them from some method
    /* string startLocationCode = null,
    string endLocationCode = null, string currencyCode = null,
    CarCommonEnumManager.CollectionSet startCollectionSet = CarCommonEnumManager.CollectionSet.NonExist,
    CarCommonEnumManager.DeliverySet startDeliverySet = CarCommonEnumManager.DeliverySet.NonExist,
    CarCommonEnumManager.CollectionSet endCollectionSet = CarCommonEnumManager.CollectionSet.NonExist,
    CarCommonEnumManager.DeliverySet endDeliverySet = CarCommonEnumManager.DeliverySet.NonExist,
    string startIntervalDays = null, string useDays = null, string extraHours = null,
    CarClassificationIDList carClassificationIDList = null, bool weekendBoolean = false, bool pickUpAtMidnigt = false*/
    private HttpClient httpClient;
    private TestScenario scenarios;
    private LocationSearchTestScenario locationSearchTestScenario;
    private SpooferTransport spooferTransport;
    private String tuid;
    private String guid;

    private String specificSupplySubset;

    private Long specificCarCategoryCode;

    private String clientCode;

    private boolean isRegression;

    private ResultFilter resultFilter;

    /*
    *  "N" NO need select prepaid car
    *  "Y"  need select prepaid car
     */
    private String needPrepaidCar;

    private Language language;

    private Long resultSetCount;
    private String optimizationStrategyCode;


    //AuditLogTrackingData
    private AuditLogTrackingData auditLogTrackingData;

    //parameter to Gernerate daily: weekly; monthly; week + day; monthly + day
    private TimeDuration useDays;
    private boolean merchantBoolean;
    private boolean weekendBoolean;
    private boolean extraHours;

    /* Example to set this param:
     *
     * CarRate cdCode = new CarRate();
     * cdCode.setCdCode("ZI-N865556");
     * parameters.setCarRate(cdCode);
     */
    private CarRate carRate;

    //SIPP not implement yet, so comment it, if there anyone implement it please uncomment it.
    //private SIPP sipp;

    /* below param is already declare in SpecialTestCasesParam, please do not declare again.
     *
     * private long driverAge;
     *
     * validate CASSS-4544 CarVehicleOption in COPP is getting back all the special equipment instead of just the selected one
     * private boolean optionListSenerio;
     *
     * build specail off airport request
     * private boolean specialOffAirPort;
     */
    private SpecialTestCasesParam specialTestCasesParam;

    //if with multiple pickup and drop-off location : testScenarioSpecialHandleParam.multiplePickAndDropLocationScenario

    /* vendor in request priority :
     * TestScenarioSpecialHandleParam.VendorSupplierIDList > TestScenarioSpecialHandleParam.VendorSupplierID > TestScenarioSpecialHandleParam.vendorCode
     *
     * if set vendorCode or vendorSupplierID, then the search Criteria is only with this vendor
     * if set vendorSupplierIDs, then the search Criteria is only with these vendor
     *
     * Example to set this param:
     *
     * TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
     * specialHandleParam.setVendorSupplierID(14);
     * specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AC);
     * specialHandleParam.setDropOffCarVendorLocationCode("T001");
     * specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());
     * parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
     */

    private TestScenarioSpecialHandleParam testScenarioSpecialHandleParam;

    private TestDataErrHandle errHandle;

    private String specialTest;

    private String carSpecialEquipmentCode;

    private String specialEquipmentEnumType;

    //ShareShift <OptimizationStrategyCode xmlns="urn:expedia:e3:data:messagetypes:defn:v4">3</OptimizationStrategyCode>
    //<ResultSetCount>1</ResultSetCount>

    private boolean needVO;

    private boolean needCC;

    private String billingNumber;

    private boolean withFliInfo;
    //use this flag if Prepare purchase request requires loyalty code
    private boolean needLoyaltyCard;
    //use this flag if Prepare purchase has multiple travelers
    private boolean needMultiTraveler;
    //use this flag if Prepare purchase traveler requires loyalty code
    private boolean needTravelerLoyalty;
    //use this flag if we you want to use special CCard in CreditCard details - AirPlus
    private boolean needSpecialCreditCard;
    //use this flag if we you want to use DescriptiveBillingInfo - Egenica Analytical codes
    private boolean needDescriptiveBillingInfo;

    //use this flag if we you want to use PaymentInstrumentToken
    private boolean needPaymentInstrumentToken;

    //use this flag if we you want to Hyphen in SiteMessageInfo/Language, e.g: en-GB
    private boolean setHyphenInLanguage;

    //use this flag if we you want to use DescriptiveBillingInfo for EDI and DBI data(Analytical codes)
    private boolean needEDIAndDBIData;
    //use this flag if Prepare purchase request need different cost price
    private boolean differentCostInPreparePurchase;
    private boolean differentPriceInPreparePurchase;


    public TestData(HttpClient httpClient, TestScenario scenarios, String tuid, String guid)
    {
        this.httpClient = httpClient;
        this.scenarios = scenarios;
        this.tuid = tuid;
        this.guid = guid;

        language = new Language("en", "US");
        auditLogTrackingData = new AuditLogTrackingData(false, false);
    }

    public TestData(HttpClient httpClient, TimeDuration useDays, TestScenario scenarios, String tuid, String guid, boolean extraHours)
    {
        this.httpClient = httpClient;
        this.scenarios = scenarios;
        this.tuid = tuid;
        this.guid = guid;
        this.useDays = useDays;
        this.extraHours = extraHours;

        language = new Language("en", "US");
        auditLogTrackingData = new AuditLogTrackingData(false, false);
    }

    public TestData(HttpClient httpClient, LocationSearchTestScenario locationSearchTestScenario, String tuid, String guid)
    {
        this.httpClient = httpClient;
        this.locationSearchTestScenario = locationSearchTestScenario;
        this.tuid = tuid;
        this.guid = guid;

        language = new Language("en", "US");
        auditLogTrackingData = new AuditLogTrackingData(false, false);
    }

    public TestData(HttpClient httpClient, TestScenario scenarios, String tuid, String guid, SpooferTransport spooferTransport)
    {
        this.httpClient = httpClient;
        this.scenarios = scenarios;
        this.tuid = tuid;
        this.guid = guid;
        this.spooferTransport = spooferTransport;

        language = new Language("en", "US");
        auditLogTrackingData = new AuditLogTrackingData(false, false);
    }

    public ResultFilter getResultFilter()
    {
        return resultFilter;
    }

    public void setResultFilter(ResultFilter resultFilter)
    {
        this.resultFilter = resultFilter;
    }

    public boolean isRegression()
    {
        return isRegression;
    }

    public void setRegression(boolean regression)
    {
        isRegression = regression;
    }

    public String getNeedPrepaidCar()
    {
        return needPrepaidCar;
    }

    public void setNeedPrepaidCar(String needPrepaidCar)
    {
        this.needPrepaidCar = needPrepaidCar;
    }

    public Long getResultSetCount()
    {
        return resultSetCount;
    }

    public void setResultSetCount(Long resultSetCount)
    {
        this.resultSetCount = resultSetCount;
    }

    public String getOptimizationStrategyCode()
    {
        return optimizationStrategyCode;
    }

    public void setOptimizationStrategyCode(String optimizationStrategyCode)
    {
        this.optimizationStrategyCode = optimizationStrategyCode;
    }

    public SpooferTransport getSpooferTransport()
    {
        return spooferTransport;
    }

    public void setSpooferTransport(SpooferTransport spooferTransport)
    {
        this.spooferTransport = spooferTransport;
    }

    public String getSpecificSupplySubset()
    {
        return specificSupplySubset;
    }

    public void setSpecificSupplySubset(String specificSupplySubset)
    {
        this.specificSupplySubset = specificSupplySubset;
    }

    public String getClientCode()
    {
        return clientCode;
    }

    public void setClientCode(String clientCode)
    {
        this.clientCode = clientCode;
    }

    public HttpClient getHttpClient()
    {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public TestScenario getScenarios()
    {
        return scenarios;
    }

    public void setScenarios(TestScenario scenarios)
    {
        this.scenarios = scenarios;
    }

    public LocationSearchTestScenario getLocationSearchTestScenario()
    {
        return locationSearchTestScenario;
    }

    public void setLocationSearchTestScenario(LocationSearchTestScenario locationSearchTestScenario)
    {
        this.locationSearchTestScenario = locationSearchTestScenario;
    }

    public String getTuid()
    {
        return tuid;
    }

    public void setTuid(String tuid)
    {
        this.tuid = tuid;
    }

    public String getGuid()
    {
        return guid;
    }

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
    }

    //audit log tracking data
    public void setAuditLogTrackingData(boolean forceDownstream, boolean forceLogging)
    {
        this.auditLogTrackingData = new AuditLogTrackingData(forceDownstream, forceLogging);
    }

    public boolean isForceDownstream()
    {
        return auditLogTrackingData.forceDownstream;
    }

    public void setForceDownstream(boolean forceDownstream)
    {
        this.auditLogTrackingData.forceDownstream = forceDownstream;
    }

    public boolean isForceLogging()
    {
        return auditLogTrackingData.forceLogging;
    }

    public void setForceLogging(boolean forceLogging)
    {
        this.auditLogTrackingData.forceLogging = forceLogging;
    }

    public CommonEnumManager.TimeDuration getUseDays()
    {
        return useDays;
    }

    public void setUseDays(CommonEnumManager.TimeDuration useDays)
    {
        this.useDays = useDays;
    }

    public CarRate getCarRate()
    {
        if (carRate == null)
        {
            carRate = new CarRate();
        }
        return carRate;
    }

    public void setCarRate(CarRate carRate)
    {
        this.carRate = carRate;
    }

    //SIPP
//    public void setSipp(long carCategoryCode, long carTypeCode, long carTransmissionDriveCode, long carFuelACCode) {
//        this.sipp = new SIPP(carCategoryCode, carTypeCode, carTransmissionDriveCode, carFuelACCode);
//    }
//
//    public SIPP getSipp() {
//        return sipp;
//    }

    public SpecialTestCasesParam getSpecialTestCasesParam()
    {
        if (specialTestCasesParam == null)
        {
            specialTestCasesParam = new SpecialTestCasesParam();
        }
        return specialTestCasesParam;
    }

    public void setSpecialTestCasesParam(SpecialTestCasesParam specialTestCasesParam)
    {
        this.specialTestCasesParam = specialTestCasesParam;
    }

    public TestScenarioSpecialHandleParam getTestScenarioSpecialHandleParam()
    {
        if (null == testScenarioSpecialHandleParam)
        {
            testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        }
        return testScenarioSpecialHandleParam;
    }

    public void setTestScenarioSpecialHandleParam(TestScenarioSpecialHandleParam testScenarioSpecialHandleParam)
    {
        this.testScenarioSpecialHandleParam = testScenarioSpecialHandleParam;
    }

    private class Language
    {
        private final String languageCode;
        private final String countryAlpha2Code;

        public Language(String languageCode, String countryAlpha2Code)
        {
            this.languageCode = languageCode;
            this.countryAlpha2Code = countryAlpha2Code;
        }
    }

    private class AuditLogTrackingData
    {
        private boolean forceDownstream;
        private boolean forceLogging;

        public AuditLogTrackingData(boolean forceDownstream, boolean forceLogging)
        {
            this.forceDownstream = forceDownstream;
            this.forceLogging = forceLogging;
        }
    }

    public TestDataErrHandle getErrHandle()
    {
        return errHandle;
    }

    public void setErrHandle(TestDataErrHandle errHandle)
    {
        this.errHandle = errHandle;
    }

    public String getSpecialTest()
    {
        return specialTest;
    }

    public void setSpecialTest(String specialTest)
    {
        this.specialTest = specialTest;
    }

    public String getCarSpecialEquipmentCode()
    {
        return carSpecialEquipmentCode;
    }

    public void setCarSpecialEquipmentCode(String carSpecialEquipmentCode)
    {
        this.carSpecialEquipmentCode = carSpecialEquipmentCode;
    }

    public String getSpecialEquipmentEnumType()
    {
        return specialEquipmentEnumType;
    }

    public void setSpecialEquipmentEnumType(String specialEquipmentEnumType)
    {
        this.specialEquipmentEnumType = specialEquipmentEnumType;
    }

    public boolean isMerchantBoolean()
    {
        return merchantBoolean;
    }

    public void setMerchantBoolean(boolean merchantBoolean)
    {
        this.merchantBoolean = merchantBoolean;
    }

    public boolean isWeekendBoolean()
    {
        return weekendBoolean;
    }

    public void setWeekendBoolean(boolean weekendBoolean)
    {
        this.weekendBoolean = weekendBoolean;
    }

    public boolean isExtraHours()
    {
        return extraHours;
    }

    public void setExtraHours(boolean extraHours)
    {
        this.extraHours = extraHours;
    }

    public boolean isNeedVO()
    {
        return needVO;
    }

    public void setNeedVO(boolean needVO)
    {
        this.needVO = needVO;
    }

    public String getBillingNumber()
    {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber)
    {
        this.billingNumber = billingNumber;
    }

    public boolean isNeedCC()
    {
        return needCC;
    }

    public void setNeedCC(boolean needCC)
    {
        this.needCC = needCC;
    }

    public boolean isWithFliInfo()
    {
        return withFliInfo;
    }

    public void setWithFliInfo(boolean withFliInfo)
    {
        this.withFliInfo = withFliInfo;
    }

    public boolean getNeedLoyaltyCard()
    {
        return needLoyaltyCard;
    }

    public void setNeedLoyaltyCard(boolean needLoyaltyCard)
    {
        this.needLoyaltyCard = needLoyaltyCard;
    }

    public boolean getNeedTravelerLoyalty()
    {
        return needTravelerLoyalty;
    }

    public void setNeedTravelerLoyalty(boolean needTravelerLoyalty)
    {
        this.needTravelerLoyalty = needTravelerLoyalty;
    }

    public boolean getNeedMultiTraveler()
    {
        return needMultiTraveler;
    }

    public void setNeedMultiTraveler(boolean needMultiTraveler)
    {
        this.needMultiTraveler = needMultiTraveler;
    }

    public boolean getNeedSpecialCreditCard()
    {
        return needSpecialCreditCard;
    }

    public void setNeedSpecialCreditCard(boolean needSpecialCreditCard)
    {
        this.needSpecialCreditCard = needSpecialCreditCard;
    }
    public boolean getdifferentCostInPreparePurchased() {
        return differentCostInPreparePurchase;
    }
    public void setdifferentCostInPreparePurchase(boolean differentCostInPreparePurchase) {
        this.differentCostInPreparePurchase = differentCostInPreparePurchase;
    }

    public boolean getdifferentPriceInPreparePurchased() {
        return differentPriceInPreparePurchase;
    }
    public void setdifferentPriceInPreparePurchase(boolean differentPriceInPreparePurchase) {
        this.differentPriceInPreparePurchase = differentPriceInPreparePurchase;
    }

    public boolean isNeedDescriptiveBillingInfo() {
        return needDescriptiveBillingInfo;
    }

    public void setNeedDescriptiveBillingInfo(boolean needDescriptiveBillingInfo) {
        this.needDescriptiveBillingInfo = needDescriptiveBillingInfo;
    }

    public boolean isNeedEDIAndDBIData() {
        return needEDIAndDBIData;
    }

    public void setNeedEDIAndDBIData(boolean needEDIAndDBIData) {
        this.needEDIAndDBIData = needEDIAndDBIData;
    }

    public Long getSpecificCarCategoryCode() {
        return specificCarCategoryCode;
    }

    public void setSpecificCarCategoryCode(Long specificCarCategoryCode) {
        this.specificCarCategoryCode = specificCarCategoryCode;
    }

    public boolean isSetHyphenInLanguage() {
        return setHyphenInLanguage;
    }

    public void setSetHyphenInLanguage(boolean setHyphenInLanguage) {
        this.setHyphenInLanguage = setHyphenInLanguage;
    }

    public boolean isNeedPaymentInstrumentToken() {
        return needPaymentInstrumentToken;
    }

    public void setNeedPaymentInstrumentToken(boolean needPaymentInstrumentToken) {
        this.needPaymentInstrumentToken = needPaymentInstrumentToken;
    }
}