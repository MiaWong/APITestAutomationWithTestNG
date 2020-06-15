package com.expedia.s3.cars.framework.test.common.splunkaccess.reporttool;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.*;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by axiang on 3/22/2017.
 */
@SuppressWarnings("PMD")
public class ExcelTool {

    Logger logger = Logger.getLogger(getClass());

    //Read Excle
    public Map<String, List<Map<String, String>>> readExcelWithTitle(String filepath) {

        final String fileType = filepath.substring(filepath.lastIndexOf('.') + 1, filepath.length());
        Map<String, List<Map<String, String>>> result = null;

        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = new FileInputStream(filepath);
            if ("xls".equals(fileType)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if ("xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                logger.info("This is not an exlce file!");
            }
            result = new HashMap<String, List<Map<String,String>>>();
            final int sheetSize = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetSize; i++) {
                final Sheet sheet = workbook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                final List<Map<String, String>> sheetDataList = new ArrayList<Map<String, String>>();

                final int rowSize = sheet.getLastRowNum() + 1;
                for (int j = 0; j < rowSize; j++) {
                    final Map<String,String> rowMap = new HashMap<String,String>();//store the row value
                    final Row row = sheet.getRow(j);
                    //skip the empty line
                    if (row == null) {
                        continue;
                    }
                    final int cellSize = row.getLastCellNum();
                    for (int k = 0; k < cellSize; k++) {
                        final Cell cell = row.getCell(k);
                        if( k == 0) {
                            rowMap.put("eventName", cell.toString());
                        } else if (k == 1) {
                            rowMap.put("oldData", cell.toString());
                        } else if (k == 2) {
                            rowMap.put("newData", cell.toString());
                        } else if (k == 3) {
                            rowMap.put("increase", cell.toString());
                        } else {
                            // do nothing
                        }
                    }
                    sheetDataList.add(rowMap);
                }
                result.put(sheetName,sheetDataList);
            }
        } catch (Exception e) {
            logger.info(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.info(e);
                }
            }
        }
        return result;
    }

    public void writeExcel(String excelExtName, Map<String, List<Map<String, String>>> data) {
        final File oldFile = new File(excelExtName);
        if (oldFile.exists()) {
            oldFile.delete();
        }
        Workbook wb = null;
        FileOutputStream os= null;
        try {
            if (excelExtName.endsWith(".xls")) {
                wb = new HSSFWorkbook();
            } else if (excelExtName.endsWith("xlsx")) {
                wb = new XSSFWorkbook();
            } else {
                logger.info("This is not an excel file");
            }
            if(!data.isEmpty() && data.size() > 1){
                os = new FileOutputStream(excelExtName);
                for (final String sheetName : data.keySet()) {
                    final List<Map<String, String>> rowList = data.get(sheetName);
                    final Sheet sheet = wb.createSheet(sheetName);
                    for (int i = 0; i < rowList.size(); i++) {
                        final Map cellMap = rowList.get(i);
                        final Row row = sheet.createRow(i);
                        for (int j = 0; j < cellMap.size(); j++) {
                            final Cell cell = row.createCell(j);
                            if(j == 0){
                                cell.setCellValue(cellMap.get("eventName").toString());
                            } else if (j == 1) {
                                cell.setCellValue(cellMap.get("oldData").toString());
                            } else if (j == 2) {
                                cell.setCellValue(cellMap.get("newData").toString());
                            } else if (j == 3) {
                                cell.setCellValue(cellMap.get("increase").toString());
                            } else {
                                // set this is option to add other item
                            }
                        }
                    }
                }
            }
            wb.write(os);
        } catch (Exception e) {
            logger.info(e);
        } finally {
            if (os != null) {
                try{
                    os.close();
                } catch(Exception e){
                    logger.info(e);
                }
            }
        }
    }

    public Map<String, List<Map<String, String>>> migrateData(Map<String, List<Map<String, String>>> excleData, Map<String,List<Map<String, String>>> splunkData){
        if(!excleData.isEmpty() && !splunkData.isEmpty()) {
            //sheet list
            for(final String excleKeyString : excleData.keySet()){
                for (final String splunkDataKeyString : splunkData.keySet()) {
                    if (excleKeyString.equals(splunkDataKeyString)) {
                        //table in sheet
                        final List<Map<String, String>> migrateMapList = migrateMap(excleData.get(excleKeyString),splunkData.get(splunkDataKeyString));
                        excleData.put(excleKeyString,migrateMapList);
                    }
                }
            }
        }
        return excleData;
    }

    public List<Map<String, String>> migrateMap(List<Map<String, String>> excleMap, List<Map<String, String>> splunkMap) {

        final int mapSize = excleMap.get(0).size();
        for (final Map<String,String> excleMapItem : excleMap) {
            final String oldData = excleMapItem.get("newData");
            excleMapItem.put("oldData", oldData);
            for (final Map<String, String> splunkMapItem : splunkMap) {
                if (excleMapItem.get("eventName").equals(splunkMapItem.get("eventName"))) {
                    final String newData = splunkMapItem.get("newData");
                    excleMapItem.put("newData", newData);
                    if (mapSize > 3) {
                        final String increase = dataCompute(oldData,newData);
                        excleMapItem.put("increase", increase);
                    }
                }
            }

        }

        final List<String> excleEventList = new ArrayList<String>();
        final List<String> splunkEventList = new ArrayList<String>();
        for (final Map<String,String> excleMapItem : excleMap) {
            final String eventString = excleMapItem.get("eventName");
            excleEventList.add(eventString);
        }
        for (final Map<String, String> splunkMapItem : splunkMap) {
            final String eventString = splunkMapItem.get("eventName");
            splunkEventList.add(eventString);
        }

        for (final Map<String, String> excleMapItem : excleMap) {
            final String excleString = excleMapItem.get("eventName");
            final boolean ifContrain = !splunkEventList.contains(excleString) && !"Name".equals(excleString);
            if (ifContrain){
                excleMapItem.put("newData", "");
                if (mapSize > 3) {
                    excleMapItem.put("increase", "");
                }
            } else if ("Name".equals(excleString)) {
                final String newData = combineString(excleMapItem.get("oldData"));
                excleMapItem.put("newData", newData);
                if (mapSize > 3) {
                    final String increase = combineString(excleMapItem.get("increase"));
                    excleMapItem.put("increase", increase);
                }
            }
        }

        for (final Map<String, String> splunkMapItem : splunkMap) {
            final String eventString = splunkMapItem.get("eventName");
            final boolean ifContain = !excleEventList.contains(eventString);
            if (ifContain){
                final Map<String,String> newMap = new HashMap<String,String>();
                newMap.put("eventName", eventString);
                newMap.put("oldData", "");
                newMap.put("newData", splunkMapItem.get("newData"));
                if (mapSize > 3) {
                    newMap.put("increase", "");
                }
                excleMap.add(newMap);
            }
        }

        return excleMap;
    }

    public static String dataCompute(String oldData, String newData) {
        final double newDataDouble = Double.valueOf(newData);
        final double oldDataDouble = Double.valueOf(oldData);
        String increaseString = null;
        if ( newDataDouble > oldDataDouble) {
            final double increase = (newDataDouble - oldDataDouble)/newDataDouble;
            increaseString = "-" + formatDouble(increase*100, 2) + "%";
        } else {
            final double increase = (oldDataDouble - newDataDouble)/newDataDouble;
            increaseString = "+" + formatDouble(increase*100, 2) + "%";
        }
        return increaseString;
    }

    public static String formatDouble(double d, int num) {
        final NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(num);
        nf.setRoundingMode(RoundingMode.UP);
        return nf.format(d);
    }

    public static String combineString(String string) {

        String mapKeyString = null;
        if (string.contains("Increase")) {

            final String front = string.substring(0, string.indexOf('R') + 1);
            final String stringKey = string.substring(string.indexOf('R') + 1, string.indexOf('v') - 1);
            final String foot = string.substring(string.indexOf('v') - 1, string.lastIndexOf('R') + 1);

            final int str = Integer.valueOf(stringKey) + 1;
            if (str < 10) {
                mapKeyString = front + "0" + str + foot + "0" + (str-1);
            } else if (str == 10) {
                mapKeyString = front + str + foot + "0" + (str-1);
            } else{
                mapKeyString = String.valueOf(Integer.valueOf(stringKey) + 1);
            }
        } else {
            final String front = string.substring(0, string.indexOf('R') + 1);
            final String foot = string.substring(string.indexOf(' '));
            final String stringKey = string.substring(string.indexOf('R') + 1, string.indexOf(' '));
            final int str = Integer.valueOf(stringKey) + 1;
            if (str < 9) {
                mapKeyString = front + "0" + str + foot;
            } else {
                mapKeyString = front + str + foot;
            }
        }
        return mapKeyString;
    }

}
