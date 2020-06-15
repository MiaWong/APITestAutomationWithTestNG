package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs;

/**
 * Created by yyang4 on 11/17/2016.
 */
public class Client {
    private Long clientId;
    private String clientName;
    private String clientCode;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
}
