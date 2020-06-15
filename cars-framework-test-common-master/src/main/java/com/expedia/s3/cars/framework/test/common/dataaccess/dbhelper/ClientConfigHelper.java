package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.CarClientConfigDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.ClientConfig;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by fehu on 2/20/2017.
 */
public class ClientConfigHelper
{

    final private DataSource dataSource;

    public ClientConfigHelper(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public boolean checkClientConfig(String env, String settingName, int clientId, String expectValue) throws DataAccessException
    {
        final CarClientConfigDataSource carClientConfigDataSource = new CarClientConfigDataSource(dataSource);
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setEnvironmentName(env);
        clientConfig.setSettingValue(expectValue);
        clientConfig.setSettingName(settingName);
        clientConfig.setClientId(clientId);
        final List<ClientConfig> clientConfigs = carClientConfigDataSource.getClientConfigValue(clientConfig);
        return CollectionUtils.isNotEmpty(clientConfigs);

    }

    public Integer getValueFromCarBSConfig(String env, String settingName, Integer clientId) throws DataAccessException
    {
        final CarClientConfigDataSource carClientConfigDataSource = new CarClientConfigDataSource(dataSource);
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setEnvironmentName(env);
        clientConfig.setSettingName(settingName);
        clientConfig.setClientId(clientId);
        final List<ClientConfig> clientConfigs = carClientConfigDataSource.getClientConfigValue(clientConfig);
        return Integer.valueOf(clientConfigs.get(0).getSettingValue());
    }
}
