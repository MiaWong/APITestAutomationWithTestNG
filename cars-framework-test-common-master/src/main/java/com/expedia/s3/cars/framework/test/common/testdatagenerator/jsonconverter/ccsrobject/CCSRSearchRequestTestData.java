package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.SearchContextData;

import java.util.List;

/**
 * Created by jiyu on 1/10/17.
 */
public class CCSRSearchRequestTestData extends SearchContextData
{
    //  data fields in search request
    private List<CarConnectivitySearchCriteria> carSearchCriteriaList;

    //  Sub Java Objects getters
    public List<CarConnectivitySearchCriteria> getCarSearchCriteriaList()
    {
        return carSearchCriteriaList;
    }
    //  Sub Java Objects setters
    public void setCarSearchCriteriaList(List<CarConnectivitySearchCriteria> carSearchCriteriaList) { this.carSearchCriteriaList = carSearchCriteriaList; }

}
