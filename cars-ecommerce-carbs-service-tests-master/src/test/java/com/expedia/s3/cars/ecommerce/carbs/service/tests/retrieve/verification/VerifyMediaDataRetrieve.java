package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.MediaDataCommonVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import expedia.om.supply.messages.defn.v1.BookedItemType;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;

import static com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.MediaDataCommonVerify.getMatchCarProductFromGDS;

/**
 * Created by miawang on 8/30/2016.
 */
@SuppressWarnings("PMD")
public class VerifyMediaDataRetrieve {

    public static void verifyMediaData(RetrieveVerificationInput input, BasicVerificationContext context, String bsClientGDSConfigValue, String acrissClientConfig) throws DataAccessException{
        final Document gdsMessageDoc = context.getSpooferTransactions();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        if(CompareUtil.isObjEmpty(input.getResponse())){
            Assert.fail("Response is null.");
        }

        @SuppressWarnings("CPD-START")
        final List<BookedItemType> bookedItemList = input.getResponse().getBookedItemList().getBookedItem();
        if(CompareUtil.isObjEmpty(bookedItemList)
                || CompareUtil.isObjEmpty(bookedItemList.get(0).getItemData().getCarOfferData())
                || CompareUtil.isObjEmpty(bookedItemList.get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct())){
            Assert.fail("No car product returned, car product list is empty.");
        }
        final NodeList gdsRspNodeList = gdsMessageDoc.getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE);
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final CarProductType productFromRsp = bookedItemList.get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();
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
