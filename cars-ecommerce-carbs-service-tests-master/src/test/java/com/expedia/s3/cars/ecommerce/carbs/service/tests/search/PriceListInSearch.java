package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyCarPriceListInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.GDSPCarType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.ResultFilter;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARRsp;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * Created by miawang on 9/19/2016.
 */
public class PriceListInSearch {
    @SuppressWarnings("CPD-START")
    Logger logger = Logger.getLogger(getClass());

    SpooferTransport spooferTransport;
    private final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    private DataSource carsInvDatasource;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        carsInvDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
                SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
                SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }
    @SuppressWarnings("CPD-END")
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForAgencyWeeklyPrice61283() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListAgencyWeeklyPrice");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario(), "2125261283", CommonEnumManager.TimeDuration.WeeklyDays7, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForAgencyDailyPrice61281() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListAgencyDailyPrice");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "2125261281", CommonEnumManager.TimeDuration.Days2, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForAgencyExtraHourPrice61282() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ExtraHourPrice");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "2125261282", CommonEnumManager.TimeDuration.Daily, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForAgencyMonthlyExtraDayPrice61286() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListMonthlyExtraDayPrice");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "2125261286", CommonEnumManager.TimeDuration.MounthlyExtDays35, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForGDSPDailyPrice61290() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUIDwithOverrideTemplate(spooferTransport, "PriceListGDSPDailyPrice");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS_CDG.getTestScenario(), "2125261290", CommonEnumManager.TimeDuration.Days2, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForGDSPWeeklyPrice61292() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUIDwithOverrideTemplate(spooferTransport, "PriceListGDSPWeeklyPrice");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "2125261292", CommonEnumManager.TimeDuration.WeeklyExtDays, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchAmadeusDailyCurrency180945() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "180945", CommonEnumManager.TimeDuration.Daily, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchAmadeusOnewayWeekly180947() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_DynamicSearch_Weekly_GB_POSU_GBP");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_LCY.getTestScenario(),
                "180947", CommonEnumManager.TimeDuration.WeeklyDays6, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchAmadeusMonthlyly180960() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_DynamicSearch_Monthly_FR_POSU_EUR");
        this.carPriceListVerification(randomGuid,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "180960", CommonEnumManager.TimeDuration.Mounthly, false);
    }

    private void carPriceListVerification(String guid, TestScenario scenarios, String tuid, CommonEnumManager.TimeDuration timeDuration, boolean extraHours) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, timeDuration, scenarios, tuid, guid, extraHours);
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);

        //search Verification
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());
        VerificationHelper.carPriceListInSearchVerification(searchVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs236191VerifyMNDynamicCommissionForPackageOnAirport() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        validateDymanicCommission(CommonScenarios.MN_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), randomGuid, "236191");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs236188VerifyMNDynamicCommissionForStandaloneOffAirport() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        validateDymanicCommission(CommonScenarios.MN_GBR_Standalone_RoundTrip_OffAirport_AGP.getTestScenario(), randomGuid, "236188");
    }

    public void validateDymanicCommission(TestScenario testScenario, String guid, String tuid) throws Exception {
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "MN_Search_POSuEUR").build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(guid), guid, testScenario);
        final VARRsp varRsp = new VARRsp(basicVerificationContext.getSpooferTransactions().getElementsByTagName("VehAvailRateRS").item(0), new CarsSCSDataSource(DatasourceHelper.getMicronNexusDatasource()));
        VerifyCarPriceListInSearchResponse.assertSearchResponseForDynamicCommission(requestGenerator.getSearchResponseType(), varRsp);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs150117StandaloneDailySearchOffAirport() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "MN_Search_POSuEUR").build(), guid);

        final TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OffAirport_AGP.getTestScenario(),
                "236189", guid);

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);
        final List<CarProductType> carProductTypes = requestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(
                requestGenerator.getSearchResponseType().getCarSearchResultList(), testData);
        Assert.assertTrue(!carProductTypes.isEmpty());

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchGDSPNetRate16026() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_DynamicSearch_Monthly_FR_POSU_EUR");
        gdspNetRateCarPriceListVerification(randomGuid,
                CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "16026", CommonEnumManager.TimeDuration.Mounthly, false);
    }

    private void gdspNetRateCarPriceListVerification(String guid, TestScenario scenarios, String tuid, CommonEnumManager.TimeDuration timeDuration, boolean extraHours) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, timeDuration, scenarios, tuid, guid, extraHours);
        final ResultFilter resultFilter = new ResultFilter();
        resultFilter.setCarType(GDSPCarType.GDSPNetRate);
        testData.setResultFilter(resultFilter);

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);

        //search Verification
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());
        //filter for GDSP Net Rate car
        searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().get(0)
                .getCarProductList().setCarProduct(requestGenerator
                .selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(searchVerificationInput.getResponse().getCarSearchResultList(), testData));

        VerificationHelper.carPriceListInSearchVerification(searchVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
    }



}