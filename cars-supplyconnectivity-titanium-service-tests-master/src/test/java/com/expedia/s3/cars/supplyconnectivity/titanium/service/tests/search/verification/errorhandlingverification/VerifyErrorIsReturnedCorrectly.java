package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.errorhandlingverification;

import com.expedia.e3.data.errortypes.defn.v4.DownstreamServiceUnavailableErrorType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyErrorIsReturnedCorrectly implements ISearchVerification {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) {
        final List remarks = this.verifyErrorReturned((CarSupplyConnectivitySearchResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }


    private List verifyErrorReturned(CarSupplyConnectivitySearchResponseType response, BasicVerificationContext verificationContext
                                     )  {
        //Create a List for error message
        final List remarks = new ArrayList();

        //Get error code from GDS response
        final List<Node> errorList = verifyErrorReturnedInVehAvailRateRS(verificationContext, remarks);

        //Verify  errorcode 30 should be returned as DownstreamServiceUnavailableError
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("30"))
        {
            final DownstreamServiceUnavailableErrorType error = response.getErrorCollectionList().getErrorCollection().get(0).getDownstreamServiceUnavailableError();
                    if (!(error != null
                               && error.getDescriptionRawText().contains("Unable to process the request")))
                    {
                        remarks.add("Expected DownstreamServiceUnavailableError did not exist in response.");

                    }

        }


        return remarks;
    }

    public static List<Node> verifyErrorReturnedInVehAvailRateRS(BasicVerificationContext verificationContext, List remarks)
    {
        //Get GDS response list from spoofer
        final NodeList gdsRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "OTA_VehAvailRateRS");
        if(gdsRspNodeList.getLength() == 0)
        {
            remarks.add("No OTA_VehAvailRateRS from template spoofer!");
            return remarks;
        }

        //Get error code from GDS response
        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(gdsRspNodeList.item(0), "Error");
        if(errorList.isEmpty())
        {
            remarks.add("No Error in OTA_VehAvailRateRS, please make sure template is correct for error mapping TCs!");
            return remarks;
        }

        return errorList;
    }


}

