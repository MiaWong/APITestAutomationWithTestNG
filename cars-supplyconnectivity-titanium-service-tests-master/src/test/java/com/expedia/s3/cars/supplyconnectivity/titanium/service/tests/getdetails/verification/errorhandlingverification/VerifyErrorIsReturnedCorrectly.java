package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.errorhandlingverification;

import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyErrorIsReturnedCorrectly implements IGetDetailsVerification {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List remarks = this.verifyErrorReturned((CarSupplyConnectivityGetDetailsResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }


    private List verifyErrorReturned(CarSupplyConnectivityGetDetailsResponseType response, BasicVerificationContext verificationContext
                                     )  {
        //Create a List for error message
        final List remarks = new ArrayList();
        //Get GDS response list from spoofer
        final NodeList gdsRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "OTA_VehRateRuleRS");
        if(gdsRspNodeList.getLength() == 0)
        {
            remarks.add("No OTA_VehRateRuleRS from template spoofer!");
            return remarks;
        }

        //Get error code from GDS response
        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(gdsRspNodeList.item(0), "Error");
        if(errorList.isEmpty())
        {
            remarks.add("No Error in OTA_VehRateRuleRS, please make sure template is correct for error mapping TCs!");
            return remarks;
        }

        //Verify error code 16 should be returned as FieldInvalidError
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("16"))
        {
            final FieldInvalidErrorListType error = response.getErrorCollection().getFieldInvalidErrorList();
                    if (!(!error.getFieldInvalidError().isEmpty()
                               && error.getFieldInvalidError().get(0).getDescriptionRawText().contains("Requested rate not available")))
                    {
                        remarks.add("Expected FieldInvalidError did not exist in response.");

                    }

        }


        return remarks;
    }


}

