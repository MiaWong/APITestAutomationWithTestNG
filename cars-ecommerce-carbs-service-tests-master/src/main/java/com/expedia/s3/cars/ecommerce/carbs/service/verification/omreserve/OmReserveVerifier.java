package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.CarbsCommonVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.CommitPreparePurchaseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.CreateRecordBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.GetOrderProcessBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.PreparePurchaseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.RollbackPreparePurchaseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.CommitPreparePurchaseVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.CreateRecordVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.GetOrderProcessVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.RollbackPreparePurchaseVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ARIARsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping.ASCSReserve;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import expedia.om.supply.messages.defn.v1.CommitPreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.CommitPreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.CreateRecordRequest;
import expedia.om.supply.messages.defn.v1.CreateRecordResponseType;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.RollbackPreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.RollbackPreparePurchaseResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 11/10/2016.
 */
public class OmReserveVerifier {

    private OmReserveVerifier() {
    }

    public static void isGetOrderProcessWorksVerifier(String guid, TestScenario scenarios, GetOrderProcessRequest requestType, GetOrderProcessResponseType responseType) throws IOException {

        final BasicVerificationContext getOrderProcessVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final GetOrderProcessVerificationInput getOrderProcessVerificationInput = new GetOrderProcessVerificationInput(requestType, responseType);
        /*final ChainedVerification<GetOrderProcessVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isOMGetOrderProcessWorksVerifier", Arrays.asList(new GetOrderProcessBasicVerification()));
*/
        final GetOrderProcessBasicVerification verifications = new GetOrderProcessBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(getOrderProcessVerificationInput, getOrderProcessVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

    }

    public static void isCreateRecordWorksVerifier(String guid, TestScenario scenarios, CreateRecordRequest requestType, CreateRecordResponseType responseType) throws IOException {

        final BasicVerificationContext createRecordVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final CreateRecordVerificationInput createRecordVerificationInput = new CreateRecordVerificationInput(requestType, responseType);
       /* final ChainedVerification<CreateRecordVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isOMCreateRecordWorksVerifier", Arrays.asList(new CreateRecordBasicVerification()));
*/
        final CreateRecordBasicVerification verifications = new CreateRecordBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(createRecordVerificationInput, createRecordVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

    }

    public static void isPreparePurchaseWorksVerifier(String guid, TestScenario scenarios, PreparePurchaseRequest requestType, PreparePurchaseResponseType responseType) throws Exception {
        final BasicVerificationContext preparePurchaseVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final PreparePurshaseVerificationInput preparePurchaseVerificationInput = new PreparePurshaseVerificationInput(requestType, responseType);
       /* final ChainedVerification<PreparePurshaseVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isOMPreparePurchaseWorksVerifier", Arrays.asList(new PreparePurchaseBasicVerification()));
*/
        final PreparePurchaseBasicVerification verifications = new PreparePurchaseBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(preparePurchaseVerificationInput, preparePurchaseVerificationContext);

        if (!result.isPassed()) {
           throw new Exception(result.toString());
        }

    }
    public static void isCommitPreparePurchaseWorksVerifier(String guid, TestScenario scenarios, CommitPreparePurchaseRequest requestType, CommitPreparePurchaseResponseType responseType) throws Exception {
        final BasicVerificationContext context = new BasicVerificationContext(null, guid, scenarios);
        final CommitPreparePurchaseVerificationInput commitpreparePurchaseVerificationInput = new CommitPreparePurchaseVerificationInput(requestType, responseType);
       /* final ChainedVerification<CommitPreparePurchaseVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCommitPreparePurchaseWorksVerifier", Arrays.asList(new CommitPreparePurchaseBasicVerification()));
*/
        final CommitPreparePurchaseBasicVerification verifications = new CommitPreparePurchaseBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(commitpreparePurchaseVerificationInput, context);

        if (!result.isPassed()) {
            throw new Exception(result.toString());
        }


    }
    public static void isRollbackPreparePurchaseWorksVerifier(String guid, TestScenario scenarios, RollbackPreparePurchaseRequest requestType, RollbackPreparePurchaseResponseType responseType) throws Exception {
        final BasicVerificationContext preparePurchaseVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final RollbackPreparePurchaseVerificationInput rollbackpreparePurchaseVerificationInput = new RollbackPreparePurchaseVerificationInput(requestType, responseType);
        final ChainedVerification<RollbackPreparePurchaseVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isRollbackPreparePurchaseWorksVerifier", Arrays.asList(new RollbackPreparePurchaseBasicVerification()));

        final IVerification.VerificationResult result = verifications.verify(rollbackpreparePurchaseVerificationInput, preparePurchaseVerificationContext);

        if(!result.isPassed() || !result.getRemarks().isEmpty()) {
            Assert.fail(result.toString());
        }
    }

    private static StringBuilder preparePurchaseResponseVerify(CarbsOMReserveReqAndRespGenerator omRequestGenerater)
    {
        StringBuilder errorMsg = new StringBuilder("");
        if (null == omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation()
        .getCustomer().getContactInformation())
        {
            errorMsg.append("Customer is missing ContactInformation.\n");
        }
        if (null == omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
        .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getTravelerList()
        .getTraveler().get(0).getContactInformation())
        {
            errorMsg.append( "Traveler is missing ContactInfrmation.\n");
        } if (null != omRequestGenerater.getPreparePurchaseRequestType().getConfiguredProductData()
        .getCarOfferData().getCarReservation().getPaymentInfo()
         && null != omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getPaymentInfo())
        {
            if (!omRequestGenerater.getPreparePurchaseRequestType().getConfiguredProductData()
                    .getCarOfferData().getCarReservation().getPaymentInfo().getBillingCode().equals(omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                            .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getPaymentInfo().getBillingCode()))
            {
                errorMsg.append("Billing Code is different between preparePrchaseRequest and preparePrchaseResponse.\n");
            }
        }

        if ("A" .equals(omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getAvailStatusCode())
                && (String.valueOf(errorMsg).trim().equals("AvailStatusCode is not correctly delivered.\n") || String.valueOf(errorMsg).trim().equals("AvailStatusCode is not correctly delivered.")))
        {
            errorMsg = new StringBuilder("");
        }


        //verify CarCatalogMarkupModel
        CompareUtil.compareObject(omRequestGenerater.getPreparePurchaseRequestType().getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarCatalogMakeModel(),
                omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getCarCatalogMakeModel(),
                null, errorMsg);


        return errorMsg;
    }

    public static void preparePurchaseRegressionVerifier(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws IOException, DataAccessException, SQLException
    {
        final StringBuilder errorMsg = preparePurchaseResponseVerify(omRequestGenerater);
        final List<String> remarks = comparePreparePurchaseResponseWithGDSdetailsAndGDSbooking(testData, omRequestGenerater);
        if (StringUtil.isNotBlank(errorMsg.toString()))
        {
            remarks.add(errorMsg + "\n\n");
        }
        if (CollectionUtils.isNotEmpty(remarks))
        {
            Assert.fail(remarks.toString());
        }

    }

    private static List<String> comparePreparePurchaseResponseWithGDSdetailsAndGDSbooking(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws IOException, DataAccessException
    {
        final Document document = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final List<Node> nodes = PojoXmlUtil.getNodesByTagName(document.getDocumentElement(), GDSMsgNodeTags.AmadeusNodeTags.ARIA_CAR_GET_DETAIL_RESPONSE_TYPE);
        final ARIARsp ariaRsp = new ARIARsp(nodes.get(nodes.size()-1), new CarsSCSDataSource(DatasourceHelper.getAmadeusSCSDatasource()), omRequestGenerater.getSelectCarProduct().getCarInventoryKey());
        final CarProductType expGDSDetailsCarProduct = ariaRsp.getCar();
        final CarProductType actCarproduct = omRequestGenerater.getPreparePurchaseResponseType().getPreparedItems()
                .getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getCarProduct();


        final ASCSReserve reserveMapping = new ASCSReserve();
        final StringBuffer eMsg = new StringBuffer();
        final BasicVerificationContext verificationContext = new BasicVerificationContext(document, testData.getGuid(), testData.getScenarios());
        final CarReservationType carReservation = new CarReservationType();
        reserveMapping.buildCarReservationCar(verificationContext, omRequestGenerater.getSelectCarProduct().getCarInventoryKey(), carReservation, new CarsSCSDataSource(DatasourceHelper.getAmadeusSCSDatasource()), eMsg);
        final CarProductType expGDSbookingCarProduct = carReservation.getCarProduct();

        final List<String> remarks = new ArrayList<>();
        if(StringUtil.isNotBlank(String.valueOf(eMsg)))
        {
            remarks.add(String.valueOf(eMsg));
        }

        //set AvailStatusCode from request car.
        expGDSbookingCarProduct.setAvailStatusCode(omRequestGenerater.getSelectCarProduct().getAvailStatusCode());
        //prepayBoolean just exist in carbs level.
        expGDSbookingCarProduct.setPrePayBoolean(actCarproduct.getPrePayBoolean());
        expGDSDetailsCarProduct.setPrePayBoolean(actCarproduct.getPrePayBoolean());
        checkLocationKey(testData, expGDSbookingCarProduct, actCarproduct);
        checkLocationKey(testData, expGDSDetailsCarProduct, actCarproduct);


        //don't need to compare the carCatalogMakeModel, for Agencia car, preparePurchase response carCatalogMakeModel
        // get from preparePurchase request , not  merge from getdetails.
        CarProductComparator.isCarProductEqual(expGDSbookingCarProduct, actCarproduct, remarks, Arrays.asList(CarTags.PACKAGEBOOLEAN, CarTags.CAR_POST_PURCHASE_BOOLEAN,CarTags.CAR_COST_LIST,CarTags.CAR_RATE_DETAIL, CarTags.CAR_CATALOG_MAKE_MODEL));

        //if there is currency conversion, just verify cost data that  currency response from GDS
        if (!testData.getScenarios().getSupplierCurrencyCode().equals(expGDSDetailsCarProduct.getCostList().getCost().get(0).getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
        {
            CarbsCommonVerification.handleCurrency(true, testData, actCarproduct);
        }
        CarProductComparator.isCarProductEqual(expGDSDetailsCarProduct, actCarproduct, remarks, Arrays.asList(CarTags.AVAIL_STATUS_CODE,CarTags.PACKAGEBOOLEAN, CarTags.CAR_POST_PURCHASE_BOOLEAN
        , CarTags.RATE_CATEGORY_CODE, CarTags.CAR_VEHICLE_OPTIONLIST,CarTags.CAR_POLICY_LIST, CarTags.CAR_CATALOG_MAKE_MODEL));

        return remarks;
    }


    public static void getOrderRegressionVerifier(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws IOException, DataAccessException, SQLException
    {
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), SettingsProvider.CARBS_POS_SET_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
        final boolean makeCostAndAvailCall = posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), "0", PosConfigSettingName.GETORDERPROCESS_MAKEDETAILSCALL_ENABLE, false);
        if (makeCostAndAvailCall)
        {
            final String errorMsg = compargetOrderProcessRequestAndResponseAssert(omRequestGenerater);

            final List<String> remarks = compareGetOrderProcessResponseWithGDS(testData, omRequestGenerater);
            if (StringUtil.isNotBlank(errorMsg))
            {
                remarks.add(errorMsg + "\n\n");
            }
            if (CollectionUtils.isNotEmpty(remarks))
            {
                Assert.fail(remarks.toString());
            }
        }
        else
        {
            //todo for make details call
        }

    }

    private static List<String> compareGetOrderProcessResponseWithGDS(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws IOException, DataAccessException
    {
        final Document document = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final List<Node> nodes = PojoXmlUtil.getNodesByTagName(document.getDocumentElement(), GDSMsgNodeTags.AmadeusNodeTags.ACAQ_RESPONSE_TYPE);
        final ACAQRsp acaqRsp = new ACAQRsp(nodes.get(nodes.size()-1),null,  new CarsSCSDataSource(DatasourceHelper.getAmadeusSCSDatasource()));

        final List<CarProductType> carProductTypeList = acaqRsp.getGdsCarProductList();
        CarProductType expGDSCarProduct = null;
        final CarProductType actCarproduct = omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData()
                .getCarReservation().getCarProduct();
        for(final CarProductType carProductType : carProductTypeList)
        {
            if (CarProductComparator.isCorrespondingCar(carProductType, omRequestGenerater.getSelectCarProduct(), false, false))
            {
                expGDSCarProduct = carProductType;
                break;
            }
        }

        // sometime we may meet this situation. the location we send down is different from response, but they can map in
        // CarVendorLocation and ExternalSupplyServiceDomainValueMap table (they have same carVendorLocationID), this is expect.
        checkLocationKey(testData, expGDSCarProduct, actCarproduct);
        final List<String> remarks = new ArrayList<>();

        //if there is currency conversion, just verify cost data that it's currency response from GDS
        if(!testData.getScenarios().getSupplierCurrencyCode().equals(expGDSCarProduct.getCostList().getCost().get(0).getMultiplierOrAmount()
        .getCurrencyAmount().getCurrencyCode()))
        {
            CarbsCommonVerification.handleCurrency(true, testData, actCarproduct);
        }
         //just carbs level have the prepayBoolean node , no need to map verify.
        expGDSCarProduct.setPrePayBoolean(actCarproduct.getPrePayBoolean());

        CarProductComparator.isCarProductEqual(expGDSCarProduct, actCarproduct, remarks, Arrays.asList(CarTags.AVAIL_STATUS_CODE, CarTags.SUPPLY_SUBSET_ID,
                CarTags.PACKAGEBOOLEAN, CarTags.CAR_POST_PURCHASE_BOOLEAN, CarTags.CAR_CATALOG_MAKE_MODEL, CarTags.CAR_DOOR_COUNT
        ));

         return remarks;
    }

    private static void checkLocationKey(TestData testData, CarProductType expGDSCarProduct, CarProductType actCarproduct) throws DataAccessException
    {
        if (!CarNodeComparator.isCarLocationKeyEqual(expGDSCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey(), actCarproduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey(), null, Arrays.asList(CarTags.CAR_VENDOR_LOCATION_ID)))
        {
            final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(DatasourceHelper.getSCSDataSource(testData));
            final long supplierID = expGDSCarProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
            final String externalDomainValue = expGDSCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode() + expGDSCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarLocationCategoryCode() + expGDSCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText().substring(1, 3);

            final List<ExternalSupplyServiceDomainValueMap> locationMapList = scsDataSource.getExternalSupplyServiceDomainValueMap(supplierID, 0L, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, null, externalDomainValue);
            final Long carVendorLocationID = locationMapList.isEmpty() ? 0L : Long.parseLong(locationMapList.get(0).getDomainValue());

            if (carVendorLocationID.equals(actCarproduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID()))

            {
                expGDSCarProduct.getCarInventoryKey().setCarCatalogKey(actCarproduct.getCarInventoryKey().getCarCatalogKey());
                expGDSCarProduct.setCarPickupLocation(actCarproduct.getCarPickupLocation());
                expGDSCarProduct.setCarDropOffLocation(actCarproduct.getCarDropOffLocation());

            }
        }
    }

    private static String compargetOrderProcessRequestAndResponseAssert(CarbsOMReserveReqAndRespGenerator omRequestGenerater)
    {
        final StringBuilder errorMsg = new StringBuilder();
        CompareUtil.compareObject(omRequestGenerater.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarLegacyBookingData(), omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarLegacyBookingData(), null, errorMsg);

        CompareUtil.compareObject(omRequestGenerater.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getTravelerList(), omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList(), null, errorMsg);

        CompareUtil.compareObject(omRequestGenerater.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getCustomer(), omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCustomer(), null, errorMsg);

        CompareUtil.compareObject(omRequestGenerater.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getReferenceList(), omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getReferenceList(), null, errorMsg);

        CompareUtil.compareObject(omRequestGenerater.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate(), omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate(), null, errorMsg);

        CompareUtil.compareObject(omRequestGenerater.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getClientCode(), omRequestGenerater.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getClientCode(), null, errorMsg);

        return String.valueOf(errorMsg);
    }

    }