package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.getresevationmapping;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping.ARISRsp;
import org.w3c.dom.Node;

/**
 * Created by miawang on 2/14/2017.
 */
@SuppressWarnings("PMD")
public class ASCSGetReservation
{
    /*
    public static void isCarSCSGetReservationRequestAndPNRRetrieveRequestMappingVerifier(Node nodeAPRQReq) {
        StringBuilder errorMsg = new StringBuilder();
        APRQReq pnrRequest = new APRQReq(nodeAPRQReq);

        // Amadeus PNR_Retrieve Request retrieve/type.
        String actualTypeValue = "2";
        String expectedTypeValue = pnrRequest.type;

        if (!actualTypeValue.equals(expectedTypeValue)) {
            errorMsg.append("The PNR_Retrieve request/retrievalFacts/retrieve/type=").append(expectedTypeValue).append(" is not equal 2.");
        }


        // Amadeus PNR_Retrieve Request reservation/controlNumber.
        String actualPNRValue = "";
        String expectedPNRValue = pnrRequest.pnrNumber;

        for (int i = 0; i < request.CarReservationList.CarReservation[0].ReferenceList.Count; i++) {
            if (request.CarReservationList.CarReservation[0].ReferenceList.Reference[i].ReferenceCategoryCode == "PNR") {
                actualPNRValue = request.CarReservationList.CarReservation[0].ReferenceList.Reference[i].ReferenceCode;
            }
        }
        if (!String.Equals(actualPNRValue, expectedPNRValue)) {
            errorMsg.AppendFormat("The PNR_Retrieve request/retrievalFacts/reservationOrProfileIdentifier/reservation/controlNumber={0} is not equal {1}", expectedPNRValue, actualPNRValue);
        }

        if (errorMsg.ToString().Trim().Length > 0) {
            Assert.Fail(errorMsg.ToString());
        }
    }

    public static void isPNRRetrieveResponseAndCarRateInformationFromCarSegmentRequestMappingVerifier(String pnrResponseStr, String arisRequestStr) {
        StringBuilder errorMsg = new StringBuilder();
        APRQResponse pnrResponse = new APRQResponse(pnrResponseStr);
        ARISRequest arisRequest = new ARISRequest(arisRequestStr);

        // Car_RAFCS/bookingIdentifier/referenceType
        String actualRefTypeValue = "S";
        String expectedRefTypeValue = arisRequest.referenceType;
        if (!String.Equals(actualRefTypeValue, expectedRefTypeValue)) {
            errorMsg.AppendFormat("The Car_RateInformationFromCarSegment request/bookingIdentifier/referenceType={0} is not equal {1}", expectedRefTypeValue, actualRefTypeValue);
        }

        // Car_RAFCS/bookingIdentifier/uniqueReference
        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:elementManagementItinerary/:reference
        String actualUniqueRefValue = pnrResponse.getNumber();
        String expectedUniqueRefValue = arisRequest.uniqueReference;
        if (!String.Equals(actualUniqueRefValue, expectedUniqueRefValue)) {
            errorMsg.AppendFormat("The Car_RateInformationFromCarSegment request/bookingIdentifier/uniqueReference={0} is not equal {1}", expectedUniqueRefValue, actualUniqueRefValue);
        }

        if (errorMsg.ToString().Trim().Length > 0) {
            Assert.Fail(errorMsg.ToString());
        }
    }
    */

    public void buildCarReservationCar(BasicVerificationContext verificationContext, CarReservationType reservation, CarsSCSDataSource scsDataSource, StringBuffer eMsg) throws DataAccessException
    {
        final Node node_APRQ_PNR_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETRESERVATION, GDSMsgNodeTags.AmadeusNodeTags.APRQ_PNR_RESPONSE_TYPE);
        final Node node_ARIS_RIFCS_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETRESERVATION, GDSMsgNodeTags.AmadeusNodeTags.ARIS_RIFCS_RESPONSE_TYPE);

        if (null == node_APRQ_PNR_RESPONSE || null == node_ARIS_RIFCS_RESPONSE)
        {
            eMsg.append("\nCan Not Find ");

            if (null == node_APRQ_PNR_RESPONSE)
            {
                eMsg.append("/ APRQ_RESPONSE ");
            }
            if (null == node_ARIS_RIFCS_RESPONSE)
            {
                eMsg.append("/ ARIS_RESPONSE ");
            }

            eMsg.append("In Spoofer document.");
        } else
        {
            buildGetReservationCarFromCRS(reservation, scsDataSource, node_APRQ_PNR_RESPONSE, node_ARIS_RIFCS_RESPONSE, eMsg);
        }
    }

    private void buildGetReservationCarFromCRS(CarReservationType carReservation, CarsSCSDataSource scsDataSource, Node aprqPNRResponse,
                                               Node arisResponse, StringBuffer eMsg) throws DataAccessException
    {
        APRQRsp aprqRsp = new APRQRsp();
        ARISRsp arisRsp = new ARISRsp();

//        System.out.println("ARIS : " + PojoXmlUtil.toString(arisResponse));
//        System.out.println("APRQ : " + PojoXmlUtil.toString(aprqPNRResponse));

        //1. BookingStateCode
        aprqRsp.buildBookingStateCode(carReservation, aprqPNRResponse);

        //2. ReferenceList
        aprqRsp.buildReferenceList(carReservation, aprqPNRResponse);

        //3. Customer
        aprqRsp.buildCustomer(carReservation, aprqPNRResponse);

        //4. TravelerList
        aprqRsp.buildTravelerList(carReservation, aprqPNRResponse);

        //5. DeliveryLocation
        aprqRsp.buildDeliveryLocation(carReservation, aprqPNRResponse);

        //6. CollectionLocation
        aprqRsp.buildCollectionLocation(carReservation, aprqPNRResponse);

        //------------------- Car Product --------------------------
        // CarCatalogMakeModel
        if (null == carReservation.getCarProduct())
        {
            carReservation.setCarProduct(new CarProductType());
        }
        carReservation.getCarProduct().setCarCatalogMakeModel(arisRsp.buildCarCatalogMakeModel(arisResponse));

        // CarInventoryKey
        // RatePeriodCode is get from request in old framwork, so add this field to ignore list.
        aprqRsp.buildCarInventoryKey(carReservation.getCarProduct(), aprqPNRResponse, scsDataSource, arisRsp);

        //CostList
        arisRsp.buildCostList(carReservation.getCarProduct(), arisResponse);

        //CarRateDetail
        arisRsp.buildCarRateDetail(carReservation.getCarProduct(), arisResponse);

        //CarPolicyList
        arisRsp.buildCarPolicyList(carReservation.getCarProduct(), arisResponse);

        //CarVehicleOptioinList
        arisRsp.buildCarVehicleOptionList(carReservation.getCarProduct(), arisResponse);

        //CarSpecialEquipmentList
        arisRsp.buildSpecialEquipmentList(carReservation, arisResponse);

        //CarPickupAndDropOffLocation
        arisRsp.buildPickupAndDropoffLocation(carReservation.getCarProduct(), arisResponse, carReservation.getBookingStateCode());

        // CarMileage
        arisRsp.buildCarMileage(carReservation.getCarProduct(), arisResponse);

        // BillingCode
        aprqRsp.buildBillingNumberAndeVoucher(carReservation, aprqPNRResponse);

        // LoyaltyProgramInfo
        aprqRsp.buildLoyaltyProgram(carReservation.getCarProduct(), aprqPNRResponse);

        //AgencyData
        aprqRsp.buildAgencyData(carReservation, aprqPNRResponse);

        carReservation.getCarProduct().setProviderID(6L);

        carReservation.getCarProduct().setAvailStatusCode("A");

        carReservation.getCarProduct().setCarDoorCount(carReservation.getCarProduct().getCarCatalogMakeModel().getCarMinDoorCount());
    }
}
