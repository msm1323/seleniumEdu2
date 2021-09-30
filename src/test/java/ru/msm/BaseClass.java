package ru.msm;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class BaseClass {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static long defaultWaitingTime;
    private static int browser = 2;

    @BeforeAll
    static void beforeAll(){
        switch (browser){
            case 1:
                System.setProperty("webdriver.ie.driver", "src/test/resources/IEDriverServer.exe");
                driver = new InternetExplorerDriver();
            case 2:
                System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
                driver = new ChromeDriver();
//            case 3:
//                System.setProperty("webdriver.gecko.driver","src/test/resources/geckodriver.exe");
//                driver = new FirefoxDriver();
        }

        driver.manage().window().maximize();

        defaultWaitingTime = 25;
        //driver.manage().timeouts().pageLoadTimeout(defaultWaitingTime, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(defaultWaitingTime, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, defaultWaitingTime, 1000);
    }

    @BeforeEach
    public void before() {
        String baseUrl = "https://www.rgs.ru/";
        //1. Перейти по ссылке http://www.rgs.ru
        driver.get(baseUrl);
    }

    @AfterAll
    static void afterAll() {
        driver.quit();
    }

}

