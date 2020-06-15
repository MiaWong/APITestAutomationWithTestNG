package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.s3.cars.framework.core.EventLogBaseID;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.logging.LogHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.CarPosConfigDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.transport.SimpleDOMTransport;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.google.common.base.Charsets;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 10/25/2016.
 */
public class PosConfigHelper {
    final private Logger logger = Logger.getLogger(getClass());
    final private DataSource dataSource;
    private String url;
    private final String environment;
    private List<PosConfig> rollbackPosConfigs = new ArrayList<>();

    public PosConfigHelper(DataSource datasource, String url, String environment) {
        this.dataSource = datasource;
        this.url = url;
        this.environment = environment;
    }

    public PosConfigHelper(DataSource datasource, String url) {
        this.dataSource = datasource;
        this.url = url;
        environment = AppConfig.resolveStringValue("${environment.name}");
    }

    public PosConfigHelper(DataSource datasource) {
        this.dataSource = datasource;
        environment = AppConfig.resolveStringValue("${environment.name}");
    }

    /**
     * query the PosConfig if the feature expected(with ignorePOS feature)
     */
    @SuppressWarnings("PMD")
    public boolean checkPosConfigFeatureEnable(TestScenario scenario, String expectValue, String settingName, boolean ignorePOS) throws DataAccessException, SQLException {
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        if (!ignorePOS) {
            return queryForPos(scenario, expectValue, settingName, carPosConfigDataSource);
        } else {
            //env and normal
            return queryIgnorePos(expectValue, settingName, environment, carPosConfigDataSource);
        }
    }

    /**
     * query the PosConfig if the feature expected(with ignorePOS feature)
     */
    @SuppressWarnings("PMD")
    public boolean checkPosConfigFeatureEnable(TestScenario scenario, String expectValue, String settingName, String environment, boolean ignorePOS) throws DataAccessException, SQLException {
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        if (!ignorePOS) {
            return queryForPos(scenario, expectValue, settingName, carPosConfigDataSource);
        } else {
            //env and normal
            return queryIgnorePos(expectValue, settingName, environment, carPosConfigDataSource);
        }
    }


    /**
     *
     * @param scenario
     * @param expectValue
     * @param settingName
     * @return
     * @throws DataAccessException
     * @throws SQLException
     */
    public boolean checkPosConfigFeatureEnable(TestScenario scenario, String expectValue, String settingName) throws DataAccessException, SQLException {
        return checkPosConfigFeatureEnable(scenario, expectValue, settingName, environment);
    }

    @SuppressWarnings("PMD")
    public boolean checkPosConfigFeatureEnable(TestScenario scenario, String expectValue, String settingName, String environment) throws DataAccessException, SQLException
    {
        //1, pos and env
        final List<PosConfig> posConfigList = queryForPos(scenario, settingName);
        if (!CollectionUtils.isEmpty(posConfigList))
        {
            if (expectValue.equals(posConfigList.get(0).getSettingValue()))
            {
                return true;
            }
            return false;

        }

        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);

        // 2.env and 3. just settingname
        return queryIgnorePos(expectValue, settingName, environment, carPosConfigDataSource);
    }

    public String getPosConfigSettingValue(TestScenario scenario, String settingName) throws DataAccessException, SQLException {
        //1,pos
        final List<PosConfig> posConfigList = queryForPos(scenario, settingName);
        if (!CollectionUtils.isEmpty(posConfigList)) {
            return posConfigList.get(0).getSettingValue();
            }

        // 2,env and Pos
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        final List<PosConfig> envPosConfigList = queryForEnv(settingName, environment, carPosConfigDataSource);
         if (!CollectionUtils.isEmpty(envPosConfigList)) {
            return envPosConfigList.get(0).getSettingValue();
            }

        //3,normal
        final List<PosConfig> normalPosConfigList = queryForAll(settingName, carPosConfigDataSource);
        if (!CollectionUtils.isEmpty(normalPosConfigList)) {
            return normalPosConfigList.get(0).getSettingValue();
            }

        return null;
    }

    /**
     * set expected value in PosConfig (no ignorePOS feature)
     *
     * @param scenario
     * @param expectValue
     * @param settingName
     * @return
     * @throws Exception
     */
    public boolean setFeatureEnable(TestScenario scenario, String expectValue, String settingName) throws Exception {

        //check the  expectValue
        if (checkPosConfigFeatureEnable(scenario, expectValue, settingName)) {
            return true;
        }

        //set the expectValue with pos
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        final PosConfig posConfig = getPosConfig(scenario, settingName, environment);

        //set RollBackList and  update Cache
        return setRollBackAndUpdateCache(expectValue, carPosConfigDataSource, posConfig);

    }

    /**
     *
     * @param scenario
     * @param expectValue
     * @param settingName
     * @param environment
     * @return
     * @throws Exception
     */
    public boolean setFeatureEnable(TestScenario scenario, String expectValue, String settingName, String environment) throws Exception {

        //check the  expectValue
        if (checkPosConfigFeatureEnable(scenario, expectValue, settingName, environment)) {
            return true;
        }

        //set the expectValue with env
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        final PosConfig posConfig = getPosConfig(scenario, settingName, environment);

        //set RollBackList and  update Cache
        return setRollBackAndUpdateCache(expectValue, carPosConfigDataSource, posConfig);

    }

    /**
     * set expected value in PosConfig (with ignorePOS feature)
     *
     * @param scenario
     * @param expectValue
     * @param settingName
     * @param ignorePOS
     * @return
     * @throws Exception
     */
    public boolean setFeatureEnable(TestScenario scenario, String expectValue, String settingName, boolean ignorePOS) throws Exception {

        //check the expectValue
        boolean exist = true;

        //check the specific pos expectValue
        if (checkPosConfigFeatureEnable(scenario, expectValue, settingName, ignorePOS)) {
            return exist;
        }
        exist = false;

        //set the expectValue with pos
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        if (!exist && ignorePOS) {
            //query the featureName ,add to rollbackList
            //env
            final PosConfig posConfig = getPosConfig(settingName, environment);

            //set RollBackList and update cache
            return setRollBackAndUpdateCache(expectValue, carPosConfigDataSource, posConfig);
        }
        if (!exist && !ignorePOS) {
            final PosConfig posConfig = getPosConfig(scenario, settingName, environment);
            //set RollBackList and  update Cache
            return setRollBackAndUpdateCache(expectValue, carPosConfigDataSource, posConfig);

        }

        return false;

    }



    /**
     * RollBack PosConfiguration
     *
     * @return
     * @throws Exception
     */
    public boolean rollbackPosConfigList() throws Exception {
        if (CollectionUtils.isEmpty(rollbackPosConfigs)) {
            return true;
        }

        return posConfigFeatureSet(rollbackPosConfigs);

    }

    @SuppressWarnings("PMD")
    private boolean queryIgnorePos(String expectValue, String settingName, String environment, CarPosConfigDataSource carPosConfigDataSource) throws DataAccessException {
        // 2, env & Pos
        final List<PosConfig> envPosConfigList = queryForEnv(settingName, environment, carPosConfigDataSource);
        if (!CollectionUtils.isEmpty(envPosConfigList)) {
            if (expectValue.equals(envPosConfigList.get(0).getSettingValue())) {
                return true;
            }
            return false;

        }

        //3. only env

        //3, only setting name
        final List<PosConfig> normalPosConfigList = queryForAll(settingName, carPosConfigDataSource);
        if (!CollectionUtils.isEmpty(normalPosConfigList)) {
            if (expectValue.equals(normalPosConfigList.get(0).getSettingValue())) {
                return true;
            }
            return false;

        }
        //default to off
        else{
            if(expectValue.equals("0")){
                return true;
            }

        }

        return false;
    }


    private List<PosConfig> queryForEnv(String settingName, String environment, CarPosConfigDataSource carPosConfigDataSource) throws DataAccessException {
        final PosConfig posConfig = getPosConfig(settingName, environment);
        return carPosConfigDataSource.getPosConfigValue(posConfig);

    }

    @SuppressWarnings("PMD")
    private boolean queryForPos(TestScenario scenario, String expectValue, String settingName, CarPosConfigDataSource carPosConfigDataSource) throws DataAccessException {
        final PosConfig posConfig = new PosConfig();
        posConfig.setCompanyCode(scenario.getCompanyCode());
        posConfig.setManagementUnitCode(scenario.getManagementUnitCode());
        posConfig.setJurisdictionCode(scenario.getJurisdictionCountryCode());
        posConfig.setSettingName(settingName);
        posConfig.setSettingValue(expectValue);
        posConfig.setEnvironmentName(PojoXmlUtil.getEnvironment());
        final List<PosConfig> posConfigList = carPosConfigDataSource.getPosConfigValue(posConfig);
        if (CollectionUtils.isEmpty(posConfigList)) {
            return false;
        }
        return true;
    }

    public List<PosConfig> queryForPos(TestScenario scenario, String settingName) throws DataAccessException {
        final PosConfig posConfig = getPosConfig(scenario, settingName, environment);
        final CarPosConfigDataSource carPosConfigDataSource = new CarPosConfigDataSource(dataSource);
        return carPosConfigDataSource.getPosConfigValue(posConfig);

    }

    private List<PosConfig> queryForAll(String settingName, CarPosConfigDataSource carPosConfigDataSource) throws DataAccessException {
        final PosConfig posConfig = new PosConfig();
        posConfig.setSettingName(settingName);
        return carPosConfigDataSource.getPosConfigValue(posConfig);

    }


    private PosConfig getPosConfig(TestScenario scenario, String settingName, String environment) {
        final PosConfig posConfig = new PosConfig();
        posConfig.setCompanyCode(scenario.getCompanyCode());
        posConfig.setManagementUnitCode(scenario.getManagementUnitCode());
        posConfig.setJurisdictionCode(scenario.getJurisdictionCountryCode());
        posConfig.setSettingName(settingName);
        posConfig.setEnvironmentName(environment);
        return posConfig;
    }

    private PosConfig getPosConfig(String settingName, String environment) {
        final PosConfig posConfig = new PosConfig();
        posConfig.setSettingName(settingName);
        posConfig.setEnvironmentName(environment);
        return posConfig;
    }

    private boolean setRollBackAndUpdateCache(String expectValue, CarPosConfigDataSource carPosConfigDataSource, PosConfig posConfig) throws Exception {

        setRollbackList(posConfig, expectValue, carPosConfigDataSource);

        posConfig.setSettingValue(expectValue);
        final List<PosConfig> posConfigs = new ArrayList<>();
        posConfigs.add(posConfig);
        // update cache
        return posConfigFeatureSet(posConfigs);
    }


    private void setRollbackList(PosConfig posConfig, String expectValue, CarPosConfigDataSource carPosConfigDataSource) throws DataAccessException {

        final List<PosConfig> posConfigList = carPosConfigDataSource.getPosConfigValue(posConfig);
        final PosConfig posConfigTemp = new PosConfig();
        if (CollectionUtils.isEmpty(posConfigList)) {
            //if featureName not exist, rollbackList add record that will be insert
            BeanUtils.copyProperties(posConfig, posConfigTemp);
            posConfigTemp.setSettingValue(expectValue);
            posConfigTemp.setDelete("true");
            final List<PosConfig> posConfigs = new ArrayList<>();
            posConfigs.add(posConfigTemp);
            this.rollbackPosConfigs.addAll(posConfigs);
        } else {
            BeanUtils.copyProperties(posConfig, posConfigTemp);
            posConfigTemp.setSettingValue(posConfigList.get(0).getSettingValue());
            final List<PosConfig> posConfigs = new ArrayList<>();
            posConfigs.add(posConfigTemp);
            this.rollbackPosConfigs.addAll(posConfigs);
        }
    }


    public boolean posConfigFeatureSet(List<PosConfig> posConfigs) throws Exception {

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        final Document requedocRequestst = dbf.newDocumentBuilder().newDocument();
        final Element rootElement = requedocRequestst.createElement("ConfigEntryList");
        rootElement.setAttribute("updatedBy", "TestNG Automation");
        rootElement.setAttribute("flushAll", "1");
        for (final PosConfig posConfig : posConfigs) {
            setChild(requedocRequestst, rootElement, posConfig);
        }
        logger.debug("ConfigEntryList :" + PojoXmlUtil.toString(rootElement));
        posConfigCacheUpdate(PojoXmlUtil.toString(rootElement));

        return true;
    }

    private void posConfigCacheUpdate(String request) {
        try {
            final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
            httpClient.start();
            httpClient
                    .POST(url)
                    .accept(MediaType.APPLICATION_XML_VALUE)
                    .content(new BytesContentProvider(MediaType.APPLICATION_XML_VALUE,
                            request.getBytes(Charsets.UTF_8)))
                    .send();

            httpClient.stop();
        } catch (Exception e) {
            LogHelper.log(null, Level.INFO, EventLogBaseID.INFO, "posConfigCacheUpdate error", e);
        }
    }

    private void posConfigCacheUpdate(Element rootElement) {
        try {
            final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
            httpClient.start();

            final SimpleDOMTransport trans = new SimpleDOMTransport(MediaType.APPLICATION_XML_VALUE, httpClient, null, 30000,
                    this.url,
                    rootElement);
            trans.execute(null);

            httpClient.stop();
        } catch (Exception e) {
            LogHelper.log(null, Level.INFO, EventLogBaseID.INFO, "posConfigCacheUpdate error", e);
        }
    }
    @SuppressWarnings("PMD")
    private void setChild(Document requedocRequestst, Element rootElement, PosConfig posConfig) {
        final Element configEntityNode = requedocRequestst.createElement("ConfigEntry");
        rootElement.appendChild(configEntityNode);
        configEntityNode.setAttribute("environment", posConfig.getEnvironmentName());
        configEntityNode.setAttribute("companyCode", StringUtils.isEmpty(posConfig.getCompanyCode()) ? null : posConfig.getCompanyCode());
        configEntityNode.setAttribute("managementUnitCode", StringUtils.isEmpty(posConfig.getManagementUnitCode()) ? null : posConfig.getManagementUnitCode());
        configEntityNode.setAttribute("jurisdictionCode", StringUtils.isEmpty(posConfig.getJurisdictionCode()) ? null : posConfig.getJurisdictionCode());
        configEntityNode.setAttribute("settingName", posConfig.getSettingName());
        configEntityNode.setAttribute("value", posConfig.getSettingValue());
        if (!StringUtils.isEmpty(posConfig.getDelete())) {
            configEntityNode.setAttribute("delete", posConfig.getDelete());
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getUrl() {
        return url;
    }

    public List<PosConfig> getRollbackPosConfigs() {
        return rollbackPosConfigs;
    }

    public void setRollbackPosConfigs(List<PosConfig> rollbackPosConfigs) {
        this.rollbackPosConfigs = rollbackPosConfigs;
    }
}
