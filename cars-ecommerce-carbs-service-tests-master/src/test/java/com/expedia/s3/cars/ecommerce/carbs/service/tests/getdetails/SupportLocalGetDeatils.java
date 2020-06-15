package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification.VerifyPriceListInGetDetailsResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.CarbsCommonVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarsInventoryKeyComparator;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 8/19/2018.
 * 1.support both locale and languageID
 * 2.Location management
 * 3.titanium getDetails priceList verify
 * 4,bs car and GDS car map verify for details and costAndAvail

 */
@SuppressWarnings("PMD")
public class SupportLocalGetDeatils extends SuiteCommon
{

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1076531testCarBSSupportlocaleinShoppingOnlyLanguageID() throws Exception
    {

        testCarbsGetdetails(CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(), "1076531", 2057l, false, false );
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1076532testCarBSSupportlocaleinShoppingOnlyLocale() throws Exception
    {
        testCarbsGetdetails(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "1076532", null, true, false);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1076533testCarBSSupportlocaleinShoppingOnlyLocale() throws Exception {
        testCarbsGetdetails(CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_CDG.getTestScenario(), "1076533", null, true, true);

    }

    private void testCarbsGetdetails(TestScenario scenarios, String tuid, Long languageId, Boolean withProductToken,
                                     boolean currencyConversion) throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.getRandomGuid();
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);

        //search
        final CarECommerceSearchRequestType request = getCarECommerceSearchRequestType(tuid, languageId, testData);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid,httpClient,request);
        final CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        final CarProductType carProductTypeFordetails = new CarProductType();
        if (withProductToken)
        {
            carProductTypeFordetails.setCarProductToken(carProductType.getCarProductToken());
        }
        else
        {
            carProductType.setCarProductToken(null);
        }

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request,response, testData);
        String guidForDetails = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "TSCS_GBP");

        testData.setGuid(guidForDetails);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = getCarECommerceGetDetailsRequestType(withProductToken, carProductType, carProductTypeFordetails, carbsSearchRequestGenerator);
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guidForDetails,httpClient,getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guidForDetails, scenarios, getDetailsRequestType, getDetailsResponseType);
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput
                (getDetailsRequestType, getDetailsResponseType);

        //1.verify language
        List<String> remarks = new ArrayList<>();
        verifyLanguage(spooferTransport, testData, getDetailsResponseType, remarks);

        //2.titanium priceList verify
        priceListVerify(testData, getDetailsVerificationInput, remarks);

        //3. node map verify for car in getdetails response and GDS car
        mapVerifyForDetails(currencyConversion, spooferTransport, testData, getDetailsResponseType, remarks);



        //getCostAndAvail
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        String guidForCostAndAvail = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "TSCS_GBP");
        testData.setGuid(guidForCostAndAvail);
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        getCostAndAvailabilityRequestType.getAuditLogTrackingData().setAuditLogForceDownstreamTransaction(true);
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guidForCostAndAvail,httpClient,getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guidForCostAndAvail, scenarios, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        //4. node map verify for car in getdetails response and GDS car
        mapVerifyForCostAndAvail(currencyConversion, spooferTransport, testData, getCostAndAvailabilityResponse, remarks);

        //5.Location management verify(CarcatalogKey verify)
        LocationManagementVerify(carProductType, getDetailsResponseType, getCostAndAvailabilityResponse, remarks);

        if (CollectionUtils.isNotEmpty(remarks))
        {
            Assert.fail(remarks.toString());
        }
    }

    private void mapVerifyForCostAndAvail(boolean currencyConversion, SpooferTransport spooferTransport, TestData testData, CarECommerceGetCostAndAvailabilityResponseType costAndAvailsResponseType, List<String> remarks) throws IOException, DataAccessException, SQLException
    {
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final TVARRsp tvarRsp = new TVARRsp(spooferDoc.getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE).item(0),
                new CarsSCSDataSource(DatasourceHelper.getTitaniumDatasource()), true, false);

        final CarProductType  actualCarproduct = costAndAvailsResponseType.getCarProductList().getCarProduct().get(0);
        CarProductType expectCarproduct = null;
        final  List<CarProductType> carProductTypeList = tvarRsp.getCarProduct().getCarProduct();
        for(final CarProductType carProductType : carProductTypeList)
        {
            if(CarProductComparator.isCorrespondingCar(actualCarproduct, carProductType))
            {
                expectCarproduct = carProductType;
                break;
            }
        }
        //GDS car no priceList , no need to compare
        for (final CarVehicleOptionType carVehicleOptionType : actualCarproduct.getCarVehicleOptionList().getCarVehicleOption())
        {
            carVehicleOptionType.setPrice(null);
        }
        if(null != expectCarproduct)
        {
            // PrepayBoolean just exist in carbs, don't need verify
            expectCarproduct.setPrePayBoolean(actualCarproduct.getPrePayBoolean());


            //if there is currency conversion, just verify cost data that  currency response from GDS
            CarbsCommonVerification.handleCurrency(currencyConversion, testData, actualCarproduct);

            CarProductComparator.isCarProductEqual(expectCarproduct, actualCarproduct, remarks, Arrays.asList(CarTags.CAR_POST_PURCHASE_BOOLEAN,
                    CarTags.AVAIL_STATUS_CODE, CarTags.SUPPLY_SUBSET_ID, CarTags.CAR_LOCATION_CATEGORY_CODE, CarTags.SUPPLIER_RAW_TEXT,
                    CarTags.LOCATION_CODE, CarTags.PACKAGEBOOLEAN, CarTags.CAR_CATALOG_MAKE_MODEL, CarTags.CAR_POLICY_LIST,
                    CarTags.CAR_DOOR_COUNT, CarTags.CAR_DROP_OFF_LOCATION));

        }
    }

    private void mapVerifyForDetails(boolean currencyConversion, SpooferTransport spooferTransport, TestData testData, CarECommerceGetDetailsResponseType getDetailsResponseType, List<String> remarks) throws IOException, DataAccessException, SQLException
    {
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final TVRRRsp tvrrRsp = new TVRRRsp(spooferDoc.getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVRR_RESPONSE_TYPE).item(0),
                new CarsSCSDataSource(DatasourceHelper.getTitaniumDatasource()), true, getDetailsResponseType.getCarProductList().getCarProduct().get(0).
                getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());


        final CarProductType expectCarproduct = tvrrRsp.getCarProduct();
        final CarProductType  actualCarproduct = getDetailsResponseType.getCarProductList().getCarProduct().get(0);

        //GDS car no priceList , no need to compare
        for (final CarVehicleOptionType carVehicleOptionType : actualCarproduct.getCarVehicleOptionList().getCarVehicleOption())
        {
            carVehicleOptionType.setPrice(null);
        }
        // PrepayBoolean just exist in carbs, don't need verify
        expectCarproduct.setPrePayBoolean(actualCarproduct.getPrePayBoolean());

        //if there is currency conversion, just verify cost data that it's currency response from GDS
        CarbsCommonVerification.handleCurrency(currencyConversion, testData, actualCarproduct);

        CarProductComparator.isCarProductEqual(expectCarproduct, actualCarproduct, remarks,
                Arrays.asList(CarTags.CAR_POST_PURCHASE_BOOLEAN, CarTags.AVAIL_STATUS_CODE, CarTags.SUPPLY_SUBSET_ID,
                        CarTags.CAR_LOCATION_CATEGORY_CODE, CarTags.SUPPLIER_RAW_TEXT,
                        CarTags.LOCATION_CODE, CarTags.PACKAGEBOOLEAN,
                        CarTags.CAR_CATALOG_MAKE_MODEL, CarTags.CAR_POLICY_LIST, CarTags.CAR_DOOR_COUNT));

        carPolicyVerify(testData, remarks, expectCarproduct, actualCarproduct);
    }



    private void carPolicyVerify(TestData testData, List<String> remarks, CarProductType expectCarproduct, CarProductType actualCarproduct) throws DataAccessException, SQLException
    {
        //Handle policy for CASSS-3371 CMA: Change R/R from Static to Dynamic for Titanium GDS
        //If Rules.setPolicyCategoryToMerchantRules/enable is on, policy except for Arrival should be renamed to merchantRules
        final PosConfigHelper configHelper = new PosConfigHelper(DatasourceHelper.getTitaniumDatasource());
        final boolean featureFlag = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(),
                "1", ServiceConfigs.RULES_SETPOLICYCATEGORYTOMERCHANTRULES_ENABLE);
        if (featureFlag)
        {
            for(final CarPolicyType policy : expectCarproduct.getCarPolicyList().getCarPolicy())
            {
                if (!policy.getCarPolicyCategoryCode().equals("Arrival"))
                {
                    policy.setCarPolicyCategoryCode("MerchantRules");
                }
            }
        }
        final StringBuilder errorMsgBuilderTemp = new StringBuilder();
        CompareUtil.compareObject(expectCarproduct.getCarPolicyList().getCarPolicy(), actualCarproduct.getCarPolicyList().getCarPolicy(), new ArrayList<>(), errorMsgBuilderTemp, true);
        if (StringUtil.isNotBlank(errorMsgBuilderTemp.toString().trim()))
        {
            remarks.add("\n\n" + errorMsgBuilderTemp.toString().trim());
        }
    }

    private void priceListVerify(TestData testData, GetDetailsVerificationInput getDetailsVerificationInput, List<String> remarks) throws DataAccessException
    {
        final VerifyPriceListInGetDetailsResponse verifier = new VerifyPriceListInGetDetailsResponse();
        final IVerification.VerificationResult result = verifier.verifyTitaniumPrice(getDetailsVerificationInput, testData);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            remarks.add("\n\n" + CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT +
                    result);
        }
    }


    private void LocationManagementVerify(CarProductType carProductType, CarECommerceGetDetailsResponseType getDetailsResponseType, CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse, List<String> remakrs) throws Exception
    {
        Boolean oneWay = false;
        if(!carProductType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode().equals(
                carProductType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode()))
        {
            oneWay = true;
        }
        //details
        existCorrectCarVendorLocationIDInCarInventoryKey(getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarInventoryKey(), null, null, oneWay, remakrs);
        carPickupAndDropOffLocationKeyInCarInventoryKeyVerify(getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarInventoryKey(), carProductType.getCarInventoryKey(), remakrs);

        //cost&avail
        existCorrectCarVendorLocationIDInCarInventoryKey(getCostAndAvailabilityResponse.getCarProductList().getCarProduct().get(0).getCarInventoryKey(), null , null, oneWay, remakrs);
        carPickupAndDropOffLocationKeyInCarInventoryKeyVerify(getCostAndAvailabilityResponse.getCarProductList().getCarProduct().get(0).getCarInventoryKey(), carProductType.getCarInventoryKey(), remakrs);


        //3. For ProductToken: Decode Product token and verify only LocationID is present in pickup and dropoff location keys.
        final CarInventoryKeyType carInventoryKey = CommonUtil.decodeProductToken(getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarProductToken());
        existCorrectCarVendorLocationIDInCarInventoryKey(carInventoryKey,
                getCostAndAvailabilityResponse.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID(),
                getCostAndAvailabilityResponse.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID(), oneWay, remakrs);
        if(existCarPickupAndDropOffLocationKeyInCarInventoryKey(carInventoryKey))
        {
          remakrs.add("CarCataLogKey should not exist!");
        }
    }

    private CarECommerceGetDetailsRequestType getCarECommerceGetDetailsRequestType(Boolean withProductToken, CarProductType carProductType, CarProductType carProductTypeFordetails, CarbsRequestGenerator carbsSearchRequestGenerator) throws DataAccessException
    {
        if(withProductToken)
        {
            carbsSearchRequestGenerator.setSelectedCarProduct(carProductTypeFordetails);
        }
        else
        {
            carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        }
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();

        if (withProductToken)
        {
            getDetailsRequestType.setCarDataCategoryCodeList(null);
        }
        return getDetailsRequestType;
    }

    private CarECommerceSearchRequestType getCarECommerceSearchRequestType(String tuid, Long languageId, TestData testData) throws DataAccessException
    {

        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);

        //1. CarBS shopping with only locale, Verify languageID retrieved from locale is applied- verify from titanium GDS  request.
        if(null == languageId)
        {
            request.getAuditLogTrackingData().setAuditLogLanguageId(null);
        }
        else
        //2.CarBS shopping with only languageID, Verify languageID is applied - verify from titanium GDS request.
        {
            request.getSiteMessageInfo().setLanguage(null);
            request.getAuditLogTrackingData().setAuditLogLanguageId(languageId);

        }
        return request;
    }

    public static void existCorrectCarVendorLocationIDInCarInventoryKey(CarInventoryKeyType carInventoryKey, Long carPickUpLocationID, Long carDropOffLocationID, Boolean oneWay, List<String> remarks) throws DataAccessException
    {
        Boolean isExistCorrect = false;
        long actualCarVendorLocationIDOfPickup = carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID();
        long carVendorLocationIDOfPickup;
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        if (null == carPickUpLocationID)
        {
            final CarVendorLocation expectedCarVendorlocation = carsInventoryHelper.getCarLocation(String.valueOf(carInventoryKey.getCarCatalogKey().getVendorSupplierID()),
                    carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getLocationCode(), carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getCarLocationCategoryCode(),
                    carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText());
            carVendorLocationIDOfPickup = expectedCarVendorlocation.getCarVendorLocationID();
        }
        else
        {
            carVendorLocationIDOfPickup = carPickUpLocationID;
        }

        if (actualCarVendorLocationIDOfPickup > 0 && actualCarVendorLocationIDOfPickup == carVendorLocationIDOfPickup)
        {
            if(oneWay)
            {
                final Long actualCarVendorLocationIDOfDropOff = carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID();
                long carVendorLocationIDOfDropOff ;
               if(null == carDropOffLocationID)
               {
                   final CarVendorLocation expectedCarVendorlocationDrop = carsInventoryHelper.getCarLocation(String.valueOf(carInventoryKey.getCarCatalogKey().getVendorSupplierID()),
                           carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getLocationCode(), carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getCarLocationCategoryCode(),
                           carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText());

                   carVendorLocationIDOfDropOff = expectedCarVendorlocationDrop.getCarVendorLocationID();
               }
               else
               {
                   carVendorLocationIDOfDropOff = carDropOffLocationID;
               }
                if (actualCarVendorLocationIDOfDropOff > 0 && actualCarVendorLocationIDOfDropOff == carVendorLocationIDOfDropOff)
                    isExistCorrect = true;

            }
            else
            {
                if(actualCarVendorLocationIDOfPickup == carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID())
                    isExistCorrect = true;
            }
        }
       if(!isExistCorrect)
       {
          remarks.add("\n\nCarVendorLcoationId exist in carbs response is not correct!");
       }
    }

    public static void carPickupAndDropOffLocationKeyInCarInventoryKeyVerify(CarInventoryKeyType carInventoryKey, CarInventoryKeyType actCarInventoryKey, List<String> remarks)
    {
        if (!existCarPickupAndDropOffLocationKeyInCarInventoryKey(carInventoryKey))
        {
            remarks.add("\n\nCarCataLogKey should be exist.");
        }

        StringBuilder errMsg = new StringBuilder("");
        CarsInventoryKeyComparator.isCarInventoryKeyEqual(carInventoryKey, actCarInventoryKey, errMsg, Arrays.asList(CarTags.SUPPLY_SUBSET_ID, CarTags.PACKAGEBOOLEAN, CarTags.CAR_POST_PURCHASE_BOOLEAN));

        if(StringUtil.isNotBlank(String.valueOf(errMsg)))
            remarks.add("\n\n" + String.valueOf(errMsg));

    }

    public static Boolean existCarPickupAndDropOffLocationKeyInCarInventoryKey(CarInventoryKeyType carInventoryKey)
    {
        if((carInventoryKey.getCarCatalogKey().getCarPickupLocationKey() == null
                || (StringUtil.isBlank(carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getCarLocationCategoryCode())
                && StringUtil.isBlank(carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getLocationCode())
                && StringUtil.isBlank(carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText())))
                &&
                (carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey() == null
                        || (StringUtil.isBlank(carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getCarLocationCategoryCode())
                        && StringUtil.isBlank(carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getLocationCode())
                        && StringUtil.isBlank(carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText())))
                )
        {
            return false;
        }
        return true;
    }


    private void verifyLanguage(SpooferTransport spooferTransport, TestData testData, CarECommerceGetDetailsResponseType getDetailsResponseType, List<String> remarks) throws IOException, DataAccessException
    {
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final NodeList tvarResps = spooferDoc.getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);

        if (null != tvarResps)
        {
            final TVARRsp tvarRsp = new TVARRsp(tvarResps.item(tvarResps.getLength() - 1), new CarsSCSDataSource(DatasourceHelper.getTitaniumDatasource()), true, false);
             if (testData.getScenarios().getJurisdictionCountryCode().equals("GBR") && !"en-GB".equalsIgnoreCase(tvarRsp.getPrimaryLangID()))
             {
                 remarks.add("\n\n" + "LanguageID send downstream wrong for search");
             }
            if (testData.getScenarios().getJurisdictionCountryCode().equals("FRA") && !"fr-fr".equalsIgnoreCase(tvarRsp.getPrimaryLangID()))
            {
                remarks.add("\n\n" + "LanguageID send downstream wrong for search");
            }
        }
        final NodeList tvrrResps = spooferDoc.getElementsByTagName("OTA_VehRateRuleRS");

        if (null != tvrrResps)
        {
            final TVRRRsp tvrrRsp = new TVRRRsp(tvrrResps.item(tvrrResps.getLength() - 1), new CarsSCSDataSource(DatasourceHelper.getTitaniumDatasource()), true, getDetailsResponseType.getCarProductList()
            .getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
            if (testData.getScenarios().getJurisdictionCountryCode().equals("GBR") &&!"en-GB".equalsIgnoreCase(tvrrRsp.getPrimaryLangID()))
            {
                remarks.add("\n\n" + "LanguageID send downstream wrong for getdetails");
            }

            if (testData.getScenarios().getJurisdictionCountryCode().equals("FRA") && !"fr-fr".equalsIgnoreCase(tvrrRsp.getPrimaryLangID()))
            {
                remarks.add("\n\n" + "LanguageID send downstream wrong for getdetails");

            }
        }

    }

    public static boolean correctCarPickupAndDropOffLocationKeyInCarInventoryKey(CarInventoryKeyType actualCarInventory, CarInventoryKeyType expectedCarInventory, StringBuilder tempError)
    {
        final boolean result = CarsInventoryKeyComparator.isCarInventoryKeyEqual(expectedCarInventory, actualCarInventory, tempError,  Arrays.asList(CarTags.PACKAGEBOOLEAN, CarTags.CAR_POST_PURCHASE_BOOLEAN));
        return result;
    }


}
