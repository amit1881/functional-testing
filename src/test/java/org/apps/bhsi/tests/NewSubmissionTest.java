package org.apps.bhsi.tests;

import org.apps.bhsi.pages.*;
import org.apps.common.tests.FuncTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NewSubmissionTest extends FuncTest{

	PolicyLogin pl;
	DashBoard db;
	NewSubmission ns;
	
	@BeforeClass
	public void policyLogin() throws Throwable{
		pl=(PolicyLogin) browser.navigateToUrl(getTestProperty("POLICY_SYSTEM_URL"), "org.apps.bhsi.pages.PolicyLogin");
		db=pl.login("uname", "pwd");
	}
	
	@Test
	public void verifyNewSubmissionFields(){
		ns=db.startNewSubmission();
	}
}
