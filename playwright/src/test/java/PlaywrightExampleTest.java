import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.visual_regression_tracker.sdk_java.IgnoreAreas;
import io.visual_regression_tracker.sdk_java.TestRunOptions;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class PlaywrightExampleTest {
    static VisualRegressionTracker vrt = new VisualRegressionTracker(
            VisualRegressionTrackerConfig.builder()
                    .apiUrl("http://localhost:4200")
                    .apiKey("DEFAULTUSERAPIKEYTOBECHANGED")
                    .project("Default project")
                    .branchName("master")
                    .enableSoftAssert(true)
                    .build());
    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage(new Browser.NewPageOptions().setViewportSize(800, 600));
        vrt.start();
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
        playwright.close();
        vrt.stop();
    }

    @Test
    public void example() throws IOException, InterruptedException {
        page.navigate("https://the-internet.herokuapp.com/");

        vrt.track("Home page", Base64.getEncoder().encodeToString(page.screenshot()));

        page.locator("a[href='/checkboxes']").click();

        vrt.track(
                "Checkboxes page",
                Base64.getEncoder().encodeToString(page.screenshot()),
                TestRunOptions.builder()
                        .device("PC")
                        .os(System.getProperty("os.name"))
                        .browser("Chromium")
                        .viewport("800x600")
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
