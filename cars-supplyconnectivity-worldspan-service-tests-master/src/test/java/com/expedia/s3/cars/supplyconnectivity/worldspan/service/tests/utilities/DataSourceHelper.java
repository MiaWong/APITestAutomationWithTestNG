package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities;

import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import org.eclipse.jetty.client.HttpClient;

import javax.sql.DataSource;

/**
 * Created by yy on 9/20/2017.
 */
public class DataSourceHelper {

    private DataSourceHelper() {
    }

    /**
     * Inventory datasource
     */
    public static final   DataSource CARINVENTORYDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    /**
     * CarSCS datasource
     */
    public static final   DataSource CARWSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_WORLDSPANSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_WORLDSPANSCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);



    public  static SpooferTransport getSpooferTransport(HttpClient httpClient)
    {
        return new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
    }


    public  static DataSource getWSCSDataSourse()
    {
        if (null != CARWSCSDATASOURCE)
        {
            return CARWSCSDATASOURCE;
        }

            return DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_WORLDSPANSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_WORLDSPANSCS_DATABASE_NAME,
                    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }

    public static DataSource getCarInventoryDatasource()
    {
        if (null != CARINVENTORYDATASOURCE)
        {
            return CARINVENTORYDATASOURCE;
        }
        return DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }
}
