package com.expedia.s3.cars.framework.test.common.constant.errorhandling;

/**
 * Created by fehu on 2/12/2017.
 */
public class ErrorHandling {

        private String invalidValue;
        private String errorType;
        private String errormessage;
        private String xPath;

    public String getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public String getxPath() {
        return xPath;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public ErrorHandling(String invalidValue, String errorType, String errormessage, String xPath) {
            this.invalidValue = invalidValue;
            this.errorType = errorType;
            this.errormessage = errormessage;
            this.xPath = xPath;

    }
}
