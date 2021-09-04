import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.visual_regression_tracker.sdk_java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Objects;

public class IOSBasicInteractionsTest extends BaseTest {
    String platformName = "iOS";
    String deviceName = "iPhone 12";
    String platformVersion = "14.5";
    private IOSDriver<WebElement> driver;
    private VisualRegressionTracker vrt;

    @BeforeTest
    public void setUp() throws IOException, InterruptedException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", deviceName);
        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("platformVersion", platformVersion);
        String appPath = Objects.requireNonNull(getClass().getClassLoader().getResource("App.zip")).getPath();
        capabilities.setCapability("app", appPath);
        capabilities.setCapability("automationName", "XCUITest");

        driver = new IOSDriver<>(getServiceUrl(), capabilities);

        VisualRegressionTrackerConfig vrtConfig = VisualRegressionTrackerConfig.builder()
                .apiUrl("http://localhost:4200")
                .apiKey("11Q288KSQKMDJGMH3RVY9N7Y0FJ3")
                .project("9cb1ff4d-5675-4c85-a41b-8e08dbf6d5f6")
                .branchName("master")
                .enableSoftAssert(true)
                .build();

        vrt = new VisualRegressionTracker(vrtConfig);
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
                {"Home page", MobileBy.AccessibilityId("Home")},
                {"Web page", MobileBy.AccessibilityId("Webview")},
                {"Login page", MobileBy.iOSClassChain("**/XCUIElementTypeButton[`label == \"Login\"`]")},
                {"Forms page", MobileBy.AccessibilityId("Forms")},
                {"Swipe page", MobileBy.AccessibilityId("Swipe")},
                {"Drag page", MobileBy.AccessibilityId("Drag")},

        };
    }

    @Test(dataProvider = "menu-buttons")
    public void shouldMatchScreenshot(String pageName, MobileBy locator) throws Exception {

        IOSElement button = (IOSElement) new WebDriverWait(driver, 30)
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
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64),
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
