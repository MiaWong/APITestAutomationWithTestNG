package com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;

public class CommonTestHelper {

    private CommonTestHelper(){
        //private for util class
    }

    public static String getClientCode(CarCommonEnumManager.ClientID clientID) throws DataAccessException {
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        return carBSHelper.getClientListById(clientID.getValue()).get(0).getClientCode();
    }
}
