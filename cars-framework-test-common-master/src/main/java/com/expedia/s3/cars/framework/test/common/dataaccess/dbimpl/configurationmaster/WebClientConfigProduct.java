package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.configurationmaster;

/**
 * Created by fehu on 1/10/2017.
 */
public class WebClientConfigProduct {

    private int tpid;
    private int partnerId;
    private int webClientConfigId;
    private  String webClientConfigValue;

    public int getTpid() {
        return tpid;
    }

    public void setTpid(int tpid) {
        this.tpid = tpid;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public int getWebClientConfigId() {
        return webClientConfigId;
    }

    public void setWebClientConfigId(int webClientConfigId) {
        this.webClientConfigId = webClientConfigId;
    }

    public String getWebClientConfigValue() {
        return webClientConfigValue;
    }

    public void setWebClientConfigValue(String webClientConfigValue) {
        this.webClientConfigValue = webClientConfigValue;
    }
}
