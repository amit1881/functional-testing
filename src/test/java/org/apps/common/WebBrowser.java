
package org.apps.common;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.mettl.helper.MettlContext;
import com.mettl.page.MettlWebPage;

/**
 * This class represents a web browser and provides an abstraction over the
 * selenium's web driver object
 * 
 * @author Ketan
 */
public class WebBrowser {

	private final WebDriver driver;
	public String startingWindow;
	public String childWindow1;
	public String childWindow2;
	public WebDriver window;
	ArrayList<String> tabs = new ArrayList<String>();
	public String corporateWindow;

	/**
	 * Default Constructor
	 */
	public WebBrowser() {
		driver = DriverFactory.getInstance().buildWebDriver();

		// TODO: these values should be configurable from
		// MainConfig.properties
		maximizeScreen();
	}

	public void maximizeScreen() {
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	public WebBrowser(String eventFireingWebdriver) {
		driver = DriverFactory.getInstance().buildWebDriver(eventFireingWebdriver);
		maximizeScreen();
	}

	/**
	 * Direct the browser to supplied URL and return the page object
	 * corresponding to the supplied class name
	 * 
	 * @param url
	 * @param pageClassName
	 * @return
	 * @throws InterruptedException
	 */
	public WebPage navigateToUrl(String url, String pageClassName) throws InterruptedException {
		driver.get(url);
		Thread.sleep(3000);
		return makeMettlWebPage(pageClassName);
	}

	/**
	 * This method provides the protocol used to open the current URL
	 * 
	 * https://tests.mettl.com/registerCandidate returns https:
	 * 
	 * @return
	 */
	public String getProtocolForCurrentUrl() {
		String completeUrl = driver.getCurrentUrl();
		if (completeUrl == null)
			throw new RuntimeException("Invalid Browser State - No URL found");

		return completeUrl.substring(0, completeUrl.indexOf("//"));
	}

	public String getCurrentUrl() {
		String completeUrl = driver.getCurrentUrl();
		if (completeUrl == null)
			throw new RuntimeException("Invalid Browser State - No URL found");

		return completeUrl;
	}

	/**
	 * @param id
	 * @param timeInSeconds
	 */
	public void waitForElementToBeEnabled(final String id, int timeInSeconds) {
		(new WebDriverWait(driver, timeInSeconds)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				try {
					WebElement element = d.findElement(By.id(id));
					return element.isEnabled();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return false;
				}
			}
		});
	}

	public void waitForElementState(final WebElement element, final String text, int timeInSeconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(1000, TimeUnit.MILLISECONDS);
		wait.withTimeout(timeInSeconds, TimeUnit.SECONDS);
		Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				if (element.getText().equals(text)) {
					return true;
				}
				return false;
			}
		};
		wait.until(function);
	}

	public void waitForElementVisiblisty(WebElement element, int timeoutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(element));
		//Above two lines can be written collectively as below
		//(new WebDriverWait(driver, timeoutInSeconds)).until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForElementInVisibility(String element, int timeoutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(element)));
	}

	public void waitForElementClickable(WebElement element, int timeInSeconds) {
		(new WebDriverWait(driver, timeInSeconds)).until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitForElementVisiblisty(List<WebElement> element, int timeInSeconds) {
		(new WebDriverWait(driver, timeInSeconds)).until(ExpectedConditions.visibilityOfAllElements(element));
	}

	public void waitForElementEnable(WebElement element, int timeInSeconds) {
		(new WebDriverWait(driver, timeInSeconds)).until(ExpectedConditions.elementToBeClickable(element));
		// driver.findElement(By.tagName("button"))
	}

	public void scrollDown() {
		JavascriptExecutor jsx = (JavascriptExecutor) driver;
		jsx.executeScript("window.scrollBy(477, 751)", "");
		//above tow lines can be written collectively as below
		//((JavascriptExecutor)driver).executeScript("", "");
	}

	public void scrollUp() {
		JavascriptExecutor jsx = (JavascriptExecutor) driver;
		jsx.executeScript("scroll(0, -250);");
	}

	public void scrollTillElementNotFound(WebElement e) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", e);

	}

	public void scrollingToBottomofAPage() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public void disconnectInternet_For_Time(int mili_sec) throws IOException, InterruptedException{
		if(OSDetector().equalsIgnoreCase("mac")){
			Runtime.getRuntime().exec("networksetup -setairportpower airport off");  
			Thread.sleep(mili_sec);
		}else{
			Runtime.getRuntime().exec("cmd /c ipconfig /release");
			Thread.sleep(mili_sec);
		}
	}
	
	public void connectInternet_and_Wait_for(int mili_sec) throws IOException, InterruptedException{
		if(OSDetector().equalsIgnoreCase("mac")){
			Runtime.getRuntime().exec("networksetup -setairportpower airport on"); 
			Thread.sleep(mili_sec);
		}else{
			Runtime.getRuntime().exec("cmd /c ipconfig /renew");
			Thread.sleep(mili_sec);
		}
	}
	
	/**
	 * Build an instance corresponding to Mettl's web page
	 * 
	 * @param pageClassName
	 * @return
	 */
	public WebPage makeMettlWebPage(String pageClassName) {
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

	/**
	 * Gets the Page Title for the currently open page
	 * 
	 * @return
	 */
	public String getCurrentPageTitle() {
		return driver.getTitle();
	}

	/**
	 * Kill the browser
	 */
	public void quit() {
		driver.quit();
	}

	/**
	 * 
	 */
	public void closeBrowser() {
		driver.close();
	}

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
			System.err.println("Unable to create directory to store screenshots - " + directoryPath);
			return;
		}

		File screenshotFile = new File(screenshotFilePath);
		File screenshotFileAsCapturedByDriver = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
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
		String directoryPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "HtmlActualImages";
		String screenshotFilePath = directoryPath + File.separator + fileName;
		File directory = new File(directoryPath);
		if (!directory.exists() && !directory.mkdirs()) {
			System.err.println("Unable to create directory to store screenshots - " + directoryPath);
			return directory;
		}

		File screenshotFile = new File(screenshotFilePath);
		File screenshotFileAsCapturedByDriver = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
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

	public void getstartingWindowInstance() {
		startingWindow = driver.getWindowHandle();
	}

	public void moveToStartingWindow() {
		driver.switchTo().window(startingWindow);
	}

	public void switchToPopUpWindow() {
		childWindow1 = (String) driver.getWindowHandles().toArray()[1];
		window = driver.switchTo().window(childWindow1);
	}

	public void switchToPopUpWindowMultiplePopUpWindow() {
		childWindow2 = (String) driver.getWindowHandles().toArray()[2];
		window = driver.switchTo().window(childWindow2);
	}

	public void closingTestWindow() {
		driver.close();

		// driver.switchTo().window(startingWindow);
	}

	public String returnStartingWindowInstance() {
		String instance = driver.getWindowHandle();
		return instance;
	}

	public void swtichToStartingWindow(String instance) {
		driver.switchTo().window(instance);
	}

	public void refreshPage() {
		driver.navigate().refresh();
	}
	
	public void clickOnMouseoverlink(WebElement link) {
		// TODO Auto-generated method stub
		Actions actions = new Actions(driver);
		actions.moveToElement(link);
		actions.build().perform();
	}

	public void hitKeyBoardEnter(WebElement e) {
		Actions actions = new Actions(driver);
		actions.moveToElement(e);
		actions.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\uE014')).perform();
		actions.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\uE004')).perform();
	}

	/*
	 * Mouse hover on any Element
	 */
	public void mouseHover(WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).build().perform();
	}

	/*
	 * Double click on element
	 */

	public void doubleClick(WebElement element) {
		Actions doubleclick = new Actions(driver);
		doubleclick.moveToElement(element).doubleClick().build().perform();
	}
	
	public void singleClick(WebElement element) {
		Actions singleclick = new Actions(driver);
		singleclick.moveToElement(element).click().build().perform();
	}

	public void closeAlertBox() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 120);
			wait.until(ExpectedConditions.alertIsPresent());
			Thread.sleep(4000);
			Alert alert = driver.switchTo().alert();
			alert.accept();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteAllCookies() {
		driver.manage().deleteAllCookies();
	}

	public void switchToIframe() {
		List<WebElement> findElements = driver.findElements(By.tagName("iframe"));
		driver.switchTo().frame(findElements.get(0));

	}
	
	public void switchToIframe(String iframe){
		driver.switchTo().frame(iframe);
	}
	
	public void switchToIframe(int iframe){
		driver.switchTo().frame(iframe);
	}

	public void switchToIframeForCodeSnippet() {
		List<WebElement> findElements = driver.findElements(By.tagName("iframe"));
		List<String> id = new ArrayList<String>();
		for (WebElement e : findElements) {
			id.add(e.getAttribute("id"));
			//system.out.println("iframe-id" + e.getAttribute("id"));
		}
		//system.out.println("Webelement at position 1" + findElements.get(1));
		driver.switchTo().frame(id.get(1));
	}

	public String iframeIdCodeSnippet() {
		List<WebElement> findElements = driver.findElements(By.tagName("iframe"));
		return findElements.get(1).getAttribute("id");
	}

	public void switchToSizeIframe() {
		List<WebElement> sizeFrame = driver.findElements(By.cssSelector(".cke>iframe.cke_panel_frame"));
		driver.switchTo().frame(sizeFrame.get(0));
	}

	public void switchToBiometricIfrmame() {
		List<WebElement> iframe = driver.findElements(By.cssSelector(".hm-body>iframe"));
		driver.switchTo().frame(iframe.get(0));
	}

	public void switchToDefaultContent() throws InterruptedException {
		driver.switchTo().defaultContent();
		Thread.sleep(1000);
	}

	public String getTextFromHiddenText(WebElement elem) {
		String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", elem);
		return text;
	}

	public void keyDown() {
		for (int i = 0; i > 5; i++) {
			Actions action = new Actions(driver);
			action.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.RETURN).build().perform();
		}
	}

	public void clickBackSpace() {
		Actions action = new Actions(driver);
		action.sendKeys(Keys.RETURN);
	}

	public String getCodeWindow() {
		String codeWindow = (String) ((JavascriptExecutor) driver)
				.executeScript("return $('.CodeMirror')[0].CodeMirror.getValue()");
		return codeWindow;

	}

	public void enterDataCodeWindow(String data) {
		String scriptString = "$('.CodeMirror')[0].CodeMirror.setValue(arguments[0])";
		((JavascriptExecutor) driver).executeScript(scriptString, data);
	}

	public void ClickonHiddenElement(WebElement webElement) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", webElement);
	}

	public String getAlertMessageTextAndClose() {
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		String text = alert.getText();
		alert.accept();
		return text;
	}

	// Testing purpose//
	public String getCodefromSnipet() {
		String codeWindow = (String) ((JavascriptExecutor) driver)
				.executeScript("return $(\"._question-body ._snippet textarea\").val()");
		return codeWindow;
	}

	// Getting data from CodeProject
	public String getCodeFromCodeCodeProject(int i) {

		String codeWindow = (String) ((JavascriptExecutor) driver)
				.executeScript("return $('.CodeMirror')[" + i + "].CodeMirror.getValue()");
		return codeWindow;
	}

	public void setCodeIntoCodeProject(String data, int i) {
		String js_attempted_hack = "angular.element($('div [ui-view=question]')[0]).scope().item.IsAttempted=true;";
		((JavascriptExecutor) driver).executeScript(js_attempted_hack);
		String scriptString = "$('.CodeMirror')[" + i + "].CodeMirror.setValue(arguments[0])";
		((JavascriptExecutor) driver).executeScript(scriptString, data);
	}

	public void setDataInFitbBlank(String data) {
		String scriptString = "$('._blank').html('" + data + "')";
		((JavascriptExecutor) driver).executeScript(scriptString, data);
	}

	public void removeFitbBlank() {
		String scriptString = "$($('._blank')[2]).remove()";
		((JavascriptExecutor) driver).executeScript(scriptString);
	}

	public void fluentWaitUntilElementIsnotVisible(WebElement element, int timeUnitInSecond, int pollingWaitInSecond) {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(element));
		new FluentWait<WebElement>(element).withTimeout(timeUnitInSecond, TimeUnit.SECONDS)
				.pollingEvery(pollingWaitInSecond, TimeUnit.MILLISECONDS).until(new Function<WebElement, Boolean>() {
					public Boolean apply(WebElement element) {
						return element.isDisplayed();
					}
				});

	}

	public void RightClickOnElement(WebElement element) {
//		Actions oAction = new Actions(driver);
//
//		oAction.contextClick().build()
//				.perform(); /*
//							 * this will perform right click
//							 */
//		oAction.contextClick().sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		
		Actions action=new Actions(driver);
		action.moveToElement(element).contextClick().build().perform();

	}

	// get short Question answer text
	// Testing purpose//
	public String getShortQuestionAnswer() {
		String answer = (String) ((JavascriptExecutor) driver).executeScript("return $('.control-left input').val()");
		return answer;
	}

	// Right click on any element
	public void rightClickOnElement(WebElement element) {
		Actions oAction = new Actions(driver);
		oAction.moveToElement(element);
		oAction.contextClick(element).build().perform();
		// WebElement elementOpen = driver.findElement(By.linkText("Add"));
		// /*This will select menu after right click */
	}

	// get fibt Question answer
	public List<String> getAllFibtAnswer(int n) {
		List<String> fibtanswer = new ArrayList<String>();
		for (int i = 0; i <= 1; i++) {
			String answer = (String) ((JavascriptExecutor) driver)
					.executeScript("return $('.text-tags input')[" + i + "].value");
			fibtanswer.add(answer);
		}
		return fibtanswer;

	}

	public void setDataInDatabaseWindow(String data, int i) {
		String scriptString = "$('._dbBoxe .CodeMirror')[" + i + "].CodeMirror.setValue(arguments[0])";
		((JavascriptExecutor) driver).executeScript(scriptString, data);
	}

	/**
	 * Enter data in codeproject type question in question creation type
	 */

	public void setCodeIntoCodeProjectInQuestionCreation(String data, int i) {
		String scriptString = "$('.CodeMirror')[" + i + "].CodeMirror.setValue(arguments[0])";
		((JavascriptExecutor) driver).executeScript(scriptString, data);
	}

	/**
	 * get starting date
	 */
	public String getStartingDate() {
		String startingDate = (String) ((JavascriptExecutor) driver)
				.executeScript("return $('._datePick._from').val()");
		return startingDate;
	}

	public String getMachineDateInMMDDYYFormat() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
		//system.out.println(sdf.format(cal.getTime()));
		String string = sdf.format(cal.getTime()).toString();
		//system.out.println(string);
		return string;
	}

	/**
	 * get End date
	 */
	public String getEndDate() {
		String endDate = (String) ((JavascriptExecutor) driver).executeScript("return $('._datePick._to').val()");
		return endDate;

	}

	/**
	 * set starting date
	 */
	public void setStartingDate(String startDate) {
		String scriptString = "$('._datePick._from').datepicker( 'setDate', \'" + startDate + "\')";
		((JavascriptExecutor) driver).executeScript(scriptString);

	}

	public void setEndDate(String endDate) {
		String scriptString = "$('._datePick._to').datepicker( 'setDate', \'" + endDate + "\')";
		((JavascriptExecutor) driver).executeScript(scriptString);

	}

	/**
	 * set End date
	 */

	public String getMachineTime() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("h.00 a");
		//system.out.println(sdf.format(cal.getTime()));
		String string = sdf.format(cal.getTime()).toString();
		//system.out.println(string);
		return string;
	}

	public int getMachineTimeinInteger() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		//system.out.println(sdf.format(cal.getTime()));
		String string = sdf.format(cal.getTime()).toString();
		//system.out.println(string);
		int currentTime = Integer.valueOf(string);
		//system.out.println(currentTime);
		return currentTime;
	}

	public String getMachineDate() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yy");
		//system.out.println(sdf.format(cal.getTime()));
		String string = sdf.format(cal.getTime()).toString();
		//system.out.println(string);
		return string;
	}

	public String getIncreasedDateInMachineDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(cal.DATE, 1);
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yy");
		//system.out.println(sdf.format(cal.getTime()));
		String string = sdf.format(cal.getTime()).toString();
		//system.out.println(string);
		return string;
	}

	public void slidermove(WebElement slider, int r1, int r2) {
		Actions moveslider = new Actions(driver);
		Action action = moveslider.dragAndDropBy(slider, r1, r2).build();
		action.perform();
	}

	public WebDriver getDriver() {
		return driver;
	}

	public String getResponseTextOFTypingSimulater() {
		String responseData = (String) ((JavascriptExecutor) driver).executeScript("return $('.response-text').val()");
		return responseData;

	}

	/**
	 * get starting date
	 * 
	 * @throws InterruptedException
	 */

	public void takeActionOnAlertBox(String action) {
		if (action == "Accept") {
			WebDriverWait wait = new WebDriverWait(driver, 20);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();
		} else if (action == "Reject") {
			WebDriverWait wait = new WebDriverWait(driver, 20);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
		}
	}

	public void clickOnDateRangeFilterOnResultPage() throws InterruptedException {
		Thread.sleep(3000);
		((JavascriptExecutor) driver).executeScript("$('span.icon-calender').siblings('input').focus()");
	}

	public void selectTextViaKeyboard() {
		Actions builder = new Actions(driver);
		Action select = builder.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build();
		select.perform();

	}

	public void clickonToolBarButtonsinQuestionCreation(String textboxclass, String buttonName) {
		switch (buttonName) {
		case "numberedlist":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__numberedlist').trigger('click')");
			break;
		case "table":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__table').trigger('click')");
			break;
		case "multimedia":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__multimedia').trigger('click')");
			break;
		case "codesnippet":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__codesnippet').trigger('click')");
			break;
		case "source":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__source').trigger('click')");
			break;
		case "bold":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__bold').trigger('click')");
			break;
		case "italic":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__italic').trigger('click')");
			break;
		case "underline":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__underline').trigger('click')");
			break;
		case "superscript":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__superscript').trigger('click')");
			break;
		case "subscript":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__subscript').trigger('click')");
			break;
		case "bulletedlist":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__bulletedlist').trigger('click')");
			break;
		case "outdent":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__outdent').trigger('click')");
			break;
		case "indent":
			((JavascriptExecutor) driver).executeScript("$('" + textboxclass
					+ "').parent().siblings('.cke_top').find('a.cke_button__indent').trigger('click')");
			break;

		}

	}

	public String getFromAndToDateValueFromPicker() {
		String responseData = (String) ((JavascriptExecutor) driver).executeScript("return $('._daterange').val()");
		//system.out.println(responseData);
		return responseData;
	}

	public void selectAll() {
		Actions action = new Actions(driver);
		Action selectAll = action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build();
		selectAll.perform();
	}

	public void switchToLaunchTestWindow() {
		corporateWindow = driver.getWindowHandle();
		for (String launchTestWindow : driver.getWindowHandles()) {
			if (!launchTestWindow.equals(corporateWindow)) {
				driver.switchTo().window(launchTestWindow);
				break;
			}
		}
	}

	public void switchToCorporateWindow() {
		driver.switchTo().window(corporateWindow);
	}

	public void sendValueInGradingTextBox(String number) {
		String scriptString = "$('._weightage').val('" + number + "')";
		((JavascriptExecutor) driver).executeScript(scriptString, number);

	}

	public void switchToFileUplaodAndCloseWindow() {
		driver.switchTo().window("File Upload").close();
	}

	public String getPageSource() {
		return driver.getPageSource();
	}

	public String getPageSource(String typeOfPageSource) {
		String javascript = "return arguments[0].innerHTML";
		String pageSource = (String) ((JavascriptExecutor) driver).executeScript(javascript,
				driver.findElement(By.tagName("html")));
		return pageSource;
	}

	public void openUrlinNewTab(String urlLink) {
		driver.navigate().to(urlLink);
	}

	public void openEmptyNewTab() {
		if(OSDetector().contains("Mac")){
			System.out.println(OSDetector());
			WebElement tab = driver.findElement(By.tagName("body"));
			tab.sendKeys(Keys.COMMAND + "T");
		}else{
		WebElement tab = driver.findElement(By.tagName("body"));
		tab.sendKeys(Keys.CONTROL + "t");
		}

	}

	public void openEmptyNewTabInChrome() {
		try{
			if (MettlContext.SINGLETON.getEntryAsString("BROWSER_NAME_Chrome").equalsIgnoreCase("Chrome")) {
	
			String script = String.format("window.open('{0}', '_blank')");
			((JavascriptExecutor) driver).executeScript("window.open('{0}', '_blank')");}
			
		}catch(Exception e){
			String script = String.format("window.open('{0}', '_blank')");
			((JavascriptExecutor) driver).executeScript("window.open('{0}', '_blank')");
		}

	}

	public void switchToPrevioustab() throws InterruptedException {
		Actions action = new Actions(driver);
		action.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys(Keys.TAB).build().perform();
		switchToDefaultContent();
	}

	public void switchToNextTab() throws InterruptedException {
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"\t");
		  //Switch to current selected tab's content.
		  driver.switchTo().defaultContent();  
	}

	public String getMachineDateindMMYYYYformat() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
		String string = sdf.format(cal.getTime()).toString();
		return string;
	}

	public void javaScriptExecutorToClickOnElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}

	public void verifyEmailInYopmail(String email_id, String email_subject) throws InterruptedException {
		String yopmail_url = "http://www.yopmail.com/en/";
		// driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL+"t");
		Actions a = new Actions(driver);
		a.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).click(driver.findElement(By.xpath("//*[text()='MY TESTS']")))
				.keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).perform();
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
		//system.out.println(email_subject);
		driver.switchTo().frame("ifinbox");
		if (email_subject.equalsIgnoreCase(driver.findElement(By.className(".lms")).getText())) {
			//system.out.println("Email verified");
			driver.switchTo().defaultContent();
			driver.close();
		}
		driver.switchTo().window(parenwindow);
	}

	public Map<String, String> getSummaryTableRow(WebElement table, Integer rowNumber) {
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

		if (rowNumber < rows.size()) {
			List<String> rowHeader = getSummaryTableHeader(table);
			List<String> rowValue = new ArrayList<String>();
			for (WebElement we : rows.get(rowNumber).findElements(By.tagName("td"))) {
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

	public List<String> getSummaryTableHeader(WebElement table) {
		waitForElementVisiblisty(table, 120);
		List<String> summaryTableHeader = new ArrayList<String>();
		List<WebElement> webElementHeader = table.findElements(By.cssSelector("thead>tr>th"));
		for (WebElement we : webElementHeader) {
			summaryTableHeader.add(we.getText());
		}
		return summaryTableHeader;
	}

	// Wait for a DropDown to have more than one Element
	public void waitForDropdownOptionsLoad(WebElement element, int timeInSeconds) {
		final Select dropdown = new Select(element);
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250, TimeUnit.MILLISECONDS);
		wait.withTimeout(timeInSeconds, TimeUnit.SECONDS);
		Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver arg0) {
				if (dropdown.getOptions().size() > 1) {
					return true;
				}
				return false;
			}
		};
		wait.until(function);
	}
	public void switchTab() {
	    Actions action = new Actions(driver);
	    action.keyDown(Keys.ALT).sendKeys(Keys.TAB).build().perform();
	}

	public boolean videoPlaying(){
		JavascriptExecutor js=(JavascriptExecutor) driver;
		String feed="remoteFeed";
      if((boolean) js.executeScript("return (document.getElementById('"+feed+"').paused)")==false && (Double)js.executeScript("return (document.getElementById('"+feed+"').currentTime)")!=0 && (boolean) js.executeScript("return (document.getElementById('"+feed+"').autoplay)")==true){
    	   return true;
      }
	return false;
	}

	public static String OSDetector () {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "Windows";
		} else if (os.contains("nux") || os.contains("nix")) {
			return "Linux";
		}else if (os.contains("mac")) {
			return "Mac";
		}else if (os.contains("sunos")) {
			return "Solaris";
		}else {
			return "Other";
		}
	}
	
	public void setTextInCKEditor(String instance_name,String text){ 
		JavascriptExecutor js=(JavascriptExecutor) driver;
	    js.executeScript("CKEDITOR.instances['"+instance_name+"'].setData('"+text+"')");
	}
}
