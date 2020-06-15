package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.*;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by asharma1 on 9/26/2016.
 */
public class CarsSCSHelper
{
    final private DataSource dataSource;

    public CarsSCSHelper(DataSource datasource)
    {
        this.dataSource = datasource;
    }

    public List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMap(long supplierID,long messageSystemID,String domainType,String domainValue,String externalDomainValue) throws DataAccessException{
        final CarsSCSDataSource carsSCSDataSource = new CarsSCSDataSource(dataSource);
        return carsSCSDataSource.getExternalSupplyServiceDomainValueMap(supplierID,messageSystemID,domainType,domainValue,externalDomainValue);
    }

    public List<ExternalSupplyServiceDomainValueMap> getExternalSupplyServiceDomainValueMap(String domainType,String externalDomainValue, List<CarVendor> carVendorList) throws DataAccessException{
        final CarsSCSDataSource carsSCSDataSource = new CarsSCSDataSource(dataSource);
        return carsSCSDataSource.getExternalSupplyServiceDomainValueMap(domainType,externalDomainValue,carVendorList);
    }

    public PoSToWorldspanDefaultSegmentMap getPoSToWorldspanDefaultSegmentMap(TestScenario testScenario)throws DataAccessException{
        final CarsSCSDataSource carsSCSDataSource = new CarsSCSDataSource(dataSource);
        final List<PoSToWorldspanDefaultSegmentMap> defaultSegmentMapList = carsSCSDataSource.getPoSToWorldspanDefaultSegmentMap(testScenario);
        return CompareUtil.isObjEmpty(defaultSegmentMapList) ? null : defaultSegmentMapList.get(0) ;
    }

    public SupplierConfiguration getSupplierSetting(String settingName,String env,long supplierId)throws DataAccessException {
        final CarsSCSDataSource carsSCSDataSource = new CarsSCSDataSource(dataSource);
        final List<SupplierConfiguration> supplierConfigurationList = carsSCSDataSource.getSupplierSetting(settingName,env,supplierId);
        return CompareUtil.isObjEmpty(supplierConfigurationList) ? null : supplierConfigurationList.get(0) ;
    }

    public List<SupplierItemMap> getSupplierItemMap(long supplySubsetID) throws DataAccessException {
        final CarsSCSDataSource carsSCSDataSource = new CarsSCSDataSource(dataSource);
        return carsSCSDataSource.getSupplierItemMap(supplySubsetID);
    }
}