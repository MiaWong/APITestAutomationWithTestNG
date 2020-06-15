package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by asharma1 on 10/3/2016.
 */
@SuppressWarnings("PMD")

public class CarsSCSDataSource {
    final private DataSource dataSource;

    public CarsSCSDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("CPD-START")
    public List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMap(long supplierID, long messageSystemID,
                                                                                            String domainType, String domainValue,
                                                                                            String externalDomainValue) throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append("Select SupplierID,MessageSystemID,DomainType,DomainValue,ExternalDomainValue,CreateDate,CreatedBy," +
                "UpdateDate,LastUpdatedBy from ExternalSupplyServiceDomainValueMap where 1=1 ");
        final Map<String, Object> paramMap = new HashMap<>();
        if(0 != supplierID) {
            sqlQuery.append(" and SupplierID = :supplierID ");
            paramMap.put("supplierID", supplierID);
        }
        if(0 != messageSystemID){
            sqlQuery.append(" and MessageSystemID = :messageSystemID ");
            paramMap.put("messageSystemID", messageSystemID);
        }
        if(!StringUtils.isEmpty(domainType)){
            sqlQuery.append(" and DomainType = :domainType ");
            paramMap.put("domainType", domainType);
        }
        if(!StringUtils.isEmpty(domainValue)){
            sqlQuery.append(" and DomainValue = :domainValue ");
            paramMap.put("domainValue", domainValue);
        }
        if(!StringUtils.isEmpty(externalDomainValue)){
            sqlQuery.append(" and ExternalDomainValue like :externalDomainValue ");
            paramMap.put("externalDomainValue", externalDomainValue);
        }
        final ParametrizedQuery<ExternalSupplyServiceDomainValueMap> tsql = new ParametrizedQuery<ExternalSupplyServiceDomainValueMap>(sqlQuery.toString(),
                dataSource, ExternalSupplyServiceDomainValueMap.class);
        return tsql.execute(paramMap);
    }

    public List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMap(List<Long> supplierIDs, long messageSystemID,
                                                                                            String domainType, String domainValue,
                                                                                            String externalDomainValue) throws DataAccessException {
        List<ExternalSupplyServiceDomainValueMap> maps = new ArrayList<>();
        for(Long supplierID : supplierIDs)
        {
            maps.addAll(getExternalSupplyServiceDomainValueMap( supplierID, messageSystemID,domainType,domainValue,externalDomainValue));
        }

        return maps;
    }

    public List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMap (String domainType,
                                                                                             String externalDomainValue,
                                                                                             List<CarVendor> carVendorList)
            throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append("Select a.SupplierID,a.MessageSystemID,a.DomainType,a.DomainValue,a.ExternalDomainValue," +
                "a.CreateDate,a.CreatedBy,a.UpdateDate,a.LastUpdatedBy from ExternalSupplyServiceDomainValueMap a");
        sqlQuery.append(" where a.DomainType = :domainType and a.ExternalDomainValue like :externalDomainValue" +
                " and a.SupplierID in (:supplierIDs)");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("domainType",domainType);
        paramMap.put("externalDomainValue",externalDomainValue);
        paramMap.put("supplierIDs", carVendorList.stream()
                .map(i -> i.getSupplierID())
                .collect(Collectors.toList()));
        final ParametrizedQuery<ExternalSupplyServiceDomainValueMap> tsql = new ParametrizedQuery<ExternalSupplyServiceDomainValueMap>(sqlQuery.toString(),
                dataSource, ExternalSupplyServiceDomainValueMap.class);
        return tsql.execute(paramMap);
    }

    public List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMap (String domainType,
                                                                                             String externalDomainValue)
            throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append("Select a.SupplierID,a.MessageSystemID,a.DomainType,a.DomainValue,a.ExternalDomainValue," +
                "a.CreateDate,a.CreatedBy,a.UpdateDate,a.LastUpdatedBy from dbo.ExternalSupplyServiceDomainValueMap a");
        sqlQuery.append(" where a.DomainType = :domainType and a.ExternalDomainValue like :externalDomainValue");

        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("domainType",domainType);
        paramMap.put("externalDomainValue",externalDomainValue);
        final ParametrizedQuery<ExternalSupplyServiceDomainValueMap> tsql = new ParametrizedQuery<ExternalSupplyServiceDomainValueMap>(sqlQuery.toString(),
                dataSource, ExternalSupplyServiceDomainValueMap.class);
        return tsql.execute(paramMap);
    }

    public List<PoSToWorldspanDefaultSegmentMap> getPoSToWorldspanDefaultSegmentMap (TestScenario testScenario)
            throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append("select JurisdictionCode,CompanyCode,ManagementUnitCode,IATA,IATAPackage,BranchCode,PackageBranchCode from PoSToWorldspanDefaultSegmentMap where 1=1");
        final Map<String, Object> paramMap = new HashMap<>();
        if(!CompareUtil.isObjEmpty(testScenario.getJurisdictionCountryCode())){
            sqlQuery.append(" and JurisdictionCode= :jurisdictionCode ");
            paramMap.put("jurisdictionCode",testScenario.getJurisdictionCountryCode());
        }
        if(!CompareUtil.isObjEmpty(testScenario.getCompanyCode())){
            sqlQuery.append(" and CompanyCode= :companyCode ");
            paramMap.put("companyCode",testScenario.getCompanyCode());
        }
        if(!CompareUtil.isObjEmpty(testScenario.getManagementUnitCode())){
            sqlQuery.append(" and ManagementUnitCode= :managementUnitCode ");
            paramMap.put("managementUnitCode",testScenario.getManagementUnitCode());
        }
        final ParametrizedQuery<PoSToWorldspanDefaultSegmentMap> tsql = new ParametrizedQuery<PoSToWorldspanDefaultSegmentMap>(sqlQuery.toString(),
                dataSource, PoSToWorldspanDefaultSegmentMap.class);
        return tsql.execute(paramMap);
    }


    public List<SupplierConfiguration> getSupplierSetting (String settingName, String env, long supplierId)throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer(" SELECT SupplierConfigurationID,EnvironmentName,SupplierID,SettingName,SettingValue FROM SupplierConfiguration WHERE 1=1 ");
        final Map<String, Object> paramMap = new HashMap<>();
        if(!CompareUtil.isObjEmpty(settingName)){
            sqlQuery.append(" and SettingName= :settingName ");
            paramMap.put("settingName",settingName);
        }
        if(!CompareUtil.isObjEmpty(env)){
            sqlQuery.append(" and EnvironmentName= :env ");
            paramMap.put("env",env);
        }
        else
        {
            sqlQuery.append(" and EnvironmentName is null ");
        }
        if(supplierId != 0){
            sqlQuery.append(" and SupplierID= :supplierId ");
            paramMap.put("supplierId",supplierId);
        }
        else
        {
            sqlQuery.append(" and SupplierID is null ");
        }
        final ParametrizedQuery<SupplierConfiguration> tsql = new ParametrizedQuery<SupplierConfiguration>(sqlQuery.toString(),
                dataSource, SupplierConfiguration.class);
        return tsql.execute(paramMap);
    }

    public List<SupplierItemMap> getSupplierItemMap(long supplySubsetID) throws DataAccessException {
        final String sqlQuery = "select distinct ItemKey,ItemValue from SupplierItemMap where SupplySubsetID in (:supplyIds)  and itemkey is not null";
        final ParametrizedQuery<SupplierItemMap> tsql = new ParametrizedQuery<SupplierItemMap>(sqlQuery, dataSource, SupplierItemMap.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplyIds", supplySubsetID);
        return tsql.execute(paramMap);
    }

    public List<Country> getCountryCodeFromCountryShortCode(String countryshortcode) throws DataAccessException {
        final String sqlQuery = "SELECT * from Country where countryshortcode = :countryshortcode";

        final ParametrizedQuery<Country> tsql = new ParametrizedQuery<>(sqlQuery, dataSource, Country.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("countryshortcode", countryshortcode);
        return tsql.execute(paramMap);

    }
    public List<SupplySubSetToWorldSpanSupplierItemMap> getWorldSpanSupplierItemMap(List<SupplySubsetIDEntryType> subsetIDEntryTypeList) throws DataAccessException {
        final String sqlQuery = "select distinct SupplySubsetID, ITNumber, CorporateDiscountCodeRequiredInShopping,CorporateDiscountCodeRequiredInBooking,RateCode,IATAAgencyCode,CorporateDiscountCode,IATAOverrideBooking from SupplySubSetToWorldSpanSupplierItemMap where SupplySubsetID in (:supplyIds)";
        final ParametrizedQuery<SupplySubSetToWorldSpanSupplierItemMap> tsql = new ParametrizedQuery<SupplySubSetToWorldSpanSupplierItemMap>(sqlQuery, dataSource, SupplySubSetToWorldSpanSupplierItemMap.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplyIds", subsetIDEntryTypeList.stream()
                .map(i -> i.getSupplySubsetID())
                .collect(Collectors.toList()));
        return tsql.execute(paramMap);
    }

    @SuppressWarnings("CPD-END")
    public String getCarBehaviorAttributValue(Long supplierID,Long supplySubsetID,Long carBehaviorAttID) throws DataAccessException {
        final String sqlQuery = "select cba.AttributeValue as carVendorCode from CarBehaviorAttributeValue as cba where SupplierID = :supplierID and SupplySubsetID in (:supplySubsetID,0) and CarBehaviorAttributeID = :carBehaviorAttID";
        final ParametrizedQuery<CarVendor> tsql = new ParametrizedQuery<CarVendor>(sqlQuery, dataSource, CarVendor.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplierID", supplierID);
        paramMap.put("supplySubsetID", supplySubsetID);
        paramMap.put("carBehaviorAttID", carBehaviorAttID);
        final List<CarVendor> resultList = tsql.execute(paramMap);
        return CompareUtil.isObjEmpty(resultList) ? "" : resultList.get(0).getCarVendorCode();
    }

}