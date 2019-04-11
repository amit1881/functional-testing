package org.apps.common.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.func.helper.FuncContext;
import java.io.File;
// SINGLETON CLASS
public class DriverFactory {
    /*** Private static member */
    private static DriverFactory factory = new DriverFactory();

    /*** Private Constructor */
    private DriverFactory() {
    }

    /*** Static Factory Method::Gets Singleton instance */
    public static DriverFactory getInstance() {
        return factory;
    }

    /*** Builds a web driver instance based on the the configured browser and the OS **/
    public WebDriver buildWebDriver() {
        WebDriver webDriver;
        String OSName = System.getProperty("os.name").toLowerCase();
        switch (FuncContext.INSTANCE.getEntryAsString("BROWSER_NAME")) {
            case "Firefox":
                webDriver = makeFirefoxDriver(OSName); // Firefox runs on all OSs
                break;
            case "Chrome":
                webDriver = makeChromeDriver(OSName); // Chrome runs on all OSs
                break;
            case "IE":
                webDriver = makeIEDriver(); // IE runs on Windows Only
                break;
            case "Edge":
                webDriver = makeEdgeDriver(); // Edge runs on Windows Only
                break;
            case "Safari":
                webDriver = makeSafariDriver(); // Safari runs on OSX Only
                break;
            case "HTMLUnit":
                webDriver = makeHTMLUnitDriver(); // Safari runs on OSX Only
                break;
            default:
                throw new RuntimeException("Browser not supported");
        }
        return webDriver;
    }

    private WebDriver makeIEDriver() {

        /* Supported Command Line Options - https://docs.microsoft.com/en-us/previous-versions/windows/internet-explorer/ie-developer/general-info/hh826025(v=vs.85)
            https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver */

        InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
        internetExplorerOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        internetExplorerOptions.setCapability("ignoreZoomSetting", true);
        internetExplorerOptions.setCapability("nativeEvents", false);
        System.setProperty("webdriver.ie.driver", "drivers/win/IEDriverServer.exe");
        return new InternetExplorerDriver(internetExplorerOptions);
    }

    private WebDriver makeEdgeDriver() {

        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        edgeOptions.setCapability("ignoreZoomSetting", true);
        edgeOptions.setCapability("nativeEvents", false);
        System.setProperty("webdriver.edge.driver", System.getProperty("user.dir") + "drivers/win/MicrosoftWebDriver.exe");
        return new EdgeDriver(edgeOptions);
    }

    private WebDriver makeChromeDriver(String OSName) {

        /* Full List of Arguments available here - https://peter.sh/experiments/chromium-command-line-switches/
        List of Capabilities Supported available here - http://chromedriver.chromium.org/capabilities
        Currently we are not using any Capabilities */

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--auto-select-desktop-capture-source=Entire screen");
        chromeOptions.addArguments("--use-fake-ui-for-media-stream");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-timezone-tracking-option");
        //File crx = new File("plugins/hkjemkcbndldepdbnbdnibeppofoooio_main.crx");
        //chromeOptions.addExtensions(crx);

        if (OSName.contains("win"))
            System.setProperty("webdriver.chrome.driver", "drivers/win/chromedriver.exe");
        else if (OSName.contains("mac"))
            System.setProperty("webdriver.chrome.driver", "drivers/mac/chromedriver");
        else
            System.setProperty("webdriver.chrome.driver", "drivers/unix/chromedriver");
        return new ChromeDriver(chromeOptions);
    }

    private WebDriver makeFirefoxDriver(String OSName) {
        
        /* Full List of Preference available here - http://kb.mozillazine.org/About:config_entries and http://kb.mozillazine.org/Category:Preferences
            List of Capabilities available here - https://github.com/mozilla/geckodriver
            List of Arguments - http://kb.mozillazine.org/Command_line_arguments   */

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setAcceptInsecureCerts(true);
        firefoxOptions.addPreference("security.enable_java", true);
        firefoxOptions.addPreference("plugin.state.java", 2);
        firefoxOptions.addPreference("media.navigator.permission.disabled", true);
        firefoxOptions.addPreference("security.insecure_password.ui.enabled", false);
        firefoxOptions.addPreference("security.insecure_field_warning.contextual.enabled", false);
        firefoxOptions.addPreference("media.navigator.streams.fake", false);
        firefoxOptions.addPreference("auto-select-desktop-capture-source='Entire screen' ", true);

        if (OSName.contains("win"))
            System.setProperty("webdriver.gecko.driver", "drivers/win/geckodriver.exe");
        else if (OSName.contains("mac"))
            System.setProperty("webdriver.gecko.driver", "drivers/mac/geckodriver");
        else
            System.setProperty("webdriver.gecko.driver", "drivers/unix/geckodriver");

        //Disable GeckoDriver logs to be printed to console
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        return new FirefoxDriver(firefoxOptions);
    }

    private WebDriver makeSafariDriver() {
        return new SafariDriver();
    }

    private WebDriver makeHTMLUnitDriver() {
        return new HtmlUnitDriver();
    }

}
