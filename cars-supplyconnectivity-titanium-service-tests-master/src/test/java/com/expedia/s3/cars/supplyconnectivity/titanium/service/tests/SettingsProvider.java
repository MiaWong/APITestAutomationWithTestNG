package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.vault.PVPlatformEnsconceIntegration;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.vault.VaultIntegrationAppConfigValues;
import org.eclipse.jetty.util.StringUtil;



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

    public static boolean isRequiredDB() {
        String groups = System.getProperty("groups");
        if (StringUtil.isNotBlank(groups)) {
            if (groups.equals("prod") || groups.equals("int") || groups.equals(TestGroup.BVT) || groups.equals(TestGroup.SHOPPING_BVT)) {
                return false;
            }
        }
        return true;
    }

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
    public static boolean isTestDataGeneratorEnable()
    {
        return TESTCONFIG_TESTDATA_CONVERTER_ENABLE;
    }

    public static boolean setTestDataGeneratorEnable()
    {
        try {
            return Boolean.parseBoolean(AppConfig.resolveStringValue("${testconfig.testdataConverterEnable}"));
        }
        catch (Exception ex){
            return false;
        }
    }
    //  TestConfig data : test data generator mode
    public static String getTestDataGeneratorMode()
    {
        return (isTestDataGeneratorEnable()) ? AppConfig.resolveStringValue("${testconfig.testdataConverterMode}") : null;

    }


    //  test config for control the flow against DBLess & test data generator
    public static boolean TESTCONFIG_TESTDATA_CONVERTER_ENABLE = setTestDataGeneratorEnable();
    public static String TESTCONFIG_TESTDATA_CONVERTER_MODE = getTestDataGeneratorMode();


    //  fields
    public static final String APPTEST_FARM = AppConfig.getEnvironment();


    public static final String SPOOFER_SERVER = AppConfig.resolveStringValue("${spooferTransport.server.name}");
    public static final int SPOOFER_PORT = Integer.parseInt(AppConfig.resolveStringValue("${spooferTransport.server.port=80}"));

    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName=restservice}");

    // Database configuration
    public static final boolean DB_USE_PASSWORD = Boolean.parseBoolean(AppConfig.resolveStringValue("${databaseConfiguration.usePassword}"));

    //  DB logic
    public static String DB_PASSWORD = isRequiredDB() ? getSQLUserPassword() : null;
    public static String DB_USER_DOMAIN = isRequiredDB() ? getSQLUserDomain() : null;
    public static String DB_USER_NAME = isRequiredDB() ? getSQLUserName() : null;

    public static final String DB_CAR_Titanium_SCS_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.titaniumSCSDatabaseServer}");
    public static final String DB_CAR_Titanium_SCS_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.titaniumSCSDatabaseName}");
}