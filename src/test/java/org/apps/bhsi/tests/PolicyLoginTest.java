package org.apps.bhsi.tests;

import org.apps.common.tests.FuncTest;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.apps.bhsi.pages.PolicyLogin;

public class PolicyLoginTest extends FuncTest{
	
	PolicyLogin pl;
	
	@BeforeClass
	public void policyLogin() throws Throwable{
		pl=(PolicyLogin) browser.navigateToUrl(getTestProperty("POLICY_SYSTEM_URL"), "org.apps.bhsi.pages.PolicyLogin");
		pl.login("bhunt", "password");
	}
	
	@Test
	public void verifyTest(){
		System.out.println("1st test");
	}

}
