package org.apps.common.tests;

import static org.testng.Assert.fail;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.apps.common.pages.WebBrowser;
import org.func.helper.FuncContext;
import org.func.helper.FuncUtils;

/**
 * Base class for All Automation Tests
 * 
 */
@Listeners(FuncTestNGListener.class)
public abstract class FuncTest {
	protected WebBrowser browser;
	/**
	 * read from
	 * $src/test/resources/${appEnvironment}/{testClassName}.properties
	 * 
	 */
	protected Properties testProperties;

	@BeforeSuite
	public void initializeSuite() throws IOException {
		Properties mainConfigProperties = new Properties();
		mainConfigProperties.load(this.getClass().getClassLoader().getResourceAsStream("config/MainConfig.properties"));
		FuncContext.INSTANCE.loadFromProperties(mainConfigProperties);
		// log configuration
		Reporter.log("Test Suite Initialized with the following properties",true);
		Reporter.log("****************************************************",true);
		Set<String> keys = FuncContext.INSTANCE.getEntryKeys();
		for (String aKey : keys) {
			String str = "* " + aKey + " -> " + FuncContext.INSTANCE.getEntry(aKey);
			Reporter.log(str, true);
		}
		Reporter.log("****************************************************",true);
	}

	@BeforeClass
	public void beforeClass() throws Exception, Throwable {
		browser = new WebBrowser();
		testProperties = new Properties();
		String testConfigFileName = this.getClass().getSimpleName();
		Reporter.log("Loading Test Configuration from: " + testConfigFileName + "....", true);
		try {
			testProperties = FuncUtils.loadProperties(this, testProperties, testConfigFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		Reporter.log("Test Started : " + method.getName(), true);
	}

	protected String getTestProperty(String key) throws Throwable {
		String value = null;
		try {
			value = FuncUtils.getTestProperty(testProperties, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	@AfterClass(alwaysRun = true)
	public void tearDownAfterClass() throws Exception, Throwable {
		try {
			browser.quit();
		} catch (Throwable e) {
			fail("Exception Occured - " + e.getMessage(), e);
		}
	}

}