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
    VisualRegressionTrackerConfig config = new VisualRegressionTrackerConfig(
            "http://localhost:4200",
            "Demo",
            "4G16TTD8E54Q6DN1YSXVD8YHSCH3",
            "master",
            true
    );
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
                        .build());
    }
}
