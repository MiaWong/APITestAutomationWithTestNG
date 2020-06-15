package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.PoSToWorldspanDefaultSegmentMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VSARReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VSARRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn
        .v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn
        .v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyang4 on 12/29/2016.
 */
public class UapiMapCostAndAvailVerification {
    private static final Logger logger = Logger.getLogger(SearchResponsesBasicVerification.class);

    public static void uapiMapVerifierWSCSCostAndAvail(BasicVerificationContext verificationContext, GetCostAndAvailabilityVerificationInput verificationInput, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final TestScenario scenario = verificationContext.getScenario();
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType scsReq = verificationInput.getRequest();
        final CarSupplyConnectivityGetCostAndAvailabilityResponseType scsRsp = verificationInput.getResponse();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        //Get VSAR
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            org.testng.Assert.fail("No GDS messages found ! ");
        }
        final Node vsarReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VSAR_REQUEST_TYPE);
        final Node vsarResNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VSAR_RESPONSE_TYPE);
        if (CompareUtil.isObjEmpty(vsarReqNode) || CompareUtil.isObjEmpty(vsarResNode)) {
            org.testng.Assert.fail("No request or response VSAR message found ! ");
        }
        //Parse request and response
        final CarProductType carProductReq = scsReq.getCarProductList().getCarProduct().get(0);
        final CarProductType carProductRsq = scsRsp.getCarProductList().getCarProduct().get(0);
        final VSARReq vsarReq = new VSARReq(vsarReqNode, scsDataSource, carsInventoryDs,scenario);
        final VSARRsp vsarRsp = new VSARRsp(vsarResNode, carProductReq.getCarInventoryKey(), scsDataSource, carsInventoryDs,scenario);
        //Add CarItemID/SupplySubsetID to parsed request and response to compare
        vsarReq.getCarInventoryKey().setCarItemID(carProductReq.getCarInventoryKey().getCarItemID());
        vsarReq.getCarInventoryKey().setSupplySubsetID(carProductReq.getCarInventoryKey().getSupplySubsetID());
        vsarRsp.getCarInventoryKey().setCarItemID(carProductReq.getCarInventoryKey().getCarItemID());
        vsarRsp.getCarInventoryKey().setSupplySubsetID(carProductReq.getCarInventoryKey().getSupplySubsetID());

        //Add CD code to response if corporateRate is true
        if (vsarRsp.isCorporateRate() && !CompareUtil.isObjEmpty(vsarReq.getCarInventoryKey().getCarRate().getCorporateDiscountCode())) {
            vsarRsp.getCarInventoryKey().getCarRate().setCorporateDiscountCode(vsarReq.getCarInventoryKey().getCarRate().getCorporateDiscountCode());
        }

        //Verify request mapping
        carProductReq.getCarInventoryKey().getCarRate().setRatePeriodCode(null);
        final List<SupplySubsetIDEntryType> subsetIDEntryTypeList = new ArrayList<SupplySubsetIDEntryType>();
        final SupplySubsetIDEntryType supplySubsetIDEntry = new SupplySubsetIDEntryType();
        supplySubsetIDEntry.setSupplySubsetID(carProductReq.getCarInventoryKey().getSupplySubsetID());
        subsetIDEntryTypeList.add(supplySubsetIDEntry);
        final List<SupplySubSetToWorldSpanSupplierItemMap> supplierItemMapList = inventoryHelper.getWorldSpanSupplierItemMap(subsetIDEntryTypeList);
        final SupplySubSetToWorldSpanSupplierItemMap supplierItemMap = CompareUtil.isObjEmpty(supplierItemMapList) ? null : supplierItemMapList.get(0);
        //Get CD code from DB if not exist in request
        if (CompareUtil.isObjEmpty(carProductReq.getCarInventoryKey().getCarRate().getCorporateDiscountCode())) {
            final String cdFromDB = CompareUtil.isObjEmpty(supplierItemMap) ? "" : supplierItemMap.getCorporateDiscountCode();
            final boolean cdSent = CompareUtil.isObjEmpty(supplierItemMap) ? false : supplierItemMap.isCorporateDiscountCodeRequiredInShopping();
            if (cdSent && !CompareUtil.isObjEmpty(cdFromDB)) {
                carProductReq.getCarInventoryKey().getCarRate().setCorporateDiscountCode(cdFromDB);
            }
        }

        //carInventoryKey
        UapiMapCommonVerification.isCarInventoryKeyEqual(vsarReq.carInventoryKey, carProductReq.getCarInventoryKey(), "", false, true, false, false);

        //bsCode
        String bsFromDB = CompareUtil.isObjEmpty(supplierItemMap) ? "" : supplierItemMap.getIataAgencyCode();
        if (!CompareUtil.compareObject(vsarReq.getBsCode(), bsFromDB, null, errorMsg.append("BookingSource: "))) {
            Assert.fail(String.format("BookingSource is not correctly mapped to VSAR request, DB: %s, VSAR request: %s!\r\n", bsFromDB, vsarReq.bsCode));
        }

        //branchCode - BranchCode for standalone, PackageBranchCode for package
        String expBranchCode = null;
        final PoSToWorldspanDefaultSegmentMap poSToWorldspan = scsHelper.getPoSToWorldspanDefaultSegmentMap(verificationContext.getScenario());
        if (carProductReq.getCarInventoryKey().getPackageBoolean()) {
            expBranchCode = CompareUtil.isObjEmpty(poSToWorldspan) ? "" : poSToWorldspan.getPackageBranchCode();
        } else {
            expBranchCode = CompareUtil.isObjEmpty(poSToWorldspan) ? "" : poSToWorldspan.getBranchCode();
        }
        if (!CompareUtil.compareObject(vsarReq.branchCode, expBranchCode, null, errorMsg)) {
            Assert.fail(String.format("BranchCode is not correctly mapped to VSAR request, DB: %s, VSAR request: %s!\r\n", expBranchCode, vsarReq.branchCode));
        }

        //ITCode & RateCategory
        for (SupplySubSetToWorldSpanSupplierItemMap mapInfo : supplierItemMapList) {
            if (!((CompareUtil.isObjEmpty(mapInfo.getItNumber()) &&
                    CompareUtil.isObjEmpty(vsarReq.getTourCodeList())) ||
                    vsarReq.getTourCodeList().contains(mapInfo.getItNumber()))) {
                Assert.fail(String.format("ITCode is not correctly mapped to VSAR request, DB: %s, VSAR request: %s!\r\n",
                        mapInfo.getItNumber(), StringUtils.join(vsarReq.getTourCodeList(), ",")));
            }

            //is prepay send in gds req Hertz+Prepaid
            //https://confluence/display/SSG/Test+plan+for+CASSS-9855+Hertz+Prepaid
            //https://jira.expedia.biz/browse/CASSS-10076
            if(null != mapInfo.getPrepaidBool() && mapInfo.getPrepaidBool().equals("1")
                    && carProductReq.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 40)
            {
                if(!vsarReq.getRateCategory().equalsIgnoreCase(CommonConstantManager.RateCategory.PREPAY))
                {
                    Assert.fail("\n Should send RateCategory=\"Prepay\" in VSAR request, but can't find.");
                }
            }
        }

        //Verify response mapping
        //Add loyalty number to expected response
        if (!CompareUtil.isObjEmpty(carProductRsq.getCarInventoryKey().getCarRate().getLoyaltyProgram()) && !CompareUtil.isObjEmpty(carProductRsq.getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode())) {
            vsarRsp.getCarInventoryKey().getCarRate().setLoyaltyProgram(carProductReq.getCarInventoryKey().getCarRate().getLoyaltyProgram());
        }
        //Add promCode to expected response when CarBehaviorAttributValue is enabled
        final String couponSupport = inventoryHelper.getCarBehaviorAttributValue(carProductRsq.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), carProductRsq.getCarInventoryKey().getSupplySubsetID(), 21L);
        if ("1".equals(couponSupport)) {
            vsarRsp.getCarInventoryKey().getCarRate().setPromoCode(carProductReq.getCarInventoryKey().getCarRate().getPromoCode());
        }
        //carInventoryKey
        UapiMapCommonVerification.isCarInventoryKeyEqual(carProductRsq.getCarInventoryKey(), vsarRsp.getCarInventoryKey(), "", false, false, false, false);
        //CCGuarantee
        if (!((CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(carProductRsq.getReservationGuaranteeCategory()) && vsarRsp.isCcGuarantee())
                || !CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals(carProductRsq.getReservationGuaranteeCategory()) && !vsarRsp.isCcGuarantee())) {
            Assert.fail(String.format("CCGuarantee is not correctly mapped from VSAR response to SCS response, SCS request: %s, VSAR response: %s!\r\n", carProductRsq.getReservationGuaranteeCategory(), vsarRsp.ccGuarantee));
        }
        //availStatusCode
        if (!CompareUtil.compareObject(carProductRsq.getAvailStatusCode(), vsarRsp.availStatusCode, null, errorMsg)) {
            Assert.fail(String.format("AvailStatusCode is not correctly mapped to SCS response, SCS response: %s, VSAR response: %s!\r\n", carProductRsq.getAvailStatusCode(), vsarRsp.availStatusCode));
        }
        //costList for Agency/GDSP car
        if (!BusinessModel.Merchant.equals(scenario.getBusinessModel())) {
            UapiMapCommonVerification.compareCostList(carProductRsq.getCostList().getCost(), vsarRsp.getCostList().getCost(), scsReq.getCurrencyCode(), verificationContext.getOriginatingGuid(), true, httpClient);
        }

        //carMileage
        UapiMapCommonVerification.isCarMileageEqual(carProductRsq.getCarMileage(), vsarRsp.getCarMileage(), "CarMileage compare between VSAR response and SCS response:");

        //carVendor/Location/carType should also be same between request and response
        UapiMapCommonVerification.isCarInventoryKeyEqual(carProductReq.getCarInventoryKey(), carProductRsq.getCarInventoryKey(), "", true, false, false, false);
    }

    public static void  verifyIfPrePayBooleanReturnInCostAndAvailResponseForHertz(GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput, DataSource carsInventoryDs) throws
            DataAccessException, ParserConfigurationException, SQLException
    {
        UapiMapCommonVerification commonVerifier = new UapiMapCommonVerification();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        commonVerifier.verifyIfPrePayBooleanReturnInProductForHertz(costAndAvailabilityVerificationInput.getResponse().getCarProductList().getCarProduct().get(0), inventoryHelper);
    }
}
