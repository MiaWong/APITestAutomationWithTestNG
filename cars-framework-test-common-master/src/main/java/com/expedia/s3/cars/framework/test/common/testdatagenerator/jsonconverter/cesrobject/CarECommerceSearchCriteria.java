package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.cesrobject;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarSearchCriteria;

import java.util.List;

/**
 * Created by jiyu on 1/12/17.
 */
public class CarECommerceSearchCriteria extends CarSearchCriteria
{
    List<Long> carClassificationIDList;

    //  getters
    public List<Long> getCarClassificationIDList() { return carClassificationIDList; }

    //  setters
    public void setCarClassificationIDList(List<Long> carClassificationIDList) { this.carClassificationIDList = carClassificationIDList; }
}
