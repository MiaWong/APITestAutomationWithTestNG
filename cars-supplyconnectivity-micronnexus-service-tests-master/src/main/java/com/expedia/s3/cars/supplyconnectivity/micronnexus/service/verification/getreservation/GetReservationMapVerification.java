package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getreservation;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRBReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRBRsp;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 8/8/2017.
 */
public class GetReservationMapVerification implements IGetReservationVerification {

    final private Logger logger = Logger.getLogger(GetReservationMapVerification.class.getName());


    public VerificationResult verifyGetReservationMap(GetReservationVerificationInput getReservationVerificationInput, BasicVerificationContext verificationContext) throws Exception {

            final Node vehRetResRSNode = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*","VehRetResRS").item(0);
            final Node vehRetResRQNode = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*","VehRetResRQ").item(0);

            final List<String> errorMessage = new ArrayList<>();

            verifyResponse(getReservationVerificationInput, vehRetResRSNode, errorMessage);

            verifyRequest(getReservationVerificationInput, vehRetResRQNode, errorMessage);

            if (CollectionUtils.isNotEmpty(errorMessage))
            {
                return new VerificationResult(this.getName(), false, errorMessage);
            }


        return  new VerificationResult(this.getName(), true, Arrays.asList(new String[]{"Map Successful"}));

    }

    private void verifyRequest(GetReservationVerificationInput getReservationVerificationInput, Node vehRetResRSNode, List<String> errorMsg) throws XPathExpressionException {
        final VRBReq vrbReq = new VRBReq(vehRetResRSNode);

        for (final ReferenceType referenceType : getReservationVerificationInput.getRequest().getCarReservationList().getCarReservation().get(0).getReferenceList().getReference()) {
            if (referenceType.getReferenceCategoryCode().equalsIgnoreCase("PNR")) {
                if (!referenceType.getReferenceCode().equalsIgnoreCase(vrbReq.getIdContext())) {
                    errorMsg.add("rerefenceCode(PNR) is not equal to IdContext for getReservstion request Map"  + "\n");
                }
                break;
            }
        }
        if (!getReservationVerificationInput.getRequest().getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler().get(0).getPerson().getPersonName().getLastName().equals(vrbReq.getSurName())) {
            errorMsg.add("personName " + getReservationVerificationInput.getRequest().getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler().get(0).getPerson().getPersonName().getLastName()
                    + "is not equal to SurName" + vrbReq.getSurName() + " for getReservstion request Map" + "\n");
        }
    }

    private List<String> verifyResponse(GetReservationVerificationInput getReservationVerificationInput, Node vehRetResRSNode, List errorMsg) throws Exception {
        final List<CarReservationType> carReservationTypes = getCarReservationTypes(getReservationVerificationInput);
        final CarProductType actualCarProduct = carReservationTypes.get(0).getCarProduct();
        final VRBRsp vrrRsp = new VRBRsp(vehRetResRSNode, new CarsSCSDataSource(SettingsProvider.CARMNSCSDATASOURCE));
        final CarProductType expectCarProduct = vrrRsp.getCarProductType();

        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expectCarProduct)));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actualCarProduct)));

        //Response Map (costList/carVehicleOptionList)
        CarProductComparator.isCarProductEqual(expectCarProduct, actualCarProduct, errorMsg,
                Arrays.asList(CarTags.SUPPLY_SUBSET_ID, CarTags.LEGACY_FINANCE_KEY, CarTags.CAR_DROP_OFF_LOCATION, CarTags.CAR_PICK_UP_LOCATION,CarTags.CAR_POLICY_LIST));

        //Verify: CarReservation -BookingStateCode
        verifyBookingState(carReservationTypes, vehRetResRSNode, errorMsg);

        //verify: referenceList
        verifyReferenceList(carReservationTypes, vrrRsp, errorMsg);
        return errorMsg;
    }

    private void verifyReferenceList(List<CarReservationType> carReservationTypes, VRBRsp vrrRsp, List<String> errorMsg) {
        for(final ReferenceType expectReferenceType : vrrRsp.getReferenceListType().getReference())
        {
            for (final ReferenceType acturalReferenceType : carReservationTypes.get(0).getReferenceList().getReference())

            {
                if(expectReferenceType.getReferenceCategoryCode().equalsIgnoreCase(acturalReferenceType.getReferenceCategoryCode()))
                {
                    if(!expectReferenceType.getReferenceCode().trim().equalsIgnoreCase(acturalReferenceType.getReferenceCode().trim()))
                    {
                        errorMsg.add("referenceCode is not equal!" + " actual is " + acturalReferenceType.getReferenceCode()+ "expect is " + expectReferenceType.getReferenceCode() +"\n");
                    }
                    break;
                }
            }

        }
    }

    private void verifyBookingState(List<CarReservationType> carReservationTypes, Node vehRetResRSNode, List<String> errorMsg) {
        if (!((carReservationTypes.get(0).getBookingStateCode().equals("Booked") && "Confirmed".equals(PojoXmlUtil.getNodeByTagName(vehRetResRSNode,"VehReservation").getAttributes().getNamedItem("ReservationStatus").getTextContent())
        ) || PojoXmlUtil.getNodeByTagName(vehRetResRSNode,"VehReservation").getAttributes().getNamedItem("ReservationStatus").getTextContent().contains(carReservationTypes.get(0).getBookingStateCode())))
        {
            errorMsg.add("The BookingStateCode:" + carReservationTypes.get(0).getBookingStateCode() + "is not equal the expected value:" + PojoXmlUtil.getNodeByTagName(vehRetResRSNode,"VehReservation").getAttributes().getNamedItem("ReservationStatus").getTextContent() + "\n");
        }
    }

    private List<CarReservationType> getCarReservationTypes(GetReservationVerificationInput getReservationVerificationInput) {
        final CarSupplyConnectivityGetReservationResponseType getReservationResponseType = getReservationVerificationInput.getResponse();
        Assert.assertNotNull(getReservationResponseType);
        final List<CarReservationType>  carReservationTypes =  getReservationResponseType.getCarReservationList().getCarReservation();
        Assert.assertNotNull(carReservationTypes);
        return carReservationTypes;
    }

    @Override
    public VerificationResult verify(GetReservationVerificationInput getReservationVerificationInput, BasicVerificationContext verificationContext) {
        return null;
    }
}
