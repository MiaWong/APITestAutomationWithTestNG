package com.expedia.s3.cars.ecommerce.carbs.service.database.util;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.eclipse.jetty.client.HttpClient;

import javax.sql.DataSource;

/**
 * Created by fehu on 11/15/2016.
 */
public class DatasourceHelper {

    private DatasourceHelper() {
    }

    public static final   DataSource CARBSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource TRAVELSERVERDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource CONFIGURATIONMASTERDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CONFIGURATIONMASTER_DATABASE_SERVER, SettingsProvider.DB_CONFIGURATIONMASTER_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource CARINVENTORYDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource CARBOOKINGDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BOOKING_DATABASE_SERVER, SettingsProvider.DB_CARS_BOOKING_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource WORLDSPANSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_WORLDSPANSCS_DATABASE_SERVER, SettingsProvider.DB_WORLDSPANSCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource AMADEUSSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_AMADEUSSCS_DATABASE_SERVER, SettingsProvider.DB_AMADEUSSCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);


    public static final   DataSource TITANIUMSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER,
            SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME,
            SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public static final   DataSource MICRONNEXUSSCSDATASOURCE = DatabaseSetting.createDataSource(SettingsProvider.DB_MICRONNEXUS_DATABASE_SERVER,
            SettingsProvider.DB_MICRONNEXUS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);


    public static DataSource getSCSDataSource(TestData testData)
    {
        if (CommonEnumManager.ServieProvider.Amadeus.getServiceProvider() == testData.getScenarios().getServiceProviderID())
        {
            return AMADEUSSCSDATASOURCE;
        }
        if (CommonEnumManager.ServieProvider.worldSpanSCS.getServiceProvider() == testData.getScenarios().getServiceProviderID())
        {
            return WORLDSPANSCSDATASOURCE;
        }
        if (CommonEnumManager.ServieProvider.MNSCS.getServiceProvider() == testData.getScenarios().getServiceProviderID())
        {
            return MICRONNEXUSSCSDATASOURCE;
        }
        if (CommonEnumManager.ServieProvider.TitaniumSCS.getServiceProvider() == testData.getScenarios().getServiceProviderID())
        {
            return TITANIUMSCSDATASOURCE;
        }

        return null;
    }

    public  static SpooferTransport getSpooferTransport(HttpClient httpClient)
    {
        return new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 40000);
    }

    public static DataSource getCarsBookingDatasource()
    {
        if (null != CARBOOKINGDATASOURCE)
        {
            return CARBOOKINGDATASOURCE;
        }
        return  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BOOKING_DATABASE_SERVER, SettingsProvider.DB_CARS_BOOKING_DATABASE_NAME,
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
    public static DataSource getCarBSDatasource()
    {
        if (null != CARBSDATASOURCE)
        {
            return CARBSDATASOURCE;
        }
        return DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }

    public static DataSource getWorldspanSCSDatasource()
    {
        if (null != WORLDSPANSCSDATASOURCE)
        {
            return WORLDSPANSCSDATASOURCE;
        }
        return DatabaseSetting.createDataSource(SettingsProvider.DB_WORLDSPANSCS_DATABASE_SERVER, SettingsProvider.DB_WORLDSPANSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }

    public static DataSource getAmadeusSCSDatasource()
    {
        if (null != AMADEUSSCSDATASOURCE)
        {
            return AMADEUSSCSDATASOURCE;
        }
        return DatabaseSetting.createDataSource(SettingsProvider.DB_AMADEUSSCS_DATABASE_SERVER, SettingsProvider.DB_AMADEUSSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }

    public static DataSource getTravelServerDatasource()
    {
        if (null != TRAVELSERVERDATASOURCE)
        {
            return TRAVELSERVERDATASOURCE;
        }
        return DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }

    public static DataSource getConfigurationMasterDatasource()
    {
        if (null != CONFIGURATIONMASTERDATASOURCE)
        {
            return CONFIGURATIONMASTERDATASOURCE;
        }
        return DatabaseSetting.createDataSource(SettingsProvider.DB_CONFIGURATIONMASTER_DATABASE_SERVER, SettingsProvider.DB_CONFIGURATIONMASTER_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    }

    public static DataSource getTitaniumDatasource(){

        if (null != TITANIUMSCSDATASOURCE)
        {
            return TITANIUMSCSDATASOURCE;
        }
        return  DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER,
                SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
                SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }

    public static DataSource getMicronNexusDatasource(){
        return MICRONNEXUSSCSDATASOURCE;
    }
}