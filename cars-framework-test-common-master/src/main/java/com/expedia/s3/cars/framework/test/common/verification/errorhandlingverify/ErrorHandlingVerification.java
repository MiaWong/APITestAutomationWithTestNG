package com.expedia.s3.cars.framework.test.common.verification.errorhandlingverify;

import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 2/12/2017.
 */
public class ErrorHandlingVerification {

    private ErrorHandlingVerification() {
    }

    public static IVerification.VerificationResult isExpectMessageVerification(Document rspDoc, String errorType, String expectMessage) {

        final List<Node> errorCollectionNode = PojoXmlUtil.getNodesByTagName(rspDoc.getDocumentElement(), "ErrorCollection");
        if (CollectionUtils.isEmpty(errorCollectionNode)) {
            return new IVerification.VerificationResult("isExpectMessageVerification", false, Arrays.asList("There is no ErrorCollection exist in  response"));
        }

        //Verify CarTypeNotAvailableErrorType returned in  response
        if (verifyResult(errorType, "CarTypeNotAvailableError", expectMessage, errorCollectionNode))
        {
            return new IVerification.VerificationResult("isExpectMessageVerification", false, Arrays.asList("Expected CarTypeNotAvailableError did not exist in  response."));
        }
        //Verify FieldInvalidErrorXML returned in  response
        if (verifyResult(errorType, "FieldInvalidError", expectMessage, errorCollectionNode))
        {
            return new IVerification.VerificationResult("isExpectMessageVerification", false, Arrays.asList("Expected FieldInvalidError did not exist in  response."));
        }

        //Verify FieldRequiredError returned in  response
        if (verifyResult(errorType, "FieldRequiredError", expectMessage, errorCollectionNode))
        {
            return new IVerification.VerificationResult("isExpectMessageVerification", false, Arrays.asList("Expected FieldRequiredError did not exist in  response."));
        }

        //Verify RentalOutOfRangeError returned in  response
        if (verifyResult(errorType, "RentalOutOfRangeError", expectMessage, errorCollectionNode))
        {
            return new IVerification.VerificationResult("isExpectMessageVerification", false, Arrays.asList("Expected RentalOutOfRangeError did not exist in  response."));
        }


        return  new IVerification.VerificationResult("isExpectMessageVerification", true, Arrays.asList("success"));

    }

    private static boolean verifyResult(String errorType, String expectErrorType, String expectMessage, List<Node> errorCollectionNode) {
        if(expectErrorType.equalsIgnoreCase(errorType))
        {
            final Node errorTypeNode = PojoXmlUtil.getNodeByTagName(errorCollectionNode.get(0), expectErrorType);
            if (!isExpect(expectMessage, errorTypeNode))
            {
                return true;

            }
        }
        return false;
    }

    private static boolean isExpect(String expectMessage, Node errorTypeNode) {
        return errorTypeNode != null && StringUtil.isNotBlank(PojoXmlUtil.getNodeByTagName(errorTypeNode, "DescriptionRawText").getTextContent())//carTypeNotAvailableErrorTypeNode.getDescriptionRawText())
                &&PojoXmlUtil.getNodeByTagName(errorTypeNode, "DescriptionRawText").getTextContent().
                contains(expectMessage);
    }
}
