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

public class GuildMortgage {
    private WebDriver driver;

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "Guild Mortgage")
    public void getFairway() throws InterruptedException{
        driver.get("https://branches.guildmortgage.com/");

        Thread.sleep(5000);
        Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
        int index = 1;
        List<String> em= new ArrayList<String>();
        FileWriter pw = null;

        try {
            List<WebElement> resultList = driver.findElements(By.className("region-name"));
            List<String> stateList = new ArrayList<String>();
            for (WebElement state: resultList) {
                stateList.add(state.getAttribute("href"));
            }

            for (String ele: stateList) {
                driver.navigate().to(ele);
                //Thread.sleep(1000);

                List<WebElement> eachState = driver.findElements(By.className("map-list-item-cta"));
                List<String> branches = new ArrayList<String>();
                for (WebElement el: eachState) {
                    branches.add(el.getAttribute("href"));
                }
                for (String elem: branches) {
                    //driver.navigate().to(el.getAttribute("href"));

                    driver.navigate().to(elem);
                    //Thread.sleep(1000);

                    List<WebElement> employees = driver.findElements(By.cssSelector(".map-list-item-wrap[data-location-type='Loan Officer']"));
                    for (WebElement element1: employees){
                        em.add(element1.findElement(By.tagName("a")).getAttribute("href"));
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pw = new FileWriter("guildMortgage.csv", true);
            for (String e: em) {
                try {
                    driver.navigate().to(e);
                    String name = driver.findElement(By.className("lo-info-top")).findElement(By.tagName("h1")).getText();
                    if (nameMap.containsKey(name)) {
                        continue;
                    }
                    nameMap.put(name, true);
                    String title = driver.findElement(By.className("lo-info-top")).findElement(By.tagName("h4")).getText();
                    pw.append("" + index++ + "," + name + "," + title.replaceAll("\n", " ") + ",");
                    System.out.println(name);

                    List<WebElement> info = driver.findElements(By.className("lo-info-right"));
                    String address = info.get(1).getText();
                    String email = info.get(2).findElement(By.tagName("a")).getText();
                    pw.append(email + ",");

                    List<WebElement> phoneNumbers = driver.findElement(By.className("lo-phone-numbers")).findElements(By.className("lo-info-group"));

                    for (WebElement number : phoneNumbers) {
                        String type = number.findElement(By.className("lo-info-left-extended")).getText();
                        pw.append(type);
                        String cleaned = number.findElement(By.tagName("a")).getText();
                        pw.append(" " + cleaned + ",");
                    }

                    pw.append("\""+ address+"\"");
                    pw.append("\n");
                    //driver.navigate().back();
                    pw.flush();
                } catch (Exception ex) {
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
