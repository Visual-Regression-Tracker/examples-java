package com.example.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public class ExampleSelenium {
    static VisualRegressionTrackerConfig config = new VisualRegressionTrackerConfig(
            "http://localhost:4200",
            "Demo",
            "4G16TTD8E54Q6DN1YSXVD8YHSCH3",
            "master",
            true
    );
    static VisualRegressionTracker vrt;
    static WebDriver driver;

    @BeforeClass
    public static void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        vrt = new VisualRegressionTracker(config);
        vrt.start();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        driver.quit();
        vrt.stop();
    }

    @Test
    public void ExampleTest() throws IOException {
        driver.navigate().to("http://automationpractice.com/index.php");

        vrt.track(
                "Home page",
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

        driver.findElement(By.cssSelector(".product-container a")).click();

        vrt.track(
                "Product page",
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder()
                        .device("Macbook Pro")
                        .os("macOS Catalina")
                        .browser("Chrome")
                        .viewport("1240x1024")
                        .diffTollerancePercent(0.0f)
                        .build());
    }
}
