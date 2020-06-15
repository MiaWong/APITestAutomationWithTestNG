package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asharma1 on 10/3/2016.
 */
@SuppressWarnings("PMD")
public class CarBSDataSource {
    final private DataSource dataSource;

    public CarBSDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Client> getClientListById (String clientId)  throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append(" select clientID,clientName,ClientCode from client where clientID = :clientId  ");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientId",clientId);
        final ParametrizedQuery<Client> tsql = new ParametrizedQuery<Client>(sqlQuery.toString(), dataSource, Client.class);
        return tsql.execute(paramMap);
    }

    public List<Client> getClientListByCode (String clientCode)  throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append(" select clientID,clientName,ClientCode from client where ClientCode = :clientCode  ");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientCode",clientCode);
        final ParametrizedQuery<Client> tsql = new ParametrizedQuery<Client>(sqlQuery.toString(), dataSource, Client.class);
        return tsql.execute(paramMap);
    }

    public List<BookingRecordLocator> getBookingRecordLocatorByID (long bookingRecordLocatorID)  throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append(" select * from BookingRecordLocator where BookingRecordLocatorID = :bookingRecordLocatorID ");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("bookingRecordLocatorID",bookingRecordLocatorID);
        final ParametrizedQuery<BookingRecordLocator> tsql = new ParametrizedQuery<BookingRecordLocator>(sqlQuery.toString(),
                dataSource, BookingRecordLocator.class);
        return tsql.execute(paramMap);
    }

    public byte[] getCarReservationNodeByBookingItemID (String bookingItemID)  throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append(" select CarReservationNodeData from CarReservationData where bookingitemid = :bookingItemID  ");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("bookingItemID",bookingItemID);
        final ParametrizedQuery<CarReservationData> tsql = new ParametrizedQuery<CarReservationData>(sqlQuery.toString(), dataSource, CarReservationData.class);
        return tsql.execute(paramMap).get(0).getCarReservationNodeData();
    }

    public CarReservationData getCarReservationDataByBookingItemID (String bookingItemID)  throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append(" select * from CarReservationData where bookingitemid = :bookingItemID  ");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("bookingItemID",bookingItemID);
        final ParametrizedQuery<CarReservationData> tsql = new ParametrizedQuery<CarReservationData>(sqlQuery.toString(), dataSource, CarReservationData.class);
        return tsql.execute(paramMap).isEmpty() ? null: tsql.execute(paramMap).get(0);
    }

    public List<CarReservationDataExtended> getCarReservationDataExtendedByBookingItemID (String bookingItemID)  throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer( );
        sqlQuery.append(" select * from CarReservationDataExtended where BookingItemID =:bookingItemID  ");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("bookingItemID",bookingItemID);
        final ParametrizedQuery<CarReservationDataExtended> tsql = new ParametrizedQuery<CarReservationDataExtended>(sqlQuery.toString(), dataSource, CarReservationDataExtended.class);
        return tsql.execute(paramMap).isEmpty() ? null: tsql.execute(paramMap);
    }

}