package io.qameta.allure.examples.junit5;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.visual_regression_tracker.sdk_java.TestRunResult;
import io.visual_regression_tracker.sdk_java.TestRunStatus;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllureImageDiffTest {

    static VisualRegressionTrackerConfig config = VisualRegressionTrackerConfig.builder()
            .apiUrl("http://localhost:4200")
            .apiKey("DEFAULTUSERAPIKEYTOBECHANGED")
            .project("Default project")
            .branchName("master")
            .enableSoftAssert(true)
            .build();
    static VisualRegressionTracker vrt = new VisualRegressionTracker(config);

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        Configuration.baseUrl = "https://the-internet.herokuapp.com";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1280x1024";
        Configuration.headless = true;

        vrt.start();
    }

    @AfterAll
    static void tearDown() throws IOException, InterruptedException {
        Selenide.closeWebDriver();
        vrt.stop();
    }

    @Step
    @Attachment(value = "{filename}", type = "image/png")
    public static byte[] attachScreenshotToAllureReport(String href, String filename) {
        if (href == null) {
            return new byte[0];
        }
        try (InputStream image = URI.create(href).toURL().openStream()) {
            return image.readAllBytes();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Test
    @DisplayName("imageDiffExample displayName")
    public void imageDiffExample() throws IOException, InterruptedException {
        Selenide.open("/");

        TestRunResult result = vrt.track("Home page", Selenide.screenshot(OutputType.BASE64));

        Allure.label("testType", "screenshotDiff");
        attachScreenshotToAllureReport(result.getImageUrl(), "actual");
        attachScreenshotToAllureReport(result.getBaselineUrl(), "expected");
        attachScreenshotToAllureReport(result.getDiffUrl(), "diff");
        assertEquals(TestRunStatus.OK, result.getTestRunResponse().getStatus());
    }
}
