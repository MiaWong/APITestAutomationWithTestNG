package com.expedia.s3.cars.framework.test.common.splunkaccess;


import com.expedia.s3.cars.framework.test.common.splunkaccess.reporttool.ExcelTool;
import com.expedia.s3.cars.framework.test.common.splunkaccess.reporttool.SplunkDataReportMapping;
import org.eclipse.jetty.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by alex on 3/15/2017.
 */
public class RetriveSplunkData {
  private static SplunkQueryExecutor splunkQE = new SplunkQueryExecutor();
  private static SplunkDataReportMapping splunkDataReportMapping = new SplunkDataReportMapping();

  private RetriveSplunkData()
  {
  }

  /**
   * Get the JSONObject.
   */
  public static List<Map> getSplunkDataFromSplunk(String hostName, int hostPort, String splunkQuery) {

    List<Map> mapList = null;
    try {
      final SplunkQueryRequest splunkRequest = buildQueryRequest(hostName,hostPort,splunkQuery);
      final String response = splunkQE.execute(splunkRequest);
      if (response.length() > 0 && response !=null) {
        final JSONObject jsonObject = new JSONObject(response);
        final JSONArray resultsObject = jsonObject.optJSONArray("results");
        mapList = new ArrayList<>();
        if (splunkQuery.contains("stats") || splunkQuery.contains("chart")) {
          for (int i = 0; i < resultsObject.length(); i++) {
            final JSONObject result = resultsObject.getJSONObject(i);
            final Map resultMap = new HashMap();
            for (final Iterator<String> it = result.keys(); it.hasNext(); ) {
              final String mapKey = it.next();
              resultMap.put(mapKey, result.get(mapKey));
            }
            mapList.add(resultMap);
          }
        } else {
          for (int i = 0; i < resultsObject.length(); i++) {
            final JSONObject result = resultsObject.getJSONObject(i);
            mapList.add(getResultMap(result.opt("_raw").toString()));
          }
        }
      }
    } catch (Exception e) {
    }

    return mapList;
  }

  public static Map<String, List<Map<String, String>>> formatSplunkData(String hostName, int hostPort, List<String> splunkQueryList) {

    final Map<String, List<Map<String, String>>> splunkDataMap = new HashMap<>();
    final Map<String, String> fieldMap = splunkDataReportMapping.fieldMapping();
    for (final String splunkQuery : splunkQueryList) {

      final List<Map<String, String>> mapNewList = new ArrayList<>();
      for (final Map mapItem : getSplunkDataFromSplunk(hostName, hostPort, splunkQuery)) {
        final String eventName = mapItem.get(formatSplunkString(splunkQuery).get("byString")).toString();
        final String newDataString = mapItem.get(formatSplunkString(splunkQuery).get("chartData")).toString();
        final String newData = ExcelTool.formatDouble(Double.parseDouble(newDataString)/1000, 3);
        for (final String foString : fieldMap.keySet()) {
          if (foString.equals(eventName)) {
            final Map newMap = new HashMap<String, String>();
            newMap.put("eventName", fieldMap.get(eventName));
            newMap.put("newData", newData);
            mapNewList.add(newMap);
          }
        }
      }
      splunkDataMap.put(formatSplunkString(splunkQuery).get("chartData"), mapNewList);
    }

    return splunkDataMap;
  }


  private static SplunkQueryRequest buildQueryRequest(String splunkServer, int port,
      String rawQuery) {
    final SplunkQueryRequest request = new SplunkQueryRequest();
    request.setSplunkServer(splunkServer);
    request.setPort(port);
    request.setUsername("s-EpcSplunk");
    request.setPassword("2hEqab+6#2c-sWAd");
    request.setTimeoutInSeconds(60);
    request.setMaxTryCount(3);
    request.setTryIntervalInSeconds(60);
    request.setRawQuery(rawQuery);
    request.setRawQuery(true);

    return request;
  }

  public static Map getResultMap(String resultString) {
    //String resultString = jsonObject.toString();

    final String resultArray[] = resultString.split(" ");
    final int arraySize = resultArray.length;
    final Map resultMap = new HashMap();
    if(resultArray != null && arraySize >0) {
      final String dataTime = resultArray[0].trim() + " " + resultArray[1].trim();
      for (int i = 0; i < arraySize; i++) {
        final String temp = resultArray[i].trim();
        if (temp.contains("=")) {
          final String mapKey = temp.substring(0, temp.indexOf('='));
          final String mapValue = temp.substring(temp.indexOf('=')+1);
          if(resultMap.containsKey(mapKey)) {
            final String newMapValue = resultMap.get(mapKey) + "-" + mapValue;
            resultMap.replace(mapKey, newMapValue);
          }
          resultMap.put(mapKey,mapValue);
        }
        //resolve special value for ErrorText, eg
        else if(!temp.contains("="))
        {
         final String tempValue = (String)resultMap.get("ErrorText");
         if(StringUtil.isNotBlank(tempValue)) {
             final StringBuffer value = new StringBuffer(tempValue);
             value.append(' ').append(temp);
           resultMap.replace("ErrorText", value.toString());
         }
        }
      }
      resultMap.put("DataTime", dataTime);
    }
    return resultMap;
  }

  public static Map<String, String> formatSplunkString(String query){
    // | chart p95(ProcessingTime) by foo
    final String str = query.substring(query.lastIndexOf('|') + 2);
    final Map<String, String> strMap = new HashMap<>();
    strMap.put("chartData", str.substring(str.indexOf(' ') + 1, str.indexOf(' ', str.indexOf(' ')+1)));
    strMap.put("byString", str.substring(str.lastIndexOf(' ') + 1));
    return strMap;
  }

  public static Map<String,String> formatBeta1(Map<String, String> resutMap) {

    return null;
  }

  /*//Create main method for testing
  public static void main(String args[]) {

    ExcelTool et = new ExcelTool();
    Map<String, List<Map<String,String>>> uu= et.readExcelWithTitle("D:\\Alex\\github\\carservice\\cars-framework-test-common\\src\\main\\java\\com\\expedia\\s3\\cars\\framework\\test\\common\\splunkaccess\\reporttool\\reportFile\\Report.xlsx");

    Map<String, List<Map<String, String>>> tt= new HashMap<>();
    List<Map<String, String>> uList = new ArrayList<>();
    Map<String, String> uMap = new HashMap<>();
    uMap.put("eventName", "CarBS Search");
    uMap.put("newData", "3.253");
    Map<String, String> u1Map = new HashMap<>();
    u1Map.put("eventName", "CarSSB Search");
    u1Map.put("newData", "1.253");
    uList.add(uMap);
    uList.add(u1Map);

    List<Map<String, String>> pList = new ArrayList<>();
    Map<String, String> pMap = new HashMap<>();
    pMap.put("eventName", "CarBS Search");
    pMap.put("newData", "3.253");
    Map<String, String> p1Map = new HashMap<>();
    p1Map.put("eventName", "CarSSB Search");
    p1Map.put("newData", "1.253");
    pList.add(pMap);
    pList.add(p1Map);
    tt.put("AvgResponseTime", uList);
    tt.put("P95ReponseTime", pList);
    Map<String, List<Map<String,String>>> result = et.migrateData(uu,tt);
    et.writeExcel("D:\\Alex\\github\\carservice\\cars-framework-test-common\\src\\main\\java" +
            "\\com\\expedia\\s3\\cars\\framework\\test\\common\\splunkaccess\\" +
            "reporttool\\reportFile\\Report.xlsx", result);

    //String hostName = "https://splunk.us-west-2.test.expedia.com";
    //String hostName = "https://splunk.us-east-1.test.expedia.com";
    RetriveSplunkData rs = new RetriveSplunkData();
    final String hostName = "https://splunklab6";
    final int hostPort = 8089;
    String startTime = "2017/03/20 14:50:11";
    String endTime = "2017/03/26 14:50:11";
    Date date1 = null;
    Date date2 = null;
    try {
      date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(startTime);
      date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(endTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    long unixTime1 = date1.getTime()/1000;
    long unixTime2 = date2.getTime()/1000;
    //final String splunkQuery = "index=app car Environment=int LogType=PerfMetrics earliest=-10m";
    String splunkQuery = "index=app host=\"chelcardlsb0*\" LogType=\"PerfMetrics\"  (HostName=\"CHELCARJVASB0*\" OR " +
            "HostName=ip-10-38-82-198 OR HostName=ip-10-38-85-119 OR HostName=ip-10-38-80-221 OR HostName=ip-10-38-80-169) " +
            "earliest=" + unixTime1 + " latest=" +unixTime2+ "| eval " + "foo=ServiceName.\"-\".ActionType | chart p95(ProcessingTime) by foo";


   final List<Map> lHp = rs.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);
    for (Map hm : lHp) {
      System.out.println(hm.toString());
    }
    List<String> sList = new ArrayList<>();
    sList.add(splunkQuery);
    final Map<String, List<Map<String, String>>> uMap = rs.formatSplunkData(hostName, hostPort, sList);
    System.out.println(uMap.size() + uMap.keySet().toString() + uMap.toString());

    String string = "2017_R09 Avg. Response Time- FarmB";
    String s = "% Increase - 2017 R03 vs 2017 R01";
    String  ss = "2017_R09 p95. Response Time- FarmB";
    System.out.println(ExcelTool.combineString(s));
    System.out.println(ExcelTool.combineString(string));
    System.out.println(ExcelTool.combineString(ss));
    String y = ExcelTool.dataCompute("2.0", "1.0");
    System.out.println(y);
    System.out.println(rs.formatSplunkString(splunkQuery));

  }*/
}