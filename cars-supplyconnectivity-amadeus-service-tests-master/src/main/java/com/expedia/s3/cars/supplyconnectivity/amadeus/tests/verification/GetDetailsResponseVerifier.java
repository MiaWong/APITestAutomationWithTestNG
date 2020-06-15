package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
//import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
//import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by mpaudel on 6/30/16.
 */
public class GetDetailsResponseVerifier implements IVerification {


    public  static void isGetDetailslWorksVerifier(CarSupplyConnectivityGetDetailsResponseType carSCSGetDetailResponse)
    {
        StringBuilder errorMsg = new StringBuilder("");
        if (null == carSCSGetDetailResponse)
        {
            errorMsg.append("No data return in response.");
        }
        else if (null == carSCSGetDetailResponse.getCarProductList() || CollectionUtils.isEmpty(carSCSGetDetailResponse.getCarProductList().getCarProduct()))
        {
            errorMsg.append("No CarProduct return in response.");
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    public static void verifyCarLocationInfo(CarSupplyConnectivityGetDetailsResponseType response, DataSource datasource) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder("");
        if( null != response.getCarProductList() && !CollectionUtils.isEmpty(response.getCarProductList().getCarProduct())){
            final CarProductType productType = response.getCarProductList().getCarProduct().get(0);
            final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
            final CarLocationKeyType startLocationReturn2 = productType.getCarPickupLocation().getCarLocationKey();
            final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
            final CarLocationKeyType endLocationReturn2 = productType.getCarDropOffLocation().getCarLocationKey();
            /*final String startCarVendorLocationCodeReturn = startLocationReturn.getCarLocationCategoryCode() + startLocationReturn.getSupplierRawText();
            final String startCarVendorLocationCodeReturn2 = startLocationReturn2.getCarLocationCategoryCode() + startLocationReturn2.getSupplierRawText();
            final String endCarVendorLocationCodeReturn = endLocationReturn.getCarLocationCategoryCode() + endLocationReturn.getSupplierRawText();
            final String endCarVendorLocationCodeReturn2 = endLocationReturn2.getCarLocationCategoryCode() + endLocationReturn2.getSupplierRawText();
            */
            if(null == startLocationReturn.getCarVendorLocationID() || 0L == startLocationReturn.getCarVendorLocationID() || null == startLocationReturn2.getCarVendorLocationID() || 0L == startLocationReturn2.getCarVendorLocationID()){
                errorMsg.append("No CarVendorLocationID returned in CarPickupLocationKey or CarPickupLocation!");
            }
            if(null == endLocationReturn.getCarVendorLocationID() || 0L == endLocationReturn.getCarVendorLocationID() || null == endLocationReturn2.getCarVendorLocationID() || 0L == endLocationReturn2.getCarVendorLocationID()){
                errorMsg.append("No CarVendorLocationID returned in CarDropOffLocationKey or CarDropOffLocation!");
            }
            /*final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(datasource);
            final CarVendorLocation startLocationVerify = carsInventoryHelper.getCarLocation(startLocationReturn.getCarVendorLocationID());
            final CarVendorLocation endLocationVerify = carsInventoryHelper.getCarLocation(endLocationReturn.getCarVendorLocationID());
            if(startLocationVerify == null){
                errorMsg.append("No matched pick up CarVendorLocation recode find in DB CarVendorLocationID!");
            }
            if(endLocationVerify == null){
                errorMsg.append("No matched drop off CarVendorLocation recode find in DB by CarVendorLocationID!");
            }
            if(!startLocationReturn.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startCarVendorLocationCodeReturn.equals(startLocationVerify.getCarLocationCategoryCode())
                    || !startLocationReturn2.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startCarVendorLocationCodeReturn2.equals(startLocationVerify.getCarLocationCategoryCode()))
            {
                errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
            }
            if(!endLocationReturn.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endCarVendorLocationCodeReturn.equals(endLocationVerify.getCarLocationCategoryCode())
                    || !endLocationReturn2.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endCarVendorLocationCodeReturn2.equals(endLocationVerify.getCarLocationCategoryCode()))
            {
                errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
            }*/
            //Meichun: 20180722 verify invalid locationis not returned
            if("XXX".equals(startLocationReturn.getLocationCode()) && "888".equals(startLocationReturn.getSupplierRawText())){
                errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
            }

            if("XXX".equals(endLocationReturn.getLocationCode()) && "888".equals(endLocationReturn.getSupplierRawText())){
                errorMsg.append("Wrong drop off  CarVendorLocationInfo returned!");
            }

        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }


    public static void verifyShuttleInformation(CarSupplyConnectivityGetDetailsResponseType response,boolean enableShuttleInformation)
    {
        Assert.assertNotNull(response);

        List<CarProductType> carProduct =  response.getCarProductList().getCarProduct();

        if(enableShuttleInformation)
        {
            Assert.assertFalse(StringUtils.isEmpty(carProduct.get(0).getCarPickupLocation().getCarShuttleCategoryCode()),"ShuttleInfo not mapped for CarProduct");
        }
        else
        {
            Assert.assertTrue(StringUtils.isEmpty(carProduct.get(0).getCarPickupLocation().getCarShuttleCategoryCode()));
        }
    }

    public static void verifyMapPhoneCategoryCode(CarSupplyConnectivityGetDetailsResponseType response, boolean mapPhoneCategoryCode)
    {
        Assert.assertNotNull(response);

        PhoneListType phoneListType = response.getCarProductList().getCarProduct().get(0).getCarPickupLocation()
                .getPhoneList();

        if(mapPhoneCategoryCode)
        {
            Assert.assertTrue((phoneListType.getPhone().get(0).getPhoneCategoryCode().equals("0"))
            || (phoneListType.getPhone().get(0).getPhoneCategoryCode().equals("6")));
        }

        else
        {
            Assert.assertEquals(phoneListType.getPhone().get(0).getPhoneCategoryCode(), null);
        }
    }

    public static void verifyLoyaltyInformation(CarSupplyConnectivityGetDetailsResponseType response, boolean enableLoyalty)
    {

        Assert.assertNotNull(response);

        CarRateType carRateType = response.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate();

        if(enableLoyalty)
        {
            Assert.assertFalse(StringUtils.isEmpty(carRateType.getLoyaltyProgram().getLoyaltyProgramMembershipCode()), "Loyalty Information is not mapped for CarProduct");
        }

        else
        {
            Assert.assertNotNull(carRateType);
        }
    }

    public static void verifyRateCode(CarSupplyConnectivityGetDetailsResponseType responseType, String rateCode, boolean shouldRateCodesMatch) {
        Assert.assertNotNull(responseType.getCarProductList());
        List<CarProductType> carProduct = responseType.getCarProductList().getCarProduct();
        Assert.assertNotNull(carProduct);
        Assert.assertEquals(carProduct.get(0).getCarInventoryKey().getCarRate().getRateCode().equals(rateCode), shouldRateCodesMatch);
    }
}
