import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;

public class SelenideExample {
    VisualRegressionTracker visualRegressionTracker;
    VisualRegressionTrackerConfig config = new VisualRegressionTrackerConfig(
            "http://localhost:4200", // replace with your data
            "Default project", // replace with your data
            "CPKVK4JNK24NVNPNGVFQ853HXXEG", // replace with your data
            "master", // replace with your data
            true
    );

    @BeforeSuite
    public void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        Configuration.baseUrl = "https://google.com";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1200x800";

        visualRegressionTracker = new VisualRegressionTracker(config);
        visualRegressionTracker.start();
    }

    @AfterSuite
    public void tearDown() throws IOException {
        WebDriverRunner.closeWebDriver();
        visualRegressionTracker.stop();
    }

    @Test
    public void testExample() throws IOException {
        Selenide.open("/");

        visualRegressionTracker.track(
                "Home page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder()
                        .build()
        );

        $("[name='q']")
                .setValue("Visual Regression tracker")
                .pressEnter();

        visualRegressionTracker.track(
                "Search result page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder()
                        .browser("Chrome")
                        .os("Windows")
                        .viewport("1200x800")
                        .diffTollerancePercent(0.0f)
                        .build()
        );
    }
}
