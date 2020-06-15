package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping.ASCSReserve;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.verification.ReserveRequestGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.verification.ReserveResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.verification.ReserveResponseGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miawang on 2/12/2017.
 */
public class ReserveVerificationHelper {


    //-------------------------------------------- Reserve ------------------
    public static String reserveBasicVerification(ReserveVerificationInput reserveVerificationInput,
                                                  TestScenario scenarios,
                                                  String guid,
                                                  Logger logger) throws Exception {
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(null, guid, scenarios);

        final IVerification.VerificationResult result = new ReserveResponseBasicVerification().verify(reserveVerificationInput, reserveVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RESERVE_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //reserve response GDSMsgMap
    public static String reserveResponseGDSMsgMappingVerification(ReserveVerificationInput reserveVerificationInput,
                                                                  SpooferTransport spooferTransport,
                                                                  TestData testData,
                                                                  Logger logger) throws IOException {
        final Document spooferTransactions = spooferTransport.retrieveRecords(testData.getGuid());

        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(), testData.getScenarios());

        ReserveResponseGDSMsgMappingVerification gdsMsgVerifier = new ReserveResponseGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(reserveVerificationInput, reserveVerificationContext);

        StringBuilder errorMsg = new StringBuilder();
        if (StringUtil.isNotBlank(testData.getBillingNumber()))
        {
            BillingNumberVerify(reserveVerificationInput, reserveVerificationContext, errorMsg);
        }

        if (!result.isPassed() || StringUtil.isNotBlank(errorMsg.toString()))
        {
            String  error = result.toString() + "\n" + errorMsg.toString();

            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RESERVE_VERIFICATION_PROMPT + error);
            }
            Assert.fail(error);
        }

        return null;


    }

    private static void BillingNumberVerify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext reserveVerificationContext, StringBuilder errorMsg)
    {
        if(!reserveVerificationInput.getRequest().getBillingCode().equals(reserveVerificationInput.getResponse().getCarReservation().getPaymentInfo().getBillingCode()))
        {
            errorMsg.append("Reserve request BillingCode is not equal to reserve response BillingCode.\n");
        }
        final Node node_ACSQ_CAR_SELL_REQUEST = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(reserveVerificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_REQUEST_TYPE);
        Node billingDetailsNode = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "billingDetails");
        String billingcode = "";
        if (null != billingDetailsNode)
        {
            billingcode = billingDetailsNode.getTextContent();
        }
        if(StringUtil.isBlank(billingcode) || !billingcode.contains(reserveVerificationInput.getRequest().getBillingCode()))
        {
            errorMsg.append("BillingCode  is not send down in Car_sell request.\n");
        }
        //For hertz delivery/collection booking with supplier config Reserve.deliveryCollectionBillingGuarantee/enable ON, billing number should be sent in payment node to ACSQ
        /*<payment>
        <formOfPayment>
        <type>ZE</type>
        <extendedPayment>GUA</extendedPayment>
        <fopFreeText>264970319986</fopFreeText>
        </formOfPayment>
        </payment>*/
        String actGDSPaymentValues = "";
        Node paymentNode = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "formOfPayment");
        if(null != paymentNode && null != PojoXmlUtil.getNodeByTagName(paymentNode, "fopFreeText"))
        {
            actGDSPaymentValues = PojoXmlUtil.getNodeByTagName(paymentNode, "type").getTextContent() +
            PojoXmlUtil.getNodeByTagName(paymentNode, "extendedPayment").getTextContent() +
            PojoXmlUtil.getNodeByTagName(paymentNode, "fopFreeText").getTextContent();
        }
        String expGDSPamentValues = "";
        if(40 == reserveVerificationInput.getRequest().getCarProduct().getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()
                && (null != reserveVerificationInput.getRequest().getDeliveryLocation() || null != reserveVerificationInput.getRequest().getCollectionLocation()))
        {
            expGDSPamentValues = "ZEGUA" + reserveVerificationInput.getRequest().getBillingCode();
        }
        if(!expGDSPamentValues.equals(actGDSPaymentValues)) {
            errorMsg.append(String.format("GDS formOfPayment(%s) is not as expected:%s.\n", actGDSPaymentValues, expGDSPamentValues));
        }
        final Node node_APRQ_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(reserveVerificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.APRQ_PNR_REPLY_TYPE);
        Node electronicVoucherNumberNode = PojoXmlUtil.getNodeByTagName(node_APRQ_RESPONSE, "electronicVoucherNumber");
        String voucherCode = "";
        if (null != electronicVoucherNumberNode)
        {
            voucherCode = PojoXmlUtil.getNodeByTagName(electronicVoucherNumberNode, "number").getTextContent();
        }
        if (StringUtil.isNotBlank(voucherCode) && !voucherCode.equals(reserveVerificationInput.getResponse().getCarReservation().getPaymentInfo().getPaymentVoucherCode()))
        {
            errorMsg.append("Voucher Code in APRQ response is not equal to in reserve response. \n");
        }
    }


    public static CarReservationType getCarReservationType(BasicVerificationContext verificationContext, CarInventoryKeyType reqInventoryKey, List remarks)
    {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        ASCSReserve reserveMapping = new ASCSReserve();
        StringBuffer eMsg = new StringBuffer();

        CarReservationType carReservation = new CarReservationType();
        reserveMapping.buildCarReservationCar(verificationContext, reqInventoryKey, carReservation, scsDataSource, eMsg);
        if (!StringUtils.isEmpty(eMsg.toString())) {
            remarks.add(eMsg);
        }
        return carReservation;
    }

    public static void verifyASCSReserve(ReserveVerificationInput input, SpooferTransport spooferTransport,
                                  TestData parameters, List<CommonEnumManager.VerifyType> verifyTypes) throws IOException
    {

        List remarks = new ArrayList();
        final Document spooferTransactions = spooferTransport.retrieveRecords(parameters.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, parameters.getGuid(), parameters.getScenarios());

        final CarReservationType actCarReservation = input.getResponse().getCarReservation();
        final CarInventoryKeyType reqInventoryKey = input.getRequest().getCarProduct().getCarInventoryKey();
        final CarReservationType expectCarReservation = getCarReservationType(verificationContext, reqInventoryKey, remarks);

        final Node node_ACSQ_CAR_SELL_REQUEST = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_REQUEST_TYPE);
        final Node node_ACSQ_CAR_SELL_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_RESPONSE_TYPE);

        StringBuilder errorMsg = new StringBuilder();
        //CarReservationRemark
        if(verifyTypes.contains(CommonEnumManager.VerifyType.CarReservationRemark))
        {
            carReservationRemarkVerify(node_ACSQ_CAR_SELL_REQUEST, input, errorMsg);
        }
        //vehicleOptionList
        if(verifyTypes.contains(CommonEnumManager.VerifyType.SpecialEquipment))
        {
            verifySpecialEquipmentCarSell(input, remarks, node_ACSQ_CAR_SELL_REQUEST, node_ACSQ_CAR_SELL_RESPONSE, errorMsg);
            if (!CompareUtil.compareObject(expectCarReservation.getCarProduct().getCarVehicleOptionList(), actCarReservation.getCarProduct().getCarVehicleOptionList(), null, errorMsg, true))
            {
                remarks.add(errorMsg.toString() + "\n");
            }
        }
        if (verifyTypes.contains(CommonEnumManager.VerifyType.CostAndCalculate))
        {
            if (!CompareUtil.compareObject(actCarReservation.getCarProduct().getCostList()
                    , expectCarReservation.getCarProduct().getCostList(), null, errorMsg, true))
            {
                remarks.add(errorMsg.toString() + "\n");
            }
            if (!CompareUtil.compareObject(actCarReservation.getCarProduct().getCarRateDetail().getConditionalCostPriceList()
                    , expectCarReservation.getCarProduct().getCarRateDetail().getConditionalCostPriceList(), null, errorMsg, true))
            {
                remarks.add(errorMsg.toString() + "\n");
            }
        }

        //Customer
        if(verifyTypes.contains(CommonEnumManager.VerifyType.Customer))
        {
        if (!CompareUtil.compareObject(input.getRequest().getCustomer(), actCarReservation.getCustomer(), null, errorMsg, true))
        {
            remarks.add(errorMsg.toString() + "\n");
        }
        }

        //TravelerList
        if(verifyTypes.contains(CommonEnumManager.VerifyType.TravelerList))
        {
            if (!CompareUtil.compareObject(input.getRequest().getTravelerList(), actCarReservation.getTravelerList(), null, errorMsg, true))
            {
                remarks.add(errorMsg.toString() + "\n");
            }
        }
        //ReferenceList
        if(verifyTypes.contains(CommonEnumManager.VerifyType.ReferenceList))
        {
            if (!CompareUtil.compareObject(expectCarReservation.getReferenceList(), actCarReservation.getReferenceList(), null, errorMsg, true))
            {
                remarks.add(errorMsg.toString() + "\n");
            }
        }
        //BookingStateCodeVerify
        if(verifyTypes.contains(CommonEnumManager.VerifyType.BookingStateCode))
        {
            if (!expectCarReservation.getBookingStateCode().equals(actCarReservation.getBookingStateCode()))
            {
                remarks.add("expected BookingStateCode=" + expectCarReservation.getBookingStateCode() + " actual value =" + actCarReservation.getBookingStateCode());
            }
        }

        //verify CCCard is send in car_sell request
        if(verifyTypes.contains(CommonEnumManager.VerifyType.CCCard))
        {
            Node formOfPaymentNode = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "formOfPayment");
            String cardVendorCode = (null != formOfPaymentNode) ? PojoXmlUtil.getNodeByTagName(formOfPaymentNode, "vendorCode").getTextContent()
                    : "";
            //creditCardNumber
            String cardNumber = (null != formOfPaymentNode) ? PojoXmlUtil.getNodeByTagName(formOfPaymentNode, "creditCardNumber").getTextContent() : "";
            String expCardVendorCode = "Airplus".equals(input.getRequest().getCreditCardFormOfPayment().getCreditCard().getCreditCardSupplierCode())
                    ? "TP" : "CA";
            String expCardNumber = "Airplus".equals(input.getRequest().getCreditCardFormOfPayment().getCreditCard().getCreditCardSupplierCode())
                    ? "122001010828235" : "5105781454975390";

            //If Accreditive loyalty number exist for AirPlus card, then AirPlus should not be sent
            //https://confluence.expedia.biz/display/SSG/TFS+693903+-+Amadeus+Support+Accredetive+Loyalty+Number+with+Airplus+Card+Design
            if(null != input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList()
                    && CollectionUtils.isNotEmpty(input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram())
                    && "Accreditive".equals(input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram().get(0).getLoyaltyProgramCategoryCode()))
            {
                expCardVendorCode = "";
                expCardNumber = "";
            }

            if (null == formOfPaymentNode && StringUtils.isNotBlank(expCardNumber))
            {
                remarks.add("CCCard value is not send down  in Car_sell request.");
            }
            else if(!cardVendorCode.equals(expCardVendorCode) || !cardNumber.equals(expCardNumber))
            {
                remarks.add(String.format("vendorCode(exp:%s,act: %s) or creditCardNumber(exp:%s,act: %s) not not correctly sent down  in Car_sell request formOfPayment node.",
                        expCardVendorCode, cardVendorCode, expCardNumber, cardNumber));
            }
        }

        //DCLocation
        if(verifyTypes.contains(CommonEnumManager.VerifyType.DCLocation))
        {
            ///1  Delivery and Collection maching in CarSell request
            CustomerLocationType delLocation_req = input.getRequest().getDeliveryLocation();
            CustomerLocationType colLocation_req = input.getRequest().getCollectionLocation();
            if (null != delLocation_req  && delLocation_req.getCustomerLocationCode() ==null
                    && (null == delLocation_req.getAddress() || delLocation_req.getAddress().getFirstAddressLine() == null))
            {
                delLocation_req = null;
            }
            if (null != colLocation_req && colLocation_req.getCustomerLocationCode() == null
                    && (null == colLocation_req.getAddress() || colLocation_req.getAddress().getFirstAddressLine() == null))
            {
                colLocation_req = null;
            }

            //Phone phone = request.TravelerList.Traveler[0].ContactInformation.PhoneList.Phone[0];

            PhoneType phone_del = delLocation_req == null ? null : delLocation_req.getPhone();
            PhoneType phone_col = colLocation_req == null ? null : colLocation_req.getPhone();

            //String phoneNumber = phone.PhoneCountryCode + phone.PhoneAreaCode + phone.PhoneNumber;
            String phoneNumber_del = phone_del==null?null:(phone_del.getPhoneCountryCode() + phone_del.getPhoneAreaCode() + phone_del.getPhoneNumber());
            String phoneNumber_col = phone_col==null?null:(phone_col.getPhoneCountryCode() + phone_col.getPhoneAreaCode() + phone_col.getPhoneNumber());
            if (phoneNumber_del==null&&phoneNumber_col==null)
            {
                remarks.add("There was no phone number exist in Del&Col node list.");
            }

            mapInCarSellRequest(remarks, node_ACSQ_CAR_SELL_REQUEST, errorMsg, delLocation_req, colLocation_req, phoneNumber_del, phoneNumber_col);

            // 2  mapping in CarSell response
            mapCarSellReponse(remarks, node_ACSQ_CAR_SELL_RESPONSE, delLocation_req, colLocation_req, phoneNumber_del, phoneNumber_col);

            //4 check the Del&Col node exist in reserve response
            checkDelColInResponse(input, remarks, delLocation_req, colLocation_req);
        }

        if(verifyTypes.contains(CommonEnumManager.VerifyType.LoyaltyNumber))
        {
            String rQ ="", rN = "";
            //get customerReferences from Car_Sell request
            Node customerReferencesNode = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "customerReferences");
            if (null != customerReferencesNode)
            {
                //rQ = xmlDoc.SelectSingleNode("//ns0:Car_Sell/ns0:sellData/ns0:customerInfo/ns0:customerReferences/ns0:referenceQualifier", xmgr).InnerText.Trim();
                //rN = xmlDoc.SelectSingleNode("//ns0:Car_Sell/ns0:sellData/ns0:customerInfo/ns0:customerReferences/ns0:referenceNumber", xmgr).InnerText.Trim();
                 rQ = PojoXmlUtil.getNodeByTagName(customerReferencesNode, "referenceQualifier").getTextContent().trim();
                 rN = PojoXmlUtil.getNodeByTagName(customerReferencesNode, "referenceNumber").getTextContent().trim();

                //when CD code exist in DB, loyalty number will be sent under otherCustomerRef
                if (rQ.equals("CD"))
                {
                    Node otherCustomerRefNode = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "otherCustomerRef");
                    if (null != otherCustomerRefNode)
                    {
                       //xmlDoc.SelectSingleNode("//ns0:Car_Sell/ns0:sellData/ns0:customerInfo/ns0:otherCustomerRef/ns0:referenceQualifier", xmgr).InnerText.Trim();
                       //rN = xmlDoc.SelectSingleNode("//ns0:Car_Sell/ns0:sellData/ns0:customerInfo/ns0:otherCustomerRef/ns0:referenceNumber", xmgr).InnerText.Trim();
                        rQ =  PojoXmlUtil.getNodeByTagName(otherCustomerRefNode, "referenceQualifier").getTextContent().trim();
                        rN = PojoXmlUtil.getNodeByTagName(otherCustomerRefNode, "referenceNumber").getTextContent().trim();
                    }
                }
            }

            String loyaltyNumberInReserveRequest = "";
            if(null != input.getRequest().getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram() &&
                StringUtils.isNotBlank(input.getRequest().getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode())
                    ) {
                loyaltyNumberInReserveRequest = input.getRequest().getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode();
            }

            if(null != input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList()
                    && CollectionUtils.isNotEmpty(input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram())
                    && !"Air".equals(input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram().get(0).getLoyaltyProgramCategoryCode()))
            {
                loyaltyNumberInReserveRequest = input.getRequest().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram().get(0).getLoyaltyProgramMembershipCode();
            }

            //Verify Loyalty Number passed down through Car_Sell correctly
            if (!rN.equals(loyaltyNumberInReserveRequest))
            {
                remarks.add("Loyalty Number in ReserveRequest is " + loyaltyNumberInReserveRequest + ", but it is " + rN + " in Car_Sell request!");
            }
            //Verify referenceQualifier = 1 in Car_Sell request
            if (!rQ.equals("1"))
            {
                remarks.add( "referenceQualifier in Car_Sell request is " + rQ + ", but not expected 1!");
            }
        }

        if(verifyTypes.contains(CommonEnumManager.VerifyType.FrequentFlyerNumber))
        {
            String rC ="", rN = "";
            //get customerReferences from Car_Sell request
            Node fFlyerNbrNode = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "fFlyerNbr");
            if (null != fFlyerNbrNode)
            {
                rC = PojoXmlUtil.getNodeByTagName(fFlyerNbrNode, "carrier").getTextContent().trim();
                rN = PojoXmlUtil.getNodeByTagName(fFlyerNbrNode, "number").getTextContent().trim();

            }

            if (!rN.equals("987654321"))
            {
                remarks.add("Loyalty Number in ReserveRequest is " + 987654321 + ", but it is " + rN + " in Car_Sell request!");
            }
            //Verify referenceQualifier = 1 in Car_Sell request
            if (!rC.equals("AA"))
            {
                remarks.add( "carrier in Car_Sell request is " + rC + ", but not expected AA!");
            }
        }

        if(verifyTypes.contains(CommonEnumManager.VerifyType.AnalyticalCode))
        {
            //Get DescriptiveBillingInfoList from amadeus reserve response
            DescriptiveBillingInfoListType reserveResponseList = input.getResponse().getCarReservation().getPaymentInfo().getDescriptiveBillingInfoList();

            String reserveRequestEDI = "";
            if (null != input.getRequest().getDescriptiveBillingInfoList() && !CollectionUtils.isEmpty(input.getRequest().getDescriptiveBillingInfoList().getDescriptiveBillingInfo())
                    && input.getRequest().getDescriptiveBillingInfoList().getDescriptiveBillingInfo().get(0).getKey().equals("EDIDATA")) {
                reserveRequestEDI = input.getRequest().getDescriptiveBillingInfoList().getDescriptiveBillingInfo().get(0).
                        getValue().replace("Einführung", "Einfuhrung");
            }
            //Verify Billing number AnalyticalCode or EDI data in Car_Sell request
            if(StringUtil.isNotBlank(reserveRequestEDI)) {
                List<Node> billingInfoNodes = PojoXmlUtil.getNodesByTagName(node_ACSQ_CAR_SELL_REQUEST, "billingInfo");
                String billingDesc = "";
                for (Node billingNode : billingInfoNodes) {
                    if ("902".equals(PojoXmlUtil.getNodeByTagName(billingNode, "billingQualifier").getTextContent())) {
                        billingDesc = PojoXmlUtil.getNodeByTagName(billingNode, "billingDetails").getTextContent();
                    }
                }
                if (!billingDesc.equals(reserveRequestEDI)) {
                    remarks.add(String.format("Car_sell request BillingDescription(%s) is not as expected value(%s) in amadeusSCS reserve request .\n",
                            billingDesc, reserveRequestEDI));
                }
                //Verify response
                if (!reserveResponseList.getDescriptiveBillingInfo().get(0).getValue().equals(reserveRequestEDI)) {
                    remarks.add(String.format("Reserve response BillingDescription(%s) is not as expected value(%s) in amadeusSCS reserve request .\n",
                            reserveResponseList.getDescriptiveBillingInfo().get(0).getValue(), reserveRequestEDI));
                }
                //no ADBI should not be sent for billing code description
                final Node node_ADBI_Request = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ADBI_PAY_MANAGEDBIDATA_REQUEST);
                if(input.getRequest().getDescriptiveBillingInfoList().getDescriptiveBillingInfo().size() ==1 && null != node_ADBI_Request)
                {
                    remarks.add("ADBI should not be sent for non-AirPlus description.\n");
                }
            }
            //Airplus AnalyticalCode
            if(null != input.getRequest().getCreditCardFormOfPayment() && null != input.getRequest().getCreditCardFormOfPayment().getCreditCard()
                    && "Airplus".equals(input.getRequest().getCreditCardFormOfPayment().getCreditCard().getCreditCardSupplierCode()))
            {
                //Get DescriptiveBillingInfoList from amadeus reserve request
                DescriptiveBillingInfoListType reserveRequestList = input.getRequest().getDescriptiveBillingInfoList();
                //Remove EDI data(first one)
                if(StringUtil.isNotBlank(reserveRequestEDI)) {
                    List<DescriptiveBillingInfoType> expectedDBIList = new ArrayList<>();
                    for(int i= 1; i< input.getRequest().getDescriptiveBillingInfoList().getDescriptiveBillingInfo().size(); i++)
                    {
                        expectedDBIList.add(input.getRequest().getDescriptiveBillingInfoList().getDescriptiveBillingInfo().get(i));
                    }
                    reserveRequestList.setDescriptiveBillingInfo(expectedDBIList);
                }
                //Need to send actual PNR to GDS if PNR key and pnr_value is sent, non-ascii need to to be converted to ascii
                setActualPNRAndConvertNonAsciiToDescriptiveBillingInfoList(input.getRequest(), input.getResponse());
                //Need to send car vendor confirmation code to ADBI request also
                addDescBillingInfo(reserveRequestList, "CNU", getVendorConfirmCodeFromRsp(input.getResponse()), null);
                //build DescriptiveBillingInfoList from ADBI request
                DescriptiveBillingInfoListType aDBIRequestList = buildDescBillingInfoListFromADBIRequest(verificationContext);
                //Compare reserve request and ADBI request list
                StringBuilder aDBIError = new StringBuilder();
                boolean compared = CompareUtil.compareObject(reserveRequestList, aDBIRequestList, new ArrayList<String>(), aDBIError);
                if(!compared)
                {
                    remarks.add(aDBIError.toString());
                }
                //build DescriptiveBillingInfoList from ADBI response
                DescriptiveBillingInfoListType aDBIResponsetList = buildDescBillingInfoListFromADBIResponse(verificationContext);
                //Compare map between scs response and adbi response
                aDBIError = new StringBuilder();
                //Remove EDI data(first one)
                if(StringUtil.isNotBlank(reserveRequestEDI)) {
                    List<DescriptiveBillingInfoType> expectedDBIList = new ArrayList<>();
                    for(int i= 1; i< reserveResponseList.getDescriptiveBillingInfo().size(); i++)
                    {
                        expectedDBIList.add(reserveResponseList.getDescriptiveBillingInfo().get(i));
                    }
                    reserveResponseList.setDescriptiveBillingInfo(expectedDBIList);
                }
                compared = CompareUtil.compareObject(aDBIResponsetList, reserveResponseList, new ArrayList<String>(), aDBIError);
                if(!compared)
                {
                    remarks.add(aDBIError.toString());
                }

            }
        }

        if(verifyTypes.contains(CommonEnumManager.VerifyType.EVoucherFail)) {
            //PNR_Retrieve should not be sent
            final Node node_APRQ_PNR_Retrieve = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.APRQ_PNR_REQUEST_TYPE);
            if(null != node_APRQ_PNR_Retrieve)
            {
                remarks.add("PNR_Retrieve should not be sent when EVoucher failed! ");
            }
            //PNR_AddMultiElements with optionCode=20 should be sent
            NodeList addMulitiElementsList = ASCSGDSMsgReadHelper.getSpecifyNodeListFromSpoofer(verificationContext, GDSMsgNodeTags.AmadeusNodeTags.PNR_ADDMULTIELEMENTS_REQUEST);
            boolean expAPCMExist = false;
            if(null != addMulitiElementsList)
            {
                for(int i = 0; i< addMulitiElementsList.getLength(); i++)
                {
                    if("20".equals(PojoXmlUtil.getNodeByTagName(addMulitiElementsList.item(i), "optionCode").getTextContent()))
                    {
                        expAPCMExist = true;
                        break;
                    }
                }
            }
            if(!expAPCMExist)
            {
                remarks.add("APCM with optionCode=20 is not sent when EVoucher failed! ");
            }



        }

        if (StringUtil.isNotBlank(parameters.getBillingNumber()))
        {
            BillingNumberVerify(input, verificationContext, errorMsg);
            if(!errorMsg.toString().trim().isEmpty())
            {
                remarks.add(errorMsg.toString());
            }
        }

        if (remarks.size() > 0)
        {
            Assert.fail(remarks.toString());
        }

    }

    private static void setActualPNRAndConvertNonAsciiToDescriptiveBillingInfoList(CarSupplyConnectivityReserveRequestType reserveRequest,CarSupplyConnectivityReserveResponseType reserveResponse)
    {
        for(DescriptiveBillingInfoType description : reserveRequest.getDescriptiveBillingInfoList().getDescriptiveBillingInfo())
        {
            if(description.getKey().equals("PNR") && description.getValue().equals("pnr_value"))
            {
                description.setValue(getPNRFromRsp(reserveResponse));
                break;
            }
            description.setValue(description.getValue().replace("ü","u"));
        }
    }
    private static String getPNRFromRsp(CarSupplyConnectivityReserveResponseType response)
    {
        for(ReferenceType reference : response.getCarReservation().getReferenceList().getReference())
        {
            if(reference.getReferenceCategoryCode().equals("PNR"))
            {
                return reference.getReferenceCode();
            }
        }
        return "";
    }


    private static String getVendorConfirmCodeFromRsp(CarSupplyConnectivityReserveResponseType response)
    {
        for(ReferenceType reference : response.getCarReservation().getReferenceList().getReference())
        {
            if(reference.getReferenceCategoryCode().equals("Vendor"))
            {
                return reference.getReferenceCode();
            }
        }
        return "";
    }

    private static void addDescBillingInfo(DescriptiveBillingInfoListType descBillingInfoList, String key, String value, String description)
    {
        DescriptiveBillingInfoType descBillingInfo = new DescriptiveBillingInfoType();
        descBillingInfo.setKey(key);
        descBillingInfo.setValue(value);
        if(null != description)
        {
            descBillingInfo.setDescription(description);
        }
        descBillingInfoList.getDescriptiveBillingInfo().add(descBillingInfo);
    }

    private static DescriptiveBillingInfoListType buildDescBillingInfoListFromADBIRequest(BasicVerificationContext verificationContext)
    {
        final Node node_ADBI_Request = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ADBI_PAY_MANAGEDBIDATA_REQUEST);
        DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        List<Node> attributeDataNodes = PojoXmlUtil.getNodesByTagName(node_ADBI_Request, "attributeData");
        for(Node attributeDataNode : attributeDataNodes ) {
            addDescBillingInfo(descBillingInfoList, PojoXmlUtil.getNodeByTagName(attributeDataNode, "indicator").getTextContent(),
                    PojoXmlUtil.getNodeByTagName(attributeDataNode, "description").getTextContent(), null);
        }

        return descBillingInfoList;
    }

    private static DescriptiveBillingInfoListType buildDescBillingInfoListFromADBIResponse(BasicVerificationContext verificationContext)
    {
        final Node node_ADBI_Response = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ADBI_PAY_MANAGEDBIDATA_RESPONSE);
        DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>()); //not include CNU
        List<Node> attributeDataNodes = PojoXmlUtil.getNodesByTagName(node_ADBI_Response, "attributeData");
        for(Node attributeDataNode : attributeDataNodes ) {
            if(!PojoXmlUtil.getNodeByTagName(attributeDataNode, "indicator").getTextContent().equals("CNU")) {
                addDescBillingInfo(descBillingInfoList, PojoXmlUtil.getNodeByTagName(attributeDataNode, "indicator").getTextContent(),
                        PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(attributeDataNode, "otherStatusDetails"),  "description").getTextContent(),
                        PojoXmlUtil.getNodeByTagName(attributeDataNode, "description").getTextContent());
            }
        }

        return descBillingInfoList;
    }


    private static void mapInCarSellRequest(List remarks, Node node_ACSQ_CAR_SELL_REQUEST, StringBuilder errorMsg, CustomerLocationType delLocation_req, CustomerLocationType colLocation_req, String phoneNumber_del, String phoneNumber_col)
    {
        List<Node> deliveryAndCollectionNodeList = PojoXmlUtil.getNodesByTagName(node_ACSQ_CAR_SELL_REQUEST, "deliveryAndCollection");
        if (CollectionUtils.isEmpty(deliveryAndCollectionNodeList))
        {
            errorMsg.append("There was no deliveryAndCollection node present at CarSell request .");
        }

        for(Node deliveryAndCollectionNode : deliveryAndCollectionNodeList)
        {
            if ("DEL".equals(PojoXmlUtil.getNodeByTagName(deliveryAndCollectionNode, "purpose").getTextContent()))
            {
                /// mapping for delivery
                StringBuilder errorTemp1 = mappingDELCol(delLocation_req, null, deliveryAndCollectionNode, phoneNumber_del, "DEL");
                if (errorTemp1.length() > 0)
                {
                    remarks.add("Faild mapping in CarSell request for Del :");
                    remarks.add(errorTemp1.toString());
                }
            }


            else if ("COL".equals(PojoXmlUtil.getNodeByTagName(deliveryAndCollectionNode, "purpose").getTextContent()))
            {
                /// mapping for collection
                StringBuilder errorTemp2 = mappingDELCol(null, colLocation_req, deliveryAndCollectionNode, phoneNumber_col, "COL");
                if (errorTemp2.length() > 0)
                {
                    remarks.add("Faild mapping in CarSell request for Col :");
                    remarks.add(errorTemp2.toString());
                }
            }

        }
    }

    private static void checkDelColInResponse(ReserveVerificationInput input, List remarks, CustomerLocationType delLocation_req, CustomerLocationType colLocation_req)
    {
        CustomerLocationType delLocation_rsp = input.getResponse().getCarReservation().getDeliveryLocation();
        CustomerLocationType colLocation_rsp = input.getResponse().getCarReservation().getCollectionLocation();
        if (delLocation_req != null)
        {
            if (delLocation_rsp == null)
            {
                remarks.add("No Del location exist in response , but exist in request");
            }
            else
            {
                String placeID_req = delLocation_req.getCustomerLocationCode();
                String placeID_rsp = delLocation_rsp.getCustomerLocationCode();
                AddressType add_req = delLocation_req.getAddress();
                AddressType add_rsp = delLocation_rsp.getAddress();
                StringBuilder temp_err = mappingColDelrsp(placeID_req, placeID_rsp, add_req, add_rsp, "Del");
                if (temp_err.length() > 0)
                {
                    remarks.add("Mapping the Col node faild in response :");
                    remarks.add(temp_err.toString());
                }
            }
        }
        if (colLocation_req != null)
        {
            if (colLocation_rsp == null)
            {
                remarks.add("No Col location exist in response , but exist in request");
            }
            else
            {
                String placeID_req = colLocation_req.getCustomerLocationCode();
                String placeID_rsp = colLocation_rsp.getCustomerLocationCode();
                AddressType add_req = colLocation_req.getAddress();
                AddressType add_rsp = colLocation_rsp.getAddress();
                StringBuilder temp_err = mappingColDelrsp(placeID_req, placeID_rsp, add_req, add_rsp, "Col");
                if (temp_err.length() > 0)
                {
                    remarks.add("Mapping the Del node faild in response :");
                    remarks.add(temp_err.toString());
                }
            }
        }
    }

    private static void mapCarSellReponse(List remarks, Node node_ACSQ_CAR_SELL_RESPONSE, CustomerLocationType delLocation_req, CustomerLocationType colLocation_req, String phoneNumber_del, String phoneNumber_col)
    {
        List<Node> delColNodeList_rsp = PojoXmlUtil.getNodesByTagName(node_ACSQ_CAR_SELL_RESPONSE, "deliveryAndCollection");
        if (CollectionUtils.isNotEmpty(delColNodeList_rsp))
        {
            for (Node delAndColNode_rsp : delColNodeList_rsp)
            {
                if ("DEL".equals(PojoXmlUtil.getNodeByTagName(delAndColNode_rsp, "purpose").getTextContent()))
                {
                    /// mapping for delivery
                    StringBuilder temp1 = mappingDELCol(delLocation_req, null, delAndColNode_rsp, phoneNumber_del, "DEL");
                    if (temp1.length() > 0)
                    {
                        remarks.add("Faild mapping in CarSell response for Del :");
                        remarks.add(temp1.toString());
                    }
                }

                else if ("COL".equals(PojoXmlUtil.getNodeByTagName(delAndColNode_rsp, "purpose").getTextContent()))
                {
                    /// mapping for collection
                    StringBuilder temp2 = mappingDELCol(null, colLocation_req, delAndColNode_rsp, phoneNumber_col, "COL");
                    if (temp2.length() > 0)
                    {
                        remarks.add("Faild mapping in CarSell response for Col :");
                        remarks.add(temp2.toString());
                    }
                }
            }
        }

    //3 If there wa no DeliveryAndCollection node , but present locationInfo For structured info in CSQ response:
    else
    {
        //For unstructured CSQ response --- only found for placeID and phone
        List<Node>  locationList = PojoXmlUtil.getNodesByTagName(node_ACSQ_CAR_SELL_RESPONSE, "locationInfo");
        if (delLocation_req != null)
        {
            String expectedValue = null;
            if (StringUtil.isNotBlank(delLocation_req.getCustomerLocationCode()))
            {
                // for place ID , don't present the address
                expectedValue = delLocation_req.getCustomerLocationCode() + " " + phoneNumber_del;
            }
            else
            {
                // for home , conbine the address line with blank space
                expectedValue = delLocation_req.getAddress().getFirstAddressLine()+delLocation_req.getAddress().getSecondAddressLine()
                        + delLocation_req.getAddress().getThirdAddressLine()+delLocation_req.getAddress().getFourthAddressLine()
                        + delLocation_req.getAddress().getFifthAddressLine()+delLocation_req.getAddress().getCityName()
                        +delLocation_req.getAddress().getCountryAlpha3Code().substring(0,2)+delLocation_req.getAddress().getPostalCode()
                        + delLocation_req.getAddress().getProvinceName() + phoneNumber_del;
            }
            boolean mached = false;
            for (Node locationNode : locationList)
            {
                String locationType = PojoXmlUtil.getNodeByTagName(locationNode, "locationType").getTextContent().trim();
                if ("7".equals(locationType))
                {
                    /// delivery
                    String delLocationInfo = PojoXmlUtil.getNodeByTagName(locationNode, "name").getTextContent();//locationNode["locationDescription"]["name"].InnerText;
                    if (!expectedValue.toUpperCase().replace(" ", "").equals(delLocationInfo.toUpperCase().replace(" ", "")))
                    {
                        //Console.WriteLine(expectedValue.toUpperCase().Trim());
                        //Console.WriteLine(delLocationInfo.toUpperCase().Trim());
                        remarks.add("Faild mapping in CarSell response for del=7 , expected value is "+ expectedValue +" , but actual value is "
                              + delLocationInfo);
                    }
                    mached = true;
                    break;
                }
            }
            if (!mached)
            {
                remarks.add("There was no unstructed Del location/type=7 present at carSell response.");
            }
        }
        if (colLocation_req != null)
        {
            String expectedValue = null;
            if (StringUtil.isNotBlank(colLocation_req.getCustomerLocationCode()))
            {
                // for place ID , don't present the address
                expectedValue = colLocation_req.getCustomerLocationCode() + " " + phoneNumber_col;
            }
            else
            {
                // for home , conbine the address line with blank space
                expectedValue = colLocation_req.getAddress().getFirstAddressLine() + colLocation_req.getAddress().getSecondAddressLine()
                        + colLocation_req.getAddress().getThirdAddressLine() + colLocation_req.getAddress().getFourthAddressLine()
                        + colLocation_req.getAddress().getFifthAddressLine() + colLocation_req.getAddress().getCityName()
                        + colLocation_req.getAddress().getCountryAlpha3Code().substring(0,2)+ colLocation_req.getAddress().getPostalCode()
                        + colLocation_req.getAddress().getProvinceName() + phoneNumber_col;
            }
            boolean mached = false;
            for (Node locationNode : locationList)
            {
                String locationType = PojoXmlUtil.getNodeByTagName(locationNode, "locationType").getTextContent().trim();
                if ("117".equals(locationType))
                {
                    /// collection
                    String colLocationInfo = PojoXmlUtil.getNodeByTagName(locationNode, "name").getTextContent();
                    if (!expectedValue.toUpperCase().replace(" ", "").equals(colLocationInfo.toUpperCase().replace(" ", "")))
                    {
                        remarks.add("Faild mapping in CarSell response for col=117 , expected value is " + expectedValue + " , but actual value is "
                               + colLocationInfo);
                    }
                    mached = true;
                    break;
                }
            }

            if (!mached)
            {
                remarks.add("There was no unstructed Col location/type=117 present at carSell response.");
            }
        }
    }
    }

    private static StringBuilder mappingColDelrsp(String placeID_req, String placeID_rsp, AddressType address_req, AddressType address_rsp, String ColOrDel)
    {
        StringBuilder errorMsg = new StringBuilder("");
        // place id 
        mappingAddressInRequestAndResponse(placeID_req, placeID_rsp, ColOrDel, "placeID", errorMsg);
        //FirstAddressLine
        if (null != address_req)
        {
            String addressInreq = address_req.getFirstAddressLine() + (address_req.getSecondAddressLine() == null ? null : "" + address_req.getSecondAddressLine()) + (address_req.getThirdAddressLine() == null ? null : "" + address_req.getThirdAddressLine()) + (address_req.getFourthAddressLine() == null ? null : "" + address_req.getFourthAddressLine()) + (address_req.getFifthAddressLine() == null ? null : "" + address_req.getFifthAddressLine());
            if (address_rsp.getFirstAddressLine() != null)
            {
                if (address_rsp.getFirstAddressLine().length() <= 60)
                {
                    if (!addressInreq.replace(" ", "").toUpperCase().contains(address_rsp.getFirstAddressLine().replace(" ", "").toUpperCase()))
                    {
                        errorMsg.append(String.format("The {0} address maching failed in request and response :" + "request value is [{1}], response value is [{2}].", ColOrDel, addressInreq, address_rsp.getFirstAddressLine()));
                    }
                    if (addressInreq.length() > 60 && address_rsp.getFirstAddressLine().length() > 60)
                    {
                        errorMsg.append(String.format("The {0} response addressline {1} should remain for 60 character,acutal lengh is {2}", ColOrDel, address_rsp.getFirstAddressLine(), address_rsp.getFirstAddressLine().length()));
                    }
                }
                else
                {
                    if (addressInreq.length() != 60 && address_rsp.getFirstAddressLine().length() >= 60)
                    {
                        errorMsg.append(String.format("The {0} response addressline {1} should remain for 60 character,acutal lengh is {1}", ColOrDel, address_rsp.getFirstAddressLine(), address_rsp.getFirstAddressLine().length()));
                    }
                }

            }


            //CityName
            mappingAddressInRequestAndResponse(address_req.getCityName(), address_rsp.getCityName(), ColOrDel, "CityName", errorMsg);
            //PostalCode
            mappingAddressInRequestAndResponse(address_req.getPostalCode(), address_rsp.getPostalCode(), ColOrDel, "PostalCode", errorMsg);
            //ProvinceName
            //MappingAddressInRequestAndResponse(address_req.ProvinceName, address_rsp.ProvinceName, ColOrDel, "ProvinceName", errorMsg);
            //CountryAlpha3Code
            mappingAddressInRequestAndResponse(address_req.getCountryAlpha3Code(), address_rsp.getCountryAlpha3Code(), ColOrDel, "CountryAlpha3Code", errorMsg);
        }

        return errorMsg;
    }

    private static void mappingAddressInRequestAndResponse(String req_st,String rsp_str,String ColOrDel,String verifyName,StringBuilder errorMsg)
    {
        if (StringUtil.isNotBlank(req_st))
        {
            if (StringUtil.isNotBlank(rsp_str))
            {
                if (!req_st.toUpperCase().trim().equals(rsp_str.toUpperCase().trim()))
                {
                    errorMsg.append(String.format("The {0} {1} assert failed : request value is [{2}] ,but in response value is [{3}]",
                            ColOrDel, verifyName, req_st, rsp_str));
                }

            }
            else
            {
                // 
                errorMsg.append(String.format("The {0} {1} assert failed : request value is [{2}] ,but in response value is null",
                        ColOrDel, verifyName, req_st));
            }
        }
    }
    
    private static StringBuilder mappingDELCol(CustomerLocationType delLocation, CustomerLocationType colLocation,
                                                 Node delAndColNode, String phoneNUmber, String delOrCol)
    {
        StringBuilder errorMsg = new StringBuilder("");
        if (delLocation != null)
        {
            /// mapping for collection  place ID
            if (StringUtil.isNotBlank(delLocation.getCustomerLocationCode()))
            {
                // place ID
                if (!delLocation.getCustomerLocationCode().equals(PojoXmlUtil.getNodeByTagName(delAndColNode, "code").getTextContent()))//delAndColNode["addressDeliveryCollection"]["locationDetails"]["code"].InnerText)
                {
                    errorMsg.append("The "+ delOrCol +" placeID assert failed : expected value is " + delLocation.getCustomerLocationCode() + " ,"
                                    + "actual value is "+ PojoXmlUtil.getNodeByTagName(delAndColNode, "code").getTextContent());
                }

            }
            else
            {
                /// maching home and phone
                /// maching address
                mappingDelColLocationAddress(delLocation, null, delAndColNode, delOrCol, phoneNUmber, errorMsg);
            }
        }

        if (colLocation != null)
        {
            /// mapping for collection  place ID
            if (StringUtil.isNotBlank(colLocation.getCustomerLocationCode()))
            {
                // place ID
                if (!colLocation.getCustomerLocationCode().equals(PojoXmlUtil.getNodeByTagName(delAndColNode, "code").getTextContent()))
                {
                    errorMsg.append("The " + delOrCol +" placeID assert failed : expected value is "+ colLocation.getCustomerLocationCode() +" ,"
                                    + "actual value is "+ PojoXmlUtil.getNodeByTagName(delAndColNode, "code").getTextContent());
                }

            }
            else
            {
                /// maching home and phone
                /// maching address
                mappingDelColLocationAddress(null, colLocation, delAndColNode, delOrCol, phoneNUmber, errorMsg);
            }
        }
        return errorMsg;
    }


    private static void mappingDelColLocationAddress(CustomerLocationType delLocation_Node, CustomerLocationType colLocation_Node,
                                                         Node delAndColNode, String delOrCol, String phoneNumber, StringBuilder errorMsg)
    {
        ///// mach home

        if (!"5".equals(PojoXmlUtil.getNodeByTagName(delAndColNode, "format").getTextContent()))//delAndColNode["addressDeliveryCollection"]["addressDetails"]["format"].InnerText)
        {
            errorMsg.append(String.format("The collectionhome maching failed :"
                    + "can't get 'addressDetails/line1=5' in Car_SELL request"));
        }
        // Phone contact
        if (!"PHO" .equals(PojoXmlUtil.getNodeByTagName(delAndColNode, "phoneOrEmailType").getTextContent()))//delAndColNode["phoneNumber"]["phoneOrEmailType"].InnerText)
        {
            errorMsg.append(String.format("The {0} home phone contact assert failed : expected value is [{1}] ,"
                    + "actual value is [{2}]", delOrCol, "PHO", PojoXmlUtil.getNodeByTagName(delAndColNode, "phoneOrEmailType").getTextContent()));
        }
        if (!phoneNumber.equals(PojoXmlUtil.getNodeByTagName(delAndColNode, "telephoneNumber").getTextContent()))//delAndColNode["phoneNumber"]["telephoneNumberDetails"]["telephoneNumber"].InnerText)
        {
            errorMsg.append(String.format("The {0} home phonenumber contact assert failed : expected value is [{1}] ,"
                    + "actual value is [{2}]", delOrCol, phoneNumber, PojoXmlUtil.getNodeByTagName(delAndColNode, "telephoneNumber").getTextContent()));
        }
        /// home address
        String cityName = null, postalCode = null, contryAlpha3Code = null, provinceName = null, addLine = null;
        if (delLocation_Node != null)
        {
            cityName = delLocation_Node.getAddress().getCityName();
            postalCode = delLocation_Node.getAddress().getPostalCode();
            contryAlpha3Code = delLocation_Node.getAddress().getCountryAlpha3Code();
            provinceName = delLocation_Node.getAddress().getProvinceName();
            /// Concat all the fields
            addLine =  delLocation_Node.getAddress().getFirstAddressLine()
                    + (delLocation_Node.getAddress().getSecondAddressLine() == null ? null : " " + delLocation_Node.getAddress().getSecondAddressLine())
                    + (delLocation_Node.getAddress().getThirdAddressLine() == null ? null : " " + delLocation_Node.getAddress().getThirdAddressLine())
                    + (delLocation_Node.getAddress().getFourthAddressLine() == null ? null : " " + delLocation_Node.getAddress().getFourthAddressLine())
                    + (delLocation_Node.getAddress().getFifthAddressLine() == null ? null : " " + delLocation_Node.getAddress().getFifthAddressLine());
        }
        else
        {// for collection location address
            cityName = colLocation_Node.getAddress().getCityName();
            postalCode = colLocation_Node.getAddress().getPostalCode();
            contryAlpha3Code = colLocation_Node.getAddress().getCountryAlpha3Code();
            provinceName = colLocation_Node.getAddress().getProvinceName();
            /// Concat all the fields
            addLine =  colLocation_Node.getAddress().getFirstAddressLine()
                    + (colLocation_Node.getAddress().getSecondAddressLine() == null ? null : " " + colLocation_Node.getAddress().getSecondAddressLine())
                    + (colLocation_Node.getAddress().getThirdAddressLine() == null ? null : " " + colLocation_Node.getAddress().getThirdAddressLine())
                    + (colLocation_Node.getAddress().getFourthAddressLine() == null ? null : " " + colLocation_Node.getAddress().getFourthAddressLine())
                    + (colLocation_Node.getAddress().getFifthAddressLine() == null ? null : " " + colLocation_Node.getAddress().getFifthAddressLine());
        }
        Node addressNode = PojoXmlUtil.getNodeByTagName(delAndColNode, "addressDeliveryCollection");//delAndColNode["addressDeliveryCollection"];
        String address_CarCell = PojoXmlUtil.getNodeByTagName(addressNode, "line1").getTextContent().toUpperCase();//addressNode["addressDetails"]["line1"].InnerText.toUpperCase();
        String address_expected = addLine.toUpperCase();
        if (address_CarCell.length() <= 60)
        {
            if (!address_expected.replace(" ", "").contains(address_CarCell.replace(" ", "")))
            {
                errorMsg.append(String.format("The {0} address maching failed :" +
                                "expected value is [{1}], actual value is [{2}].", delOrCol, addLine,
                        address_CarCell));
            }
            if (address_CarCell.length() != 60 && address_expected.length() >= 60)
            {
                errorMsg.append(String.format("The {0} address maching failed :expected value is [{1}],"
                                +" actual value is [{2}] and the length() should be remains 60 charater, but the actual length() is [{3}] ",
                        delOrCol, addLine, address_CarCell, address_CarCell.length()));
            }
        }
        else
        {
            errorMsg.append(String.format("The {0} address maching failed :expected value is [{1}],"
                            + " actual value is [{2}] and the length() should be remains 60 charater, but the actual length() is [{3}] ",
                    delOrCol, addLine, address_CarCell, address_CarCell.length()));
        }
        if (!cityName.toUpperCase().trim().equals(PojoXmlUtil.getNodeByTagName(addressNode, "city").getTextContent().toUpperCase().trim()))//addressNode["city"].InnerText.toUpperCase().Trim())
        {
            errorMsg.append(String.format("The {0} Address city assert failed : expected value is [{1}] ,"
                    + "actual value is [{2}]", delOrCol, cityName, PojoXmlUtil.getNodeByTagName(addressNode, "city").getTextContent()));
        }
        if (!postalCode.equals(PojoXmlUtil.getNodeByTagName(addressNode, "zipCode").getTextContent()))//addressNode["zipCode"].InnerText)
        {
            errorMsg.append(String.format("The {0} Address PostalCode assert failed : expected value is [{1}] ,"
                    + "actual value is [{2}]", delOrCol, postalCode, PojoXmlUtil.getNodeByTagName(addressNode, "zipCode").getTextContent()));
        }
        if (!contryAlpha3Code.contains(PojoXmlUtil.getNodeByTagName(addressNode, "countryCode").getTextContent()))//addressNode["countryCode"].InnerText))
        {
            errorMsg.append(String.format("The {0} Address countryCode assert failed : expected value is [{1}] ,"
                    + "actual value is [{2}]", delOrCol, contryAlpha3Code, PojoXmlUtil.getNodeByTagName(addressNode, "countryCode").getTextContent()));
        }
    }

    private static void verifySpecialEquipmentCarSell(ReserveVerificationInput input, List remarks, Node node_ACSQ_CAR_SELL_REQUEST, Node node_ACSQ_CAR_SELL_RESPONSE, StringBuilder errorMsg)
    {
        Node specialEquipNodeInRequest = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_REQUEST, "specialEquipPrefs");
        String specialEquip = "";
        if(null != specialEquipNodeInRequest)
        {
            specialEquip = specialEquipNodeInRequest.getTextContent();
            if(!input.getRequest().getCarProduct().getCarVehicleOptionList().getCarVehicleOption().get(0)
                    .getCarSpecialEquipmentCode() .equals(specialEquip))
            {
                remarks.add("SpecailEquipment code NVS not exist in Crs log for Car_SELL request." +"\n");
            }
        }
        else
        {
            remarks.add("There is no SpecailEquipment code exist in Crs log for Car_SELL request path \",\n"
                    + "                        \"carSegment/typicalCarData/vehicleInformation/vehSpecialEquipment" +"\n");
        }

        Node specialEquipNodeInResponse = PojoXmlUtil.getNodeByTagName(node_ACSQ_CAR_SELL_RESPONSE, "vehSpecialEquipment");
        String specialEquipCode = "";
        if(null!= specialEquipNodeInResponse)
        {
            specialEquipCode = specialEquipNodeInResponse.getTextContent();
            if(!input.getRequest().getCarProduct().getCarVehicleOptionList().getCarVehicleOption().get(0)
                    .getCarSpecialEquipmentCode() .equals(specialEquipCode))
            {
                remarks.add("SpecailEquipment code NVS not exist in Crs log for Car_SELL request." +"\n");
            }
        }
        else
        {
            remarks.add("SpecailEquipment code NVS not exist in Maserati response" +"\n");

        }

    }

    private static void carReservationRemarkVerify(Node carsellRequestNode, ReserveVerificationInput input, StringBuilder remarks)
    {
        Node supleInfoNode = PojoXmlUtil.getNodeByTagName(carsellRequestNode,"supleInfo");
        String freeText = "";
        if(null != supleInfoNode)
        {
            freeText = PojoXmlUtil.getNodeByTagName(supleInfoNode, "freetext").getTextContent();
        }
        for (CarReservationRemarkType remarkSCS : input.getRequest().getCarReservationRemarkList().getCarReservationRemark())
        {
            String text = "";
            if(StringUtil.isBlank(remarkSCS.getCarReservationRemarkCategoryCode()))
            {
                text = remarkSCS.getCarReservationRemarkText();
            }
            else
            {
                text = remarkSCS.getCarReservationRemarkCategoryCode() + " - " + remarkSCS.getCarReservationRemarkText();
            }

            if (remarkSCS.getCarReservationRemarkText().contains("U2"))
            {
                if (freeText.contains("U2"))
                {
                    remarks.append("CarReservationRemark=" + remarkSCS.getCarReservationRemarkText() + " is passed to Car_Sell request."+ "\n");
                }
            }
            else if (!freeText.equalsIgnoreCase(text))
            {
                remarks.append("CarReservationRemark=" + remarkSCS.getCarReservationRemarkText() + " is not passed to GDS request message."+ "\n");
            }
        }
    }


    //reserve request GDSMsgMap
    public static String reserveRequestGDSMsgMappingVerification(ReserveVerificationInput reserveVerificationInput,
                                                                  SpooferTransport spooferTransport,
                                                                  TestScenario scenarios,
                                                                  String guid,
                                                                  Logger logger) throws IOException {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

//        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        ReserveRequestGDSMsgMappingVerification gdsMsgVerifier = new ReserveRequestGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(reserveVerificationInput, reserveVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RESERVE_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }


    public static String verifyspecialequipmentinacsq(CarSpecialEquipmentListType actSpecialEquipmentCodeList,
                                                      CarVehicleOptionListType actCarVehicleOptionList,
                                                      BasicVerificationContext reserveVerificationContext) throws IOException, DataAccessException
    {
        final Node node_ACSQ_CAR_SELL_REQUEST = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(reserveVerificationContext, CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_REQUEST_TYPE);
        StringBuilder errorMsg = new StringBuilder();
       CarsSCSHelper carsSCSHelper = new CarsSCSHelper(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        String expectedSpeqCode = "";
        String actualSpeqCode = "";
        List<String> actualSpeqCodes = new ArrayList<>();
        List<Node> specialEquipNodesInRequest = PojoXmlUtil.getNodesByTagName(node_ACSQ_CAR_SELL_REQUEST, "specialEquipPrefs");
        for(Node specialEquipNodeInRequest : specialEquipNodesInRequest)
        {
            actualSpeqCodes.add(specialEquipNodeInRequest.getTextContent());
          /*  List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0, 0,"CarSpecialEquipment", null, gdsSpeqCode);
            if(CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMapList))
            {
                actualSpeqCode = externalSupplyServiceDomainValueMapList.get(0).getDomainValue();
            }*/

        }
        actualSpeqCode = String.join(",",actualSpeqCodes);
            //Get expected Special eqipment code from SCS reserve request
        // if CarSpecialEquipmentList is null or empty, it will send it's sepcial eqipment code in ASCQ
        //or else it will send CarVehicleOptionList's
        if (null!= actSpecialEquipmentCodeList && CollectionUtils.isNotEmpty(actSpecialEquipmentCodeList.getCarSpecialEquipment()))
        {
            for (CarSpecialEquipmentType carSpecialEquipment : actSpecialEquipmentCodeList.getCarSpecialEquipment())
            {
                String expectedSpeqCodetem = "";
                if (StringUtil.isNotBlank(carSpecialEquipment.getCarSpecialEquipmentCode()))
                {
                    List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0, 0,"CarSpecialEquipment", carSpecialEquipment.getCarSpecialEquipmentCode(), null);
                    if(CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMapList))
                    {
                        expectedSpeqCodetem = externalSupplyServiceDomainValueMapList.get(0).getExternalDomainValue();
                    }
                    else
                    {
                        expectedSpeqCodetem = carSpecialEquipment.getCarSpecialEquipmentCode();
                    }

                    if (StringUtil.isBlank(expectedSpeqCode))
                    {
                        expectedSpeqCode = expectedSpeqCodetem;
                    }
                    else if (StringUtil.isNotBlank(expectedSpeqCode))
                    {
                        expectedSpeqCode = expectedSpeqCode + "," + expectedSpeqCodetem;
                    }
                }
            }
        }
        else if (null != actCarVehicleOptionList && CollectionUtils.isNotEmpty(actCarVehicleOptionList.getCarVehicleOption()))
        {
            for (CarVehicleOptionType carVehicleOption : actCarVehicleOptionList.getCarVehicleOption())
            {
                if (StringUtil.isNotBlank(carVehicleOption.getCarSpecialEquipmentCode()) && StringUtil.isBlank(expectedSpeqCode))
                {
                    expectedSpeqCode = carVehicleOption.getCarSpecialEquipmentCode();
                }
                else if (StringUtil.isNotBlank(carVehicleOption.getCarSpecialEquipmentCode()) && StringUtil.isNotBlank(expectedSpeqCode))
                {
                    expectedSpeqCode = expectedSpeqCode + "," + carVehicleOption.getCarSpecialEquipmentCode();
                }
            }
        }

        //Verification between actaul and expected
        if(StringUtil.isBlank(expectedSpeqCode) && StringUtil.isNotBlank(actualSpeqCode))
        {
            errorMsg.append( "Expected Special Eqipment code is null, but actual Special Eqipment code is not null, it's " + actualSpeqCode + "\r\n");
        }
        else if(StringUtil.isNotBlank(expectedSpeqCode) && StringUtil.isBlank(actualSpeqCode))
        {
            errorMsg.append("Actual Special Eqipment code is null, but expecte Special Eqipment code is not null, it's " + expectedSpeqCode + "\r\n");
        }
        else if (StringUtil.isNotBlank(expectedSpeqCode) && StringUtil.isNotBlank(actualSpeqCode))
        {

            List<String> sepListExpected = Arrays.asList(expectedSpeqCode.split(","));
            List<String> sepListActual = Arrays.asList(actualSpeqCode.split(","));
            if(sepListActual.size() != sepListExpected.size())
            {
                errorMsg.append("The special eqipment code count is different bewteen service request and ACSQ request, the service request count is "
                        + sepListExpected.size() + ", but ACSQ count is " +  sepListActual.size()+ "\\r\n");
            }
            else
            {
                for (int i = 0; i < sepListExpected.size(); i++)
                {
                    if (!sepListActual.contains(sepListExpected.get(i)))
                        errorMsg.append("The service special eqipment code " + sepListExpected.get(i) + " not in ACSQ request! \r\n");
                }
            }
        }
        return String.valueOf(errorMsg);
    }

    public static String verifySpecialEuipmentListInSCSResponse(BasicVerificationContext reserveVerificationContext, CarSpecialEquipmentListType actualCarSpecialEquipmentList,
                                                                CarVehicleOptionListType actualCarVehicleOptionList,
                                                                String verifyType,
                                                                CarSupplyConnectivityReserveRequestType carSupplyConnectivityReserveRequestType ) throws IOException, DataAccessException
    {
        StringBuffer eMsg = new StringBuffer();
        final Node node_ARIS_RIFCS_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(reserveVerificationContext,
                CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ARIS_RIFCS_RESPONSE_TYPE);
        CarReservationType carReservationType = new CarReservationType();
        carReservationType.setCarProduct(new CarProductType());
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();

        CarSpecialEquipmentListType expecteCarSpecialEquipmentList = null;
        CarSpecialEquipmentListType requestCarSpecialEquipmentList = carSupplyConnectivityReserveRequestType.getCarSpecialEquipmentList();

        if(verifyType.equals("GetReservation"))
        {
           commonNodeReader.buildSpecialEquipmentForGetReservation(carReservationType, node_ARIS_RIFCS_RESPONSE, new CarsSCSDataSource(SettingsProvider.CARAMADEUSSCSDATASOURCE));
            expecteCarSpecialEquipmentList = carReservationType.getCarSpecialEquipmentList();
        }
        if (verifyType.equals("Reserve"))
        {
            commonNodeReader.buildSpecialEquipmentForReserve(carReservationType, node_ARIS_RIFCS_RESPONSE, new CarsSCSDataSource(SettingsProvider.CARAMADEUSSCSDATASOURCE));
            expecteCarSpecialEquipmentList = carReservationType.getCarSpecialEquipmentList();
        }
        //for Reserve, SCS we only return the CarSpecialEquipmentList if the special equipment was requested through the CarSpecialEuqipmentList
        if (verifyType == "Reserve" && (requestCarSpecialEquipmentList == null || CollectionUtils.isEmpty(requestCarSpecialEquipmentList.getCarSpecialEquipment())
                || (requestCarSpecialEquipmentList.getCarSpecialEquipment().size() == 1 && StringUtil.isBlank(requestCarSpecialEquipmentList.getCarSpecialEquipment().get(0).getCarSpecialEquipmentCode()))))
        {
            expecteCarSpecialEquipmentList = null;
        }



        commonNodeReader.buildCarVehicleOptionForReserve(carReservationType.getCarProduct(), node_ARIS_RIFCS_RESPONSE);
        CarVehicleOptionListType expectedCarVehicleOptionList = carReservationType.getCarProduct().getCarVehicleOptionList();

        StringBuilder msg = new StringBuilder();
        if (!CompareUtil.compareObject(expecteCarSpecialEquipmentList, actualCarSpecialEquipmentList, null, msg))
        {
            eMsg.append(msg.toString() + "\r\n");
        }

        if (!CompareUtil.compareObject(expectedCarVehicleOptionList, actualCarVehicleOptionList, null, msg))
        {
            eMsg.append(msg.toString() + "\r\n");
        }


        return String.valueOf(eMsg);
    }



}
