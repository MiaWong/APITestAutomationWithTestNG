package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarReservationData;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.junit.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/16/2018.
 */
public class VerifyCarReservationData {

    private  VerifyCarReservationData(){}

    public static void verifyCarReservationData(PreparePurchaseRequestType request, PreparePurchaseResponseType response) throws DataAccessException, IOException {
        //Get expected CarReservationData
        final CarReservationData expCarReservationData = getExpCarReservationData(request, response);
        final String bookingItemId = BookingVerificationUtils.getBookingItemID(response);
        expCarReservationData.setBookingItemID(bookingItemId);
        //Get actual CarReservationData
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final CarReservationData actCarReservationData = carBSHelper.getCarReservationDataByBookingItemID(bookingItemId);
        //Compare CarReservationData except carReservationNodeData
        final List<String> ignoreList = new ArrayList<>();
        ignoreList.add(CarReservationDataObjectTag.CREATEDATE);
        ignoreList.add(CarReservationDataObjectTag.CARRESERVATIONNODEDATA);
        final StringBuilder erroMsg = new StringBuilder();
        final boolean compared = CompareUtil.compareObject(expCarReservationData, actCarReservationData, ignoreList, erroMsg);
        if(!compared){
            Assert.fail(erroMsg.toString());
        }
        //Compare carReservationNodeData
        final CarReservationType expCarReservation = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation();
        final boolean carReservationCompared = CompareUtil.compareObject(expCarReservation,deserializeReservation(actCarReservationData.getCarReservationNodeData()),
                new ArrayList<>(), erroMsg);
        if(!carReservationCompared){
            Assert.fail(erroMsg.toString());
        }


    }

    public static CarReservationData getExpCarReservationData(PreparePurchaseRequestType request, PreparePurchaseResponseType response)
    {
        final CarReservationData carReservationData= new CarReservationData();
        //Get POS values
        carReservationData.setJurisdictionCode(request.getSiteMessageInfo().getPointOfSaleKey().getJurisdictionCountryCode());
        carReservationData.setCompanyCode(request.getSiteMessageInfo().getPointOfSaleKey().getCompanyCode());
        carReservationData.setManagementUnitCode(request.getSiteMessageInfo().getPointOfSaleKey().getManagementUnitCode());

        //Get message version
        carReservationData.setCarReservationNodeMajorVersion(request.getMessageInfo().getMessageVersion().split("\\.")[0]);
        carReservationData.setCarReservationNodeMinorVersion(request.getMessageInfo().getMessageVersion().split("\\.")[1]);

        //Get CarReservation data
        final CarReservationType rspCarReservation = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation();

        //CarReservationDataExtendedElementCnt - Cost count + Price count
        carReservationData.setCarReservationDataExtendedElementCnt(rspCarReservation.getCarProduct().getCostList().getCost().size()
                + rspCarReservation.getCarProduct().getPriceList().getPrice().size());
        carReservationData.setCarReservationDataExtendedPriceListCnt(rspCarReservation.getCarProduct().getPriceList().getPrice().size());

        //Use date end
        carReservationData.setUseDateEnd(new Timestamp(rspCarReservation.getCarProduct().getCarInventoryKey().getCarDropOffDateTime().toCalendar().getTimeInMillis()));
        return carReservationData;
    }

    public static CarReservationType deserializeReservation(byte[] data) throws IOException
    {
        CarReservationType reservation = null;

        if (null != data)
        {
            try
            {
                final InputStream stream = new ByteArrayInputStream(data);
                final XMLStreamReader streamReader = new StAXDocumentParser(stream);
                final JAXBContext jaxbContext = JAXBContext.newInstance("expedia.e3.data.cartypes.defn.v5");
                final Unmarshaller u = jaxbContext.createUnmarshaller();
                final JAXBElement<CarReservationType> output = u.unmarshal(streamReader, CarReservationType.class);
                reservation = output.getValue();
            }
            catch (Exception e)
            {
                throw new IOException("Unable to deserialize CarReservation: " + e.getMessage(), e);
            }
        }

        return reservation;
    }
}
