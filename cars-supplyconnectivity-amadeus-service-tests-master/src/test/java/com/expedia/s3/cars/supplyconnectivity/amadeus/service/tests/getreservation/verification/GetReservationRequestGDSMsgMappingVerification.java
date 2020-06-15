package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.verification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.getresevationmapping.APRQReq;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;

public class GetReservationRequestGDSMsgMappingVerification implements IGetReservationVerification
{
    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        try
        {
            if (null != input.getRequest().getCarReservationList().getCarReservation().get(0).getCarProduct())
            {
                final APRQReq aprqReqReq = GetGdsMsg(verificationContext, remarks);

                if (!aprqReqReq.type.equals("2")) {
                    remarks.add("The PNR_Retrieve request/retrievalFacts/retrieve/type is not equal 2.");
                }
                // Amadeus PNR_Retrieve Request reservation/controlNumber.
                String expPNRValue = "";
                final String actPNRValue = aprqReqReq.pnrNumber;

                for (ReferenceType reference : input.getRequest().getCarReservationList().getCarReservation().get(0)
                        .getReferenceList().getReference()) {
                    if (reference.getReferenceCategoryCode().equals("PNR")) {
                        expPNRValue = reference.getReferenceCode();
                    }
                }
                if (!expPNRValue.equals(actPNRValue)) {
                    remarks.add(String.format("The PNR_Retrieve request/retrievalFacts/reservationOrProfileIdentifier/reservation/controlNumber=%s is not equal %s", actPNRValue, expPNRValue));
                }

            }
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }

        if (remarks.size() > 0)
        {
            remarks.add("Get Reservation Request GDS Msg verification failed.\n");
            return new VerificationResult(this.getName(), isPassed, remarks);
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private APRQReq GetGdsMsg(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException
    {
        final Node node_APRQ_PNR_Retrieve_REQUEST = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                CommonConstantManager.ActionType.GETRESERVATION, GDSMsgNodeTags.AmadeusNodeTags.APRQ_PNR_REQUEST_TYPE);

        if (node_APRQ_PNR_Retrieve_REQUEST != null)
        {
            return new APRQReq(node_APRQ_PNR_Retrieve_REQUEST);
        } else
        {
            remarks.add("Not Find ACAQ Request In GDS Msg");
        }

        return null;
    }
}