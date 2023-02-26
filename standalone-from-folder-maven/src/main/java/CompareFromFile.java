import io.visual_regression_tracker.sdk_java.*;
import io.visual_regression_tracker.sdk_java.response.BuildResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        //String apiUrl = "http://localhost:4200";
        String apiKey = args[1];
        //String apiKey = "CDJ3HHD5MY45G0PK5M3PBB6NBGC9"; //Populate your key
        String project = args[2];
        //String project = "c936bfa4-50b3-40c4-b177-94efbeabfbcf"; // Populate your project
        String ciBuildId = args.length > 3 ? args[3] : null;
        //String ciBuildId = null;
        String branch = args.length > 4 ? args[4] : null;
        //String branch = "main";
        String filePath = args.length > 5 ? args[5] : null;
        //String filePath = ".";//Give complete path of the image files or . for current path.

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
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("ERROR: Ensure path " + filePath + " exists.");
                System.exit(1);
            }
            visualRegressionTracker.start();
            //Use the default options builder or custom options builder.
            TestRunOptions.TestRunOptionsBuilder testRunOptionsBuilder = TestRunOptions.builder();
            //TestRunOptions.TestRunOptionsBuilder testRunOptionsBuilder = getTestRunOptionsBuilder();
            File[] fileList = file.listFiles();
            for (File eachFile : fileList) {
                String[] imageExtensions = {"png", "pdf"};
                if (Arrays.asList(imageExtensions).contains(FilenameUtils.getExtension(eachFile.getName()))) {
                    //If the folder has pdf files, they will be converted to png to be able to compare.
                    if (FilenameUtils.getExtension(eachFile.getName()).equalsIgnoreCase("pdf")) {
                        List<BufferedImage> bufferedImageList = getPDFPagesAsImages(eachFile);
                        for (int index = 0; index < bufferedImageList.size(); index++) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            BufferedImage bufferedImage = bufferedImageList.get(index);
                            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
                            byte[] bytes = byteArrayOutputStream.toByteArray();
                            String encodedBase64 = new String(Base64.getEncoder().encode(bytes));
                            String screenshotName = eachFile.getName().replace(".pdf", "_Page") + (index + 1) + ".png";
                            runComparison(visualRegressionTracker, results, testRunOptionsBuilder, screenshotName, encodedBase64);
                            bufferedImage.getGraphics().dispose();
                            bufferedImage.flush();
                        }
                    } else {
                        FileInputStream fileInputStreamReader = new FileInputStream(eachFile);
                        byte[] bytes = new byte[(int) eachFile.length()];
                        fileInputStreamReader.read(bytes);
                        String encodedBase64 = new String(Base64.getEncoder().encode(bytes));
                        fileInputStreamReader.close();
                        runComparison(visualRegressionTracker, results, testRunOptionsBuilder, eachFile.getName(), encodedBase64);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            results.put("errorMessage", ex.getMessage());
            results.put("allImageVerified", false);
            results.put("allPassed", false);
        } finally {
            try {
                BuildResponse buildResponse = visualRegressionTracker.stop();
                System.out.println(BOLD_TEXT + ANSI_BLUE + "VRT status is " + buildResponse.getStatus() + ANSI_RESET);
                writeHTMLFile(buildResponse.getPassedCount(), buildResponse.getFailedCount(), buildResponse.getUnresolvedCount());
            } catch (Exception e) {
            }
        }
        System.out.println(BOLD_TEXT + ANSI_GREEN + results + ANSI_RESET);
    }

    private static void runComparison(VisualRegressionTracker visualRegressionTracker, Map<String, Object> results, TestRunOptions.TestRunOptionsBuilder testRunOptionsBuilder, String screenshotName, String encodedBase64) throws IOException, InterruptedException {
        try {
            int countOfProcessed = results.get("imageProcessedCount") == null ? 1 : Integer.parseInt(results.get("imageProcessedCount").toString()) + 1;
            results.put("imageProcessedCount", countOfProcessed);
            TestRunResult testRunResult = visualRegressionTracker.track(screenshotName, encodedBase64, testRunOptionsBuilder.build());
            String result = String.valueOf(testRunResult.getTestRunResponse().getStatus());
            if (!result.equals("OK") && !result.equals("autoApproved"))
                results.put("allPassed", false);
            int countOfResult = results.get(result) == null ? 1 : Integer.parseInt(results.get(result).toString()) + 1;
            results.put(result, countOfResult);
            Object imageVerifiedCount = results.get("imageVerifiedCount");
            int countOfFilesVerified = imageVerifiedCount == null ? 1 : Integer.parseInt(imageVerifiedCount.toString()) + 1;
            results.put("imageVerifiedCount", countOfFilesVerified);
        } catch (Exception ex) {
            if (!(ex.getMessage().contains("No baseline:") || ex.getMessage().contains("Difference found"))) {
                throw ex;
            } else {
                results.put("allPassed", false);
            }
        }
    }

    private static List<BufferedImage> getPDFPagesAsImages(File eachFile) throws Exception {
        List<BufferedImage> bufferedImageList = new ArrayList<>();
        try (PDDocument pdDocument = PDDocument.load(eachFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
            for (int pageNumber = 0; pageNumber < pdDocument.getNumberOfPages(); ++pageNumber) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNumber, 300);
                bufferedImageList.add(bufferedImage);
                //ImageIO.write(bufferedImage, "PNG", new File(eachFile.getPath().replace(".pdf", "_Page") + (pageNumber + 1) + ".png"));
            }
        }
        return bufferedImageList;
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

    public static void writeHTMLFile(int passed, int failed, int unresolved) throws IOException {
        String content = "<html>\n" +
                "  <head>\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "     var data;\n" +
                "     var chart;\n" +
                "      google.charts.load('current', {'packages':['corechart','table']});\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "      google.charts.setOnLoadCallback(drawTable);\n" +
                "      function drawChart() {\n" +
                "        data = new google.visualization.DataTable();\n" +
                "        data.addColumn('string', 'Status');\n" +
                "        data.addColumn('number', 'Count');\n" +
                "        data.addRows([\n" +
                "          ['Passed', " + passed + "],\n" +
                "          ['Failed', " + failed + "],\n" +
                "          ['Unresolved', " + unresolved + "]\n" +
                "        ]);\n" +
                "        var options = {'title':'VRT Statistics',\n" +
                "                       'width':400,\n" +
                "                       'height':300,\n" +
                "                                                'is3D':true,\n" +
                "                                                'colors':['green','red','orange']\n" +
                "                                                };\n" +
                "        chart = new google.visualization.PieChart(document.getElementById('chart_div'));\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "function drawTable() {\n" +
                "        var data = new google.visualization.DataTable();\n" +
                "        data.addColumn('string', 'Status');\n" +
                "        data.addColumn('number', 'Count');\n" +
                "        data.addRows([\n" +
                "          ['Passed',  " + passed + "],\n" +
                "          ['Failed',  " + failed + "],\n" +
                "          ['Unresolved',  " + unresolved + "]\n" +
                "        ]);\n" +
                "        var table = new google.visualization.Table(document.getElementById('table_div'));\n" +
                "        table.draw(data, {showRowNumber: false, width: 'fit-content', height: 'fit-content'});\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <span id=\"chart_div\" style=\"width:400; height:300\"></span>\n" +
                "    <span id=\"table_div\"></span>\n" +
                "  </body>\n" +
                "</html>";
        Path path = Paths.get("vrt_result.html");
        byte[] strToBytes = content.getBytes();
        Files.write(path, strToBytes);
    }

}
