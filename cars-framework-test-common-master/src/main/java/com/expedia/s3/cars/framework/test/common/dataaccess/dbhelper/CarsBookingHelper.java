package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.*;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by v-mechen on 9/5/2018.
 */
public class CarsBookingHelper {
    final private DataSource dataSource;
    public CarsBookingHelper(DataSource datasource)
    {
        this.dataSource = datasource;
    }

    public List<BookingAmount> getBookingAmountList(String bookingItemID) throws DataAccessException {
        final CarBookingDatasource datasource = new CarBookingDatasource(dataSource);
        return datasource.getBookingAmountListByBookingItemID(bookingItemID);
    }

    public List<BookingAmount> getBookingAmountList(String bookingItemID, boolean cancelBoolean) throws DataAccessException {
        final CarBookingDatasource datasource = new CarBookingDatasource(dataSource);
        return datasource.getBookingAmountListByBookingItemID(bookingItemID, cancelBoolean);
    }

    public BookingItemCar getBookingItemCar(String bookingItemID) throws DataAccessException {
        final CarBookingDatasource datasource = new CarBookingDatasource(dataSource);
        return datasource.getBookingItemCarByBookingItemID(Integer.parseInt(bookingItemID));
    }

    public BookingItem getBookingItem(String bookingItemID) throws DataAccessException {
        final CarBookingDatasource datasource = new CarBookingDatasource(dataSource);
        return datasource.getBookingItemByBookingItemID(Integer.parseInt(bookingItemID));
    }

    public Booking getBooking(String bookingID) throws DataAccessException {
        final CarBookingDatasource datasource = new CarBookingDatasource(dataSource);
        return datasource.getBookingByBookingID(Integer.parseInt(bookingID));
    }
}
