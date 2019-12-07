package org.apps.bhsi.pages;

import org.apps.common.pages.WebPage;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class DashBoard extends WebPage{
	
	@FindBy(linkText="New Submission")
	private WebElement newSubmission;
	
	@FindBy(linkText="NewSubmission")
	private WebElement newSubmission1;

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Boolean isNewSubmissionLinkVisible(){
		Boolean elementPresence=webBrowser.isElementPresent(newSubmission, 60);
		return elementPresence;
		
	}
	
	public Boolean isNewSubmissionLinkVisible1(){
		Boolean elementPresence=webBrowser.isElementPresent(newSubmission1, 60);
		return elementPresence;
		
	}
	
	public NewSubmission startNewSubmission(){
		newSubmission.click();
		NewSubmission ns=(NewSubmission) webBrowser.makeWebPage("org.apps.bhsi.pages.NewSubmission");
		return ns;
	}

}
