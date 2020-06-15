package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import org.testng.annotations.BeforeClass;

import javax.sql.DataSource;

/**
 * Created by jiyu on 12/6/16 :
 *    to extract this part into service test common for all of individual suites
 */
@SuppressWarnings("PMD")
public class SuiteContext extends SuiteCommon
{
    public SpooferTransport spooferTransport;
    public static DataSource carSCSDatasource;

    @BeforeClass(alwaysRun = true)
    public void preBeforeClass() throws Exception
    {
        logger.info("@BeforeClass starts-----");
        //  spoofer transport
        spooferTransport = new SpooferTransport(httpClient,
                SettingsProvider.SPOOFER_SERVER,
                SettingsProvider.SPOOFER_PORT,
                30000);

        carSCSDatasource = sqlConnectionSetup();

    }

    private DataSource sqlConnectionSetup() throws Exception
    {
        //  SQL DB data source
        return  DatabaseSetting.createDataSource(
                    SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER,
                    SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
                    SettingsProvider.DB_USER_DOMAIN,
                    SettingsProvider.DB_USER_NAME,
                    SettingsProvider.DB_PASSWORD,
                    SettingsProvider.DB_USE_PASSWORD);

    }


    public static CarSupplyConnectivitySearchRequestType createSearchRequest(TestScenario scenarios, String tuid, String guid) throws DataAccessException, Exception
    {
            final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
            final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carSCSDatasource);
            CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);

        return searchRequest;
    }

    public static CarSupplyConnectivitySearchRequestType createSearchRequest(TestData testData) throws  Exception
    {

         final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carSCSDatasource);
        CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);


        return searchRequest;
    }
/*

    public static void testHook2ExportCCSRKeyInfo(String scenario, CarSupplyConnectivitySearchRequestType request) throws Exception
    {
        final CCSRSearchRequestTestData ccsrExportObject = SCSSearchRequestGenerator.preExportCCSRConverter(request);
        final CCSRJavaToJSON jsonWriter = new CCSRJavaToJSON(scenario, scenario + ".json", ccsrExportObject);
        //  export to json file
        jsonWriter.exportJSONFile();
    }

    public static CarSupplyConnectivitySearchRequestType testHook2ImportCCSRKeyInfo(String scenario, TestData testData) throws Exception
    {
        final CCSRJavaFromJSON jsonReader = new CCSRJavaFromJSON(scenario, "jsonTestDataCCSR.json");
        //  export to json file
        CCSRSearchRequestTestData ccsrImportObject = jsonReader.parseJSONFileAsResource();

        return SCSSearchRequestGenerator.postImportCCSRConverter(ccsrImportObject, testData);

    }
*/

}
