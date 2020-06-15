package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class CarVehicle //implements Serializable
{
    //@SerializedName("CarCategoryCode")
    int carCategoryCode;
    //@SerializedName("CarTypeCode")
    int carTypeCode;
    //@SerializedName("CarTransmissionDriveCode")
    int carTransmissionDriveCode;
    //@SerializedName("CarFuelACCode")
    int carFuelACCode;

    //  getters
    public int getCarCategoryCode() { return this.carCategoryCode; }
    public int getCarTypeCode() { return this.carTypeCode; }
    public int getCarTransmissionDriveCode() { return this.carTransmissionDriveCode; }
    public int getCarFuelACCode() { return this.carFuelACCode; }


    //  setters
    public void setCarCategoryCode(int carCategoryCode) { this.carCategoryCode = carCategoryCode; }
    public void setCarTypeCode(int carTypeCode) { this.carTypeCode = carTypeCode; }
    public void setCarTransmissionDriveCode(int carTransmissionDriveCode) { this.carTransmissionDriveCode = carTransmissionDriveCode; }
    public void setCarFuelACCode(int carFuelACCode) { this.carFuelACCode = carFuelACCode; }


}