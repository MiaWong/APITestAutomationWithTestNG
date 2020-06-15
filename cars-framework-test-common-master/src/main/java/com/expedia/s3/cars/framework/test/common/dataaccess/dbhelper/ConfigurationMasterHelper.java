package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.placetypes.defn.v4.CountryType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.configurationmaster.ConfigurationMasterDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yyang4 on 11/15/2016.
 */
public class ConfigurationMasterHelper {
    final private DataSource dataSource;

    public ConfigurationMasterHelper(DataSource datasource)
    {
        this.dataSource = datasource;
    }

    public String getCurrencyCode(Long travelProductID) throws DataAccessException {
        final ConfigurationMasterDataSource configurationMasterDataSource = new ConfigurationMasterDataSource(dataSource);
        final List<CarSearchCriteriaType> currencyCodeList = configurationMasterDataSource.getCurrencyCode(travelProductID);
        return CollectionUtils.isEmpty(currencyCodeList) ? "" : currencyCodeList.get(0).getCurrencyCode();
    }

    public String getCountryCodeFromCountryShortCode(String countryshortcode) throws DataAccessException {
        final ConfigurationMasterDataSource configurationMasterDataSource = new ConfigurationMasterDataSource(dataSource);
        final List<CountryType> countryList = configurationMasterDataSource.getCountryCodeFromCountryShortCode(countryshortcode);
        return CollectionUtils.isEmpty(countryList) ? "" : countryList.get(0).getCountryAlpha3Code();
    }
}
