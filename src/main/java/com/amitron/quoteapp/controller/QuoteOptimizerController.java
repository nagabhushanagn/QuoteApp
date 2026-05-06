/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.controller;

import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.service.optimizer.PanelOptimizer;
import com.amitron.quoteapp.model.optimizer.*;
import com.amitron.quoteapp.service.I8JobSubmitService;
import com.amitron.quoteapp.service.optimizer.ReportService;
import com.amitron.quoteapp.ui.PanelViewDrawer;
import com.amitron.quoteapp.utils.XmlContextUtils;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ToggleGroup;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class QuoteOptimizerController {

    private final PanelOptimizer optimizer = new PanelOptimizer();
    Map<String, Object> reportData = null;
    private SubmitRow jobData;

    @FXML
    private Parent rootPane;
    @FXML
    private ComboBox<String> unitCombo;
    @FXML
    private CheckBox rotationCheck;

    @FXML
    private TextField panelWField;
    @FXML
    private TextField panelHField;
    @FXML
    private TextField borderLeftField;
    @FXML
    private TextField borderRightField;
    @FXML
    private TextField borderTopField;
    @FXML
    private TextField borderBottomField;

    @FXML
    private TextField boardWField;
    @FXML
    private TextField boardHField;

    @FXML
    private TextField gapXField;
    @FXML
    private TextField gapYField;

    @FXML
    private Canvas panelCanvas;

    @FXML
    private Label layoutLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label yieldLabel;
    @FXML
    private Label matrixLabel;
    @FXML
    private Label spacingLabel;
    @FXML
    private Label panelBorderLabel;
    @FXML
    private Label arrayBorderLabel;
    @FXML
    private Label statusLabel;

    @FXML
    private RadioButton singleRadio;
    @FXML
    private RadioButton arrayRadio;
    @FXML
    private TitledPane arrayPane;
    
    @FXML
    private RadioButton custArrayRadio;
    @FXML
    private RadioButton proposedScoredArrayRadio;
    @FXML
    private RadioButton proposedRoutedArrayRadio;

    @FXML
    private TextField pcbWField;
    @FXML
    private TextField pcbHField;
    @FXML
    private TextField countXField;
    @FXML
    private TextField countYField;
    @FXML
    private TextField arrayGapXField;
    @FXML
    private TextField arrayGapYField;
    @FXML
    private TextField railLeftField;
    @FXML
    private TextField railRightField;
    @FXML
    private TextField railTopField;
    @FXML
    private TextField railBottomField;

    @FXML
    private Button optimizeBtn;
    @FXML
    private Button calcArrayBtn;
    @FXML
    private Button savePdfBtn;

    @FXML
    public void initialize() {

        unitCombo.getItems().addAll("inch", "mm");
        unitCombo.setValue("inch");

        /* Toggle group */
        ToggleGroup group = new ToggleGroup();
        singleRadio.setToggleGroup(group);
        arrayRadio.setToggleGroup(group);
        singleRadio.setSelected(true);
        
        /*Array type group button*/
        ToggleGroup arrayGroup = new ToggleGroup();
        custArrayRadio.setToggleGroup(arrayGroup);
        proposedScoredArrayRadio.setToggleGroup(arrayGroup);
        proposedRoutedArrayRadio.setToggleGroup(arrayGroup);
        custArrayRadio.setSelected(true);
        
        //Setting array with score default values
        proposedScoredArrayRadio.setOnAction(e -> {
            if(proposedScoredArrayRadio.isSelected()){
                arrayGapXField.setText("0");
                arrayGapYField.setText("0");
                railLeftField.setText("0.300");
                railRightField.setText("0.300");
                railTopField.setText("0.300");
                railBottomField.setText("0.300");
            }   
        });
        
        //Setting array with rout default values
        proposedRoutedArrayRadio.setOnAction(e -> {
            if(proposedRoutedArrayRadio.isSelected()){
                arrayGapXField.setText("0.1");
                arrayGapYField.setText("0.1");
                railLeftField.setText("0.350");
                railRightField.setText("0.350");
                railTopField.setText("0.350");
                railBottomField.setText("0.350");
            }   
        });

        rotationCheck.setSelected(true);
        singleRadio.setOnAction(e -> {
            arrayPane.setVisible(false);
            arrayPane.setManaged(false);
        });

        arrayRadio.setOnAction(e -> {
            arrayPane.setVisible(true);
            arrayPane.setManaged(true);
        });

        /* Button actions */
        calcArrayBtn.setOnAction(e -> calculateArraySize());
        optimizeBtn.setOnAction(e -> optimize());
        savePdfBtn.setOnAction(e -> pdfGeneration());
    }

    public void setJobData(SubmitRow jobData) {
        this.jobData = jobData;
    }

    public void setQedJson(JSONObject json) {
        Object pcbWidth = XmlContextUtils.getValue(json, "board_specs.dimensions.length");
        Object pcbHeight = XmlContextUtils.getValue(json, "board_specs.dimensions.width");
        
        // Set values in UI
        if(pcbWidth != null && pcbHeight != null){
            boardWField.setText(String.format("%.3f", Double.valueOf(pcbWidth+"")));
            boardHField.setText(String.format("%.3f", Double.valueOf(pcbHeight+"")));
            pcbWField.setText(String.format("%.3f", Double.valueOf(pcbWidth+"")));
            pcbHField.setText(String.format("%.3f", Double.valueOf(pcbHeight+"")));
        }    
    }

    private void optimize() {
        try {
            double panelW = parseDouble(panelWField);
            double panelH = parseDouble(panelHField);
            double panelBorderLeft = parseDouble(borderLeftField);
            double panelBorderRight = parseDouble(borderRightField);
            double panelBorderTop = parseDouble(borderTopField);
            double panelBorderBottom = parseDouble(borderBottomField);

            double boardW = parseDouble(boardWField);
            double boardH = parseDouble(boardHField);

            double gapX = parseDouble(gapXField);
            double gapY = parseDouble(gapYField);

            Panel panel = new Panel(panelW, panelH,
                    panelBorderLeft, panelBorderRight, panelBorderTop, panelBorderBottom);
            Board board = new Board(boardW, boardH);

            /* ===== array mode ===== */
            ArrayDefinition arrayDef = null;

            if (arrayRadio.isSelected()) {
                arrayDef = new ArrayDefinition(
                        parseDouble(pcbWField),
                        parseDouble(pcbHField),
                        parseInt(countXField),
                        parseInt(countYField),
                        parseDouble(arrayGapXField),
                        parseDouble(arrayGapYField),
                        parseDouble(railLeftField),
                        parseDouble(railRightField),
                        parseDouble(railTopField),
                        parseDouble(railBottomField)
                );
            }

            OptimizationResult result = optimizer.optimize(panel, board, arrayDef,
                    true, rotationCheck.isSelected(), gapX, gapY, gapX, gapY);

            PanelViewDrawer.drawPanel(panelCanvas, panel, result);

            int cols = (int) (panelW / (boardW + gapX));
            int rows = (int) (panelH / (boardH + gapY));
            //int total = cols * rows;
            int total = result.getTotalBoards();
            double panelArea = panelW * panelH;
            double boardArea = boardW * boardH * total;

            double utilization = (boardArea / panelArea) * 100;

            //drawLayout(panelW,panelH,boardW,boardH,gapX,gapY,cols,rows);
            Map<String, Object> resultAnalyze = optimizer.analyzeResult(result, panelW, panelH);
            updateInfo(panelW, panelH, boardW, boardH, gapX, gapY, cols, rows, total, utilization, arrayDef, resultAnalyze);

        } catch (Exception ex) {
            statusLabel.setText("Invalid input");
            ex.printStackTrace();
        }
    }

    private void drawLayout(double panelW, double panelH, double boardW, double boardH,
            double gapX, double gapY, int cols, int rows) {

        GraphicsContext g = panelCanvas.getGraphicsContext2D();

        g.clearRect(0, 0, panelCanvas.getWidth(), panelCanvas.getHeight());

        double scale = Math.min(
                panelCanvas.getWidth() / panelW,
                panelCanvas.getHeight() / panelH
        );

        g.setStroke(Color.RED);
        g.strokeRect(10, 10, panelW * scale, panelH * scale);

        g.setFill(Color.LIGHTGREEN);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                double x = 10 + c * (boardW + gapX) * scale;
                double y = 10 + r * (boardH + gapY) * scale;

                g.fillRect(x, y, boardW * scale, boardH * scale);

                g.setStroke(Color.BLACK);
                g.strokeRect(x, y, boardW * scale, boardH * scale);
            }
        }
    }

    private void updateInfo(double panelW, double panelH,
            double boardW, double boardH, double gapX, double gapY,
            int cols, int rows, int total, double utilization,
            ArrayDefinition arrayDef, Map<String, Object> resultAnalyz) {

        reportData = new HashMap<>();
        String unitText = "Dimensions are in " + unitCombo.getValue();
        layoutLabel.setText(unitText);

        String sizeText = "Panel: " + panelW + " x " + panelH + "\n";
        reportData.put("Utilization", String.format("%.2f", utilization));
        String yieldText = String.format("%.2f", utilization) + "% Panel Utilization";
        String matrixText = "On Panel ";
        reportData.put("GridMatrixX", resultAnalyz.get("GridMatrixX"));
        reportData.put("GridMatrixY", resultAnalyz.get("GridMatrixY"));
        matrixText = matrixText + resultAnalyz.get("GridMatrixX") + " x " + resultAnalyz.get("GridMatrixY") + "\n";
        String spacingText = "On Panel " + gapX + " x " + gapY;
        //reportData.put("IsMixedRotation", resultAnalyz.get("IsMixedRotation"));
        if ((boolean) resultAnalyz.get("IsMixedRotation")) {
            reportData.put("RotatedMatrixX", resultAnalyz.get("RotatedMatrixX"));
            reportData.put("RotatedMatrixY", resultAnalyz.get("RotatedMatrixY"));
            matrixText = matrixText + "On Panel " + resultAnalyz.get("RotatedMatrixX") + " x " + resultAnalyz.get("RotatedMatrixY");
        }
        String arrayBorder = "NA";

        int totalPcbs = 0;
        if (singleRadio.isSelected()) {//single pcb jobs
            reportData.put("PcbCount", total);
            totalPcbs = total;
            sizeText = sizeText + "PCB: " + boardW + " x " + boardH;

            yieldText = total + " PCB\n" + yieldText;

        } else if (arrayDef != null) {//for array jobs
            int pcbsInArray = arrayDef.getCountX() * arrayDef.getCountY();
            totalPcbs = total * pcbsInArray;
            double arrayArea = boardW * boardH;
            double pcbsArea = arrayDef.getPcbHeight() * arrayDef.getPcbWidth() * pcbsInArray;
            double arrayUtilization = (pcbsArea / arrayArea) * 100;
            sizeText = sizeText + "Array: " + boardW + " x " + boardH + "\n"
                    + "PCB: " + arrayDef.getPcbHeight() + " x " + arrayDef.getPcbWidth();

            yieldText = total + " Arrays of " + pcbsInArray + " PCBs\n"
                    + totalPcbs + " Total PCBs\n"
                    + yieldText + "\n"
                    + String.format("%.2f", arrayUtilization) + "% Array Utilization";

            matrixText = matrixText + "\nOn Array " + arrayDef.getCountX() + " x " + arrayDef.getCountY();

            spacingText = spacingText + "\nOn Array " + arrayDef.getGapX() + " x " + arrayDef.getGapY();

            arrayBorder = "Left: " + arrayDef.getRailLeft() + "\n"
                    + "Right: " + arrayDef.getRailRight() + "\n"
                    + "Top: " + arrayDef.getRailTop() + "\n"
                    + "Bottom: " + arrayDef.getRailBottom();

            reportData.put("ArrayWidth", boardW);
            reportData.put("ArrayHeight", boardH);
            reportData.put("PcbCount", totalPcbs);
            reportData.put("ArrayCount", total);
            reportData.put("PcbInArray", pcbsInArray);
            reportData.put("ArrayUtilization", String.format("%.2f", arrayUtilization));
        }

        sizeLabel.setText(sizeText);
        yieldLabel.setText(yieldText);
        matrixLabel.setText(matrixText);
        spacingLabel.setText(spacingText);

        String panelBorderText = "Left: " + resultAnalyz.get("LeftOutPanelBorderLeft") + "\n"
                + "Right: " + resultAnalyz.get("LeftOutPanelBorderRight") + "\n"
                + "Top: " + resultAnalyz.get("LeftOutPanelBorderTop") + "\n"
                + "Bottom: " + resultAnalyz.get("LeftOutPanelBorderBottom");
        panelBorderLabel.setText(panelBorderText);
        arrayBorderLabel.setText(arrayBorder);

        String utilizationText = "Boards: " + totalPcbs + " | Utilization: " + String.format("%.2f", utilization) + "%";
        statusLabel.setText(utilizationText);

        reportData.put("SizeText", sizeText);
        reportData.put("YieldText", yieldText);
        reportData.put("MatrixText", matrixText);
        reportData.put("SpacingText", spacingText);
        reportData.put("PanelBorderText", panelBorderText);
        reportData.put("ArrayBorderText", arrayBorder);
        reportData.put("UtilizationText", utilizationText);
        reportData.put("UnitText", unitText);

        reportData.putAll(jsonToMap(buildOptimizerJson()));

        if (jobData != null) {
            reportData.put("I8Id", jobData.i8IdProperty().get());
        }

    }

    private void calculateArraySize() {
        try {
            ArrayDefinition arr = new ArrayDefinition(
                    parseDouble(pcbWField),
                    parseDouble(pcbHField),
                    parseInt(countXField),
                    parseInt(countYField),
                    parseDouble(arrayGapXField),
                    parseDouble(arrayGapYField),
                    parseDouble(railLeftField),
                    parseDouble(railRightField),
                    parseDouble(railTopField),
                    parseDouble(railBottomField)
            );

            double arrayW = arr.getArrayWidth();
            double arrayH = arr.getArrayHeight();

            boardWField.setText(String.format("%.3f", arrayW));
            boardHField.setText(String.format("%.3f", arrayH));

        } catch (Exception ex) {
            statusLabel.setText("Invalid array input");
            ex.printStackTrace();
        }
    }

    public void pdfGeneration() {

        double panelW = parseDouble(panelWField);
        if (reportData != null && !reportData.isEmpty() && jobData != null) {
            ReportService rsv = new ReportService();
            rsv.generateReport(panelCanvas, reportData, jobData);
        }

        // Save to DB
        //Convert reportData to JSON
        String optimizerData = null;
        if (reportData != null && !reportData.isEmpty()) {
            optimizerData = new JSONObject(reportData).toString();
        }
        //Save to DB (optimizer only)
        if (jobData != null && optimizerData != null) {
            I8JobSubmitService.saveOrUpdateOptimizerData(jobData.getRowId(), optimizerData);
        }
    }
    

    public void runOptimizer() {
        optimizeBtn.fire();   //riggers optimize()
    }

    /* ================= HELPERS ================= */
    private int parseInt(TextField tf) {
        return Integer.parseInt(tf.getText().trim());
    }

    private double parseDouble(TextField tf) {
        String v = tf.getText();
        if (v == null || v.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(v.trim());
    }

    public Map<String, Object> getOptimizerReportData() {
        return reportData;
    }

    public JSONObject buildOptimizerJson() {
        JSONObject json = new JSONObject();
        collectNodes(rootPane, json);

        return json;
    }

    private void collectNodes(Parent parent, JSONObject json) {

        for (Node node : parent.getChildrenUnmodifiable()) {

            if (node.getId() != null) {

                String key = node.getId();

                if (node instanceof TextField) {
                    json.put(key, ((TextField) node).getText());
                } else if (node instanceof ComboBox) {
                    Object val = ((ComboBox<?>) node).getValue();
                    json.put(key, val != null ? val.toString() : JSONObject.NULL);
                } else if (node instanceof Label) {
                    json.put(key, ((Label) node).getText());
                } else if (node instanceof TextArea) {
                    json.put(key, ((TextArea) node).getText());
                } else if (node instanceof RadioButton) {
                    json.put(key, ((RadioButton) node).isSelected());
                } else if (node instanceof CheckBox) {
                    json.put(key, ((CheckBox) node).isSelected());
                }
            }

            //recursion for nested containers
            if (node instanceof Parent) {
                collectNodes((Parent) node, json);
            }
        }
    }

    public Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> map = new HashMap<>();

        for (String key : json.keySet()) {
            Object value = json.get(key);
            map.put(key, value == JSONObject.NULL ? null : value);
        }

        return map;
    }

    public void loadOptimizerJson(String jsonString) {

        try {
            JSONObject json = new JSONObject(jsonString);

            for (String key : json.keySet()) {

                Node node = rootPane.lookup("#" + key);
                if (node == null) {
                    continue;
                }

                Object value = json.get(key);

                if (node instanceof TextField) {
                    ((TextField) node).setText(value != null ? value.toString() : "");

                } else if (node instanceof ComboBox) {
                    ComboBox combo = (ComboBox) node;

                    if (value != null) {
                        Object val = value.toString();

                        if (!combo.getItems().contains(val)) {
                            combo.getItems().add(val);
                        }

                        combo.setValue(val);
                    }

                } else if (node instanceof TextArea) {
                    ((TextArea) node).setText(value != null ? value.toString() : "");

                } else if (node instanceof Label) {
                    ((Label) node).setText(value != null ? value.toString() : "");

                } else if (node instanceof RadioButton) {
                    ((RadioButton) node).setSelected(Boolean.parseBoolean(value.toString()));
                } else if (node instanceof CheckBox) {
                    ((CheckBox) node).setSelected(Boolean.parseBoolean(value.toString()));
                }
            }

            if (arrayRadio.isSelected()) {
                arrayPane.setVisible(true);
                arrayPane.setManaged(true);
            }
            System.out.println("Optimizer JSON loaded into UI");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
