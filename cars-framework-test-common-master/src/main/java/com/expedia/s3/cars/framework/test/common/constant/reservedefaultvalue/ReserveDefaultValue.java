package com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue;

/**
 * Created by fehu on 4/6/2017.
 */
public enum ReserveDefaultValue {
   CARSPECIALEQUIPMENTLIST_ONEUNAVAILABLE (new ReserveConfigValue("ComputerizedDirections",null)),
   CARSPECIALEQUIPMENTLIST_MULTISPECIALEQUIPMENT(new ReserveConfigValue("InfantChildSeat,NavigationalSystem", null)),
   MULTISPECIALEQUIPMENT (new ReserveConfigValue("MobilePhone,LeftHandControl,ComputerizedDirections","InfantChildSeat,NavigationalSystem")),
    CARSPECIALEQUIPMENT_HANDCONTROL (new ReserveConfigValue("LeftHandControl,RightHandControl",null)),
    CARSPECIALEQUIPMENT_SNOWCHAINS_SKIRACK (new ReserveConfigValue("SnowChains,SkiRack",null)),
    CARSPECIALEQUIPMENT_UNAVAILABLE (new ReserveConfigValue("stR8GarBaGeValuE,tHaTmEan$N0ThinG", null)),
    VAILDSPECIALEQUIPMENTLISTANDEMPTYVEHICLEOPTIONLIST(new ReserveConfigValue("InfantChildSeat,NavigationalSystem", "")),
    CARSPECIALEQUIPMENTLIST_MASERATIDOMIANVALUE_CSI(new ReserveConfigValue("InfantChildSeat", null)),
    CarSpecialEquipmentList_MaseratiDomianValue_CSI_NVS(new ReserveConfigValue("InfantChildSeat", "NavigationalSystem"));
    private ReserveConfigValue reserveConfigValue;

    ReserveDefaultValue(ReserveConfigValue reserveConfigValue) {
        this.reserveConfigValue = reserveConfigValue;
    }

    public ReserveConfigValue getReserveConfigValue() {
        return reserveConfigValue;
    }
}
