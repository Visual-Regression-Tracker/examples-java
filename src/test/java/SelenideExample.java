import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.visual_regression_tracker.sdk_java.Config;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;

public class SelenideExample {
    VisualRegressionTracker visualRegressionTracker;
    Config config = new Config(
            "http://localhost:4200", // replace with your data
            "31d57436-0924-4e19-86ca-9c5fd3bf32eb", // replace with your data
            "BAZ0EG0PRH4CRQPH19ZKAVADBP9E", // replace with your data
            "develop" // replace with your data
    );

    @BeforeSuite
    public void setUp() {
        visualRegressionTracker = new VisualRegressionTracker(config);

        WebDriverManager.chromedriver().setup();
        Configuration.baseUrl = "https://google.com";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1200x800";
    }

    @AfterSuite
    public void tearDown() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void testExample() throws IOException {
        Selenide.open("/");
        visualRegressionTracker.track(
                "Home page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder()
                        .diffTollerancePercent(0)
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
                        .diffTollerancePercent(1)
                        .build()
        );
    }
}
