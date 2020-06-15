package com.expedia.s3.cars.supplyconnectivity.worldspan.service.testsuites;

import org.testng.TestNG;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpaudel on 5/20/16.
 */
public class BVTSuite {
    public void bvtSuiteTests()

    {
        //First create a list of test suites
        List<XmlSuite> suites = new ArrayList<>();

        //Create list of packages
        List<XmlPackage> packages = new ArrayList<>();

        //create as many suites as you want
        XmlSuite suite = new XmlSuite();
        suite.setName("Search Tests");

        //Now create a test
        XmlTest test = new XmlTest(suite);
        test.setName("All regression tests");

        XmlPackage xmlPackage = new XmlPackage("com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.bvt.*");
        packages.add(xmlPackage);
        test.setPackages(packages);

        //run the tests under suites
        TestNG testNG = new TestNG();
        testNG.setXmlSuites(suites);
        testNG.run();
    }

    public static void main(String args[]) {
        BVTSuite suite = new BVTSuite();
        suite.bvtSuiteTests();
    }
}
