package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.cesrobject;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.SearchContextData;

import java.util.List;

/**
 * Created by jiyu on 1/10/17.
 */
public class CESRSearchRequestTestData extends SearchContextData
{
    //  data fields in search request
    private SiteMessageInfo siteMessageInfo;
    private Long optimizationStrategyCode;
    private CarECommerceSearchStrategy carECommerceSearchStrategy;
    private List<CarECommerceSearchCriteria> carECommerceSearchCriteriaList;
    private Long carProductCatalogID;
    private boolean disableCostListProcessingBoolean;


    //
    //  Sub Java Objects getters
    //
    public SiteMessageInfo getSiteMessageInfo()
    {
        return siteMessageInfo;
    }
    public Long getOptimizationStrategyCode()
    {
        return optimizationStrategyCode;
    }
    public CarECommerceSearchStrategy getCarECommerceSearchStrategy()
    {
        return carECommerceSearchStrategy;
    }
    public List<CarECommerceSearchCriteria> getCarECommerceSearchCriteriaList() { return carECommerceSearchCriteriaList; }
    public Long getCarProductCatalogID() { return carProductCatalogID;}
    public boolean isDisableCostListProcessingBoolean() { return disableCostListProcessingBoolean; }
    //
    //  Sub Java Objects setters
    //
    public void setSiteMessageInfo(SiteMessageInfo siteMessageInfo )
    {
        this.siteMessageInfo = siteMessageInfo;
    }
    public void setOptimizationStrategyCode(Long optimizationStrategyCode) {this.optimizationStrategyCode = optimizationStrategyCode;}
    public void setCarECommerceSearchStrategy(CarECommerceSearchStrategy carECommerceSearchStrategy) { this.carECommerceSearchStrategy = carECommerceSearchStrategy; }
    public void setCarSearchCriteriaList(List<CarECommerceSearchCriteria> carSearchCriteriaList) { this.carECommerceSearchCriteriaList = carECommerceSearchCriteriaList; }
    public void setCarProductCatalogID(Long carProductCatalogID) { this.carProductCatalogID = carProductCatalogID;}
    public void setDisableCostListProcessingBoolean(boolean disableCostListProcessingBoolean) { this.disableCostListProcessingBoolean = disableCostListProcessingBoolean; }

    /*
    public void logjsonTestData()
    {
        final Logger logger = Logger.getLogger(getClass());
        logger.info(getScenarioName());
        logger.info(getSiteMessageInfo().getPointOfSaleKey().getManagementUnitCode());
        logger.info(getSiteMessageInfo().getPointOfSaleKey().getJurisdictionCountryCode());
        logger.info(getSiteMessageInfo().getPointOfSaleKey().getCompanyCode());
        logger.info(getSiteMessageInfo().getLanguage().getLanguageCode());
        logger.info(getSiteMessageInfo().getLanguage().getCountryAlpha2Code());
    }
    */

}
