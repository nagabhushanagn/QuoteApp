/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.controller;

import com.amitron.quoteapp.model.QuantityRow;
import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.service.CalculationService;
import com.amitron.quoteapp.service.I8JobSubmitService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class CalculationController {

    private SubmitRow jobData;
    private TextField basePriceField;
    private TextField materialField;
    private TextField innerCuField;
    private TextField outerCuField;
    private TextField surfaceFinishField;
    private TextField drillingField;
    private TextField routingField;
    private TextField scoringField;
    private TextField sumOfAField;
    private TextField solderMaskField;
    private TextField legendField;

    private JSONObject optimizerJson;
    private JSONObject savedJson;
    private JSONObject stackupJson;
    private JSONObject masterPriceJson;

    private int masterPriceVersion = 1;

    private static final String PER_PANEL = "PER_PANEL";
    private static final String PER_PCB = "PER_PCB";
    private static final String FLAT = "FLAT";

    @FXML
    private ComboBox<Integer> versionCombo;
    
    @FXML
    private Label masterPriceVersionLable;

    @FXML
    private GridPane priceGrid;

    @FXML
    private VBox specialProcessContainer;

    @FXML
    private VBox nrcContainer;

    @FXML
    private TextField sumOfBField;

    @FXML
    private TableView<QuantityRow> quantityTable;

    @FXML
    private TableColumn<QuantityRow, Integer> qtyCol;

    @FXML
    private TableColumn<QuantityRow, String> deliveryCol;

    @FXML
    private TableColumn<QuantityRow, Number> launchCol;

    @FXML
    private TableColumn<QuantityRow, Number> unitPriceCol;

    @FXML
    private TableColumn<QuantityRow, Number> nrcCol;

    @FXML
    private TableColumn<QuantityRow, Number> calcPriceCol;

    @FXML
    private TableColumn<QuantityRow, Double> discountCol;

    @FXML
    private TableColumn<QuantityRow, Number> finalPriceCol;

    private final ObservableList<QuantityRow> rows = FXCollections.observableArrayList();

    private static final JSONObject SPECIAL_PROCESS_MAPPING
            = new JSONObject()
                    .put("viaFillPlugCombo", "Viafill")
                    .put("carbonInkCombo", "Carbon Ink")
                    .put("peelableMaskCombo", "Peelable")
                    .put("goldTabCombo", "Hard Gold")
                    .put("aluminumFinishCombo", "Aluminum")
                    .put("plasmaDesmearCombo", "Plasma")
                    .put("multiPressCombo", "Multi Press")
                    .put("hdiCombo", "HDI")
                    .put("edgeBevelCombo", "Beveling")
                    .put("counterSinkCombo", "Counter shunk")
                    .put("counterBoreCombo", "Counter bore")
                    .put("edgePlatingCombo", "Edge plating")
                    .put("tdrTestingCombo", "Impedence");

    @FXML
    public void initialize() {
        buildPriceGrid();
        initializeTable();

        quantityTable.setItems(rows);
        quantityTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        quantityTable.setEditable(true);
        onAddRow();
        //onAddNrc();

        calculateSumOfA();
        recalculateAllRows();

        versionCombo.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                loadSelectedVersion(newV);
            }
        });
    }

    public void setJobData(SubmitRow jobData) {
        this.jobData = jobData;
        System.out.println("SET JOB DATA : " + jobData.getRowId());

        loadInitialData();
    }

    public void loadInitialData() {
        if (jobData == null) {
            return;
        }
        try {
            loadVersions();
            Map<String, String> dbData
                    = I8JobSubmitService
                            .getCalculationSourceData(
                                    jobData.getRowId());

            // =========================================
            // OPTIMIZER
            // =========================================
            if (dbData.get("optimizer") != null
                    && !dbData.get("optimizer").isEmpty()) {

                optimizerJson = new JSONObject(dbData.get("optimizer"));
            }

            // =========================================
            // SAVED
            // =========================================
            if (dbData.get("saved") != null && !dbData.get("saved").isEmpty()) {
                savedJson = new JSONObject(dbData.get("saved"));
            }

            // =========================================
            // STACKUP
            // =========================================
            if (dbData.get("stackup") != null && !dbData.get("stackup").isEmpty()) {

                stackupJson = new JSONObject(dbData.get("stackup"));
            }

            // =========================================
            // MASTER PRICE
            // =========================================
            JSONObject masterRoot = CalculationService.getLatestMasterPrice();

            System.out.println("MASTER ROOT = " + masterRoot);

            if (masterRoot != null) {
                masterPriceVersion = masterRoot.optInt("version");

                // SUPPORT BOTH STRUCTURES
                if (masterRoot.has("data")) {
                    masterPriceJson = masterRoot.getJSONObject("data");
                } else {
                    masterPriceJson = masterRoot;
                }
            }

            System.out.println("MASTER PRICE JSON = " + masterPriceJson);

            // LOAD DEFAULT CALCULATION
            loadBasePrice();
            loadMaterialPrice();
            loadCopperThicknessPrice();
            loadSurfaceFinishPrice();
            loadDrillingPrice();
            loadRoutingPrice();
            loadScoringPrice();
            loadSolderMaskPrice();
            loadLegendPrice();
            loadSpecialProcessPrices();
            loadNrcItems();

            // LOAD SAVED VERSION IF AVAILABLE
            loadVersions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getSubmitId() {
        if (jobData == null) {
            return 0;
        }
        return jobData.getRowId();
    }

    private void buildPriceGrid() {

        int row = 0;

        basePriceField = addPriceRow(row++, "Base Price", false, "normal");

        materialField = addPriceRow(row++, "Material", false, "normal");

        innerCuField = addPriceRow(row++,"Inner Cu Price", false, "normal");

        outerCuField = addPriceRow(row++, "Outer Cu Price", false, "normal");

        surfaceFinishField = addPriceRow(row++, "Surface Finish", false, "normal");

        drillingField = addPriceRow(row++, "Drilling", false, "normal");

        routingField = addPriceRow(row++, "Routing", false, "normal");

        scoringField = addPriceRow(row++, "Scoring", false, "normal");

        solderMaskField = addPriceRow(row++, "Solder Mask", false, "normal");

        legendField = addPriceRow(row++, "Legend", false, "normal");

        // SPECIAL PROCESS TITLE
        Label spLabel = new Label("Special Process");
        priceGrid.add(spLabel, 0, row++);
        // SPECIAL PROCESS CONTAINER
        specialProcessContainer = new VBox(5);
        priceGrid.add(specialProcessContainer, 0, row++, 5, 1);

        // SUM OF A
        sumOfAField = addPriceRow(row++, "Sum Of A", false, "bold");
    }

    private TextField addPriceRow(int row, String label, boolean editable, String style) {

        Label lbl = new Label(label);
        if(style.matches("bold")){
            lbl.setStyle("-fx-font-weight:bold;");
        }

        TextField tf = new TextField();

        tf.setPrefWidth(120);

        tf.setEditable(editable);

        priceGrid.add(lbl, 0, row);

        priceGrid.add(tf, 1, row);

        return tf;
    }

    private void calculateSumOfA() {
        double total = 0;
        total += parseField(basePriceField);
        total += parseField(materialField);
        total += parseField(innerCuField);
        total += parseField(outerCuField);
        total += parseField(surfaceFinishField);
        total += parseField(drillingField);
        total += parseField(routingField);
        total += parseField(scoringField);
        total += parseField(solderMaskField);
        total += parseField(legendField);

        // DYNAMIC SPECIAL PROCESS TOTAL
        for (Node node : specialProcessContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                if (hbox.getChildren().size() >= 2) {
                    TextField tf = (TextField) hbox.getChildren().get(1);
                    total += parseField(tf);
                }
            }
        }

        sumOfAField.setText(String.format("%.2f", total));
    }

    private void initializeTable() {
        qtyCol.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        qtyCol.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new IntegerStringConverter()));
        qtyCol.setOnEditCommit(event -> {
            QuantityRow row = event.getRowValue();
            row.setQuantity(event.getNewValue());
            recalculateRow(row);
        });

        deliveryCol.setCellValueFactory(data -> data.getValue().deliveryProperty());
        deliveryCol.setCellFactory(
                TextFieldTableCell.forTableColumn());

        deliveryCol.setOnEditCommit(event -> {
            QuantityRow row = event.getRowValue();
            row.setDelivery(event.getNewValue());
            recalculateRow(row);
        });

        launchCol.setCellValueFactory(
                data -> data.getValue().launchingPanelProperty());

        unitPriceCol.setCellValueFactory(
                data -> data.getValue().unitPriceAProperty());

        nrcCol.setCellValueFactory(
                data -> data.getValue().nrcBProperty());

        calcPriceCol.setCellValueFactory(
                data -> data.getValue().calculatedPriceProperty());

        discountCol.setCellValueFactory(data -> data.getValue().discountProperty().asObject());
        discountCol.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new DoubleStringConverter()));
        discountCol.setOnEditCommit(event -> {
            QuantityRow row = event.getRowValue();
            row.setDiscount(event.getNewValue());
            recalculateRow(row);
        });

        finalPriceCol.setCellValueFactory(data -> data.getValue().finalPriceProperty());
    }

    @FXML
    private void onAddNrc() {
        HBox row = new HBox(10);

        TextField labelField = new TextField();

        labelField.setPromptText("NRC Name");

        TextField valueField = new TextField();

        valueField.setPromptText("0");

        Button removeBtn = new Button("-");

        valueField.textProperty().addListener(
                (obs, oldV, newV) -> {
                    calculateNrcTotal();
                    recalculateAllRows();
                });

        removeBtn.setOnAction(e -> {

            nrcContainer.getChildren().remove(row);

            calculateNrcTotal();
        });

        row.getChildren().addAll(
                labelField,
                valueField,
                removeBtn
        );

        nrcContainer.getChildren().add(row);
    }

    private void calculateNrcTotal() {
        double total = 0;
        for (javafx.scene.Node node : nrcContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                if (hbox.getChildren().size() >= 2) {
                    TextField valueField = (TextField) hbox.getChildren().get(1);
                    try {
                        double value = Double.parseDouble(valueField.getText());
                        total += value;
                    } catch (Exception ex) {

                    }
                }
            }
        }

        sumOfBField.setText(String.format("%.2f", total));
    }

    @FXML
    private void onAddRow() {
        QuantityRow row = new QuantityRow();
        row.setQuantity(100);
        row.setDelivery("7 Days");
        row.setDiscount(0);
        recalculateRow(row);
        rows.add(row);
    }

    @FXML
    private void onDeleteSelectedRow() {
        QuantityRow selected = quantityTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rows.remove(selected);
        }
    }

    @FXML
    private void onSave() {
        try {
            Integer version = versionCombo.getValue();
            if (version == null) {
                version = 1;
            }
            JSONObject saveJson = buildCalculationJson();
            saveJson.put("version", version);

            CalculationService.saveCalculation(
                    getSubmitId(),
                    version,
                    masterPriceVersion,
                    saveJson,
                    false);

            loadVersions();

            versionCombo.setValue(version);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onSaveAsVersion() {
        try {
            JSONObject latestMaster = CalculationService.getLatestMasterPrice();
            masterPriceVersion = latestMaster.optInt("version");
            masterPriceJson = latestMaster.optJSONObject("data");
            
            int latestMasterVersion = latestMaster.optInt("version",1);
            
            loadBasePrice();
            loadMaterialPrice();
            loadCopperThicknessPrice();
            loadSurfaceFinishPrice();
            loadDrillingPrice();
            loadRoutingPrice();
            loadScoringPrice();
            loadSolderMaskPrice();
            loadLegendPrice();
            loadSpecialProcessPrices();
            loadNrcItems();
            
            calculateSumOfA();
            calculateNrcTotal();
            recalculateAllRows();
            
            JSONObject saveJson = buildCalculationJson();
            saveJson.put("masterPriceVersion", latestMasterVersion);
            masterPriceVersionLable.setText("Master Price Version: " + latestMasterVersion);
            int version = CalculationService.saveAsNewVersion(
                            getSubmitId(),
                            saveJson,
                            latestMasterVersion);

            loadVersions();

            versionCombo.setValue(version);
            
            /*
            JSONObject saveJson = buildCalculationJson();

            int version = CalculationService.saveAsNewVersion(
                    getSubmitId(),
                    saveJson, masterPriceVersion);
            loadVersions();

            versionCombo.setValue(version);
            */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        
    }

    private double parseField(TextField tf) {
        try {
            return Double.parseDouble(tf.getText());

        } catch (Exception ex) {
            return 0;
        }
    }

    private void recalculateRow(QuantityRow row) {
        double unitPriceA = parseField(sumOfAField);
        double baseNrc = parseField(sumOfBField);
        double deliveryPrice = getDeliveryPrice(row.getDelivery());
        double nrcB = baseNrc + deliveryPrice;
        double calculated = unitPriceA + nrcB;
        double discount = row.getDiscount();
        double finalPrice = calculated - (calculated * discount / 100.0);
        int launchPanels = calculateLaunchPanels(row.getQuantity());
        row.setLaunchingPanel(launchPanels);
        row.setUnitPriceA(unitPriceA);
        row.setNrcB(nrcB);
        row.setCalculatedPrice(calculated);
        row.setFinalPrice(finalPrice);

        quantityTable.refresh();
    }

    private void recalculateAllRows() {
        for (QuantityRow row : rows) {
            recalculateRow(row);
        }
    }

    private JSONObject getTabsJson() {
        if (masterPriceJson == null) {
            return new JSONObject();
        }
        return masterPriceJson.optJSONObject("tabs");
    }

    private double findPriceByField(JSONArray array, String fieldName, String fieldValue, String priceField) {
        if (array == null) {
            return 0;
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String value = obj.optString(fieldName);

            if (fieldValue.equalsIgnoreCase(value)) {
                return obj.optDouble(priceField);
            }
        }
        return 0;
    }

    private JSONArray getPriceArray(String key) {
        try {
            if (masterPriceJson == null) {
                System.out.println("MASTER PRICE JSON NULL");

                return new JSONArray();
            }

            JSONObject tabs = masterPriceJson.optJSONObject("tabs");
            if (tabs == null) {
                System.out.println("TABS NULL");

                return new JSONArray();
            }

            JSONArray arr = tabs.optJSONArray(key);

            if (arr == null) {
                System.out.println("ARRAY NULL : " + key);

                return new JSONArray();
            }

            System.out.println(key + " ARRAY SIZE = " + arr.length());

            return arr;

        } catch (Exception ex) {
            ex.printStackTrace();

            return new JSONArray();
        }
    }

    private int getPcbCount() {
        if (optimizerJson == null) {
            return 1;
        }
        return optimizerJson.optInt("PcbCount", 1);
    }

    private double findConvertedPrice(JSONArray array, String fieldName, String fieldValue) {
        if (array == null) {
            return 0;
        }

        String searchValue = fieldValue.trim().toLowerCase();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String value = obj.optString(fieldName).trim().toLowerCase();

            if (searchValue.equals(value)) {
                return convertToPerPanel(obj);
            }
        }

        return 0;
    }

    private double convertToPerPanel(JSONObject priceObj) {
        if (priceObj == null) {
            return 0;
        }

        String unitType = priceObj.optString("unitType");

        double price = priceObj.optDouble("price");

        if (PER_PANEL.equalsIgnoreCase(unitType)) {
            return price;
        }

        if (PER_PCB.equalsIgnoreCase(unitType)) {
            return price * getPcbCount();
        }

        if (FLAT.equalsIgnoreCase(unitType)) {
            return price;
        }

        return 0;
    }

    /**
     * Fileds mapping
     */
    //Base price
    private void loadBasePrice() {
        try {
            if (savedJson == null) {
                return;
            }
            JSONObject basicInfo = savedJson.optJSONObject("basicInformation");
            if (basicInfo == null) {
                return;
            }

            int layers = Integer.parseInt(basicInfo.optString("layerCountCombo", "0"));

            JSONArray arr = getPriceArray("basePrice");

            double price = 0;

            for (int i = 0; i < arr.length(); i++) {

                JSONObject obj = arr.getJSONObject(i);

                int dbLayers = obj.optInt("layers");

                if (dbLayers == layers) {

                    price = convertToPerPanel(obj);
                    break;
                }
            }

            basePriceField.setText(String.format("%.2f", price));

            calculateSumOfA();

            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    //material price
    private void loadMaterialPrice() {

        try {
            if (savedJson == null) {
                return;
            }

            JSONObject basicInfo = savedJson.optJSONObject("basicInformation");

            if (basicInfo == null) {
                return;
            }

            String material = basicInfo.optString("materialTypeCombo");

            JSONArray arr = getPriceArray("materialPrice");

            double price = findConvertedPrice(arr, "material", material);

            materialField.setText(String.format("%.2f", price));

            calculateSumOfA();

            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    //====================================================
// LOAD COPPER THICKNESS PRICE
//====================================================
    private void loadCopperThicknessPrice() {
        try {
            innerCuField.setText("0.00");
            outerCuField.setText("0.00");

            JSONArray rows = stackupJson.optJSONArray("rows");

            if (rows == null) {
                return;
            }

            // ALL COPPER LAYERS
            List<JSONObject> copperLayers = new ArrayList<>();

            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                String type = row.optString("type");
                String displayName = row.optString("displayName");

                if ("COPPER_LAYER".equalsIgnoreCase(type)
                        && displayName.contains("Layer")) {
                    copperLayers.add(row);
                }
            }

            if (copperLayers.isEmpty()) {
                return;
            }

            JSONArray masterArr = getPriceArray("copperThickness");

            double innerTotal = 0;
            double outerTotal = 0;

            for (int i = 0; i < copperLayers.size(); i++) {
                JSONObject layer = copperLayers.get(i);
                double thicknessInch = layer.optDouble("thickness");
                // INCH -> OZ
                double oz = convertThicknessToOz(thicknessInch);

                // FIND NEAREST MATCH
                JSONObject priceObj = findNearestCopperMatch(masterArr, oz);

                if (priceObj == null) {
                    continue;
                }
                boolean isOuter = (i == 0) || (i == copperLayers.size() - 1);

                String unitType = priceObj.optString("unitType");
                if (isOuter) {
                    double outerPrice
                            = Double.parseDouble(
                                    priceObj.get(
                                            "outer")
                                            .toString());

                    outerTotal += convertToPerPanel(
                            new JSONObject()
                                    .put("unitType", unitType)
                                    .put("price", outerPrice));

                } else {
                    double innerPrice
                            = Double.parseDouble(
                                    priceObj.get(
                                            "inner")
                                            .toString());

                    innerTotal += convertToPerPanel(
                            new JSONObject()
                                    .put("unitType", unitType)
                                    .put("price", innerPrice));
                }
            }
            innerCuField.setText(String.format("%.2f", innerTotal));
            outerCuField.setText(String.format("%.2f", outerTotal));

            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

//====================================================
// CONVERT COPPER THICKNESS
// INCH -> OZ
//====================================================
    private double convertThicknessToOz(double thicknessInch) {
        // 1 oz copper ≈ 1.37 mil
        double mil = thicknessInch * 1000.0;

        return Math.round((mil / 1.37) * 100.0) / 100.0;
    }

//====================================================
// FIND NEAREST OZ MATCH
//====================================================
    private JSONObject findNearestCopperMatch(JSONArray array, double ozValue) {
        JSONObject nearest = null;
        double nearestDiff = Double.MAX_VALUE;

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            double dbOz = Double.parseDouble(obj.get("thickness").toString());
            double diff = Math.abs(dbOz - ozValue);
            if (diff < nearestDiff) {
                nearestDiff = diff;
                nearest = obj;
            }
        }

        return nearest;
    }

    //====================================================
// SURFACE FINISH PRICE
//====================================================
    private void loadSurfaceFinishPrice() {
        try {
            surfaceFinishField.setText("0.00");
            if (savedJson == null) {
                return;
            }
            JSONObject basicInfo = savedJson.optJSONObject("basicInformation");

            if (basicInfo == null) {
                return;
            }

            String finish = basicInfo.optString("surfaceFinishCombo", "").trim().toLowerCase();

            if (finish.isEmpty()) {
                return;
            }

            JSONArray arr = getPriceArray("surfaceFinish");
            double price = 0;

            for (int i = 0; i < arr.length(); i++) {

                JSONObject obj = arr.getJSONObject(i);

                String type
                        = obj.optString(
                                "type",
                                "")
                                .trim()
                                .toLowerCase();

                if (finish.equals(type)) {
                    price = convertToPerPanel(obj);
                    break;
                }
            }

            surfaceFinishField.setText(String.format("%.2f", price));
            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

//====================================================
// DRILLING PRICE
//====================================================
    private void loadDrillingPrice() {
        try {
            drillingField.setText("0.00");
            if (stackupJson == null || savedJson == null) {
                return;
            }
            JSONObject basicInfo = savedJson.optJSONObject("basicInformation");
            if (basicInfo == null) {
                return;
            }
            int layerCount = Integer.parseInt(basicInfo.optString("layerCountCombo", "0"));

            JSONArray drills = stackupJson.optJSONArray("drills");

            if (drills == null || drills.length() == 0) {
                return;
            }

            JSONArray drillPriceArr = getPriceArray("drilling");
            double total = 0;
            for (int i = 0; i < drills.length(); i++) {
                JSONObject drill = drills.getJSONObject(i);
                int start = drill.optInt("startLayer");
                int end = drill.optInt("endLayer");
                String drillType = "";

                // MAIN DRILL
                if (start == 1 && end == layerCount) {
                    drillType = "Main Drill";
                } // BLIND
                else if (start == 1 || end == layerCount) {
                    drillType = "Blind";

                } // BURIED
                else {
                    drillType = "Burried";
                }

                // FIND PRICE
                for (int j = 0; j < drillPriceArr.length(); j++) {

                    JSONObject obj = drillPriceArr.getJSONObject(j);

                    String type
                            = obj.optString(
                                    "type",
                                    "")
                                    .trim()
                                    .toLowerCase();

                    if (type.equals(drillType.toLowerCase())) {
                        total += convertToPerPanel(obj);
                        break;
                    }
                }
            }

            drillingField.setText(String.format("%.2f", total));

            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

//====================================================
// ROUTING PRICE
//====================================================
    private void loadRoutingPrice() {
        try {
            routingField.setText("0.00");
            if (savedJson == null) {
                return;
            }
            JSONObject mech = savedJson.optJSONObject("mechanicalFabrication");
            if (mech == null) {
                return;
            }
            String breakApart = mech.optString("breakApartCombo", "").trim().toLowerCase();
            String routingType = breakApart.equals("yes") ? "Complex" : "Normal";
            JSONArray arr = getPriceArray("routing");
            double price = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                // IGNORE FLAT
                String unitType = obj.optString("unitType", "").trim();
                if (FLAT.equalsIgnoreCase(unitType)) {
                    continue;
                }
                String type = obj.optString("type", "").trim().toLowerCase();
                if (type.equals(routingType.toLowerCase())) {
                    price = convertToPerPanel(obj);
                    break;
                }
            }

            routingField.setText(String.format("%.2f", price));
            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

//====================================================
// SCORING PRICE
//====================================================
    private void loadScoringPrice() {
        try {
            scoringField.setText("0.00");
            if (savedJson == null) {
                return;
            }
            JSONObject mech = savedJson.optJSONObject("mechanicalFabrication");
            if (mech == null) {
                return;
            }
            String scoringType = mech.optString("scoringCombo", "").trim();

            if (scoringType.isEmpty() || scoringType.equalsIgnoreCase("No")) {
                return;
            }

            JSONArray arr = getPriceArray("scoring");
            double price = 0;
            for (int i = 0; i < arr.length(); i++) {

                JSONObject obj = arr.getJSONObject(i);

                // IGNORE FLAT
                String unitType = obj.optString("unitType", "").trim();

                if (FLAT.equalsIgnoreCase(unitType)) {
                    continue;
                }

                String type = obj.optString("type", "").trim().toLowerCase();

                if (type.equals(scoringType.toLowerCase())) {
                    price = convertToPerPanel(obj);
                    break;
                }
            }

            scoringField.setText(String.format("%.2f", price));
            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSolderMaskPrice() {
        try {
            solderMaskField.setText("0.00");
            if (savedJson == null) {
                return;
            }
            JSONObject inks = savedJson.optJSONObject("inksCoatings");
            if (inks == null) {
                return;
            }
            String color = inks.optString("soldermaskCombo").trim();
            if (color.isEmpty()) {
                return;
            }
            JSONArray arr = getPriceArray("maskLegend");
            double price = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String dbColor = obj.optString("color").trim().toLowerCase();
                if (dbColor.equals(color.toLowerCase())) {
                    double maskPrice = obj.optDouble("mask");
                    String unitType = obj.optString("unitType");

                    JSONObject temp = new JSONObject()
                            .put("unitType", unitType)
                            .put("price", maskPrice);

                    price = convertToPerPanel(temp);
                    break;
                }
            }

            solderMaskField.setText(String.format("%.2f", price));
            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private void loadLegendPrice() {
        try {
            legendField.setText("0.00");
            if (savedJson == null) {
                return;
            }
            JSONObject inks = savedJson.optJSONObject("inksCoatings");
            if (inks == null) {
                return;
            }
            String color = inks.optString("legendCombo").trim();
            if (color.isEmpty()) {
                return;
            }
            JSONArray arr = getPriceArray("maskLegend");
            double price = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String dbColor = obj.optString("color").trim().toLowerCase();
                if (dbColor.equals(color.toLowerCase())) {
                    double legendPrice = obj.optDouble("legend");
                    String unitType = obj.optString("unitType");
                    JSONObject temp
                            = new JSONObject()
                                    .put("unitType", unitType)
                                    .put("price", legendPrice);
                    price = convertToPerPanel(temp);
                    break;
                }
            }

            legendField.setText(String.format("%.2f", price));
            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private void loadSpecialProcessPrices() {
        try {
            specialProcessContainer.getChildren().clear();
            JSONArray masterArr = getPriceArray("specialProcess");
            if (masterArr == null || masterArr.length() == 0) {
                return;
            }
            double total = 0;
            // =========================================
            // ALL SAVED JSON SECTIONS
            // =========================================
            List<JSONObject> sections = new ArrayList<>();
            if (savedJson.optJSONObject("inksCoatings") != null) {
                sections.add(
                        savedJson.getJSONObject(
                                "inksCoatings"));
            }
            if (savedJson.optJSONObject("specialProces") != null) {
                sections.add(savedJson.getJSONObject("specialProces"));
            }
            if (savedJson.optJSONObject("mechanicalFabrication") != null) {
                sections.add(savedJson.getJSONObject("mechanicalFabrication"));
            }
            if (savedJson.optJSONObject("qualityTesting") != null) {
                sections.add(savedJson.getJSONObject("qualityTesting"));
            }
            // =========================================
            // LOOP ALL MAPPINGS
            // =========================================
            for (String savedKey : SPECIAL_PROCESS_MAPPING.keySet()) {
                String masterLookup
                        = SPECIAL_PROCESS_MAPPING
                                .optString(savedKey)
                                .trim()
                                .toLowerCase();
                String savedValue = null;
                // =====================================
                // FIND VALUE FROM ANY SECTION
                // =====================================
                for (JSONObject section : sections) {
                    if (section.has(savedKey)) {
                        savedValue = section.optString(savedKey).trim();
                        break;
                    }
                }
                // NOT FOUND
                if (savedValue == null || savedValue.isEmpty()) {
                    continue;
                }
                // IGNORE NO
                if (savedValue.equalsIgnoreCase("No")) {
                    continue;
                }
                // =====================================
                // FIND MASTER PRICE
                // =====================================
                JSONObject matchedObj = null;
                for (int i = 0; i < masterArr.length(); i++) {
                    JSONObject obj = masterArr.getJSONObject(i);
                    String process = obj.optString("process").trim().toLowerCase();
                    if (process.equals(masterLookup)) {
                        matchedObj = obj;
                        break;
                    }
                }
                if (matchedObj == null) {
                    continue;
                }
                double price = convertToPerPanel(matchedObj);
                total += price;
                addSpecialProcessRow(SPECIAL_PROCESS_MAPPING.optString(savedKey), price);
            }

            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addSpecialProcessRow(String label, double price) {
        HBox row = new HBox(5);
        Label lbl = new Label(label);
        lbl.setPrefWidth(120);
        TextField tf = new TextField(String.format("%.2f", price));
        tf.setEditable(false);
        row.getChildren().addAll(lbl, tf);
        specialProcessContainer
                .getChildren()
                .add(row);
    }

    private void loadNrcItems() {
        try {
            JSONArray arr = getPriceArray("nrc");
            if (arr == null || arr.length() == 0) {
                return;
            }
            double total = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String label = obj.optString("nrc");
                double price = convertToPerPanel(obj);
                total += price;
                addNrcRow(label, price);
            }

            sumOfBField.setText(String.format("%.2f", total));
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private void addNrcRow(String label, double price) {
        HBox row = new HBox(10);
        TextField labelField = new TextField(label);
        labelField.setPrefWidth(180);
        TextField valueField = new TextField(String.format("%.2f", price));
        Button removeBtn = new Button("-");

        valueField.textProperty().addListener(
                (obs, oldV, newV) -> {
                    calculateNrcTotal();
                    recalculateAllRows();
                });

        removeBtn.setOnAction(e -> {
            nrcContainer.getChildren().remove(row);
            calculateNrcTotal();
            recalculateAllRows();
        });

        row.getChildren().addAll(
                labelField,
                valueField,
                removeBtn);

        nrcContainer.getChildren().add(row);
    }

    private int calculateLaunchPanels(int quantity) {
        try {
            if (optimizerJson == null || savedJson == null) {
                return 0;
            }
            int pcbPerPanel = optimizerJson.optInt("PcbCount", 1);
            if (pcbPerPanel <= 0) {
                pcbPerPanel = 1;
            }

            // GET LAYER COUNT
            JSONObject basicInfo = savedJson.optJSONObject("basicInformation");
            int layerCount = 0;
            if (basicInfo != null) {
                layerCount = Integer.parseInt(
                        basicInfo.optString(
                                "layerCountCombo",
                                "0"));
            }
            // GET REJECTION %
            double rejectionPercent = 0;
            JSONArray rejectionArr = getPriceArray("rejectionFactor");
            for (int i = 0; i < rejectionArr.length(); i++) {
                JSONObject obj = rejectionArr.getJSONObject(i);
                int dbLayer = obj.optInt("layerCount");
                if (dbLayer == layerCount) {
                    rejectionPercent = obj.optDouble("rejectionPercent");
                    break;
                }
            }
            // PANEL CALCULATION
            double panelQty = (double) quantity / pcbPerPanel;
            double finalPanels = panelQty * (1 + (rejectionPercent / 100.0));
            return (int) Math.ceil(finalPanels);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private int convertDeliveryToDays(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return 0;
            }
            value = value.trim().toLowerCase();
            // EXTRACT NUMBER
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(value);
            if (!matcher.find()) {
                return 0;
            }

            int number = Integer.parseInt(matcher.group(1));
            // DAYS
            if (value.contains("day")) {
                return number;
            }
            // WEEKS
            if (value.contains("week")) {
                return number * 7;
            }
            // MONTHS
            if (value.contains("month")) {
                return number * 30;
            }
            return number;

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    private double getDeliveryPrice(String deliveryText) {
        try {
            int days = convertDeliveryToDays(deliveryText);
            JSONArray arr = getPriceArray("deliveryPrice");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int min = obj.optInt("minDays");
                int max = obj.optInt("maxDays");
                if (days >= min && days <= max) {
                    return convertToPerPanel(obj);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    private JSONObject buildCalculationJson() {
        JSONObject root = new JSONObject();
        root.put("version", versionCombo.getValue());
        root.put("masterPriceVersion", masterPriceVersion);
        // PRICING
        JSONObject pricing = new JSONObject();

        pricing.put("basePrice", parseField(basePriceField));
        pricing.put("material", parseField(materialField));
        pricing.put("innerCopper", parseField(innerCuField));
        pricing.put("outerCopper", parseField(outerCuField));
        pricing.put("surfaceFinish", parseField(surfaceFinishField));
        pricing.put("drilling", parseField(drillingField));
        pricing.put("routing", parseField(routingField));
        pricing.put("scoring", parseField(scoringField));
        pricing.put("solderMask", parseField(solderMaskField));
        pricing.put("legend", parseField(legendField));

        // SPECIAL PROCESS
        JSONArray specialArr = new JSONArray();

        for (javafx.scene.Node node : specialProcessContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                if (row.getChildren().size() >= 2) {
                    Label lbl = (Label) row.getChildren().get(0);
                    TextField tf = (TextField) row.getChildren().get(1);
                    JSONObject obj = new JSONObject();
                    obj.put("name", lbl.getText());
                    obj.put("price", parseField(tf));
                    specialArr.put(obj);
                }
            }
        }
        pricing.put("specialProcesses", specialArr);
        root.put("pricing", pricing);
        // NRC
        JSONArray nrcArr = new JSONArray();
        for (javafx.scene.Node node : nrcContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                if (row.getChildren().size() >= 2) {
                    TextField labelField = (TextField) row.getChildren().get(0);
                    TextField valueField = (TextField) row.getChildren().get(1);
                    JSONObject obj = new JSONObject();
                    obj.put("name", labelField.getText());
                    obj.put("price", parseField(valueField));
                    nrcArr.put(obj);
                }
            }
        }
        root.put("nrc", nrcArr);
        // QUANTITY TABLE
        JSONArray rowsArr = new JSONArray();
        for (QuantityRow row : rows) {
            JSONObject obj = new JSONObject();

            obj.put("quantity", row.getQuantity());
            obj.put("delivery", row.getDelivery());
            obj.put("launchPanels", row.getLaunchingPanel());
            obj.put("unitPriceA", row.getUnitPriceA());
            obj.put("nrcB", row.getNrcB());
            obj.put("calculatedPrice", row.getCalculatedPrice());
            obj.put("discount", row.getDiscount());
            obj.put("finalPrice", row.getFinalPrice());

            rowsArr.put(obj);
        }
        root.put("rows", rowsArr);

        return root;
    }

    private void loadVersions() {
        try {
            versionCombo.getItems().clear();
            List<Integer> versions = CalculationService.getVersions(getSubmitId());

            // NO SAVED VERSION
            if (versions == null || versions.isEmpty()) {
                versionCombo.getItems().add(1);
                versionCombo.setValue(1);
                return;
            }
            // LOAD AVAILABLE VERSIONS
            versionCombo.getItems().addAll(versions);
            Integer latest
                    = versions.stream()
                            .max(Integer::compareTo)
                            .orElse(1);
            versionCombo.setValue(latest);

            // LOAD SAVED DATA
            JSONObject json = CalculationService.loadCalculation(getSubmitId(), latest);
            if (json != null) {
                loadCalculationData(json);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSelectedVersion(int version) {
        try {
            JSONObject json = CalculationService.loadCalculation(getSubmitId(), version);
            if (json == null) {
                return;
            }
            loadCalculationData(json);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCalculationData(JSONObject json) {
        try {
            specialProcessContainer.getChildren().clear();
            nrcContainer.getChildren().clear();
            rows.clear();
            masterPriceVersion = json.optInt("masterPriceVersion", 1);
            masterPriceVersionLable.setText("Master Price Version: " + masterPriceVersion);
            JSONObject masterRoot = CalculationService.getMasterPrice(masterPriceVersion);
            if (masterRoot != null) {
                masterPriceJson = masterRoot.optJSONObject("data");
                System.out.println("Loaded Master Version = " + masterPriceVersion);
            }
            
            // PRICING
            JSONObject pricing = json.optJSONObject("pricing");

            if (pricing != null) {
                basePriceField.setText(String.format("%.2f", pricing.optDouble("basePrice")));
                materialField.setText(String.format("%.2f", pricing.optDouble("material")));
                innerCuField.setText(String.format("%.2f", pricing.optDouble("innerCopper")));
                outerCuField.setText(String.format("%.2f", pricing.optDouble("outerCopper")));
                surfaceFinishField.setText(String.format("%.2f", pricing.optDouble("surfaceFinish")));
                drillingField.setText(String.format("%.2f", pricing.optDouble("drilling")));
                routingField.setText(String.format("%.2f", pricing.optDouble("routing")));
                scoringField.setText(String.format("%.2f", pricing.optDouble("scoring")));
                solderMaskField.setText(String.format("%.2f", pricing.optDouble("solderMask")));
                legendField.setText(String.format("%.2f", pricing.optDouble("legend")));

                // SPECIAL PROCESS
                specialProcessContainer.getChildren().clear();
                JSONArray spArr = pricing.optJSONArray("specialProcesses");
                if (spArr != null) {
                    for (int i = 0; i < spArr.length(); i++) {

                        JSONObject obj = spArr.getJSONObject(i);

                        addSpecialProcessRow(obj.optString("name"), obj.optDouble("price"));
                    }
                }
            }

            // NRC
            nrcContainer.getChildren().clear();
            JSONArray nrcArr = json.optJSONArray("nrc");
            if (nrcArr != null) {
                for (int i = 0; i < nrcArr.length(); i++) {
                    JSONObject obj = nrcArr.getJSONObject(i);
                    addNrcRow(obj.optString("name"), obj.optDouble("price"));
                }
            }
            // QUANTITY TABLE
            rows.clear();

            JSONArray rowsArr = json.optJSONArray("rows");

            if (rowsArr != null) {
                for (int i = 0; i < rowsArr.length(); i++) {
                    JSONObject obj = rowsArr.getJSONObject(i);
                    QuantityRow row = new QuantityRow();
                    row.setQuantity(obj.optInt("quantity"));
                    row.setDelivery(obj.optString("delivery"));
                    row.setLaunchingPanel(obj.optInt("launchPanels"));
                    row.setUnitPriceA(obj.optDouble("unitPriceA"));
                    row.setNrcB(obj.optDouble("nrcB"));
                    row.setCalculatedPrice(obj.optDouble("calculatedPrice"));
                    row.setDiscount(obj.optDouble("discount"));
                    row.setFinalPrice(obj.optDouble("finalPrice"));

                    rows.add(row);
                }
            }
            quantityTable.refresh();

            calculateNrcTotal();
            calculateSumOfA();
            recalculateAllRows();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
