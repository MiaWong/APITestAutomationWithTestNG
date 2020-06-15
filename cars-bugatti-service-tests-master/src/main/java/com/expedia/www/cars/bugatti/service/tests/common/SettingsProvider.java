package com.expedia.www.cars.bugatti.service.tests.common;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;

/**
 * Created by miawang on 3/21/2017.
 */
public class SettingsProvider
{
    public static final String SERVICE_ADDRESS = AppConfig.resolveStringValue("${serviceTransport.server.address}");
    public static final String SERVICE_E3DESTINATION = AppConfig.resolveStringValue("${serviceTransport.server.e3destinationName=search}");
}