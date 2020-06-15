package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.vault.PVPlatformEnsconceIntegration;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.StringUtil;

import javax.sql.DataSource;


/**
 * A simple class with constants defined for various appconfig settings;
 * The appconfig "ResolveString" can take spring style placeholders (like ${some.setting=defaultValue}) and 'resolve' the values for u...
 * Created by sswaminathan on 8/5/16.
 */
@SuppressWarnings("PMD")
public class SettingsProvider
{
    private SettingsProvider()
    {
        //nop
    }

    public static final String SPOOFER_SERVER = AppConfig.resolveStringValue("${spooferTransport.server.name}");

    public static final int SPOOFER_PORT = Integer.parseInt(AppConfig.resolveStringValue("${spooferTransport.server.port=443}"));

    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName=restservice}");
    public static final boolean SPOOFER_ENABLE = Boolean.parseBoolean(AppConfig.resolveStringValue("${serviceTransport.server.useSpoofer}"));
    //database
    public static String DB_PASSWORD = isRequiredDB() ? getSQLUserPassword(): null;
    public static String DB_USER_DOMAIN = isRequiredDB() ? getSQLUserDomain() : null;
    public static String DB_USER_NAME = isRequiredDB() ? getSQLUserName() :  null;

    public static final boolean DB_USE_PASSWORD = Boolean.parseBoolean(AppConfig.resolveStringValue("${databaseConfiguration.usePassword}"));
    public static final String DB_CARS_SCS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carSCSDatabaseServer}");
    public static final String DB_CARS_SCS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carSCSDatabaseName}");
    //Environment
    public static final String ENVIRONMENT_NAME = AppConfig.resolveStringValue("${environment.name}");

    public static final String BVTTEST_VENDORLIST = AppConfig.resolveStringValue("${bvtTest.vendorList}");
    public static final String BVTTEST_OFFAIRPORTLOCATIONLIST = AppConfig.resolveStringValue("${bvtTest.offAirportLocationList}");

    public static final String UPDATE_POSCONFIG_URL_AMADEUS = AppConfig.resolveStringValue("${serviceTransport.posConfig.endPoint}");


    public  static SpooferTransport getSpooferTransport(HttpClient httpClient)
    {
        return new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
    }

    /**
     * CarSCS datasource
     */
    public static final   DataSource CARAMADEUSSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);



    public static String getSQLUserPassword()
    {
        PVPlatformEnsconceIntegration pvPlatformEnsconceIntegration = new PVPlatformEnsconceIntegration();
        return pvPlatformEnsconceIntegration.getSQLSecretPassword(
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

    public static boolean isRequiredDB() {
        String groups = System.getProperty("groups");
        if (StringUtil.isNotBlank(groups)) {
            if (groups.equals("prod") || groups.equals("int") || groups.equals(TestGroup.BVT) || groups.equals(TestGroup.SHOPPING_BVT)) {
                return false;
            }
        }
        return true;
    }
}
