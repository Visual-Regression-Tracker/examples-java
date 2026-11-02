import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.visual_regression_tracker.sdk_java.IgnoreAreas;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.openqa.selenium.OutputType;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class SelenideExample {
    VisualRegressionTracker vrt = new VisualRegressionTracker(vrtConfig());

    @BeforeSuite
    public void setUp() throws IOException, InterruptedException {
        Configuration.baseUrl = "https://the-internet.herokuapp.com";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1280x1024";
        Configuration.headless = true;

        vrt.start();
    }

    @AfterSuite
    public void tearDown() throws IOException, InterruptedException {
        Selenide.closeWebDriver();
        vrt.stop();
    }

    @Test
    public void testExample() throws IOException, InterruptedException {
        Selenide.open("/");

        vrt.track("Home page", Selenide.screenshot(OutputType.BASE64));

        $("a[href='/checkboxes']").click();

        vrt.track(
                "Checkboxes page",
                Selenide.screenshot(OutputType.BASE64),
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

    static VisualRegressionTrackerConfig vrtConfig() {
        VisualRegressionTrackerConfig.VisualRegressionTrackerConfigBuilder builder = VisualRegressionTrackerConfig.builder();
        File configFile = new File("vrt.json");
        if (configFile.exists()) {
            builder.configFile(configFile);
        }
        return builder.build();
    }
}
