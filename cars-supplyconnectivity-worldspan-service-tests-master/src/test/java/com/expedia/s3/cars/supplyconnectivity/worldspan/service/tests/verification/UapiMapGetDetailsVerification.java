package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.CarPosConfigDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.SupplierConfiguration;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.UAPICommonNodeReader;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VRURReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VRURRes;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VSARRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yyang4 on 12/27/2016.
 */
public class UapiMapGetDetailsVerification {
    private static final Logger logger = Logger.getLogger(SearchResponsesBasicVerification.class);

    public static void uapiMapVerifierWSCSGetDetails(BasicVerificationContext verificationContext, GetDetailsVerificationInput verificationInput, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final TestScenario scenario = verificationContext.getScenario();
        final CarSupplyConnectivityGetDetailsRequestType scsReq = verificationInput.getRequest();
        final CarSupplyConnectivityGetDetailsResponseType scsRsp = verificationInput.getResponse();
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        final StringBuilder errorMsg = new StringBuilder();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found !");
        }
        final Node vrurReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VRUR_REQUEST_TYPE);
        final Node vrurResNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VRUR_RESPONSE_TYPE);
        if (CompareUtil.isObjEmpty(vrurReqNode) || CompareUtil.isObjEmpty(vrurResNode)) {
            Assert.fail("No request or response VRUR message found ! ");
        }
        //Parse request and response
        final VRURReq vrurReqeust = new VRURReq(vrurReqNode, scsDataSource, carsInventoryDs);
        final VRURRes vrurResponse = new VRURRes(vrurResNode, vrurReqeust.getInventoryKey(), scsDataSource, carsInventoryDs, SettingsProvider.USE_SPOOFER);

        final CarInventoryKeyType carsInventoryRequesteEpected = vrurReqeust.getInventoryKey();
        // Set expected CDCode
        if (vrurResponse.isCorporateRate() && !CompareUtil.isObjEmpty(vrurReqeust.getInventoryKey().getCarRate().getCorporateDiscountCode())) {
            vrurResponse.getCarProduct().getCarInventoryKey().getCarRate().setCorporateDiscountCode(vrurReqeust.getInventoryKey().getCarRate().getCorporateDiscountCode());
        }
        //if PropagateCDCodeUpstream_enable == 1, we will also return CD code.
        if (!CompareUtil.isObjEmpty(scsRsp.getCarProductList().getCarProduct())) {
            final long supplierId = scsRsp.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
            SupplierConfiguration supplierConfiguration = scsHelper.getSupplierSetting(CommonConstantManager.SupplierConfigurationSettingName.PROPAGATE_CDCODE_UPSTREAM_ENABLE, SettingsProvider.ENVIRONMENT_NAME, supplierId);
            String supplierConfig = CompareUtil.isObjEmpty(supplierConfiguration) ? "" : supplierConfiguration.getSettingValue();
            if (CompareUtil.isObjEmpty(supplierConfig)) {
                supplierConfiguration = scsHelper.getSupplierSetting(CommonConstantManager.SupplierConfigurationSettingName.PROPAGATE_CDCODE_UPSTREAM_ENABLE, SettingsProvider.ENVIRONMENT_NAME, 0L);
                supplierConfig = CompareUtil.isObjEmpty(supplierConfiguration) ? "" : supplierConfiguration.getSettingValue();
            }
            if (CompareUtil.isObjEmpty(supplierConfig)) {
                supplierConfiguration = scsHelper.getSupplierSetting(CommonConstantManager.SupplierConfigurationSettingName.PROPAGATE_CDCODE_UPSTREAM_ENABLE, null, 0L);
                supplierConfig = CompareUtil.isObjEmpty(supplierConfiguration) ? "" : supplierConfiguration.getSettingValue();
            }
            if ("1".equals(supplierConfig)) {
                vrurResponse.getCarProduct().getCarInventoryKey().getCarRate().setCorporateDiscountCode(vrurReqeust.getInventoryKey().getCarRate().getCorporateDiscountCode());
            }
        }
        final CarProductType carProductResponseExpected = vrurResponse.getCarProduct();
        final CarProductType carProductRsp = scsRsp.getCarProductList().getCarProduct().get(0);
        final CarProductType carProductReq = scsReq.getCarProductList().getCarProduct().get(0);
        //Add promCode to expected response when CarBehaviorAttributValue is enabled
        final String couponSupport = inventoryHelper.getCarBehaviorAttributValue(carProductRsp.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), carProductRsp.getCarInventoryKey().getSupplySubsetID(), 21L);
        if ("1".equals(couponSupport)) {
            carProductResponseExpected.getCarInventoryKey().getCarRate().setPromoCode(carProductReq.getCarInventoryKey().getCarRate().getPromoCode());
        }

        /// ----------------------verify the request ------------------------------
        /// we have to send a default rate category otherwise uAPI returns a validation error
        if (CompareUtil.isObjEmpty(carProductReq.getCarInventoryKey().getCarRate().getRateCategoryCode())) {
            carProductReq.getCarInventoryKey().getCarRate().setRateCategoryCode("Standard");
        }
        //Add CarItemID/SupplySubsetID to parsed request and response to compare
        carsInventoryRequesteEpected.setCarItemID(carProductReq.getCarInventoryKey().getCarItemID());
        carsInventoryRequesteEpected.setSupplySubsetID(carProductReq.getCarInventoryKey().getSupplySubsetID());
        carProductResponseExpected.getCarInventoryKey().setCarItemID(carProductReq.getCarInventoryKey().getCarItemID());
        carProductResponseExpected.getCarInventoryKey().setSupplySubsetID(carProductReq.getCarInventoryKey().getSupplySubsetID());

        // for corporateDiscountCode override
        String corporateDiscountCodeOverrideShopping = null;
        final List<SupplySubsetIDEntryType> subsetIDEntryTypeList = new ArrayList<SupplySubsetIDEntryType>();
        final SupplySubsetIDEntryType supplySubsetIDEntry = new SupplySubsetIDEntryType();
        supplySubsetIDEntry.setSupplySubsetID(carProductReq.getCarInventoryKey().getSupplySubsetID());
        subsetIDEntryTypeList.add(supplySubsetIDEntry);
        final List<SupplySubSetToWorldSpanSupplierItemMap> supplierItemMaps = inventoryHelper.getWorldSpanSupplierItemMap(subsetIDEntryTypeList);
        final boolean corporateDiscountCodeRequiredInShopping = CompareUtil.isObjEmpty(supplierItemMaps) ? false : supplierItemMaps.get(0).isCorporateDiscountCodeRequiredInShopping();
        if (corporateDiscountCodeRequiredInShopping) {
            corporateDiscountCodeOverrideShopping = supplierItemMaps.get(0).getCorporateDiscountCode();
        }
        if (!CompareUtil.isObjEmpty(corporateDiscountCodeOverrideShopping)) {
            carProductReq.getCarInventoryKey().getCarRate().setCorporateDiscountCode(corporateDiscountCodeOverrideShopping);
        }

        UapiMapCommonVerification.isCarInventoryKeyEqual(carProductReq.getCarInventoryKey(), carsInventoryRequesteEpected, "", false, true, false, false);

        //bsCode
        String bsFromDB = CompareUtil.isObjEmpty(supplierItemMaps) ? "" : supplierItemMaps.get(0).getIataAgencyCode();
        if (!CompareUtil.compareObject(bsFromDB, vrurReqeust.getBookingSource().getCode(), null, errorMsg.append("BookingSource: "))) {
            Assert.fail(String.format("BookingSource is not correctly mapped to VRUR request, DB: %s, VRUR request: %s!",
                    bsFromDB, vrurReqeust.getBookingSource().getCode()));
        }

        //PolicyList
        String expCarPolicyList = "";
        if (CompareUtil.isObjEmpty(scsReq.getCarPolicyCategoryCodeList())) {
            expCarPolicyList = "ALL";
        } else {
            Collections.sort(scsReq.getCarPolicyCategoryCodeList().getCarPolicyCategoryCode());
            final List<String> cpccs = new ArrayList<String>();
            String externalDomainValue = "";
            for (final String cpcc : scsReq.getCarPolicyCategoryCodeList().getCarPolicyCategoryCode()) {
                externalDomainValue = UAPICommonNodeReader.readExternalDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.CAR_POLICY_CATEGORY, cpcc, "");
                if (CompareUtil.isObjEmpty(externalDomainValue)) {
                    cpccs.add("ALL");
                } else {
                    cpccs.add(cpcc);
                }
            }
            expCarPolicyList = StringUtils.join(cpccs, ",");
        }
        if (!CompareUtil.compareObject(expCarPolicyList, vrurReqeust.getCarPolicyCategoryCodeList(), null, errorMsg.append("CarPolicyCategoryCodeList: "))) {
            Assert.fail(errorMsg.toString());
        }

        //ITCode & RateCategory
        for (SupplySubSetToWorldSpanSupplierItemMap mapInfo : supplierItemMaps)
        {
            if (!CompareUtil.compareObject(mapInfo.getItNumber(), vrurReqeust.getItCode(), null, errorMsg.append("ITCode: ")))
            {
                Assert.fail(String.format("ITCode is not correctly mapped to VRUR request, DB: %s, VSAR request: %s!\r\n",
                        mapInfo.getItNumber(), vrurReqeust.getItCode()));
            }

            //Hertz Pre-Paid
            //https://confluence/display/SSG/CASSS-9855+Hertz+Prepaid#CASSS-9855HertzPrepaid-Details
            if (null != mapInfo.getPrepaidBool() && mapInfo.getPrepaidBool().equals("1") &&
                    carProductReq.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 40)
            {
                uapiGetDetailsMapPrePayBooleanForHertzVerifier(vrurReqeust, vrurReqNode);
            }
        }

        //------------------------ verify the response --------------------------------
        final String pickupRspCarShuttleCategoryCode = carProductRsp.getCarPickupLocation().getCarShuttleCategoryCode();
        final String dropoffRspCarShuttleCategoryCode = carProductRsp.getCarDropOffLocation().getCarShuttleCategoryCode();
        final String pickupExpCarShuttleCategoryCode = carProductResponseExpected.getCarPickupLocation().getCarShuttleCategoryCode();
        final String dropoffExpCarShuttleCategoryCode = carProductResponseExpected.getCarDropOffLocation().getCarShuttleCategoryCode();

        //dropoff CarShuttleCategoryCode has no value in response if request has no value, but expected has value
        if (CompareUtil.isObjEmpty(dropoffRspCarShuttleCategoryCode) && !CompareUtil.isObjEmpty(pickupRspCarShuttleCategoryCode)
                && !CompareUtil.isObjEmpty(dropoffExpCarShuttleCategoryCode)) {
            carProductRsp.getCarDropOffLocation().setCarShuttleCategoryCode(pickupRspCarShuttleCategoryCode);
        }

        //bug 1062606, fixed by design, The reason "ShuttleToCounter" is returned because if nothing is returned from GDS we returned what is in the request.
        if (CompareUtil.isObjEmpty(pickupExpCarShuttleCategoryCode) && !CompareUtil.isObjEmpty(pickupRspCarShuttleCategoryCode)) {
            carProductResponseExpected.getCarPickupLocation().setCarShuttleCategoryCode(pickupRspCarShuttleCategoryCode);
        }
        if (CompareUtil.isObjEmpty(dropoffExpCarShuttleCategoryCode) && !CompareUtil.isObjEmpty(dropoffRspCarShuttleCategoryCode)) {
            carProductResponseExpected.getCarDropOffLocation().setCarShuttleCategoryCode(pickupRspCarShuttleCategoryCode);
        }

        //CarPickupLocation -> OpenSchedule.
        UapiMapCommonVerification.isCarLocationTypeEqual(carProductResponseExpected.getCarPickupLocation(), carProductRsp.getCarPickupLocation(), "CarPickupLocation: ");
        //CarDropOffLocation -> OpenSchedule.
        UapiMapCommonVerification.isCarLocationTypeEqual(carProductResponseExpected.getCarDropOffLocation(), carProductRsp.getCarDropOffLocation(), "CarDropOffLocation: ");

        // CarInventory
        UapiMapCommonVerification.isCarInventoryKeyEqual(carProductRsp.getCarInventoryKey(), carProductResponseExpected.getCarInventoryKey(), "", false, false, true, false);

        // CarPolicyList
        UapiMapCommonVerification.policeListMatch(carProductRsp.getCarPolicyList(), carProductResponseExpected.getCarPolicyList(), "CarPolicyList: ");

        // CarRateDetail
        UapiMapCommonVerification.conditionalCostPriceListMatch(carProductRsp.getCarRateDetail().getConditionalCostPriceList().getCostPrice(), carProductResponseExpected.getCarRateDetail().getConditionalCostPriceList().getCostPrice(), "CarRateDetail: ");

        //costList for Agency/GDSP car
        //bug 1046080 - bug fixed
        if (!BusinessModel.Merchant.equals(scenario.getBusinessModel())) {
            final List<CostType> costTypeList = new ArrayList<CostType>();
            for (CostType cost : carProductResponseExpected.getCostList().getCost()) {
                if (!(!CompareUtil.isObjEmpty(cost.getDescriptionRawText()) && cost.getDescriptionRawText().toUpperCase().contains("DROP CHARGE") && cost.getFinanceCategoryCode().contains("Fee") && cost.getFinanceApplicationCode().contains("Trip"))) {
                    costTypeList.add(cost);
                }
            }
            UapiMapCommonVerification.compareCostList(carProductRsp.getCostList().getCost(), costTypeList, scsReq.getCurrencyCode(), verificationContext.getOriginatingGuid(), true, httpClient);
            UapiMapCommonVerification.verifyCostAddupToTotal(carProductRsp.getCostList().getCost());
        }


        UapiMapCommonVerification.carVehicleOptionMatch(carProductResponseExpected.getCarVehicleOptionList(), carProductRsp.getCarVehicleOptionList(), "CarVehicleOption: ");
        UapiMapCommonVerification.carRentalLimitsMatch(carProductRsp.getCarRateDetail().getCarRentalLimits(), carProductResponseExpected.getCarRateDetail().getCarRentalLimits(), "CarRentalLimits: ");
        UapiMapCommonVerification.isCarMileageEqual(carProductRsp.getCarMileage(), carProductResponseExpected.getCarMileage(), "");

        //ReservationGuaranteeCategory
        if ((CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(carProductRsp.getReservationGuaranteeCategory()) && !CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(vrurResponse.getCarProduct().getReservationGuaranteeCategory()))
                || (!CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(carProductRsp.getReservationGuaranteeCategory()) && CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(vrurResponse.getCarProduct().getReservationGuaranteeCategory())))

        {
            Assert.fail(String.format("CCGuarantee is not correctly mapped from VRUR response to SCS response, SCS request: %s, VRUR response: %s!\r\n",
                    CompareUtil.isObjEmpty(carProductRsp.getReservationGuaranteeCategory()) ? "null" : carProductRsp.getReservationGuaranteeCategory(),
                    CompareUtil.isObjEmpty(vrurResponse.getCarProduct().getReservationGuaranteeCategory()) ? "null" : vrurResponse.getCarProduct().getReservationGuaranteeCategory()));
        }

    }

    public static void uapiVerifierMileageGetdetail(BasicVerificationContext verificationContext, GetDetailsVerificationInput detailsVerificationInput, GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput, CarProductType selectedCarProduct, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final TestScenario scenario = verificationContext.getScenario();
        StringBuilder errorMsg = new StringBuilder();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        //verify search
        String expFreeDistanceRatePeriodCode = "";
        final List<Node> vsarList = PojoXmlUtil.getNodesByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.CRS_MESSGE_TYPE);
        if (CompareUtil.isObjEmpty(vsarList)) {
            Assert.fail("No VSAR message find ! ");
        }
        //Parse request and response
        final Node vsarResNode = PojoXmlUtil.getNodeByTagName(vsarList.get(0), GDSMsgNodeTags.WorldSpanNodeTags.VSAR_RESPONSE_TYPE);
        VSARRsp vsarRsp = new VSARRsp(vsarResNode, selectedCarProduct.getCarInventoryKey(), scsDataSource, carsInventoryDs, scenario);
        expFreeDistanceRatePeriodCode = vsarRsp.getCarMileage().getFreeDistanceRatePeriodCode();

        CompareUtil.compareObject(expFreeDistanceRatePeriodCode, selectedCarProduct.getCarMileage().getFreeDistanceRatePeriodCode(), null, errorMsg);

        //verify cost&avail
        CompareUtil.compareObject(expFreeDistanceRatePeriodCode, costAndAvailabilityVerificationInput.getResponse().getCarProductList().getCarProduct().get(0).getCarMileage().getFreeDistanceRatePeriodCode(), null, errorMsg);

        //verify detail
        CompareUtil.compareObject(expFreeDistanceRatePeriodCode, detailsVerificationInput.getResponse().getCarProductList().getCarProduct().get(0).getCarMileage().getFreeDistanceRatePeriodCode(), null, errorMsg);
        if (!CompareUtil.isObjEmpty(errorMsg)) {
            Assert.fail(errorMsg.toString());
        }
    }

    public static void uapiCCGuaranteeVerifier(BasicVerificationContext verificationContext, GetDetailsVerificationInput detailsVerificationInput, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final TestScenario scenario = verificationContext.getScenario();
        final CarSupplyConnectivityGetDetailsResponseType response = detailsVerificationInput.getResponse();
        StringBuilder errorMsg = new StringBuilder();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        //get VSAR message
        final List<Node> vsarList = PojoXmlUtil.getNodesByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.CRS_MESSGE_TYPE);
        if (CompareUtil.isObjEmpty(vsarList)) {
            Assert.fail("No VSAR message find ! ");
        }
        //Parse request and response
        final Node vsarResNode = PojoXmlUtil.getNodeByTagName(vsarList.get(0), GDSMsgNodeTags.WorldSpanNodeTags.VSAR_RESPONSE_TYPE);
        VSARRsp vsarRsp = new VSARRsp(vsarResNode, response.getCarProductList().getCarProduct().get(0).getCarInventoryKey(), scsDataSource, carsInventoryDs, scenario);

        //get VRUR message
        final Node vrurReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VRUR_REQUEST_TYPE);
        final Node vrurResNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VRUR_RESPONSE_TYPE);
        if (CompareUtil.isObjEmpty(vrurReqNode) || CompareUtil.isObjEmpty(vrurResNode)) {
            Assert.fail("No request or response VRUR message found ! ");
        }
        //Parse request and response
        final VRURReq vrurReqeust = new VRURReq(vrurReqNode, scsDataSource, carsInventoryDs);
        final VRURRes vrurResponse = new VRURRes(vrurResNode, vrurReqeust.getInventoryKey(), scsDataSource, carsInventoryDs, SettingsProvider.USE_SPOOFER);

        final CarPosConfigDataSource posConfigDataSource = new CarPosConfigDataSource(DataSourceHelper.getWSCSDataSourse());
        final PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.GETDETAILS_DOCOSTANDAVAILREQUEST_ENABLE);
        posConfig.setJurisdictionCode(scenario.getJurisdictionCountryCode());
        posConfig.setCompanyCode(scenario.getCompanyCode());
        posConfig.setManagementUnitCode(scenario.getManagementUnitCode());
        final String getDetailsDoCostAndAvailEnable = CompareUtil.isObjEmpty(posConfigDataSource.getPosConfigValue(posConfig)) ?
                "1" : posConfigDataSource.getPosConfigValue(posConfig).get(0).getSettingValue();


        if (!CompareUtil.isObjEmpty(response) && !CompareUtil.isObjEmpty(response.getCarProductList())) {
            // Verify if CCGuarantee in GetDetails response is correct.
            // Get CC Guarantee from downstream GetDetails VRUR response.
            String sCCGuaranteeFromGetDetails = CompareUtil.isObjEmpty(vrurResponse.getCarProduct().getReservationGuaranteeCategory()) ? CommonConstantManager.ReservationGuaranteeCategory.OPTIONAL : vrurResponse.getCarProduct().getReservationGuaranteeCategory();
            String sCCGuaranteeFromCostAndAvail = "";
            String sAvailStatusCode = response.getCarProductList().getCarProduct().get(0).getAvailStatusCode();

            // If GetDetails enable to do CostAndAvail, get CC Guarantee from downstream GetCostAndAvail (VSAR or VAQ) response.
            if ("1".equals(getDetailsDoCostAndAvailEnable)) {
                sCCGuaranteeFromCostAndAvail = vsarRsp.isCcGuarantee() ? CommonConstantManager.ReservationGuaranteeCategory.REQUIRED : CommonConstantManager.ReservationGuaranteeCategory.OPTIONAL;
                sAvailStatusCode = vsarRsp.getAvailStatusCode();
            }

            // If CC Guaratee in VSAR|VAQ or VRUR|VRD is required, expected CC Guaratee in GetDetails response must be required.
            String expCCGuarantee = CommonConstantManager.ReservationGuaranteeCategory.OPTIONAL;
            if (CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(sCCGuaranteeFromGetDetails) || CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(sCCGuaranteeFromCostAndAvail)) {
                expCCGuarantee = CommonConstantManager.ReservationGuaranteeCategory.REQUIRED;
            }

            // Check if the CC Guarantee in GetDetails response is correct.
            CompareUtil.compareObject(expCCGuarantee, response.getCarProductList().getCarProduct().get(0).getReservationGuaranteeCategory(), null, errorMsg);

            // Check CC Guarantee in TP95 for GetDetails is correct. Wait to do ..

            // Check AvailStatusCode
            if (!sAvailStatusCode.equals(response.getCarProductList().getCarProduct().get(0).getAvailStatusCode())) {
                errorMsg.append(String.format("The value of AvailStatusCode is incorrect. AvailStatusCodeInGetDetail= %s, AvailStatusCodeInGetCostAndAvail= %s",
                        response.getCarProductList().getCarProduct().get(0).getAvailStatusCode(), sAvailStatusCode));
            }
            if(!CompareUtil.isObjEmpty(errorMsg)) {
                Assert.fail(errorMsg.toString());
            }
        }
    }

    //is prepay send in gds req Hertz+Prepaid
    //https://confluence/display/SSG/Test+plan+for+CASSS-9855+Hertz+Prepaid
    //https://jira.expedia.biz/browse/CASSS-10076
    public static void uapiGetDetailsMapPrePayBooleanForHertzVerifier(VRURReq vrurReqeust, Node vrurReq)
    {
        if (!vrurReqeust.getRateCategoryFromVRUR(vrurReq).equalsIgnoreCase(CommonConstantManager.RateCategory.PREPAY))
        {
            Assert.fail("\n Should send RateCategory=\"Prepay\" in VRUR request, but can't find.");
        }
    }

    public static void  verifyIfPrePayBooleanReturnInGetDetailResponseForHertz(GetDetailsVerificationInput getDetailsVerificationInput, DataSource carsInventoryDs) throws
            DataAccessException, ParserConfigurationException, SQLException
    {
        UapiMapCommonVerification commonVerifier = new UapiMapCommonVerification();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        commonVerifier.verifyIfPrePayBooleanReturnInProductForHertz(getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct().get(0), inventoryHelper);
    }
}
