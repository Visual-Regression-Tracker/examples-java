package io.qameta.allure.examples.junit5;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertSame;

public class AllureImageDiffTest {

    static VisualRegressionTrackerConfig config = new VisualRegressionTrackerConfig(
            "http://localhost:4200",
            "Demo",
            "4G16TTD8E54Q6DN1YSXVD8YHSCH3",
            "master",
            true
    );
    static VisualRegressionTracker vrt = new VisualRegressionTracker(config);

    @BeforeAll
    static void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        Configuration.baseUrl = "http://automationpractice.com/index.php";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1240x1024";

        vrt.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        WebDriverRunner.closeWebDriver();
        vrt.stop();
    }

    @Step
    @Attachment(value = "{filename}", type = "image/png")
    public static byte[] attachScreenshotToAllureReport(String href, String filename) {
        BufferedImage image;
        byte[] imageInByte = new byte[0];

        try {
            URL url = new URL(href);
            image = ImageIO.read(url);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return imageInByte;
    }

    @Test
    @DisplayName("imageDiffExample displayName")
    public void imageDiffExample() throws IOException {
        Selenide.open("/");

        TestRunResult result = vrt.track(
                "Home page",
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BASE64));

        Allure.label("testType", "screenshotDiff");
        attachScreenshotToAllureReport(result.getImageUrl(), "actual");
        attachScreenshotToAllureReport(result.getBaselineUrl(), "expected");
        attachScreenshotToAllureReport(result.getDiffUrl(), "diff");
        assertSame(result.getTestRunResponse().getStatus(), TestRunStatus.OK);
    }
}
