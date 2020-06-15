package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 2/20/2017.
 */
public class CarClientConfigDataSource {

    Logger logger = Logger.getLogger(getClass());
    final private DataSource dataSource;

    public CarClientConfigDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public List<ClientConfig> getClientConfigValue(ClientConfig clientConfig) throws DataAccessException {

        final Map<String, Object> paramMap = new HashMap<>(1);
        final StringBuffer sqlStr = new StringBuffer();
        if (StringUtil.isNotBlank(clientConfig.getEnvironmentName())) {
            paramMap.put("environmentName", clientConfig.getEnvironmentName());
            sqlStr.append(" and EnvironmentName = :environmentName");
        }

        if (StringUtil.isNotBlank(clientConfig.getSettingName())) {
            paramMap.put("settingName", clientConfig.getSettingName());
            sqlStr.append(" and SettingName = :settingName");
        }

        if (StringUtil.isNotBlank(clientConfig.getSettingValue())) {
            paramMap.put("settingValue", clientConfig.getSettingValue());
            sqlStr.append(" and SettingValue = :settingValue");
        }
        if (null != clientConfig.getClientId()) {
            paramMap.put("clientId", clientConfig.getClientId());
            sqlStr.append(" and ClientId = :clientId");
        }

        final ParametrizedQuery<ClientConfig> tsql = new ParametrizedQuery<>("SELECT top 1 ClientConfigurationID, EnvironmentName, ClientID, SettingName, SettingValue, CreateDate,CreatedBy,UpdateDate,LastUpdatedBy \n" +
                "FROM ClientConfiguration where 1 = 1 " + sqlStr, dataSource, ClientConfig.class);

        logger.info("PosConfig querySql: SELECT top 1 ClientConfigurationID, EnvironmentName, ClientID, SettingName, SettingValue, CreateDate,CreatedBy,UpdateDate,LastUpdatedBy \n" +
                "FROM ClientConfiguration where 1 = 1 " + sqlStr);

        return tsql.execute(paramMap);
    }
}
