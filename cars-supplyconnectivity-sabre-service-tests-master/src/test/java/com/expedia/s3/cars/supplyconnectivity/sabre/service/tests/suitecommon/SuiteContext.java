package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon;

import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.BeforeClass;

import javax.sql.DataSource;
/**
 * Created by A-5858 on 19-12-2016.
 */
public class SuiteContext extends SuiteCommon
{
    public SpooferTransport spooferTransport;
    public DataSource carsSabreDatasource;


    private static final String POSTGRES_DRIVER_NAME = "org.postgresql.Driver";
    private static final String POSTGRES_JDBC_URL = "jdbc:postgresql://";
    private static final String SLASH = "/";
    private static final String OPTION_SCHEMA = "?currentSchema=";
    private static final String OPTION_USER = "&user=";
    private static final String OPTION_PASSWORD = "&password=";

    @BeforeClass(alwaysRun = true)
    public void sqlConnectionSetup() throws Exception
    {
        //  spoofer transport
        spooferTransport = new SpooferTransport(httpClient,
                SettingsProvider.SPOOFER_SERVER,
                SettingsProvider.SPOOFER_PORT,
                30000);

            carsSabreDatasource =  createLocalDataSource( SettingsProvider.DB_CARS_SABRE_DATABASE_SERVER,
                    SettingsProvider.DB_CARS_SABRE_DATABASE_NAME,
                    SettingsProvider.DB_SABRE_USER_NAME,
                    SettingsProvider.DB_SABRE_PASSWORD,
                    SettingsProvider.DB_USE_PASSWORD);
    }

    public static DataSource createLocalDataSource(String dbServer, String dbName, String userName, String password, boolean usePassword)
    {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setMaxActive(100);
        dataSource.setDriverClassName(POSTGRES_DRIVER_NAME);
        final StringBuffer connectionStringBuffer = new StringBuffer(POSTGRES_JDBC_URL);
        connectionStringBuffer.append(dbServer).append(SLASH).append(dbName);
        if(usePassword)
        {
            connectionStringBuffer.append(OPTION_SCHEMA).append(dbName).append(OPTION_USER).append(userName).append(OPTION_PASSWORD).append(password);
        }
        dataSource.setUrl(connectionStringBuffer.toString());
        return dataSource;
    }
}