package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyOffaiportLocationReturnedMatchedReq;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
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
import java.util.UUID;

/**
  * Created by v-mechen on 8/19/2018.
  */

public class OffairportLocationSearch {
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
    public void verifyOffAirportLocationOnewayTFS179721() throws IOException, DataAccessException {
        final String randomGuid = UUID.randomUUID().toString();
        this.offAirportLocationTest(randomGuid,
                        CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_CDG_PAR.getTestScenario(), "179721", CommonEnumManager.TimeDuration.WeeklyDays7, false);
    }

    private void offAirportLocationTest(String guid, TestScenario scenarios, String tuid, CommonEnumManager.TimeDuration timeDuration,
                                        boolean extraHours) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, timeDuration, scenarios, tuid, guid, extraHours);
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInvDatasource, logger);

        //search Verification
        VerifyOffaiportLocationReturnedMatchedReq.verifyLocationReturned(requestGenerator.getSearchRequestType(), requestGenerator.getSearchResponseType());
    }
}
