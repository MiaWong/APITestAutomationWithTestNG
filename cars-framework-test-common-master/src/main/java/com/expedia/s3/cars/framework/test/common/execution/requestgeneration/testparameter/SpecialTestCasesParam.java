package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

/**
 * Created by miawang on 12/19/2016.
 */
public class SpecialTestCasesParam {
    private long driverAge;

    //validate CASSS-4544 CarVehicleOption in COPP is getting back all the special equipment instead of just the selected one
    private boolean optionListSenerio;
    //build specail off airport request
    private boolean specialOffAirPort;

    public long getDriverAge() {
        return driverAge;
    }

    public void setDriverAge(long driverAge) {
        this.driverAge = driverAge;
    }

    public boolean isOptionListSenerio() {
        return optionListSenerio;
    }

    public void setOptionListSenerio(boolean optionListSenerio) {
        this.optionListSenerio = optionListSenerio;
    }

    public boolean isSpecialOffAirPort() {
        return specialOffAirPort;
    }

    public void setSpecialOffAirPort(boolean specialOffAirPort) {
        this.specialOffAirPort = specialOffAirPort;
    }
}
