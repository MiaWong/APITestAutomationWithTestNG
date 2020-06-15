package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification.errorhandlingverification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorListType;
import com.expedia.e3.data.errortypes.defn.v4.ReferenceInvalidErrorListType;
import com.expedia.e3.data.errortypes.defn.v4.ReferenceUnavailableErrorListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyErrorIsReturnedCorrectly implements ICancelVerification {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(CancelVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext) {
        final List remarks = this.verifyErrorReturned((CarSupplyConnectivityCancelRequestType)input.getRequest(),
                    (CarSupplyConnectivityCancelResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }


    private List verifyErrorReturned(CarSupplyConnectivityCancelRequestType request, CarSupplyConnectivityCancelResponseType response,
                                     BasicVerificationContext verificationContext)  {
        //Create a List for error message
        final List remarks = new ArrayList();

        //If request PNR is empty, then no GDS request sent
        final String pnr = getRequestPNR(request);
        if(StringUtils.isEmpty(pnr))
        {
            final FieldInvalidErrorListType error = response.getErrorCollection().getFieldInvalidErrorList();
            if (!(!error.getFieldInvalidError().isEmpty()
                    && error.getFieldInvalidError().get(0).getDescriptionRawText().contains("Incorrect Format for PNR locator")))
            {
                remarks.add("Expected FieldInvalidError did not exist in response.");

            }
            return remarks;

        }

        //else verify GDS error map
        verifyGDSErrorMapped(response, verificationContext, remarks);


        return remarks;
    }

    private static String getRequestPNR(CarSupplyConnectivityCancelRequestType request)
    {
        String pnr = "";
        for (final ReferenceType reference : request.getCarReservation().getReferenceList().getReference())
        {
            if (reference.getReferenceCategoryCode().equals("PNR"))
            {
                pnr = reference.getReferenceCode();
                break;
            }
        }
        return pnr;
    }

    private static void verifyGDSErrorMapped(CarSupplyConnectivityCancelResponseType response,
                                             BasicVerificationContext verificationContext, List remarks)
    {
        final NodeList gdsRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "OTA_VehCancelRS");
        if(gdsRspNodeList.getLength() == 0)
        {
            remarks.add("No OTA_VehCancelRS from template spoofer!");
            return;
        }

        //Get error code from GDS response
        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(gdsRspNodeList.item(0), "Error");
        if(errorList.isEmpty())
        {
            remarks.add("No Error in OTA_VehCancelRS, please make sure template is correct for error mapping TCs!");
            return;
        }

        //Verify error code 8 should be returned as ReferenceUnavailableError
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("8"))
        {
            final ReferenceUnavailableErrorListType error = response.getErrorCollection().getReferenceUnavailableErrorList();
            if (!(!error.getReferenceUnavailableError().isEmpty()
                    && error.getReferenceUnavailableError().get(0).getDescriptionRawText().contains("Missing PNR number")))
            {
                remarks.add("Expected ReferenceUnavailableError did not exist in response.");

            }

        }

        //Verify error code 19 should be returned as ReferenceInvalidError
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("19"))
        {
            final ReferenceInvalidErrorListType error = response.getErrorCollection().getReferenceInvalidErrorList();
            if (!(!error.getReferenceInvalidError().isEmpty()
                    && error.getReferenceInvalidError().get(0).getDescriptionRawText().contains("Invalid PNR number")))
            {
                remarks.add("Expected ReferenceInvalidError did not exist in response.");

            }

        }
    }


}

