package com.expedia.s3.cars.ecommerce.carbs.service.database;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 11/15/2016.
 */
public class CarbsDB {

    final Logger logger = Logger.getLogger(getClass());
    final private  DataSource dataSource;

    public CarbsDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public  int getCountByBookingRecordLocatorID(long bookingRecorLocatorID) throws DataAccessException {
        final Map<String, Object> paramMap = new HashMap<>();
        final ParametrizedQuery<Object> tsql = new ParametrizedQuery<>("SELECT BookingRecordLocatorID\n" +
                "  FROM BookingRecordLocator where BookingRecordLocatorID= :bookingRecorLocatorID" , dataSource, Object.class);
        paramMap.put("bookingRecorLocatorID", bookingRecorLocatorID);
        final List<Object> recordLocatorIds=  tsql.execute(paramMap);
        return recordLocatorIds.size();

    }

    public int getValueByName(String settingName) throws DataAccessException {
        final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());
        return clientConfigHelper.getValueFromCarBSConfig(null, settingName, null);
    }
}