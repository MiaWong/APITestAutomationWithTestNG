package com.expedia.s3.cars.framework.test.common.dataaccess;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

/**
 * Created by asharma1 on 10/7/2016.
 */
public class DatabaseSetting
{
    private static final String DRIVER_NAME = "net.sourceforge.jtds.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:jtds:sqlserver://";
    private static final String SLASH = "/";
    private static final String OPTION_NTLM = ";useNTLMv2=true";
    private static final String OPTION_DOMAIN = ";domain=";
    private static final String OPTION_USER = ";user=";
    private static final String OPTION_PASSWORD = ";password=";

    private DatabaseSetting()
    {
    }

    public static DataSource createDataSource(String dbServer, String dbName, String userDomain, String userName, String password, boolean usePassword)
    {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setMaxActive(100);
        dataSource.setDriverClassName(DRIVER_NAME);
        final StringBuffer connectionStringBuffer = new StringBuffer(JDBC_URL);
        connectionStringBuffer.append(dbServer).append(SLASH).append(dbName);
        if(usePassword)
        {
            connectionStringBuffer.append(OPTION_NTLM).append(OPTION_DOMAIN).append(StringUtils.isBlank(userDomain) ? "" : userDomain).append(OPTION_USER).append(userName).append(OPTION_PASSWORD).append(password);
        }
        dataSource.setUrl(connectionStringBuffer.toString());
        return dataSource;
    }
}