package com.crossover.e2e;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;
//import junit.framework.TestCase;
//import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GMailTest {
	private WebDriver driver;
	private Properties properties = new Properties();
	String emailSubject=null;
	@BeforeClass
	public void setUp() throws Exception {

		properties.load(new FileReader(new File("src/test/resources/test.properties")));
		// Dont Change below line. Set this value in test.properties file incase
		// you need to change it..
		System.setProperty("webdriver.chrome.driver", properties.getProperty("webdriver.chrome.driver"));
		driver = new ChromeDriver();

	}

	@AfterClass
	 public void tearDown() throws Exception {
	 driver.quit();
	 }

	/*
	 * Please focus on completing the task
	 * 
	 */
	@Test
	public void testSendEmail() throws Exception {
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.get("https://mail.google.com/");
		
		WebDriverWait wait = new WebDriverWait(driver, 60);
		
		WebElement userElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierId")));
		userElement.sendKeys(properties.getProperty("username"));
		driver.findElement(By.id("identifierNext")).click();

		WebElement passwordElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));
		passwordElement.sendKeys(properties.getProperty("password"));
		WebElement passwordNext = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("passwordNext")));
		passwordNext.click();
		
		WebElement composeElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button' and (.)='Compose']")));
		composeElement.click();

		WebElement toElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("to")));
		toElement.click();
		toElement.clear();
		toElement.sendKeys(String.format("%s@gmail.com", properties.getProperty("username")));
		
		// emailSubject and emailbody to be used in this unit test.
		emailSubject = properties.getProperty("email.subject");
		WebElement subjectElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("subjectbox")));                                                                                                                                                                              
		subjectElement.clear();                                                                                                                                        
		subjectElement.sendKeys(emailSubject);                                                     
		
		String emailBody = properties.getProperty("email.body");
		WebElement emailBodyElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role = 'textbox']")));                                         
		emailBodyElement.click();                                                                                                                                      
		emailBodyElement.clear();                                                                                                                                      
		emailBodyElement.sendKeys(emailBody);
		
		//Label email as social
		WebElement moreOptions = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-tooltip='More options']")));
		moreOptions.click();
		WebElement label = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Label']")));
		label.click();
		WebElement labelName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Label as:']/following::input")));
		labelName.sendKeys("Social");
		WebElement selectLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Label as:']/following::input/following::*[text()='Social']")));
		selectLabel.click();
		
		//Send the email
		WebElement sendMailButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button' and text()='Send']")));
		sendMailButton.click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Message sent')]")));
		
		WebElement socialSection = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='tab' and @aria-label=contains(.,'Social')]")));
		socialSection.click();
//		driver.findElement(By.name("q")).sendKeys(emailSubject);
//		Thread.sleep(5000);
//		driver.findElement(By.name("q")).click();
//		Actions builder=new Actions(driver);
//		builder.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
//		Thread.sleep(5000);
//		WebElement subject=driver.findElement(By.xpath("//h2[text()='"+emailSubject+"']"));
//		assertTrue(subject.isDisplayed(),"email subject is not correct");
		
		//List<WebElement> inboxEmails = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(By.xpath("//*[@class='zA zE']"))));                   
		List<WebElement> inboxEmails = driver.findElements(By.xpath("//*[@class='zA zE']"));
		   for(WebElement email : inboxEmails){ 
			   //System.out.println(email.getText());
		       if(email.isDisplayed() && email.getText().contains(emailSubject)){                                                                                                                                   
		           email.click();                                                                                                                                         

		           //WebElement lbl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@title,'with label Inbox')]")));                    
		           WebElement subject = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'"+emailSubject+"')]")));          
		           WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'"+emailBody+"')]")));   
		           //System.out.println(lbl.getText());
		           System.out.println(subject.getText());
		           System.out.println(body.getText());
		           assertEquals(subject.getText(),emailSubject,"Email subject is not correct");
		           assertEquals(body.getText(),emailBody,"Email body is not corret");
		           break;
		       }                                                                                                                                                          
		   }
		
		
		
		
		
		
//		driver.findElement(By.id(":19c")).click();
//		WebElement labels=driver.findElement(By.xpath("//*[@data-tooltip='Labels']"));
//		//JavascriptExecutor js=(JavascriptExecutor)driver;
//		//js.executeAsyncScript("arguments[0].click()", labels);
//		labels.click();
//		List<WebElement> listofSelectedLabels=driver.findElements(By.xpath("//*[@role='menuitemcheckbox' and @aria-checked='true']"));
//		System.out.println(listofSelectedLabels.get(0).getText());
//		assertEquals(listofSelectedLabels.get(0).getText(),"Social","email is not under correct label");
		//builder.moveToElement(driver.findElement(By.name("q"))).sendKeys(emailSubject).sendKeys(Keys.ENTER).build().perform();
		//Thread.sleep(5000);
		//driver.findElement(By.xpath("//*[text()='"+emailSubject+"']/preceding::*[@role='button' and @title='Not starred']")).click();
	}
	
	@Test
	public void verifyEmailIsStarred() throws InterruptedException{
		WebElement star=driver.findElement(By.xpath("//h2[text()='"+emailSubject+"']/following::*[@role='checkbox']"));
		star.click();
		Thread.sleep(5000);
		assertEquals(star.getAttribute("title"), "Starred", "email is not starred");
	}
	
	@Test
	public void verifyLabel(){
		WebElement lbl=driver.findElement(By.xpath("(//*[@role='button' and @title='Labels'])[2]"));
		lbl.click();
		List<WebElement> listofSelectedLabels=driver.findElements(By.xpath("//*[@role='menuitemcheckbox' and @aria-checked='true']"));
		//System.out.println(listofSelectedLabels.get(0).getText());
		for(WebElement ele:listofSelectedLabels){
				System.out.println(ele.getText());
				assertEquals(ele.getText(),"Social","email is not under correct label");		
		}
	}

}
