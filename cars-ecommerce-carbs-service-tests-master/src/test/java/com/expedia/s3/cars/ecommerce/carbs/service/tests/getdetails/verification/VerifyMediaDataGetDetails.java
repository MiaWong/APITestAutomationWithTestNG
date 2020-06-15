package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.MediaDataCommonVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
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
public class VerifyMediaDataGetDetails {

    private VerifyMediaDataGetDetails(){ }

    @SuppressWarnings("CPD-START")
    public static void verifyMediaData(GetDetailsVerificationInput input, BasicVerificationContext context, String bsClientGDSConfigValue) throws DataAccessException {
        final Document gdsMessageDoc = context.getSpooferTransactions();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        if (CompareUtil.isObjEmpty(input.getResponse())) {
            Assert.fail("Response is null.");
        }
        final CarProductListType productListType = input.getResponse().getCarProductList();
        if (CompareUtil.isObjEmpty(productListType)
                || CompareUtil.isObjEmpty(productListType.getCarProduct())) {
            Assert.fail("No car product returned, car product list is empty.");
        }
        final CarProductType productFromRsp = productListType.getCarProduct().get(0);
        final NodeList gdsRspNodeList = gdsMessageDoc.getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE);
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        //verify if titanium Car
        if (inventoryHelper.isSpecificProviderCar(productFromRsp, 7)) {
            final CarProductType productFromGDS = getMatchCarProductFromGDS(gdsRspNodeList, productFromRsp);
            if (productFromGDS == null) {
                Assert.fail("No corresponding car product from GDS is returned.");
            }

            MediaDataCommonVerify.verifyMediaInfoCommon(productFromGDS, productFromRsp, bsClientGDSConfigValue, null, false);

        }
    }

    public static void verifyMediaInfoForSearch (CarECommerceSearchResponseType carBSSearchResponse, boolean noImageReturned, String bsClientMediaConfigValue) throws DataAccessException {
        for (final CarSearchResultType carSearchResult : carBSSearchResponse.getCarSearchResultList().getCarSearchResult()) {
            for (final CarProductType carProduct : carSearchResult.getCarProductList().getCarProduct()) {
                if ("1".equals(bsClientMediaConfigValue) && carProduct.getCarCatalogMakeModel().getMediaID() > 0 && carSearchResult.getCarProductList().getCarProduct().size() != 1) {
                    Assert.fail("Search response shouldn't have MediaID greater than 0 or CarProduct count should be 1 when media config is ON, please check");
                    break;
                }
                if (noImageReturned && !carProduct.getCarCatalogMakeModel().getImageThumbnailFilenameString().isEmpty()) {
                    Assert.fail("ImageThumbnailFilenameString should be null in search response when media config is ON, please check");
                    break;
                }
                if ("0".equals(bsClientMediaConfigValue)) {
                    verifyImageThumbnailAndFileName(carProduct);
                }
            }
        }
    }

    public static void verifyMediaInfoForGetDetails(CarbsRequestGenerator requestGenerator, BasicVerificationContext context, String bsClientMediaConfigValue) throws DataAccessException {
        final Document spooferDoc = context.getSpooferTransactions();
        if (CompareUtil.isObjEmpty(requestGenerator.getGetDetailsResponseType()) || CompareUtil.isObjEmpty(spooferDoc)) {
            Assert.fail("GetDetails or Spoofer response is null, please check");
        } else {
            if ("1".equals(bsClientMediaConfigValue)) {
                final CarCatalogMakeModelType carCatalogMakeModel = requestGenerator.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0).getCarCatalogMakeModel();
                if (carCatalogMakeModel != null && (carCatalogMakeModel.getCarModelString() != null || carCatalogMakeModel.getImageFilenameString() != null
                        || carCatalogMakeModel.getImageThumbnailFilenameString() != null || carCatalogMakeModel.getMediaID() != 0)) {
                    Assert.fail("CarCatalogMakeModel should not exist in GetDetails response when media config is ON");
                }
                final NodeList spooferPictureSize = spooferDoc.getElementsByTagNameNS("*", "pictureSize");
                if (!spooferPictureSize.item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue().equals("7")
                        || !spooferPictureSize.item(1).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue().equals("4")) {
                    Assert.fail("Spoofer request doesn't have Picture size as 7 or 4");
                }
            } else {
                verifyImageThumbnailAndFileName(requestGenerator.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0));
            }
        }
    }

    public static void verifyImageThumbnailAndFileName(CarProductType carProduct) throws DataAccessException {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        final String mediaFileFromDB = carsInventoryDataSource.getMediaInfoByCarMediaID(carProduct.getCarCatalogMakeModel().getMediaID());
        final String mediaFileName = mediaFileFromDB.split("\\.")[0];
        final String mediaFileType = mediaFileFromDB.split("\\.")[1];
        if (!carProduct.getCarCatalogMakeModel().getImageFilenameString().contains(mediaFileName + "_s." + mediaFileType) || !carProduct.getCarCatalogMakeModel()
                .getImageThumbnailFilenameString().contains((mediaFileName + "_t." + mediaFileType))) {
            Assert.fail("ImageFilenameString and ImageThumbnailFilenameString are not as expected, please check.");
        }
    }
}



