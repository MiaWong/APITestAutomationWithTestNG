package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.cssrobject;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarSearchCriteria;

import java.util.List;

/**
 * Created by jiyu on 1/12/17.
 */
public class CarSupplySearchCriteria extends CarSearchCriteria
{
    List<Long> carItemIDList;

    //  getters
    public List<Long> getCarItemIDList() { return carItemIDList; }

    //  setters
    public void setCarItemIDList(List<Long> carItemIDList) { this.carItemIDList = carItemIDList; }

}
