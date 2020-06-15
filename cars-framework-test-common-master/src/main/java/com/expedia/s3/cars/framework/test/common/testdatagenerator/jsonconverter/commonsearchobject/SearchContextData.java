package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

import org.apache.log4j.Logger;

/**
 * Created by jiyu on 1/12/17.
 */
public class SearchContextData extends ScenarioData
{
    private AuditLogTrackingData auditLogTrackingData;
    private PointOfSaleKey pointOfSaleKey;
    private Language language;
    private CarSearchStrategy carSearchStrategy;

    //  getters
    public AuditLogTrackingData getAuditLogTrackingData()
    {
        return auditLogTrackingData;
    }
    public PointOfSaleKey getPointOfSaleKey()
    {
        return pointOfSaleKey;
    }
    public Language getLanguage()
    {
        return language;
    }
    public CarSearchStrategy getCarSearchStrategy()
    {
        return carSearchStrategy;
    }

    //  setters
    public void setAuditLogTrackingData(AuditLogTrackingData auditLogTrackingData) { this.auditLogTrackingData = auditLogTrackingData; }
    public void setPointOfSaleKey(PointOfSaleKey pointOfSaleKey )
    {
        this.pointOfSaleKey = pointOfSaleKey;
    }
    public void setLanguage(Language language)
    {
        this.language = language;
    }
    public void setCarSearchStrategy(CarSearchStrategy carSearchStrategy) { this.carSearchStrategy = carSearchStrategy; }


    public void logjsonTestData()
    {
        final Logger logger = Logger.getLogger(getClass());
        logger.info(getScenarioName());
        logger.info(getPointOfSaleKey().getCompanyCode());
        logger.info(getPointOfSaleKey().getJurisdictionCountryCode());
        logger.info(getPointOfSaleKey().getManagementUnitCode());
        logger.info(getLanguage().getLanguageCode());
        logger.info(getLanguage().getCountryAlpha2Code());
        //  TODO : more log
    }


}
