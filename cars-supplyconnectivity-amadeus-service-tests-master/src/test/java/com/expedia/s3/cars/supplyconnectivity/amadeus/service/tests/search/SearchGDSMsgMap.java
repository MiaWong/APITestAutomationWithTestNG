package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter
        .TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.log4j.Level;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 12/6/2016.
 */

public class SearchGDSMsgMap extends SuiteContext
{
    /**
     * Pickup at GEO location and DropOff at IATA, multiplecriteria - RoundTrip
     * Test_ASCS_Search("//ASCS_off_pickGEO_round_multipleCriteria_FR", tuid: "282366");
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282366PickGEODropOffRoundMultipleCriteriaFR() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCE.getTestScenario(),
                "282366", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * Pickup at GEO location dropoff IATA location, multiple search criteria and only require published rates(CDCode: EP-null) for the single vendors(EP,14) - Oneway with currency convert.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282367OffPickGEOOnewayMultipleCriteriaOneVendorPubRatesUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_MAN_EDI.getTestScenario(),
                "282367", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-null");

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        parameters.setCarRate(cdCode);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP");
    }

    /**
     * Pickup at GEO location dropoff IATA location, multiple search criteria and require CD rates for the each vendor with multiple CD code - Oneway
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    // <vendorCode>SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282368OffPickGEOOnewayMultipleCriteriaMulVendorCDCodeUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "282368", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("SX-5020105,ZI-N865556,ZE-3456789");

        parameters.setCarRate(cdCode);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP");
    }

    /**
     * Pickup at GEO, single search criteria and multiple supplierIDs is specified in request - Oneway
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282369AscsOnPickGEOOnewaySingleCriteriaMulVendorESP() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_OneWay_OffAirport_LYS_CDG.getTestScenario(),
                "282369", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();

        specialHandleParam.setPickUpCarVendorLocationCode("T001");
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * Pickup at GEO location,single search criteria-require CD rates, published rates- multiple vendor-multiple CD codes - Oneway
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282371AscsOnPickGEOOnewaySingleCriteriaMulVendorPubRateMulCDCodeESP() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(),
                "282371", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickUpCarVendorLocationCode("T001");
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-null,SX-5020105,SX-null,ZI-N865556,ZI-null,ZE-null,ZE-3456789");
        parameters.setCarRate(cdCode);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * Pickup at GEO location and DropOff at IATA, single search criteria and single supplierID is specified in request - RoundTrip MADT001-MAD
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282372PickGEODropOffRoundSingleCriteriaSingleVendorITA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_MAD.getTestScenario(),
                "282372", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(2);
        specialHandleParam.setPickUpCarVendorLocationCode("T001");
        specialHandleParam.setSearchCriteriaCount(1);
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * DropOff at GEO location , with multiple search criteria , CD rates-published rates , multiple CD code - RoundTrip
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282373OffDropGEORoundMultipleCriteriaMulVendorPubrateCDCodeFR() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(),
                "282373", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);

        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-null,SX-5020105,SX-null,ZI-N865556,ZI-null,ZE-null,ZE-3456789");
        parameters.setCarRate(cdCode);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * DropOff at GEO location, search with multiple search criteria - Oneway
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282374OffDropGEOOnewayMultipleCriteriaUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_MAN_EDI.getTestScenario(),
                "282374", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP");
    }

    /**
     * DropOff at GEO location,  single search criteria and multiple supplierIDs  - RoundTrip
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282375OnDropGEORoundSingleCriteriaMulVendorITA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_BCN.getTestScenario(),
                "282375", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);
        specialHandleParam.setDropOffCarVendorLocationCode("T001");
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * DropOff at GEO location, single search criteria, only  published rates for the multiple vendors - RoundTrip
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,SX,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282378OnDropGEORoundSingleCriteriaMulVendorPubRateITA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_BCN.getTestScenario(),
                "282378", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);
        specialHandleParam.setDropOffCarVendorLocationCode("T001");
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-null,SX-null,ZI-null,ZE-null");
        parameters.setCarRate(cdCode);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * DropOff GEO location, single search criteria,CD rates for the multiple vendors - RoundTrip
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282379OnDropGEORoundSingleCriteriaMulVendorCDRateITA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_BCN.getTestScenario(),
                "282379", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);
        specialHandleParam.setDropOffCarVendorLocationCode("T001");
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,ZI-N865556");
        parameters.setCarRate(cdCode);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
    public void TFS_282381_ASCS_on_dropGEO_oneway_singleCriteria_singleVendor_UK()
    {
        Test_ASCS_Search("//ASCS_on_dropGEO_oneway_singleCriteria_singleVendor_UK", tuid: "282381");
    }
     <ASCS_on_dropGEO_oneway_singleCriteria_singleVendor_UK>
     <tpid>60003</tpid>
     <eapid>0</eapid>
     <tuid>282381</tuid>
     <purchaseTypeMask>128</purchaseTypeMask>
     <onAirportBoolean>true</onAirportBoolean>
     <pickupAirport>LHR</pickupAirport>
     <dropoffAirport>EDIT001</dropoffAirport>
     <startDateFromNow>90</startDateFromNow>
     <endDateFromNow>91</endDateFromNow>
     <pickupGEOSupport>false</pickupGEOSupport>
     <vendorCode>ZI</vendorCode>
     </ASCS_on_dropGEO_oneway_singleCriteria_singleVendor_UK>
     */
    /**
     * DropOff GEO location, single search criteria and single supplierID - Oneway
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //<vendorCode>EP,ZE,ZI</vendorCode>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs282381OnDropGEOOnewaySingleCriteriaSingleVendorUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_LHR_EDI.getTestScenario(),
                "282381", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41L);
        specialHandleParam.setDropOffCarVendorLocationCode("T001");
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP");
    }


    /**
     * Drop-off at GEO location, multiple search criteria and multiple vendors  - RoundTrip
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    /*@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287904OffDropGEORoundMultipleCriteria_mulVendor_UK() throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, DataAccessException, IllegalAccessException
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_MAN.getTestScenario(),
                "287904", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(14L);
        vendorSupplierIDs.add(35L);
        vendorSupplierIDs.add(40L);
        vendorSupplierIDs.add(41L);
        specialHandleParam.setVendorSupplierIDs(vendorSupplierIDs);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }*/

    /**
     * Pickup at GEO location and DropOff at IATA, Oneway Multiple Criteria Multiple Vendor FR
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287908OffPickGEOOnewayMultipleCriteriaMultipleVendorFR() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_NCE_LYS.getTestScenario(),
                "287908", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for different roundtrip locations by IATA to SL
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287842SearchOptimizationMultipleIATAToSLRoundtrip() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(),
                "287842", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setSearchCriteriaCount(4);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 1 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with single search criteria for roundtrip locations by IATA to SL
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287843SearchOptimizationSingleIATAToSLRoundtrip() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCE.getTestScenario(),
                "287843", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setSearchCriteriaCount(1);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for differnet roundtrip locations by SL to  IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287844SearchOptimizationMultipleSLToIATARountrip() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG.getTestScenario(),
                "287844", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setSearchCriteriaCount(4);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 1 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with single search criteria for one-way locations by SL to IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287845SearchOptimizationSingleSLToIATAOneWay() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_CDG_PAR.getTestScenario(),
                "287845", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for different one-way locations by IATA to SL
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292578SearchOptimizationMultipleIATAToSLOneWay() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(),
                "292578", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setSearchCriteriaCount(4);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for  one-way/roundtrip locations by IATA to SL
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    //TODO <pickupAirport>CDG,CDG</pickupAirport>   <dropoffAirport>PAR,CDG</dropoffAirport>
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292583SearchOptimizationMultipleIATAToSLMix() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(),
                "292583", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setSearchCriteriaCount(2);

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 1 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with single search criteria for one-way locations by IATA to SL
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292588SearchOptimizationSingleIATAToSLOneWay() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(),
                "292588", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        //Pickup On-Airport drop-off off-airport
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setSearchCriteriaCount(4);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for differnet one-way locations by SL to  IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292590SearchOptimizationMultipleSLToIATAOneWay() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(),
                "292590", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        //Pickup On-Airport drop-off off-airport
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        specialHandleParam.setVendorSupplierID(14L);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for  one-way/roundtrip locations by SL to  IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292591SearchOptimizationMultipleCriteriaSLToIATAMix() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG.getTestScenario(),
                "292591", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        //Pickup On-Airport drop-off off-airport
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * The AmadeusSCS search request results in 1 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with single search criteria for roundtrip locations by SL to IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292592SearchOptimizationSingleCriteriaSLToIATARoundtrip() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_CDG_PAR.getTestScenario(),
                "292592", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        //Pickup On-Airport drop-off off-airport
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSOptimization(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR");
    }

    /**
     * Pickup at IATA location, Drop-off at GEO location, multiple search criteria and multiple vendors  - RoundTrip
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs287904OffDropGEORoundMultipleCriteriaMulVendorUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_MAN.getTestScenario(),
                "287904", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP");
    }

    private void amadeusSCSSearchGDSMsgMapping(TestData parameters, String searchTemplateOverrideName)
            throws Exception
    {
        final SearchVerificationInput searchVerificationInput = ascsSearch(parameters, searchTemplateOverrideName);

        SearchVerificationHelper.searchRequestGDSMsgMappingVerification(searchVerificationInput, spooferTransport, parameters.getScenarios(),
                parameters.getGuid(), logger);
        SearchVerificationHelper.searchResponseGDSMsgMappingVerification(searchVerificationInput, spooferTransport, parameters.getScenarios(),
                parameters.getGuid(), logger);
    }

    private void amadeusSCSOptimization(TestData parameters, String searchTemplateOverrideName)
            throws Exception {
        final SearchVerificationInput searchVerificationInput = ascsSearch(parameters, searchTemplateOverrideName);

        SearchVerificationHelper.searchRequestOptimizationVerification(searchVerificationInput, spooferTransport, parameters.getScenarios(),
                parameters.getGuid(), logger);
    }

    private SearchVerificationInput ascsSearch(TestData parameters, String searchTemplateOverrideName)
            throws Exception
    {
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(
                SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", searchTemplateOverrideName).build(), parameters.getGuid());

        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

//        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
//        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        return searchVerificationInput;
    }

    /**
     * Loyalty implementation check in Amadeus search request
     *
     * @throws Exception
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyLoyaltyInformationInSearchService() throws Exception
    {

        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(), "37233645", null);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);

        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "ACAQ_Amadeus_FR_Agency_Standalone_Loyalty_Code_Scenario").build(), "269d6dc8-4077-4224-bd37-562ceb5ce94f");

        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);

        boolean enableLoyalty = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(),"1","LoyaltyInformation/enable");

        if(enableLoyalty)
        {
            CarRate carRateType = new CarRate();
            carRateType.setLoyaltyNum("1012");
            testData.setCarRate(carRateType);
        }

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);

        logger.info("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse("269d6dc8-4077-4224-bd37-562ceb5ce94f",httpClient,searchRequest);

    }
}