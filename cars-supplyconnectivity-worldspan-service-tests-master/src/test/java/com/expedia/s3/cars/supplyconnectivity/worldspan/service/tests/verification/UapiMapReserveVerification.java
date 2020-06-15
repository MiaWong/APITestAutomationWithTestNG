package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationRemarkListType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationRemarkType;
import com.expedia.e3.data.cartypes.defn.v5.CarReserveFieldOverridesType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.PoSToWorldspanDefaultSegmentMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubset;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VCRRReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VCRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.DateTimeUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.VoucherUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
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
 * Created by yyang4 on 1/5/2017.
 */
public class UapiMapReserveVerification {

    private static final Logger logger = Logger.getLogger(SearchResponsesBasicVerification.class);

    public static void uapiMapVerifierWSCSReserve(BasicVerificationContext verificationContext, ReserveVerificationInput verificationInput, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final CarSupplyConnectivityReserveRequestType scsReq = verificationInput.getRequest();
        final CarSupplyConnectivityReserveResponseType scsRsp = verificationInput.getResponse();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            org.testng.Assert.fail("No GDS messages found ! ");
        }
        //Get VCRR
        final Node vcrrReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VCRR_REQUEST_TYPE);
        final Node vcrrRspNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VCRR_RESPONSE_TYPE);
        if (CompareUtil.isObjEmpty(vcrrReqNode) || CompareUtil.isObjEmpty(vcrrRspNode)) {
            org.testng.Assert.fail("No request or response VCRR message found ! ");
        }
        //Parse request and response
        final VCRRReq vcrrReq = new VCRRReq(vcrrReqNode, scsDataSource, carsInventoryDs);
        final VCRRRsp vcrrRsp = new VCRRRsp(vcrrRspNode, null, scsDataSource, carsInventoryDs);

        //Add CarItemID/SupplySubsetID to parsed request and response to compare
        vcrrReq.getCarInventoryKey().setCarItemID(scsReq.getCarProduct().getCarInventoryKey().getCarItemID());
        vcrrReq.getCarInventoryKey().setSupplySubsetID(scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID());
        vcrrRsp.getCarInventoryKey().setCarItemID(scsReq.getCarProduct().getCarInventoryKey().getCarItemID());
        vcrrRsp.getCarInventoryKey().setSupplySubsetID(scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID());
        //Verify request mapping
        final List<SupplySubset> supplySubsetIDs = new ArrayList<SupplySubset>();
        final SupplySubset supplySubset = new SupplySubset();
        supplySubset.setSupplySubsetID(scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID());
        supplySubsetIDs.add(supplySubset);
        final String corporateDiscountCode = VoucherUtil.getExpectedCorpDiscBasedOnFormat(scsReq.getCarProduct().getCarInventoryKey().getCarRate(),
                scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID(), inventoryHelper.getCarItemListBySubsetID(supplySubsetIDs).get(0).getCarItemID(), carsInventoryDs);
        scsReq.getCarProduct().getCarInventoryKey().getCarRate().setCorporateDiscountCode(corporateDiscountCode);
        final String rateCode = VoucherUtil.getExpectedRateCodeBasedOnFormat(scsReq.getCarProduct().getCarInventoryKey().getCarRate(),
                scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID(), scsReq.getCarProduct().getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), carsInventoryDs);
        scsReq.getCarProduct().getCarInventoryKey().getCarRate().setRateCode(rateCode);
        //Move loyalty number under CarRate to TravelerList
        if (!CompareUtil.isObjEmpty(scsReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram()) && !CompareUtil.isObjEmpty(scsReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode())) {
            final LoyaltyProgramType loyalty = new LoyaltyProgramType();
            loyalty.setLoyaltyProgramCategoryCode("Car");
            loyalty.setLoyaltyProgramCode(scsReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramCode());
            loyalty.setLoyaltyProgramMembershipCode(scsReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode());
            final LoyaltyProgramListType loyaltyProgramListType = new LoyaltyProgramListType();
            final List<LoyaltyProgramType> programTypeList = new ArrayList<LoyaltyProgramType>();
            loyaltyProgramListType.setLoyaltyProgram(programTypeList);
            programTypeList.add(loyalty);
            for (final LoyaltyProgramType loyaltyOriginal : scsReq.getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram()) {
                if (!CompareUtil.compareObject(loyaltyOriginal.getLoyaltyProgramCategoryCode(), "Car", null, null)) {
                    programTypeList.add(loyaltyOriginal);
                }
            }
            scsReq.getTravelerList().getTraveler().get(0).setLoyaltyProgramList(loyaltyProgramListType);
            vcrrRsp.getCarInventoryKey().getCarRate().setLoyaltyProgram(loyalty);
            scsReq.getCarProduct().getCarInventoryKey().getCarRate().setLoyaltyProgram(null);
        }
        //carInventoryKey compare
        UapiMapCommonVerification.isCarInventoryKeyEqual(vcrrReq.getCarInventoryKey(), scsReq.getCarProduct().getCarInventoryKey(), "", false, true, false, false);

        long rentalDays = DateTimeUtil.getDiffDays(scsReq.getCarProduct().getCarInventoryKey().getCarDropOffDateTime(), scsReq.getCarProduct().getCarInventoryKey().getCarPickUpDateTime());
        //Get expected Tour code and verify
        final String expTourCode = VoucherUtil.getExpectedTourCodeBasedOnFormat(scsReq.getCarProduct().getCarInventoryKey().getCarRate(),
                scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID(), rentalDays, carsInventoryDs);
        if (!(CompareUtil.isObjEmpty(expTourCode) && CompareUtil.isObjEmpty(vcrrReq.tourCode)) && !expTourCode.equals(vcrrReq.tourCode)) {
            Assert.fail(String.format("TourCode is not correctly mapped to VCRR request, expected: %s, VCRR request: %s!\r\n", expTourCode, vcrrReq.tourCode));
        }

        //Get expected voucher and verify
        /*final String expVoucher = VoucherUtil.getExpectedVoucherBasedOnFormat(scsReq.getReferenceList().getReference(), scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID(), carsInventoryDs);
        if (!CompareUtil.compareObject(expVoucher, vcrrReq.voucher, null, null)) {
            Assert.fail(String.format("Voucher is not correctly mapped to VCRR request, expected: %s, VCRR request: %s!\r\n", expVoucher, vcrrReq.voucher));
        }

        //Get expected SI and verify
        String expSI = VoucherUtil.getExpectedSupplementalInfoBasedOnFormat(scsReq.getReferenceList().getReference(), scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID(), rentalDays, carsInventoryDs);
        //Append no smoking -E.g: CCI-039ZR68 NOSMOKING
        if (!scsReq.getSmokingBoolean()) {
            if (CompareUtil.isObjEmpty(expSI)) {
                expSI = "NOSMOKING";
            } else {
                expSI = expSI + " NOSMOKING";
            }
        }
        if (CompareUtil.compareObject(expSI, vcrrReq.siCode, null, null)) {
            Assert.fail(String.format("SI is not correctly mapped to VCRR request, expected: %s, VCRR request: %s!\r\n", expSI, vcrrReq.siCode));
        }*/

        //cc card
        if (!CompareUtil.isObjEmpty(scsReq.getCreditCardFormOfPayment()) && !CompareUtil.isObjEmpty(scsReq.getCreditCardFormOfPayment().getCreditCard()) && !CompareUtil.isObjEmpty(scsReq.getCreditCardFormOfPayment().getCreditCard().getCreditCardNumberEncrypted())) {
            final String expireM = String.valueOf(scsReq.getCreditCardFormOfPayment().getCreditCard().getExpirationDate().getMonth());
            final String expireY = String.valueOf(scsReq.getCreditCardFormOfPayment().getCreditCard().getExpirationDate().getYear());
            //CAXXXXXXXXXXXX5390EXP2014-12
            final String expCCText = "CAXXXXXXXXXXXX" + scsReq.getCreditCardFormOfPayment().getCreditCard().getMaskedCreditCardNumber() + "EXP" + expireY + "-" + expireM;
            if (!CompareUtil.compareObject(vcrrReq.ccCardText, expCCText, null, null)) {
                Assert.fail(String.format("Actual CCCardText({0}) in VCRR request is not as expected {1}!\r\n", vcrrReq.ccCardText, expCCText));
            }
        }

        //Billing number
        /*String expBN = null;
        for (ReferenceType reference : scsReq.getReferenceList().getReference()) {
            if ("BillingNumber".equals(reference.getReferenceCategoryCode())) {
                expBN = reference.getReferenceCode();
            }
        }
        if (!(CompareUtil.isObjEmpty(expBN) && CompareUtil.isObjEmpty(vcrrReq.billingNumber)) && !expBN.equals(vcrrReq.billingNumber)) {
            Assert.fail(String.format("BillingNumber is not expected, expect: %s, actual: %s!", expBN == null ? "null" : expBN, vcrrReq.billingNumber == null ? "null" : vcrrReq.billingNumber));
        }*/

        //special equipment
        if(!CompareUtil.isObjEmpty(vcrrReq.getCarSpecialEquipmentListType()) && !CompareUtil.isObjEmpty(scsReq.getCarSpecialEquipmentList())) {
            UapiMapCommonVerification.isCarSpecialEquipmentListEqualDirectCompare(vcrrReq.getCarSpecialEquipmentListType().getCarSpecialEquipment(), scsReq.getCarSpecialEquipmentList().getCarSpecialEquipment(), "");
        }


        //TravelerList
        UapiMapCommonVerification.isTravelerListEqual(scsReq.getTravelerList().getTraveler(),vcrrReq.getTravelerListType().getTraveler(), false, "");

        //CarReservationRemarkList - Add some info to expected list before compare
        buildExpCarReservationRemarkList(scsReq);
        UapiMapCommonVerification.isCarReservationRemarkListEqual(vcrrReq.getCarReservationRemarkListType().getCarReservationRemark(), scsReq.getCarReservationRemarkList().getCarReservationRemark());

        //bsCode - If IATAOverrideBooking exist, we should use it, else use IATAAgencyCode
        final List<SupplySubsetIDEntryType> paramList = new ArrayList<SupplySubsetIDEntryType>();
        final SupplySubsetIDEntryType subsetIDEntryType = new SupplySubsetIDEntryType();
        subsetIDEntryType.setSupplySubsetID(scsReq.getCarProduct().getCarInventoryKey().getSupplySubsetID());
        paramList.add(subsetIDEntryType);
        final SupplySubSetToWorldSpanSupplierItemMap supplierItemMap = CompareUtil.isObjEmpty(inventoryHelper.getWorldSpanSupplierItemMap(paramList)) ? null : inventoryHelper.getWorldSpanSupplierItemMap(paramList).get(0);
        String bsFromDB = CompareUtil.isObjEmpty(supplierItemMap) ? "" : supplierItemMap.getIataAgencyCode();
        final String iataOoverrideDB = CompareUtil.isObjEmpty(supplierItemMap) ? "" : supplierItemMap.getIataOverrideBooking();
        if (!CompareUtil.isObjEmpty(iataOoverrideDB)) {
            bsFromDB = iataOoverrideDB;
        }
        if (!CompareUtil.compareObject(bsFromDB, vcrrReq.bsCode, null, null)) {
            Assert.fail(String.format("BookingSource is not correctly mapped to VCRR request, DB: %s, VCRR request: %s!\r\n", bsFromDB, vcrrReq.bsCode));
        }

        //branchCode and IATA - BranchCode/IATA for standalone, PackageBranchCode/IATAPackage for package

        String expBranchCode = null;
        String expIATA = null;
        final PoSToWorldspanDefaultSegmentMap posMap = scsHelper.getPoSToWorldspanDefaultSegmentMap(verificationContext.getScenario());
        if (scsReq.getCarProduct().getCarInventoryKey().getPackageBoolean()) {
            expBranchCode = posMap.getPackageBranchCode();
            expIATA = posMap.getIata();
        } else {
            expBranchCode = posMap.getBranchCode();
            expIATA = posMap.getIata();
        }
        if (expIATA != null && expIATA.length() > 7) {
            expIATA = expIATA.substring(0, 7);
        }
        if (!((CompareUtil.isObjEmpty(expBranchCode) && CompareUtil.isObjEmpty(vcrrReq.branchCode)) || expBranchCode.equals(vcrrReq.branchCode))) {
            Assert.fail(String.format("BranchCode is not correctly mapped to VCRR request, DB: %s, VCRR request: %s!\r\n", expBranchCode, vcrrReq.branchCode));
        }
        if (!((CompareUtil.isObjEmpty(expIATA) && CompareUtil.isObjEmpty(vcrrReq.iataNum)) || expIATA.equals(vcrrReq.iataNum))) {
            Assert.fail(String.format("IATA is not correctly mapped to VCRR request, DB: %s, VCRR request: %s!\r\n", expIATA, vcrrReq.iataNum));
        }

        //Verify response mapping
        //Add promCode to expected response when CarBehaviorAttributValue is enabled
        final String couponSupport = inventoryHelper.getCarBehaviorAttributValue(scsRsp.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), scsRsp.getCarReservation().getCarProduct().getCarInventoryKey().getSupplySubsetID(), 21L);
        if ("1".equals(couponSupport)) {
            vcrrRsp.carInventoryKey.getCarRate().setPromoCode(scsReq.getCarProduct().getCarInventoryKey().getCarRate().getPromoCode());
        }

        //carInventoryKey
        UapiMapCommonVerification.isCarInventoryKeyEqual(scsRsp.getCarReservation().getCarProduct().getCarInventoryKey(), vcrrRsp.getCarInventoryKey(), "", false, false, false, false);

        //costList

        UapiMapCommonVerification.compareCostList(scsRsp.getCarReservation().getCarProduct().getCostList().getCost(), vcrrRsp.getCostListType().getCost(), scsReq.getCurrencyCode(), verificationContext.getOriginatingGuid(), false, httpClient);

        //TravelerList
        vcrrRsp.getTravelerListType().getTraveler().get(0).getLoyaltyProgramList().setLoyaltyProgram(null);//loyalty number is mapped back to TravelerList
        UapiMapCommonVerification.isTravelerListEqual(scsRsp.getCarReservation().getTravelerList().getTraveler(), vcrrRsp.getTravelerListType().getTraveler(), false, "VCRR response to WSCS response map verify:");

        //ReferenceList
        //UapiMapCommonVerification.isReferenceListEqual(scsRsp.getCarReservation().getReferenceList().getReference(), vcrrRsp.getReferenceList().getReference(), "");


        //AdvisoryTextList
        UapiMapCommonVerification.isAdvisoryTextListEqual(scsRsp.getAdvisoryTextList().getAdvisoryText(), vcrrRsp.getAdvisoryTextListType().getAdvisoryText());

        //Book status code
        if (!CompareUtil.compareObject(scsRsp.getCarReservation().getBookingStateCode(), vcrrRsp.getBookingStateCode(), null, null)) {
            Assert.fail(String.format("BookingStateCode is not correctly mapped to SCS response, SCS response: %s, VCRR response: %s!\r\n", scsRsp.getCarReservation().getBookingStateCode(), vcrrRsp.bookingStateCode));
        }

        //special equipment
        if(!CompareUtil.isObjEmpty(scsRsp.getCarReservation().getCarSpecialEquipmentList()) && !CompareUtil.isObjEmpty(vcrrRsp.getCarSpecialEquipmentListType().getCarSpecialEquipment())) {
            UapiMapCommonVerification.isCarSpecialEquipmentListEqualDirectCompare(scsRsp.getCarReservation().getCarSpecialEquipmentList().getCarSpecialEquipment(), vcrrRsp.getCarSpecialEquipmentListType().getCarSpecialEquipment(), "VCRR response to WSCS response map verify: CarSpecialEquipment:CarSpecialEquipmentCode");
        }
        //carVendor/Location/carType should also be same between request and response
        UapiMapCommonVerification.isCarInventoryKeyEqual(scsReq.getCarProduct().getCarInventoryKey(), scsRsp.getCarReservation().getCarProduct().getCarInventoryKey(), "SCS request and response CarInventoryKey compare:", true, false, false, false);

        //Actually used VO/SI/RC/CD/IT/CP/FT/ID should be returned in CarReserveFieldOverrides
        CarReserveFieldOverridesType expCarReserveFieldOverrides = buildExpCarReserveFieldOverridesInRsp(scsReq, "", "", expTourCode);
        UapiMapCommonVerification.isCarReserveFieldOverridesEqual(scsRsp.getCarReserveFieldOverrides(), expCarReserveFieldOverrides, "WSCS reserve response CarReserveFieldOverrides verify:");


    }

    // <summary>
    // Add some info to expected CarReservationRemarkList
    // </summary>
    // <param name="reserveReq"></param>
    public static void buildExpCarReservationRemarkList(CarSupplyConnectivityReserveRequestType reserveReq) {
        //First Trverler's email address - Meichun on 5/23/2016: we don't need to send email for uapi
        //CarReservationRemark remarkEmail = new CarReservationRemark();
        //remarkEmail.CarReservationRemarkText = "U1-" + reserveReq.TravelerList[0].ContactInformation.EmailAddressEntryList[0].EmailAddress;
        //reserveReq.CarReservationRemarkList.Add(remarkEmail);

        //Lang ID
        final CarReservationRemarkType remarkLang = new CarReservationRemarkType();
        remarkLang.setCarReservationRemarkText("U49-LANG ID " + reserveReq.getAuditLogTrackingData().getAuditLogLanguageId());
        if(CompareUtil.isObjEmpty(reserveReq.getCarReservationRemarkList())){
            final CarReservationRemarkListType carReservationRemarkListType = new CarReservationRemarkListType();
            final List<CarReservationRemarkType> reservationRemarkTypeList = new ArrayList<CarReservationRemarkType>();
            carReservationRemarkListType.setCarReservationRemark(reservationRemarkTypeList);
            reserveReq.setCarReservationRemarkList(carReservationRemarkListType);
        }
        reserveReq.getCarReservationRemarkList().getCarReservationRemark().add(remarkLang);
        //Expedia Test PNR
        final CarReservationRemarkType remarkTestPNR = new CarReservationRemarkType();
        remarkTestPNR.setCarReservationRemarkText("Expedia Test PNR");
        reserveReq.getCarReservationRemarkList().getCarReservationRemark().add(remarkTestPNR);
    }

    // <summary>
    // Build expected CarReserveFieldOverrides based on actually used values
    // Refer to https://confluence/display/people/S2C1W8+Implement+PNRA+merchant-specific+fields
    // </summary>
    // <param name="reserveReq"></param>
    // <param name="expVO"></param>
    // <param name="expSI"></param>
    // <param name="expTourCode"></param>
    public static CarReserveFieldOverridesType buildExpCarReserveFieldOverridesInRsp(CarSupplyConnectivityReserveRequestType reserveReq, String expVO, String expSI, String expTourCode) {
        final CarReserveFieldOverridesType expCarReserveFieldOverrides = new CarReserveFieldOverridesType();

        //CarRate is the actually used RC,CD,CP,LoyaltyProgram
        //Note: the CarRate in this reserveReq should be the actually used one, so please override before passing in
        if(CompareUtil.isObjEmpty(expCarReserveFieldOverrides.getCarRate())){
            final CarRateType carRate = new CarRateType();
            expCarReserveFieldOverrides.setCarRate(carRate);
        }
        expCarReserveFieldOverrides.getCarRate().setCorporateDiscountCode(reserveReq.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode());
        expCarReserveFieldOverrides.getCarRate().setRateCode(reserveReq.getCarProduct().getCarInventoryKey().getCarRate().getRateCode());
        expCarReserveFieldOverrides.getCarRate().setPromoCode(reserveReq.getCarProduct().getCarInventoryKey().getCarRate().getPromoCode());
        if (!CompareUtil.isObjEmpty(reserveReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram()) && !CompareUtil.isObjEmpty(reserveReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode())) {
            expCarReserveFieldOverrides.getCarRate().setLoyaltyProgram(reserveReq.getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram());
        }
        //SI, TourCode;
       // expCarReserveFieldOverrides.setSupplementalInfoText(expSI);
        expCarReserveFieldOverrides.setTourCode(expTourCode);

        //VO to ReferenceList
        /*final ReferenceListType referenceList = new ReferenceListType();
        if (!CompareUtil.isObjEmpty(expVO)) {
            final ReferenceType reference = new ReferenceType();
            reference.setReferenceCategoryCode("Voucher");
            reference.setReferenceCode(expVO);
        }*/

        //FT - "Air" LoyaltyProgram under TravelerList
        if(!CompareUtil.isObjEmpty(reserveReq.getTravelerList().getTraveler().get(0).getLoyaltyProgramList())) {
            for (LoyaltyProgramType loyaltyP : reserveReq.getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram()) {
                if ("Air".equals(loyaltyP.getLoyaltyProgramCategoryCode())) {
                    expCarReserveFieldOverrides.setLoyaltyProgram(loyaltyP);
                }
                if ("Car".equals(loyaltyP.getLoyaltyProgramCategoryCode())) {
                    expCarReserveFieldOverrides.getCarRate().setLoyaltyProgram(loyaltyP);
                }
            }
        }

        return expCarReserveFieldOverrides;
    }

    public static void  verifyIfPrePayBooleanReturnInReserveForHertz(ReserveVerificationInput
                                                                             reserveVerificationInput, DataSource carsInventoryDs) throws
            DataAccessException, ParserConfigurationException, SQLException
    {
        UapiMapCommonVerification commonVerifier = new UapiMapCommonVerification();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        commonVerifier.verifyIfPrePayBooleanReturnInProductForHertz(reserveVerificationInput.getResponse()
                .getCarReservation().getCarProduct(), inventoryHelper);
    }
}

