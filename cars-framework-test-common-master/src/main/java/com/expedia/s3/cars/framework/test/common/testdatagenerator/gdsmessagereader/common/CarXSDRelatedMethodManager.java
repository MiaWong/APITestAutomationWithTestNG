package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarConfigurationFormat;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yyang4 on 10/13/2017.
 */
@SuppressWarnings("PMD")
public class CarXSDRelatedMethodManager {
    //DataTable siVOFormat = CarsInventory.GetSIVOFormatFromSupplySubSetID(supplySubSetID);
    public static CarProductType SelectCarWithVOFormat(CarSearchResultListType carSearchResultList, DataSource dataSource) throws DataAccessException {
        CarProductType selectedCar = null;
        final CarsInventoryDataSource inventoryDataSource = new CarsInventoryDataSource(dataSource);

        for (int i = 0; i < carSearchResultList.getCarSearchResult().size(); i++)
        {
            if (!CompareUtil.isObjEmpty(carSearchResultList.getCarSearchResult().get(i).getCarProductList().getCarProduct()))
            {
                final List<CarProductType> searchResult = carSearchResultList.getCarSearchResult().get(i).getCarProductList().getCarProduct();
                for(CarProductType car : searchResult)
                {
                    final List<CarConfigurationFormat> configurationFormatList = inventoryDataSource.getCarConfigurationFormatList(car.getCarInventoryKey().getSupplySubsetID());
                    if (!CompareUtil.isObjEmpty(configurationFormatList))
                    {
                        String voFormat = configurationFormatList.get(0).getCarVoucherNumberFormat();
                        if (!CompareUtil.isObjEmpty(voFormat.trim()))
                        {
                            selectedCar = car;
                            break;
                        }
                    }
                }
            }
        }
        return selectedCar;
    }

}
