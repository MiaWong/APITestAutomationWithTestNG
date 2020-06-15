package com.expedia.s3.cars.framework.test.common.execution.scenarios;

/**
 * Created by yyang4 on 10/10/2016.
 */
public enum BusinessModel {
    Agency(1),
    Merchant(2),
    GDSP(3);

    private int value;

    BusinessModel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
