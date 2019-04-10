package org.apps.bhsi.pages;

import org.apps.common.pages.WebPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PolicyLogin extends WebPage {

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

	public DashBoard login(String userName, String password) {

		this.userName.sendKeys(userName);
		this.password.sendKeys(password);
		loginBtn.click();
		DashBoard db = (DashBoard) webBrowser.makeWebPage("org.apps.bhsi.pages.DashBoard");
		return db;
	}

}
