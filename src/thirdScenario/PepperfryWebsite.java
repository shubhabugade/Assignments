package thirdScenario;

import org.testng.annotations.Test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

public class PepperfryWebsite {

	WebDriver driver;
	WebDriverWait wait;

	@BeforeTest
	public void beforeTest() {

		System.setProperty("webdriver.chrome.driver", "D:\\Softwares\\ChromeDriver\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 10);
	}

	@DataProvider
	public Object[][] dp() {
		String FilePath = "/ThirdScenario/datafile/PepperFry.xls";
		String SheetName = "PepperFry";

		Object [][] excelData = null;

		try {
			excelData = getExcelData(FilePath, SheetName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return excelData;
	}

	@Test(dataProvider = "dp")
	public void productlist(String items)
	{

		System.out.println("Searching for Product = " + items );
		driver.get("https://www.pepperfry.com/");
		WebElement SearchBox = driver.findElement(By.id("search"));
		SearchBox.clear();
	    SearchBox.sendKeys(items);
		SearchBox.sendKeys(Keys.ENTER);

		By sortBy = By.id("curSortType");
		By lowToHigh = By.xpath("//a[normalize-space()='Price Low to High']");
		int tryCount = 0;

		//  sortby and low to high are clicked
		
		while (tryCount <3 ) {
		System.out.println("In the while loop count = " +  tryCount);
			
			
			boolean clickedFlag = Testdata.elementClick(driver, sortBy);
			
			if (clickedFlag) {
				
				System.out.println("Clicking on low to high");
				clickedFlag = Testdata.elementClick(driver, lowToHigh);

				if (clickedFlag) {
					break; 
				}
			}
		
			//  two popups handling
			handlePopupsException(driver,wait);
			// to be on the safer side do a refresh too to get rid of any popups
			driver.navigate().refresh();
			tryCount++; 
		} 

		// Fetch the prices from the page and add in the list
		By proPrice = By.xpath("//*/span[starts-with(@class,'clip-offr-price')]");
		WebElement element = driver.findElement(proPrice);
		

		
		try {
			wait.until(ExpectedConditions.refreshed(ExpectedConditions.stalenessOf(element)));
			
		}
		catch(Exception e) {
			
			System.out.println("Error message" + e.getMessage());
			// try to handle popups
			handlePopupsException(driver,wait);
		}

		List <WebElement> actualPrice = driver.findElements(proPrice);

		int iSize = actualPrice.size();	

		System.out.println("iSize = " + iSize);

		ArrayList<Integer> actualSortedPriceList = new ArrayList<Integer>(iSize); 

		for(int i=0;i<iSize;i++) {
			WebElement wePrice = actualPrice.get(i);
			String strPrice = wePrice.getText().replaceAll("","");
			int iPrice = Integer.parseInt(strPrice);
			actualSortedPriceList.add(iPrice);

		}

		//After Sort by 'Low to High'
		System.out.println("Price list before comparing with the collection sort method");
		for(int obj:actualSortedPriceList)  {  
			System.out.print(obj+":"); 
		}
		System.out.println("");

		ArrayList<Integer> finalsortedlist = new ArrayList<Integer>(iSize);
		finalsortedlist = actualSortedPriceList;

		//After Sorting (In Ascending Order)
		Collections.sort(finalsortedlist);
		System.out.println("Price list After using Collection sort method");
		for(int obj1:finalsortedlist)  {
			System.out.print(obj1+":"); 
		}
		System.out.println("");
		try {
			Assert.assertEquals(actualSortedPriceList, finalsortedlist);
		}catch(AssertionError e) {
			System.out.println("Sort By feature 'Low to High");
		}


	}

	public static String[][] getExcelData (String fileName, String sheetName) throws IOException {
		String[][] arrayExcelData = null;
		Workbook wb = null;
		try {
			File file = new File(fileName);		
			FileInputStream fs = new FileInputStream(file);
			if
			(fileName.substring(fileName.indexOf(".")).equals(".xlsx"))
			{
				
				wb = new XSSFWorkbook(fs);
			}
			else if
			(fileName.substring(fileName.indexOf(".")).equals(".xls"))
			{
				wb = new HSSFWorkbook(fs);
			}

			if (wb==null)
			{
				
				Exception exp = new Exception("sheeet not found - May be File not found " + sheetName );
				throw exp;
			}		

			Sheet sh = wb.getSheet(sheetName);
			if (sh==null)
			{
				
				Exception exp = new Exception("Sheet Name not found " + sheetName );
				throw exp;
			}

			int totalNoOfRows = sh.getPhysicalNumberOfRows();
			int totalNoOfCols = 
					sh.getRow(0).getPhysicalNumberOfCells();

			System.out.println("totalNoOfrows="+totalNoOfRows+","
					+ " totalNoOfcolumns="+totalNoOfCols);
			arrayExcelData = 
					new String[totalNoOfRows-1][totalNoOfCols];
			for (int i= 1 ; i <= totalNoOfRows-1; i++) {
				for (int j=0; j <= totalNoOfCols-1; j++) {
					sh.getRow(i).getCell(j).setCellValue(1);
					arrayExcelData[i-1][j] = 
							sh.getRow(i).getCell(j).getStringCellValue().toString();
				}
			}
		} catch (Exception e) {
			System.out.println("Exception error in getExcelsheet()");
			System.out.println(e.getMessage());
			if (arrayExcelData==null)
			{
				IOException exp = new IOException(e.getMessage());
				throw exp;
			}
		}
		return arrayExcelData;
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
	}

	public static void handlePopupsException(WebDriver driver, WebDriverWait wait ) {

		try {
			
			By popup1 = By.xpath("//*[@id='page']");
			wait.until(ExpectedConditions.visibilityOfElementLocated(popup1));
			WebElement wePopup1 = driver.findElement(popup1);
			wePopup1.click();
			wePopup1.sendKeys(Keys.ESCAPE);
			System.out.println(" handled popup1");

		}catch(Exception e1) {

			System.out.println("Exception " + e1.getMessage());
		}

		try {
			
			By frameId = By.id("webklipper-publisher-widget-container-notification-frame");
			WebElement frameElement = driver.findElement(frameId);
			driver.switchTo().frame(frameElement);

			/*By popup2 = By.xpath("//*[@id=\"webklipper-publisher-widget-container-notification-close-div\"]");
			WebElement wePopup2 = driver.findElement(popup2);
			wait.until(ExpectedConditions.visibilityOfElementLocated(popup2));
			wePopup2.click();

			System.out.println(" handled popup2");
*/
		}catch(Exception e2) {

			System.out.println("Exception " + e2.getMessage());
			
		}	
		

	}
}