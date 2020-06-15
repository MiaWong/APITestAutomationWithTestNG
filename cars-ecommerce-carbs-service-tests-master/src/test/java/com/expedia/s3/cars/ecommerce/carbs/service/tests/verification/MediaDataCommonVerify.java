package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.junit.Assert;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yyang4 on 5/9/2018.
 */
@SuppressWarnings("PMD")
public class MediaDataCommonVerify {

    public static void verifyMediaInfoCommon(CarProductType productFromGDS, CarProductType productFromResp, String gdsClientconfig, String acrissClientConfig, boolean forSearch) throws DataAccessException {
        //a.BS with GDS feature on / BS with ACRISS feature on
        //1.Verify if GDS value is not null then CarBS media info is from GDS for which TSCS returned
        //2.Verify if GDS media info value is null and media id is 0 then CarBS media info value is null
        //3.Verify if GDS media info value is null and media id > 0 then CarBS media info is from DB
        //4.Verify ACRISS info is from DB.

        //b.BS with GDS feature on / BS with ACRISS feature off
        //Verify the same step 1 to 3 as above.
        //Verify ACRISS info is null.
        if(!CompareUtil.isObjEmpty(productFromResp.getCarCatalogMakeModel())) {
            if ("1".equals(gdsClientconfig)) {
                verifyMediaInfoFeatureOn(productFromGDS, productFromResp);
            } else {
                verifyMediaInfoFeatureOff(productFromResp);
            }

            if(forSearch)
            verifyACRISSInfo(productFromResp, acrissClientConfig);
        }

    }

    private static void verifyMediaInfoFeatureOn(CarProductType productFromGDS, CarProductType productFromResp) throws DataAccessException {

        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        // verify media info is from gds
        //1.verify carModelGuaranteedBoolean
        if (productFromGDS.getCarCatalogMakeModel().isCarModelGuaranteedBoolean() != productFromResp.getCarCatalogMakeModel().isCarModelGuaranteedBoolean()) {
            Assert.fail(String.format("carModelGuaranteedBoolean in CarBS response is not equal with GDS response,CarBS is %s,GDS is %s ",
                    productFromResp.getCarCatalogMakeModel().isCarModelGuaranteedBoolean(),
                    productFromGDS.getCarCatalogMakeModel().isCarModelGuaranteedBoolean()));
        }

        final CarCatalogMakeModelType makeModelGDS = productFromGDS.getCarCatalogMakeModel();
        final CarCatalogMakeModelType makeModelRsp = productFromResp.getCarCatalogMakeModel();
        CarCatalogMakeModelType makeModelDB = null;
        if (makeModelRsp.getMediaID() > 0) {
            makeModelDB = inventoryHelper.getMakeModelInfo(makeModelRsp.getMediaID());
        }
        //2.verify CarCapacityAdultCount
        if (makeModelGDS.getCarCapacityAdultCount() > 0) {
            //if gds value is not null then compare CarBS media info from GDS
            if (makeModelRsp.getCarCapacityAdultCount() != makeModelGDS.getCarCapacityAdultCount()) {
                Assert.fail(String.format("CarCapacityAdultCount in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarCapacityAdultCount(), makeModelGDS.getCarCapacityAdultCount()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (makeModelRsp.getCarCapacityAdultCount() != makeModelDB.getCarCapacityAdultCount()) {
                Assert.fail(String.format("CarCapacityAdultCount in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarCapacityAdultCount(), makeModelDB.getCarCapacityAdultCount()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (makeModelRsp.getCarCapacityAdultCount() > 0) {
                Assert.fail(String.format("CarCapacityAdultCount in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarCapacityAdultCount()));
            }
        }

        //3.verify CarCapacityChildCount
        if (makeModelGDS.getCarCapacityChildCount() > 0) {
            //if gds value is not null then compare CarBS media info from GDS
            if (makeModelRsp.getCarCapacityChildCount() != makeModelGDS.getCarCapacityChildCount()) {
                Assert.fail(String.format("CarCapacityChildCount in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarCapacityChildCount(), makeModelGDS.getCarCapacityChildCount()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (makeModelRsp.getCarCapacityChildCount() != makeModelDB.getCarCapacityChildCount()) {
                Assert.fail(String.format("CarCapacityChildCount in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarCapacityChildCount(), makeModelDB.getCarCapacityChildCount()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (makeModelRsp.getCarCapacityChildCount() > 0) {
                Assert.fail(String.format("CarCapacityChildCount in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarCapacityChildCount()));
            }
        }

        //4.verify CarMinDoorCount
        if (makeModelGDS.getCarMinDoorCount() > 0) {
            //if gds value is not null then compare CarBS media info from GDS
            if (makeModelRsp.getCarMinDoorCount() != makeModelGDS.getCarMinDoorCount()) {
                Assert.fail(String.format("CarMinDoorCount in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarMinDoorCount(), makeModelGDS.getCarMinDoorCount()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (makeModelRsp.getCarMinDoorCount() != makeModelDB.getCarMinDoorCount()) {
                Assert.fail(String.format("CarMinDoorCount in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarMinDoorCount(), makeModelDB.getCarMinDoorCount()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (makeModelRsp.getCarMinDoorCount() > 0) {
                Assert.fail(String.format("CarMinDoorCount in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarMinDoorCount()));
            }
        }

        //4.verify CarMaxDoorCount
        if (makeModelGDS.getCarMaxDoorCount() > 0) {
            //if gds value is not null then compare CarBS media info from GDS
            if (makeModelRsp.getCarMaxDoorCount() != makeModelGDS.getCarMaxDoorCount()) {
                Assert.fail(String.format("CarMaxDoorCount in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarMaxDoorCount(), makeModelGDS.getCarMaxDoorCount()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (makeModelRsp.getCarMaxDoorCount() != makeModelDB.getCarMaxDoorCount()) {
                Assert.fail(String.format("CarMaxDoorCount in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarMaxDoorCount(), makeModelDB.getCarMaxDoorCount()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (makeModelRsp.getCarMaxDoorCount() > 0) {
                Assert.fail(String.format("CarMaxDoorCount in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarMaxDoorCount()));
            }
        }

        //5.verify carCapacitySmallLuggageCount
        if (makeModelGDS.getCarCapacitySmallLuggageCount() > 0) {
            //if gds value is not null then compare CarBS media info from GDS
            if (makeModelRsp.getCarCapacitySmallLuggageCount() != makeModelGDS.getCarCapacitySmallLuggageCount()) {
                Assert.fail(String.format("carCapacitySmallLuggageCount in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarCapacitySmallLuggageCount(), makeModelGDS.getCarCapacitySmallLuggageCount()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (makeModelRsp.getCarCapacitySmallLuggageCount() != makeModelDB.getCarCapacitySmallLuggageCount()) {
                Assert.fail(String.format("carCapacitySmallLuggageCount in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarCapacitySmallLuggageCount(), makeModelDB.getCarCapacitySmallLuggageCount()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (makeModelRsp.getCarCapacitySmallLuggageCount() > 0) {
                Assert.fail(String.format("carCapacitySmallLuggageCount in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarCapacitySmallLuggageCount()));
            }
        }

        //6.verify carCapacityLargeLuggageCount
        if (makeModelGDS.getCarCapacityLargeLuggageCount() > 0) {
            //if gds value is not null then compare CarBS media info from GDS
            if (makeModelRsp.getCarCapacityLargeLuggageCount() != makeModelGDS.getCarCapacityLargeLuggageCount()) {
                Assert.fail(String.format("carCapacityLargeLuggageCount in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarCapacityLargeLuggageCount(), makeModelGDS.getCarCapacityLargeLuggageCount()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (makeModelRsp.getCarCapacityLargeLuggageCount() != makeModelDB.getCarCapacityLargeLuggageCount()) {
                Assert.fail(String.format("carCapacityLargeLuggageCount in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarCapacityLargeLuggageCount(), makeModelDB.getCarCapacityLargeLuggageCount()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (makeModelRsp.getCarCapacityLargeLuggageCount() > 0) {
                Assert.fail(String.format("carCapacityLargeLuggageCount in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarCapacityLargeLuggageCount()));
            }
        }

        //7.verify imageFilenameString
        if (!CompareUtil.isObjEmpty(makeModelGDS.getImageFilenameString())) {
            //if gds value is not null then compare CarBS media info from GDS
            if (!makeModelRsp.getImageFilenameString().equals(makeModelGDS.getImageFilenameString())) {
                Assert.fail(String.format("imageFilenameString in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getImageFilenameString(), makeModelGDS.getImageFilenameString()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (!makeModelRsp.getImageFilenameString().equals(makeModelDB.getImageFilenameString())) {
                Assert.fail(String.format("imageFilenameString in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getImageFilenameString(), makeModelDB.getImageFilenameString()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (!CompareUtil.isObjEmpty(makeModelRsp.getImageFilenameString())) {
                Assert.fail(String.format("imageFilenameString in CarBS response is not correct, expect null actual %s",
                        makeModelRsp.getImageFilenameString()));
            }
        }

        //8.verify ImageThumbnailFilenameString
        if (!CompareUtil.isObjEmpty(makeModelGDS.getImageThumbnailFilenameString())) {
            //if gds value is not null then compare CarBS media info from GDS
            if (!makeModelRsp.getImageThumbnailFilenameString().equals(makeModelGDS.getImageThumbnailFilenameString())) {
                Assert.fail(String.format("ImageThumbnailFilenameString in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getImageThumbnailFilenameString(), makeModelGDS.getImageThumbnailFilenameString()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (!makeModelRsp.getImageThumbnailFilenameString().equals(makeModelDB.getImageThumbnailFilenameString())) {
                Assert.fail(String.format("ImageThumbnailFilenameString in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getImageThumbnailFilenameString(), makeModelDB.getImageThumbnailFilenameString()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (!CompareUtil.isObjEmpty(makeModelRsp.getImageThumbnailFilenameString())) {
                Assert.fail(String.format("ImageThumbnailFilenameString in CarBS response is not correct, expect null actual %s",
                        makeModelRsp.getImageThumbnailFilenameString()));
            }
        }

        //10.verify carMakeString
        if (!CompareUtil.isObjEmpty(makeModelGDS.getCarMakeString())) {
            //if gds value is not null then compare CarBS media info from GDS
            if (!makeModelRsp.getCarMakeString().equals(makeModelGDS.getCarMakeString())) {
                Assert.fail(String.format("carMakeString in CarBS response is not equal with GDS response , CarBS is %s,GDS is %s",
                        makeModelRsp.getCarMakeString(), makeModelGDS.getCarMakeString()));
            }
        } else if (makeModelRsp.getMediaID() > 0) {
            //if gds value is null and CarBS response mediaID >0 then compare CarBS media info from DB
            if (!makeModelRsp.getCarMakeString().equals(makeModelDB.getCarMakeString())) {
                Assert.fail(String.format("carMakeString in CarBS response is not equal with DB , CarBS is %s,DB is %s",
                        makeModelRsp.getCarMakeString(), makeModelDB.getCarMakeString()));
            }
        } else if (makeModelRsp.getMediaID() == 0) {
            //if gds value is null and CarBS response mediaId =0 then CarBS media info should be null
            if (!CompareUtil.isObjEmpty(makeModelRsp.getCarMakeString())) {
                Assert.fail(String.format("carMakeString in CarBS response is not correct, expect 0 actual %s",
                        makeModelRsp.getCarMakeString()));
            }
        }

    }

    private static void verifyACRISSInfo(CarProductType productFromResp, String acrissClientConfig) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final List<String> needVerifyFields = new ArrayList<String>(Arrays.asList("ACRISSCategoryCode", "ACRISSTypeCode",
                "ACRISSTransmissionDriveCode", "ACRISSFuelACCode"));
        final StringBuilder errorMsg = new StringBuilder();
        if ("1".equals(acrissClientConfig)) {
            final CarCatalogMakeModelType acrissInfoDB = inventoryHelper.getACRISSInfo(productFromResp.getCarInventoryKey().getCarCatalogKey().getCarVehicle());
            errorMsg.append("Compare DB ACRISSInfo with CarBS Response.. ");
            if (!CompareUtil.compareObjectOnlyForNeedField(acrissInfoDB, productFromResp.getCarCatalogMakeModel(), needVerifyFields, errorMsg)) {
                Assert.fail(errorMsg.toString());
            }
        } else {
            final CarCatalogMakeModelType acrissInfoNull = new CarCatalogMakeModelType();
            errorMsg.append("Verify ACRISSInfo if is null.. ");
            if (!CompareUtil.compareObjectOnlyForNeedField(acrissInfoNull, productFromResp.getCarCatalogMakeModel(), needVerifyFields, errorMsg)) {
                Assert.fail(errorMsg.toString());
            }
        }
    }

    private static void verifyMediaInfoFeatureOff(CarProductType productFromResp) throws DataAccessException {
        final List<String> needVerifyFields = new ArrayList<String>(Arrays.asList(
                "carMakeString", "carCapacityAdultCount", "carCapacityChildCount", "carMinDoorCount",
                "carMaxDoorCount", "carCapacitySmallLuggageCount", "carCapacityLargeLuggageCount",
                "imageFilenameString", "imageThumbnailFilenameString"));
        final StringBuilder errorMsg = new StringBuilder();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final CarCatalogMakeModelType makeModelRsp = productFromResp.getCarCatalogMakeModel();
        //compare data with DB
        if (makeModelRsp.getMediaID() > 0) {
            final CarCatalogMakeModelType makeModelDB = inventoryHelper.getMakeModelInfo(makeModelRsp.getMediaID());
            errorMsg.append("Compare DB media Info  with CarBS Response.. ");
            if (!CompareUtil.compareObjectOnlyForNeedField(makeModelDB, productFromResp.getCarCatalogMakeModel(), needVerifyFields, errorMsg)) {
                Assert.fail(errorMsg.toString());
            }
        } else {
            final CarCatalogMakeModelType acrissInfoNull = new CarCatalogMakeModelType();
            errorMsg.append("Verify media Info if is null.. ");
            if (!CompareUtil.compareObjectOnlyForNeedField(acrissInfoNull, productFromResp.getCarCatalogMakeModel(), needVerifyFields, errorMsg)) {
                Assert.fail(errorMsg.toString());
            }
        }


    }

    public static CarProductType getMatchCarProductFromGDS(NodeList gdsRspNodeList, CarProductType productRsp) throws DataAccessException {
        for(int i=0;i<gdsRspNodeList.getLength();i++){
            final TVARRsp tvarRsp = new TVARRsp(gdsRspNodeList.item(i), new CarsSCSDataSource(DatasourceHelper.getTitaniumDatasource()), false, false);
            if(tvarRsp != null && !CompareUtil.isObjEmpty(tvarRsp.getCarProduct())
                    && !CompareUtil.isObjEmpty(tvarRsp.getCarProduct().getCarProduct()) ){
                //get corresponding car from gds
                for(CarProductType carProductGDS : tvarRsp.getCarProduct().getCarProduct()){
                    if(CarProductComparator.isCorrespondingCar(productRsp, carProductGDS)){
                        return carProductGDS;
                    }
                }
            }
        }
        return null;
    }

}
