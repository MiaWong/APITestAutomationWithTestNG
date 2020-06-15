package com.expedia.s3.cars.framework.test.common.splunkaccess.reporttool;

import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by axiang on 3/27/2017.
 */

public class CreateReport {

    Logger logger = Logger.getLogger(getClass());
    ExcelTool et = new ExcelTool();

    public void createExcelReport(String filePath, String hostName, int hostPort, List<String> query) {

        final Map<String, List<Map<String, String>>> excelData= et.readExcelWithTitle(filePath);
        final Map<String, List<Map<String, String>>> splunkData = RetriveSplunkData.formatSplunkData(hostName, hostPort, query);
        et.writeExcel(filePath, et.migrateData(excelData, splunkData));
    }

    public long dateTimeFormatToUnixTime(String time) {
        long unixTime = 0;
        try {
            final Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).parse(time);
            unixTime = date.getTime()/1000;
        } catch (ParseException e) {
            logger.info(e);
        }
        return unixTime;
    }

    /*
    //blow is the guide for using this method , also is a test case
    public static void main(String[] args) {

        CreateReport cr = new CreateReport();
        // The time in query must be uninxTime. So we need to transform it to be uninxTime
        final String startTime = "2017/06/07 14:50:11";
        final String endTime = "2017/06/07 17:50:11";
        long unixTime1 = cr.dateTimeFormatToUnixTime(startTime);
        long unixTime2 = cr.dateTimeFormatToUnixTime(endTime);
        final String splunkQuery1 = "index=app host=\"chelcardlsb0*\" LogType=\"PerfMetrics\"  (HostName=\"CHELCARJVASB0*\" OR " +
                "HostName=ip-10-38-82-198 OR HostName=ip-10-38-85-119 OR HostName=ip-10-38-80-221 OR HostName=ip-10-38-80-169) " +
                "earliest=" + unixTime1 + " latest=" +unixTime2+ "| eval " + "foo=ServiceName.\"-\".ActionType | chart avg(ProcessingTime) by foo";
        final String splunkQuery2 = "index=app host=\"chelcardlsb0*\" LogType=\"PerfMetrics\" (HostName=\"CHELCARJVASB0*\" OR " +
                "HostName=ip-10-38-82-198 OR HostName=ip-10-38-85-119 OR HostName=ip-10-38-80-221 OR HostName=ip-10-38-80-169)" +
                "earliest=" + unixTime1 + " latest=" +unixTime2+ "| eval foo=ServiceName.\"-\".ActionType | chart p95(ProcessingTime) by foo";
        final List<String> sList = new ArrayList<>();
        sList.add(splunkQuery1);
        sList.add(splunkQuery2);

        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        //filePath is the report file location. You should create the last week data first. The report.xlsx is an example, you should follow the format.
        final String filePath = "D:\\Automation\\Report.xlsx";

        cr.createExcelReport(filePath, hostName, hostPort, sList);
    }
    */
}
