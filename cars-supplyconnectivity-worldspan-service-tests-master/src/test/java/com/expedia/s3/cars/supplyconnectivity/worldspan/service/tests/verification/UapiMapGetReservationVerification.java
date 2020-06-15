package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.URRRReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.URRRRes;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VCRRReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VCRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager.DomainType.CarSpecialEquipment;

/**
 * Created by yyang4 on 1/5/2017.
 */
public class UapiMapGetReservationVerification {

    private static final Logger logger = Logger.getLogger(SearchResponsesBasicVerification.class);

    public static void uapiMapVerifierWSCSGetReservation(BasicVerificationContext verificationContext, GetReservationVerificationInput verificationInput, CarSupplyConnectivityReserveResponseType reserveResponse, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final CarSupplyConnectivityGetReservationRequestType scsReq = verificationInput.getRequest();
        final CarSupplyConnectivityGetReservationResponseType scsRsp = verificationInput.getResponse();
        final StringBuilder errorMsg = new StringBuilder();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            org.testng.Assert.fail("No GDS messages found ! ");
        }
        //Get URRR
        final Node urrrReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.URRR_REQUEST_TYPE);
        final Node urrrRspNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.URRR_RESPONSE_TYPE);
        if (CompareUtil.isObjEmpty(urrrReqNode) || CompareUtil.isObjEmpty(urrrRspNode)) {
            org.testng.Assert.fail("No request or response URRR message found ! ");
        }
        //Parse request and response
        final URRRReq urrrReq = new URRRReq(urrrReqNode);
        final URRRRes urrrRsp = new URRRRes(urrrRspNode, scsDataSource, carsInventoryDs);

        //Add CarItemID/SupplySubsetID to parsed request and response to compare
        final CarProductType carProductRequest = scsReq.getCarReservationList().getCarReservation().get(0).getCarProduct();
        urrrRsp.getCarInventoryKey().setCarItemID(carProductRequest.getCarInventoryKey().getCarItemID());
        urrrRsp.getCarInventoryKey().setSupplySubsetID(carProductRequest.getCarInventoryKey().getSupplySubsetID());

        //pickup/dropoff time is from request
        urrrRsp.getCarInventoryKey().setCarPickUpDateTime(carProductRequest.getCarInventoryKey().getCarPickUpDateTime());
        urrrRsp.getCarInventoryKey().setCarDropOffDateTime(carProductRequest.getCarInventoryKey().getCarDropOffDateTime());

        //request map verify
        //Traveler lastname and PNR passed down to uAPI.
        final String lastname = scsRsp.getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler().get(0).getPerson().getPersonName().getLastName();
        String pnr = null;
        for (ReferenceType reference : scsRsp.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference()) {
            if ("PNR".equals(reference.getReferenceCategoryCode())) {
                pnr = reference.getReferenceCode();
                break;
            }
        }
        final String expectedLastName = scsReq.getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler().get(0).getPerson().getPersonName().getLastName();
        if (!CompareUtil.compareObject(pnr, urrrReq.getPnr(), null, errorMsg)) {
            Assert.fail(String.format("The PNR=%s in CarSCSGetReservation request is not equal the value=%s in URRR response.", pnr, urrrReq.getPnr()));
        }

        //Verify response mapping
        final CarProductType carProductResposne = scsRsp.getCarReservationList().getCarReservation().get(0).getCarProduct();
        //Compare 1 CarInventoryKey
        if (SettingsProvider.USE_SPOOFER) {
            urrrRsp.getCarInventoryKey().getCarCatalogKey().setCarPickupLocationKey(reserveResponse.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
            urrrRsp.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(reserveResponse.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey());
        }

        if (CompareUtil.isObjEmpty(reserveResponse.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarLocationCategoryCode())
                && CompareUtil.isObjEmpty(reserveResponse.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getSupplierRawText())
                && CompareUtil.isObjEmpty(reserveResponse.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode())) {
            urrrRsp.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(null);
        }
        //UapiMapCommonVerification.isCarInventoryKeyEqual(carProductResposne.getCarInventoryKey(), urrrRsp.getCarInventoryKey(), "URRR respponse to SCS CarInventoryKey verify:", false, false, false, false);

        //Compare 2 Traveler
        UapiMapCommonVerification.isTravelerListEqual(scsRsp.getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler(), urrrRsp.getTravelerList().getTraveler(), false, "URRR response to WSCS response map verify:");

        //Compare 4 ReferenceList
        UapiMapCommonVerification.isReferenceListEqual(scsRsp.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference(), urrrRsp.getReferenceList().getReference(), "");

        //Compare 5 BookingStateCode
        if (!CompareUtil.compareObject(scsRsp.getCarReservationList().getCarReservation().get(0).getBookingStateCode(), urrrRsp.getBookingStateCode(), null, errorMsg)) {
            Assert.fail(String.format("BookingStateCode is not correctly mapped to SCS response, SCS response: %s, URRR response: %s!\r\n", scsRsp.getCarReservationList().getCarReservation().get(0).getBookingStateCode(), urrrRsp.getBookingStateCode()));
        }

        //Compare 6 CostList
        UapiMapCommonVerification.verifyCertainCostInCostList(carProductResposne.getCostList().getCost(), urrrRsp.getCostList().getCost(), "Base", null);

        //Compare 7 CarPickUpDateTime and CarDropOffDateTime in CarSCS GetReservation response with CarSCS GetReservation request.
        if (!SettingsProvider.USE_SPOOFER) {
            if (!(CompareUtil.compareObject(carProductRequest.getCarInventoryKey().getCarPickUpDateTime(), carProductResposne.getCarInventoryKey().getCarPickUpDateTime(), null, null)
                    && CompareUtil.compareObject(carProductRequest.getCarInventoryKey().getCarDropOffDateTime(), carProductResposne.getCarInventoryKey().getCarDropOffDateTime(), null, null)))
                Assert.fail(String.format("The PickupDataTime/CarDropOffDateTime=%s/%s in CarSCS GetReservation response is not equal the value=%s/%s in CarSCS GetReesrvation request",
                        carProductRequest.getCarInventoryKey().getCarPickUpDateTime(), carProductRequest.getCarInventoryKey().getCarDropOffDateTime(),
                        carProductResposne.getCarInventoryKey().getCarPickUpDateTime(), carProductResposne.getCarInventoryKey().getCarDropOffDateTime()));
        }

    }

    public static void VerifySpecialEquipmentForWSPN(BasicVerificationContext verificationContext, ReserveVerificationInput reserveVerificationInput, CarSupplyConnectivityGetReservationResponseType carSCSGetReservationResponse, ReserveDefaultValue reserveDefaultValue) throws DataAccessException{
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final CarSupplyConnectivityReserveRequestType reserveRequest = reserveVerificationInput.getRequest();
        final CarSupplyConnectivityReserveResponseType reserveResponse = reserveVerificationInput.getResponse();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DataSourceHelper.getCarInventoryDatasource());
        final CarsSCSHelper scsHelper = new CarsSCSHelper(DataSourceHelper.getWSCSDataSourse());
        final StringBuilder errorMsg = new StringBuilder();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            org.testng.Assert.fail("No GDS messages found ! ");
        }
        //Get VCRR
        final Node vcrrReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VCRR_REQUEST_TYPE);
        final Node vcrrRspNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.WorldSpanNodeTags.VCRR_RESPONSE_TYPE);
        if (CompareUtil.isObjEmpty(vcrrReqNode) || CompareUtil.isObjEmpty(vcrrRspNode)) {
            Assert.fail("No request or response VCRR message found ! ");
        }
        //Parse request and response
        final VCRRReq vcrrReq = new VCRRReq(vcrrReqNode, DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource());
        final VCRRRsp vcrrRsp = new VCRRRsp(vcrrRspNode, null, DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource());

       //Compare request
        Map<String, String> scsSPCodes = UapiMapCommonVerification.getSPCodesFromCarSpecialEquipmentListAndCarVehicleOptionList(reserveRequest.getCarSpecialEquipmentList(), reserveRequest.getCarProduct().getCarVehicleOptionList(),reserveDefaultValue);
        CarSpecialEquipmentListType scsReqSpecialEquipList = new CarSpecialEquipmentListType();
        List<CarSpecialEquipmentType> specialEquipmentTypeList = new ArrayList<CarSpecialEquipmentType>();
        scsReqSpecialEquipList.setCarSpecialEquipment(specialEquipmentTypeList);
        for(String spCode : scsSPCodes.keySet())
        {
            CarSpecialEquipmentType specialEquip = new CarSpecialEquipmentType();
            specialEquip.setCarSpecialEquipmentCode(spCode);
            specialEquipmentTypeList.add(specialEquip);
        }
        List<String> needCompareField = Arrays.asList("carSpecialEquipmentCode");
        errorMsg.append("VCRR request CarSpecialEquipmentList verify");
        if(!CompareUtil.compareObjectOnlyForNeedField(vcrrReq.getCarSpecialEquipmentListType().getCarSpecialEquipment(), scsReqSpecialEquipList.getCarSpecialEquipment(),needCompareField,errorMsg)){
            Assert.fail(errorMsg.toString());
        }
        //Compare response
        errorMsg.setLength(0);
        errorMsg.append("VCRR response to SCS reserve response CarSpecialEquipmentList verify:");
        final CarSpecialEquipmentListType equipmentListRsp = reserveResponse.getCarReservation().getCarSpecialEquipmentList();
        final CarSpecialEquipmentListType equipmentListGDS = vcrrRsp.getCarSpecialEquipmentListType();
        if(CompareUtil.isObjEmpty(equipmentListRsp) && (!CompareUtil.isObjEmpty(equipmentListGDS) && !CompareUtil.isObjEmpty(equipmentListGDS.getCarSpecialEquipment()))){
            Assert.fail(errorMsg.append(" expect ").append(equipmentListGDS).append(" actual ").append(equipmentListRsp).toString());
        }else if(CompareUtil.isObjEmpty(equipmentListGDS) && (!CompareUtil.isObjEmpty(equipmentListRsp) && !CompareUtil.isObjEmpty(equipmentListRsp.getCarSpecialEquipment()))){
            Assert.fail(errorMsg.append(" expect ").append(equipmentListGDS).append(" actual ").append(equipmentListRsp).toString());
        }else if(equipmentListRsp != null && equipmentListGDS !=null && !CompareUtil.isObjEmpty(equipmentListRsp.getCarSpecialEquipment()) && !CompareUtil.isObjEmpty(equipmentListGDS.getCarSpecialEquipment())) {
            if (!CompareUtil.compareObjectOnlyForNeedField(equipmentListRsp.getCarSpecialEquipment(), equipmentListGDS.getCarSpecialEquipment(), needCompareField, errorMsg)) {
                Assert.fail(errorMsg.toString());
            }
        }

    }

}

