import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PrimeLending {
    private WebDriver driver;

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "Prime Lending")
    public void getFairway() throws InterruptedException{
        driver.get("https://www.primelending.com/find-branch/index/");

        Thread.sleep(5000);
        Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
        int index = 1075;
        List<String> branches= new ArrayList<String>();
        FileWriter pw = null;
        Actions builder = new Actions(driver);

        try {

            List<WebElement> resultList = driver.findElements(By.className("stateDrop"));
            for (int i =0; i<18; i++) {
                resultList.remove(0);
            }
            for (WebElement ele: resultList) {

                List<WebElement> states = ele.findElements(By.className("panel-title"));
                System.out.println("cities "+ states.size());
                /*for (WebElement e: states) {
                    WebElement id = e.findElement(By.tagName("a"));
                    System.out.println(id);
                    List<WebElement> eachState = driver.findElements(By.className("loanOfficerContactCard"));
                    System.out.println(eachState.size());
                    //driver.navigate().to(id);
                    //WebElement id = e.findElement(By.className("collapse"));
                    //System.out.println(id.getText());
                }*/
                List<WebElement> eachState = driver.findElements(By.className("loanOfficerContactCard"));
                for (WebElement el: eachState) {
                    List<WebElement> linkText = el.findElements(By.tagName("a"));
                    branches.add(linkText.get(2).getAttribute("href"));
                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            pw = new FileWriter("primeLending.csv", true);
            for (String e: branches) {
                try {
                    driver.navigate().to(e);
                    WebElement loPanel = driver.findElement(By.id("loPanel"));
                    List<WebElement> employees = loPanel.findElements(By.className("loBlock"));
                    String address = driver.findElement(By.className("branchInfo")).
                            findElement(By.tagName("a")).getText().replaceAll("\n", " ");
                    for (WebElement element1: employees) {
                        String name = element1.findElement(By.tagName("h2")).getText();
                        if (nameMap.containsKey(name)) {
                            continue;
                        }
                        nameMap.put(name, true);
                        pw.append("" + index++ + "," + name + ",");
                        String[] titleNmls = element1.findElement(By.tagName("p")).getText().split("\n");
                        String title = titleNmls[0];
                        pw.append(title + "," );
                        List<WebElement> contact = element1.findElements(By.className("col-xs-4"));
                        System.out.println(contact.size());
                        String tel = contact.get(1).findElement(By.tagName("a")).getAttribute("href").substring(4);
                        String email = contact.get(2).findElement(By.tagName("a")).getAttribute("href").substring(7);
                        pw.append(tel + "," + email + "," + "\""+ address + "\" \n");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                pw.flush();
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
