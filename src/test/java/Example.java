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

public class Example {
    VisualRegressionTracker visualRegressionTracker;
    Config config = new Config(
            "http://localhost:4200",
            "003f5fcf-6c5f-4f1f-a99f-82a697711382",
            "F5Z2H0H2SNMXZVHX0EA4YQM1MGDD",
            "develop"
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
                TestRunOptions.builder().build()
        );


        $("[name='q']")
                .setValue("Visual Regression tracker")
                .pressEnter();

        visualRegressionTracker.track(
                "Search result page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder().build()
        );
    }
}
