package com.expedia.s3.cars.framework.test.common.execution.verification;


/**
 * Created by v-mechen on 9/29/2016.
 */
public class ExpectedErrorContext {
    private final String expErrorType;
    private final String expErrorMessage;


    public ExpectedErrorContext(String expErrorType, String expErrorMessage) {
        this.expErrorType = expErrorType;
        this.expErrorMessage = expErrorMessage;
    }

    public String getExpErrorType()
    {
        return expErrorType;
    }

    public String getExpErrorMessage()
    {
        return expErrorMessage;
    }
}
