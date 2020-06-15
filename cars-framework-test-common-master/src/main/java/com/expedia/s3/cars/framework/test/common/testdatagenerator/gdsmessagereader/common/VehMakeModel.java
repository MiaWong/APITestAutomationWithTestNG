package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common;

/**
 * Created by miawang on 12/5/2016.
 */
public class VehMakeModel {
    //SIPP CarBS
    private String carBSCarType;
    private String carBSCarTransmissionDrive;
    private String carBSCarFuelAirCondition;
    private String carBSCarCategory;

    //SIPP External
    private String externalCarType;
    private String externalCarTransmissionDrive;
    private String externalCarFuelAirCondition;
    private String externalCarCategory;

    public VehMakeModel(String carCategory, String carType, String carTransmissionDrive, String carFuelAirCondition, boolean isExternal) {
        if (isExternal) {
            this.externalCarCategory = carCategory;
            this.externalCarType = carType;
            this.externalCarTransmissionDrive = carTransmissionDrive;
            this.externalCarFuelAirCondition = carFuelAirCondition;
        } else {
            this.carBSCarCategory = carCategory;
            this.carBSCarType = carType;
            this.carBSCarTransmissionDrive = carTransmissionDrive;
            this.carBSCarFuelAirCondition = carFuelAirCondition;
        }
    }

    public String getCarBSCarType() {
        return carBSCarType;
    }

    public void setCarBSCarType(String carBSCarType) {
        this.carBSCarType = carBSCarType;
    }

    public String getCarBSCarTransmissionDrive() {
        return carBSCarTransmissionDrive;
    }

    public void setCarBSCarTransmissionDrive(String carBSCarTransmissionDrive) {
        this.carBSCarTransmissionDrive = carBSCarTransmissionDrive;
    }

    public String getCarBSCarFuelAirCondition() {
        return carBSCarFuelAirCondition;
    }

    public void setCarBSCarFuelAirCondition(String carBSCarFuelAirCondition) {
        this.carBSCarFuelAirCondition = carBSCarFuelAirCondition;
    }

    public String getCarBSCarCategory() {
        return carBSCarCategory;
    }

    public void setCarBSCarCategory(String carBSCarCategory) {
        this.carBSCarCategory = carBSCarCategory;
    }

    public String getExternalCarType() {
        return externalCarType;
    }

    public void setExternalCarType(String externalCarType) {
        this.externalCarType = externalCarType;
    }

    public String getExternalCarTransmissionDrive() {
        return externalCarTransmissionDrive;
    }

    public void setExternalCarTransmissionDrive(String externalCarTransmissionDrive) {
        this.externalCarTransmissionDrive = externalCarTransmissionDrive;
    }

    public String getExternalCarFuelAirCondition() {
        return externalCarFuelAirCondition;
    }

    public void setExternalCarFuelAirCondition(String externalCarFuelAirCondition) {
        this.externalCarFuelAirCondition = externalCarFuelAirCondition;
    }

    public String getExternalCarCategory() {
        return externalCarCategory;
    }

    public void setExternalCarCategory(String externalCarCategory) {
        this.externalCarCategory = externalCarCategory;
    }
}
