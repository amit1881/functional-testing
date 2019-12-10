package org.apps.bhsi.tests;

import static org.testng.Assert.assertTrue;

import org.apps.bhsi.pages.DashBoard;
import org.apps.bhsi.pages.PolicyLogin;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apps.common.tests.FuncTest;

public class DashBoardTest extends FuncTest{
	
	PolicyLogin pl;
	DashBoard db;
	
	@BeforeClass
	public void policyLogin() throws Throwable{
		pl=(PolicyLogin) browser.navigateToUrl(getTestProperty("POLICY_SYSTEM_URL"), "org.apps.bhsi.pages.PolicyLogin");
		db=pl.login("uname", "pwd");
	}
	
	@Test
	public void verifyNewSubmissionLinkIsVisble(){
		assertTrue(db.isNewSubmissionLinkVisible(),"new submission link is not visible");
	}
	
	@Test
	public void verifyNewSubmissionLinkIsVisble1(){
		assertTrue(db.isNewSubmissionLinkVisible1(),"new submission link is not visible");
	}

}
