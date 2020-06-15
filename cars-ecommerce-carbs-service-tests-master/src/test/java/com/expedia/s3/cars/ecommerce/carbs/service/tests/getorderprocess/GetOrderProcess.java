package com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.GetOrderProcessVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;

/**
 * Created by miawang on 9/27/2016.
 */
@SuppressWarnings("PMD")
public class GetOrderProcess {
    Logger logger = Logger.getLogger(getClass());

    SpooferTransport spooferTransport;
    private HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    private DataSource carsInventoryDatasource;
    private DataSource titaniumDatasource;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 40000);
        carsInventoryDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
                SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
                SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

        titaniumDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER, SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInGetOrderProcess() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInGetOrderProcessVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2125248501");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInGetOrderProcessWithCurrencyConvert() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInGetOrderProcessVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.
                getTestScenario(), "2125248501");
    }

    //    CASSS-2125    ConditionalCostList to be returned in xml format
    //    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
    private void carRateDetailInGetOrderProcessVerification(String guid, TestScenario scenarios, String tuid) throws Exception {
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //send OMS PrepaidAgencyCarForReserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);

        //GetOrderProcess Verification
        final GetOrderProcessVerificationInput getOrderProcessVerificationInput = new GetOrderProcessVerificationInput
                (omRequestGenerate.getGetOrderProcessRequestType(), omRequestGenerate.getGetOrderProcessResponseType());

        final  Document spooferDoc = spooferTransport.retrieveRecords(guid);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));

        VerificationHelper.getOrderProcessBasicVerification(getOrderProcessVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
        VerificationHelper.carRateDetailInGetOrderProcessVerification(getOrderProcessVerificationInput, spooferTransport,
                carsInventoryDatasource, titaniumDatasource, scenarios, guid, true, logger);
    }

    // CASSS-2798 [CarBS] Special equipment needs to be returned with both POSa and POSu currency
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2798VerifyOptionListInGetOrderProcess() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListInGetOrderProcessVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2798");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2798VerifyOptionListInGetOrderProcessWithCurrencyConvert() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListInGetOrderProcessVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.
                getTestScenario(), "2798");
    }

    public void optionListInGetOrderProcessVerification(String guid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //send OMS PrepaidAgencyCarForReserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);

        //GetOrderProcess optionList Verification
        final GetOrderProcessVerificationInput getOrderProcessVerificationInput = new GetOrderProcessVerificationInput
                (omRequestGenerate.getGetOrderProcessRequestType(), omRequestGenerate.getGetOrderProcessResponseType());
        VerificationHelper.getOrderProcessBasicVerification(getOrderProcessVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
        VerificationHelper.optionListInGetOrderProcessVerification(getOrderProcessVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
    }
}