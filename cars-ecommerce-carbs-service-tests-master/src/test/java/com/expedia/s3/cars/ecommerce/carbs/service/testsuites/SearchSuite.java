package com.expedia.s3.cars.ecommerce.carbs.service.testsuites;

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
        final List<XmlSuite> suites = new ArrayList<>();

        //Create a list of classes to be added in the suite
        final List<XmlClass> classes = new ArrayList<>();

        //create as many suites as you want
        final XmlSuite suite = new XmlSuite();
        suite.setName("Search Tests");

        //Now create a test
        final XmlTest test = new XmlTest(suite);
        test.setName("All regression tests");

        //Get the class to run and setup test and suites
        final XmlClass xmlClass = new XmlClass("com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.Search.Search");
        classes.add(xmlClass);
        test.setXmlClasses(classes);
        suites.add(suite);

        //run the tests under suites
        final TestNG testNG = new TestNG();
        testNG.setXmlSuites(suites);
        testNG.run();
    }

    public static void main() {
        final SearchSuite suite = new SearchSuite();
        suite.searchSuiteTests();
    }
}
