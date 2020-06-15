package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 6/20/2017.
 */
public class CarBookingDatasource {
    public final Logger log = Logger.getLogger(CarBookingDatasource.class);
    public DataSource dataSource;

    public CarBookingDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BookingItemCar getBookingItemCarByBookingItemID(int bookingItemID) throws DataAccessException {
        final ParametrizedQuery<BookingItemCar> tsql = new ParametrizedQuery<>("select * from BookingItemCar where BookingItemID =:pBookingItemID", dataSource, BookingItemCar.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pBookingItemID", bookingItemID);
        final List<BookingItemCar> bookingItemCars = tsql.execute(paramMap);
        if (CollectionUtils.isEmpty(bookingItemCars))
        {
            log.error("bookingItemCars is empty for getBookingItemCarByBookingItemID method");
            return null;
        }
        return bookingItemCars.get(0);
    }

    public BookingItem getBookingItemByBookingItemID(int bookingItemID) throws DataAccessException {
        final ParametrizedQuery<BookingItem> tsql = new ParametrizedQuery<>("select * from BookingItem where BookingItemID =:pBookingItemID", dataSource, BookingItem.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pBookingItemID", bookingItemID);
        final List<BookingItem> bookingItems = tsql.execute(paramMap);
        if (CollectionUtils.isEmpty(bookingItems))
        {
            log.error("bookingItems is empty for getBookingItemCarByBookingItemID method");
            return null;
        }
        return bookingItems.get(0);
    }

    public Booking getBookingByBookingID(int bookingD) throws DataAccessException {
        final ParametrizedQuery<Booking> tsql = new ParametrizedQuery<>("select * from Booking where BookingID =:pBookingID", dataSource, Booking.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pBookingID", bookingD);
        final List<Booking> bookings = tsql.execute(paramMap);
        if (CollectionUtils.isEmpty(bookings))
        {
            log.error("bookings is empty for getBookingByBookingID method");
            return null;
        }
        return bookings.get(0);
    }

    public List<BookingAmount> getBookingAmountListByBookingItemID(String bookingItemID) throws DataAccessException {
        final ParametrizedQuery<BookingAmount> tsql = new ParametrizedQuery<>("select * from BookingAmount where BookingItemID =:pBookingItemID", dataSource, BookingAmount.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pBookingItemID", bookingItemID);
        final List<BookingAmount> amountRows = tsql.execute(paramMap);
        if (CollectionUtils.isEmpty(amountRows))
        {
            log.error("BookingAmount is empty for getBookingAmountByBookingItemID method");
        }
        return amountRows;
    }

    public List<BookingAmount> getBookingAmountListByBookingItemID(String bookingItemID, boolean cancelBoolean) throws DataAccessException {
        final ParametrizedQuery<BookingAmount> tsql = new ParametrizedQuery<>("select * from BookingAmount where BookingItemID =:pBookingItemID and CancelBool =:pCancelBoolean", dataSource, BookingAmount.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pBookingItemID", bookingItemID);
        paramMap.put("pCancelBoolean", cancelBoolean ? 1 : 0);
        final List<BookingAmount> amountRows = tsql.execute(paramMap);
        if (CollectionUtils.isEmpty(amountRows))
        {
            log.error("BookingAmount is empty for getBookingAmountByBookingItemID method");
        }
        return amountRows;
    }

}
