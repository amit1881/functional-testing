package org.apps.common;

import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mettl.helper.MettlContext;

import net.minidev.json.JSONObject;

/**
 * Base class for All Mettl Automation Tests
 * 
 * @author Saarthak
 */
@Listeners(MettlTestNGListener.class)
public abstract class MettlTest {
	protected WebBrowser browser;
	protected JSONObject testMethodData;
	protected Map<String, JSONObject> testClassData;
	
	protected String hostName;

	// read from
	// $src/test/resources/${mettlEnvironment}/{testClassName}.properties
	protected Properties testProperties;

	// used to compare images
	protected ImageComparator imageComparator = new ImageComparator();

	@BeforeSuite
	public void initializeSuite() throws IOException {
		_loadMainConfigProperties();
	//	MettlUtils.readExcel();

	}

	private void _loadMainConfigProperties() throws IOException {
		Properties mainConfigProperties = new Properties();
		//system.out.println((this.getClass().getClassLoader().getResourceAsStream("config/MainConfig.properties")));
		mainConfigProperties.load(this.getClass().getClassLoader().getResourceAsStream("config/MainConfig.properties"));
		MettlContext.SINGLETON.loadFromProperties(mainConfigProperties);

		// log configuration
		_logMainConfigProperties();
	}

	@BeforeClass
	public void beforeClass() throws Exception, Throwable {
		browser = new WebBrowser();
		_loadTestProperties();
		// loadClassData();

		MettlContext.SINGLETON.setEntry("browser", browser);
		switch (MettlContext.SINGLETON.getEntryAsString("mettlEnvironment")) {
		case "production":
			hostName = "mettl.com";
			break;
		case "online":
            hostName = "mettl.online";
            break;
		case "staging":
			hostName = "mettl.de";
			break;
		case "stagingxyz":
			hostName = "mettl.info";
			break;
		default:
			break;
		}
	}

	protected void _loadTestProperties() throws IOException {
		testProperties = MettlUtils.loadProperties(this);
	}

	protected void loadClassData() throws IOException {
		testClassData = MettlUtils.readSheet(this);
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		// testMethodData = testClassData.get(method.getName());
		System.out.println("\t* " + method.getName());
	}

	protected String getTestProperty(String key) throws Throwable {
		String value = null;
		try {
			value = MettlUtils.getTestProperty(testProperties, key).replaceAll("mettl.de", hostName).replaceAll("mettl.com", hostName).replaceAll("mettl.xyz", hostName).replaceAll("mettl.info", hostName).replaceAll("mettl.online", hostName);
			if(value.contains("@")){
				return value.replaceAll("mettl.de", "mettl.com").replaceAll("mettl.xyz", "mettl.com").replaceAll("mettl.info", "mettl.com").replaceAll("mettl.online", "mettl.com");
			}

		} catch (Exception e) {

		}
		return value;
	}

	public String getTestData(String key) {
		return (String) testMethodData.get(key);

	}

	@AfterClass(alwaysRun = true)
	public void tearDownAfterClass() throws Exception, Throwable {
		try {
			browser.quit();
		} catch (Throwable e) {
			fail("Exception Occured - " + e.getMessage(), e);
		}
	}

	protected void _logMainConfigProperties() {
		Reporter.log("Test Suite Initialized with the following properties");
		Reporter.log("****************************************************");
		Set<String> keys = MettlContext.SINGLETON.getEntryKeys();
		for (String aKey : keys) {
			String str = "* " + aKey + " -> " + MettlContext.SINGLETON.getEntry(aKey);
			Reporter.log(str, true);
		}
		Reporter.log("****************************************************");
	}

	/**
	 * Gets the Test Resource Image path
	 * 
	 * @param imageFileName
	 * @return
	 */
	public String getImageFilePath(String imageFileName) {
		return "src/test/resources/TestImages/" + imageFileName;
	}

	public String getDataFilePath(String dataFileName) {
		return "src/test/resources/TestData/" + dataFileName;
	}

	public String getExpectedScreenShot(String fileName) {
		return "src/test/resources/HtmlExpectedImages/" + fileName;
	}

	public String getActualScrrenShot(String fileName) {
		return "src/test/resources/HtmlActualImages/" + fileName;
	}

	public String getDataFromFile(String filePath) throws IOException {
		filePath = Files.toString(new File(filePath), Charsets.UTF_8);
		// filePath = filePath.replaceAll("\r|\n", "");
		return filePath;
	}

	public void clickonImageUsingSikuli(String imageName) {
		try {
			ScreenRegion s = new DesktopScreenRegion();
			Target target = new ImageTarget(new File(getImageFilePath(imageName)));
			ScreenRegion r = s.wait(target, 200000);
			if (r == null) {
				//system.out.println("not found");
			} else {
				Mouse mouse = new DesktopMouse();
				mouse.click(r.getCenter());
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clickonImageUsingSikuliOnLeft(String imageName) {
		try {
			ScreenRegion s = new DesktopScreenRegion();
			Target target = new ImageTarget(new File(getImageFilePath(imageName)));
			ScreenRegion r = s.wait(target, 80000);
			if (r == null) {
				//system.out.println("not found");
			} else {
				Mouse mouse = new DesktopMouse();
				mouse.click(r.getRelativeScreenLocation(30, 20));
				Thread.sleep(90000);
				s.removeState(target);

			}

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String readReportForVerification(String reportName) {
		String divSavedContent = FileHelper
				.readFromFile(FileHelper.class.getClassLoader().getResourceAsStream("TestReports/" + reportName));
		return divSavedContent;
	}

	public void renameFile() {

		File folder = new File("src/test/resources/PdfReports/");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (!file.getName().replaceAll("//", "").contains("coldplay"))
				continue;
			file.renameTo(new File("src/test/resources/PdfReports/Actual.pdf"));
		}

	}

	public void deleteFile(String fileName) {
		try {
			File file = new File(fileName);

			if (file.delete()) {
				//system.out.println(file.getName() + " is deleted!");
			} else {
				//system.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}