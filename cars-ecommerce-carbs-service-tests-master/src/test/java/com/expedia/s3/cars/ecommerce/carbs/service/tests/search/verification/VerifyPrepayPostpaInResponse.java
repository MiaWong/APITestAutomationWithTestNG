package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SuppressWarnings("PMD")
public class VerifyPrepayPostpaInResponse implements IVerification<List<CarProductType>, BasicVerificationContext>  {

    DataSource carsInventoryDatasource;
    private static final String MESSAGE_SUCCESS = "Success";

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }


    @Override
    public VerificationResult verify(List<CarProductType> carProducts, BasicVerificationContext verificationContext) {
        boolean isPassed = false;
        final List remarks = new ArrayList();

        if (carsInventoryDatasource == null) {
            remarks.add("carsInventoryDatasource is Null, need initialization first");
        }
        Long carItemID = 0L;
        int carBusinessModel = 0;
        for (final CarProductType carProduct : carProducts)
         {
            try {

                carItemID = carProduct.getCarInventoryKey().getCarItemID();
                final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDatasource);
                carBusinessModel = inventoryHelper.getBusinessModelID(carProduct);
                switch (carBusinessModel)
                {
                    case 1: //Agency
                    {
                        if (carProduct.getPrePayBoolean())
                        {
                            remarks.add("The value of PrePayBoolean is incorrect for AgencyCarItem=" + carItemID + ". PrePayBooleanInResp=" + carProduct.getPrePayBoolean());
                        }
                        break;
                    }
                    case 2: //Merchant
                    {
                        if (!carProduct.getPrePayBoolean())
                        {
                            remarks.add("The value of PrePayBoolean is incorrect for MerchantCarItem=" + carItemID + ". PrePayBooleanInResp=" + carProduct.getPrePayBoolean());
                        }
                        break;
                    }
                    case 3: //GDSP
                    {
                        if (!carProduct.getPrePayBoolean())
                        {
                            remarks.add("The value of PrePayBoolean is incorrect for GDSPCarItem=" + carItemID + ". PrePayBooleanInResp=" + carProduct.getPrePayBoolean());
                        }
                        break;
                    }
                    default:
                        remarks.add("Invalid car business model :" + carBusinessModel);
                        break;
                }
            } catch (DataAccessException e) {
                remarks.add(e);
            }
         }
         if (CollectionUtils.isEmpty(remarks))
         {
            isPassed = true;
         }
         if (!isPassed)
         {
             return  new VerificationResult(getName(), false, Arrays.asList(remarks.toString()));
         }
         return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }
}
