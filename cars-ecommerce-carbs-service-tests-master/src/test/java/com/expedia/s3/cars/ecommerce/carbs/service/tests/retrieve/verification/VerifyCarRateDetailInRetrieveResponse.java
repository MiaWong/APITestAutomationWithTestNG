package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarRateDetailCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by fehu on 11/23/2016.
 */
public class VerifyCarRateDetailInRetrieveResponse implements IVerification<RetrieveVerificationInput, BasicVerificationContext> {
    DataSource carsInventoryDatasource;

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    //compare the car.
    @Override
    public VerificationResult verify(RetrieveVerificationInput input, BasicVerificationContext verificationContext) throws Exception {

        final CarProductType carInResponse = input.getResponse().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();

        boolean isPassed = false;
        final ArrayList remarks = new ArrayList();

        final CarRateDetailCommonVerifier CCLCommonVerifier = new CarRateDetailCommonVerifier();
        if (carsInventoryDatasource == null)
        {
            remarks.add("carsInventoryDatasource is Null, need initial first");
        } else
        {
            //input.getCarProductType()----select the car that booked in request compare to car in retrieve response,

                //retrieve no downstream,for GDS response car,send null.
                CCLCommonVerifier.verifyCarRateDetail(carsInventoryDatasource, null,

                        input.getCarProductType(), carInResponse, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RETRIEVE_VERIFICATION_PROMPT, true);

        }
        if (CollectionUtils.isEmpty(remarks))
        {
            isPassed = true;
        }

        return new VerificationResult(getName(), isPassed, remarks);
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}