import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoanDepot {
    private WebDriver driver;

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "Home Depot")
    public void getHomeDepot() throws InterruptedException {
        driver.get("https://www.loandepot.com/loan-officers");

        Thread.sleep(1000);

        Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
        int index = 1;

        for (char alphabet = 'A'; alphabet <='Z'; alphabet ++ ) {
            WebElement searchBox = driver.findElement(By.id("loan-officer-search-input"));
            searchBox.clear();
            searchBox.sendKeys(alphabet + "");
            searchBox.submit();
            Thread.sleep(5000);

            FileWriter pw = null;

            while (true) {
                try {
                    String currentActivepage = driver.findElement(By.className("active")).getText();
                    List<WebElement> elements = driver.findElements(By.className("card-block"));
                    pw = new FileWriter("loan-depot.csv", true);
                    for (WebElement element : elements) {
                        String name = element.findElement(By.className("lo-card-name")).getText();
                        if (nameMap.containsKey(name)) {
                            continue;
                        }
                        nameMap.put(name, true);
                        String address = "";

                        String []contact = element.findElement(By.className("lo-card-contact")).getText().split("\n");
                        try {
                            address = element.findElement(By.tagName("address")).getText().replaceAll("\n", " ");
                        } catch (NoSuchElementException ex) {
                            System.out.println("No address for " + name);
                        }
                        System.out.println(name + "," + contact[0] + "," + contact[1] + "," + address);
                        pw.append("" + index++);
                        pw.append(",");
                        pw.append(name);
                        pw.append(",");
                        pw.append(contact[0]);
                        pw.append(",");
                        pw.append(contact[1]);
                        pw.append(",");
                        pw.append("\"").append(address).append("\"");
                        pw.append("\n");
                    }
                    pw.flush();
                    pw.close();
                    WebElement nextPageLink = driver.findElement(By.className("page-link-next"));
                    nextPageLink.click();
                    Thread.sleep(4000);

                    String newActivepage = driver.findElement(By.className("active")).getText();

                    if ( newActivepage.equalsIgnoreCase(currentActivepage)) {
                        break;
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @AfterClass
    public void cleanup(){
//        if(driver !=null)
//            driver.quit();
    }



}