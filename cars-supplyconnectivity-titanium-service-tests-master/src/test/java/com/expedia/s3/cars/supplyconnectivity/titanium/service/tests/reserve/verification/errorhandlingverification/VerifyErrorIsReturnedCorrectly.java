package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.errorhandlingverification;

import com.expedia.e3.data.errortypes.defn.v4.ReferenceRequiredErrorListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyErrorIsReturnedCorrectly implements IReserveVerification {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) {
        final List remarks =  this.verifyErrorReturned((CarSupplyConnectivityReserveResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }


    private List verifyErrorReturned(CarSupplyConnectivityReserveResponseType response, BasicVerificationContext verificationContext
                                     )  {
        //Create a List for error message
        final List remarks = new ArrayList();
        //Get GDS response list from spoofer
        final NodeList gdsRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "OTA_VehResRS");
        if(gdsRspNodeList.getLength() == 0)
        {
            remarks.add("No OTA_VehResRS from template spoofer!");
            return remarks;
        }

        //Get error code from GDS response
        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(gdsRspNodeList.item(0), "Error");
        if(errorList.isEmpty())
        {
            remarks.add("No Error in OTA_VehResRS, please make sure template is correct for error mapping TCs!");
            return remarks;
        }

        //Verify error code 8 should be returned as ReferenceRequiredErrorList
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("8"))
        {
            final ReferenceRequiredErrorListType referenceRequiredErrorList = response.getErrorCollection().getReferenceRequiredErrorList();
                    if (!(null != referenceRequiredErrorList && !referenceRequiredErrorList.getReferenceRequiredError().isEmpty()
                               && referenceRequiredErrorList.getReferenceRequiredError().get(0).getDescriptionRawText().contains("Missing PNR number")))
                    {
                        remarks.add("Expected ReferenceRequiredError did not exist in response.");

                    }

        }



        return remarks;
    }


}

