import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.visual_regression_tracker.sdk_java.IgnoreAreas;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

import static com.codeborne.selenide.Selenide.$;

public class SelenideExample {
    VisualRegressionTrackerConfig config = VisualRegressionTrackerConfig.builder()
            .apiUrl("http://localhost:4200")
            .apiKey("0TK0P0NQP6MNFQQPTYYBN27JRAA5")
            .project("Default project")
            .branchName("master")
            .enableSoftAssert(true)
            .ciBuildId("some build id")
            .build();

    VisualRegressionTracker vrt = new VisualRegressionTracker(config);

    @BeforeSuite
    public void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        Configuration.baseUrl = "http://automationpractice.com/index.php";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1240x1024";

        vrt.start();
    }

    @AfterSuite
    public void tearDown() throws IOException {
        WebDriverRunner.closeWebDriver();
        vrt.stop();
    }

    @Test
    public void testExample() throws IOException {
        Selenide.open("/");

        vrt.track(
                "Home page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64));

        $(".product-container").click();

        vrt.track(
                "Product page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64),
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
