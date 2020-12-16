import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
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
import java.util.Collections;

public class PlaywrightExample {
    static VisualRegressionTracker vrt = new VisualRegressionTracker(
            VisualRegressionTrackerConfig.builder()
                    .apiUrl("http://localhost:4200")
                    .apiKey("0TK0P0NQP6MNFQQPTYYBN27JRAA5")
                    .project("Default project")
                    .branchName("master")
                    .enableSoftAssert(true)
                    .build());
    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeClass
    public static void setUp() throws IOException {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().withHeadless(false)
        );
        page = browser.newContext(
                new Browser.NewContextOptions().withViewport(800, 600)
        ).newPage();
        vrt.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        browser.close();
        playwright.close();
        vrt.stop();
    }

    @Test
    public void example() throws IOException {
        page.navigate("http://automationpractice.com/index.php");

        vrt.track("Home page", Base64.getEncoder().encodeToString(page.screenshot()));

        page.click(".product-container a img");

        vrt.track(
                "Product page",
                Base64.getEncoder().encodeToString(page.screenshot()),
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
