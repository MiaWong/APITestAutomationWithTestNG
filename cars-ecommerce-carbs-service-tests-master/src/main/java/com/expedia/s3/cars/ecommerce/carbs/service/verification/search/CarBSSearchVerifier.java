package com.expedia.s3.cars.ecommerce.carbs.service.verification.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.CarbsCommonVerification;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import org.testng.Assert;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by fehu on 11/9/2016.
 */
public class CarBSSearchVerifier
{

    private CarBSSearchVerifier()
    {
    }

    public static CarProductType isCarbsSearchWorksVerifier(TestData testData, CarECommerceSearchRequestType requestType,
                                                            CarECommerceSearchResponseType responseType) throws IOException, DataAccessException
    {
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestType, responseType);
        final CarbsSearchResponseBasicVerification verifications = new CarbsSearchResponseBasicVerification();
        final CarProductType result = verifications.verify(searchVerificationInput, testData);

        if (null == result)
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + "no expect car return");
        }
        return result;
    }

    public static String verifyIfPrePayBooleanReturnInSearchResponseForHertz(CarECommerceSearchResponseType responseType,
                                                                             DataSource inventoryDataSource) throws DataAccessException
    {
        final StringBuilder errorMsg = new StringBuilder();

        final CarbsCommonVerification verifications = new CarbsCommonVerification();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(inventoryDataSource);

        for (final CarSearchResultType searchResult : responseType.getCarSearchResultList().getCarSearchResult())
        {
            for (final CarProductType car : searchResult.getCarProductList().getCarProduct())
            {
                errorMsg.append(verifications.verifyHertzPrepayCarIsCorrect(car, inventoryHelper));
            }
        }

        if (!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }
        return errorMsg.toString();
    }

    public static String verifyIfPrePayCarReferencePriceReturnForSearch(CarECommerceSearchResponseType responseType, boolean shouldReferencePriceReturned) throws DataAccessException
    {
        final StringBuilder errorMsg = new StringBuilder();

        final CarbsCommonVerification verifications = new CarbsCommonVerification();

        for (final CarSearchResultType searchResult : responseType.getCarSearchResultList().getCarSearchResult())
        {
            for (final CarProductType car : searchResult.getCarProductList().getCarProduct())
            {
                if (car.getPrePayBoolean().booleanValue())
                {
                    errorMsg.append(verifications.verifyIfPrePayCarReferencePriceReturn(car, shouldReferencePriceReturned));
                }
            }
        }

        if (!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }
        return errorMsg.toString();
    }
}
