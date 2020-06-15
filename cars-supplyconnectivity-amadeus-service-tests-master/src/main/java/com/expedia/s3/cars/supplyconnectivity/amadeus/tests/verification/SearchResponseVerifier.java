package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
//import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
//import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators.TestScenarios;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.util.List;



/**
 * Created by mpaudel on 5/18/16.
 */
@SuppressWarnings("PMD")
public class SearchResponseVerifier {

    public void setScenarios(TestScenarios scenarios) {
        this.scenarios = scenarios;
    }

    public void setResponse(CarSupplyConnectivitySearchResponseType response) {
        this.response = response;
    }

    public void setRequest(CarSupplyConnectivitySearchRequestType request) {
        this.request = request;
    }

    public void setDownstreamMessages(Document downstreamMessages) {
        this.downstreamMessages = downstreamMessages;
    }

    public SearchResponseVerifier(TestScenarios scenarios, CarSupplyConnectivitySearchRequestType request, CarSupplyConnectivitySearchResponseType response, Document downstreammessages) {
        this.scenarios = scenarios;
        this.request = request;
        this.response = response;
        this.downstreamMessages=downstreammessages;
    }

    private TestScenarios scenarios;
    private Document downstreamMessages;
    private CarSupplyConnectivitySearchRequestType request;
    private CarSupplyConnectivitySearchResponseType response;

    public static void verifyCarProductReturned(CarSupplyConnectivitySearchResponseType response)
    {
        //verify car product returned
        StringBuilder errorMsg = new StringBuilder("");
        boolean matchedCarReturned = false;
        if (null == response)
        {
            errorMsg.append("search Response is null.");
        }
        else
        {
            if (null == response.getCarSearchResultList() || CollectionUtils.isEmpty(response.getCarSearchResultList().getCarSearchResult()))
            {
                errorMsg.append("No SearchResult return in response.");
            } else {
                for (CarSearchResultType result : response.getCarSearchResultList().getCarSearchResult()) {
                    if (null != result.getCarProductList()
                            && null != result.getCarProductList().getCarProduct()
                            && result.getCarProductList().getCarProduct().size() > 0) {
                        matchedCarReturned = true;
                        break;
                    }
                }
            }
            if (!matchedCarReturned)
                errorMsg.append("No Car returned in CarSCS response.");

            if (null != response.getErrorCollectionList() && null != response.getErrorCollectionList().getErrorCollection()) {
                List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(response.getErrorCollectionList().getErrorCollection(), "DescriptionRawText");
                if (!CollectionUtils.isEmpty(descriptionRawTextList)) {
                    errorMsg.append("ErrorCollection is present in response");
                    descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
                }
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    public static void verifyExistsResponseError(SearchVerificationInput input){
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        if(input.getResponse().getCarSearchResultList() != null && !CollectionUtils.isEmpty(input.getResponse().getCarSearchResultList().getCarSearchResult())) {
            for (CarSearchResultType result : input.getResponse().getCarSearchResultList().getCarSearchResult()) {
                if (null != result.getCarProductList()
                        && null != result.getCarProductList().getCarProduct()
                        && result.getCarProductList().getCarProduct().size() > 0) {
                    Assert.fail("Car returned error,except no car return!");
                    break;
                }
            }
        }
        if (null == input.getResponse().getErrorCollectionList()) {
            Assert.fail("No error collection return in response.");
        }else{
            final List<ErrorCollectionType>  errorCollectionTypeList = input.getResponse().getErrorCollectionList().getErrorCollection();
            if (!CollectionUtils.isEmpty(errorCollectionTypeList)) {
                for(ErrorCollectionType errorCollectionType : errorCollectionTypeList) {
                    if(errorCollectionType.getFieldInvalidErrorList() == null || CollectionUtils.isEmpty(errorCollectionType.getFieldInvalidErrorList().getFieldInvalidError())) {
                        Assert.fail("No expected error return in response.");
                        break;
                    }
                }
            }
        }

    }

    public static void verifyCarLocationInfo(CarSupplyConnectivitySearchResponseType response, DataSource datasource,String ignoreFlag) throws DataAccessException{
        StringBuilder errorMsg = new StringBuilder("");
        final CarSearchResultListType carSearchResultListType = response.getCarSearchResultList();
        if(!CollectionUtils.isEmpty(carSearchResultListType.getCarSearchResult())){
            for(CarSearchResultType resultType : carSearchResultListType.getCarSearchResult()){
                if(null != resultType.getCarProductList() && !CollectionUtils.isEmpty(resultType.getCarProductList().getCarProduct())){
                    for(CarProductType productType : resultType.getCarProductList().getCarProduct()){
                        final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
                        final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
                        //final String startCarVendorLocationCodeReturn = startLocationReturn.getCarLocationCategoryCode() + startLocationReturn.getSupplierRawText();
                        //final String endCarVendorLocationCodeReturn = endLocationReturn.getCarLocationCategoryCode() + endLocationReturn.getSupplierRawText();
                        //when carlocationId and location code are both invalid,ignore this error
                        if("1".equals(ignoreFlag) && "XXX".equals(startLocationReturn.getLocationCode())){
                            continue;
                        }
                        if( null == startLocationReturn.getCarVendorLocationID() || 0L == startLocationReturn.getCarVendorLocationID()){
                            errorMsg.append("No CarVendorLocationID returned in CarPickupLocationKey!");
                            break;
                        }
                        if( null == endLocationReturn.getCarVendorLocationID() || 0L == endLocationReturn.getCarVendorLocationID()){
                            errorMsg.append("No CarVendorLocationID returned in CarDropOffLocationKey!");
                            break;
                        }
                        /*final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(datasource);
                        final CarVendorLocation startLocationVerify = carsInventoryHelper.getCarLocation(startLocationReturn.getCarVendorLocationID());
                        final CarVendorLocation endLocationVerify = carsInventoryHelper.getCarLocation(endLocationReturn.getCarVendorLocationID());
                        if(startLocationVerify == null){
                            errorMsg.append("No matched pick up CarVendorLocation recode find in DB CarVendorLocationID!");
                            break;
                        }
                        if(endLocationVerify == null){
                            errorMsg.append("No matched drop off CarVendorLocation recode find in DB by CarVendorLocationID!");
                            break;
                        }
                        if(!startLocationReturn.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startCarVendorLocationCodeReturn.equals(startLocationVerify.getCarLocationCategoryCode())){
                            errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
                            break;
                        }
                        if(!endLocationReturn.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endCarVendorLocationCodeReturn.equals(endLocationVerify.getCarLocationCategoryCode())){
                            errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
                            break;
                        }*/

                        //Meichun: 20180722 verify invalid locationis not returned
                        if("XXX".equals(startLocationReturn.getLocationCode()) && "888".equals(startLocationReturn.getSupplierRawText())){
                            errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
                            break;
                        }

                        if("XXX".equals(endLocationReturn.getLocationCode()) && "888".equals(endLocationReturn.getSupplierRawText())){
                            errorMsg.append("Wrong drop off  CarVendorLocationInfo returned!");
                            break;
                        }

                    }
                    if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
                    {
                        break;
                    }
                }
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }

    }

    public static void verifyShuttleInfo(CarSupplyConnectivitySearchResponseType response, boolean enableShuttleInfo)
    {
        Assert.assertNotNull(response);

        long unMappedShuttleInfoCount = 0;

        for( CarSearchResultType carSearchResultType : response.getCarSearchResultList().getCarSearchResult())
        {
            unMappedShuttleInfoCount += carSearchResultType.getCarProductList().getCarProduct()
                    .stream().filter(carProduct-> StringUtils.isEmpty(carProduct.getCarPickupLocation().getCarShuttleCategoryCode())).count();
        }

        if(enableShuttleInfo)
        {
            // All carproducts should have valid shuttle information mapped
            Assert.assertEquals(unMappedShuttleInfoCount, 0, "Shuttle information not mapped for all car products");
        }
        else
        {
            int totalCarProducts = response.getCarSearchResultList().getCarSearchResult()
                                  .stream().mapToInt(carProductList-> carProductList.getCarProductList()
                                   .getCarProduct().size()).sum();

           // No car products should have the shuttle infromation mapped
            Assert.assertEquals(unMappedShuttleInfoCount, totalCarProducts);
        }

    }

    public void verifyMappings()
    {
        //you have all the downstream messages
        //you have SCS request and response
        //validate any/all mappings you want to verify here
    }

    public void verifyMileageInfoIsPresent()
    {
        //verify CarMileage node is present
    }

    public void verifyCDCodeIsApplied()
    {
        //verify CD Code is applied
    }



}
