import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GuaranteedRate {
    private WebDriver driver;

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "GuaranteedRate")
    public void getFairway() throws InterruptedException{
        driver.get("https://www.rate.com/find-loan-officer");

        Thread.sleep(5000);
        Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
        int index = 1;
        List<String> lo= new ArrayList<String>();
        FileWriter pw = null;
        for (char alphabet = 'A'; alphabet <='Z'; alphabet ++ ) {
            WebElement inputField = driver.findElement(By.className("heroInput")).findElement(By.tagName("input"));
            inputField.clear();
            inputField.sendKeys(alphabet + "");
            //inputField.submit();
            Thread.sleep(5000);
            try {
                List<WebElement> resultList = driver.findElements(By.className("vpMatch"));
                for (WebElement element: resultList) {
                    lo.add(element.findElement(By.tagName("a")).getAttribute("href"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


            /*for (String ele: stateList) {
                driver.navigate().to(ele);
                //Thread.sleep(1000);

                List<WebElement> eachState = driver.findElement(By.className("branch-locations-cnt")).findElements(By.tagName("a"));
                List<String> branches = new ArrayList<String>();
                for (WebElement el: eachState) {
                    branches.add(el.getAttribute("href"));
                }
                for (String elem: branches) {
                    //driver.navigate().to(el.getAttribute("href"));

                    driver.navigate().to(elem);
                    //Thread.sleep(1000);

                    List<WebElement> employees = driver.findElements(By.className("wrapper-nobg"));

                    for (WebElement element1: employees){
                        if (element1.findElement(By.tagName("h5")).getText() == "Loan Officer") {
                            em.add(element1.findElement(By.tagName("a")).getAttribute("href"));
                        }
                    }

                }

            }*/


        try {
            pw = new FileWriter("guaranteedRate.csv", true);
            for (String e: lo) {
                try {
                    driver.navigate().to(e);
                    Thread.sleep(1000);

                    String name = driver.findElement(By.className("name")).getText();
                    if (nameMap.containsKey(name)) {
                        continue;
                    }
                    nameMap.put(name, true);

                    pw.append("" + index++);
                    pw.append(",");
                    pw.append(name.replaceAll("\n", "") + ",");
                    System.out.println(name);
                    String title = driver.findElement(By.className("heroTitle")).getText();
                    pw.append(title + ",");

                    String []contact = driver.findElement(By.className("addressContainer")).getText().split("\n");
                    pw.append(contact[1] + ",\"" + contact[2] + "\"" +  ",\n");
                    pw.flush();

                } catch (Exception ex) {
                    driver.navigate().to(e);
                    String name = driver.findElement(By.tagName("title")).getAttribute("innerText");
                    pw.append("" + index++ + "," + name + "," + e + ",\n");
                    System.out.println(name);
                    ex.printStackTrace();
                }


            }
            pw.close();
        } catch (Exception exep){
            exep.printStackTrace();
        }


    }

    @AfterClass
    public void cleanup(){
        if(driver !=null)
            driver.quit();
    }
}
