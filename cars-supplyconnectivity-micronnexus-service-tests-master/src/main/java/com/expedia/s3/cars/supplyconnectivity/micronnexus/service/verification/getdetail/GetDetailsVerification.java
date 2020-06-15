package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail;


import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail.CostAndAvailVerification;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Created by asharma1 on 8/12/2016.
 */
@SuppressWarnings("PMD")
public class GetDetailsVerification implements IGetDetailsVerification {
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarProductList());
        Assert.assertNotNull(input.getResponse().getCarProductList().getCarProduct());

        //invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(), Arrays.asList(new VerifyGetDetailsBasic()));

        return verifications.verify(input, verificationContext);
    }

    public static void verifyCarLocationInfo(BasicVerificationContext basicVerificationContext,CarSupplyConnectivityGetDetailsResponseType response, DataSource scsDataSource) throws DataAccessException {
        CostAndAvailVerification.verifySpooferReqCarLocationInfo(basicVerificationContext,scsDataSource);
        verifySCSResCarLocationInfo(response, scsDataSource);

    }


    private static void verifySCSResCarLocationInfo(CarSupplyConnectivityGetDetailsResponseType response, DataSource scsDataSource) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder("");
        if( null != response.getCarProductList() && !CollectionUtils.isEmpty(response.getCarProductList().getCarProduct())){
            final CarProductType productType = response.getCarProductList().getCarProduct().get(0);
            CostAndAvailVerification.verifyLocation(errorMsg, productType, scsDataSource);


        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }
}
