package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.passwordvault.PVPlatformEnsconceIntegration;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;

import javax.sql.DataSource;

/**
 * A simple class with constants defined for various appconfig settings;
 * The appconfig "ResolveString" can take spring style placeholders (like ${some.setting=defaultValue}) and 'resolve' the values for u...
 * Created by miawang on 8/17/2016.
 */
public class SettingsProvider {

    private SettingsProvider() {
        //nop
    }

    public  static SpooferTransport getSpooferTransport(HttpClient httpClient)
    {
        return new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
    }
    public static final String APPTEST_FARM = AppConfig.getEnvironment();
    public static final String APPTEST_ENV = getApplicationEnvironment();
    public static final String APPTEST_GROUP = getApplicationGroup();

    public static final String SPOOFER_SERVER = AppConfig.resolveStringValue("${spooferTransport.server.name}");
    public static final int SPOOFER_PORT = Integer.parseInt(AppConfig.resolveStringValue("${spooferTransport.server.port=80}"));
    public static final boolean USE_SPOOFER = Boolean.parseBoolean(AppConfig.resolveStringValue("${serviceTransport.server.useSpoofer}"));

    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName=restservice}");
    //database
    // Database configuration
    public static final String DB_DRIVER_NAME = AppConfig.resolveStringValue("${databaseConfiguration.driverName}");
    public static final boolean DB_USE_PASSWORD = Boolean.parseBoolean(AppConfig.resolveStringValue("${databaseConfiguration.usePassword}"));
//database
    public static String DB_PASSWORD = isRequiredDB() ? getSQLUserPassword(): null;
    public static String DB_USER_DOMAIN = isRequiredDB() ? getSQLUserDomain() : null;
    public static String DB_USER_NAME =isRequiredDB() ? getSQLUserName() :  null;


    public static final String DB_CARS_INVENTORY_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carsInventoryDatabaseServer}");
    public static final String DB_CARS_INVENTORY_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carsInventoryDatabaseName}");
    public static final String DB_CARS_WORLDSPANSCS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carWorldspanSCSDatabaseServer}");
    public static final String DB_CARS_WORLDSPANSCS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carWorldspanSCSDatabaseName}");
    public static final String DB_CONFIGURATIONMASTER_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.configurationMasterDatabaseServer}");
    public static final String DB_CONFIGURATIONMASTER_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.configurationMasterDatabaseName}");

    //Environment
    public static final String ENVIRONMENT_NAME = AppConfig.resolveStringValue("${environment.name}");

    //update posconfig url
    public static final String UPDATE_POSCONFIG_URL =  AppConfig.resolveStringValue("${serviceTransport.posConfig.endPoint}");

    //FXRS Uri
    public static final String FXRS_URI= AppConfig.resolveStringValue("${fxrs.uri}");

    //bvt test
    public static final String BVTTEST_VENDORLIST = AppConfig.resolveStringValue("${bvtTest.vendorList}");
    public static final String BVTTEST_OFFAIRPORTLOCATIONLIST = AppConfig.resolveStringValue("${bvtTest.offAirportLocationList}");


    final public  static DataSource CARWORLDSPANSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_WORLDSPANSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_WORLDSPANSCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static String getSQLUserPassword()
    {
        return PVPlatformEnsconceIntegration.getSQLSecretPassword(
                VaultIntegrationAppConfigValues.getCertResource(),
                VaultIntegrationAppConfigValues.getHost(),
                VaultIntegrationAppConfigValues.getRoleID(),
                VaultIntegrationAppConfigValues.getSecretID(),
                VaultIntegrationAppConfigValues.getSecretApp(),
                VaultIntegrationAppConfigValues.getSecretUserDomain(),
                VaultIntegrationAppConfigValues.getSecretUserName(),
                VaultIntegrationAppConfigValues.getSecretPassword());
    }
    public static String getSQLUserName()
    {
        return PVPlatformEnsconceIntegration.getSQLSecretUser();
    }

    public static String getSQLUserDomain()
    {
        return PVPlatformEnsconceIntegration.getSQLSecretDomain();
    }
    public static class VaultIntegrationAppConfigValues {
        private static String HOST = null;
        private static String APP_ID = null;
        private static String USER_ID = null;
        private static String SECRET_APP = null;

        private static String SECRET_USERDOMAIN = null;
        private static String SECRET_USERNAME = null;
        private static String SECRET_USERPASSWORD = null;
        private static String CERT_RESOURCE = null;

        private VaultIntegrationAppConfigValues() { }

        public static String getHost()
        {
            if (HOST == null) {
                HOST = AppConfig.resolveStringValue("${passwordVault.hostUrl}");
            }
            return HOST;
        }

        public static String getRoleID()
        {
            if (APP_ID == null) {
                APP_ID = AppConfig.resolveStringValue("${passwordVault.roleID}");
            }
            return APP_ID;
        }

        public static String getSecretID()
        {
            if (USER_ID == null) {
                USER_ID = AppConfig.resolveStringValue("${passwordVault.secretID}");
            }
            return USER_ID;
        }

        public static String getSecretApp()
        {
            if (SECRET_APP == null) {
                SECRET_APP = AppConfig.resolveStringValue("${passwordVault.secretApp}");
            }
            return SECRET_APP;
        }

        public static String getSecretUserDomain()
        {
            if (SECRET_USERDOMAIN == null) {
                SECRET_USERDOMAIN = AppConfig.resolveStringValue("${passwordVault.secretUserDomain}");
            }
            return SECRET_USERDOMAIN;
        }

        public static String getSecretUserName()
        {
            if (SECRET_USERNAME == null) {
                SECRET_USERNAME = AppConfig.resolveStringValue("${passwordVault.secretUserName}");
            }
            return SECRET_USERNAME;
        }

        public static String getSecretPassword()
        {
            if (SECRET_USERPASSWORD == null) {
                SECRET_USERPASSWORD = AppConfig.resolveStringValue("${passwordVault.secretUserPassword}");
            }
            return SECRET_USERPASSWORD;
        }


        public static String getCertResource()
        {
            if (CERT_RESOURCE == null) {
                CERT_RESOURCE = AppConfig.resolveStringValue("${passwordVault.certResource}");
            }
            return CERT_RESOURCE;
        }
    }

    private static String getApplicationEnvironment()
    {
        String env = System.getProperty("application.environment");
        if (APPTEST_FARM.equals("dev") && StringUtil.isBlank(env)) {
            env = "dev";
        }
        return env;
    }

    private static String getApplicationGroup()
    {
        String group = System.getProperty("groups");
        if (APPTEST_FARM.equals("dev") && StringUtil.isBlank(group)) {
            group = "All";
        }
        return group;
    }

    public static boolean isRequiredDB()
    {
        Assert.assertNotNull(APPTEST_ENV);
        Assert.assertNotNull(APPTEST_GROUP);

        if (SuiteCommon.logger != null) {
            SuiteCommon.logger.info("ENV = " + APPTEST_ENV);
            SuiteCommon.logger.info("GROUP = " + APPTEST_GROUP);
        }

        if (APPTEST_ENV.equals("prod") || APPTEST_ENV.equals("int") || APPTEST_GROUP.equals(TestGroup.BVT)) {
            return false;
        }

        if (APPTEST_ENV.equals("stt01") || APPTEST_ENV.equals("stt05") || APPTEST_ENV.equals("sttstress02") || APPTEST_ENV.equals("test")) {
            if (APPTEST_GROUP.equals(TestGroup.SHOPPING_REGRESSION_LIVE) || APPTEST_GROUP.equals(TestGroup.BOOKING_REGRESSION_LIVE)) {
                return false;
            }
            else if (APPTEST_GROUP.equals(TestGroup.SHOPPING_REGRESSION) || APPTEST_GROUP.equals(TestGroup.BOOKING_REGRESSION)) {
                return true;
            }
        /*  else
            if (APPTEST_GROUP.equals("All"))
            {
                return true;
            }
        */
            //  future support for other group type : falling into true
        }

        //  future support for other non stt01/stt05/sttstress02/int/prod environment : falling into true like dev
        return true;
    }
}
