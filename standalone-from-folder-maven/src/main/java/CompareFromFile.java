import io.visual_regression_tracker.sdk_java.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class CompareFromFile {

    static final String ANSI_BLACK = "\u001B[30m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_PURPLE = "\u001B[35m";
    static final String ANSI_CYAN = "\u001B[36m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_WHITE = "\u001B[37m";
    static final String BOLD_TEXT = "\033[0;1m";

    public static void main(String[] args) {
        /***
         * Option 1 - Creating and running as a fat jar to be executed from command line
         *   Step 1: create a jar file with mvn command
         *     mvn clean compile assembly:single
         *   Step 2: Run as a standalone jar like this from command line
         *     Example: java -jar vrt-standalone-client-1.0.0.jar http://localhost:4200 CDJ3HHD5MY45G0PK5M3PBB6NBGC9 c936bfa4-50b3-40c4-b177-94efbeabfbcf null main
         *
         * Option 2 - Ensure the values in option2_RunWithHardcodedValue method are correct and run either from this code or run as a jar file from command line.
         */

        //Based on your option, ensure only one of the lines below is uncommented while running.
        //option1_RunWithParameters(args);
        option2_RunWithHardcodedValue();
    }

    private static void option2_RunWithHardcodedValue() {
        String apiUrl = "http://localhost:4200";
        String apiKey = "CDJ3HHD5MY45G0PK5M3PBB6NBGC9"; //Populate your key
        String project = "c936bfa4-50b3-40c4-b177-94efbeabfbcf"; // Populate your project
        String ciBuildId = null;
        String branch = "main";
        String filePath = ".";//Give complete path of the image files or . for current path.
        runVRT(apiUrl, apiKey, project, ciBuildId, branch, filePath);
    }

    private static void option1_RunWithParameters(String[] args) {
        if (args.length == 0 || args[0].trim().contentEquals("help")) {
            System.out.println(
                    BOLD_TEXT + ANSI_PURPLE + "Pass arguments in the following order" + ANSI_RESET + System.lineSeparator() +
                            BOLD_TEXT + ANSI_GREEN + "API URL " + ANSI_RESET + ANSI_CYAN + "(mandatory)" + ANSI_RESET + " - This is the url of VRT backend." + System.lineSeparator() +
                            BOLD_TEXT + ANSI_GREEN + "API Key " + ANSI_RESET + ANSI_CYAN + "(mandatory)" + ANSI_RESET + " - Get it from VRT UI." + System.lineSeparator() +
                            BOLD_TEXT + ANSI_GREEN + "Project " + ANSI_RESET + ANSI_CYAN + "(mandatory)" + ANSI_RESET + " - Get it from VRT UI." + System.lineSeparator() +
                            BOLD_TEXT + ANSI_GREEN + "CI Build Id " + ANSI_RESET + ANSI_CYAN + "(optional)" + ANSI_RESET + " - Use it to tag a VRT build with Jenkins build id. Pass null if you want to skip it and use the next parameter." + System.lineSeparator() +
                            BOLD_TEXT + ANSI_GREEN + "Branch " + ANSI_RESET + ANSI_CYAN + "(optional for VRT higher than 4.19.0)" + ANSI_RESET + " - Will take default branch defined in VRT for the project. Pass null if you want to skip it and use the next parameter." + System.lineSeparator() +
                            BOLD_TEXT + ANSI_GREEN + "File Path " + ANSI_RESET + ANSI_CYAN + "(optional)" + ANSI_RESET + " - Give full path. It will take current folder if not provided." + System.lineSeparator() +
                            BOLD_TEXT + ANSI_YELLOW + "Example: " + ANSI_RESET + ANSI_YELLOW + "java -jar vrt-standalone-client-1.0.0.jar http://localhost:4200 CDJ3HHD5MY45G0PK5M3PBB6NBGC9 c936bfa4-50b3-40c4-b177-94efbeabfbcf null main" + ANSI_RESET);
            System.exit(0);
        }

        if (args.length < 2) {
            System.out.println("You have to pass apiUrl, apiKey and project at a minimum." + System.lineSeparator() +
                    "Example: java -jar vrt-standalone-client-1.0.0.jar http://localhost:4200 CDJ3HHD5MY45G0PK5M3PBB6NBGC9 c936bfa4-50b3-40c4-b177-94efbeabfbcf null main");
            System.exit(0);
        }

        String apiUrl = args[0];
        String apiKey = args[1];
        String project = args[2];
        String ciBuildId = args.length > 3 ? args[3] : null;
        String branch = args.length > 4 ? args[4] : null;
        String filePath = args.length > 5 ? args[5] : null;
        filePath = filePath == null ? "." : filePath;
        runVRT(apiUrl, apiKey, project, ciBuildId, branch, filePath);
    }

    private static void runVRT(String apiUrl, String apiKey, String project, String ciBuildId, String branch, String filePath) {
        VisualRegressionTrackerConfig.VisualRegressionTrackerConfigBuilder configBuilder = VisualRegressionTrackerConfig.builder()
                .apiUrl(apiUrl)
                .apiKey(apiKey)
                .project(project);

        if (ciBuildId != null && !ciBuildId.contentEquals("null"))
            configBuilder.ciBuildId(ciBuildId);
        //If you use VRT version 4.19.0 or lower, then branch is mandatory
        //For higher versions, branch name will default to the main branch if not provided here.
        if (branch != null && !branch.contentEquals("null"))
            configBuilder.branchName(branch);
        VisualRegressionTrackerConfig config = configBuilder.build();

        VisualRegressionTracker visualRegressionTracker = new VisualRegressionTracker(config);
        Map<String, Object> results = new HashMap<>();
        results.put("allImageVerified", true);
        results.put("allPassed", true);
        results.put("imageVerifiedCount", 0);
        results.put("imageProcessedCount", 0);
        try {
            visualRegressionTracker.start();
            TestRunOptions.TestRunOptionsBuilder testRunOptionsBuilder = getTestRunOptionsBuilder();

            File file = new File(filePath);
            File[] fileList = file.listFiles();
            for (File eachFile : fileList) {
                String[] imageExtensions = {"png"};
                if (Arrays.asList(imageExtensions).contains(FilenameUtils.getExtension(eachFile.getName()))) {
                    FileInputStream fileInputStreamReader = new FileInputStream(eachFile);
                    byte[] bytes = new byte[(int) eachFile.length()];
                    fileInputStreamReader.read(bytes);
                    String encodedBase64 = new String(Base64.getEncoder().encode(bytes));
                    fileInputStreamReader.close();
                    try {
                        int countOfProcessed = results.get("imageProcessedCount") == null ? 1 : Integer.parseInt(results.get("imageProcessedCount").toString()) + 1;
                        results.put("imageProcessedCount", countOfProcessed);
                        TestRunResult testRunResult = visualRegressionTracker.track(eachFile.getName(), encodedBase64, testRunOptionsBuilder.build());
                        String result = String.valueOf(testRunResult.getTestRunResponse().getStatus());
                        if (!result.equals("OK"))
                            results.put("allPassed", false);
                        int countOfResult = results.get(result) == null ? 1 : Integer.parseInt(results.get(result).toString()) + 1;
                        results.put(result, countOfResult);
                        Object imageVerifiedCount = results.get("imageVerifiedCount");
                        int countOfFilesVerified = imageVerifiedCount == null ? 1 : Integer.parseInt(imageVerifiedCount.toString()) + 1;
                        results.put("imageVerifiedCount", countOfFilesVerified);
                    } catch (Exception ex) {
                        if (!ex.getMessage().contains("No baseline:")) {
                            throw ex;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            results.put("errorMessage", ex.getMessage());
            results.put("allImageVerified", false);
            results.put("allPassed", false);
        } finally {
            try {
                visualRegressionTracker.stop();
            } catch (Exception e) {
            }
        }
        System.out.println(BOLD_TEXT + ANSI_GREEN + results + ANSI_RESET);
    }

    private static TestRunOptions.TestRunOptionsBuilder getTestRunOptionsBuilder() {
        //Use only the options you need in below line.
        String maskData_x = "0";
        String maskData_y = "0";
        String maskData_width = "200";
        String maskData_height = "20";
        String browser = "";
        String device = "Sample";
        String os = "";
        String viewport = "";

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
        return testRunOptionsBuilder;
    }
}
