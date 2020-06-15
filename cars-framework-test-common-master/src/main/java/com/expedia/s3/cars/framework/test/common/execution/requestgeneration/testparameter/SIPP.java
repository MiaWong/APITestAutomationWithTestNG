package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

/**
 * Created by miawang on 12/19/2016.
 */
public class SIPP {
    private long carCategoryCode;
    private long carTypeCode;
    private long carTransmissionDriveCode;
    private long carFuelACCode;

    public SIPP(long carCategoryCode, long carTypeCode, long carTransmissionDriveCode, long carFuelACCode) {
        this.carCategoryCode = carCategoryCode;
        this.carTypeCode = carTypeCode;
        this.carTransmissionDriveCode = carTransmissionDriveCode;
        this.carFuelACCode = carFuelACCode;
    }

    public long getCarCategoryCode() {
        return carCategoryCode;
    }

    public void setCarCategoryCode(long carCategoryCode) {
        this.carCategoryCode = carCategoryCode;
    }

    public long getCarTypeCode() {
        return carTypeCode;
    }

    public void setCarTypeCode(long carTypeCode) {
        this.carTypeCode = carTypeCode;
    }

    public long getCarTransmissionDriveCode() {
        return carTransmissionDriveCode;
    }

    public void setCarTransmissionDriveCode(long carTransmissionDriveCode) {
        this.carTransmissionDriveCode = carTransmissionDriveCode;
    }

    public long getCarFuelACCode() {
        return carFuelACCode;
    }

    public void setCarFuelACCode(long carFuelACCode) {
        this.carFuelACCode = carFuelACCode;
    }
}
