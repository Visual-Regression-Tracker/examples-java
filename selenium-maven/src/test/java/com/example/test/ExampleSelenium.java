package com.example.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.visual_regression_tracker.sdk_java.IgnoreAreas;
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
import java.util.Collections;

public class ExampleSelenium {
    static VisualRegressionTracker vrt;
    static WebDriver driver;
    static VisualRegressionTrackerConfig config = VisualRegressionTrackerConfig.builder()
            .apiUrl("http://localhost:4200")
            .apiKey("0TK0P0NQP6MNFQQPTYYBN27JRAA5")
            .project("Default project")
            .branchName("master")
            .enableSoftAssert(true)
            .ciBuildId("some build id")
            .build();

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
                        .ignoreAreas(Collections.singletonList(
                                IgnoreAreas.builder()
                                        .x(10L)
                                        .y(10L)
                                        .width(100L)
                                        .height(200L)
                                        .build()
                        ))
                        .build());
    }
}
