package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.cesrobject;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.Language;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.PointOfSaleKey;


/**
 * Created by jiyu on 1/12/17.
 */
public class SiteMessageInfo
{
    private PointOfSaleKey pointOfSaleKey;

    private Language language;

    public PointOfSaleKey getPointOfSaleKey()
    {
        return pointOfSaleKey;
    }
    public Language getLanguage()
    {
        return language;
    }

    public void setPointOfSaleKey(PointOfSaleKey pointOfSaleKey) { this.pointOfSaleKey = pointOfSaleKey; }
    public void setLanguage(Language language) { this.language = language; }

}
