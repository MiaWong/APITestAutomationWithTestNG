package com.expedia.s3.cars.ecommerce.carbs.service.verification.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 11/9/2016.
 */
public class CarbsSearchResponseBasicVerification implements IVerification<SearchVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_SEARCH_RESULT_IN_RESPONSE = "No valid car product in Search response.";
    private static final String MESSAGE_SUCCESS = "Success";


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    @Deprecated
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext basicVerificationContext) {
        String errorMessage = "";

        errorMessage = verifyCarProductReturnedBasic(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = verifyCarProductReturnedExtra(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    private String verifyCarProductReturnedBasic(CarECommerceSearchResponseType response) {
        if (null == response || null == response.getCarSearchResultList())
        {
            return "response is null or searchresultList is null";
        }

        if (CollectionUtils.isEmpty(response.getCarSearchResultList().getCarSearchResult())
                || CollectionUtils.isEmpty(response.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct())) {
            return MESSAGR_NO_SEARCH_RESULT_IN_RESPONSE;
        }
        return null;
    }

    @SuppressWarnings("PMD")
    private String verifyCarProductReturnedExtra(CarECommerceSearchResponseType response) {
        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder();

        if (null != response.getSearchErrorCollection()) {
            if (response.getSearchErrorCollection().getDownstreamServiceTimeoutError() != null) {
                final List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(response.getSearchErrorCollection().getDownstreamServiceTimeoutError(), "DescriptionRawText");
                if (!descriptionRawTextList.isEmpty()) {
                    errorMsg.append("ErrorCollection is present in response");
                    descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
                }
            }
        }


        return errorMsg.toString().isEmpty() ? null : errorMsg.toString();
    }


    public CarProductType verify(SearchVerificationInput input, TestData testData) throws DataAccessException {
        if (verifyCarProductReturnedBasic(input.getResponse()) != null) {
            Assert.fail(verifyCarProductReturnedBasic(input.getResponse()));
        }

        if (verifyCarProductReturnedExtra(input.getResponse()) != null) {
            Assert.fail(verifyCarProductReturnedExtra(input.getResponse()));
        }

        final CarbsRequestGenerator carbsRequestGenerator = new CarbsRequestGenerator(input.getRequest(), input.getResponse(), testData);
        return carbsRequestGenerator.getCarProduct(input.getResponse(), testData);
    }

}
