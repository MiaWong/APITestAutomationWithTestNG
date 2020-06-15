package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.errorhandlingverification;

import com.expedia.e3.data.errortypes.defn.v4.DownstreamServiceUnavailableErrorType;
import com.expedia.s3.cars.data.carerrortypes.defn.v2.RentalOutOfRangeErrorType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyErrorIsReturnedCorrectly implements IGetCostAndAvailabilityVerification {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) {
        final List remarks = this.verifyErrorReturned((CarSupplyConnectivityGetCostAndAvailabilityResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }


    private List verifyErrorReturned(CarSupplyConnectivityGetCostAndAvailabilityResponseType response, BasicVerificationContext verificationContext
                                     )  {
        //Create a List for error message
        final List remarks = new ArrayList();
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

        //Verify error code 12 should be returned as RentalOutOfRangeError
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("12"))
        {
            final RentalOutOfRangeErrorType error = response.getErrorCollection().getRentalOutOfRangeError();
                    if (!(null != error
                               && error.getDescriptionRawText().contains("Pickup or dropoff date in the past")))
                    {
                        remarks.add("Expected RentalOutOfRangeError did not exist in response.");

                    }

        }

        //Verify error code 2 should be returned as DownstreamServiceUnavailableErrorType
        if(errorList.get(0).getAttributes().getNamedItem("Code").getTextContent().equals("2"))
        {
            final DownstreamServiceUnavailableErrorType error = response.getErrorCollection().getDownstreamServiceUnavailableError();
            if (!(null != error
                    && error.getDescriptionRawText().contains("Unable to process the request")))
            {
                remarks.add("Expected DownstreamServiceUnavailableError did not exist in response.");

            }

        }


        return remarks;
    }


}

