package org.apps.bhsi.tests;

import org.apps.common.tests.FuncTest;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.apps.bhsi.pages.DashBoard;
import org.apps.bhsi.pages.PolicyLogin;
import static org.testng.Assert.assertTrue;

public class PolicyLoginTest extends FuncTest{
	
	PolicyLogin pl;
	DashBoard db;
	
	@BeforeClass
	public void policyLogin() throws Throwable{
		pl=(PolicyLogin) browser.navigateToUrl(getTestProperty("POLICY_SYSTEM_URL"), "org.apps.bhsi.pages.PolicyLogin");
		db=pl.login("uname", "pwd");
	}
	
}
