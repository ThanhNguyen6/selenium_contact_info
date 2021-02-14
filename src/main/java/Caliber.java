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

public class Caliber {
    private WebDriver driver;

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "Caliber Home Loans")
    public void getCaliber() throws InterruptedException{
        driver.get("https://caliberhomeloans.com/branch-locator");

        Thread.sleep(2000);
        Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
        int index = 1;
        List<String> urls= new ArrayList<String>();
        FileWriter pw = null;

        try {
            pw = new FileWriter("caliber.csv", true);
            WebElement selection = driver.findElement(By.className("stateSelector"));
            List<WebElement> allOptions = selection.findElements(By.tagName("option"));
            //allOptions.remove(0);

            for (int i = 1; i < allOptions.size(); i++) {
                WebElement select= allOptions.get(i);
                select.click();
                //System.out.println("test " + i);
                List<WebElement> branches = driver.findElements(By.className("locationDetails"));
                for (WebElement el: branches) {
                    String url = el.getAttribute("onClick").substring(22);
                    url = "https://caliberhomeloans.com/"+ url;
                    url = url.substring(0, url.length() - 1);
                    urls.add(url);
                    System.out.println(url);
                }
                driver.get("https://caliberhomeloans.com/branch-locator");
                selection = driver.findElement(By.className("stateSelector"));
                allOptions = selection.findElements(By.tagName("option"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pw = new FileWriter("caliber.csv", true);
            for (String e: urls) {
                try {
                    driver.navigate().to(e);
                    List<WebElement> members = driver.findElements(By.className("memberListItem"));
                    for (WebElement mem: members) {
                        String[] nameInfo = driver.findElement(By.className("memberListName")).getText().split("\n");
                        String name = nameInfo[0];
                        if (nameMap.containsKey(name)) {
                            continue;
                        }
                        nameMap.put(name, true);
                        String title = nameInfo[1];
                        pw.append("" + index++ + "," + name + "," + title.replaceAll("\n", " ") + ",");

                        List<WebElement> list = driver.findElement(By.className("memberListButtons")).findElements(By.tagName("a"));
                        String email = list.get(1).getAttribute("href").substring(7);
                        pw.append(email + ",");

                        String[] phoneNumbers = driver.findElement(By.className("memberListContact")).getAttribute("innerText").split("\n");

                        for (int i = 0; i < phoneNumbers.length; i++) {
                            pw.append(" " + phoneNumbers[i] + ",");

                        }
                        WebElement branch = driver.findElement(By.id("dragLayoutsHere_ctl11_ctl00_ctl00_detailContainer"));
                        List<WebElement> tagAs = branch.findElements(By.tagName("a"));
                        String address = tagAs.get(3).getAttribute("innerText");
                        pw.append("\""+ address+"\"\n");
                        pw.flush();
                    }

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
