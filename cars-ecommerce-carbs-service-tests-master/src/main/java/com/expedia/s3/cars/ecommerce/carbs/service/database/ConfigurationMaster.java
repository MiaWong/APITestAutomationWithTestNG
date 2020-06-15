package com.expedia.s3.cars.ecommerce.carbs.service.database;

import com.expedia.s3.cars.ecommerce.carbs.service.database.pojo.LanguageWin32;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 11/15/2016.
 */
public class ConfigurationMaster {
    final Logger logger = Logger.getLogger(getClass());
    final private DataSource dataSource;

    public ConfigurationMaster(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public String getLanguageCode(String countryCode) throws DataAccessException {

        final Map<String, Object> paramMap = new HashMap<>();
        final ParametrizedQuery<LanguageWin32> tsql = new ParametrizedQuery<>("select * from LanguageWin32 where CountryCode =:countryCode", dataSource, LanguageWin32.class);
        paramMap.put("countryCode", countryCode);
        final List<LanguageWin32> localeCodes = tsql.execute(paramMap);
        return String.valueOf(CollectionUtils.isEmpty(localeCodes) ? "" : localeCodes.get(0).getLocaleCode());

    }
}
