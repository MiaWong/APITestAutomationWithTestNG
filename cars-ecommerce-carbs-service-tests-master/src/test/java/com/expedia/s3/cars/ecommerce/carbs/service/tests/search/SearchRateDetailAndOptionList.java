package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;

/**
  * Created by miawang on 9/19/2016.
  */
public class SearchRateDetailAndOptionList {
    Logger logger = Logger.getLogger(getClass());
    
    SpooferTransport spooferTransport;
    private final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    private DataSource carsInvDatasource;
    private DataSource tiDatasource;
    
    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        carsInvDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
                        SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
                        SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

                tiDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER, SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME,
                        SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }
    
    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }
    
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInSearch() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInSearchVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "2125248501");
    }
    
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInSearchWithCurrencyConvert() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInSearchVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(), "2125248501");
    }

    //    CASSS-2125    ConditionalCostList to be returned in xml format
    //    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
    private void carRateDetailInSearchVerification(String guid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
        //send search Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);

        //search Verification
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());
        VerificationHelper.carRateDetailInSearchVerification(searchVerificationInput, spooferTransport,
                carsInvDatasource, tiDatasource, scenarios, guid, true, logger);
    }

    // CASSS-2798 [CarBS] Special equipment needs to be returned with both POSa and POSu currency
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2798VerifyOptionListInSearch() throws IOException, DataAccessException {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListInSearchVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "2798011");
    }
    
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2798VerifyOptionListInSearchWithCurrencyConvert() throws IOException, DataAccessException {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListInSearchVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(), "2798012");
    }

    public void optionListInSearchVerification(String guid, TestScenario scenarios, String tuid) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //send search Request
        final  CarbsRequestGenerator requestGernerator = ExecutionHelper.executeSearch(testData, spooferTransport,
                carsInvDatasource, logger);

        //search Verification
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGernerator.getSearchRequestType(),
                requestGernerator.getSearchResponseType());
        VerificationHelper.optionListInSearchVerification(searchVerificationInput, scenarios, guid, logger);
    }

}