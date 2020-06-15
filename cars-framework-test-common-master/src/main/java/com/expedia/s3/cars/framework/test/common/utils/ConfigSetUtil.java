package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.ClientConfig;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.HttpClient;

import java.io.IOException;

/**
 * Created by yyang4 on 11/21/2016.
 */
@SuppressWarnings("PMD")
public class ConfigSetUtil {
    public static void posConfigSet(PosConfig posConfig, TestScenario scenario, HttpClient httpClient,String tuid, String url, boolean setEnv) throws IOException{
        //create set url
        final String env = posConfig.getEnvironmentName();
        final String envSetString = setEnv ? "&environment=" + env : "";
        final String actionType = !StringUtils.isEmpty(posConfig.getSettingValue()) ? "set" : "delete";
        final String valueSetString = !StringUtils.isEmpty(posConfig.getSettingValue())  ? "&value=" + posConfig.getSettingValue() : "";
        url = url.split("://")[0] + "://" + url.split("://")[1].split("/")[0] + "/config/" + actionType + "?" + envSetString;
        if(scenario != null) {
            if (!StringUtils.isEmpty(scenario.getCompanyCode())) {
                url = url + "&companyCode=" + scenario.getCompanyCode();
            }
            if (!StringUtils.isEmpty(scenario.getManagementUnitCode())) {
                url = url + "&managementUnitCode=" + scenario.getManagementUnitCode();
            }
            if (!StringUtils.isEmpty(scenario.getJurisdictionCountryCode())) {
                url = url + "&jurisdictionCode=" + scenario.getJurisdictionCountryCode();
            }
        }
        url = url + "&settingName=" + posConfig.getSettingName() + valueSetString + "&updatedBy=CarServiceAutomationTest" + tuid + "&flushAll=1";
        //send set request
        final HttpMessageSendUtil httpMessageSendUtil = new HttpMessageSendUtil();
        httpMessageSendUtil.setHttpClient(httpClient);
        httpMessageSendUtil.setServiceUrl(url);
        httpMessageSendUtil.sendHttpMessage();

    }

    public static void clientConfigSet(ClientConfig clientConfig, HttpClient httpClient, String tuid, String url, boolean setEnv) throws IOException{
        //create set url
        final String env = clientConfig.getEnvironmentName();
        final String envSetString = setEnv ? "&environment=" + env : "";
        final String actionType = !StringUtils.isEmpty(clientConfig.getSettingValue()) ? "set" : "delete";
        final String valueSetString = !StringUtils.isEmpty(clientConfig.getSettingValue())  ? "&value=" + clientConfig.getSettingValue() : "";
        url = url.split("://")[0] + "://" + url.split("://")[1].split("/")[0] + "/clientconfig/" + actionType + "?" + envSetString;
        if(clientConfig.getClientId() != null && 0 != clientConfig.getClientId().intValue()){
            url = url + "&clientID=" + clientConfig.getClientId();
        }

        url = url + "&settingName=" + clientConfig.getSettingName() + valueSetString + "&updatedBy=CarServiceAutomationTest" + tuid + "&flushAll=1";
        //send set request
        final HttpMessageSendUtil httpMessageSendUtil = new HttpMessageSendUtil();
        httpMessageSendUtil.setHttpClient(httpClient);
        httpMessageSendUtil.setServiceUrl(url);
        httpMessageSendUtil.sendHttpMessage();

    }
}
