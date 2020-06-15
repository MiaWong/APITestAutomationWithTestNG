package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.configurationmaster;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.placetypes.defn.v4.CountryType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asharma1 on 10/3/2016.
 */
@SuppressWarnings("PMD")
public class ConfigurationMasterDataSource {
    final private DataSource dataSource;

    public ConfigurationMasterDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<CarSearchCriteriaType> getCurrencyCode(Long travelProductID) throws DataAccessException {
        final String sqlQuery = "Select b.CurrencyCodeDefault as CurrencyCode from TravelProductDomain a " +
                "join Country b on a.CountryCode = b.CountryCode " +
                "where  a.TravelProductID = :travelProductID";
        final ParametrizedQuery<CarSearchCriteriaType> tsql = new ParametrizedQuery<CarSearchCriteriaType>(sqlQuery, dataSource, CarSearchCriteriaType.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("travelProductID", travelProductID);
        return tsql.execute(paramMap);
    }

    public List<CountryType> getCountryCodeFromCountryShortCode(String countryshortcode) throws DataAccessException {
        final String sqlQuery = "SELECT CountryCode as countryAlpha3Code from Country where countryshortcode = :countryshortcode";

        final ParametrizedQuery<CountryType> tsql = new ParametrizedQuery<CountryType>(sqlQuery, dataSource, CountryType.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("countryshortcode", countryshortcode);
        return tsql.execute(paramMap);
    }


    public String getVendorCodeListForCcCar(int tpid) throws DataAccessException {
        final String sqlQuery = "select WebClientConfigValue from webclientconfigproduct "
                                   + "where webclientconfigid in "
                                   +"(select webclientconfigid from webclientconfiguration "
                                   +"where webclientconfigcode = '2cnv') "
                                   + "and travelproductid =:tpid " +"and PartnerID=0";

        final ParametrizedQuery<WebClientConfigProduct> tsql = new ParametrizedQuery<>(sqlQuery, dataSource, WebClientConfigProduct.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tpid", tpid);
        List<WebClientConfigProduct>  webClientConfigProducts = tsql.execute(paramMap);
        if(CollectionUtils.isNotEmpty(webClientConfigProducts))
        {
            return webClientConfigProducts.get(0).getWebClientConfigValue();
        }
        return null;
    }
}