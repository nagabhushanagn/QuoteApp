/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.controller;

import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.service.I8JobSubmitService;
import com.amitron.quoteapp.utils.XmlContextUtils;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class I8DataController {

    private SubmitRow submitRow;
    private Map<String, Object> optimizerReportData;
    @FXML
    private ScrollPane rootPane;
    // ================= PART INFORMATION =================
    @FXML
    private TextField customerCodeField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField quoteField;
    @FXML
    private TextField partField;
    @FXML
    private TextField revisionField;
    @FXML
    private TextField pcbWidth;
    @FXML
    private TextField pcbHeight;
    @FXML
    private TextField arrayWidth;
    @FXML
    private TextField arrayHeight;
    @FXML
    private TextField upPerPanelField;
    @FXML
    private TextField upPerArrayField;
    @FXML
    private ComboBox<String> xOutAllowedCombo;
    @FXML
    private ComboBox<String> amortizeCombo;
    @FXML
    private ComboBox<String> stencilsCombo;
    @FXML
    private TextField panelSizeField;
    @FXML
    private TextField utilizationField;

    // ================= COPPER WEIGHT =================
    @FXML
    private ComboBox<String> twoOzLayersCombo;
    @FXML
    private ComboBox<String> threeOzLayersCombo;
    @FXML
    private ComboBox<String> fourOzLayersCombo;
    @FXML
    private ComboBox<String> fiveOzLayersCombo;
    @FXML
    private ComboBox<String> sixOzLayersCombo;
    @FXML
    private ComboBox<String> unevenCopperCombo;
    @FXML
    private ComboBox<String> extraCopperCombo;

    // ================= BASIC INFORMATION =================
    @FXML
    private ComboBox<String> layerCountCombo;
    @FXML
    private ComboBox<String> constructionCombo;
    @FXML
    private ComboBox<String> materialTypeCombo;
    @FXML
    private TextField thicknessField;
    @FXML
    private ComboBox<String> surfaceFinishCombo;

    // ================= INKS & COATINGS =================
    @FXML
    private ComboBox<String> soldermaskCombo;
    @FXML
    private ComboBox<String> soldermaskSideCombo;
    @FXML
    private ComboBox<String> legendCombo;
    @FXML
    private ComboBox<String> legendSideCombo;
    @FXML
    private ComboBox<String> carbonInkCombo;
    @FXML
    private ComboBox<String> carbonInkSideCombo;
    @FXML
    private ComboBox<String> peelableMaskCombo;
    @FXML
    private ComboBox<String> peelableMaskSideCombo;
    @FXML
    private ComboBox<String> viaFillPlugCombo;

    // ================= SPECIAL PROCESS =================
    @FXML
    private ComboBox<String> plasmaDesmearCombo;
    @FXML
    private ComboBox<String> aluminumFinishCombo;
    @FXML
    private ComboBox<String> hdiCombo;
    @FXML
    private ComboBox<String> multiPressCombo;
    @FXML
    private ComboBox<String> goldTabCombo;

    // ================= MECHANICAL =================
    @FXML
    private TextField smallestHoleField;
    @FXML
    private TextField holeCount;
    @FXML
    private ComboBox<String> edgeBevelCombo;
    @FXML
    private TextField slotsField;
    @FXML
    private TextField slotCount;
    @FXML
    private ComboBox<String> counterSinkCombo;
    @FXML
    private ComboBox<String> counterBoreCombo;
    @FXML
    private ComboBox<String> edgePlatingCombo;
    @FXML
    private ComboBox<String> scoringCombo;
    @FXML
    private TextField scorecount;
    @FXML
    private ComboBox<String> breakApartCombo;

    // ================= QUALITY & TESTING =================
    @FXML
    private ComboBox<String> electricalTestCombo;
    @FXML
    private TextField testPointField;
    @FXML
    private ComboBox<String> qualitySpecCombo;
    @FXML
    private ComboBox<String> tdrTestingCombo;
    @FXML
    private ComboBox<String> firstArticleCombo;
    @FXML
    private ComboBox<String> hiPotTestingCombo;
    @FXML
    private ComboBox<String> itarCombo;

    // ================= Note =================
    @FXML
    private TextArea customerNote;
    @FXML
    private TextArea internalNote;

    // ================= BUTTON =================
    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        loadDropdowns();
        saveButton.setOnAction(e -> saveI8Data());

    }

    public void setJobSubmittedData(SubmitRow submitRow) {
        this.submitRow = submitRow;
        try {
            loadQEDData();
        } catch (SQLException ex) {
            Logger.getLogger(I8DataController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setOptimizerReportData(Map<String, Object> optimizerReportData) {
        this.optimizerReportData = optimizerReportData;

        loadOptimizerData();
    }

    // ================= DROPDOWN DATA =================
    private void loadDropdowns() {
        // Common Yes
        setYes(xOutAllowedCombo, amortizeCombo);

        // Common No
        setNo(edgeBevelCombo, counterBoreCombo, counterSinkCombo, edgePlatingCombo,
                breakApartCombo, carbonInkCombo, peelableMaskCombo, tdrTestingCombo,
                hiPotTestingCombo, itarCombo, hdiCombo, plasmaDesmearCombo);

        // Layer Count
        layerCountCombo.getItems().addAll("1", "2", "4", "6", "8", "10", "12",
                "14", "16", "18", "20", "22", "24", "26", "28", "30");

        //Stencil
        stencilsCombo.getItems().addAll("No", "1 Frameless - $165", "2 Frameless - $320", "1 Framed - $250", "2 Framed - $500");
        stencilsCombo.setValue("No");
        // Material Type
        materialTypeCombo.getItems().addAll("FR-4 per IPC-4101/21", "FR-4 min. Tg 170",
                "FR-4 min. Tg 180", "FR-4 per IPC-4101/126", "CEM-1",
                "Isola 185HR", "Isola 370HR/IS410/FR406", "IMS PCB - Generic",
                "IMS PCB - Laird", "IMS PCB - AIS Malibar");
        materialTypeCombo.setValue("FR-4 min. Tg 170");

        //Construction
        constructionCombo.getItems().addAll("Standard", "Filler Core", "Cap Const.", "Make Mtl");
        constructionCombo.setValue("Standard");

        // Surface Finish
        surfaceFinishCombo.getItems().addAll("Tin/Lead Solder", "ENIG", "L/F Solder", "Imm Silver",
                "Imm Tin", "OSP", "Outside Gold - Full Body", "Outside Gold - Selective", "Outside Gold - Selective w/bus");
        surfaceFinishCombo.setValue("ENIG");
        
        //Copper weights
        twoOzLayersCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        threeOzLayersCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        fourOzLayersCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        fiveOzLayersCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        sixOzLayersCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        unevenCopperCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        unevenCopperCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15");
        extraCopperCombo.getItems().addAll("No", "0.0014\" In Hole", "0.002\" in Hole", "2oz Pattern");
        extraCopperCombo.setValue("No");

        // Inks & Coatings
        soldermaskCombo.getItems().addAll("Green", "Red", "Blue", "White", "Black",
                "LED White", "Purple", "Clear", "Yellow", "None");
        soldermaskCombo.setValue("Green");
        soldermaskSideCombo.getItems().addAll("1 Side", "2 Sides", "1 Side(2Coats)", "2 Side(2Coats)");
        soldermaskSideCombo.setValue("2 Sides");
        legendCombo.getItems().addAll("None", "White", "Black", "Yellow", "Blue");
        legendCombo.setValue("None");
        legendSideCombo.getItems().addAll("None", "1 Side", "2 Sides");
        legendSideCombo.setValue("None");
        carbonInkSideCombo.getItems().addAll("None", "1 Side", "2 Sides");
        carbonInkSideCombo.setValue("None");
        peelableMaskSideCombo.getItems().addAll("None", "1 Side", "2 Sides");
        peelableMaskSideCombo.setValue("None");
        viaFillPlugCombo.getItems().addAll("No", "Via-Plug", "Non Cond. (All holes filled)", "Non Cond. (>0.006\" Space)",
                "Non Cond. (>0.005\" Space)", "Non Cond. (>0.004\" Space)", "Cond. (All holes filled)",
                "Cond. (>0.006\" Space)", "Cond. (>0.005\" Space)", "Cond. (>0.004\" Space)");
        viaFillPlugCombo.setValue("No");

        // Quality Spec
        qualitySpecCombo.getItems().addAll("IPC Class 2", "IPC-600 Class 3", "IPC Class 3 + Coupons",
                "IPC Class 3 + A/B Coupons", "MIL-55110 App. A", "MIL-31032 Rev. C");
        qualitySpecCombo.setValue("IPC Class 2");
        electricalTestCombo.getItems().addAll("Flying Probe", "Fastek", "TTI", "Outside Testing", "None");
        electricalTestCombo.setValue("Flying Probe");
        firstArticleCombo.getItems().addAll("None", "Full Mechanical", "PPAP", "AS9102");
        firstArticleCombo.setValue("None");
        
        //Special process
        aluminumFinishCombo.getItems().addAll("No", "Anodize (Panel Form)", "Anodize(Piece Form)",
                "Chromate(Panel Form)", "Chromate(Piece Form)");
        aluminumFinishCombo.setValue("No");
        goldTabCombo.getItems().addAll("None", "1 Row/Panel", "2 Row/Panel", "3 Row/Panel", "4 Row/Panel",
                "5 Row/Panel", "6 Row/Panel", "7 Row/Panel", "8 Row/Panel", "9 Row/Panel", "10 Row/Panel");
        goldTabCombo.setValue("None");
        multiPressCombo.getItems().addAll("No", "2 Press", "3 Press", "4 Press");
        multiPressCombo.setValue("No");
        
        //Mechanical anf Fabrication
        scoringCombo.getItems().addAll("No", "Normal", "Jump");
        scoringCombo.setValue("No");
    }
    
    // ================= HELPER =================
    @SafeVarargs
    private final void setYes(ComboBox<String>... combos) {
        for (ComboBox<String> combo : combos) {
            combo.getItems().addAll("Yes", "No");
            combo.setValue("Yes");
        }
    }

    @SafeVarargs
    private final void setNo(ComboBox<String>... combos) {
        for (ComboBox<String> combo : combos) {
            combo.getItems().addAll("Yes", "No");
            combo.setValue("No");
        }
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : "";
    }

    private String nullValidate(Object value) {
        return value != null ? value.toString() : "";
    }

    private void loadQEDData() throws SQLException {
        try {
            if (submitRow != null) {
                JSONObject jsonQED = I8JobSubmitService.getQedJsonData(submitRow.i8IdProperty().get());
                customerCodeField.setText(submitRow.customerCodeProperty().get());
                customerNameField.setText(submitRow.customerNameProperty().get());
                partField.setText(submitRow.partNumberProperty().get());
                layerCountCombo.setValue(nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.layers.count")));
                soldermaskCombo.setValue(nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.soldermask.color")));
                String smSide = nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.soldermask.sides"));
                if (!smSide.isEmpty()) {
                    if (smSide.matches("both")) {
                        smSide = "2 Sides";
                    } else {
                        smSide = "1 Side";
                    }
                }
                soldermaskSideCombo.setValue(smSide);
                legendCombo.setValue(nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.silkscreen.color")));
                String silkSide = nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.silkscreen.sides"));
                if (!silkSide.isEmpty()) {
                    if (silkSide.matches("both")) {
                        silkSide = "2 Sides";
                    } else {
                        silkSide = "1 Side";
                    }
                }
                legendSideCombo.setValue(silkSide);
                String cInk = nullValidate(XmlContextUtils.getValue(jsonQED, "special_requirements.carbon_ink"));
                if (cInk.matches("true")) {
                    cInk = "Yes";
                } else {
                    cInk = "No";
                }
                carbonInkCombo.setValue(cInk);
                String pMask = nullValidate(XmlContextUtils.getValue(jsonQED, "special_requirements.peelable_mask"));
                if (pMask.matches("true")) {
                    pMask = "Yes";
                } else {
                    pMask = "No";
                }
                peelableMaskCombo.setValue(pMask);
                String minHole = nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.holes.min_drill_size"));
                if (!minHole.isEmpty()) {
                    smallestHoleField.setText(String.format("%.3f", (Double.parseDouble(minHole) / 1000)));
                }
                holeCount.setText(nullValidate(XmlContextUtils.getValue(jsonQED, "board_specs.holes.hole_count_estimate")));
                testPointField.setText(nullValidate(XmlContextUtils.getValue(jsonQED, "testing.test_points")));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }

    }

    private void loadOptimizerData() {
        if (optimizerReportData != null && !optimizerReportData.isEmpty()) {
            pcbWidth.setText(nullValidate(optimizerReportData.get("pcbWField")));
            pcbHeight.setText(nullValidate(optimizerReportData.get("pcbHField")));
            arrayWidth.setText(nullValidate(optimizerReportData.get("ArrayWidth")));
            arrayHeight.setText(nullValidate(optimizerReportData.get("ArrayHeight")));
            upPerPanelField.setText(nullValidate(optimizerReportData.get("PcbCount")));
            upPerArrayField.setText(nullValidate(optimizerReportData.get("PcbInArray")));
            panelSizeField.setText(nullValidate(optimizerReportData.get("panelWField")) + " x "
                    + nullValidate(optimizerReportData.get("panelHField")));
            utilizationField.setText(nullValidate(optimizerReportData.get("Utilization")));
        }
    }

    private void saveI8Data() {
        JSONObject json = buildJson();

        String savedData = json.toString();
        String optimizerData = null;
        if (optimizerReportData != null && !optimizerReportData.isEmpty()) {
            optimizerData = new JSONObject(optimizerReportData).toString();
        }
        int submitId = submitRow.getRowId();

        I8JobSubmitService.saveOrUpdateQuoteData(submitId, optimizerData, savedData);
    }

    private JSONObject buildJson() {
        JSONObject result = new JSONObject();
        if (rootPane == null || rootPane.getContent() == null) {
            return result;
        }
        Node content = rootPane.getContent(); // HBox
        collectTitledPanes(content, result);

        return result;
    }

    private void collectTitledPanes(Node node, JSONObject result) {
        if (node == null) {
            return;
        }
        //If TitledPane found
        if (node instanceof TitledPane) {
            TitledPane pane = (TitledPane) node;
            String paneId = pane.getId();
            if (paneId != null) {
                JSONObject paneJson = new JSONObject();
                collectFields(pane.getContent(), paneJson);
                result.put(paneId, paneJson);
            }
        }

        //Traverse children
        if (node instanceof Pane) {
            for (Node child : ((Pane) node).getChildren()) {
                collectTitledPanes(child, result);
            }
        } else if (node instanceof ScrollPane) {
            collectTitledPanes(((ScrollPane) node).getContent(), result);
        }
    }

    private void collectFields(Node node, JSONObject json) {
        if (node == null) {
            return;
        }

        if (node instanceof TextField) {
            TextField tf = (TextField) node;

            if (tf.getId() != null) {
                json.put(tf.getId(), tf.getText());
            }
        } else if (node instanceof TextArea) {
            TextArea ta = (TextArea) node;
            if (ta.getId() != null) {
                json.put(ta.getId(), ta.getText());
            }

        } else if (node instanceof ComboBox) {
            ComboBox<?> cb = (ComboBox<?>) node;
            if (cb.getId() != null) {
                Object value = cb.getValue();
                json.put(cb.getId(), value != null ? value.toString() : null);
            }
        } else if (node instanceof CheckBox) {
            CheckBox cb = (CheckBox) node;
            if (cb.getId() != null) {
                json.put(cb.getId(), cb.isSelected());
            }
        } else if (node instanceof DatePicker) {
            DatePicker dp = (DatePicker) node;
            if (dp.getId() != null) {
                json.put(dp.getId(),
                        dp.getValue() != null ? dp.getValue().toString() : null);
            }
        }

        //Traverse deeper (GridPane, VBox, etc.)
        if (node instanceof Pane) {
            for (Node child : ((Pane) node).getChildren()) {
                collectFields(child, json);
            }
        } else if (node instanceof ScrollPane) {
            collectFields(((ScrollPane) node).getContent(), json);
        }
    }

    public void loadSavedJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            for (String sectionKey : json.keySet()) {
                JSONObject section = json.getJSONObject(sectionKey);
                for (String fieldId : section.keySet()) {
                    Object value = section.get(fieldId);
                    Node node = rootPane.lookup("#" + fieldId);
                    if (node instanceof TextField) {
                        ((TextField) node).setText(value != null ? value.toString() : "");
                    } else if (node instanceof ComboBox) {
                        ComboBox combo = (ComboBox) node;
                        if (value != null) {
                            Object val = value.toString();
                            if (combo.getItems() != null && !combo.getItems().contains(val)) {
                                combo.getItems().add(val); 
                            }
                            combo.setValue(val);
                        }
                    } else if (node instanceof TextArea) {
                        ((TextArea) node).setText(value != null ? value.toString() : "");
                    }
                }
            }
            System.out.println("I8 JSON loaded into UI");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
