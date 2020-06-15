package com.expedia.s3.cars.ecommerce.carbs.service.commonutils;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.test.common.passwordvault.PVPlatformEnsconceIntegration;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.StringUtil;

/**
 * Created by fehu on 8/9/2016.
 */
@SuppressWarnings("PMD")
public class SettingsProvider {

    private SettingsProvider() {
    }

    public static class VaultIntegrationAppConfigValues {
        private static String HOST = null;
        private static String APP_ID = null;
        private static String USER_ID = null;
        private static String SECRET_APP = null;

        private static String SECRET_USERDOMAIN = null;
        private static String SECRET_USERNAME = null;
        private static String SECRET_USERPASSWORD = null;
        private static String SECRET_SCS_USERNAME = null;
        private static String SECRET_SCS_USERPASSWORD = null;
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

        public static String getSecretSCSUserName()
        {
            if (SECRET_SCS_USERNAME == null) {
                SECRET_SCS_USERNAME = AppConfig.resolveStringValue("${passwordVault.secretSCSUserName}");
            }
            return SECRET_SCS_USERNAME;
        }

        public static String getSecretSCSPassword()
        {
            if (SECRET_SCS_USERPASSWORD == null) {
                SECRET_SCS_USERPASSWORD = AppConfig.resolveStringValue("${passwordVault.secretSCSUserPassword}");
            }
            return SECRET_SCS_USERPASSWORD;
        }

        public static String getCertResource()
        {
            if (CERT_RESOURCE == null) {
                CERT_RESOURCE = AppConfig.resolveStringValue("${passwordVault.certResource}");
            }
            return CERT_RESOURCE;
        }
    }
    public static final String SPOOFER_SERVER = AppConfig.resolveStringValue("${spooferTransport.server.name}");
    public static final int SPOOFER_PORT = Integer.parseInt(AppConfig.resolveStringValue("${spooferTransport.server.port=80}"));

    public static final boolean SPOOFER_ENABLE = Boolean.parseBoolean(AppConfig.resolveStringValue("${serviceTransport.server.useSpoofer}"));
    public static final String SERVICE_LOCATION_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.locationserver.address}");

    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_V5_SEARCH_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.v5search.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName}");


    //OM service
    public static final String GETPROCESSORDER_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.GetOrderProcess.address}");
    public static final String CREATERECORD_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.CreateRecord.address}");
    public static final String PREPAREPURCHASE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.PreparePurchase.address}");
    public static final String ROLLBACKPREPAREPURCHASE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.RollbackPreparePurchase.address}");
    public static final String COMMITPREPAREPURCHASE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.CommitPreparePurchase.address}");
    public static final String RETRIEVE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.Retrieve.address}");
    public static final String GETCHANGEPROCESS_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.GetChangeProcess.address}");
    public static final String PREPARECHANGE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.PrepareChange.address}");
    public static final String COMMITPREPARECHANGE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.CommitPrepareChange.address}");
    public static final String ROLLBACKPREPARECHANGE_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.RollbackPrepareChange.address}");
    public static final String GETCHANGEDETAIL_SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.GetChangeDetail.address}");

    public static final String SERVICE_E3DESTINATIONFORGETORDER = AppConfig.resolveStringValue("${serviceTransport.GetOrderProcess.destination=order/GetOrderProcess}");
    public static final String SERVICE_E3DESTINATIONFORCREATERECORD = AppConfig.resolveStringValue("${serviceTransport.CreateRecord.address=order/CreateRecord}");
    public static final String SERVICE_E3DESTINATIONFORPREPAREPURCHASE = AppConfig.resolveStringValue("${serviceTransport.PreparePurchase.address=order/PreparePurchase}");
    public static final String SERVICE_E3DESTINATIONFORCOMMITPURCHASE = AppConfig.resolveStringValue("${serviceTransport.CommitPreparePurchase.address=order/CommitPreparePurchase}");
    public static final String SERVICE_E3DESTINATIONFORROLLBACKPUCHASE = AppConfig.resolveStringValue("${serviceTransport.RollbackPreparePurchase.address=order/RollbackPreparePurchase}");
    public static final String SERVICE_E3DESTINATIONFORRETRIEVE = AppConfig.resolveStringValue("${serviceTransport.Retrieve.address=order/Retrieve}");
    public static final String SERVICE_E3DESTINATIONFORGETCHANGE = AppConfig.resolveStringValue("${serviceTransport.GetChangeProcess.address=order/GetChangeProcess}");
    public static final String SERVICE_E3DESTINATIONFORPREPARECHANGE = AppConfig.resolveStringValue("${serviceTransport.PrepareChange.address=order/PrepareChange}");
    public static final String SERVICE_E3DESTINATIONFORCOMMITPREPARE = AppConfig.resolveStringValue("${serviceTransport.CommitPrepareChange.address=order/CommitPrepareChangeInput}");
    public static final String SERVICE_E3DESTINATIONFORROLLBACKPREPARE = AppConfig.resolveStringValue("${serviceTransport.RollbackPrepareChange.address=order/RollbackPrepareChange}");
    public static final String SERVICE_E3DESTINATIONGETCHANGEDETAIL = AppConfig.resolveStringValue("${serviceTransport.getChangeDetail.address=order/GetChangeDetai}");

    // Database configuration
    public static final String DB_DRIVER_NAME = AppConfig.resolveStringValue("${databaseConfiguration.driverName}");

    //database
   public static String DB_PASSWORD = isRequiredDB() ? getSQLUserPassword() : null;
    public static String DB_USER_DOMAIN = isRequiredDB() ? getSQLUserDomain() : null;
    public static String DB_USER_NAME =isRequiredDB() ? getSQLUserName() :  null;
    public static String DB_SCS_USER_NAME =isRequiredDB() ? getSCSSQLUserName() :  null;
    public static String DB_SCS_PASSWORD = isRequiredDB() ? getSCSSQLUserPassword() : null;



    public static final boolean DB_USE_PASSWORD = Boolean.parseBoolean(AppConfig.resolveStringValue("${databaseConfiguration.usePassword}"));

    //carInventory
    public static final String DB_CARS_INVENTORY_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carsInventoryDatabaseServer}");
    public static final String DB_CARS_INVENTORY_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carsInventoryDatabaseName}");

    //carbooking
    public static final String DB_CARS_BOOKING_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carbookingDatabaseServer}");
    public static final String DB_CARS_BOOKING_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carbookingDatabaseName}");


    //Carbs
    public static final String DB_CARS_BS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carsBSDatabaseServer}");
    public static final String DB_CARS_BS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carsBSDatabaseName}");

    //ConfigurationMaster
    public static final String DB_CONFIGURATIONMASTER_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.configurationMasterDatabaseServer}");
    public static final String DB_CONFIGURATIONMASTER_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.configurationMasterDatabaseName}");

    //titanium scs database
    public static final String DB_TITANIUMSCS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.cartitaniumscsDatabaseServer}");
    public static final String DB_TITANIUMSCS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.cartitaniumscsDatabaseName}");

    //worldspan scs database
    public static final String DB_WORLDSPANSCS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carWorldspanscsDatabaseServer}");
    public static final String DB_WORLDSPANSCS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carWorldspanscsDatabaseName}");

    //Amadeus scs database
    public static final String DB_AMADEUSSCS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carAmadeusscsDatabaseServer}");
    public static final String DB_AMADEUSSCS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carAmadeusscsDatabaseName}");

    //MicronNexus scs database
    public static final String DB_MICRONNEXUS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carMicronNexusscsDatabaseServer}");
    public static final String DB_MICRONNEXUS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carMicronNexusscsDatabaseName}");

    public static final String SCS_TITANIUM_ADDRESS = AppConfig.resolveStringValue("${scs.titanium.address}");

    public static final String CARBS_POS_SET_ADDRESS = AppConfig.resolveStringValue("${setPosConfig.server.address}");

    public static final String WSCS_POS_SET_ADDRESS = AppConfig.resolveStringValue("${setWSCSPosConfig.server.address}");



    public static  SpooferTransport spooferTransport = null;

    public  static SpooferTransport getSpooferTransport(HttpClient httpClient)
    {
        if(null != spooferTransport)
        {
           return  spooferTransport;
        }

       spooferTransport =  new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 40000);
       return  spooferTransport;
    }
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
                VaultIntegrationAppConfigValues.getSecretPassword(),
        VaultIntegrationAppConfigValues.getSecretSCSUserName(),
        VaultIntegrationAppConfigValues.getSecretSCSPassword());
    }
    public static String getSQLUserName()
    {
        return PVPlatformEnsconceIntegration.getSQLSecretUser();
    }
    public static String getSCSSQLUserName()
    {
        return PVPlatformEnsconceIntegration.getSCSSQLSecretUser();
    }

    public static String getSQLUserDomain()
    {
        return PVPlatformEnsconceIntegration.getSQLSecretDomain();
    }

    public static String getSCSSQLUserPassword()
    {
        return PVPlatformEnsconceIntegration.getSCSSQLSecretPassword();
    }

    //Environment
    public static final String ENVIRONMENT_NAME = AppConfig.resolveStringValue("${environment.name}");

    public static final String AWS_ENVIRONMENT_NAME = AppConfig.resolveStringValue("${aws.environment.name}");

    public static boolean isRequiredDB() {
        String groups = System.getProperty("groups");
        if (StringUtil.isNotBlank(groups)) {
            if (groups.equals("prod")) {
                return false;
            }
        }
        return true;
    }
}
