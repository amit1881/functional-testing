package org.apps.bhsi.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.mettl.page.MettlWebPage;

public class PolicyLogin extends MettlWebPage {

	@FindBy(id = "username-inputEl")
	private WebElement userName;

	@FindBy(id = "password-inputEl")
	private WebElement password;

	@FindBy(linkText = "Login")
	private WebElement loginBtn;

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public NewSubmission login(String userName, String password) {

		this.userName.sendKeys(userName);
		this.password.sendKeys(password);
		NewSubmission ns = (NewSubmission) webBrowser.makeMettlWebPage("apps.bhsi.pages.NewSubmission");
		return ns;
	}

}
