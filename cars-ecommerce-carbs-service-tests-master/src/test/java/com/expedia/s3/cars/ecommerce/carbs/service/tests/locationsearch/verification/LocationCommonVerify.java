package com.expedia.s3.cars.ecommerce.carbs.service.tests.locationsearch.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocationLatLong;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACLQReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACLQRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.FilterType;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.messages.location.search.defn.v1.CarLocationSearchResponse;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyang4 on 1/9/2018.
 */
@SuppressWarnings("PMD")
public class LocationCommonVerify {
    public static void verifyLocationSearch(CarLocationSearchResponse response, LocationSearchTestScenario scenario) throws DataAccessException {
        final double MILES_PER_KILOMETER = 0.621371192;
        final double MAP_MILES_PER_LATITUDE = 69.05;
        final double MAP_MILES_PER_LONGITUDE = 69.172;
        final double DEGREES_TO_RADIANS_MULTIPLIER = .01745329251994329547;

        final BigDecimal latitude = scenario.getLatitude();
        final BigDecimal longitude = scenario.getLongitude();

        double radius = (double) scenario.getRadius();
        final String radiusUnit = scenario.getDistanceUnit();

        // if the radius isn't in miles, convert to miles
        // the distance unit has already been validated to be in miles (MI) or kilometers (KM)
        if ("km".equals(radiusUnit)) {
            // convert the radius to miles
            radius = radius * MILES_PER_KILOMETER;
        }

        final double latitudeIncrement = radius / MAP_MILES_PER_LATITUDE;
        final BigDecimal latitudeIncrementBD = BigDecimal.valueOf(latitudeIncrement);
        final BigDecimal latitudeMin = latitude.subtract(latitudeIncrementBD);
        final BigDecimal latitudeMax = latitude.add(latitudeIncrementBD);

        final double longitudeMinSubtrahend = radius / (MAP_MILES_PER_LONGITUDE * Math.cos(latitudeMin.doubleValue() * DEGREES_TO_RADIANS_MULTIPLIER));
        final BigDecimal longitudeMin = longitude.subtract(BigDecimal.valueOf(longitudeMinSubtrahend));

        final double longitudeMaxAugend = radius / (MAP_MILES_PER_LONGITUDE * Math.cos(latitudeMax.doubleValue() * DEGREES_TO_RADIANS_MULTIPLIER));
        final BigDecimal longitudeMax = longitude.add(BigDecimal.valueOf(longitudeMaxAugend));

        /**search the location codes from DB by sproc, and compare location code reuturned in response
         with location code searched out by sproc**/
        final String vendorCode = "null";

        final List<String> parametersForSproc = new ArrayList<String>();
        parametersForSproc.add(vendorCode);
        parametersForSproc.add(String.valueOf(latitudeMin));
        parametersForSproc.add(String.valueOf(latitudeMax));
        parametersForSproc.add(String.valueOf(longitudeMin));
        parametersForSproc.add(String.valueOf(longitudeMax));
        //Add the collection,delivery,outOfficeHours parameters
        if (!CompareUtil.isObjEmpty(scenario.getDeliveryBoolean())) {
            parametersForSproc.add(scenario.getDeliveryBoolean());
        } else {
            parametersForSproc.add("null");
        }
        if (!CompareUtil.isObjEmpty(scenario.getCollectionBoolean())) {
            parametersForSproc.add(scenario.getCollectionBoolean());
        } else {
            parametersForSproc.add("null");
        }
        if (!CompareUtil.isObjEmpty(scenario.getOutOfOfficeHourBoolean())) {
            parametersForSproc.add(scenario.getOutOfOfficeHourBoolean());
        } else {
            parametersForSproc.add("null");
        }

        //Search location code from DB by sproc based on specified lat/long/radius/VendorCode
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final List<CarVendorLocationLatLong> vendorLocationList = inventoryHelper.excuteCarVendorLocationProc("CarVendorLocationLstByLatLong#01", parametersForSproc);
        //DataTable getSupplierID = new DataTable();

        if (!CompareUtil.isObjEmpty(vendorLocationList)) {
            final List<CarLocationType> resLocationList = response.getCarLocationList().getCarLocation();
            for (CarVendorLocationLatLong location : vendorLocationList) {
                //string vendorCode = dr["CarVendorCode"].ToString();
                final String airportCode = location.getAirportCode();
                final String vendorLocationCode = location.getCarVendorLocationCode();
                final String lat = location.getLatitude();
                final String lon = location.getLongitude();
                final Long supplierIDFromDB = Long.valueOf(location.getSupplierID());

                //Map the LocationTypeID to expected  CarShuttleCategoryCode
                //-1NoShuttle 0NoShuttle 1NoShuttle 2NoShuttle 3NoShuttle ShuttleToCar 5ShuttleToCounter 6NoShuttle
                String expectedCarShuttleCategoryCode = null;
                final String carShuttleCategoryID = CompareUtil.isObjEmpty(location.getCarShuttleCategoryID()) ? "" : location.getCarShuttleCategoryID();
                if (carShuttleCategoryID.contains("1")) {
                    expectedCarShuttleCategoryCode = "NoShuttle";
                }
                if (carShuttleCategoryID.contains("2")) {
                    expectedCarShuttleCategoryCode = "ShuttleToCounter";
                }
                if (carShuttleCategoryID.contains("3")) {
                    expectedCarShuttleCategoryCode = "ShuttleToCar";
                }
                //add the CollectionBool and DeliveryBool,OutOfOfficeHoursBool  DeliveryBoolean="true"
                final String collectionBool = location.getCollectionBool();
                final String deliveryBool = location.getDeliveryBool();
                final String outOfOfficeHoursBool = location.getOutOfOfficeHoursBool();
                //Get the details info also
                //FirstAddressLine:StreetAddress1  CityName:CityName ProvinceName: StateProvinceName CountryAlpha3Code:ISOCountryCode
                final String expectedFirstAddressLine = location.getStreetAddress();
                final String expectedCityName = location.getCityName();
                final String expectedProvinceName = location.getStateProvinceName();
                final String expectedCountryAlpha3Code = location.getiSOCountryCode();


                final int latFromDB = Integer.valueOf(lat.split("\\.")[0] + lat.split("\\.")[1]);
                final int lonFronDB = Integer.valueOf(lon.split("\\.")[0] + lon.split("\\.")[1]);

                boolean match_CarLocation = false;
                for (CarLocationType carLocation : resLocationList) {
                    if (CompareUtil.compareObject(carLocation.getCarLocationKey().getLocationCode(), airportCode, null, null)
                            && CompareUtil.compareObject(carLocation.getCarLocationKey().getCarLocationCategoryCode() + carLocation.getCarLocationKey().getSupplierRawText(), vendorLocationCode, null, null)
                            && CompareUtil.compareObject(carLocation.getLatLong().getLatitudeAmount().getDecimal(), latFromDB, null, null)
                            && CompareUtil.compareObject(carLocation.getLatLong().getLongitudeAmount().getDecimal(), lonFronDB, null, null)
                            && CompareUtil.compareObject(carLocation.getSupplierID(), supplierIDFromDB, null, null)
                            && CompareUtil.compareObject(carLocation.getCarShuttleCategoryCode(), expectedCarShuttleCategoryCode, null, null)
                            && CompareUtil.compareObject(String.valueOf(carLocation.getCarLocationKey().getCollectionBoolean()), collectionBool, null, null)
                            && CompareUtil.compareObject(String.valueOf(carLocation.getCarLocationKey().getDeliveryBoolean()), deliveryBool, null, null)
                            && CompareUtil.compareObject(String.valueOf(carLocation.getCarLocationKey().getOutOfOfficeHourBoolean()), outOfOfficeHoursBool, null, null)
                            && ((!scenario.isIncludeLocationDetails() && CompareUtil.isObjEmpty(carLocation.getAddress().getFirstAddressLine())) ||
                            (scenario.isIncludeLocationDetails() && (CompareUtil.compareObject(carLocation.getAddress().getFirstAddressLine(), expectedFirstAddressLine, null, null)
                                    && CompareUtil.compareObject(carLocation.getAddress().getCityName(), expectedCityName, null, null)
                                    && CompareUtil.compareObject(carLocation.getAddress().getProvinceName(), expectedProvinceName, null, null)
                                    && CompareUtil.compareObject(carLocation.getAddress().getCountryAlpha3Code(), expectedCountryAlpha3Code, null, null))))) {
                        match_CarLocation = true;
                        break;
                    }

                }

                if (!match_CarLocation) {
                    Assert.fail("The CarLocation searched from DB cannot be found in CarLocationSearch response:"
                            + " LocationCode: " + airportCode
                            + " CarVendorLocationCode: " + vendorLocationCode
                            + " Latitude: " + latFromDB
                            + " Longitude: " + lonFronDB
                            + " SupplierID: " + supplierIDFromDB
                            + " CarShuttleCategoryCode: " + expectedCarShuttleCategoryCode
                            + " DeliveryBoolean: " + deliveryBool
                            + " CollectionBoolean: " + collectionBool
                            + " outOfOfficeHoursBoolean: " + outOfOfficeHoursBool);

                    break;
                }

            }

            if (resLocationList.size() != vendorLocationList.size()) {
                Assert.fail(String.format("LocaitonCount is not expected, expected: {%s}, Actual: {%s}", vendorLocationList.size(), resLocationList.size()));
            }
        }


    }


    public static void verifyCarBSLocationSearchRouting(String testType, CarLocationIataSearchResponse response, CarLocationSearchResponse responseCVL, Document gdsMessageDoc) throws Exception {
        //Get CRSLogs for CarSS location search and AmoudeusSCS location search
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        final Node aclqRspNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.AmadeusNodeTags.ACLQ_LOCATION_LIST_RESPONSE_TYPE);
        final ACLQRsp aclqRsp = new ACLQRsp(aclqRspNode, null, new CarsSCSDataSource(DatasourceHelper.getAmadeusSCSDatasource()));

        //Verify locations returned in response
        if ("BVT".equals(testType) && CompareUtil.isObjEmpty(response.getCarLocationList())) {
            Assert.fail("CarLocationList is empty in CarBS location search response!");
        }
        //Verify CarBS route the location search request to downstream (AmoudeusSCS)
        else if ("BVT".equals(testType) && CompareUtil.isObjEmpty(aclqRsp.getCarLocationList())) {
            Assert.fail("CarBS do not route the location search request to AmoudeusSCS due to no CRSLogs found!");
        }

        //Verify CarBS is not doing any filtering based on CVL, CarBS return all the data as provided by CarSS abd AmadeusSCS
        boolean isEqualCarLocationKey = false;
        if ("NoFilterFromCVL".equals(testType) && response.getCarLocationList().getCarLocation().size() == aclqRsp.getCarLocationList().size()) {
            //Mapping CarLocations between CarBS and AmoudeusSCS location search response
            for (CarLocationType carLocationSCS : aclqRsp.getCarLocationList()) {
                String carLocationKeySCS = carLocationSCS.getCarLocationKey().getLocationCode() + carLocationSCS.getCarLocationKey().getCarLocationCategoryCode() + carLocationSCS.getCarLocationKey().getSupplierRawText();
                for (CarLocationType carLocation : response.getCarLocationList().getCarLocation()) {
                    String carLocationKey = carLocation.getCarLocationKey().getLocationCode() + carLocation.getCarLocationKey().getCarLocationCategoryCode() + carLocation.getCarLocationKey().getSupplierRawText();
                    if (carLocationKey.equals(carLocationKeySCS)) {
                        isEqualCarLocationKey = true;
                    }
                }
                if (!isEqualCarLocationKey) {
                    Assert.fail("CarLocationKey : " + carLocationKeySCS + " is not passed from AmoudeusSCS to CarBS level");
                }
                isEqualCarLocationKey = false;
            }
        } else if ("NoFilterFromCVL".equals(testType) && (response.getCarLocationList().getCarLocation().size() != aclqRsp.getCarLocationList().size())) {
            Assert.fail("The count of CarLocations is not equal from CarBS and CarSS and AmoudeusSCS level!");
        }

        //Verify CarBS route to CVL
        if ("RouteToCVL".equals(testType) && aclqRspNode == null) {
            if (response.getCarLocationList().getCarLocation().size() != 0 && response.getCarLocationList().getCarLocation().size() == responseCVL.getCarLocationList().getCarLocation().size()) {
                //Mapping CarLocations between CarBS location search response and carLocationKeyList from CVL table
                for (CarLocationType carLocation : responseCVL.getCarLocationList().getCarLocation()) {
                    String carLocationKey = carLocation.getCarLocationKey().getLocationCode() + carLocation.getCarLocationKey().getCarLocationCategoryCode() + carLocation.getCarLocationKey().getSupplierRawText();
                    for (CarLocationType cl : response.getCarLocationList().getCarLocation()) {
                        String carLocationKeyCVL = cl.getCarLocationKey().getLocationCode() + cl.getCarLocationKey().getCarLocationCategoryCode() + cl.getCarLocationKey().getSupplierRawText();
                        if (carLocationKeyCVL.equals(carLocationKey)) {
                            isEqualCarLocationKey = true;
                        }
                    }
                    if (!isEqualCarLocationKey) {
                        Assert.fail("CarLocationKey :" + carLocationKey + " is not found in CVL table!");
                    }
                }
            } else if (response.getCarLocationList().getCarLocation().size() != responseCVL.getCarLocationList().getCarLocation().size()) {
                Assert.fail(String.format("The counts of locations between CarBS location search response(%s) and CVL filtered results(%s) is not equal!",
                        response.getCarLocationList().getCarLocation().size(), responseCVL.getCarLocationList().getCarLocation().size()));
            }
        } else if ("RouteToCVL".equals(testType) && aclqRspNode != null) {
            Assert.fail("CarBS do not route the location search to CVL but downstream for WorldSpan!");
        }
    }


    public static void verifyCarBSLocationSearchRoutingNegative(CarLocationIataSearchResponse response, String testType) {
        if (CompareUtil.isObjEmpty(response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError())) {
            Assert.fail("NO FieldInvalidErrorList returned in response!");
        }
        if (("EgenciaIATAForWorldSpanPOS".equals(testType) || "WorldSpanIATAForEgenciaPOS".equals(testType))
                && !response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError().get(0).getDescriptionRawText().contains(
                "IATA requests are not supported")) {
            Assert.fail("Do not return expected error message!");
        }

        if ("BothLatLongAndIATA".equals(testType) && (response.getErrorCollection().getFieldInvalidErrorList() == null
                || response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError() == null
                || !response.getErrorCollection().getFieldInvalidErrorList().getFieldInvalidError().get(0).getDescriptionRawText()
                .contains("Only IATA or longitude, latitude, and radius can be present in the request"))) {
            Assert.fail("Do not return expected error messages!");
        }

    }


    public static void verifyLocationSearchFilterInAclqRequest(Document gdsMessageDoc, LocationSearchTestScenario config) {
        // Get ACLQ request
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            Assert.fail("No GDS messages found ! ");
        }
        final Node aclqReqNode = PojoXmlUtil.getNodeByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags.AmadeusNodeTags.ACLQ_LOCATION_LIST_REQUEST_TYPE);
        final ACLQReq aclqReq = new ACLQReq(aclqReqNode);

        // verify the Del Col or OHR, VIC
        FilterType filter = aclqReq.getFilter();
        if (!CompareUtil.isObjEmpty(config.getDeliveryBoolean())) {
            boolean delBool = Boolean.parseBoolean(config.getDeliveryBoolean());
            if (filter.getDeliveryBoolean() != delBool) {
                Assert.fail(String.format("%s value in ACLQ %s is [%s] but expected value is %s", "request", "DEL", filter.getDeliveryBoolean(), delBool));
            }

        }
        if (!CompareUtil.isObjEmpty(config.getCollectionBoolean())) {
            boolean colBool = Boolean.parseBoolean(config.getCollectionBoolean());
            if (filter.getCollectionBoolean() != colBool) {
                Assert.fail(String.format("%s value in ACLQ %s is [%s] but expected value is %s", "request", "COL", filter.getCollectionBoolean(), colBool));
            }

        }
        if (!CompareUtil.isObjEmpty(config.getOutOfOfficeHourBoolean())) {
            boolean ohrBool = Boolean.parseBoolean(config.getOutOfOfficeHourBoolean());
            if (filter.getOutOfOfficeHourBoolean() != ohrBool) {
                Assert.fail(String.format("%s value in ACLQ %s is [%s] but expected value is %s", "request", "OHR", filter.getOutOfOfficeHourBoolean(), ohrBool));
            }

        }
        if (!CompareUtil.isObjEmpty(config.getAirportVicinityBoolean())) {
            boolean vicBool = Boolean.parseBoolean(config.getAirportVicinityBoolean());
            if (filter.getAirportVicinityBoolean() != vicBool) {
                Assert.fail(String.format("%s value in ACLQ %s is [%s] but expected value is %s", "request", "VIC", filter.getAirportVicinityBoolean(), vicBool));
            }
        }
    }
}
