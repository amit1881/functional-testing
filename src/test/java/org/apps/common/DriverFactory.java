package org.apps.common;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import org.func.helper.FuncContext;

/**
 * Web Driver Factory class that is responsible for making a web driver instance
 * based on the configured browser
 * 
 * @author amit
 */
public class DriverFactory {

	private static DriverFactory factory = new DriverFactory();

	/**
	 * Private Constructor
	 */
	private DriverFactory() {
	}

	/**
	 * Gets Singleton intsance
	 * 
	 * @return
	 */
	public static DriverFactory getInstance() {
		return factory;
	}

	/**
	 * Builds a web driver instance based on the the configured browser (in
	 * MainProperties)
	 * 
	 * @return
	 */
	public WebDriver buildWebDriver() {
		return _buildWebDriver();
	}

	// public EventFiringWebDriver buildWebDriver(String webDriverType) {
	// WebDriver driver = new FirefoxDriver();
	// EventFiringWebDriver eventFiringWD = new EventFiringWebDriver(driver);
	// return eventFiringWD;
	// }

	public WebDriver buildWebDriver(String webDriverType) {
		if (webDriverType.equalsIgnoreCase("Chrome")) {
			return _makeChromeDriver();
		} else {
			try {
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + "/DriverExes/gecko/geckodriver1");
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("media.navigator.permission.disabled", true);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting", false);
				String dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
						+ File.separator + "resources" + File.separator + "ExcelReports";
				profile.setPreference("browser.download.dir", dir);
				profile.setPreference("browser.helperApps.neverAsk.openFile",
						"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf");
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
						"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf");
				profile.setPreference("browser.helperApps.alwaysAsk.force", false);
				profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
				profile.setPreference("browser.download.manager.focusWhenStarting", false);
				profile.setPreference("browser.download.manager.useWindow", false);
				profile.setPreference("browser.download.manager.showAlertOnComplete", false);
				profile.setPreference("browser.download.manager.closeWhenDone", false);
				profile.setPreference("media.navigator.streams.fake", false);
				profile.setPreference("auto-select-desktop-capture-source='Entire screen' ", true);
				profile.setPreference("pdfjs.disabled", true);
				// system.out.println("this is profile browser");
				return new FirefoxDriver(profile);
			} catch (Exception e) {
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("media.navigator.permission.disabled", true);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting", false);
				String dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
						+ File.separator + "resources" + File.separator + "ExcelReports";
				profile.setPreference("browser.download.dir", dir);
				profile.setPreference("browser.helperApps.neverAsk.openFile",
						"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf");
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
						"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/pdf");
				profile.setPreference("browser.helperApps.alwaysAsk.force", false);
				profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
				profile.setPreference("browser.download.manager.focusWhenStarting", false);
				profile.setPreference("browser.download.manager.useWindow", false);
				profile.setPreference("browser.download.manager.showAlertOnComplete", false);
				profile.setPreference("browser.download.manager.closeWhenDone", false);
				profile.setPreference("media.navigator.streams.fake", false);
				profile.setPreference("auto-select-desktop-capture-source='Entire screen' ", true);
				profile.setPreference("pdfjs.disabled", true);
				System.out.println("this is profile browser");
				return new FirefoxDriver(profile);
			}
		}

	}

	private WebDriver _buildWebDriver() {
		String browserName = MettlContext.SINGLETON.getEntryAsString("BROWSER_NAME");
		if (browserName == null) {
			throw new RuntimeException("Browser not specified. Please check MainConfig.properties");
		} else if ("Firefox".equalsIgnoreCase(browserName)) {
			return _makeFirefoxDriver();
		} else if ("htmlUnit".equalsIgnoreCase(browserName)) {
			return new HtmlUnitDriver();
		} else if ("Chrome".equalsIgnoreCase(browserName)) {
			return _makeChromeDriver();
		} else if ("IE".equalsIgnoreCase(browserName)) {
			return _makeIEDriver();
		} else
			throw new RuntimeException("Browser not supported - " + browserName);
	}

	private WebDriver _makeIEDriver() {
		DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
		ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		ieCapabilities.setCapability("ignoreZoomSetting", true);
		ieCapabilities.setCapability("nativeEvents", false);
		System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "/DriverExes/IE/IEDriverServer.exe");
		return new InternetExplorerDriver(ieCapabilities);
	}

	private WebDriver _makeChromeDriver() {
		if (WebBrowser.OSDetector().contains("Mac")) {
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("test-type");
			options.addArguments("--start-maximized");
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			File crx = new File(
					System.getProperty("user.dir") + "/DriverExes/Chrome/hkjemkcbndldepdbnbdnibeppofoooio_main.crx");
			options.addExtensions(crx);
			options.addArguments("--auto-select-desktop-capture-source=Entire screen");
			options.addArguments("--use-fake-ui-for-media-stream");
			return new ChromeDriver(capabilities);
		} else {
			if (WebBrowser.OSDetector().contains("Win"))
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "/DriverExes/Chrome/chromedriver.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("test-type");
			capabilities.setCapability("chrome.binary",
					System.getProperty("user.dir") + "/DriverExes/Chrome/chromedriver.exe");
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			File crx = new File(
					System.getProperty("user.dir") + "/DriverExes/Chrome/hkjemkcbndldepdbnbdnibeppofoooio_main.crx");
			options.addExtensions(crx);
			options.addArguments("--auto-select-desktop-capture-source=Entire screen");
			options.addArguments("--use-fake-ui-for-media-stream");
			return new ChromeDriver(capabilities);
		}
	}

	private WebDriver _makeFirefoxDriver() {
		try {
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + "/DriverExes/gecko/geckodriver1");
			FirefoxProfile fp = new FirefoxProfile();
			fp.setAcceptUntrustedCertificates(true);
			fp.setPreference("security.enable_java", true);
			fp.setPreference("plugin.state.java", 2);
			fp.setPreference("media.navigator.permission.disabled", true);
			fp.setPreference("media.navigator.streams.fake", false);
			fp.setPreference("auto-select-desktop-capture-source='Entire screen' ", true);
			if (WebBrowser.OSDetector().contains("Mac"))
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + "/DriverExes/gecko/geckodriver1");
			WebDriver d = new FirefoxDriver(fp);
			return d;
		} catch (Exception e) {
			FirefoxProfile fp = new FirefoxProfile();
			fp.setAcceptUntrustedCertificates(true);
			fp.setPreference("security.enable_java", true);
			fp.setPreference("plugin.state.java", 2);
			fp.setPreference("media.navigator.permission.disabled", true);
			fp.setPreference("media.navigator.streams.fake", false);
			fp.setPreference("auto-select-desktop-capture-source='Entire screen' ", true);
			if (WebBrowser.OSDetector().contains("Mac"))
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + "/DriverExes/gecko/geckodriver1");
			WebDriver d = new FirefoxDriver(fp);
			return d;
		}

	}

	public static void main(String[] args) {
		MettlContext.SINGLETON.setEntry("BROWSER_NAME", "ie9");
		DriverFactory factory = new DriverFactory();
		WebDriver driver = null;
		try {
			driver = factory.buildWebDriver();
			// system.out.println("Built " + driver);

			Thread.sleep(5000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (driver != null)
				driver.quit();
		}
	}
}
