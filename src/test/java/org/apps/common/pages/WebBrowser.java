package org.apps.common.pages;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.google.common.base.Function;

public class WebBrowser {

    private final WebDriver driver;
    private final int IMPLICIT_WAIT_TIME = 30; // Seconds
    private final int EXPLICIT_WAIT_TIME = 10; // Seconds
    private final String startingWindow;
    private ArrayList<String> windows = new ArrayList<String>(); // List of All Open Windows
    private Alert alert;

    /*** Default Constructor **/
    public WebBrowser() {
        driver = DriverFactory.getInstance().buildWebDriver();
        setImpicitWait(IMPLICIT_WAIT_TIME);
        maximizeScreen();
        startingWindow = getCurrentWindowHandle();
        windows.add(getCurrentWindowHandle());
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void maximizeScreen() {
        driver.manage().window().maximize();
    }

    /**
     * Direct the browser to supplied URL and return the page object corresponding to the supplied
     * class name
     *
     * @param url
     * @param pageClassName
     * @return
     * @throws InterruptedException
     */
    public WebPage navigateToUrl(String url, String pageClassName) throws InterruptedException {
        driver.get(url);
        Thread.sleep(3000); // TODO Why is this sleep needed?
        return makeWebPage(pageClassName);
    }

    public String executeJSAndGetResponse(String jsCommand) {
        JavascriptExecutor jexec = (JavascriptExecutor) driver;
        return (String) jexec.executeScript(jsCommand);

    }

    /**
     * Build an instance corresponding to App's web page. Use this method if you have already
     * navigated to the page.
     *
     * @param pageClassName
     * @return
     */
    public WebPage makeWebPage(String pageClassName) {
        try {
            Class<WebPage> pageClass = (Class<WebPage>) Class.forName(pageClassName);
            WebPage page = PageFactory.initElements(driver, pageClass);
            page.setBrowser(this);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create Page instance for " + pageClassName);
        }
    }


    /******************** Browser Level Interactions ***********************/


    /*
     * getTestUrl is ImageProctor or normal test
     *
     */
    public boolean IsImageProctorLink() {
        JavascriptExecutor jexec = (JavascriptExecutor) driver;
        return (boolean) jexec.executeScript("return window.isImageProctored;");
    }

    /*** This method provides the protocol used to open the current URL **/
    public String getProtocolForCurrentUrl() {
        String completeUrl = driver.getCurrentUrl();
        if (completeUrl == null)
            throw new RuntimeException("Invalid Browser State - No URL found");

        return completeUrl.substring(0, completeUrl.indexOf("//"));
    }

    /*** Gets the Current URL */
    public String getCurrentUrl() {
        // TODO Explanation of why this Sleep is needed.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String completeUrl = driver.getCurrentUrl();
        if (completeUrl == null)
            throw new RuntimeException("Invalid Browser State - No URL found");

        return completeUrl;
    }

    /*** Gets the Page Title for the currently open page */
    public String getCurrentPageTitle() {
        return driver.getTitle();
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    /*** Kill the browse ***/
    public void quit() {
        driver.quit();
    }

    /**
     * Close the Browser
     **/
    public void close() {
        driver.close();
    }

    /* Delete All Cookies */
    public void deleteAllCookies() {
        driver.manage().deleteAllCookies();
    }

    /***************************** Waits Handling **********************************/

    // Implicit wait
    public void setImpicitWait(Integer timeInSeconds) {
        driver.manage().timeouts().implicitlyWait((int) timeInSeconds, TimeUnit.SECONDS);
    }

    /**
     * Explicit Wait - This Method waits for an Element to be Enabled in the Active Browser Window
     *
     * @param webElement
     * @param timeOutInSeconds
     */
    public void waitForElementToBeEnabled(final WebElement webElement, int timeOutInSeconds) {
        (new WebDriverWait(driver, timeOutInSeconds)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                try {
                    return webElement.isEnabled();
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    public void waitForElementVisiblisty(WebElement element, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementInVisibility(String cssLocator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssLocator)));
    }

    /**
     * wait till element becomes invisible or hidden
     *
     * @param element
     * @param timeoutInSeconds
     */
    public void waitTillElementBecomesInvisible(WebElement element, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void waitForElementClickable(WebElement element, int timeoutInSeconds) {
        (new WebDriverWait(driver, timeoutInSeconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElementVisiblisty(List<WebElement> element, int timeoutInSeconds) {
        (new WebDriverWait(driver, timeoutInSeconds))
                .until(ExpectedConditions.visibilityOfAllElements(element));
    }

    public void scrollElementInView(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
    }

    // Wait for a DropDown to have more than one Element
    public void waitForDropdownOptionsLoad(WebElement element, int timeOutInSeconds) {
        final Select dropdown = new Select(element);
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
        wait.pollingEvery(250, TimeUnit.MILLISECONDS);
        wait.withTimeout(timeOutInSeconds, TimeUnit.SECONDS);
        Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver arg0) {
                if (dropdown.getOptions().size() >= 1) {
                    return true;
                }
                return false;
            }
        };
        wait.until(function);
    }

    /*************** Screenshots ************************/

    /**
     * Takes screenshot and store it in a particular directory
     *
     * @param screenshotId
     */
    public void takeScreenshotAndStore(String screenshotId) {
        if (!(driver instanceof TakesScreenshot)) {
            System.err.println("Driver being used is not screenshot enabled");
            return;
        }

        // Assumption: tests are run from the base directory only
        String fileName = screenshotId + ".png";
        String directoryPath = "target" + File.separator + "screenshots";
        String screenshotFilePath = directoryPath + File.separator + fileName;
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err
                    .println("Unable to create directory to store screenshots - " + directoryPath);
            return;
        }

        File screenshotFile = new File(screenshotFilePath);
        File screenshotFileAsCapturedByDriver =
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshotFileAsCapturedByDriver, screenshotFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("** Error occured in creating screenshot file - " + e.getMessage());
        }
    }

    /**
     * Takes screenshot and store it in a particular directory
     *
     * @param screenshotId
     * @return
     * @throws IOException
     */
    public File takeScreenshotAndStore(WebElement element, String screenshotId) throws IOException {
        if (!(driver instanceof TakesScreenshot)) {
            System.err.println("Driver being used is not screenshot enabled");
            return null;
        }
        // Assumption: tests are run from the base directory only
        String fileName = screenshotId + ".png";
        String directoryPath =
                System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                        + File.separator + "resources" + File.separator + "HtmlActualImages";
        String screenshotFilePath = directoryPath + File.separator + fileName;
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err
                    .println("Unable to create directory to store screenshots - " + directoryPath);
            return directory;
        }

        File screenshotFile = new File(screenshotFilePath);
        File screenshotFileAsCapturedByDriver =
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Point p = element.getLocation();
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();
        Rectangle rect = new Rectangle(width, height);
        BufferedImage img = null;
        img = ImageIO.read(screenshotFileAsCapturedByDriver);
        BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
        ImageIO.write(dest, "png", screenshotFileAsCapturedByDriver);
        try {
            FileUtils.copyFile(screenshotFileAsCapturedByDriver, screenshotFile);
            return screenshotFile;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("** Error occured in creating screenshot file - " + e.getMessage());
        }
        return screenshotFile;
    }


    /***************** Handle Multiple Windows ********************/

    // Switch to starting window
    public void switchToStartingWindow() {
        driver.switchTo().window(startingWindow);
    }

    // Switch to any window
    public void switchToWindow(Integer index) {
        driver.switchTo().window(windows.get(index));
    }

    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    // TODO needs improvement - will fail in some cases
    public String getNewWindowHandle() throws NoSuchWindowException {
        try {
            Integer count = 0;
            while (driver.getWindowHandles().size() <= windows.size() && count < 2) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
            String window = (String) driver.getWindowHandles().toArray()[windows.size()];
            windows.add(window);
            return window;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchWindowException("No New Window Found");
        }

    }

    // Switch to a new window
    public void switchToNewWindow() {
        driver.switchTo().window(getNewWindowHandle());
    }

    // All Open Window Handles
    public ArrayList<String> getAllWindowHandles() {
        return windows;
    }

    //Deletes Last Entry from Windows Array List
    public void removeNewWindowFromList() {
        windows.remove(windows.size() - 1);
    }

    /*********************** Alert Handling ***************************/

    public Boolean isAlertPresent() {
        try {
            new WebDriverWait(driver, EXPLICIT_WAIT_TIME)
                    .until(ExpectedConditions.alertIsPresent());
            alert = driver.switchTo().alert();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void acceptAlert() {
        if (isAlertPresent()) {
            alert.accept();
        } else
            throw new NoAlertPresentException();
    }

    public void dismissAlert() {
        if (isAlertPresent()) {
            alert.dismiss();
        } else
            throw new NoAlertPresentException();
    }

    public String getAlertMessageTextAndAccept() {
        if (isAlertPresent()) {
            String text = alert.getText();
            alert.accept();
            return text;
        } else
            throw new NoAlertPresentException();
    }

    /*********************** Handling iFrames *************************/

    // Switches to an iframe located by - Name/ID/Web Element
    public void switchToIframe(Object iframeLocator) {
        if (iframeLocator instanceof String)
            driver.switchTo().frame((String) iframeLocator);
        else if (iframeLocator instanceof Integer)
            driver.switchTo().frame((Integer) iframeLocator);
        else if (iframeLocator instanceof WebElement)
            driver.switchTo().frame((WebElement) iframeLocator);
        else {
            Reporter.log(
                    "Failed to Switch to iFrame, incorrect locator specifid :" + iframeLocator);
            throw new NoSuchFrameException("Could not locate : " + iframeLocator);
        }
    }

    public void switchToDefaultContent() throws InterruptedException {
        driver.switchTo().defaultContent();
    }

    /****************** Read Yopmail ***************************/
    // TODO
    public void verifyEmailInYopmail(String email_id, String email_subject)
            throws InterruptedException {
        String yopmail_url = "http://www.yopmail.com/en/";
        // driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL+"t");
        Actions a = new Actions(driver);
        a.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT)
                .click(driver.findElement(By.xpath("//*[text()='MY TESTS']"))).keyUp(Keys.SHIFT)
                .keyUp(Keys.CONTROL).perform();
        String parenwindow = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        handles.remove(parenwindow);
        String child_window = handles.iterator().next();
        if (child_window != parenwindow) {
            driver.switchTo().window(child_window);
        }
        driver.navigate().to(yopmail_url);
        Thread.sleep(35000);
        driver.findElement(By.cssSelector("#login")).sendKeys(email_id);
        Thread.sleep(5000);
        driver.findElement(By.cssSelector(".sbut")).click();
        Thread.sleep(35000);
        System.out.println(email_subject);
        driver.switchTo().frame("ifinbox");
        if (email_subject.equalsIgnoreCase(driver.findElement(By.className(".lms")).getText())) {
            System.out.println("Email verified");
            driver.switchTo().defaultContent();
            driver.close();
        }
        driver.switchTo().window(parenwindow);
    }


    /*********************** Table Handling ***********************/

    public Map<String, String> getTableRow(WebElement table, Integer rowNumber) {
        waitForElementVisiblisty(table, 120);
        List<WebElement> rows = null;
        rows = table.findElements(By.cssSelector("tbody>tr"));
        Map<String, String> rowData = new LinkedHashMap<String, String>();

        List<WebElement> columns = null;
        columns = rows.get(0).findElements(By.cssSelector("td"));
        int count = 0;
        while (columns.size() == 1 && count < 120) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rows = table.findElements(By.cssSelector("tbody>tr"));
            columns = rows.get(0).findElements(By.cssSelector("td"));
            count++;
        }
        if (columns.size() == 1)
            return rowData;

        if (rowNumber <= rows.size()) {
            List<String> rowHeader = getTableHeader(table);
            List<String> rowValue = new ArrayList<String>();
            for (WebElement we : rows.get(rowNumber - 1).findElements(By.tagName("td"))) {
                rowValue.add(we.getText());
            }
            rows.clear();
            for (int i = 0; i < rowHeader.size(); i++) {
                rowData.put(rowHeader.get(i), rowValue.get(i));
            }
            rowHeader.clear();
            rowValue.clear();
        }
        return rowData;
    }

    public List<String> getTableHeader(WebElement table) {
        waitForElementVisiblisty(table, 120);
        List<String> summaryTableHeader = new ArrayList<String>();
        List<WebElement> webElementHeader = table.findElements(By.cssSelector("thead>tr>th"));
        for (WebElement we : webElementHeader) {
            summaryTableHeader.add(we.getText());
        }
        return summaryTableHeader;
    }

    /************************ Misc Browser Actions ********************/

    // TODO ?
    public void clickOnMouseoverlink(WebElement link) {
        new Actions(driver).moveToElement(link).build().perform();
    }

    public void hitKeyBoardEnter(WebElement e) {
        Actions actions = new Actions(driver);
        actions.moveToElement(e);
        actions.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\uE014')).perform();
        actions.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\uE004')).perform();
    }

    /**
     * Mouse hover on any Element
     */
    public void mouseHover(WebElement element) {
        new Actions(driver).moveToElement(element);
    }

    /**
     * Double click on element
     */
    public void doubleClick(WebElement element) {
        new Actions(driver).moveToElement(element).doubleClick().build().perform();
    }

    public String getTextFromHiddenElement(WebElement elem) {
        String text = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].innerHTML;", elem);
        return text;
    }

    public void clickBackSpace() {
        Actions action = new Actions(driver);
        action.sendKeys(Keys.RETURN);
    }

    public void clickonHiddenElement(WebElement webElement) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", webElement);
    }

    // Right click on any element
    public void rightClickOnElement(WebElement element) {
        new Actions(driver).contextClick(element).build().perform();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    /*
     * Clicks on an Element using Java Script Executor, to be used only when redular click does not
     * work
     */
    public void javaScriptExecutorToClickOnElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }

    public void scrollToBottomOfAPage() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollDown() {
        JavascriptExecutor jsx = (JavascriptExecutor) driver;
        jsx.executeScript("window.scrollBy(477, 751)", "");
    }

    public void scrollUp() {
        JavascriptExecutor jsx = (JavascriptExecutor) driver;
        jsx.executeScript("scroll(0, -250);");
    }

    public boolean videoPlaying() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String feed = "remoteFeed";
        if ((boolean) js.executeScript("return (document.getElementById('" + feed + "').paused)")
                == false && (Double) js
                .executeScript("return (document.getElementById('" + feed + "').currentTime)") != 0
                && (boolean) js
                .executeScript("return (document.getElementById('" + feed + "').autoplay)")
                == true) {
            return true;
        }
        return false;
    }

    /**
     * Enter data using java script
     *
     * @param javascript
     * @param data
     */
    public void enterDataUsingJScript(String javascript, String data) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(javascript, data);
    }

    public void enterDataUsingJScriptByID(String id, String data) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("document.getElementById('" + id + "').value = '" + data + "';");
    }

    /**
     * Switch to code window
     *
     * @param javascript
     * @return
     */
    public String switchToCodeWindow(String javascript) {
        String codeWindow = (String) ((JavascriptExecutor) driver).executeScript(javascript);
        return codeWindow;
    }

    /**
     * Check element is present
     *
     * @param locator
     * @return
     */
    public Boolean isElementPresent(String locator, Integer timeout) {
        setImpicitWait(timeout);
        Boolean result = false;
        try {
            driver.findElement(By.xpath((locator)));
            result = true;
        } catch (NoSuchElementException e) {
            Reporter.log("Element not found with locator : " + locator);
        } finally {
            setImpicitWait(IMPLICIT_WAIT_TIME);
        }
        return result;
    }

    /**
     * Get random String of specific length
     *
     * @param len
     * @return
     */
    public String randomString(int len) {
        String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * Get random Number Between a Range
     *
     * @param lowerLimit
     * @param upperLimit
     * @return
     */
    public int randomNumber(int lowerLimit, int upperLimit) {
        return ThreadLocalRandom.current().nextInt(lowerLimit, upperLimit);
    }

    /**
     * Create Folder
     *
     * @param folderPath
     */
    public void createFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * Capture Screenshot
     *
     * @param fileName
     */
    public void captureScreenshot(String fileName) {
        String folderPath = "target/surefire-reports/html/Screenshots/";
        createFolder(folderPath);
        try {
            String screenshotName = this.getFileName(fileName);
            FileOutputStream out = new FileOutputStream(folderPath + screenshotName + ".jpg");
            out.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            out.close();
            String screenshotRelativePathPath = "Screenshots/" + screenshotName + ".jpg";
            System.setProperty("org.uncommons.reportng.escape-output", "false");
            Reporter.log("<a href=\"" + screenshotRelativePathPath + "\" target=\"_blank\">\n"
                    + "  <img width=\"42\" height=\"42\" border=\"0\" align=\"center\"  src=\""
                    + screenshotRelativePathPath + "\"/>");
        } catch (Exception e) {
            e.printStackTrace();
            Reporter.log("Failed To Take screenshot " + e, true);
        }
    }

    /**
     * Get file name
     *
     * @param file
     * @return
     */
    public String getFileName(String file) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh_mm_ssaa");
        Calendar cal = Calendar.getInstance();
        String fileName = file + "_" + dateFormat.format(cal.getTime());
        return fileName;
    }
}
