package com.expedia.s3.cars.supplyconnectivity.sabre.service.testsuites;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

public class EndToEndSuite
{

    public static void main()
    {
        final EndToEndSuite suite = new EndToEndSuite();
        suite.e2eSuiteTests();
    }

    public void e2eSuiteTests()
    {
        //First create a list of test suites
        final List<XmlSuite> suites = new ArrayList<>();

        //Create a list of classes to be added in the suite
        final List<XmlClass> classes = new ArrayList<>();

        //create as many suites as you want
        final XmlSuite suite = new XmlSuite();
        suite.setName("sabre E2E Tests");

        //Now create a test
        final XmlTest test = new XmlTest(suite);
        test.setName("All regression tests");

        //Get the class to run and setup test and suites
        final XmlClass xmlClass = new XmlClass("com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.bvt.EndToEnd");
        classes.add(xmlClass);
        test.setXmlClasses(classes);
        suites.add(suite);

        //run the tests under suites
        final TestNG testNG = new TestNG();
        testNG.setXmlSuites(suites);
        testNG.run();
    }

}
