package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.locationsearch.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACLQReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACLQRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.FilterType;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSLocationSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchResponseType;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by v-mechen on 9/29/2018.
 */
public class VerifyASCSLocationSearch {
    private VerifyASCSLocationSearch()
    {}

    public static String verifyACLQReq(ACLQReq aclqReq, LocationSearchTestScenario config)
    {
        final StringBuilder errorMessage = new StringBuilder("");
        // verify the Del Col or OHR, VIC
        final FilterType filter = aclqReq.getFilter();
        final Boolean expDelBool = CompareUtil.isObjEmpty(config.getDeliveryBoolean()) || !Boolean.parseBoolean(config.getDeliveryBoolean())
                ? null : true;
        final Boolean expColBool = (CompareUtil.isObjEmpty(config.getCollectionBoolean()) || !Boolean.parseBoolean(config.getCollectionBoolean()))
                ? null : true;
        final Boolean expOhrBool = CompareUtil.isObjEmpty(config.getOutOfOfficeHourBoolean()) || !Boolean.parseBoolean(config.getOutOfOfficeHourBoolean())
                ? null : true;
        final Boolean expVicBool = CompareUtil.isObjEmpty(config.getAirportVicinityBoolean()) || !Boolean.parseBoolean(config.getAirportVicinityBoolean())
                ? null : true;
        if (expDelBool != filter.getDeliveryBoolean()) {
            errorMessage.append(String.format("%s value in ACLQ %s is [%s] but expected value is %s!", "request", "DEL",
                    filter.getDeliveryBoolean(), expDelBool));
         }
        if (expColBool != filter.getCollectionBoolean()) {
                errorMessage.append(String.format("%s value in ACLQ %s is [%s] but expected value is %s!", "request", "COL",
                        filter.getCollectionBoolean(), expColBool));

        }
        if (expOhrBool != filter.getOutOfOfficeHourBoolean()) {
                errorMessage.append(String.format("%s value in ACLQ %s is [%s] but expected value is %s!", "request", "OHR",
                        filter.getOutOfOfficeHourBoolean(), expOhrBool));

        }
        if (expVicBool != filter.getAirportVicinityBoolean()) {
                errorMessage.append(String.format("%s value in ACLQ %s is [%s] but expected value is %s!", "request", "VIC",
                        filter.getAirportVicinityBoolean(), expVicBool));

        }

        return errorMessage.toString();
    }

    public static String verifyACLQRspMap(CarSupplyConnectivityLocationSearchResponseType response, ACLQRsp aclqRsp, boolean includeLocationDetails)
    {
        final StringBuilder errorMessage = new StringBuilder("");
        boolean isEqualCarLocationKey = false;
        if (response.getCarLocationList().getCarLocation().size() == aclqRsp.getCarLocationList().size()) {
            //Mapping CarLocations between GDS and AmoudeusSCS location search response
            for (final CarLocationType carLocationACLQ : aclqRsp.getCarLocationList()) {
                String carLocationKeyACLQ = carLocationACLQ.getSupplierID()+carLocationACLQ.getCarLocationKey().getLocationCode() + carLocationACLQ.getCarLocationKey().getCarLocationCategoryCode() + carLocationACLQ.getCarLocationKey().getSupplierRawText();
                for (CarLocationType carLocation : response.getCarLocationList().getCarLocation()) {
                    String carLocationKey = carLocation.getSupplierID()+carLocation.getCarLocationKey().getLocationCode() + carLocation.getCarLocationKey().getCarLocationCategoryCode() + carLocation.getCarLocationKey().getSupplierRawText();
                    if (carLocationKey.equals(carLocationKeyACLQ)) {
                        isEqualCarLocationKey = true;
                        //Compare full info
                        if(!includeLocationDetails){
                            carLocationACLQ.setAddress(null);
                        }
                        final StringBuilder locationComareError = new StringBuilder("");
                        final boolean compared = CompareUtil.compareObject(carLocationACLQ, carLocation, new ArrayList<>(), locationComareError);
                        if(!compared)
                        {
                            errorMessage.append(locationComareError.toString());
                        }
                        break;
                    }
                }
                if (!isEqualCarLocationKey) {
                    errorMessage.append("CarLocationKey : " + carLocationKeyACLQ + " is not passed from ACLQ to ASCS");
                }
                isEqualCarLocationKey = false;
            }
        } else {
            errorMessage.append("The count of CarLocations is not equal from ACLQ to AmoudeusSCS!");
        }

        return errorMessage.toString();
    }

    //check the returned carLocationKey contains the correct value for COL,DEl,OHR,VIC
    public static String verifyACLQRsp(ACLQReq aclqReq, ACLQRsp aclqRsp)
    {
        final StringBuilder errorMessage = new StringBuilder("");
        final FilterType filter = aclqReq.getFilter();
        for(final CarLocationType carLocationACLQ : aclqRsp.getCarLocationList())
        {
            final CarLocationKeyType locationKey = carLocationACLQ.getCarLocationKey();
            if (locationKey.getDeliveryBoolean() != filter.getDeliveryBoolean())
            {
                errorMessage.append(String.format("DeliveryBoolean in ACLQ response %s is not equal to request filter value %s",
                        locationKey.getDeliveryBoolean(), filter.getDeliveryBoolean()));
            }
            if (locationKey.getCollectionBoolean() != filter.getCollectionBoolean())
            {
                errorMessage.append(String.format("CollectionBoolean in ACLQ response %s is not equal to request filter value %s",
                        locationKey.getCollectionBoolean(), filter.getCollectionBoolean()));
            }
            if (locationKey.getOutOfOfficeHourBoolean() != filter.getOutOfOfficeHourBoolean())
            {
                errorMessage.append(String.format("OutOfOfficeHourBoolean in ACLQ response %s is not equal to request filter value %s",
                        locationKey.getOutOfOfficeHourBoolean(), filter.getOutOfOfficeHourBoolean()));
            }
        }
        return errorMessage.toString();
    }

    public static void verifyASCSLocationSearch(SCSLocationSearchRequestGenerator requestGenerator, LocationSearchTestScenario config) throws DataAccessException {
        StringBuilder errorMessage = new StringBuilder("");

        //Get GDS request
        final Node aclqReqNode = PojoXmlUtil.getNodeByTagName(requestGenerator.getSpooferDoc().getFirstChild(), GDSMsgNodeTags.AmadeusNodeTags.ACLQ_LOCATION_LIST_REQUEST_TYPE);
        final ACLQReq aclqReq = new ACLQReq(aclqReqNode);

        // verify the Del Col or OHR, VIC in GDS request
        errorMessage.append(verifyACLQReq(aclqReq, config));

        //Get GDS response
        final DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        final Node aclqRspNode = PojoXmlUtil.getNodeByTagName(requestGenerator.getSpooferDoc().getFirstChild(), GDSMsgNodeTags.AmadeusNodeTags.ACLQ_LOCATION_LIST_RESPONSE_TYPE);
        final ACLQRsp aclqRsp = new ACLQRsp(aclqRspNode, null, new CarsSCSDataSource(amadeusSCSDatasource));

        //Verify response map
        errorMessage.append(verifyACLQRspMap(requestGenerator.getResponse(), aclqRsp, config.nullIncludeLocation ? false : config.isIncludeLocationDetails()));
        //Verify delivery/collection/outofoffice in response
        errorMessage.append(verifyACLQRsp(aclqReq, aclqRsp));

        if(!StringUtils.isEmpty(errorMessage.toString().trim()))
        {
            Assert.fail(errorMessage.toString());
        }

    }
}
