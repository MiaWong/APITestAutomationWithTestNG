package com.expedia.s3.cars.supplyconnectivity.amadeus.service.testsuites;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpaudel on 5/20/16.
 */
public class SearchSuite {
    public void searchSuiteTests()

    {
        //First create a list of test suites
        List<XmlSuite> suites = new ArrayList<>();

        //Create a list of classes to be added in the suite
        List<XmlClass> classes = new ArrayList<>();

        //create as many suites as you want
        XmlSuite suite = new XmlSuite();
        suite.setName("search Tests");

        //Now create a test
        XmlTest test = new XmlTest(suite);
        test.setName("All regression tests");

        //Get the class to run and setup test and suites
        XmlClass xmlClass = new XmlClass("com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.bvt.search");
        classes.add(xmlClass);
        test.setXmlClasses(classes);
        suites.add(suite);

        //run the tests under suites
        TestNG testNG = new TestNG();
        testNG.setXmlSuites(suites);
        testNG.run();
    }
    public static void main(String args[])
    {
        SearchSuite suite = new SearchSuite();
        suite.searchSuiteTests();
    }
}
