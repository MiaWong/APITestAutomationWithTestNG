package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

/**
 * Created by yyang4 on 11/14/2016.
 */
public class TestDataErrHandle {
    private String errorType;
    private String invalidValue;
    private String expErrorMsg;


    public TestDataErrHandle(String errorType, String invalidValue, String expErrorMsg) {
        this.errorType = errorType;
        this.invalidValue = invalidValue;
        this.expErrorMsg = expErrorMsg;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }

    public String getExpErrorMsg() {
        return expErrorMsg;
    }

    public void setExpErrorMsg(String expErrorMsg) {
        this.expErrorMsg = expErrorMsg;
    }
}

