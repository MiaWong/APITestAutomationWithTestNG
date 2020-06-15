package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;


/**
 * Created by v-mechen on 2/9/2017.
 */
public class CarProductUtil {
    private CarProductUtil()
    {

    }

    /*
    Get matched car by SIPP/supplierID/location/CD code from CarProductList
     */
    public static CarProductType getMatchedCarFromList(CarProductListType carList, CarProductType expCar) {
        for(final CarProductType car: carList.getCarProduct()) {
            if(CarProductComparator.isCorrespondingCar(expCar, car))
            {
                return car;
            }
        }
        return null;
    }
}
