package com.expedia.s3.cars.framework.test.common.reporting;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import java.util.List;
import java.util.Map;

/**
 * A sample TestNG reporter that logs to console... This is intended as a sample
 * Created by mpaudel on 5/20/16.
 */
@SuppressWarnings("PMD")
public class ConsoleTestNGReporter implements IReporter {
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

        //iterate over all the suites
        for (ISuite suite: suites)
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
