package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorType;
import com.expedia.e3.data.errortypes.defn.v4.FieldRequiredErrorType;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.SearchErrorCollectionType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.List;

public class VerifyInvalidLatLongSearch {

    private VerifyInvalidLatLongSearch() {

    }

    public static void verifyErrorList(SearchVerificationInput verificationInput, TestScenario scenario, String errorMsg)
    {
        boolean containedMessage = false;
        final SearchErrorCollectionType searchError = verificationInput.getResponse().getSearchErrorCollection();
        final List<FieldInvalidErrorType> fieldInvalidError = searchError.getFieldInvalidErrorList().getFieldInvalidError();
        final List<FieldRequiredErrorType> fieldRequiredError = searchError.getFieldRequiredErrorList().getFieldRequiredError();
        if (CollectionUtils.isNotEmpty(fieldInvalidError))
        {
            if (errorMsg.equalsIgnoreCase(fieldInvalidError.get(0).getDescriptionRawText()))
            {
                containedMessage = true;
            }
        }

        if (CollectionUtils.isNotEmpty(fieldRequiredError))
        {
            if (errorMsg.equalsIgnoreCase(fieldRequiredError.get(0).getDescriptionRawText()))
            {
                containedMessage = true;
            }
        }

        if (!containedMessage)
        {
            Assert.fail(scenario.getScenarionName() + " failed because in response there is no expected value like " + errorMsg );
        }
    }
}