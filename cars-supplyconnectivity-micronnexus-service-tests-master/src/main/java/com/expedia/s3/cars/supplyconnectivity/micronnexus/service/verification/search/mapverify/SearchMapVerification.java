package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.search.mapverify;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARRsp;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

import java.util.Collections;

/**
 * Created by fehu on 1/3/2017.
 */
public class SearchMapVerification implements ISearchVerification {

    @SuppressWarnings("PMD")
    public static void AssertMNSCSSearchMessage(CarSupplyConnectivitySearchResponseType responseType, VARRsp varRsp, String verifyType)
    {
        //Loop vehicles in mnRsp, then verify nodes in MNSCS search response according to mnRsp
        for (final CarProductType carProductGDS: varRsp.getCarProductList().getCarProduct())
        {
            //Loop car products in MNSCS response
            for(final CarProductType car : responseType.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct())
            {
                //In spoofer_Search,every car have the same Context_ID for same car type
                if (car.getCarInventoryKey().getCarRate().getCarRateQualifierCode().equals(carProductGDS.getCarInventoryKey().getCarRate().getCarRateQualifierCode())
                        && car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()== carProductGDS.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID())
                {
                    //Verify PickupLocationCode in MNSCS response is identical with MN response
                    if (!car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode().equals(carProductGDS.getCarInventoryKey()
                            .getCarCatalogKey().getCarPickupLocationKey().getLocationCode())
                            && "LocationCode".equals(verifyType)) {
                        Assert.fail("PickupLocation(" + car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode() +" ) in MNSCS response is not identical with the PickupLocation("+
                                carProductGDS.getCarInventoryKey()
                                        .getCarCatalogKey().getCarPickupLocationKey().getLocationCode() +") in MN repsonse.");
                    }
                    //Verify DropoffLocationCode in MNSCS response is identical with MN response
                    if (!car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode().equals(carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode())
                            && "LocationCode".equals(verifyType)) {
                        Assert.fail("DropOffLocation("+ car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode() +") in MNSCS response is not identical with the DropOffLocation(" + carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode() +") in MN repsonse."
                        );
                    }


                    //Verify VendorCode in MNSCS response is identical with MN response
                    if (car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()!= carProductGDS.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() && verifyType.equals("VendorSupplierID")) {
                        Assert.fail("vendorSupplierID("+ car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() +") in MNSCS response is not identical with the VendorCode(" + carProductGDS.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() + ") in MN repsonse."
                        );
                    }

                    //Verify RateCode in MNSCS response is identical with MN response
                    if (car.getCarInventoryKey().getCarRate().getRateCode() != null && !car.getCarInventoryKey().getCarRate().getRateCode().equals(carProductGDS.getCarInventoryKey().getCarRate().getRateCode()) && verifyType.equals("RateCode")) {
                        Assert.fail("RateCode(" + car.getCarInventoryKey().getCarRate().getRateCode() + ") in MNSCS response is not identical with the RateCode("+ carProductGDS.getCarInventoryKey().getCarRate().getRateCode()+") in MN repsonse."
                        );
                    }

                    //Verify PickupDateTime in MNSCS response is identical with MN response
                    if (!car.getCarInventoryKey().getCarPickUpDateTime().getDateTimeString().substring(0,19).equals(carProductGDS.getCarInventoryKey().getCarPickUpDateTime().getDateTimeString()) && verifyType.equals("PickupDropoffTime")) {
                        Assert.fail("PickupDateTime("+ car.getCarInventoryKey().getCarPickUpDateTime().getDateTimeString() +") in MNSCS response is not identical with the PickupDateTime(" + carProductGDS.getCarInventoryKey().getCarPickUpDateTime().getDateTimeString() + ") in MN repsonse."
                        );
                    }

                    //Verify DropoffDateTime in MNSCS response is identical with MN response
                    if (!car.getCarInventoryKey().getCarDropOffDateTime().getDateTimeString().substring(0,19).equals(carProductGDS.getCarInventoryKey().getCarDropOffDateTime().getDateTimeString()) && verifyType.equals("PickupDropoffTime")) {
                        Assert.fail("DropoffDateTime("+ car.getCarInventoryKey().getCarDropOffDateTime().getDateTimeString() +") in MNSCS response is not identical with the DropoffDateTime("+ carProductGDS.getCarInventoryKey().getCarDropOffDateTime().getDateTimeString() +") in MN repsonse.");
                    }

                    //Verify CarVehicle in MNSCS response is identical with MN response
                    boolean sippEqual = car.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()
                            == carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode();
                    sippEqual = sippEqual && car.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode()
                            == carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode();
                    sippEqual = sippEqual && car.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode()
                            == carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode();
                    sippEqual = sippEqual && car.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()
                            == carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode();


                    if ( !sippEqual && "CarVehicle".equals(verifyType)) {
                        Assert.fail("CarVehicle("+ car.getCarInventoryKey().getCarCatalogKey().getCarVehicle() +") in MNSCS response is not identical with the CarVehicle("+ carProductGDS.getCarInventoryKey().getCarCatalogKey().getCarVehicle() +") in MN repsonse.");
                    }
                    //verify CostList for MNSCS response and MN response
                    if(verifyType.equals("CostList")){
                        StringBuilder errorMsg = new StringBuilder();
                        CarNodeComparator.isCostListEqual(car.getCostList(), carProductGDS.getCostList(), errorMsg, Collections.singletonList(CarTags.LEGACY_FINANCE_KEY));
                        if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
                            Assert.fail("CostList in MNSCS response is not identical with CostList in MN response: " + errorMsg.toString());
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext) {
        return null;
    }
}