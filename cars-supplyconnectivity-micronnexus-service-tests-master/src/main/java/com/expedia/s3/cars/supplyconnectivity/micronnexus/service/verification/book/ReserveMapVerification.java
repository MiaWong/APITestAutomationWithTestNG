package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.book;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRSRsp;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 8/15/2017.
 */
public class ReserveMapVerification implements IReserveVerification {

    final private Logger logger = Logger.getLogger(ReserveMapVerification.class.getName());
    @Override
    public VerificationResult verify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext verificationContext) {

        Assert.notNull(reserveVerificationInput.getResponse());
        Assert.notNull(reserveVerificationInput.getResponse().getCarReservation());
        Assert.notNull(reserveVerificationInput.getResponse().getCarReservation());
       final Node vehResRSNode = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*","VehResRS").item(0);
        final VRSRsp vrsRsp = new VRSRsp(vehResRSNode, new CarsSCSDataSource(SettingsProvider.CARMNSCSDATASOURCE));
        final  CarReservationType acturalCarReservationType = reserveVerificationInput.getResponse().getCarReservation();
        final CarReservationType expectCarReservationType = vrsRsp.getCarReservationType();

        final List<String> errorMessage = new ArrayList<>();

       //carproduct
        CarProductComparator.isCarProductEqual(expectCarReservationType.getCarProduct(), acturalCarReservationType.getCarProduct(), errorMessage, Arrays.asList(CarTags.SUPPLY_SUBSET_ID, CarTags.LEGACY_FINANCE_KEY));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(acturalCarReservationType.getCarProduct())));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expectCarReservationType.getCarProduct())));
        //referenceList
        verifyReferenceList(acturalCarReservationType, expectCarReservationType, errorMessage);

        //bookingStateCode
        verifyBookingStateCode(acturalCarReservationType, expectCarReservationType, errorMessage);


        //travelerList
        verifyTravelerList(acturalCarReservationType, expectCarReservationType, errorMessage);

        //carSpecialEquipList
        verifyCarSpecialEquipList(acturalCarReservationType, expectCarReservationType, errorMessage);

        if (CollectionUtils.isNotEmpty(errorMessage))
        {
            return new VerificationResult(this.getName(), false, errorMessage);
        }

        return  new VerificationResult(this.getName(), true, Arrays.asList(new String[]{"Map Successful"}));

}

    private void verifyBookingStateCode(CarReservationType acturalCarReservationType, CarReservationType expectCarReservationType, List<String> errorMessage) {
        if(!acturalCarReservationType.getBookingStateCode() .equalsIgnoreCase(expectCarReservationType.getBookingStateCode()))
         {
             errorMessage.add("actural bookingstateCode :" + acturalCarReservationType.getBookingStateCode() + " is not equal to  expect bookingstatesCode :" + expectCarReservationType.getBookingStateCode());
         }
    }

    private void verifyCarSpecialEquipList(CarReservationType acturalCarReservationType, CarReservationType expectCarReservationType, List<String> errorMessage) {
        final  CarSpecialEquipmentListType actualSpecialEquipmentListType = acturalCarReservationType.getCarSpecialEquipmentList();
        final  CarSpecialEquipmentListType expectSpecialEquipmentListType = expectCarReservationType.getCarSpecialEquipmentList();
        if(null != actualSpecialEquipmentListType && null != expectSpecialEquipmentListType && CollectionUtils.isNotEmpty(actualSpecialEquipmentListType.getCarSpecialEquipment())
        && CollectionUtils.isNotEmpty(expectSpecialEquipmentListType.getCarSpecialEquipment()) && actualSpecialEquipmentListType.getCarSpecialEquipment().size() !=  expectSpecialEquipmentListType.getCarSpecialEquipment().size())
        {
            errorMessage.add("expect specialEquipmentList size: " + expectSpecialEquipmentListType.getCarSpecialEquipment().size() + "not equal to actural specialEquipmentList size :" + actualSpecialEquipmentListType.getCarSpecialEquipment().size());
        }
    }

    private void verifyTravelerList(CarReservationType acturalCarReservationType, CarReservationType expectCarReservationType, List<String> errorMessage) {
        final TravelerListType acturalTravelerListType = acturalCarReservationType.getTravelerList();
        final TravelerListType expectTravelerListType =  expectCarReservationType.getTravelerList();
        if(null != acturalTravelerListType && null != expectTravelerListType && CollectionUtils.isNotEmpty(acturalTravelerListType.getTraveler())
                && CollectionUtils.isNotEmpty(expectTravelerListType.getTraveler()) && acturalTravelerListType.getTraveler().size() !=  expectTravelerListType.getTraveler().size())
        {
            errorMessage.add("expect travelerList size: " + expectTravelerListType.getTraveler().size() + "not equal to actural travelerList size :" + acturalTravelerListType.getTraveler().size());
        }
    }

    private void verifyReferenceList(CarReservationType acturalCarReservationType, CarReservationType expectCarReservationType, List<String> errorMessage) {
        final   ReferenceListType acturalReferenceListType = acturalCarReservationType.getReferenceList();
        final   ReferenceListType expectReferenceListType = expectCarReservationType.getReferenceList();
        for(final ReferenceType expectReferenceType : expectReferenceListType.getReference())
        {
            for (final ReferenceType acturalReferenceType : acturalReferenceListType.getReference())

            {
                if(expectReferenceType.getReferenceCategoryCode().equalsIgnoreCase(acturalReferenceType.getReferenceCategoryCode()))
                {
                    if(!expectReferenceType.getReferenceCode().trim().equalsIgnoreCase(acturalReferenceType.getReferenceCode().trim()))
                    {
                        errorMessage.add("referenceCode is not equal!" + " actual is " + acturalReferenceType.getReferenceCode()+ "expect is " + expectReferenceType.getReferenceCode() +"\n");
                    }
                    break;
                }
            }

        }
    }
}
