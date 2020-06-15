package com.expedia.s3.cars.supply.service.common;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.passwordvault.PVPlatformEnsconceIntegration;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;

import javax.sql.DataSource;

/**
 * A simple class with constants defined for various appconfig settings;
 * The appconfig "ResolveString" can take spring style placeholders (like ${some.setting=defaultValue}) and 'resolve' the values for u...
 * Created by sswaminathan on 8/5/16.
 */
@SuppressWarnings("PMD")
public class SettingsProvider
{

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

        public static String getApplicationID()
        {
            if (APP_ID == null) {
                APP_ID = AppConfig.resolveStringValue("${passwordVault.appID}");
            }
            return APP_ID;
        }

        public static String getUserID()
        {
            if (USER_ID == null) {
                USER_ID = AppConfig.resolveStringValue("${passwordVault.userID}");
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

    private SettingsProvider()
    {
        //nop
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

    public static boolean isRequiredDBAccess()
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
            else
            if (APPTEST_GROUP.equals(TestGroup.SHOPPING_REGRESSION) || APPTEST_GROUP.equals(TestGroup.BOOKING_REGRESSION)) {
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
        //  one more check if no argument in dev boc
        if (APPTEST_ENV.equals("dev") && APPTEST_GROUP.equals("All")) {
            //  if local dev box, we need hold PV implementation
            return false;
        }

        //  future support for other non stt01/stt05/sttstress02/int/prod environment : falling into true like dev
        return true;
    }

    public static String getSQLUserPassword()
    {
        return PVPlatformEnsconceIntegration.getSQLSecretPassword(
                VaultIntegrationAppConfigValues.getCertResource(),
                VaultIntegrationAppConfigValues.getHost(),
                VaultIntegrationAppConfigValues.getApplicationID(),
                VaultIntegrationAppConfigValues.getUserID(),
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

    public static void ResetPVCredential()
    {
        DB_PASSWORD = getSQLUserPassword();
        DB_USER_DOMAIN = getSQLUserDomain();
        DB_USER_NAME = getSQLUserName();
    }

    public static final String APPTEST_FARM = AppConfig.getEnvironment();
    public static final String APPTEST_ENV = getApplicationEnvironment();
    public static final String APPTEST_GROUP = getApplicationGroup();

    public static final String SPOOFER_SERVER = AppConfig.resolveStringValue("${spooferTransport.server.name}");
    public static final int SPOOFER_PORT = Integer.parseInt(AppConfig.resolveStringValue("${spooferTransport.server.port=80}"));

    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName=restservice}");

    // Database configuration
    public static final String DB_DRIVER_NAME = AppConfig.resolveStringValue("${databaseConfiguration.driverName}");
    public static final boolean DB_USE_PASSWORD = Boolean.parseBoolean(AppConfig.resolveStringValue("${databaseConfiguration.usePassword}"));
    public static boolean DB_USE_ON = isRequiredDBAccess();

    public static String DB_PASSWORD = (DB_USE_ON) ? getSQLUserPassword() : null;
    public static String DB_USER_DOMAIN = (DB_USE_ON) ? getSQLUserDomain() : null;
    public static String DB_USER_NAME = (DB_USE_ON) ? getSQLUserName() : null;

    public static final String DB_CARS_INVENTORY_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carsInventoryDatabaseServer}");
    public static final String DB_CARS_INVENTORY_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carsInventoryDatabaseName}");
    public static final String DB_CAR_SS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carSSDatabaseServer}");
    public static final String DB_CAR_SS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carSSDatabaseName}");

    final public  static DataSource CAR_INVENTORY_DATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);


}
