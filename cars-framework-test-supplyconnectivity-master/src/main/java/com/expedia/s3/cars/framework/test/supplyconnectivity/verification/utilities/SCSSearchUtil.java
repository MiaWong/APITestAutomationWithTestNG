package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 7/3/2017.
 */
public class SCSSearchUtil {

    private SCSSearchUtil()
    {
    }

    public static String getRequestVendorIDsFromSCSsearchRequest(CarSupplyConnectivitySearchRequestType request)
    {
        String requestVendorIDs = "";
        final List<String> requestVendorIDList = new ArrayList<String>();
        for (final CarSearchCriteriaType searchCriteria : request.getCarSearchCriteriaList().
                getCarSearchCriteria())
        {
            for (final Long vendorSupplierID : searchCriteria.getVendorSupplierIDList().getVendorSupplierID())
            {
                boolean exist = false;
                for (final String existingSupplierID : requestVendorIDList)
                {
                    if (existingSupplierID.equals(vendorSupplierID.toString()))
                    {
                        exist = true;
                        break;
                    }
                }
                if (!exist)
                {
                    requestVendorIDList.add(vendorSupplierID.toString());
                }
            }
        }
        if (!requestVendorIDList.isEmpty())
        {
            requestVendorIDs = StringUtils.join(requestVendorIDList, ",");
        }
        return requestVendorIDs;
    }

    public static String getPickupLocationCategoryFilterFromSCSsearchRequest(CarSupplyConnectivitySearchRequestType request)
    {
        String locationCategoryFilter = "";
        final List<String> locationCategoryList = new ArrayList<>();
        for(final CarSearchCriteriaType searchCriteria : request.getCarSearchCriteriaList().
            getCarSearchCriteria())
        {
            if (null != searchCriteria.getCarTransportationSegment().getStartCarLocationKey().
                    getCarLocationCategoryCode())
            {
                boolean exist = false;
                for (final String existingLocationCategory : locationCategoryList)
                {

                    if (existingLocationCategory.equals(searchCriteria.getCarTransportationSegment().
                            getStartCarLocationKey().getCarLocationCategoryCode()))
                    {
                        exist = true;
                        break;
                    }

                }
                if (!exist)
                {
                    locationCategoryList.add(searchCriteria.getCarTransportationSegment().
                            getStartCarLocationKey().getCarLocationCategoryCode());
                }
            }

        }
        if (locationCategoryList.isEmpty())
        {
            locationCategoryFilter = "All";
        }
        else
        {
            locationCategoryFilter = StringUtils.join(locationCategoryList, ",");
        }
        return locationCategoryFilter;
    }

    public static String getDropoffLocationCategoryFilterFromSCSsearchRequest(CarSupplyConnectivitySearchRequestType request)
    {
        String locationCategoryFilter = "";
        final List<String> locationCategoryList = new ArrayList<String>();
        for (final CarSearchCriteriaType searchCriteria : request.getCarSearchCriteriaList().getCarSearchCriteria())
        {
            if (null != searchCriteria.getCarTransportationSegment().getEndCarLocationKey().
                    getCarLocationCategoryCode())
            {
                boolean exist = false;
                for (final String existingLocationCategory : locationCategoryList)
                {

                    if (existingLocationCategory.equals(searchCriteria.getCarTransportationSegment().
                            getEndCarLocationKey().getCarLocationCategoryCode()))
                    {
                        exist = true;
                        break;
                    }

                }
                if (!exist)
                {
                    locationCategoryList.add(searchCriteria.getCarTransportationSegment().
                            getEndCarLocationKey().getCarLocationCategoryCode());
                }
            }

        }
        if (locationCategoryList.isEmpty())
        {
            locationCategoryFilter = "All";
        }
        else
        {
            locationCategoryFilter = StringUtils.join(locationCategoryList, ",");
        }
        return locationCategoryFilter;
    }

    public static String getPickupLocationsFromSCSsearchRequest(CarSupplyConnectivitySearchRequestType request)
    {
        String pickupLocations = "";
        final List<String> pickupLocationList = new ArrayList<String>();
        for (final CarSearchCriteriaType searchCriteria : request.getCarSearchCriteriaList().getCarSearchCriteria())
        {
            boolean exist = false;
            for (final String existingPickupLocation : pickupLocationList)
            {
                if (existingPickupLocation.equals(searchCriteria.getCarTransportationSegment().getStartCarLocationKey().getLocationCode()))                {
                    exist = true;
                    break;
                }
            }
            if (!exist)
            {
                pickupLocationList.add(searchCriteria.getCarTransportationSegment().getStartCarLocationKey().getLocationCode());
            }

        }
        if (!pickupLocationList.isEmpty())
        {
            pickupLocations = StringUtils.join(pickupLocationList, ",");
        }
        return pickupLocations;
    }

    public static String getHasSoftErrorsFromSCSSearchResponse(CarSupplyConnectivitySearchResponseType response)
    {
        String hasSoftErrors = "false";
        if (null!= response.getErrorCollectionList() && null != response.getErrorCollectionList().getErrorCollection() && !response.getErrorCollectionList().getErrorCollection().isEmpty())
        {
            hasSoftErrors = "true";
        }
        return hasSoftErrors;
    }

    public static String getResponsetVendorIDsFromSCSsearchResponse(CarSupplyConnectivitySearchResponseType response)
    {
        if(null == response.getCarSearchResultList() || null == response.getCarSearchResultList().getCarSearchResult())
        {
            return null;
        }
        String responseVendorIDs = "";
        final List<String> responseVendorIDList = new ArrayList<String>();
        for (final CarSearchResultType searchResult : response.getCarSearchResultList().getCarSearchResult())
        {
            if(null == searchResult.getCarProductList().getCarProduct() || searchResult.getCarProductList().getCarProduct().isEmpty())
            {
                continue;
            }
            for (final CarProductType carProduct : searchResult.getCarProductList().getCarProduct())
            {
                if (!responseVendorIDList.contains(String.valueOf(carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID())))
                {
                    responseVendorIDList.add(String.valueOf(carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()));
                }
            }
        }
        if (!responseVendorIDList.isEmpty())
        {
            responseVendorIDs = StringUtils.join(responseVendorIDList, ",");
        }
        return responseVendorIDs;
    }

    public static String getResponseCarCountFromSCSSearchResponse(CarSupplyConnectivitySearchResponseType response)
    {
        if (null == response.getCarSearchResultList() || response.getCarSearchResultList().getCarSearchResult().isEmpty())
        {
            return null;
        }
        int carCount = 0;
        for (final CarSearchResultType searchResult : response.getCarSearchResultList().getCarSearchResult())
        {
            if(null == searchResult.getCarProductList().getCarProduct() || searchResult.getCarProductList().getCarProduct().isEmpty())
            {
                continue;
            }
            carCount += searchResult.getCarProductList().getCarProduct().size();
        }
        return String.valueOf(carCount);
    }
}
