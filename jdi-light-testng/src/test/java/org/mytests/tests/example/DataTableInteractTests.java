package org.mytests.tests.example;

import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import org.mytests.tests.TestsInit;
import org.mytests.tests.testng.TestNGListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.*;

import java.io.IOException;

import static com.epam.jdi.light.driver.WebDriverFactory.getDriver;
import static org.mytests.tests.states.States.shouldBeLoggedIn;
import static org.mytests.uiobjects.example.site.SiteJdi.usersPage;

@Listeners(TestNGListener.class)
public class DataTableInteractTests implements TestsInit {
    static VisualRegressionTracker vrt;
    static VisualRegressionTrackerConfig config = VisualRegressionTrackerConfig.builder()
            .apiUrl("http://localhost:4200")
            .apiKey("5H90NFWM6BMWWDMWKG8T11DWW22Y")
            .project("cb537710-9d84-4318-b450-2953c5d98361")
            .branchName("master")
            .enableSoftAssert(false)
            .build();

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        vrt = new VisualRegressionTracker(config);
        vrt.start();
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
        vrt.stop();
    }

    @BeforeMethod
    public void before() {
        shouldBeLoggedIn();
        usersPage.shouldBeOpened();
    }

    @Test
    public void shouldUsersPageLookSame() throws IOException, InterruptedException {
        vrt.track("Users page",
                ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64));
    }
}
