import io.visual_regression_tracker.sdk_java.*;
import io.visual_regression_tracker.sdk_java.response.BuildResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
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

    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String BOLD_TEXT = "\033[0;1m";

    public static void main(String[] args) {
        if (args.length > 1 || (args.length == 1 && args[0].equals("help"))) {
            System.out.println("Usage: java -jar vrt-standalone-client.jar [folder]" + System.lineSeparator()
                    + "Tracks the PNG and PDF files in the folder (default: the current one)." + System.lineSeparator()
                    + "The connection comes from vrt.json in the current folder or VRT_* environment variables.");
            System.exit(args.length > 1 ? 1 : 0);
        }
        runVRT(args.length > 0 ? args[0] : ".");
    }

    private static void runVRT(String filePath) {
        VisualRegressionTracker visualRegressionTracker = new VisualRegressionTracker();
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
            TestRunOptions.TestRunOptionsBuilder testRunOptionsBuilder = TestRunOptions.builder();
            File[] fileList = file.listFiles();
            for (File eachFile : fileList) {
                String[] imageExtensions = {"png", "pdf"};
                if (Arrays.asList(imageExtensions).contains(FilenameUtils.getExtension(eachFile.getName()))) {
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
            TestRunStatus status = testRunResult.getTestRunResponse().getStatus();
            if (status != TestRunStatus.OK && status != TestRunStatus.AUTO_APPROVED)
                results.put("allPassed", false);
            String result = status.name();
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
        try (PDDocument pdDocument = Loader.loadPDF(eachFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
            for (int pageNumber = 0; pageNumber < pdDocument.getNumberOfPages(); ++pageNumber) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNumber, 300);
                bufferedImageList.add(bufferedImage);
            }
        }
        return bufferedImageList;
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
