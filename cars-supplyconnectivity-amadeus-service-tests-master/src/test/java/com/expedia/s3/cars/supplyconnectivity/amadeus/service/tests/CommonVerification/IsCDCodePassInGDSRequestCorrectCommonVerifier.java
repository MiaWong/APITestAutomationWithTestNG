package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.SupplierItemMap;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by miawang on 8/30/2016.
 */
public class IsCDCodePassInGDSRequestCorrectCommonVerifier
{
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public void verify(CarProductType carProduct, Map<Long, String> vendorDiscountNumsMap, ArrayList remarks)
    {
        try
        {
            //verify cd code send in gds request is correct.
            //cd code in request
            String cdCodeConfigured = null;

            if (null != carProduct)
            {
                //get cd code from database.
                CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);
                List<SupplierItemMap> supplierItems = scsDataSource.getSupplierItemMap(carProduct.getCarInventoryKey().getSupplySubsetID());
                for (SupplierItemMap supplierItem : supplierItems)
                {
                    if (supplierItem.getItemKey().equals("CorporateDiscountCode"))
                    {
                        cdCodeConfigured = supplierItem.getItemValue();
                    }
                }

                verifyCdCodeInGDSRequest(null == carProduct.getCarInventoryKey().getCarRate()? null : carProduct.getCarInventoryKey().getCarRate().getCorporateDiscountCode(),
                        cdCodeConfigured,carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), vendorDiscountNumsMap, remarks);
            }
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }
    }

    public void verifyCdCodeInGDSRequest(String cdCodeInReq, String cdCodeConfigured, long vendorSupplierIDInRequest, Map<Long, String> vendorDiscountNumsMap, ArrayList remarks)
    {
        //verify cd code send in gds request is correct.
        if (!vendorDiscountNumsMap.isEmpty() && vendorDiscountNumsMap.containsKey(vendorSupplierIDInRequest))
        {
            if (null != cdCodeInReq && !cdCodeInReq.equals(vendorDiscountNumsMap.get(vendorSupplierIDInRequest)))
            {
                remarks.add("\n CD Code in Amadeus GDS request for vendor : "+ vendorSupplierIDInRequest
                        + " is : " + vendorDiscountNumsMap.get(vendorSupplierIDInRequest)
                        + ", different with CD Code in SCS Request : " + cdCodeInReq + ". \n");
            } else if (null == cdCodeInReq && null != cdCodeConfigured && !cdCodeConfigured.equals(vendorDiscountNumsMap.get(vendorSupplierIDInRequest)))
            {
                remarks.add("\n Configured CD Code for vendor : "+ vendorSupplierIDInRequest
                        + " is : " + cdCodeConfigured + ", different with the CD Code send in GDS request : "
                        + vendorDiscountNumsMap.get(vendorSupplierIDInRequest) + ". \n");
            }
        } else if (null != cdCodeInReq)
        {
            remarks.add("SCS request have CarRateOverrideList, cd code is : "+cdCodeInReq+" for vendor : " + vendorSupplierIDInRequest
                    + ", but do not pass to GDS Request.\n");
        } else if (null != cdCodeConfigured)
        {
            remarks.add("This supplysubset have configured CD code : "+ cdCodeConfigured+" for vendor : " + vendorSupplierIDInRequest
                    + ", but do not pass to GDS Request.\n");
        }
    }

    private String getName()
    {
        return getClass().getSimpleName();
    }
}