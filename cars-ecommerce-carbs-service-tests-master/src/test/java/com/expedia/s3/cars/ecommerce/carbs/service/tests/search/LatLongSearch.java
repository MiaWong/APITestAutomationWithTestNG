package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.VendorLatLongList;
import com.expedia.s3.cars.ecommerce.carbs.service.database.CarbsDB;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyInvalidLatLongSearch;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class LatLongSearch {
    Logger logger = Logger.getLogger(getClass());

    SpooferTransport spooferTransport;
    private final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    private DataSource carsInvDatasource;
    private static final String INVALID_RADIUS_NODE_MESSAGE = "must be between 1 and 100";

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

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs57982CarBSLatLongSearchAgencyUS() throws Exception {
        final Map<String, Object> testParams = new HashMap<>();
        testParams.put("latLongList",VendorLatLongList.tfs57982CarBSLatLongSearchAgencyUS());
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "USAgencyStandaloneLatLong");
        this.carLatAndLongInSearchVerification(randomGuid, CommonScenarios.Worldspan_US_Agency_Standalone_US_LatLong_oneway.getTestScenario(), "2125248504", testParams);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs58371CarBSLatLongSearchGDSPUK() throws Exception {
        final Map<String, Object> testParams = new HashMap<>();
        testParams.put("latLongList",VendorLatLongList.carBSLatLongSearchUK());
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport,"ExtraHourPrice");
        this.carLatAndLongInSearchVerification(randomGuid, CommonScenarios.Worldspan_GBR_GDSP_Standalone_UK_LatLong_oneway.getTestScenario(), "2125248501", testParams);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs131295CarBSLatLongLocationIndexLocationCountAgencyUS() throws Exception {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport,"USAgencyStandaloneLatLong");
        this.carLatLongLocationIndexLocationCountInSearchVerification(randomGuid, CommonScenarios.Worldspan_US_Agency_Standalone_US_LatLong_startLocationIndex_LocationCount.getTestScenario(), "2125248510");
    }
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs58379CarBSLatLongInvalidSearchAgencyCA() throws Exception {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        this.carLatLongInInvalidSearchVerification(randomGuid, CommonScenarios.Worldspan_CA_Agency_Standalone_LatLong_oneway.getTestScenario(), "2125248501", INVALID_RADIUS_NODE_MESSAGE);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161822CarBSLatLongSearchAmadeusUK() throws Exception {
        final Map<String, Object> testParams = new HashMap<>();
        testParams.put("latLongList",VendorLatLongList.carBSLatLongSearchUK());
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        this.carLatAndLongInSearchVerification(randomGuid, CommonScenarios.Amadeus_GBR_Agency_Standalone_UK_LatLong_oneway.getTestScenario(), "161822", testParams);
    }

    private void carLatAndLongInSearchVerification(String guid, TestScenario scenarios, String tuid, Map<String, Object> testParams) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
        //send search Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);

        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());
        VerificationHelper.latLongInSearchResponseVerification(searchVerificationInput, scenarios, guid, logger, testParams);
    }

    private void carLatLongInInvalidSearchVerification(String guid, TestScenario scenarios, String tuid, String errorMsg) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.invalidSearch(testData);

        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());

        VerifyInvalidLatLongSearch.verifyErrorList(searchVerificationInput, scenarios, errorMsg);
    }

    private void carLatLongLocationIndexLocationCountInSearchVerification(String guid, TestScenario scenarios, String tuid) throws Exception
    {
        final CarbsDB carbsDB = new CarbsDB(DatasourceHelper.getCarBSDatasource());

            final int defaultCount = carbsDB.getValueByName(ServiceConfigs.SEARCH_DEFAULTLATLONGLOCATION_COUNT);
            final int maxCount = carbsDB.getValueByName(ServiceConfigs.SEARCH_MAXLATLONGLOCATION_COUNT);
            if (0 == defaultCount || 0 == maxCount)
            {
                Assert.fail("The client config Search.defaultLatLongLocationCount or Search.maxLatLongLocationCount is missing in client config, please make sure it is there !" );
            }
            TestData testData = new TestData(httpClient, scenarios, tuid, guid);
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
            //send search Request
            final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);
            final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());
            VerificationHelper.latLongLocationIndexLocationCountInSearchResponseVerification(searchVerificationInput, scenarios, guid, logger);

                // change the request's location count to greater then what we have maxlatlonglocationCount in DB

            final TestScenario changedScenario = new TestScenario("Worldspan_US_Agency_Standalone_US_LatLong_startLocationIndex_LocationCount",
                "USA", "10111", "1010", "USD", new BigDecimal("47.4428214"), new BigDecimal("-122.2988017"), 1, 1, 500, false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),1 );
            testData = new TestData(httpClient, changedScenario, tuid, guid);
            final CarbsRequestGenerator changedRequestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);
            final SearchVerificationInput changedSearchVerificationInput = new SearchVerificationInput(changedRequestGenerator.getSearchRequestType(),
                    changedRequestGenerator.getSearchResponseType());
            VerificationHelper.latLongLocationIndexLocationCountInSearchResponseVerification(changedSearchVerificationInput, changedScenario, guid, logger);


    }
}
