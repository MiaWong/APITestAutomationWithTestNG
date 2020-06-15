package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.verification.errorhandlingverification;

import com.expedia.e3.data.errortypes.defn.v4.ReferenceInvalidErrorListType;
import com.expedia.e3.data.errortypes.defn.v4.ReferenceRequiredErrorListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyErrorIsReturnedCorrectly implements IGetReservationVerification {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext) {
        final List remarks = this.verifyErrorReturned((CarSupplyConnectivityGetReservationResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }


    private List verifyErrorReturned(CarSupplyConnectivityGetReservationResponseType response, BasicVerificationContext verificationContext
                                     )  {
        //Create a List for error message
        final List remarks = new ArrayList();
        //Get GDS response list from spoofer
        final NodeList gdsRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "OTA_VehRetResRS");
        if(gdsRspNodeList.getLength() == 0)
        {
            remarks.add("No OTA_VehRetResRS from template spoofer!");
            return remarks;
        }

        //Get error code from GDS response
        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(gdsRspNodeList.item(0), "Error");
        if(errorList.isEmpty())
        {
            remarks.add("No Error in OTA_VehRetResRS, please make sure template is correct for error mapping TCs!");
            return remarks;
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

        //Verify error code 8 should be returned as ReferenceRequiredError
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("8"))
        {
            final ReferenceRequiredErrorListType error = response.getErrorCollection().getReferenceRequiredErrorList();
            if (!(!error.getReferenceRequiredError().isEmpty()
                    && error.getReferenceRequiredError().get(0).getDescriptionRawText().contains("Missing PNR number")))
            {
                remarks.add("Expected ReferenceRequiredError did not exist in response.");

            }

        }


        return remarks;
    }


}

