package com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue;

/**
 * Created by fehu on 4/6/2017.
 */
public class ReserveConfigValue {

        final  private  String carSpecialEquipmentCode ;
        final private  String specialEquipmentEnumType;

        public ReserveConfigValue(String carSpecialEquipmentCode, String specialEquipmentEnumType) {
            this.carSpecialEquipmentCode = carSpecialEquipmentCode;
            this.specialEquipmentEnumType = specialEquipmentEnumType;
        }

        public String getCarSpecialEquipmentCode() {
            return carSpecialEquipmentCode;
        }

        public String getSpecialEquipmentEnumType() {
            return specialEquipmentEnumType;
        }

}
