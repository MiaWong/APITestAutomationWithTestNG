package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.v5;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.v5.util.SearchHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5.VerifyPricingInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager.TimeDuration;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * TODO: commented out tests are not implemented yet.  These fall out of the scope of the initial v5 integration.
 */
@SuppressWarnings("PMD")
public class PricingInSearch
{
    private final HttpClient httpClient = new HttpClient(new SslContextFactory(true));

    private Logger logger = Logger.getLogger(getClass());
    private SpooferTransport spooferTransport;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception
    {
        httpClient.start();
        spooferTransport = new SpooferTransport(
                httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
    }

    @AfterClass
    public void tearDown() throws Exception
    {
        httpClient.stop();
    }

    /**
     * Test the pricing information returned in the response for US Agency weekly non US on airport.
     *
     * @throws Exception
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void testPricingInSearchForAgencyWeeklyPrice61283() throws Exception
    {
        final String randomGUID = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListAgencyWeeklyPrice");
        verifyPriceListInSearch(randomGUID,
                CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario(),
                "2125261283", TimeDuration.WeeklyDays7, false);

    }

    /**
     * Test the pricing is returned for daily agency.
     *
     * @throws Exception
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void testPricingInSearchForAgencyDailyPrice61281() throws Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListAgencyDailyPrice");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "2125261281", TimeDuration.Days2, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void testPricingInSearchForAgencyExtraHourPrice61282() throws Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ExtraHourPrice");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "2125261282", TimeDuration.Daily, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForAgencyMonthlyExtraDayPrice61286() throws Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListMonthlyExtraDayPrice");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(),
                "2125261286", TimeDuration.MounthlyExtDays35, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForGDSPDailyPrice61290() throws Exception 
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUIDwithOverrideTemplate(
                spooferTransport, "PriceListGDSPDailyPrice");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS_CDG.getTestScenario(),
                "2125261290", TimeDuration.Days2, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchForGDSPWeeklyPrice61292() throws Exception 
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUIDwithOverrideTemplate(
                spooferTransport, "PriceListGDSPWeeklyPrice");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "2125261292", TimeDuration.WeeklyExtDays, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchAmadeusDailyCurrency180945() throws Exception 
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "180945", TimeDuration.Daily, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchAmadeusOnewayWeekly180947() throws Exception 
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(
                spooferTransport, "Amadues_DynamicSearch_Weekly_GB_POSU_GBP");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_LCY.getTestScenario(),
                "180947", TimeDuration.WeeklyDays6, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchAmadeusMonthlyly180960() throws Exception 
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(
                spooferTransport, "Amadues_DynamicSearch_Monthly_FR_POSU_EUR");
        verifyPriceListInSearch(randomGuid,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "180960", TimeDuration.Mounthly, false);
    }

    /*
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs236191VerifyMNDynamicCommissionForPackageOnAirport() throws Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        validateDymanicCommission(
                CommonScenarios.MN_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), randomGuid, "236191");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs236188VerifyMNDynamicCommissionForStandaloneOffAirport() throws Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        validateDymanicCommission(CommonScenarios.MN_GBR_Standalone_RoundTrip_OffAirport_AGP.getTestScenario(), randomGuid, "236188");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs150117StandaloneDailySearchOffAirport() throws Exception
    {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "MN_Search_POSuEUR").build(), guid);

        final TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OffAirport_AGP.getTestScenario(),
                "236189", guid);

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);
        final List<CarProductType> carProductTypes = requestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(
                requestGenerator.getSearchResponseType().getCarSearchResultList(), testData);
        Assert.assertTrue(!carProductTypes.isEmpty());

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInSearchGDSPNetRate16026() throws Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_DynamicSearch_Monthly_FR_POSU_EUR");
        gdspNetRateCarPriceListVerification(randomGuid,
                CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "16026", TimeDuration.Mounthly, false);
    }
    */

    private void verifyPriceListInSearch(String guid, TestScenario scenarios, String userGUID,
                                         TimeDuration timeDuration, boolean extraHours) throws Exception
    {
        final TestData testData = new TestData(httpClient, timeDuration, scenarios, userGUID, guid, extraHours);
        final V5SearchVerificationInput searchVerificationInput = SearchHelper.search(httpClient, testData, userGUID);
        SearchHelper.searchVerification(
                searchVerificationInput, spooferTransport, scenarios, guid, logger, false);
        SearchHelper.priceListVerification(
                searchVerificationInput, spooferTransport, testData, guid, logger, false);
    }

    private void validateDymanicCommission(TestScenario testScenario, String guid, String userGUID) throws Exception
     {
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride(
                "ScenarioName", "MN_Search_POSuEUR").build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, userGUID, guid);
        final V5SearchVerificationInput searchVerificationInput = SearchHelper.search(httpClient, testData, userGUID);
         SearchHelper.searchVerification(
                 searchVerificationInput, spooferTransport, testScenario, guid, logger, false);

         final BasicVerificationContext basicVerificationContext =
                 new BasicVerificationContext(spooferTransport.retrieveRecords(guid), guid, testScenario);
        final VARRsp varRsp = new VARRsp(basicVerificationContext.getSpooferTransactions()
                .getElementsByTagName("VehAvailRateRS").item(0),
                new CarsSCSDataSource(DatasourceHelper.getMicronNexusDatasource()));
        VerifyPricingInSearchResponse
                .assertSearchResponseForDynamicCommission(searchVerificationInput.getResponse(), varRsp);
    }

    /*
    private void gdspNetRateCarPriceListVerification(String guid, TestScenario scenarios, String tuid, CommonEnumManager.TimeDuration timeDuration, boolean extraHours) throws Exception
     {
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
    */
}
