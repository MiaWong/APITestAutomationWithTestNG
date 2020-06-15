package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.basetypes.defn.v4.ProductCategoryCodeListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.eclipse.jetty.util.StringUtil;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 9/12/2018.
 */
public class CarMIPAndPackageBooleanTests extends SuiteCommon {

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1029333OMSSupportCarMIPpostPurchaseTpackageF() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "1029333", PojoXmlUtil.getRandomGuid());
        testOMSReserveCarMIPAndPackageBoolean(testData,"ZE",true, false, "Car,Air");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1029334tfs563705OMSSupportCarMIPpostPurchasepackageF() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "1029333", PojoXmlUtil.getRandomGuid());
        testOMSReserveCarMIPAndPackageBoolean(testData,"ZE",true, true, "Car,Air");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //1053776 - Verify CarBS Search to reserve response CarInventoryKey include correct PostPurchaseBoolean and ProductCategoryCodeList when PostPurchase = true, packageboolean=false, productcodelist=air,car (CarMIP)
    public void tfs1053776IdentifyCarMIPAtBookpostPurchasepackageF() throws Exception
    {
        final TestData testData = new TestData(httpClient,CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "1053776", PojoXmlUtil.getRandomGuid());
        testOMSReserveCarMIPAndPackageBoolean(testData,"ZE", false, true, "Air,Hotel,Car");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //1053776 - Verify CarBS Search to reserve response CarInventoryKey include correct PostPurchaseBoolean and ProductCategoryCodeList when PostPurchase = true, packageboolean=false, productcodelist=air,car (CarMIP)
    public void tfs1053778IdentifyCarMIPAtBookpostPurchasepackageF() throws Exception
    {
        final TestData testData = new TestData(httpClient,CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "1053778", PojoXmlUtil.getRandomGuid());
        testOMSReserveCarMIPAndPackageBoolean(testData,"ZE", false, false, "Air,Hotel,Car");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs550356PackageBooleanFromIKStandalone() throws Exception
    {
        final TestData testData = new TestData(httpClient,CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(), "550356", PojoXmlUtil.getRandomGuid());
        testOMSReserveCarMIPAndPackageBoolean(testData,"", false, false, "Car");
    }

    private void  testOMSReserveCarMIPAndPackageBoolean(TestData testData, String vendorCode, boolean postPurchaseBoolean, boolean packageBoolean, String productCategoryList) throws Exception
    {
        testData.setGuid(PojoXmlUtil.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient), "CarMIP_booking"));
        //2.1 search original package
        if(StringUtil.isNotBlank(vendorCode))
        {
            CommonUtil.setVendor(testData, vendorCode);
        }
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        final CarProductType select_packgeCar = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData,request, response);

        //2.2 CarMIP search with postPurchase and packageboolean and productCategoryList
        final CarECommerceSearchRequestType requestCarMIP = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        requestCarMIP.getCarECommerceSearchStrategy().setPackageBoolean(packageBoolean);
        requestCarMIP.getCarECommerceSearchStrategy().setPostPurchaseBoolean(postPurchaseBoolean);
        setProductCategoryList(productCategoryList, requestCarMIP);
        //Cannot specify both PurchaseTypeMask and ProductCategoryCodeList.
        requestCarMIP.getCarECommerceSearchStrategy().setPurchaseTypeMask(null);
        final CarECommerceSearchResponseType responseForSearchMIP = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), requestCarMIP);


        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(requestCarMIP, responseForSearchMIP, testData);

        final List<CarProductType> carProductTypeList =  carbsSearchRequestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(responseForSearchMIP.getCarSearchResultList(), testData);
        final CarProductType carMIP_product_search = getCorrespondingCarProductType(packageBoolean, select_packgeCar, carProductTypeList);
        if (null == carMIP_product_search)
        {
            Assert.fail("No test carMIP car product matched with the same package car on searching");
        }
        carbsSearchRequestGenerator.setSelectedCarProduct(carMIP_product_search);


        //details
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        //Set different scenario for PackageBoolean verification
        //1)token exist and packageBoolean is removed from CarsInventoryKey 2) token not exist and pacakgeBoolean exist in CarsInventoryKey
        if(postPurchaseBoolean){
            getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarInventoryKey().setPackageBoolean(null);
        }
        else
        {
            getDetailsRequestType.getCarProductList().getCarProduct().get(0).setCarProductToken(null);
        }
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);


        //cost&avail
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(), getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);


        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(requestCarMIP, responseForSearchMIP);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carMIP_product_search);

        //OMReserve
        CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);

        final StringBuffer errorMSg = new StringBuffer("");

        //verify totalPrice
        verifyPackagePriceforCarMIPSearchAndBooking(errorMSg, testData, postPurchaseBoolean, packageBoolean, select_packgeCar, carMIP_product_search, carOMSReqAndRespObj);


        //verify if  postPurchaseBoolean/packageBoolean/productCategoryList map to value in CarInvetoryKey
        for(final CarSearchResultType carSearchResultType : responseForSearchMIP.getCarSearchResultList().getCarSearchResult())
        {
            for(final CarProductType carProductType : carSearchResultType.getCarProductList().getCarProduct())
            {
                verifyToken("search" ,postPurchaseBoolean, packageBoolean, productCategoryList, carProductType, errorMSg);
            }
        }
        verifyToken("getDetails" ,postPurchaseBoolean, packageBoolean, productCategoryList,  getDetailsResponseType.getCarProductList().getCarProduct().get(0), errorMSg);
        verifyToken("cost&avail" ,postPurchaseBoolean, packageBoolean, productCategoryList,  getCostAndAvailabilityResponse.getCarProductList().getCarProduct().get(0), errorMSg);
        verifyToken("getOrderProcess" ,postPurchaseBoolean, packageBoolean, productCategoryList,  carOMSReqAndRespObj.getGetOrderProcessResponseType().getOrderProductList()
                .getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct(), errorMSg);
        verifyToken("preparPurchase" ,postPurchaseBoolean, packageBoolean, productCategoryList,  carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct(), errorMSg);


        if (StringUtil.isNotBlank(errorMSg.toString()))
        {
            Assert.fail(errorMSg.toString());
        }

        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //For cancel, just PackageBoolean is returned
        verifyToken("preparChange" ,null, packageBoolean, null,  omsCancelReqAndRespObj.getPrepareChangeResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct(), errorMSg);

    }

    private void verifyToken(String transaction, Boolean postPurchaseBoolean, boolean packageBoolean, String productCategoryList,  CarProductType carProductType, StringBuffer errorMSg)
    {
        compareCarProductCategoryList(transaction, productCategoryList, carProductType, errorMSg);

        if(packageBoolean != carProductType.getCarInventoryKey().getPackageBoolean()
                || postPurchaseBoolean != carProductType.getCarInventoryKey().getPostPurchaseBoolean())

        {
            errorMSg.append(transaction + ": PackageBoolean/postPurchaseBoolean actual is " + carProductType.getCarInventoryKey().getPackageBoolean()
                    + "/"+ carProductType.getCarInventoryKey().getPostPurchaseBoolean()+ "  not expect " + packageBoolean + "/"+ postPurchaseBoolean + "\n");
        }
    }

    private void compareCarProductCategoryList(String transaction,String productCategoryList, CarProductType carProductType, StringBuffer errorMSg)
    {
        if(null == productCategoryList && null == carProductType.getCarInventoryKey().getProductCategoryCodeList())
        {
            return;
        }
        final List<String> expProductCategoryList = Arrays.asList(productCategoryList.split(","));
        if (expProductCategoryList.size() == carProductType.getCarInventoryKey().getProductCategoryCodeList().getProductCategoryCode().size())
        {
            int count = 0;
            for (final String productCategory : expProductCategoryList)
            {
                for (final String actProductCategory : carProductType.getCarInventoryKey().getProductCategoryCodeList().getProductCategoryCode())

                {
                    if (productCategory.equals(actProductCategory))
                    {
                        count++;
                        break;
                    }
                }
            }
            if (count != expProductCategoryList.size())
            {
                errorMSg.append(transaction + ": ProductCategoryCodeList is actual " + carProductType.getCarInventoryKey().getProductCategoryCodeList().getProductCategoryCode().toString() + " not expect " + expProductCategoryList.toString()+ "\n");
            }
        }
        else
        {
            errorMSg.append(transaction + ": expect ProductCategoryCodeList size " + expProductCategoryList.size() +" not equal to actProductCategoryCodeList size " + carProductType.getCarInventoryKey().getProductCategoryCodeList().getProductCategoryCode().size()+ "\n");
        }
    }

    private void verifyPackagePriceforCarMIPSearchAndBooking(StringBuffer errorMSg, TestData testData, boolean postPurchaseBoolean, boolean packageBoolean, CarProductType selectPackgeCar, CarProductType carMIProductSearch, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj)
    {
        // 1.search price verify
        //postPurchase false , package false , not check the price
        if (postPurchaseBoolean || packageBoolean)
        {
            checkCarMIPPrice(testData, selectPackgeCar, errorMSg, carMIProductSearch.getCostList(), "Match the cost failed for package and MIP car for search.");
        }


        //2.booking price verify
        final CarProductType carMIP_product_booking = carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();

        //postPurchase false , package false , not check the price
        if (postPurchaseBoolean || packageBoolean)
        {
            checkCarMIPPrice(testData, selectPackgeCar, errorMSg, carMIP_product_booking.getCostList(), "Match the cost failed for package and MIP car for booking.");
        }

    }

    private void checkCarMIPPrice(TestData testData, CarProductType selectPackgeCar, StringBuffer errorMSg, CostListType costList2, String str)
    {
        final BigDecimal packageTotalAmount = CostPriceCalculator.getCostAmountByFinanceCategoryCode(selectPackgeCar.getCostList(), testData.getScenarios().getSupplierCurrencyCode(), "Total");
        final BigDecimal mipTotalAmount = CostPriceCalculator.getCostAmountByFinanceCategoryCode(costList2, testData.getScenarios().getSupplierCurrencyCode(), "Total");
        if (!packageTotalAmount.equals(mipTotalAmount))
        {
            errorMSg.append(str);
        }
    }

    private CarProductType getCorrespondingCarProductType(boolean packageBoolean, CarProductType selectPackgeCar, List<CarProductType> carProductTypes)
    {

            for (final CarProductType cp : carProductTypes)
            {
                if (CarProductComparator.isCorrespondingCar(selectPackgeCar, cp, false, false))
                {
                    if (packageBoolean)
                    {
                        // the same carItem and SupplySubsetID
                        if (cp.getCarInventoryKey().getCarItemID().equals(selectPackgeCar.getCarInventoryKey().getCarItemID())
                                && cp.getCarInventoryKey().getSupplySubsetID().equals(selectPackgeCar.getCarInventoryKey().getSupplySubsetID()))
                        {

                           return cp;
                        }
                    }
                    else
                    {
                        // different carITem and SupplySubsetID , but locationCatgory should be same
                        final boolean locationPickUpEqual = CarNodeComparator.isCarLocationKeyEqual(selectPackgeCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey(),
                               cp.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
                        final boolean locationDropOffEqual = CarNodeComparator.isCarLocationKeyEqual(selectPackgeCar.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey(),
                                    cp.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey());
                        if(locationPickUpEqual && locationDropOffEqual)
                        {
                           return cp;
                        }
                    }
                }

        }
        return null;
    }

    private void setProductCategoryList(String productCategoryList, CarECommerceSearchRequestType request)
    {
        final ProductCategoryCodeListType productCategoryCodeListType = new ProductCategoryCodeListType();
        final List<String> productCategory = new ArrayList<>();
        final String[] productCategoryCode = productCategoryList.split(",");
        for (final String a : productCategoryCode)
        {
            productCategory.add(a);
        }
        productCategoryCodeListType.setProductCategoryCode(productCategory);
        request.getCarECommerceSearchStrategy().setProductCategoryCodeList(productCategoryCodeListType);
    }
}
