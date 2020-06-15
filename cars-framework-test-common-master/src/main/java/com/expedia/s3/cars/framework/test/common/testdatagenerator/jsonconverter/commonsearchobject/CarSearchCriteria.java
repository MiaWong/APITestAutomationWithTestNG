package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

import java.util.List;

/**
 * Created by jiyu on 1/12/17.
 */
public class CarSearchCriteria
{
    private int sequence;
    private CarTransportationSegment carTransportSegment;
    private List<CarVehicle> carVehicleList;        //  Attention : for CarBS search, no carVehicleList

    private String currencyCode;
    private CarRate carRate;
    private boolean smokingBoolean;
    private boolean prePaidFuelBoolean;
    private boolean unlimitedMileageBoolean;
    private boolean packageBoolean;

    //  getters
    public int getSequence() { return sequence; }
    public CarTransportationSegment getCarTransportSegment() { return carTransportSegment; }
    public List<CarVehicle> getCarVehicleList() { return carVehicleList; }

    public String getCurrencyCode() { return currencyCode; }
    public CarRate getCarRate() { return carRate; }
    public boolean isSmokingBoolean() { return smokingBoolean; }
    public boolean isPrePaidFuelBoolean() { return prePaidFuelBoolean; }
    public boolean isUnlimitedMileageBoolean() { return unlimitedMileageBoolean; }
    public boolean isPackageBoolean() { return packageBoolean; }

    //  setters
    public void setSequence(int sequence) { this.sequence = sequence; }
    public void setCarTransportSegment(CarTransportationSegment carTransportSegment) { this.carTransportSegment = carTransportSegment; }
    public void setCarVehicleList(List<CarVehicle> carVehicleList) { this.carVehicleList = carVehicleList; }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public void setCarRate(CarRate carRate) { this.carRate = carRate; }
    public void setSmokingBoolean(boolean smokingBoolean) { this.smokingBoolean = smokingBoolean; }
    public void setPrePaidFuelBoolean(boolean prePaidFuelBoolean) { this.prePaidFuelBoolean = prePaidFuelBoolean; }
    public void setUnlimitedMileageBoolean(boolean unlimitedMileageBoolean) { this.unlimitedMileageBoolean = unlimitedMileageBoolean; }
    public void setPackageBoolean(boolean packageBoolean) { this.packageBoolean = packageBoolean; }

}
