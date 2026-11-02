package com.example.test;

import io.visual_regression_tracker.sdk_java.IgnoreAreas;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.List;

public class SeleniumExampleTest {
    static VisualRegressionTracker vrt;
    static ChromeDriver driver;
    static VisualRegressionTrackerConfig config = VisualRegressionTrackerConfig.builder()
            .apiUrl("http://localhost:4200")
            .apiKey("DEFAULTUSERAPIKEYTOBECHANGED")
            .project("Default project")
            .branchName("master")
            .enableSoftAssert(true)
            .build();

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
        driver.manage().window().setSize(new Dimension(1280, 1024));
        vrt = new VisualRegressionTracker(config);
        vrt.start();
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
        driver.quit();
        vrt.stop();
    }

    @Test
    public void exampleTest() throws IOException, InterruptedException {
        driver.navigate().to("https://the-internet.herokuapp.com/");

        vrt.track("Home page", driver.getScreenshotAs(OutputType.BASE64));

        driver.findElement(By.linkText("Checkboxes")).click();

        vrt.track(
                "Checkboxes page",
                driver.getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder()
                        .device("PC")
                        .os(System.getProperty("os.name"))
                        .browser("Chrome")
                        .viewport("1280x1024")
                        .diffTollerancePercent(0.0f)
                        .ignoreAreas(List.of(
                                IgnoreAreas.builder()
                                        .x(10L)
                                        .y(10L)
                                        .width(100L)
                                        .height(200L)
                                        .build()))
                        .build());
    }
}
