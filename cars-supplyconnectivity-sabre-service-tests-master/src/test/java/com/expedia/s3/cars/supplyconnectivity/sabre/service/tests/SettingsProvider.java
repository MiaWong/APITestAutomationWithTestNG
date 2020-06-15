package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;

/**
 * A simple class with constants defined for various appconfig settings;
 * The appconfig "ResolveString" can take spring style placeholders (like ${some.setting=defaultValue}) and 'resolve' the values for u...
 */
public class SettingsProvider
{
    public static final String SPOOFER_SERVER = AppConfig.resolveStringValue("${spooferTransport.server.name}");
    public static final int SPOOFER_PORT = Integer.parseInt(AppConfig.resolveStringValue("${spooferTransport.server.port=80}"));
    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName=restservice}");
    public static final boolean SPOOFER_ENABLE = Boolean.parseBoolean(AppConfig.resolveStringValue("${serviceTransport.server.useSpoofer}"));

    // Database configuration
    public static final boolean DB_USE_PASSWORD = Boolean.parseBoolean(AppConfig.resolveStringValue("${databaseConfiguration.usePassword}"));
    public static final String DB_CARS_SABRE_DATABASE_SERVER = AppConfig.resolveStringValue("${databaseConfiguration.carsSabreSCSDatabaseServer}");
    public static final String DB_CARS_SABRE_DATABASE_NAME = AppConfig.resolveStringValue("${databaseConfiguration.carsSabreSCSDatabaseName}");
    public static final String DB_SABRE_USER_NAME = AppConfig.resolveStringValue("${databaseConfiguration.userName}");
    public static final String DB_SABRE_PASSWORD = AppConfig.resolveStringValue("${databaseConfiguration.password}");

    private SettingsProvider()
    {
        //nop
    }
}
