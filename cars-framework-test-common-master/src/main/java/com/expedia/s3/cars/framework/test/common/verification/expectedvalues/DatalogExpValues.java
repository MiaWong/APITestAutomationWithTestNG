package com.expedia.s3.cars.framework.test.common.verification.expectedvalues;

import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.CommonKeys;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.TP95Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-mechen on 7/2/2017.
 */
public class DatalogExpValues {
    private DatalogExpValues()
    {
    }

    //Common keys for TP95 and VRD error logging, for some nodes we have no way to verify these values, so just verify these keys are logged - give it a value ""
    public static Map<String, String> getExpCommonValues(AuditLogTrackingDataType trackingData,
                                                         PointOfSaleKeyType pointOfSaleKey)
    {
        final Map<String, String> expectedValues = new HashMap<String, String>();

        expectedValues.put(CommonKeys.CHILDGUID, "");
        expectedValues.put(CommonKeys.ORIGINATINGGUID, "");
        expectedValues.put(CommonKeys.POSJURISDICTION, pointOfSaleKey.getJurisdictionCountryCode());
        expectedValues.put(CommonKeys.POSMANAGEMENTUNIT, pointOfSaleKey.getManagementUnitCode());
        expectedValues.put(CommonKeys.POSCOMPANY, pointOfSaleKey.getCompanyCode());
        expectedValues.put(CommonKeys.TPID, trackingData.getAuditLogTPID().toString());
        expectedValues.put(CommonKeys.TUID, String.valueOf(trackingData.getLogonUserKey().getUserID()));
        return expectedValues;
    }

    //Common keys for TP95, for some nodes we have no way to verify these values, so just verify these keys are logged - give it a value ""
    public static Map<String, String> getExpCommonTP95Values(AuditLogTrackingDataType trackingData, PointOfSaleKeyType pointOfSaleKey)
    {
        final Map<String, String> expectedValues = getExpCommonValues(trackingData, pointOfSaleKey);

        expectedValues.put(TP95Keys.COMPLETETIME, "");
        expectedValues.put(TP95Keys.PROCESSINGTIME, "");
        expectedValues.put(TP95Keys.STARTTIME, "");
        expectedValues.put(TP95Keys.UNCAUGHTEXCEPTION, "");

        return expectedValues;
    }
}
