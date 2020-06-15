package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification;

import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Created by MiaWang on 12/26/2017.
 */
public class ErrorHandlingVerification
{
    public IVerification.VerificationResult isExpectMessageVerification(Document rspDoc, String expectMessage) {

        final List<Node> errorCollectionNode = PojoXmlUtil.getNodesByTagName(rspDoc.getDocumentElement(), "ErrorCollection");
        if (CollectionUtils.isEmpty(errorCollectionNode)) {
            return new IVerification.VerificationResult("isExpectMessageVerification", false,
                    Arrays.asList("There is no ErrorCollection exist in  response"));
        }

        //Verify expectMessage returned in response
        if (!verifyResult(expectMessage, errorCollectionNode))
        {
            return new IVerification.VerificationResult("isExpectMessageVerification", false,
                    Arrays.asList("Expected "+expectMessage+" did not exist in  response."));
        }

        return  new IVerification.VerificationResult("", true, null);
    }

    private static boolean verifyResult(String expectMessage, List<Node> errorCollectionNodes)
    {
        StringBuffer errorTxtsInResponse = new StringBuffer();
        for(Node errorCollectionNode : errorCollectionNodes)
        {
            final Node errorDesNode = PojoXmlUtil.getNodeByTagName(errorCollectionNode, "DescriptionRawText");
            if(null != errorDesNode)
            {
                errorTxtsInResponse.append("^^").append(errorDesNode.getTextContent().replaceAll("\n", "").replaceAll("\t", ""));
            }
        }

        if (errorTxtsInResponse.toString().toLowerCase().contains(expectMessage.toLowerCase()))
        {
            return true;
        }
        return false;
    }
}
