/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils;

import com.amitron.quoteapp.constants.Constants;
import static com.amitron.quoteapp.constants.Constants.HOTFOLDER_PATH;
import static com.amitron.quoteapp.constants.Constants.SERVER;
import com.amitron.quoteapp.constants.I8Progress;
import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.repository.I8JobSubmitRepository;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ngn
 */
public class I8JobSubmitUtils implements Constants {

    /* =====================================================
       ================= HTTP / INTEGR8TOR =================
       ===================================================== */
    public static void login() throws Exception {
        String loginUrl
                = SERVER + "/Login.do"
                + "?state=processLogin"
                + "&LoginName=" + URLEncoder.encode(USERNAME, StandardCharsets.UTF_8)
                + "&Password=" + URLEncoder.encode(PASSWORD, StandardCharsets.UTF_8);

        callGet(loginUrl, "LOGIN");
    }

    public static String submitJob(File selectedFile) throws Exception {
        copyToHotfolder(selectedFile);
        Path hotfolder = Paths.get(HOTFOLDER_PATH);
        String jobUrl
                = SERVER + "/JobControl.do"
                + "?state=jobSubmit"
                + "&uploadFile=" + URLEncoder.encode(
                        hotfolder.resolve(selectedFile.getName()).toString(),
                        StandardCharsets.UTF_8)
                + "&priority=3";

        return callGet(jobUrl, "JOB SUBMIT");
    }

    public static String getStatus(int i8Id) throws Exception {
        String statusUrl
                = SERVER + "/JobControl.do"
                + "?state=jobStatus&jobid=" + i8Id;

        return callGet(statusUrl, "JOB STATUS");
    }

    private static String callGet(String urlStr, String tag) throws Exception {

        HttpURLConnection conn
                = (HttpURLConnection) new URL(urlStr).openConnection();

        conn.setRequestMethod("GET");

        int code = conn.getResponseCode();
        //System.out.println("\n[" + tag + "] HTTP " + code);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        code >= 200 && code < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()
                ))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append('\n');
            }

            String html = response.toString();
            //System.out.println(html);

            return getResultFromResponse(html, tag);
        }
    }

    private static String getResultFromResponse(String html, String tag) {

        Pattern pattern = null;

        if ("JOB SUBMIT".equalsIgnoreCase(tag)) {
            pattern = Pattern.compile(
                    "JobNumber:\\s*(\\d+)",
                    Pattern.CASE_INSENSITIVE
            );
        } else if ("JOB STATUS".equalsIgnoreCase(tag)) {
            pattern = Pattern.compile(
                    "<td\\s+class=\"text\"[^>]*>Status</td>\\s*"
                    + "<td\\s+class=\"text\">\\s*([A-Z])\\s*</td>",
                    Pattern.CASE_INSENSITIVE
            );
        }

        if (pattern != null) {
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    /* =====================================================
       ================= FILE / HOTFOLDER =================
       ===================================================== */
    private static void copyToHotfolder(File file) throws Exception {

        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        Path hotfolder = Paths.get(HOTFOLDER_PATH);
        Path target = hotfolder.resolve(file.getName());

        Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        waitUntilStable(target);
    }

    private static void waitUntilStable(Path file) throws Exception {

        long size1;
        long size2;

        do {
            size1 = Files.size(file);
            Thread.sleep(500);
            size2 = Files.size(file);
        } while (size1 != size2);
    }

    public static boolean updateRunningJobStatuses() throws Exception {

        boolean updated = false;
        List<Integer> activeIds = I8JobSubmitRepository.fetchActiveI8Ids();

        for (int i8Id : activeIds) {

            String statusChar = getStatus(i8Id);
            if (statusChar == null) {
                continue;
            }

            I8Progress newProgress = I8Progress.fromCode(statusChar);
            I8Progress oldProgress = I8JobSubmitRepository.getProgressFromDb(i8Id);

            if (newProgress != null && newProgress != oldProgress) {
                I8JobSubmitRepository.updateProgressInDb(i8Id, newProgress);
                updated = true;
            }
        }
        return updated;
    }

    /* =====================================================
       ===================== JTABLE ========================
       ===================================================== */
    public static void refreshJobTable(TableView jobTable) {
        try {
            ObservableList<SubmitRow> data = I8JobSubmitRepository.fetchLatestSubmits();

            jobTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void copyFileToDirectory(File tarFile, File destFile) throws Exception {

        if (!tarFile.exists()) {
            throw new FileNotFoundException(tarFile.getAbsolutePath());
        }

        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        Path destfolder = Paths.get(destFile.getAbsolutePath());
        Path target = destfolder.resolve(tarFile.getName());

        Files.copy(tarFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        waitUntilStable(target);
    }

    public static void createToQuoteStructure(File selectedFile, int freshDeskId, boolean is_itar) throws Exception {
        if (selectedFile.exists()) {
            String quoteJobPath = QUOTE_DIR + File.separator;
            if (is_itar) {
                quoteJobPath = quoteJobPath + "ITAR";
            } else {
                quoteJobPath = quoteJobPath + "NON_ITAR";
            }
            quoteJobPath = quoteJobPath + File.separator + freshDeskId + File.separator;
            //Creating quote folder structure
            for (String qFold : QUOTE_STRUCTURE) {
                File qFildFile = new File(quoteJobPath + qFold);
                if (!qFildFile.exists()) {
                    qFildFile.mkdirs();
                }
            }
            File destFolder = new File(quoteJobPath + QUOTE_STRUCTURE[0]);//01_RFQ_Input folder
            copyFileToDirectory(selectedFile, destFolder);
        }

    }

    /**
     * Copy qed report and xml file to working job folder
     *
     * @param i8no
     * @param partNum
     * @param is_itar
     * @return 0 for unsuccessful, 1 for pdf copy, 2 for xml copy, 3 for both
     * files copy
     * @throws Exception
     */
    public static int copyQedReport(SubmitRow quoteData) throws Exception {
        int i8no = quoteData.i8IdProperty().get();
        String partNum = quoteData.partNumberProperty().get();
        boolean is_itar = quoteData.itarProperty().get();
        String orgData = quoteData.originalDataProperty().get();
        String custName = quoteData.customerNameProperty().get();
        String custCode = quoteData.customerCodeProperty().get();
        int freshDeskId = quoteData.freshdeskIdProperty().get();
        int status = 0;
        String qedReportPath = QED_PATH + File.separator + i8no + File.separator + "work\\reports\\";
        File qedpdf = new File(qedReportPath + "I8QuotationReport_" + i8no + ".pdf");
        File qedxml = new File(qedReportPath + "qed.xml");
        String quoteJobPath = QUOTE_DIR + File.separator;
        if (is_itar) {
            quoteJobPath = quoteJobPath + "ITAR";
        } else {
            quoteJobPath = quoteJobPath + "NON_ITAR";
        }
        quoteJobPath = quoteJobPath + File.separator + freshDeskId + File.separator;
        //Creating quote folder structure
        for (String qFold : QUOTE_STRUCTURE) {
            File qFildFile = new File(quoteJobPath + qFold);
            if (!qFildFile.exists()) {
                qFildFile.mkdirs();
            }
        }
        File destFolder = new File(quoteJobPath + QUOTE_STRUCTURE[1]);//02_Technical_Data folder
        if (qedpdf.exists()) {
            copyFileToDirectory(qedpdf, destFolder);
            status = status + 1;
        }
        if (qedxml.exists()) {
            copyFileToDirectory(qedxml, destFolder);
            status = status + 2;
            createJSONFile(qedxml, destFolder, orgData, custName, custCode, partNum);
        }
        return status;
    }

    private static void createJSONFile(File qedxml, File destFolder, String orgData, String custName, String custCode, String partNum) throws Exception {
        // Load template FIRST
        JSONObject json = JsonFileUtils.load(JSON_TEMPLATE);
        // Load XML ONCE
        XmlContextUtils xml = new XmlContextUtils(qedxml);

        //Update Json data
        String i8Num = xml.getValue("/QED/Header/Job/@id");
        JsonFileUtils.update(json, "extraction_meta.i8_number", i8Num);
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        String dateTimeNow = nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        JsonFileUtils.update(json, "extraction_meta.extraction_date", dateTimeNow);

        JsonFileUtils.update(json, "extraction_meta.source_files[0].filename", orgData);
        String selectedFileExtension = "zip";
        if (orgData.endsWith(".tgz")) {
            selectedFileExtension = "tgz";
        }
        JsonFileUtils.update(json, "extraction_meta.source_files[0].type", selectedFileExtension);
        JsonFileUtils.update(json, "customer.company_name", custName);
        JsonFileUtils.update(json, "customer.company_code", custCode);
        JsonFileUtils.update(json, "customer.part_number", partNum);

        JsonFileUtils.update(json, "board_specs.dimensions.length", xml.getValue("/QED/BoardCharacteristics/Dimension/Height"));
        JsonFileUtils.update(json, "board_specs.dimensions.width", xml.getValue("/QED/BoardCharacteristics/Dimension/Width"));
        JsonFileUtils.update(json, "board_specs.layers.count", xml.getValue("/QED/Summary/SummaryParameter[@name='copperlayercount']/text()"));

// Count only layers where class="layer"
        int layerCount = xml.getNodeCount("/QED/CadData/LayerDesc/Layer[@class='layer']");
        for (int i = 0; i < layerCount; i++) {
            String baseXpath = "/QED/CadData/LayerDesc/Layer[@class='layer'][" + (i + 1) + "]";
            String layerFunction = xml.getValue(baseXpath + "/@layerFunction");
            String layerName = xml.getValue(baseXpath + "/@name");
            // Update JSON array
            JsonFileUtils.update(json, "board_specs.layers.stackup[" + i + "].layer_num", i + 1);
            JsonFileUtils.update(json, "board_specs.layers.stackup[" + i + "].type", layerFunction);
            JsonFileUtils.update(json, "board_specs.layers.stackup[" + i + "].copper_weight", "");
            JsonFileUtils.update(json, "board_specs.layers.stackup[" + i + "].name", layerName);

        }

        //min line width and spacing
        Double minTrack = XmlContextUtils.findMin(xml, "/QED/CopperCharacteristics/CopperLayer/MinTrack");
        Double minGap = XmlContextUtils.findMin(xml, "/QED/CopperCharacteristics/CopperLayer/MinGap");
        JsonFileUtils.update(json, "board_specs.copper.min_trace_width", minTrack);
        JsonFileUtils.update(json, "board_specs.copper.min_trace_spacing", minGap);

        //Solder mask
        int maskVal = Integer.parseInt(xml.getValue("/QED/Summary/SummaryParameter[@name='soldermask']/text()") + "");
        if (maskVal == 0) {
            JsonFileUtils.update(json, "board_specs.soldermask.sides", "none");
        } else if (maskVal == 1) {
            JsonFileUtils.update(json, "board_specs.soldermask.sides", "top");
        }
        if (maskVal == 2) {
            JsonFileUtils.update(json, "board_specs.soldermask.sides", "bottom");
        }
        if (maskVal == 4) {
            JsonFileUtils.update(json, "board_specs.soldermask.sides", "both");
        }

        //Legend
        int silkVal = Integer.parseInt(xml.getValue("/QED/Summary/SummaryParameter[@name='legend']/text()") + "");
        if (silkVal == 0) {
            JsonFileUtils.update(json, "board_specs.silkscreen.sides", "none");
        } else if (silkVal == 1) {
            JsonFileUtils.update(json, "board_specs.silkscreen.sides", "top");
        }
        if (silkVal == 2) {
            JsonFileUtils.update(json, "board_specs.silkscreen.sides", "bottom");
        }
        if (silkVal == 4) {
            JsonFileUtils.update(json, "board_specs.silkscreen.sides", "both");
        }

        //
        JsonFileUtils.update(json, "board_specs.holes.min_drill_size", xml.getValue("/QED/DrillSequences/DrillSequence/MinEndDia"));
        JsonFileUtils.update(json, "board_specs.holes.hole_count_estimate", xml.getValue("/QED/DrillSequences/DrillSequence/Holes"));

        //Electrical test points
        double totalTestpoints = xml.getDouble( "sum(/QED/BareBoardTestCharacteristics/BareBoardTestLayer/@testablePads)");
        JsonFileUtils.update(json, "testing.test_points", totalTestpoints);
        //Write final JSON
        JsonFileUtils.write(json, destFolder.getAbsolutePath() + File.separator + i8Num + "_data.json");
        //update to DB
        I8JobSubmitRepository.updateQedDataToDb(Integer.parseInt(i8Num), json.toString());
    }

    public static File getQuoteFolder(SubmitRow quoteData) throws Exception {
        int i8no = quoteData.i8IdProperty().get();
        String partNum = quoteData.partNumberProperty().get();
        boolean is_itar = quoteData.itarProperty().get();
        String orgData = quoteData.originalDataProperty().get();
        String custName = quoteData.customerNameProperty().get();
        String custCode = quoteData.customerCodeProperty().get();
        int freshDeskId = quoteData.freshdeskIdProperty().get();

        String quoteJobPath = QUOTE_DIR + File.separator;
        if (is_itar) {
            quoteJobPath = quoteJobPath + "ITAR";
        } else {
            quoteJobPath = quoteJobPath + "NON_ITAR";
        }
        quoteJobPath = quoteJobPath + File.separator + freshDeskId;
        return new File(quoteJobPath);
    }

    public static File getTechnicalDataFolder(SubmitRow quoteData) throws Exception {
        return new File(getQuoteFolder(quoteData).getAbsolutePath() + File.separator + QUOTE_STRUCTURE[1]);//02_Technical_Data folder
    }
}
