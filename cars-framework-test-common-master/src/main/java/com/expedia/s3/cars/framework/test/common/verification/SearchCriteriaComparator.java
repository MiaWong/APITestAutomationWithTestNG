package com.expedia.s3.cars.framework.test.common.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 1/2/2017.
 */
public class SearchCriteriaComparator {
    private SearchCriteriaComparator() {
    }

    public static List<String> createLocationKeyIgnoreList(CarLocationKeyType locationKey)
    {
        //ignore location codes when locationID exist
        final List<String> ignoreList = new ArrayList<>();
        if(null != locationKey.getCarVendorLocationID()
                && locationKey.getCarVendorLocationID() > 0) {
            ignoreList.add(CarTags.LOCATION_CODE);
            ignoreList.add(CarTags.CAR_LOCATION_CATEGORY_CODE);
            ignoreList.add(CarTags.SUPPLIER_RAW_TEXT);
        }
        else{
            ignoreList.add(CarTags.CAR_VENDOR_LOCATION_ID);
        }

        return ignoreList;
    }

    public static boolean isSearchCriteriaEqual(CarSearchCriteriaType expSearchCriteria, CarSearchCriteriaType actSearchCriteria,
                                                StringBuilder errorMsg) throws DataAccessException {
        boolean isEqual = true;
        //ignore location codes when locationID exist for pickUP
        List<String> ignoreList = createLocationKeyIgnoreList(expSearchCriteria.getCarTransportationSegment().getStartCarLocationKey());

        if(!CarNodeComparator.isCarLocationKeyEqual(expSearchCriteria.getCarTransportationSegment().getStartCarLocationKey(),
                actSearchCriteria.getCarTransportationSegment().getStartCarLocationKey(), errorMsg, ignoreList))
        {
            isEqual = false;
        }
        //ignore location codes when locationID exist for dropOff
        ignoreList = createLocationKeyIgnoreList(expSearchCriteria.getCarTransportationSegment().getEndCarLocationKey());

        if(!CarNodeComparator.isCarLocationKeyEqual(expSearchCriteria.getCarTransportationSegment().getEndCarLocationKey(),
                actSearchCriteria.getCarTransportationSegment().getEndCarLocationKey(), errorMsg, ignoreList))
        {
            isEqual = false;
        }
        if(!CarNodeComparator.isCarRateEqual(expSearchCriteria.getCarRate(),actSearchCriteria.getCarRate(), errorMsg, new ArrayList<>()))
        {
            isEqual = false;
        }
        if(!CarNodeComparator.isVendorSupplierIDListEqual(expSearchCriteria.getVendorSupplierIDList(),actSearchCriteria.getVendorSupplierIDList(), errorMsg))
        {
            isEqual = false;
        }
        if(!CarNodeComparator.isSegmentDateTimeRangeEqual(expSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange(),actSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange(), errorMsg))
        {
            isEqual = false;
        }
        return isEqual;
    }
}
