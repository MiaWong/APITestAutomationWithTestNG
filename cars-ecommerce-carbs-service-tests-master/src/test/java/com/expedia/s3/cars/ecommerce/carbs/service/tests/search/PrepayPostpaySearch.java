package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
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
import java.util.List;

public class PrepayPostpaySearch {
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

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161736CarBSAgencyNeedCC() throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "USAgencyStandaloneLatLong").build(), randomGuid);
        this.carPostpayInSearchVerification(randomGuid, CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "2125248504");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161739CarBSMerchantPrePay() throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "WSCS_EUR_Daily").build(), randomGuid);
        this.carPrepayInSearchVerification(randomGuid, CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario(), "2125248504");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161741CarBSGDSPPrePay() throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "ExtraHourPrice").build(), randomGuid);
        this.carPrepayInSearchVerification(randomGuid, CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport_Oneway.getTestScenario(), "2125248504");

    }


    private void carPostpayInSearchVerification(String guid, TestScenario scenarios, String tuid) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_ZT);
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInvDatasource);
        final List<CarVendor> carVendors = carsInventoryHelper.getCarVendorList(RequestDefaultValues.VENDOR_CODE_ZT);
        specialHandleParam.setVendorSupplierID(Long.valueOf(carVendors.get(0).getSupplierID()));
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);

        carPrepayPostpayInSearchVerification(testData, guid, scenarios);

    }

    private void carPrepayInSearchVerification(String guid, TestScenario scenarios, String tuid) throws IOException, DataAccessException
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);

        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);

        carPrepayPostpayInSearchVerification(testData, guid, scenarios);
    }

    private void carPrepayPostpayInSearchVerification(TestData testData, String guid, TestScenario scenarios) throws IOException, DataAccessException
    {
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());
        final List<CarSearchResultType> carSearchResult = searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult();
        for (final CarSearchResultType searchResult : carSearchResult)
        {
            VerificationHelper.prepayPostpayInResponseVerification(searchResult.getCarProductList().getCarProduct(), scenarios, guid, logger, carsInvDatasource);
        }
    }
}
