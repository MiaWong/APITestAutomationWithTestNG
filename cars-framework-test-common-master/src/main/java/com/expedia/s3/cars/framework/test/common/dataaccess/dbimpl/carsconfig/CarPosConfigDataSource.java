package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 10/25/2016.
 */
public class CarPosConfigDataSource {
    Logger logger = Logger.getLogger(getClass());
    final private DataSource dataSource;

    public CarPosConfigDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<PosConfig> getPosConfigValue(PosConfig posConfig) throws DataAccessException {

        final Map<String, Object> paramMap = new HashMap<>(1);
        final StringBuffer sqlStr = new StringBuffer();
        if (StringUtils.isEmpty(posConfig.getEnvironmentName())) {
            sqlStr.append(" and EnvironmentName is null");
        }
        else
        {
            paramMap.put("environmentName", posConfig.getEnvironmentName());
            sqlStr.append(" and EnvironmentName = :environmentName");
        }

        if (StringUtils.isEmpty(posConfig.getJurisdictionCode())) {
            sqlStr.append(" and JurisdictionCode is null");

        }
        else
        {
            paramMap.put("jurisdictionCode", posConfig.getJurisdictionCode());
            sqlStr.append(" and JurisdictionCode = :jurisdictionCode");
        }

        if (StringUtils.isEmpty(posConfig.getCompanyCode())) {
            sqlStr.append(" and CompanyCode is null");
        }
        else
        {
            paramMap.put("companyCode", posConfig.getCompanyCode());
            sqlStr.append(" and CompanyCode = :companyCode");

        }

        if (StringUtils.isEmpty(posConfig.getManagementUnitCode())) {
            sqlStr.append(" and ManagementUnitCode is null");
        }
        else
        {
            paramMap.put("managementUnitCode", posConfig.getManagementUnitCode());
            sqlStr.append(" and ManagementUnitCode = :managementUnitCode");

        }

        if (!StringUtils.isEmpty(posConfig.getSettingName())) {
            paramMap.put("settingName", posConfig.getSettingName());
            sqlStr.append(" and SettingName = :settingName");
        }

        if (!StringUtils.isEmpty(posConfig.getSettingValue())) {
            paramMap.put("settingValue", posConfig.getSettingValue());
            sqlStr.append(" and SettingValue = :settingValue");
        }


        final ParametrizedQuery<PosConfig> tsql = new ParametrizedQuery<PosConfig>("SELECT top 1 PoSConfigurationID,EnvironmentName,JurisdictionCode,CompanyCode,ManagementUnitCode,SettingName,SettingValue,CreateDate,CreatedBy,UpdateDate,LastUpdatedBy \n" +
                "FROM PoSConfiguration where 1 = 1 " + sqlStr, dataSource, PosConfig.class);

        logger.info("PosConfig querySql: SELECT top 1 PoSConfigurationID,EnvironmentName,JurisdictionCode,CompanyCode,ManagementUnitCode,SettingName,SettingValue,CreateDate,CreatedBy,UpdateDate,LastUpdatedBy \n" +
                "FROM PoSConfiguration where 1 = 1 " + sqlStr);
        return tsql.execute(paramMap);
    }
}
