package com.expedia.s3.cars.supply.service.utils;

import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by v-mechen on 1/16/2017.
 */
public final class ExecutionHelper
{
    private ExecutionHelper() {}

    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException
    {
        final String randomGuid = UUID.randomUUID().toString();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        return randomGuid;
    }

    public static DataSource getCarsInventoryDatasource()
    {
        return DatabaseSetting.createDataSource( SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
                SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN,
                SettingsProvider.DB_USER_NAME,
                SettingsProvider.DB_PASSWORD,
                SettingsProvider.DB_USE_PASSWORD);

    }


}
