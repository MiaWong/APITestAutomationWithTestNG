package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.cssrobject;


import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.SearchContextData;

import java.util.List;

/**
 * Created by jiyu on 1/10/17.
 */
public class CSSRSearchRequestTestData extends SearchContextData
{
    //  data fields in search request
    private List<CarSupplySearchCriteria> carSearchCriteriaList;

    //  Sub Java Objects getters
    public List<CarSupplySearchCriteria> getCarSearchCriteriaList()
    {
        return carSearchCriteriaList;
    }

    //  Sub Java Objects setters
    public void setCarSearchCriteriaList(List<CarSupplySearchCriteria> carSearchCriteriaList) { this.carSearchCriteriaList = carSearchCriteriaList; }

}
