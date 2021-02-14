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

public class Fairway {
    private WebDriver driver;

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "Fairway Independent MC")
    public void getFairway() throws InterruptedException{
        driver.get("https://www.fairwayindependentmc.com/locations");

        Thread.sleep(5000);
        Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
        int index = 3161;
        List<String> em= new ArrayList<String>();
        FileWriter pw = null;

        try {
            List<WebElement> resultList = driver.findElements(By.className("us-state-text"));
            List<String> stateList = new ArrayList<String>();
            for (WebElement state: resultList) {
                stateList.add(state.getAttribute("href"));
            }
            for (int i = 1; i< 44; i++) {
               stateList.remove(0);
            }
            for (String ele: stateList) {
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
                        em.add(element1.findElement(By.tagName("a")).getAttribute("href"));
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pw = new FileWriter("fairway.csv", true);
            for (String e: em) {
                try {
                    driver.navigate().to(e);
                    Thread.sleep(1000);
                    String name = driver.findElement(By.className("officer-name")).findElement(By.tagName("h1")).getText();
                    if (nameMap.containsKey(name)) {
                        continue;
                    }
                    nameMap.put(name, true);
                    String title = driver.findElement(By.className("officer-name")).findElement(By.tagName("p")).getText();
                    pw.append("" + index++ + "," + name + "," + title + ",");
                    System.out.println(name);
                    WebElement officer = driver.findElement(By.className("mobile-bio-info"));
                    List<WebElement> info = officer.findElements(By.tagName("p"));
                    for (WebElement contact : info) {
                        String cleaned = contact.getAttribute("innerText").replaceAll("[^\\p{ASCII}]", " ");
                        cleaned = cleaned.replaceAll("\n", " ");
                        pw.append("\"" + cleaned + "\"");
                        pw.append(",");
                    }
                    pw.append("\n");
                        //driver.navigate().back();
                    pw.flush();
                } catch (Exception ex) {
                    driver.navigate().to(e);
                    String name = driver.findElement(By.tagName("title")).getAttribute("innerText").replaceAll("\n", "");
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
