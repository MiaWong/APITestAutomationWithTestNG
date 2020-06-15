package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by miawang on 9/11/2018.
 */
public class CarPromotion
{
    private int carPromotionID;

    private String carPromotionName;
    private String carPromotionDescription;
    private String carPromotionType;

    private String searchStartDate;
    private String searchEndDate;

    private String travelStartDate;
    private String travelEndDate;

    private String carOfferType;

    public int getCarPromotionID()
    {
        return carPromotionID;
    }

    public void setCarPromotionID(int carPromotionID)
    {
        this.carPromotionID = carPromotionID;
    }

    public String getCarPromotionName()
    {
        return carPromotionName;
    }

    public void setCarPromotionName(String carPromotionName)
    {
        this.carPromotionName = carPromotionName;
    }

    public String getCarPromotionDescription()
    {
        return carPromotionDescription;
    }

    public void setCarPromotionDescription(String carPromotionDescription)
    {
        this.carPromotionDescription = carPromotionDescription;
    }

    public String getCarPromotionType()
    {
        return carPromotionType;
    }

    public void setCarPromotionType(String carPromotionType)
    {
        this.carPromotionType = carPromotionType;
    }

    public String getSearchStartDate()
    {
        return searchStartDate;
    }

    public void setSearchStartDate(String searchStartDate)
    {
        this.searchStartDate = searchStartDate;
    }

    public String getSearchEndDate()
    {
        return searchEndDate;
    }

    public void setSearchEndDate(String searchEndDate)
    {
        this.searchEndDate = searchEndDate;
    }

    public String getTravelStartDate()
    {
        return travelStartDate;
    }

    public void setTravelStartDate(String travelStartDate)
    {
        this.travelStartDate = travelStartDate;
    }

    public String getTravelEndDate()
    {
        return travelEndDate;
    }

    public void setTravelEndDate(String travelEndDate)
    {
        this.travelEndDate = travelEndDate;
    }

    public String getCarOfferType()
    {
        return carOfferType;
    }

    public void setCarOfferType(String carOfferType)
    {
        this.carOfferType = carOfferType;
    }
}
