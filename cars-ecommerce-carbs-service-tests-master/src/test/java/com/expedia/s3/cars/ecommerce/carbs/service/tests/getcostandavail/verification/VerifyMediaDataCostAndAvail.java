package com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.MediaDataCommonVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.MediaDataCommonVerify.getMatchCarProductFromGDS;

/**
 * Created by miawang on 8/30/2016.
 */
@SuppressWarnings("PMD")
public class VerifyMediaDataCostAndAvail {
    @SuppressWarnings("CPD-START")
    public static void verifyMediaData(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext context, String bsClientGDSConfigValue, String acrissClientConfig) throws DataAccessException{
        final Document gdsMessageDoc = context.getSpooferTransactions();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        if(CompareUtil.isObjEmpty(input.getResponse())){
            Assert.fail("Response is null.");
        }
        final CarProductListType productListType= input.getResponse().getCarProductList();
        if(CompareUtil.isObjEmpty(productListType)
                || CompareUtil.isObjEmpty(productListType.getCarProduct())){
            Assert.fail("No car product returned, car product list is empty.");
        }
        final CarProductType productFromRsp = productListType.getCarProduct().get(0);
        final NodeList gdsRspNodeList = gdsMessageDoc.getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE);
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        //verify if titanium Car
        if(inventoryHelper.isSpecificProviderCar(productFromRsp,7)){
            final CarProductType productFromGDS = getMatchCarProductFromGDS(gdsRspNodeList,productFromRsp);
            if(productFromGDS == null){
                Assert.fail("No corresponding car product from GDS is returned.");
            }
            MediaDataCommonVerify.verifyMediaInfoCommon(productFromGDS,productFromRsp,bsClientGDSConfigValue,acrissClientConfig, false);
        }
    }


}
