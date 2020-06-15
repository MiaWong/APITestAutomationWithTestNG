package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.utils;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiyu on 8/25/16.
 */
public final class PropertyResetHelper {
    //  filter request search list
    public static CarSupplyConnectivitySearchRequestType filterReqSearchList(CarSupplyConnectivitySearchRequestType request, DataSource dataSource) throws DataAccessException {
        final CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        final List<CarSearchCriteriaType> carSearchCriteriaTypeList = new ArrayList<CarSearchCriteriaType>();
        if(!CollectionUtils.isEmpty(request.getCarSearchCriteriaList().getCarSearchCriteria())) {
            for (CarSearchCriteriaType criteriaType : request.getCarSearchCriteriaList().getCarSearchCriteria()) {
                final long supplierId = criteriaType.getVendorSupplierIDList().getVendorSupplierID().get(0);
                final String domainType = "CarVendorLocation";
                final CarLocationKeyType startCarLocation = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
                final CarLocationKeyType endCarLocation = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
                final String startDomainValue = String.valueOf(startCarLocation.getCarVendorLocationID());
                final String endDomainValue = String.valueOf(endCarLocation.getCarVendorLocationID());
                final String startExternalDomainValue= startCarLocation.getLocationCode() + startCarLocation.getCarLocationCategoryCode() + startCarLocation.getSupplierRawText().substring(1);
                final String endExternalDomainValue = endCarLocation.getLocationCode() + endCarLocation.getCarLocationCategoryCode() + endCarLocation.getSupplierRawText().substring(1);
                final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(dataSource);
                final List<ExternalSupplyServiceDomainValueMap> startMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(supplierId,0,domainType,startDomainValue,startExternalDomainValue);
                final List<ExternalSupplyServiceDomainValueMap> endMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(supplierId,0,domainType,endDomainValue,endExternalDomainValue);
                if(!CollectionUtils.isEmpty(startMapList) && !CollectionUtils.isEmpty(endMapList)){
                    carSearchCriteriaTypeList.add(criteriaType);
                }

            }
        }
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypeList);
        request.setCarSearchCriteriaList(carSearchCriteriaListType);
        return request;
    }

}
