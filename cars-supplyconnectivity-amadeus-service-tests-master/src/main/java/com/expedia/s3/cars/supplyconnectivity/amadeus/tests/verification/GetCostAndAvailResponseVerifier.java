package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.List;


/**
 * Created by fehu on 8/4/2016.
 */
public class GetCostAndAvailResponseVerifier implements IVerification {
    public static void isGetCostAndAvailWorksVerifier(CarSupplyConnectivityGetCostAndAvailabilityResponseType carSCSGetCostAndAvailResponse)
    {
        StringBuilder errorMsg = new StringBuilder("");
        if (null == carSCSGetCostAndAvailResponse)
        {
            errorMsg.append("No data return in response.");
        } else {
            if (null == carSCSGetCostAndAvailResponse.getCarProductList() || CollectionUtils.isEmpty(carSCSGetCostAndAvailResponse.getCarProductList().getCarProduct())) {
                errorMsg.append("No CarProduct return in response.");
            }
            if (null != carSCSGetCostAndAvailResponse.getErrorCollection()) {
                List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(carSCSGetCostAndAvailResponse.getErrorCollection(), "DescriptionRawText");
                if (descriptionRawTextList.size() > 0) {
                     errorMsg.append("Exist error in response. DescriptionRawText=");
                }
                for (String descriptionRawText : descriptionRawTextList) {
                    errorMsg.append(descriptionRawText);
                }
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }

    }

    public static void verifyCarLocationInfo(CarSupplyConnectivityGetCostAndAvailabilityRequestType request, CarSupplyConnectivityGetCostAndAvailabilityResponseType response) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder();
        if( null != response.getCarProductList() && !CollectionUtils.isEmpty(response.getCarProductList().getCarProduct())){
            final CarProductType productType = response.getCarProductList().getCarProduct().get(0);
            final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
            final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
            final String startCarVendorLocationCodeReturn = startLocationReturn.getCarLocationCategoryCode() + startLocationReturn.getSupplierRawText();
            final String endCarVendorLocationCodeReturn = endLocationReturn.getCarLocationCategoryCode() + endLocationReturn.getSupplierRawText();
            final CarProductType productTypeVerify = request.getCarProductList().getCarProduct().get(0);
            final CarLocationKeyType startLocationVerify = productTypeVerify.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
            final CarLocationKeyType endLocationVerify = productTypeVerify.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
            final String startCarVendorLocationCodeVerify = startLocationVerify.getCarLocationCategoryCode() + startLocationVerify.getSupplierRawText();
            final String endCarVendorLocationCodeVerify = endLocationVerify.getCarLocationCategoryCode() + endLocationVerify.getSupplierRawText();
            final boolean startLocationIdVerifyError1 = startLocationReturn.getCarVendorLocationID() == null && startLocationVerify.getCarVendorLocationID() != null ;
            final boolean startLocationIdVerifyError2 = startLocationReturn.getCarVendorLocationID() != null && !startLocationReturn.getCarVendorLocationID().equals(startLocationVerify.getCarVendorLocationID());
            final boolean startLocationInfoVerifyError = !startLocationReturn.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startCarVendorLocationCodeReturn.equals(startCarVendorLocationCodeVerify);
            final boolean endLocationIdVerifyError1 = endLocationReturn.getCarVendorLocationID() == null && endLocationVerify.getCarVendorLocationID() != null;
            final boolean endLocationIdVerifyError2 = endLocationReturn.getCarVendorLocationID() != null && !endLocationReturn.getCarVendorLocationID().equals(endLocationVerify.getCarVendorLocationID());
            final boolean endLocationInfoVerifyError = !endLocationReturn.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endCarVendorLocationCodeReturn.equals(endCarVendorLocationCodeVerify);
            if(startLocationIdVerifyError1 || startLocationIdVerifyError2 || startLocationInfoVerifyError)
            {
                errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
            }
            if(endLocationIdVerifyError1 || endLocationIdVerifyError2 ||endLocationInfoVerifyError)
            {
                errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
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

    public static void verifyLoyaltyInformation(CarSupplyConnectivityGetCostAndAvailabilityRequestType request, CarSupplyConnectivityGetCostAndAvailabilityResponseType response, boolean enableLoyalty) throws DataAccessException
    {
        StringBuilder errorMsg = new StringBuilder();

        if (null != response.getCarProductList() && !CollectionUtils.isEmpty(response.getCarProductList().getCarProduct()))
        {
            if(enableLoyalty)
            {
                if (null != request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate()
                        && null != response.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate()
                        && null != request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getLoyaltyProgram() &&
                        null != response.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getLoyaltyProgram())
                {

                    String loyaltyInRequest = request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode();

                    String loyaltyInResponse = response.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode();

                    Assert.assertTrue(loyaltyInRequest.equalsIgnoreCase(loyaltyInResponse));
                }
            }

            else
            {
                Assert.assertNotNull(response);

            }
        }
    }
}
