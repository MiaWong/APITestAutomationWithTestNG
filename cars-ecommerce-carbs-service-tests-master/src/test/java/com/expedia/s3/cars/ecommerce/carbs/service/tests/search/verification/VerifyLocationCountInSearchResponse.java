package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.database.CarbsDB;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerifyLocationCountInSearchResponse  implements IVerification<SearchVerificationInput, BasicVerificationContext> {

    final Logger logger = Logger.getLogger(getClass());
    private static final String MESSAGE_SUCCESS = "Success";
    private static boolean isChangedScenario;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext) {
        if(isChangedScenario)
        {
            try {
                verifyChangedLocationCountInResponse(searchVerificationInput);
            } catch (DataAccessException e) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + e);
            }
        }
        else
        {
            verifyStartLocationIndex(searchVerificationInput);
            verifyLocationCountInResponse(searchVerificationInput);
            isChangedScenario = true;
        }
        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    private void verifyStartLocationIndex(SearchVerificationInput searchVerificationInput)
    {
        final long startLocationIndexReq = searchVerificationInput.getRequest().getCarECommerceSearchCriteriaList()
                .getCarECommerceSearchCriteria().get(0).getCarTransportationSegment().getStartCarLocation().getStartLocationIndex();
        final long startLocationIndexRs = searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().get(0).getStartLocationIndex();
        if (startLocationIndexReq < 1 && startLocationIndexRs != 1)
        {
            Assert.fail("When StartLocationIndex in request is not set or null, StartLocationIndex in response should be 1");
        }

        if (startLocationIndexReq >= 1 && startLocationIndexReq != startLocationIndexRs)
        {
            Assert.fail("When StartLocationIndex in request is >= 1, StartLocationIndex in response should be equal to the value in request!"
            + " StartLocationIndex in request:" + startLocationIndexReq + " ,StartLocationIndex in response: " + startLocationIndexRs);
        }
    }

    private void verifyLocationCountInResponse(SearchVerificationInput searchVerificationInput)
    {
        final long locationCountInAllCarProduct = getLocationCountFromCarBSSearchResponse(searchVerificationInput.getResponse());
        final long locationCountInSearchRs = searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().get(0).getLocationCount();
        if (locationCountInAllCarProduct != locationCountInSearchRs)
        {
            Assert.fail("LocationCount in CarBS search response is not correct! LocationCount in CarSearchResult's attribute is "
            + locationCountInSearchRs + " ,actual location count returned in CarBS search response is " + locationCountInAllCarProduct);
        }
    }

    private void verifyChangedLocationCountInResponse(SearchVerificationInput searchVerificationInput) throws DataAccessException {
        final long locationCountInSearchRs = searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().get(0).getLocationCount();
        final CarbsDB carbsDB = new CarbsDB(DatasourceHelper.getCarBSDatasource());
        final long defaultMaxCount = carbsDB.getValueByName(ServiceConfigs.SEARCH_MAXLATLONGLOCATION_COUNT);
        if (locationCountInSearchRs > defaultMaxCount)
        {
            Assert.fail("LocationCount in CarBS search response is not correct! LocationCount in CarSearchResult's attribute is "
                    + locationCountInSearchRs + " ,max location count in CarBS config/list is " + defaultMaxCount);
        }
    }

    private long getLocationCountFromCarBSSearchResponse(CarECommerceSearchResponseType response)
    {
        final List<String> locationList = new ArrayList<>();
        final List<CarProductType> carProducts = response.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct();

        for (final CarProductType carProduct : carProducts)
        {
            final CarCatalogKeyType carCatalogKey = carProduct.getCarInventoryKey().getCarCatalogKey();
            final CarLocationKeyType carLocationKey = carCatalogKey.getCarPickupLocationKey();
            final long supplierId = carCatalogKey.getVendorSupplierID();
            final String locationCode = carLocationKey.getLocationCode();
            final String locationNum = carLocationKey.getCarLocationCategoryCode() + carLocationKey.getSupplierRawText();
            final String location = supplierId + locationCode + locationNum;

            boolean locationExist = false;
            for (final String loc: locationList)
            {
                if (loc.equalsIgnoreCase(location))
                {
                    locationExist = true;
                    break;
                }
            }
            if (!locationExist)
            {
                locationList.add(location);
            }
        }

        return locationList.size();
    }
}
