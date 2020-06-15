package com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.*;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.locationsearch.verification.LocationCommonVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.messages.location.search.defn.v1.CarLocationSearchRequest;
import com.expedia.s3.cars.messages.location.search.defn.v1.CarLocationSearchResponse;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchRequest;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon.logger;

/**
 * Created by jiyu on 8/26/16.
 */
@SuppressWarnings("PMD")
public final class ExecutionHelper {
    //  validation message


    private ExecutionHelper() {
    }


    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        return randomGuid;
    }

    //------- Search ------------------
    //  generator for non-SCS-Search request
    public static CarbsRequestGenerator createCarBSRequestGenerator(SearchVerificationInput searchVerificationInput, TestData testDta) {
        return new CarbsRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse(), testDta);
    }

    public static CarbsRequestGenerator executeSearch(TestData testData, SpooferTransport spooferTransport, DataSource carsInventoryDatasource,
                                                      Logger logger) throws DataAccessException, IOException {
        final CarbsRequestGenerator requestGenerator = executeSearch(testData);

        VerificationHelper.searchBasicVerification(new SearchVerificationInput(requestGenerator.getSearchRequestType(), requestGenerator.getSearchResponseType()), spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger);
        return requestGenerator;
    }

    public static CarbsRequestGenerator executeSearch(TestData testData) throws DataAccessException {

        //search
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        if(!CompareUtil.isObjEmpty(testData.getSpecialTest()))
        {
            modifyCarEcommerceSearchReauest(request, testData.getSpecialTest());
        }

        final  CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);

        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(request, response);

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.createCarBSRequestGenerator(searchVerificationInput, testData);

        return requestGenerator;
    }

    public static CarbsRequestGenerator invalidSearch(TestData testData) throws DataAccessException {

        return executeSearch(testData);
    }

    //search + getCostAndAvailability
    public static CarbsRequestGenerator getCostAndAvailability(TestData testData, SpooferTransport spooferTransport, DataSource carsInventoryDatasource,
                                                               Logger logger) throws IOException, DataAccessException {
        //Search
        final  CarbsRequestGenerator requestGenerators = executeSearch(testData, spooferTransport, carsInventoryDatasource, logger);

        //CostAndAvailability
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType =
                requestGenerators.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType =
                CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);

        final  GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                new GetCostAndAvailabilityVerificationInput(getCostAndAvailabilityRequestType, getCostAndAvailabilityResponseType);

        requestGenerators.setGetCostAndAvailabilityRequestType(getCostAndAvailabilityRequestType);
        requestGenerators.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponseType);

        VerificationHelper.getCostAndAvailabilityBasicVerification(getCostAndAvailabilityVerificationInput, spooferTransport,
                testData.getScenarios(), testData.getGuid(), false, logger);

        return requestGenerators;
    }

    //search + getCostAndAvailability
    public static CarbsRequestGenerator getCostAndAvailabilityByBusinessModelIDAndServiceProviderID(TestData testData,
                                                                                                    SpooferTransport spooferTransport, DataSource carsInventoryDatasource, Logger logger) throws IOException, DataAccessException {
        //search
        final  CarbsRequestGenerator requestGenerator = executeSearch(testData, spooferTransport, carsInventoryDatasource, logger);

        //CostAndAvailability
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType =
                requestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType =
                CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);

        final  GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                new GetCostAndAvailabilityVerificationInput(getCostAndAvailabilityRequestType, getCostAndAvailabilityResponseType);

        requestGenerator.setGetCostAndAvailabilityRequestType(getCostAndAvailabilityRequestType);
        requestGenerator.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponseType);

        VerificationHelper.getCostAndAvailabilityBasicVerification(getCostAndAvailabilityVerificationInput, spooferTransport,
                testData.getScenarios(), testData.getGuid(), false, logger);

        return requestGenerator;
    }

    //search + getDetail
   /* public static CarbsRequestGenerator getDetail(TestData testData, SpooferTransport spooferTransport, DataSource carsInventoryDatasource,
                                                  Logger logger) throws IOException, DataAccessException {
        //search & getCostAndAvailability
        final CarbsRequestGenerator requestGenerator = executeSearch(testData, spooferTransport, carsInventoryDatasource, logger);

        //getDetails
        final  CarECommerceGetDetailsRequestType getDetailsRequestType =
                requestGenerator.createCarbsDetailsRequest();
        final   CarECommerceGetDetailsResponseType getDetailsResponseType =
                CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);

        final  GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);

        requestGenerator.setGetDetailsRequestType(getDetailsRequestType);
        requestGenerator.setGetDetailsResponseType(getDetailsResponseType);

        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger);

        return requestGenerator;
    }*/

    //search + getDetail
    public static CarbsRequestGenerator executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(TestData testData,
                                                                                                       SpooferTransport spooferTransport, DataSource carsInventoryDatasource,Logger logger) throws IOException, DataAccessException {
        //search
        final CarbsRequestGenerator requestGenerator = executeSearch(testData, spooferTransport, carsInventoryDatasource, logger);

        //getDetails
        final  CarECommerceGetDetailsRequestType getDetailsRequestType =
                requestGenerator.createCarbsDetailsRequest();
        final  CarECommerceGetDetailsResponseType getDetailsResponseType =
                CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);

        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);

        requestGenerator.setGetDetailsRequestType(getDetailsRequestType);
        requestGenerator.setGetDetailsResponseType(getDetailsResponseType);

        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger);

        return requestGenerator;
    }

    public static CarbsOMReserveReqAndRespGenerator carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(TestData testData) throws Exception {
        //OMReserve
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.
                oMSReserveSendWithShopMsgByBusinessModelIDAndServiceProviderID
                        (testData);

        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        //Cancel
        CarbsOMCancelRequestSender.omsCancelSend
                (testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), testData.getHttpClient(),
                        CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        return carbsOMReserveReqAndRespGenerator;
    }

    public static CarbsLocationSearchRequestGenerator locationSearch(LocationSearchTestScenario scenarios, String tuid, HttpClient httpClient,
                                                                     String guid) throws IOException, DataAccessException {
        //  Create details request
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final CarbsLocationSearchRequestGenerator requestGenerator = new CarbsLocationSearchRequestGenerator();
        final CarLocationSearchRequest request = requestGenerator.createCarLocationSearchRequest(testData);

        //http://chelcarjvafe101:52028/location/search
        final SimpleE3FIHttpTransport<CarLocationSearchRequest, CarLocationSearchResponse , Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION, SettingsProvider.SERVICE_LOCATION_ADDRESS,
                30000, request, CarLocationSearchResponse.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarLocationSearchResponse response = transport4.getServiceRequestContext().getResponse();

        //  Return response so it can be used for verification
        requestGenerator.setLocationSearchResponse(response);
        System.out.println("request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        System.out.println("response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));

        //BVT verification TODO: Use common verification method once the common method is complete
        LocationCommonVerify.verifyLocationSearch(response, scenarios);
        return requestGenerator;
    }

    public static CarbsLocationSearchRequestGenerator locationIataSearch(LocationSearchTestScenario scenarios, String tuid, HttpClient httpClient,
                                                                         String guid, String testType) throws IOException, DataAccessException {
        //  Create details request
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final CarbsLocationSearchRequestGenerator requestGenerator = new CarbsLocationSearchRequestGenerator();
        final CarLocationIataSearchRequest request = requestGenerator.createLocationIataSearchRequest(testData);
        if ("BothLatLongAndIATA".equals(testType)) {
            request.getSearchCriteria().setIata("CDG");
        }

        //http://chelcarjvafe101:52028/location/search
        final SimpleE3FIHttpTransport<CarLocationIataSearchRequest, CarLocationIataSearchResponse , Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION, SettingsProvider.SERVICE_LOCATION_ADDRESS,
                30000, request,CarLocationIataSearchResponse.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarLocationIataSearchResponse response = transport4.getServiceRequestContext().getResponse();

        //  Return response so it can be used for verification
        requestGenerator.setCarLocationIataSearchResponse(response);
        System.out.println("request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        System.out.println("response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        //BVT verification TODO: Use common verification method once the common method is complete

        //BothLatLongAndIATA  is for carbs level errorhanding verify,  no need to retrieve GDS message.
        if(!"BothLatLongAndIATA" .equalsIgnoreCase(testType))
        {
            final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
            final Document spooferDoc = spooferTransport.retrieveRecords(guid);
            System.out.println("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));
            requestGenerator.setSpooferDoc(spooferDoc);
        }
        return requestGenerator;
    }

    public static void checkConfigRetrieveDataFromGDS(TestData testData, String bsClientMediaConfigValue, String bsClientGDSConfigValue, String bsClientACRISSConfigValue, String bsClientDynamicConfigValue, String clientCode) throws Exception
    {
        ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());

        if(!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.POPULATEMEDIAINFOFROMGDS_ENABLE
                , CommonUtil.getClientIDbyCode(clientCode),bsClientMediaConfigValue))
        {
            {
                Assert.fail("The client config " + ServiceConfigs.POPULATEMEDIAINFOFROMGDS_ENABLE +" set not expect " + bsClientMediaConfigValue
                        + ", clientCode : " + clientCode);
            }
        }

        if(!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.POPULATEDYNAMICMEDIADATAFROMGDS_ENABLE
                , CommonUtil.getClientIDbyCode(clientCode),bsClientGDSConfigValue))
        {
            {
                Assert.fail("The client config " + ServiceConfigs.POPULATEDYNAMICMEDIADATAFROMGDS_ENABLE +" set not expect " + bsClientGDSConfigValue
                        + ", clientCode : " + clientCode);
            }
        }

        if(StringUtil.isNotBlank(bsClientACRISSConfigValue))
        {
            if(!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.SEARCH_RETURNACRISSCODE_ENABLE
                    , CommonUtil.getClientIDbyCode(clientCode), bsClientACRISSConfigValue))
            {
                Assert.fail("The client config " + ServiceConfigs.SEARCH_RETURNACRISSCODE_ENABLE +" set not expect " + bsClientACRISSConfigValue
                        + ", clientCode : " + clientCode);
            }
        }

       //check TSCS posconfig enableDynamicContentFromGDS/enable feature on, then SCS will map mediainfo from GDS
       if(StringUtil.isNotBlank(bsClientDynamicConfigValue))
       {
           PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getTitaniumDatasource(), SettingsProvider.SCS_TITANIUM_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
          if(!posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), bsClientDynamicConfigValue, PosConfigSettingName.ENABLEDYNAMICCONTENTFROMGDS_ENABLE
                   , SettingsProvider.ENVIRONMENT_NAME))
          {
              Assert.fail("The pos config " +PosConfigSettingName.ENABLEDYNAMICCONTENTFROMGDS_ENABLE +" set not expect " + bsClientDynamicConfigValue
                      + ", Pos : " + testData.getScenarios().getJurisdictionCountryCode() + "," + testData.getScenarios().getManagementUnitCode()
              + "," +testData.getScenarios().getCompanyCode());

          }
       }
       }

    public static TestData setTestScenarioSpecialHandleParamOfTestData(TestData testData, DataSource carsInvDatasource, boolean needSupplierID, String vendorCode) throws DataAccessException {
        if (needSupplierID) {
            final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
            specialHandleParam.setVendorCode(vendorCode);
            final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInvDatasource);
            final List<CarVendor> carVendors = carsInventoryHelper.getCarVendorList(vendorCode);
            specialHandleParam.setVendorSupplierID(Long.valueOf(carVendors.get(0).getSupplierID()));
            testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        }
        return testData;
    }

    private static void modifyCarEcommerceSearchReauest(CarECommerceSearchRequestType request, String name)
    {
        switch (name)
        {
            case "tfs452726PackageCheapestFilter":
                PackageFilterHelper.tfs452726PackageCheapestFilter(request);
                break;
            case "tfs452891PackageNumericFilter":
                PackageFilterHelper.tfs452891PackageNumericFilter(request);
                break;
            case "tfs453786PackageSavingFilter":
                PackageFilterHelper.tfs453786PackageSavingFilter(request);
                break;
            case "tfs453731PackageMarginFilter":
                PackageFilterHelper.tfs453731PackageMarginFilter(request);
                break;
            case "tfs452875PackageCheapestFilter":
                PackageFilterHelper.tfs452875PackageCheapestFilter(request);
                break;
            case "tfs453718PackageCapacityFilter":
                PackageFilterHelper.tfs453718PackageCapacityFilter(request);
                break;
            case "tfs453696PackageOldCheapestNbestFilter":
                PackageFilterHelper.tfs453696PackageOldCheapestNbestFilter(request);
                break;
            case "tfs453702PackageOldCheapestNoOptFilter":
                PackageFilterHelper.tfs453702PackageOldCheapestNoOptFilter(request);
                break;
            default:
                logger.error("Please check test case name");
                break;
        }
    }

    public static void setCarRateOfTestData(TestData testData, boolean needCarRate, String loyaltyNum, String cdCode) {
        if (needCarRate) {
            final CarRate carRateParam = new CarRate();
            carRateParam.setLoyaltyNum(loyaltyNum);
            carRateParam.setCdCode(cdCode);
            testData.setCarRate(carRateParam);
        }
    }

    public static void checkPosConfigForSearchFilterInvalidLocationCodeEnable(TestData testData, String searchFilterInvalidCode) throws Exception {
        if (StringUtil.isNotBlank(searchFilterInvalidCode)) {
            PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), SettingsProvider.SERVICE_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
            if (!posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), searchFilterInvalidCode, PosConfigSettingName.SEARCH_FILTER_COMPARE_INVENTORYKEY_ENABLE, SettingsProvider.ENVIRONMENT_NAME)) {
                Assert.fail("The pos config " + PosConfigSettingName.SEARCH_FILTER_COMPARE_INVENTORYKEY_ENABLE + " set not expect " + searchFilterInvalidCode + ", Pos : "
                        + testData.getScenarios().getJurisdictionCountryCode() + "," + testData.getScenarios().getManagementUnitCode() + "," + testData.getScenarios().getCompanyCode());
            }
        }
    }

    public static Map<String, AddressType> getAddressFromLocationSearchWithLocationKey(CarProductType carProduct,
                                                                                 CarLocationIataSearchResponse locationResponse_pick, CarLocationIataSearchResponse locationResponse_drop) {
        Map<String, AddressType> addressMap = new HashMap<>();

        // address from pick up location key
        final CarLocationKeyType pickLocationKey = carProduct.getCarPickupLocation().getCarLocationKey();
        final List<CarLocationType> carLocationList = locationResponse_pick.getCarLocationList().getCarLocation();

        // get mached address
        AddressType matchedAddress = getAddressFromLocationResponseForCarProduct(pickLocationKey,carLocationList);
        if (matchedAddress != null)
        {
            addressMap.put(CarCommonEnumManager.PICKLOCATIONKEY, matchedAddress);
        }

        if (locationResponse_drop != null)
        {
            final CarLocationKeyType dropLocationKey = carProduct.getCarDropOffLocation().getCarLocationKey();
            final List<CarLocationType> dropLocationList = locationResponse_drop.getCarLocationList().getCarLocation();
            AddressType matchedAddress_drop = getAddressFromLocationResponseForCarProduct(dropLocationKey,dropLocationList);
            if (matchedAddress_drop != null)
            {
                addressMap.put(CarCommonEnumManager.DROPLOCATIONKEY, matchedAddress_drop);
            }
        }
        return addressMap;
    }

    private static AddressType getAddressFromLocationResponseForCarProduct(CarLocationKeyType pickOrDropLocationKey, List<CarLocationType> carLocationList) {
        AddressType matchedAddress = null;
        boolean matchLocation = false;
        for (CarLocationType location: carLocationList) {
            if (location.getCarLocationKey().getLocationCode().equals(pickOrDropLocationKey.getLocationCode())
                    && location.getCarLocationKey().getCarLocationCategoryCode().equals(pickOrDropLocationKey.getCarLocationCategoryCode())
                    && location.getCarLocationKey().getSupplierRawText().equals(pickOrDropLocationKey.getSupplierRawText()))
            {
                matchedAddress = location.getAddress();
                matchLocation = true;
                break;
            }
        }
        if (!matchLocation)
        {
            logger.info("Get Del or Col address from location response failed,"
                    + " there is no mached pick up loction key exist in location response for selected car for"
                    + pickOrDropLocationKey.getLocationCode()
                    + pickOrDropLocationKey.getCarLocationCategoryCode()
                    + pickOrDropLocationKey.getSupplierRawText());
        }
        return matchedAddress;
    }
}
