package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.logging;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.ZonedDateTime;

/**
 * Created by mpaudel on 5/20/16.
 */
public class CustomTestNGLogger implements ITestListener
{

    @Override
    public void onTestStart(ITestResult iTestResult)
    {
        System.out.println("Test method started: "+iTestResult.getName() +
        " and time is: "+ ZonedDateTime.now());
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult)
    {
        System.out.println("Test method Succeeded: "+iTestResult.getName() +
                " and time is: "+ ZonedDateTime.now());
    }

    @Override
    public void onTestFailure(ITestResult iTestResult)
    {
        System.out.println("Test method failed: "+iTestResult.getName() +
                " and time is: "+ ZonedDateTime.now());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult)
    {
        System.out.println("Test method skipped: "+iTestResult.getName() +
                " and time is: "+ ZonedDateTime.now());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult)
    {

    }

    @Override
    public void onStart(ITestContext iTestContext)
    {
        System.out.println("Test in a suite started: "+iTestContext.getName() +
                " and time is: "+ ZonedDateTime.now());
    }

    @Override
    public void onFinish(ITestContext iTestContext)
    {
        System.out.println("Test in a suite finished: "+iTestContext.getName() +
                " and time is: "+ ZonedDateTime.now());
    }
}
