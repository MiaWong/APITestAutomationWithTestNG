package com.expedia.s3.cars.supply.service.verification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.UAPICommonNodeReader;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveRequestType;
import com.expedia.s3.cars.supply.service.utils.TestDataUtil;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveResponseType;
import com.expedia.s3.cars.supply.service.common.CarCommonEnumManager;
import org.testng.Assert;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by yyang4 on 8/24/2016.
 */
public class ReserveVerifier implements IVerification {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }

    public static void verifyReturn(CarSupplyReserveResponseType response, TestScenario scenarios) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = verifyResponse(response);

        if (null != response.getErrorCollection()) {
            final List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(response.getErrorCollection(), "DescriptionRawText");
            if (!descriptionRawTextList.isEmpty()) {
                errorMsg.append("ErrorCollection is present in Reserve response");
                descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
            }

        }
        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }
    }

    public static StringBuilder verifyResponse(CarSupplyReserveResponseType response) {
        final StringBuilder errorMsg = new StringBuilder();
        if (null == response) {
            errorMsg.append("reserve Response is null.");
        } else if (null == response.getCarReservation() || null == response.getCarReservation().getBookingStateCode()) {
            errorMsg.append("No ReserveResult returns in response.");
        } else if (!CarCommonEnumManager.BookingStateCode.Booked.toString().equals(response.getCarReservation().getBookingStateCode())
                && !CarCommonEnumManager.BookingStateCode.Pending.toString().equals(response.getCarReservation().getBookingStateCode())
                && !CarCommonEnumManager.BookingStateCode.Confirm.toString().equals(response.getCarReservation().getBookingStateCode())) {
            errorMsg.append(String.format("Verify Reseve failed. BookingStateCode={0}.", response.getCarReservation().getBookingStateCode()));
        }

        return errorMsg;
    }

    //Node vcrrReq
    public static void verifySIVO(CarSupplyReserveRequestType request, CarSupplyReserveResponseType response, Node vcrrReq) {
        final StringBuilder errorMsg = verifyResponse(response);
        //get request voucher
        final String reqVO = getVoucherFromRequest(request);
        //get expect SI format
        final String expSI = TestDataUtil.getSupplysubsetTestData(response.getCarReservation().getCarProduct().getCarInventoryKey().getSupplySubsetID(),
                "SIFormat").replace("%v%",reqVO);
        //Get actual SI
        String actSI = "";
        final Node vehicleSpecialRequest = PojoXmlUtil.getNodeByTagName(vcrrReq,"VehicleSpecialRequest");
        if (!CompareUtil.isObjEmpty(vehicleSpecialRequest)  && !CompareUtil.isObjEmpty(vehicleSpecialRequest.getAttributes().getNamedItem("Key"))) {
            actSI = vehicleSpecialRequest.getAttributes().getNamedItem("Key").getNodeValue();
        }
        //Verify SI
        if(!expSI.equals(actSI))
        {
            errorMsg.append("SI is not sent correctly, expected:" + expSI + ", actual:" + actSI + ";");
        }

        //Get expVO
        final String expVO = TestDataUtil.getSupplysubsetTestData(response.getCarReservation().getCarProduct().getCarInventoryKey().getSupplySubsetID(),
                "VOFormat").replace("%v%",reqVO);;
        //Get actVO
        final String actVO = UAPICommonNodeReader.readVoucher(vcrrReq) == null ? "" : UAPICommonNodeReader.readVoucher(vcrrReq);
        //Verify SI
        if(!expVO.equals(actVO))
        {
            errorMsg.append("VO is not sent correctly, expected:" + expVO + ", actual:" + actVO + ";");
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

    }

    public static String getVoucherFromRequest(CarSupplyReserveRequestType request)
    {
        String voucher ="";
        if(request.getReferenceList() == null || request.getReferenceList().getReference() == null)
        {
            return voucher;
        }
        for (final ReferenceType reference: request.getReferenceList().getReference()
             ) {
            if(reference.getReferenceCategoryCode().equals("Voucher"))
            {
                voucher = reference.getReferenceCode();
            }
        }

        return voucher;

    }
}
