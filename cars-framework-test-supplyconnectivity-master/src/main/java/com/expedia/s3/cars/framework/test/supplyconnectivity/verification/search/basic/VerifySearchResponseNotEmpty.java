package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.basic;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;

import java.util.Arrays;
import java.util.Optional;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;

//import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorType;
//import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionType;

/**
 * Created by jiyu on 8/24/16.
 */
public class VerifySearchResponseNotEmpty implements ISearchVerification
{
    private static final String MESSAGR_NO_SEARCH_RESULT_IN_RESPONSE = "No valid car product in Search response.";
    private static final String MESSAGE_SUCCESS = "Success";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";


    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;

    }

    @Override
    public IVerification.VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        //Verify CarSearchResult exist or not
        if(!Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivitySearchResponseType::getCarSearchResultList)
                .map(CarSearchResultListType::getCarSearchResult)
                .isPresent())
        {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList("No CarSearchResult in response!"));
        }

        String errorMessage = "";

        errorMessage = verifyCarProductReturnedBasic(input.getResponse());
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = verifyCarProductReturnedExtra(input.getResponse());
        if (errorMessage != null && !errorMessage.equals("null")) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new IVerification.VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    private boolean isthereValidCarProductInResult(CarSupplyConnectivitySearchResponseType response)
    {
        return Optional.ofNullable(response)
                .map(CarSupplyConnectivitySearchResponseType::getCarSearchResultList)
                .map(CarSearchResultListType::getCarSearchResult)
                .map(results -> results == null ? null : results.get(0))
                .map(CarSearchResultType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(cars -> cars == null ? null : cars.get(0))
                .isPresent();
    }

    private String verifyCarProductReturnedBasic(CarSupplyConnectivitySearchResponseType response)
    {
        final boolean isThereCarExisted = Optional.ofNullable(response)
                .map(CarSupplyConnectivitySearchResponseType::getCarSearchResultList)
                .map(CarSearchResultListType::getCarSearchResult)
                .isPresent();

        return  (isThereCarExisted) ? null : MESSAGR_NO_SEARCH_RESULT_IN_RESPONSE;
    }

    private String verifyCarProductReturnedExtra(CarSupplyConnectivitySearchResponseType response)
    {
        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder();

        if (!isthereValidCarProductInResult(response)) {
            errorMsg.append(MESSAGR_NO_SEARCH_RESULT_IN_RESPONSE);
        }

        if (null != response.getErrorCollectionList()) {
        /*
            for (final ErrorCollectionType errorCollectionType : response.getErrorCollectionList().getErrorCollection())
            {
                for (final FieldInvalidErrorType fieldInvalidErrorType : errorCollectionType.getFieldInvalidErrorList().getFieldInvalidError())
                {
                    final String filedKey = fieldInvalidErrorType.getFieldKey().getXPath();
                    final String description = fieldInvalidErrorType.getDescriptionRawText();

                    errorMsg.append(filedKey);
                    errorMsg.append(description);
                }
            }

        */
            errorMsg.append(VerificationHelper.verifyExtra(getXmlFieldValue(response.getErrorCollectionList(), TAG_DESCRIPTION_RAW_TEXT)));
        }

        return errorMsg.toString().isEmpty() ? null : errorMsg.toString();
    }
}
