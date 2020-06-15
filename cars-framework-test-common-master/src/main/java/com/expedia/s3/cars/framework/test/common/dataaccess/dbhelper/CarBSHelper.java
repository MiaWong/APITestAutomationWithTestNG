package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.BookingRecordLocator;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarBSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarReservationData;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarReservationDataExtended;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.Client;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by asharma1 on 9/26/2016.
 */
public class CarBSHelper
{
    final private DataSource dataSource;

    public CarBSHelper(DataSource datasource)
    {
        this.dataSource = datasource;
    }

    public List<Client> getClientListById (String clientId)  throws DataAccessException {
        final CarBSDataSource carBSDataSource = new CarBSDataSource(dataSource);
        return carBSDataSource.getClientListById(clientId);
    }

    public int getClientIDByCode (String clientCode)  throws DataAccessException {
        final CarBSDataSource carBSDataSource = new CarBSDataSource(dataSource);
        final List<Client> clientList= carBSDataSource.getClientListByCode(clientCode);
        return CollectionUtils.isEmpty(clientList) ?  0 : clientList.get(0).getClientId().intValue();
    }

    public byte[] getCarReservationNodeByBookingItemID (String bookingItemID)  throws DataAccessException {
        final CarBSDataSource carBSDataSource = new CarBSDataSource(dataSource);
        return carBSDataSource.getCarReservationNodeByBookingItemID(bookingItemID);
    }

    public CarReservationData getCarReservationDataByBookingItemID (String bookingItemID)  throws DataAccessException {
        final CarBSDataSource carBSDataSource = new CarBSDataSource(dataSource);
        return carBSDataSource.getCarReservationDataByBookingItemID(bookingItemID);
    }

    public List<CarReservationDataExtended> getCarReservationDataExtendedByBookingItemID (String bookingItemID)  throws DataAccessException {
        final CarBSDataSource carBSDataSource = new CarBSDataSource(dataSource);
        return carBSDataSource.getCarReservationDataExtendedByBookingItemID(bookingItemID);
    }
    public String getBookingItemIDByBookingRecordLocatorByID (long bookingRecordLocatorID)  throws DataAccessException {
        final CarBSDataSource carBSDataSource = new CarBSDataSource(dataSource);
        final List<BookingRecordLocator> bookingRecordLocatorList = carBSDataSource.getBookingRecordLocatorByID(bookingRecordLocatorID);
        return bookingRecordLocatorList.isEmpty() ? null : bookingRecordLocatorList.get(0).getBookingItemID();
    }
}