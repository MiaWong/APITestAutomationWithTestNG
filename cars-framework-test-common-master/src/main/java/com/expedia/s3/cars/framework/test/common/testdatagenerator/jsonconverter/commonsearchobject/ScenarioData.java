package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;

/**
 * Created by jiyu on 1/12/17.
 */
public class ScenarioData
{
    /*
    public class TestScenario
    {
        private final String scenarionName;             //  defined in SearchRequestTestData***
        private final String jurisdictionCountryCode;   //  defined in PointOfSaleKey
        private final String companyCode;               //  defined in PointOfSaleKey
        private final String managementUnitCode;        //  defined in PointOfSaleKey
        private final String supplierCurrencyCode;      //  defined in CarSearchCriteria
        private final String pickupLocationCode;        //  defined in CarTransportationSegment
        private final String dropOffLocationCode;       //  defined in CarTransportationSegment
        private final boolean onAirPort;                //  defined in SearchRequestTestData***
        private final PurchaseType purchaseType;        //  defined in SearchRequestTestData***
        private final int businessModel;                //  defined in SearchRequestTestData***
        private final int serviceProviderID;            //  defined in SearchRequestTestData***
    }
*/

    //
    //  Sub Java Objects :
    //
    private String scenarioName;
    private boolean isOnAirport;
    private PurchaseType purchaseType;
    private BusinessModel businessModel;
    private int serviceID;

    //  getters
    public String getScenarioName()
    {
        return scenarioName;
    }
    public boolean isSetOnAirport()
    {
        return isOnAirport;
    }
    public PurchaseType getPurchaseType()
    {
        return purchaseType;
    }
    public BusinessModel getBusinessModel()
    {
        return businessModel;
    }
    public int getServiceID() { return serviceID; }

    //  setters
    public void setScenarioName(String scenarioName)
    {
        this.scenarioName = scenarioName;
    }
    public void setIsOnAirport(boolean isOnAirport)
    {
        this.isOnAirport = isOnAirport;
    }
    public void setPurchaseType(PurchaseType purchaseType)
    {
        this.purchaseType = purchaseType;
    }
    public void setBusinessModel(BusinessModel businessModel)
    {
        this.businessModel = businessModel;
    }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }

}
