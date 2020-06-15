package com.expedia.s3.cars.supplyconnectivity.amadeus.service.testsuites;

import org.testng.TestNG;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpaudel on 5/20/16.
 */
public class AllAmadeusSuite {
    public void allSuiteTests()
    {
        //First create a list of test suites
        List<XmlSuite> suites = new ArrayList<>();

        //Create list of packages
        List<XmlPackage> packages = new ArrayList<>();

        XmlSuite suite = new XmlSuite();
        suite.setName("All tests in this project");

        XmlTest test = new XmlTest(suite);
        test.setName("All tests");

        XmlPackage xmlPackage = new XmlPackage("com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.*");
        packages.add(xmlPackage);
        test.setPackages(packages);

        // if you want to run regression tests only if bvt succeeds
        //test.addXmlDependencyGroup("regression", "bvt");

        suites.add(suite);

        TestNG testNG = new TestNG();
        testNG.setXmlSuites(suites);
        testNG.run();
    }
    public static void main(String args[])
    {
        AllAmadeusSuite suite = new AllAmadeusSuite();
        suite.allSuiteTests();
    }
}
