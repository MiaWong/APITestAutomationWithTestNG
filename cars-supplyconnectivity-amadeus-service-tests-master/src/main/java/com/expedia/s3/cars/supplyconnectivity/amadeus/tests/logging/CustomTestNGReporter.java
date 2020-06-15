package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.logging;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import java.util.List;
import java.util.Map;

/**
 * Created by mpaudel on 5/20/16.
 */
public class CustomTestNGReporter implements IReporter {
    @Override
    public void generateReport(List<XmlSuite> list, List<ISuite> list1, String s) {

        //iterate over all the suites
        for (ISuite suite: list1)
        {
            String suiteName= suite.getName();
            Map<String, ISuiteResult> suiteResultMap = suite.getResults();
            for( ISuiteResult sr: suiteResultMap.values())
            {
                ITestContext testContext = sr.getTestContext();
                System.out.println("Passed test for Suite '"+ suiteName + "' is: "
                +testContext.getPassedTests().getAllResults().size());
                System.out.println("Failed test for Suite '"+ suiteName + "' is: "
                        +testContext.getFailedTests().getAllResults().size());
                System.out.println("Skipped test for Suite '"+ suiteName + "' is: "
                        +testContext.getSkippedTests().getAllResults().size());

            }
        }
    }
}
