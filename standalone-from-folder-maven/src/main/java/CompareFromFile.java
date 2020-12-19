import io.visual_regression_tracker.sdk_java.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class CompareFromFile {
    public static void main(String[] args) {

        //Server identifier
        String apiUrl = "http://localhost:4200";
        String project = ""; // Populate your project
        String apiKey = ""; //Populate your key
        String branchName = "1.0.0";
        String ciBuildId = null;

        //Image file details
        String filePath = "/Users/test/Desktop/delete";//Put some image files in a folder and give the full path of the folder here
        String maskData_x = "0";
        String maskData_y = "0";
        String maskData_width = "200";
        String maskData_height = "20";
        String browser = "";
        String device = "Sample";
        String os = "";
        String viewport = "";

        VisualRegressionTrackerConfig config = new VisualRegressionTrackerConfig(apiUrl, project, apiKey, branchName, true, ciBuildId);
        VisualRegressionTracker visualRegressionTracker = new VisualRegressionTracker(config);
        Map<String, Object> results = new HashMap<>();
        results.put("allImageVerified", true);
        results.put("allPassed", true);
        results.put("imageVerifiedCount", 0);
        try {
            visualRegressionTracker.start();
            TestRunOptions.TestRunOptionsBuilder testRunOptionsBuilder = TestRunOptions.builder()
                    .diffTollerancePercent(0.0f)
                    .os(os)
                    .browser(browser)
                    .device(device)
                    .viewport(viewport);
            if (maskData_height != null && maskData_width != null) {
                testRunOptionsBuilder.ignoreAreas(Collections.singletonList(
                        IgnoreAreas.builder()
                                .x(Long.valueOf(maskData_x))
                                .y(Long.valueOf(maskData_y))
                                .width(Long.valueOf(maskData_width))
                                .height(Long.valueOf(maskData_height))
                                .build()
                ));
            }
            File file = new File(filePath);
            File[] fileList = file.listFiles();
            for (File eachFile : fileList) {
                String[] imageExtensions = {"png", "jpg", "jpeg"};
                if (Arrays.asList(imageExtensions).contains(FilenameUtils.getExtension(eachFile.getName()))) {
                    FileInputStream fileInputStreamReader = new FileInputStream(eachFile);
                    byte[] bytes = new byte[(int) eachFile.length()];
                    fileInputStreamReader.read(bytes);
                    String encodedBase64 = new String(Base64.getEncoder().encode(bytes));
                    fileInputStreamReader.close();
                    TestRunResult testRunResult = visualRegressionTracker.track(eachFile.getName(), encodedBase64, testRunOptionsBuilder.build());
                    String result = String.valueOf(testRunResult.getTestRunResponse().getStatus());
                    if (!result.equals("OK"))
                        results.put("allPassed", false);
                    int countOfResult = results.get(result) == null ? 1 : Integer.parseInt(results.get(result).toString()) + 1;
                    results.put(result, countOfResult);
                    Object imageVerifiedCount = results.get("imageVerifiedCount");
                    int countOfFilesVerified = imageVerifiedCount == null ? 1 : Integer.parseInt(imageVerifiedCount.toString()) + 1;
                    results.put("imageVerifiedCount", countOfFilesVerified);
                }
            }
        } catch (Exception ex) {
            results.put("allImageVerified", false);
            results.put("errorMessage", ex.getMessage());
        } finally {
            try {
                visualRegressionTracker.stop();
            } catch (Exception e) {
            }
        }
        System.out.println(results);
    }
}
