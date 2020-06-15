package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class CarTransportationSegment //implements Serializable
{
    //@SerializedName("StartCarLocationCodeKey")
    CarLocationKey startCarLocationCodeKey;
    //@SerializedName("EndCarLocationCodeKey")
    CarLocationKey endCarLocationCodeKey;
    //@SerializedName("StartDateTime")
    String startDateTime;
    //@SerializedName("EndDateTime")
    String endDateTime;

    //  getters
    public CarLocationKey getStartCarLocationCodeKey() { return this.startCarLocationCodeKey; }
    public CarLocationKey getEndCarLocationCodeKey() { return this.endCarLocationCodeKey; }
    public String getStartDateTime() { return this.startDateTime; }
    public String getEndDateTime() { return this.endDateTime; }


    //  setters
    public void setStartCarLocationCodeKey(CarLocationKey startCarLocationCodeKey) { this.startCarLocationCodeKey = startCarLocationCodeKey; }
    public void setEndCarLocationCodeKey(CarLocationKey endCarLocationCodeKey) { this.endCarLocationCodeKey = endCarLocationCodeKey; }
    public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }

}
