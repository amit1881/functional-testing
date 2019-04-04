/**
 * 
 */
package org.apps.common;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.mettl.helper.MettlContext;

/**
 * @author guneet
 * 
 */
public class FuncTestNGListener implements ITestListener {

    public void onFinish(ITestContext arg0) {
        // TODO Auto-generated method stub

    }

    public void onStart(ITestContext arg0) {
        // TODO Auto-generated method stub

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        // TODO Auto-generated method stub

    }

    public void onTestFailure(ITestResult testResult) {
        WebBrowser browser = (WebBrowser) MettlContext.SINGLETON.getEntry("browser");
        if (browser == null) {
            System.err.println("Programming Error - Web Browser instance not found. Screenshot will not be captured");
            return;
        }


        String screenShotId = testResult.getTestClass().getName() + "_" + testResult.getMethod().getMethodName();
        screenShotId = screenShotId.replace('.', '_');
        browser.takeScreenshotAndStore(screenShotId);
    }

    public void onTestSkipped(ITestResult arg0) {
    }

    public void onTestStart(ITestResult testResult) {
        Reporter.log(testResult.getClass().getName() + "#" + testResult.getMethod().getMethodName());
    }

    public void onTestSuccess(ITestResult arg0) {
    }

}
