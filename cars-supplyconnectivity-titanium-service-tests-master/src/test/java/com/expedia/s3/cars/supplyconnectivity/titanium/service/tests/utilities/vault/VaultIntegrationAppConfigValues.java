package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.vault;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;

/**
 * Created by fehu on 2/7/2018.
 */
@SuppressWarnings("PMD")
public class VaultIntegrationAppConfigValues {
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
