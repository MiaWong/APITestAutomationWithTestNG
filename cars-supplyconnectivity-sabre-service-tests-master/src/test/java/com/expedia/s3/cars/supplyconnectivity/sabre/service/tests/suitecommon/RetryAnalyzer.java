package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Created by srajendran on 10-10-2018.
 */
public class RetryAnalyzer implements IRetryAnalyzer
{
    int counter;
    int retryLimit = 1;
	/*
	 * (non-Javadoc)
	 * @see org.testng.IRetryAnalyzer#retry(org.testng.ITestResult)
	 *
	 * This method decides how many times a test needs to be rerun.
	 * TestNg will call this method every time a test fails. So we
	 * can put some code in here to decide when to rerun the test.
	 *
	 * Note: This method will return true if a tests needs to be retried
	 * and false it not.
	 *
	 */

    @Override
    public boolean retry(ITestResult result) {

        if(counter < retryLimit)
        {
            counter++;
            return true;
        }
        return false;
    }
}
