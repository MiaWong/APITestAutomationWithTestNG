package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionListType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionType;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by yyang4 on 10/19/2017.
 */
public class UapiErrorMappingSearchVerification {
    public static void errorMappingVerifier(ErrorCollectionListType errorCollectionList,
                                            ErrorHandling testDataErrHandle) {
        ErrorCollectionType errorCollection = errorCollectionList.getErrorCollection().get(0);
        if (CompareUtil.isObjEmpty(errorCollection)) {
            Assert.fail("no correct error message return!");
        }

        final List<Node> errorList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.pojoToDoc(errorCollection).getFirstChild(), testDataErrHandle.getErrorType());
        //Verify the ErrorType, ErrrorMessage, Xpath returned as expected.
        if (CompareUtil.isObjEmpty(errorList)) {
            final Node errorCollectionNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.pojoToDoc(errorCollection).getFirstChild(), "ErrorCollection");
            /* no need to proceed when no errortype, no error message and no xpath returned, such as:
             *<Error Text="".*NO VEHICLES AVAILABLE.*"" Message=""Cars not available"" InterruptProcessing=""0"" Log=""0""></Error>
             */
            if (null != errorCollectionNode)
            {
                final String errorCollectionText = errorCollectionNode.getTextContent();
                if (!(CompareUtil.isObjEmpty(testDataErrHandle.getErrorType()) && CompareUtil.isObjEmpty(errorCollectionText)))
                Assert.fail(String.format("This errorType=%s not returned in response.", testDataErrHandle.getErrorType()));
            }
        } else {
            String actErrorMessage = null;
            String actErrorXpath = null;
            switch (testDataErrHandle.getErrorType()) {
                case "CarProductNotAvailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getCarProductNotAvailableError())) {
                        Assert.fail("The actual CarProductNotAvailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getCarProductNotAvailableError().getDescriptionRawText();
                    }
                    break;
                case "SupplierUnavailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getSupplierUnavailableError())) {
                        Assert.fail("The actual SupplierUnavailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getSupplierUnavailableError().getDescriptionRawText();
                    }
                    break;
                case "RateCodeNotAppliedError":
                    if (CompareUtil.isObjEmpty(errorCollection.getRateCodeNotAppliedError())) {
                        Assert.fail("The actual RateCodeNotAppliedError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getRateCodeNotAppliedError().getDescriptionRawText();
                    }
                    break;
                case "RentalOutOfRangeError":
                    if (CompareUtil.isObjEmpty(errorCollection.getRentalOutOfRangeError())) {
                        Assert.fail("The actual RentalOutOfRangeError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getRentalOutOfRangeError().getDescriptionRawText();
                    }
                    break;
                case "VendorLocationClosedError":
                    if (CompareUtil.isObjEmpty(errorCollection.getVendorLocationClosedError())) {
                        Assert.fail("The actual VendorLocationClosedError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getVendorLocationClosedError().getDescriptionRawText();
                    }
                    break;
                case "DifferentDropOffLocationNotAllowedError":
                    if (CompareUtil.isObjEmpty(errorCollection.getDifferentDropOffLocationNotAllowedError())) {
                        Assert.fail("The actual DifferentDropOffLocationNotAllowedError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getDifferentDropOffLocationNotAllowedError().getDescriptionRawText();
                    }
                    break;
                case "CarTypeNotAvailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getCarTypeNotAvailableError())) {
                        Assert.fail("The actual CarTypeNotAvailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getCarTypeNotAvailableError().getDescriptionRawText();
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
                case "UnclassifiedError":
                    if (CompareUtil.isObjEmpty(errorCollection.getUnclassifiedErrorList().getUnclassifiedError())) {
                        Assert.fail("The actual UnclassifiedError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getUnclassifiedErrorList().getUnclassifiedError().get(0).getDescriptionRawText();
                    }
                    break;
                case "SpecialEquipmentNotAvailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getSpecialEquipmentNotAvailableError())) {
                        Assert.fail("The actual SpecialEquipmentNotAvailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getSpecialEquipmentNotAvailableError().getDescriptionRawText();
                    }
                    break;
                case "SupplierDownError":
                    if (CompareUtil.isObjEmpty(errorCollection.getSupplierDownError())) {
                        Assert.fail("The actual SupplierDownError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getSupplierDownError().getDescriptionRawText();
                    }
                    break;
                case "DownstreamServiceUnavailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getDownstreamServiceUnavailableError())) {
                        Assert.fail("The actual DownstreamServiceUnavailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getDownstreamServiceUnavailableError().getDescriptionRawText();
                    }
                    break;
                case "CarNotAvailableWithGuaranteedRateError":
                    if (CompareUtil.isObjEmpty(errorCollection.getCarNotAvailableWithGuaranteedRateError())) {
                        Assert.fail("The actual CarNotAvailableWithGuaranteedRateError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getCarNotAvailableWithGuaranteedRateError().getDescriptionRawText();
                    }
                    break;
                case "CarNotAvailableWithMileageError":
                    if (CompareUtil.isObjEmpty(errorCollection.getCarNotAvailableWithMileageError())) {
                        Assert.fail("The actual CarNotAvailableWithMileageError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getCarNotAvailableWithMileageError().getDescriptionRawText();
                    }
                    break;
                case "RatePlanNotAvailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getRatePlanNotAvailableError())) {
                        Assert.fail("The actual RatePlanNotAvailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getRatePlanNotAvailableError().getDescriptionRawText();
                    }
                    break;
                case "RateCategoryNotAvailableError":
                    if (CompareUtil.isObjEmpty(errorCollection.getRateCategoryNotAvailableError())) {
                        Assert.fail("The actual RateCategoryNotAvailableError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getRateCategoryNotAvailableError().getDescriptionRawText();
                    }
                    break;
                case "CorporateDiscountNotAppliedError":
                    if (CompareUtil.isObjEmpty(errorCollection.getCorporateDiscountNotAppliedError())) {
                        Assert.fail("The actual CorporateDiscountNotAppliedError is null, not equal the expected.");
                    } else {
                        actErrorMessage = errorCollection.getCorporateDiscountNotAppliedError().getDescriptionRawText();
                    }
                    break;
                default:
                    Assert.fail("No mapping the error, the error type is " + testDataErrHandle.getErrorType());
                    break;
            }

            if (!(CompareUtil.isObjEmpty(testDataErrHandle.getErrormessage()) && CompareUtil.isObjEmpty(actErrorMessage))
                    && !CompareUtil.compareObject(testDataErrHandle.getErrormessage(), actErrorMessage, null, null)) {
                Assert.fail(String.format("The actual errorMsg='%s' is not equal the expected='%s'", actErrorMessage, testDataErrHandle.getErrormessage()));
            }

            if (!((CompareUtil.isObjEmpty(testDataErrHandle.getxPath()) && CompareUtil.isObjEmpty(actErrorXpath))
                    || actErrorXpath.toUpperCase().contains(testDataErrHandle.getxPath().toUpperCase()))) {
                Assert.fail(String.format("The actual Xpath='%s' is not equal the expected='%s'", actErrorXpath, testDataErrHandle.getxPath()));
            }
        }


    }

    public static void errorMappingVerifierTransaction(ErrorCollectionListType errorCollectionList,
                                                       ErrorHandling testDataErrHandle) {
        ErrorCollectionType errorCollection = errorCollectionList.getErrorCollection().get(0);
        if (CompareUtil.isObjEmpty(errorCollection)) {
            Assert.fail("no correct error message return!");
        }

        if(errorCollection.getUnclassifiedErrorList().getUnclassifiedError().isEmpty())
        {
            Assert.fail("no correct error message return!");
        }

        if(!(errorCollection.getUnclassifiedErrorList().getUnclassifiedError().get(0).getDescriptionRawText().contains(testDataErrHandle.getErrormessage())))
        {
            Assert.fail("no correct error message return!");
        }



    }
}

