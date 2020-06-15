package com.expedia.s3.cars.framework.test.common.passwordvault;

/**
 * Created by jiyu on 11/23/16.
 */
@SuppressWarnings("PMD")
public class PVPlatformEnsconceIntegrationValues
{
    private static final String HOST = "https://ewe-vault.test.expedia.com:8200";
    private static final String APP_ID = "de11c19f-07ab-4a06-9bbf-7852fc672e1a";
    private static final String USER_ID = "749c148a-322b-4fc7-9b23-629fccde7f1a";
    private static final String SECRET_APP = "secret/all-cars-service-automation-tests";
    //  SQL service account should not be used : {db_userDomain, db_userName, db_password}
    //  SQL full-admin user account doess not require for domain
    private static final String SECRET_USERDOMAIN = null;
    private static final String SECRET_USERNAME = "testCommon.dbUser";
    private static final String SECRET_USERPASSWORD = "testCommon.dbPassword";
    private static final String CERT_RESOURCE = "combined-ca.pem";

    private PVPlatformEnsconceIntegrationValues() { }

    public static String getHost() { return HOST; }

    public static String getApplicationID() { return APP_ID;}

    public static String getUserID() { return USER_ID; }

    public static String getSecretApp() { return SECRET_APP; }

    public static String getSecretUserDomain() { return SECRET_USERDOMAIN; }

    public static String getSecretUserName() { return SECRET_USERNAME;}

    public static String getSecretUserPassword() { return SECRET_USERPASSWORD;}

    public static String getCertResource() { return CERT_RESOURCE; }
}

