package thirdScenario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Testdata {

	static boolean switchToWindow(WebDriver driver, String title) {
		boolean flag = false;

		Set<String> iterate = driver.getWindowHandles();

		int numhandles = iterate.size();

		Object str[] = iterate.toArray();

		for (int i = 0; i < numhandles; i++) {
			System.out.println("windows" + i + "  name" + str[i]);

			String handle = (String) str[i];

			driver.switchTo().window(handle);

			String strTitle = driver.getTitle();
			if (strTitle.contains(title)) {
				System.out.println("we are on correct window");
				flag = true;
				break;
			}

		}
		return flag;

	}

	public static WebDriver Drivers(String browserName) {
		WebDriver driver;
		browserName = browserName.toUpperCase();

		switch (browserName) {

		case "CHROME":

			System.setProperty("webdriver.chrome.driver", "D:\\Softwares\\ChromeDriver\\chromedriver.exe");
			driver = new ChromeDriver();
			break;

		case "FIREFOX":
			System.setProperty("webdriver.gecko.driver", "D:\\Softwares\\geckodriver\\geckodriver.exe");
			driver = new FirefoxDriver();
			break;

		/*
		 * Not able to automate on IE browser case "IE":
		 * System.setProperty("webdriver.ie.driver",
		 * "D:\\Softwares\\IEDriverServer.exe"); driver = new EdgeDriver(); break;
		 */
		default:
			// create chrome by default
			System.setProperty("webdriver.chrome.driver", "D:\\Softwares\\ChromeDriver\\chromedriver.exe");
			driver = new ChromeDriver();
			break;
		}

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		return driver;
	}

	public static WebDriver createAppropriateDriver(String browserName, boolean headless) {
		WebDriver driver;
		ChromeOptions chromeOptions = new ChromeOptions();

		browserName = browserName.toUpperCase();

		switch (browserName) {

		case "CHROME":

			chromeOptions = new ChromeOptions();
			System.setProperty("webdriver.chrome.driver", "D:\\Softwares\\ChromeDriver\\chromedriver.exe");

			chromeOptions.setHeadless(headless);

			driver = new ChromeDriver(chromeOptions);
			break;

		case "FIREFOX":
			FirefoxOptions ffOptions = new FirefoxOptions();
			System.setProperty("webdriver.gecko.driver", "D:\\Softwares\\geckodriver\\geckodriver.exe");
			ffOptions.setHeadless(headless);
			driver = new FirefoxDriver(ffOptions);
			break;
		/*
		 * Not able to automate on IE browser case "IE":
		 * 
		 * System.setProperty("webdriver.edge.driver",
		 * "src\\test\\resources\\drivers\\MicrosoftWebDriver.exe");
		 * 
		 * break;
		 */
		default:
			// create chrome by default
			chromeOptions = new ChromeOptions();
			System.setProperty("webdriver.chrome.driver", "D:\\Softwares\\ChromeDriver\\chromedriver.exe");
			chromeOptions.setHeadless(headless);

			driver = new ChromeDriver(chromeOptions);
			break;
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		return driver;
	}

	public static String getBrowserName(WebDriver driver) {

		String strBrowserName = null;

		Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
		strBrowserName = cap.getBrowserName().toLowerCase();
		System.out.println(strBrowserName);
		String os = cap.getPlatform().toString();
		System.out.println(os);
		String v = cap.getVersion();
		System.out.println(v);
		return strBrowserName;
	}

	public static void sleep(int arg0) {

		try {
			Thread.sleep(arg0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static int waitTimeinSec = 10;

	public static boolean elementClick(WebDriver driver, By byObject) {

		WebDriverWait wait = new WebDriverWait(driver, waitTimeinSec);

		try {
			wait.until(ExpectedConditions.elementToBeClickable(byObject));
			// continue even if the page is not loaded
			driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
			driver.findElement(byObject).click();
			System.out.println("Clicked on " + byObject);
			return true;

		} catch (Exception e) {
			System.out.println("Not able to click on " + byObject);
			System.out.println(e.getMessage());
			return false;
		}
	}

	static public List<WebElement> getElementList(WebDriver driver, By byObj) {

		List<WebElement> listObj = null;
		WebDriverWait wait = new WebDriverWait(driver, waitTimeinSec);
		// wait for elements to be visible
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(byObj));

		listObj = driver.findElements(byObj);

		if (listObj != null) {
			System.out.println("list size = " + listObj.size());
			System.out.println(listObj.get(0).getText());
		}
		return listObj;
	}

}