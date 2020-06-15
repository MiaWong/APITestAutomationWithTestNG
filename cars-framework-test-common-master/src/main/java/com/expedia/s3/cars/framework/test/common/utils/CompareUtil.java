package com.expedia.s3.cars.framework.test.common.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by yyang4 on 12/6/2016.
 */
@SuppressWarnings("PMD")
public class CompareUtil {
    private CompareUtil(){}
    private static final List<String> baseType = new ArrayList<String>(Arrays.asList("long", "int", "float", "double", "boolean", "byte", "char", "shot"));

    public static boolean compareObjectOnlyForNeedField(Object expect, Object actual, List<String> needFieldList, StringBuilder errorMsg) {
       return doCompare(expect,actual,null,needFieldList,errorMsg);
    }
    public static boolean compareObject(Object expect, Object actual, List<String> ignoreList, StringBuilder errorMsg) {
        return doCompare(expect,actual,ignoreList,null,errorMsg);
    }

    public static boolean compareObject(Object expect, Object actual, List<String> ignoreList, StringBuilder errorMsg, boolean ignoreCase) {
        return doCompare(expect,actual,ignoreList,null,errorMsg, ignoreCase);
    }

    @SuppressWarnings("CPD-START")
    private static boolean doCompare(Object expect, Object actual, List<String> ignoreList,List<String> needFieldList, StringBuilder errorMsg){
        boolean result = true;
        if (isObjEmpty(ignoreList)) {
            ignoreList = new ArrayList<>(Arrays.asList("serialVersionUID"));
        }else if(!ignoreList.contains("serialVersionUID")) {
            ignoreList.add("serialVersionUID");
        }
        if(isObjEmpty(needFieldList)){
            needFieldList = new ArrayList<String>();
        }
        if(null == errorMsg){
            errorMsg = new StringBuilder();
        }
        if ((isObjEmpty(expect) && !isObjEmpty(actual)) || (!isObjEmpty(expect) && isObjEmpty(actual))) {
            errorMsg.append(String.format("The actual = %s is not equal the expect = %s .\r\n",actual,expect));
            result = false;
        }else if(!isObjEmpty(expect) && !isObjEmpty(actual)){
            final String objTypeName = expect.getClass().getTypeName();
            if(!objTypeName.equals(actual.getClass().getTypeName())){
                result = false;
            }else if(objTypeName.toLowerCase().contains("java") && objTypeName.toLowerCase().contains("list")){
                final List objList1 = (List)expect;
                final List objList2 = (List)actual;
                if (objList1.size() != objList2.size()) {
                    errorMsg.append(String.format("The actual list size = %s is not equal the expect list size = %s .\r\n",objList2.size(),objList1.size()));
                    result = false;
                }
                for (int i = 0; i < objList1.size(); i++) {
                    boolean isMatch = false;
                    for(int j=0; j < objList2.size(); j++){
                        if (doCompare(objList1.get(i), objList2.get(j), ignoreList,needFieldList,errorMsg)) {
                            isMatch = true;
                            break;
                        }
                    }
                    if (!isMatch) {
                        errorMsg.append("The actual list is not match the expect list .\r\n");
                        result = false;
                        break;
                    }
                }
            }
            else if(objTypeName.toString().equalsIgnoreCase("com.expedia.e3.data.basetypes.defn.v4.AmountType"))
            {
                final Field[] fieldsList1 = expect.getClass().getDeclaredFields();
                try
                {
                    if (!isObjEmpty(fieldsList1))
                    {
                        long decimalPlaceCountExpect = 0;
                        long decimalPlaceCountActual = 0;

                        int amountExpect = 0;
                        int amountActual = 0;

                        for (final Field field1 : fieldsList1)
                        {
                            if ((isObjEmpty(needFieldList) && !ignoreList.contains(field1.getName())) ||
                                    (!isObjEmpty(needFieldList) && needFieldList.contains(field1.getName()) && !ignoreList.contains(field1.getName())))

                            {
                                PropertyDescriptor pd = null;
                                Method getMethod = null;
                                Object value1 = null;
                                Object value2 = null;
                                try
                                {
                                    pd = new PropertyDescriptor(field1.getName(), expect.getClass());
                                    getMethod = pd.getReadMethod();
                                    value1 = getMethod.invoke(expect);
                                    value2 = getMethod.invoke(actual);
                                } catch (IntrospectionException e)
                                {
                                    continue;
                                }
                                if(field1.getName().toLowerCase().contains("decimalplacecount"))
                                {
                                    decimalPlaceCountExpect = (long)value1;
                                    decimalPlaceCountActual = (long)value2;
                                }
                                else if(field1.getName().toLowerCase().contains("decimal"))
                                {
                                    amountExpect = (int)value1;
                                    amountActual = (int)value2;
                                }
                            }
                        }

                        double expAmount = (double)amountExpect / (double)Math.pow(10, decimalPlaceCountExpect);
                        double actAmount = (double)amountActual / (double)Math.pow(10, decimalPlaceCountActual);

                        if (!(Math.abs(expAmount - actAmount) <= 0.01 )) {

                            errorMsg.append("The actual ").append(objTypeName).append(String.format(" = %s is not equal the expect = %s .\r\n", actAmount, expAmount));
                            result = false;
                        }
                    } else
                    {
                        result = false;
                    }
                } catch (Exception e)
                {
                    errorMsg.append(e.getStackTrace());
                    result = false;
                }
            }
            else if(!baseType.contains(objTypeName) && !objTypeName.toLowerCase().contains("java") && !objTypeName.toLowerCase().contains("datetime")){
                final Field[] fieldsList1 = expect.getClass().getDeclaredFields();
                try {
                    if (!isObjEmpty(fieldsList1)) {
                        for (final Field field1 : fieldsList1) {
                            if ((isObjEmpty(needFieldList) && !ignoreList.contains(field1.getName())) || (!isObjEmpty(needFieldList) && needFieldList.contains(field1.getName()) && !ignoreList.contains(field1.getName()))) {
                                PropertyDescriptor pd = null;
                                Method getMethod = null;
                                Object value1 = null;
                                Object value2 = null;
                                try {
                                    pd = new PropertyDescriptor(field1.getName(), expect.getClass());
                                    getMethod = pd.getReadMethod();
                                    value1 = getMethod.invoke(expect);
                                    value2 = getMethod.invoke(actual);
                                } catch (IntrospectionException e) {
                                    continue;
                                }
                                if(!doCompare(value1,value2,ignoreList,needFieldList,errorMsg)){
                                    errorMsg.append("The actual ").append(field1.getName()).append(String.format(" = %s is not equal the expect = %s .\r\n",value2,value1));
                                    result = false;
                                    break;
                                }
                            }
                        }
                    } else {
                        result = false;
                    }
                } catch (Exception e) {
                    errorMsg.append(e.getStackTrace());
                    result = false;
                }
            }else{
                if(expect instanceof String && actual instanceof String){
                    expect = ((String) expect).trim().replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\r\\n", "").replaceAll(" ", "");
                    actual = ((String) actual).trim().replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\r\\n", "").replaceAll(" ", "");
                }
                if(!expect.equals(actual)) {
                    errorMsg.append(String.format("The actual = %s is not equal the expect = %s .\r\n", actual, expect));
                    result = false;

                }
            }
        }
        if(result){
            errorMsg.setLength(0);
        }
        return result;
    }

    private static boolean doCompare(Object expect, Object actual, List<String> ignoreList,List<String> needFieldList, StringBuilder errorMsg, boolean ignoreCase){
        boolean result = true;
        if (isObjEmpty(ignoreList)) {
            ignoreList = new ArrayList<>(Arrays.asList("serialVersionUID"));
        }else if(!ignoreList.contains("serialVersionUID")) {
            ignoreList.add("serialVersionUID");
        }
        if(isObjEmpty(needFieldList)){
            needFieldList = new ArrayList<>();
        }
        if(null == errorMsg){
            errorMsg = new StringBuilder();
        }
        if ((isObjEmpty(expect) && !isObjEmpty(actual)) || (!isObjEmpty(expect) && isObjEmpty(actual))) {
            errorMsg.append(String.format("The actual = %s is not equal the expect = %s .\r\n",actual,expect));
            result = false;
        }else if(!isObjEmpty(expect) && !isObjEmpty(actual)){
            final String objTypeName = expect.getClass().getTypeName();
            if(!objTypeName.equals(actual.getClass().getTypeName())){
                result = false;
            }
            else if(objTypeName.toLowerCase().contains("java") && objTypeName.toLowerCase().contains("list")){
                final List objList1 = (List)expect;
                final List objList2 = (List)actual;
                if (objList1.size() != objList2.size()) {
                    errorMsg.append(String.format("The actual list size = %s is not equal the expect list size = %s .\r\n",objList2.size(),objList1.size()));
                    result = false;
                }
                for (int i = 0; i < objList1.size(); i++) {
                    boolean isMatch = false;
                    for(int j=0; j < objList2.size(); j++){
                        if (doCompare(objList1.get(i), objList2.get(j), ignoreList,needFieldList,errorMsg, ignoreCase)) {
                            isMatch = true;
                            break;
                        }
                    }
                    if (!isMatch) {
                        errorMsg.append("The actual list is not match the expect list .\r\n");
                        result = false;
                        break;
                    }
                }
            }
            else if(!baseType.contains(objTypeName) && !objTypeName.toLowerCase().contains("java") && !objTypeName.toLowerCase().contains("datetime")){
                final Field[] fieldsList1 = expect.getClass().getDeclaredFields();
                try {
                    if (!isObjEmpty(fieldsList1)) {
                        for (final Field field1 : fieldsList1) {
                            if ((isObjEmpty(needFieldList) && !ignoreList.contains(field1.getName())) || (!isObjEmpty(needFieldList) && needFieldList.contains(field1.getName()) && !ignoreList.contains(field1.getName()))) {
                                PropertyDescriptor pd = null;
                                Method getMethod = null;
                                Object value1 = null;
                                Object value2 = null;
                                try {
                                    pd = new PropertyDescriptor(field1.getName(), expect.getClass());
                                    getMethod = pd.getReadMethod();
                                    value1 = getMethod.invoke(expect);
                                    value2 = getMethod.invoke(actual);
                                } catch (IntrospectionException e) {
                                    continue;
                                }
                                if(!doCompare(value1,value2,ignoreList,needFieldList,errorMsg, ignoreCase)){
                                    errorMsg.append("The actual ").append(field1.getName()).append(String.format(" = %s is not equal the expect = %s .\r\n",value2,value1));
                                    result = false;
                                    break;
                                }
                            }
                        }
                    } else {
                        result = false;
                    }
                } catch (Exception e) {
                    errorMsg.append(e.getStackTrace());
                    result = false;
                }
            }
            else{
                if(expect instanceof String && actual instanceof String){
                    expect = ((String) expect).trim().replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\r\\n", "").replaceAll(" ", "");
                    actual = ((String) actual).trim().replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\r\\n", "").replaceAll(" ", "");

                    if(ignoreCase) {
                        if(!((String) expect).equalsIgnoreCase(((String) actual))) {
                            errorMsg.append(String.format("The actual = %s is not equal the expect = %s .\r\n", actual, expect));
                            result = false;
                        }

                    }
                }

                else if(!expect.equals(actual)) {
                    errorMsg.append(String.format("The actual = %s is not equal the expect = %s .\r\n", actual, expect));
                    result = false;

                }
            }
        }
        if(result){
            errorMsg.setLength(0);
        }
        return result;
    }
    @SuppressWarnings("CPD-END")
    public static boolean isObjEmpty(Object obj){
        if(obj == null || "".equals(obj) || "".equals(obj.toString().trim()) || "null".equals(obj)){
            return true;
        }
        if(obj instanceof StringBuilder){
            return ((StringBuilder)obj).length() == 0;
        }
        if(obj instanceof StringBuffer){
            return ((StringBuffer)obj).length() == 0;
        }
        if(obj instanceof Collection){
            return CollectionUtils.isEmpty((Collection) obj);
        }
        if(obj instanceof Object[]){
            return ((Object[]) obj).length == 0;
        }
        return false;
    }

    public static boolean isDouble(String obj){
        try {
            Double.parseDouble(obj);
            return true;
        }catch (Exception e){
            return false;
        }
    }



    public static String compareSplunkMap(Map<String, String> expectedDic, Map<String, String> actualDic)
    {
        //Print the actual values
        System.out.println("--------------------Actual Values------------------------");
        for (final Map.Entry<String, String> actualPair : actualDic.entrySet())
        {
            System.out.println(actualPair.getKey() + ":" + actualPair.getValue());
        }
        StringBuilder errorMsg = new StringBuilder("");
        System.out.println("--------------------Expected Values------------------------");
        for (final Map.Entry<String, String> expectedPair : expectedDic.entrySet())
        {
            //Print expected Value
            String expectedValue = (null == expectedPair.getValue()) ? "null" : expectedPair.getValue();
            System.out.println(expectedPair.getKey() + ":" + expectedValue);
            boolean compareSame = true;
            if (actualDic.containsKey(expectedPair.getKey()))
            {
                double expectNum = 0;
                //Replace all []
                String expectedValues = (null == expectedPair.getValue()) ? null : expectedValue.replace("[", "").replace("]", "").replace("\r\n", "").trim();
                String actualValues = actualDic.get(expectedPair.getKey()).replace("[", "").replace("]", "").replace("\r\n", "").replace("\"", "").trim();

                //If contains ",", we need to split it then compare the string array
                if (!StringUtils.isEmpty(expectedPair.getValue()) && expectedPair.getValue().contains(","))
                {
                    //remove first and last "
                    if(expectedValues.matches("\"(.+)\""))
                    {
                        expectedValues = expectedValues.substring(1, expectedValues.length() - 1);
                    }
                    if (actualValues.matches("\"(.+)\""))
                    {
                        actualValues = actualValues.substring(1, actualValues.length() - 1);
                    }
                    String[] array1 = expectedValues.split(",");
                    String[] array2 = actualValues.split(",");
                    //Handle the special value: number + string.E.g: DMDTotalMarkupRate: 159.15484CAD(DMDRppMinusOpp,
                    String regEx = "^-?[0-9]+.*[0-9]*[A-Z]{3}$";
                    if (!StringUtils.isEmpty(array1[0]) && array1[0].matches(regEx) && array2[0].length() > 3)
                    {
                        expectNum = Double.parseDouble(array1[0].substring(0, array1[0].length() - 3));
                        String expectCurrency = array1[0].substring(array1[0].length() - 3);
                        for (int i = 0; i < array1.length; i++) array1[i] = array1[i].replace(expectCurrency, "");
                        for (int i = 0; i < array2.length; i++) array2[i] = array2[i].replace(expectCurrency, "");
                        compareSame = compareObject(array1, array2, new ArrayList<String>(), errorMsg);
                    }
                    else
                    {
                        //Convert to List to ignore order
                        compareSame = compareObject(Arrays.asList(array1),
                                Arrays.asList(array2), new ArrayList<String>(), errorMsg);
                    }
                }
                //If it is a number, don't need exactly match, use regex to matche - "-?[0-9]+.*[0-9]*"
                else if (!StringUtils.isEmpty(expectedValues) && expectedValues.matches("-?[0-9]+.*[0-9]*"))
                {
                    expectNum = Double.parseDouble(expectedValues);
                    double actualNum = 0;
                    if(actualValues.matches("-?[0-9]+.*[0-9]*"))
                    {
                        actualNum = Double.parseDouble(actualValues);
                    }
                    if (!actualValues.matches("-?[0-9]+.*[0-9]*") || Math.abs(actualNum - expectNum) > 0.01)
                    {
                        compareSame = false;
                    }
                }
                //Handle the special value: number + stirng.E.g: DMDTotalMarkupRate: 159.15484CAD(DMDRppMinusOpp, "^-?[0-9]+.*[0-9]*[A-Z]{3}$"
                else if (!StringUtils.isEmpty(expectedValues) && expectedValues.matches("^-?[0-9]+.*[0-9]*[A-Z]{3}$"))
                {
                    expectNum = Double.parseDouble(expectedValues.substring(0, expectedValues.length() - 3));
                    double actualNum = 0;
                    if(actualValues.substring(0, actualValues.length() - 3).matches("-?[0-9]+.*[0-9]*"))
                    {
                        actualNum = Double.parseDouble(actualValues.substring(0, actualValues.length() - 3));
                    }
                    if (!actualValues.substring(0, actualValues.length() - 3).matches("-?[0-9]+.*[0-9]*") || Math.abs(actualNum - expectNum) > 0.01)
                    {
                        compareSame = false;
                    }
                    String expectCurrency = expectedValues.substring(expectedValues.length() - 3);
                    String actualCurrency = actualValues.substring(actualValues.length() - 3);
                    if (expectCurrency != actualCurrency)
                    {
                        compareSame = false;
                    }
                }
                //If no special character exist in expected value and expected value is not "", just conpare the string - if expected value is "", we don't compare the detail value, just verify Key exist in Actual Dic
                else if (!StringUtils.isEmpty(expectedValues) && !expectedValues.isEmpty())
                {
                    if (!expectedValues.toLowerCase().equals(actualValues.toLowerCase()))
                    {
                        compareSame = false;
                    }
                }
            }
            //Compare if the expected Key exist in actual Map
            if (null != expectedPair.getValue() && !actualDic.containsKey(expectedPair.getKey()))
            {
                errorMsg.append(String.format("Expected Key %s not exist in actual values!\r\n",expectedPair.getKey()));
            }
            //Compare if the Key not expected exist in actual Map
            if (null == expectedPair.getValue() && actualDic.containsKey(expectedPair.getKey()) &&
                    !StringUtils.isEmpty(actualDic.get(expectedPair.getKey())))
            {
                errorMsg.append(String.format("Key %s should not exist in actual values!\r\n", expectedPair.getKey()));
            }

            if (!compareSame) errorMsg.append(String.format("Value for Key %s is not as expected, exptected: %s, acutal: %s!\r\n",
                    expectedPair.getKey(), expectedPair.getValue(), actualDic.get(expectedPair.getKey())));
        }
        return errorMsg.toString();
    }

}
