package com.crossover.e2e;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class TestMail {
	
	@Test
	public void testMail(WebDriver driver, WebDriverWait wait, Properties properties){
		driver.get("https://mail.google.com/");                                                                                                                        

		   WebElement userElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierId")));                                                           
		   userElement.click();                                                                                                                                           
		   userElement.clear();                                                                                                                                           
		   userElement.sendKeys(properties.getProperty("username"));                                                                                                      

		   WebElement identifierNext = wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierNext")));                                                      
		   identifierNext.click();                                                                                                                                        

		   WebElement passwordElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));                                                         
		   passwordElement.click();                                                                                                                                       
		   passwordElement.clear();                                                                                                                                       
		   passwordElement.sendKeys(properties.getProperty("password"));                                                                                                  

		   WebElement passwordNext = wait.until(ExpectedConditions.elementToBeClickable(By.id("passwordNext")));                                                          
		   passwordNext.click();                                                                                                                                          

		   WebElement composeElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button' and (.)='Compose']")));                            
		   composeElement.click();                                                                                                                                        

		   WebElement maximizeEmailElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td//img[2]")));                                               
		   maximizeEmailElement.click();                                                                                                                                  

		   WebElement sendToElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("to")));                                                                 
		   sendToElement.click();                                                                                                                                         
		   sendToElement.clear();                                                                                                                                         
		   sendToElement.sendKeys(String.format("%s@gmail.com", properties.getProperty("username")));                                                                     

		   WebElement subjectElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@name = 'subjectbox']")));                                        
		   subjectElement.click();                                                                                                                                        
		   subjectElement.clear();                                                                                                                                        
		   subjectElement.sendKeys(properties.getProperty("email.subject"));                                                                                              

		   WebElement emailBodyElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role = 'textbox']")));                                         
		   emailBodyElement.click();                                                                                                                                      
		   emailBodyElement.clear();                                                                                                                                      
		   emailBodyElement.sendKeys(properties.getProperty("email.body"));                                                                                               

		   WebElement sendMailElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Send']")));                                            
		   sendMailElement.click();                                                                                                                                       

		   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Message sent')]")));                                                   
		   List<WebElement> inboxEmails = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(By.xpath("//*[@class='zA zE']"))));                   

		   for(WebElement email : inboxEmails){                                                                                                                           
		       if(email.isDisplayed() && email.getText().contains("email.subject")){                                                                                                                                   
		           email.click();                                                                                                                                         

		           WebElement label = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@title,'with label Inbox')]")));                    
		           WebElement subject = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'Subject of this message')]")));          
		           WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Single line body of this message')]")));   

		       }                                                                                                                                                          
		   }       

	}

}
