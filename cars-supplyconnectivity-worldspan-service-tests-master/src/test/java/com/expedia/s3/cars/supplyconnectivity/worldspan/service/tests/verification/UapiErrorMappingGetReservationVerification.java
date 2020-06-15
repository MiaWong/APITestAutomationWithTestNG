package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.ErrorCollectionType;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by yyang4 on 10/19/2017.
 */
public class UapiErrorMappingGetReservationVerification {
    public static void errorMappingVerifier(ErrorCollectionType errorCollection,
                                            ErrorHandling testDataErrHandle) {
        if(CompareUtil.isObjEmpty(errorCollection)){
            Assert.fail("no correct error message return!");
        }
        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.pojoToDoc(errorCollection).getFirstChild(), testDataErrHandle.getErrorType());
        //Verify the ErrorType, ErrrorMessage, Xpath returned as expected.
        if (CompareUtil.isObjEmpty(errorList)) {
                /* no need to proceed when no errortype, no error message and no xpath returned, such as:
                *<Error Text="".*NO VEHICLES AVAILABLE.*"" Message=""Cars not available"" InterruptProcessing=""0"" Log=""0""></Error>
                */
            final String errorCollectionText = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.pojoToDoc(errorCollection).getFirstChild(), "ErrorCollection").getTextContent();
            if (!(CompareUtil.isObjEmpty(testDataErrHandle.getErrorType()) && CompareUtil.isObjEmpty(errorCollectionText))) {
                Assert.fail(String.format("This errorType=%s not returned in response.", testDataErrHandle.getErrorType()));
            }
        } else {
            String actErrorMessage = null;
            String actErrorXpath = null;
            switch (testDataErrHandle.getErrorType()) {
                case "ReferenceUnavailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getReferenceInvalidErrorList())) {
                        Assert.fail("The actual ReferenceUnavailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getReferenceUnavailableErrorList().getReferenceUnavailableError().get(0).getDescriptionRawText();
                        actErrorXpath = errorCollection.getReferenceRequiredErrorList().getReferenceRequiredError().get(0).getFieldKey().getXPath();
                    }
                    break;
                case "FieldInvalidError":
                    if (CompareUtil.isObjEmpty(errorCollection.getFieldInvalidErrorList().getFieldInvalidError())) {
                        Assert.fail("The actual FieldInvalidError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getFieldInvalidErrorList().getFieldInvalidError().get(0).getDescriptionRawText();
                        actErrorXpath = errorCollection.getFieldInvalidErrorList().getFieldInvalidError().get(0).getFieldKey().getXPath();
                    }
                    break;
                case "DownstreamServiceUnavailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getDownstreamServiceUnavailableError())) {
                        Assert.fail("The actual DownstreamServiceUnavailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getDownstreamServiceUnavailableError().getDescriptionRawText();
                    }
                    break;
                case "ReferenceInvalidError":
                    if (CompareUtil.isObjEmpty(errorCollection.getReferenceInvalidErrorList())) {
                        Assert.fail("The actual DownstreamServiceUnavailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getReferenceInvalidErrorList().getReferenceInvalidError().get(0).getDescriptionRawText();
                    }
                    break;
                case "SupplierUnavailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getSupplierUnavailableError())) {
                        Assert.fail("The actual SupplierUnavailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getSupplierUnavailableError().getDescriptionRawText();
                    }
                    break;

                case "UnclassifiedError":
                    if (CompareUtil.isObjEmpty(errorCollection.getUnclassifiedErrorList().getUnclassifiedError())) {
                        Assert.fail("The actual UnclassifiedError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getUnclassifiedErrorList().getUnclassifiedError().get(0).getDescriptionRawText();
                    }
                    break;
                case "CurrencyNotAvailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getCurrencyNotAvailableError())) {
                        Assert.fail("The actual CurrencyNotAvailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getCurrencyNotAvailableError().getDescriptionRawText();
                    }
                    break;
                case "FieldRequiredError":
                    if (CompareUtil.isObjEmpty(errorCollection.getFieldRequiredErrorList().getFieldRequiredError())) {
                        Assert.fail("The actual FieldRequiredError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getFieldRequiredErrorList().getFieldRequiredError().get(0).getDescriptionRawText();
                        actErrorXpath = errorCollection.getFieldRequiredErrorList().getFieldRequiredError().get(0).getFieldKey().getXPath().replace("\n", " ").replace(" ", "");
                    }
                    break;
                case "ReferenceRequiredError":
                    if (CompareUtil.isObjEmpty(errorCollection.getReferenceRequiredErrorList())) {
                        Assert.fail("The actual ReferenceRequiredError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getReferenceRequiredErrorList().getReferenceRequiredError().get(0).getDescriptionRawText();
                        final String XPath = errorCollection.getReferenceRequiredErrorList().getReferenceRequiredError().get(0).getFieldKey().getXPath();
                        if (!CompareUtil.isObjEmpty(XPath)) {
                            actErrorXpath = errorCollection.getReferenceRequiredErrorList().getReferenceRequiredError().get(0).getFieldKey().getXPath().replace("\n", " ").replace(" ", "");
                        }
                    }
                    break;
                default:
                    Assert.fail("No mapping the error, the error type is " + testDataErrHandle.getErrorType());
                    break;
            }

            if (!(CompareUtil.isObjEmpty(testDataErrHandle.getErrormessage()) && CompareUtil.isObjEmpty(actErrorMessage))
                    && !CompareUtil.compareObject(testDataErrHandle.getErrormessage(), actErrorMessage,null,null)) {
                Assert.fail(String.format("The actual errorMsg='%s' is not equal the expected='%s'", actErrorMessage, testDataErrHandle.getErrormessage()));
            }

            if (!((CompareUtil.isObjEmpty(testDataErrHandle.getxPath()) && CompareUtil.isObjEmpty(actErrorXpath))
                    || actErrorXpath.contains(testDataErrHandle.getxPath()) || actErrorXpath.equals(testDataErrHandle.getxPath()))) {
                Assert.fail(String.format("The actual Xpath='%s' is not equal the expected='%s'", actErrorXpath, testDataErrHandle.getxPath()));
            }
        }
    }
}
