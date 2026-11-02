import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.visual_regression_tracker.sdk_java.*;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class IOSBasicInteractionsTest extends BaseTest {
    String platformName = "iOS";
    String deviceName = "iPhone 12";
    String platformVersion = "14.5";
    private IOSDriver driver;
    private VisualRegressionTracker vrt;

    @BeforeTest
    public void setUp() throws IOException, InterruptedException {
        String appPath = Objects.requireNonNull(getClass().getClassLoader().getResource("App.zip")).getPath();
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(deviceName)
                .setPlatformVersion(platformVersion)
                .setApp(appPath);

        driver = new IOSDriver(getServiceUrl(), options);

        vrt = new VisualRegressionTracker();
        vrt.start();
    }

    @AfterTest
    public void tearDown() throws IOException, InterruptedException {
        if (driver != null) {
            driver.quit();
        }
        vrt.stop();
    }

    @DataProvider(name = "menu-buttons")
    public Object[][] getMenuButtons() {
        return new Object[][]{
                {"Home page", AppiumBy.accessibilityId("Home")},
                {"Web page", AppiumBy.accessibilityId("Webview")},
                {"Login page", AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == \"Login\"`]")},
                {"Forms page", AppiumBy.accessibilityId("Forms")},
                {"Swipe page", AppiumBy.accessibilityId("Swipe")},
                {"Drag page", AppiumBy.accessibilityId("Drag")},

        };
    }

    @Test(dataProvider = "menu-buttons")
    public void shouldMatchScreenshot(String pageName, By locator) throws Exception {

        WebElement button = new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));

        button.click();

        vrtTrackWithRetry(
                pageName,
                TestRunOptions.builder()
                        .device(deviceName)
                        .os(platformName)
                        .build(),
                2
        );
    }

    void vrtTrackWithRetry(String pageName, TestRunOptions testRunOptions, int maxRetries) throws Exception {
        TestRunResult result = null;
        Exception exception = null;

        try {
            result = vrt.track(pageName,
                    driver.getScreenshotAs(OutputType.BASE64),
                    testRunOptions);
        } catch (Exception ex) {
            exception = ex;
        }
        if (maxRetries <= 0 || (Objects.nonNull(result) && !result.getTestRunResponse().getStatus().equals(TestRunStatus.UNRESOLVED))
        ) {
            if (Objects.nonNull(exception)) {
                throw exception;
            } else {
                return;
            }
        }

        vrtTrackWithRetry(pageName, testRunOptions, maxRetries - 1);
    }
}
