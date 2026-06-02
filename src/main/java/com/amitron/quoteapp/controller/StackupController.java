/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.controller;

import com.amitron.quoteapp.model.*;
import com.amitron.quoteapp.service.StackupService;
import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.utils.ThicknessConverter;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author Ngn
 */
public class StackupController {

    private SubmitRow jobData;

    private StackupService stackupService = new StackupService();
    private Map<Integer, Rectangle> layerRectMap = new HashMap<>();
    private Label totalThicknessLabel = new Label();
    private Label totalPriceLabel = new Label();

    private List<StackRow> rows = new ArrayList<>();
    private List<DrillSpan> drills = new ArrayList<>();

    @FXML
    private TextField layerCountField;

    @FXML
    private TextField materialField;

    @FXML
    private GridPane stackGrid;

    @FXML
    private Pane drillOverlay;

    @FXML
    public void initialize() {

        buildHeader();
    }

    public void setJobData(SubmitRow jobData) {
        this.jobData = jobData;
    }

    public void loadJson(String jsonString) {
        String stackupData = StackupService.loadStackupJsonFromDb(jobData.getRowId());

        if (stackupData != null && !stackupData.isBlank()) {
            loadSavedStackup(stackupData);

            return;
        }

        JSONObject json = new JSONObject(jsonString);

        StackupInput input
                = stackupService.parseStackupInput(json);

        layerCountField.setText(
                String.valueOf(input.getLayerCount())
        );

        materialField.setText(input.getMaterial());

        rows = stackupService.generate(input);

        DrillSpan through = new DrillSpan();
        through.setStartLayer(1);
        through.setEndLayer(input.getLayerCount());
        through.setLabel("drl");
        //through.setX(470);
        drills.clear();
        drills.add(through);

        renderGrid();
    }

    // =====================================================
    // HEADER
    // =====================================================
    private void buildHeader() {

        stackGrid.add(header("Layer"), 0, 0);
        stackGrid.add(header("Graphic"), 1, 0);
        stackGrid.add(header("Material"), 2, 0);
        stackGrid.add(header("Thickness"), 3, 0);
        stackGrid.add(header("Price"), 4, 0);
        stackGrid.add(header("+"), 5, 0);

        //Add Additional drill button
        Button addDrillBtn = new Button("+ Drill");
        addDrillBtn.setOnAction(e -> {
            showAddDrillDialog();
        });

        stackGrid.add(addDrillBtn, 6, 0);
        refreshDrillButtons();
    }

    private Label header(String text) {

        Label lbl = new Label(text);

        lbl.setStyle(
                "-fx-font-weight:bold;"
                + "-fx-padding:5;"
                + "-fx-background-color:#D3D3D3;"
        );

        lbl.setPrefWidth(120);

        return lbl;
    }

    // =====================================================
    // GRID RENDER
    // =====================================================
    private void renderGrid() {
        layerRectMap.clear();
        stackGrid.getChildren().clear();
        buildHeader();
        int rowIndex = 1;
        for (StackRow row : rows) {
            // =================================================
            // LABEL
            // =================================================
            Label name = new Label(row.getDisplayName());
            name.setPrefWidth(120);
            stackGrid.add(name, 0, rowIndex);
            // =================================================
            // GRAPHIC
            // =================================================
            Rectangle rect = createRectangle(row);
            if (row.getType() == RowType.COPPER_LAYER) {
                layerRectMap.put(row.getLayerNumber(), rect);
            }
            stackGrid.add(rect, 1, rowIndex);
            // =================================================
            // MATERIAL
            // =================================================
            TextField material = new TextField(
                    row.getMaterial() != null
                    ? row.getMaterial()
                    : ""
            );
            material.setPrefWidth(180);
            stackGrid.add(material, 2, rowIndex);
            // =================================================
            // THICKNESS
            // =================================================
            TextField thickness = new TextField(String.format("%.4f",row.getThickness()));
            thickness.setOnAction(e -> {
                double inchValue = ThicknessConverter.toInch(thickness.getText());
                row.setThickness(inchValue);

                thickness.setText(String.format("%.4f", inchValue));
                updateTotals();
            });
            thickness.setPrefWidth(100);
            stackGrid.add(thickness, 3, rowIndex);
            // =================================================
            // PRICE 
            // =================================================
            TextField price = new TextField(String.format("%.3f",row.getPrice()));
            price.setPrefWidth(100);
            price.setOnAction(e -> {
                try {
                    double value
                            = Double.parseDouble(
                                    price.getText()
                            );
                    row.setPrice(value);
                    updateTotals();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            stackGrid.add(price, 4, rowIndex);
            // =================================================
            // ADD BUTTON
            // =================================================
            if (row.getType() == RowType.PREPREG) {
                HBox buttonBox = new HBox(5);
                // =========================
                // ADD
                // =========================
                Button addBtn = new Button("+");
                addBtn.setOnAction(e -> {
                    int currentIndex = rows.indexOf(row);
                    StackRow newRow = new StackRow();
                    newRow.setType(RowType.PREPREG);
                    newRow.setDisplayName("Prepreg");
                    newRow.setAdditional(true);
                    rows.add(currentIndex + 1, newRow);

                    renderGrid();
                });
                // =========================
                // REMOVE
                // =========================
                Button removeBtn = new Button("-");
                removeBtn.setDisable(!row.isAdditional());
                removeBtn.setOnAction(e -> {
                    rows.remove(row);
                    renderGrid();
                });
                buttonBox.getChildren().addAll(addBtn, removeBtn);
                stackGrid.add(buttonBox, 5, rowIndex);
            }

            rowIndex++;
            addTotalsRow(rowIndex);
        }

        Platform.runLater(() -> {
            stackGrid.applyCss();
            stackGrid.layout();
            refreshDrillButtons();
            drawDrills();
        });
    }

    // =====================================================
    // RECTANGLES
    // =====================================================
    private Rectangle createRectangle(StackRow row) {

        Color color;
        double height;

        switch (row.getType()) {

            case SILK:
                color = Color.YELLOW;
                height = 14;
                break;

            case MASK:
                color = Color.GREEN;
                height = 18;
                break;

            case COPPER_LAYER:
                color = Color.GOLDENROD;
                height = 24;
                break;

            case PREPREG:
                color = Color.ORANGE;
                height = 20;
                break;

            case CORE:
                color = Color.FIREBRICK;
                height = 28;
                break;

            default:
                color = Color.GRAY;
                height = 10;
        }

        Rectangle rect = new Rectangle(300, height);

        rect.setFill(color);

        rect.setStroke(Color.BLACK);

        return rect;
    }

    // =====================================================
    // DRILL
    // =====================================================
    private void drawDrills() {

        drillOverlay.getChildren().clear();

        for (int i = 0; i < drills.size(); i++) {
            DrillSpan drill = drills.get(i);
            Rectangle startRect
                    = layerRectMap.get(drill.getStartLayer());

            Rectangle endRect
                    = layerRectMap.get(drill.getEndLayer());

            if (startRect == null || endRect == null) {
                continue;
            }

            double y1 = startRect.localToScene(
                    startRect.getBoundsInLocal()
            ).getMinY();

            double y2 = endRect.localToScene(
                    endRect.getBoundsInLocal()
            ).getMaxY();

            double overlayY = drillOverlay.localToScene(
                    drillOverlay.getBoundsInLocal()
            ).getMinY();

            y1 -= overlayY;
            y2 -= overlayY;

            // center align
            y1 += 2;
            y2 -= 2;

            double centerX = 320;
            double spacing = 20;
            double totalWidth = (drills.size() - 1) * spacing;
            double startX = centerX - (totalWidth / 2);
            double drillX = startX + (i * spacing);
            //double drillX = 320;

            Line line = new Line(
                    drillX,
                    y1,
                    drillX,
                    y2
            );

            line.setStrokeWidth(6);

            line.setStroke(Color.WHITE);

            Label lbl = new Label(drill.getLabel());
            lbl.setLayoutX(drillX - 5);
            lbl.setLayoutY(y2 + 10);
            lbl.setStyle(
                    "-fx-font-size:10px;"
                    + "-fx-background-color:black;"
                    + "-fx-text-fill:yellow;"
            );

            drillOverlay.getChildren().addAll(line, lbl);
        }
    }

    private int findLayerRow(int layerNo) {
        int rowIndex = 1;
        for (StackRow row : rows) {
            if (row.getType() == RowType.COPPER_LAYER
                    && row.getLayerNumber() == layerNo) {

                return rowIndex;
            }
            rowIndex++;
        }

        return 1;
    }

    private double calculateRowY(int row) {
        double rowHeight = 32;
        double topOffset = 25;

        return topOffset + (row * rowHeight) + 12;
    }

    private void showAddDrillDialog() {
        TextInputDialog startDialog
                = new TextInputDialog();
        startDialog.setHeaderText("Enter Start Layer");

        startDialog.showAndWait().ifPresent(start -> {
            TextInputDialog endDialog
                    = new TextInputDialog();
            endDialog.setHeaderText("Enter End Layer");
            endDialog.showAndWait().ifPresent(end -> {
                try {
                    int startLayer = Integer.parseInt(start);
                    int endLayer = Integer.parseInt(end);
                    DrillSpan drill = new DrillSpan();
                    drill.setStartLayer(startLayer);
                    drill.setEndLayer(endLayer);
                    drill.setLabel("drill" + drills.size());
                    drills.add(drill);

                    renderGrid();

                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            });
        });
    }

    private void refreshDrillButtons() {
        // remove old drill controls
        stackGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            int actualRow = row == null ? 0 : row;

            return actualRow == 0
                    && col != null
                    && col >= 7;
        });

        int colIndex = 7;
        for (int i = 1; i < drills.size(); i++) {
            DrillSpan drill = drills.get(i);
            HBox box = new HBox(3);
            Label lbl = new Label(drill.getLabel());
            Button removeBtn = new Button("-");
            int index = i;
            removeBtn.setOnAction(e -> {
                drills.remove(index);
                renderGrid();
            });
            box.getChildren().addAll(lbl, removeBtn);
            stackGrid.add(box, colIndex++, 0);
        }
    }

    private void addTotalsRow(int rowIndex) {
        // remove previous totals row
        stackGrid.getChildren().removeAll(
                totalThicknessLabel,
                totalPriceLabel
        );

        stackGrid.getChildren().removeIf(node -> {
            if (!(node instanceof Label)) {
                return false;
            }
            Label lbl = (Label) node;

            return "TOTAL".equals(lbl.getText());
        });

        double totalThickness = rows.stream()
                .mapToDouble(StackRow::getThickness)
                .sum();

        double totalPrice = rows.stream()
                .mapToDouble(StackRow::getPrice)
                .sum();

        Label totalLbl = new Label("TOTAL");
        totalLbl.setStyle("-fx-font-weight:bold;");
        totalThicknessLabel.setText(String.format("%.4f", totalThickness));
        totalPriceLabel.setText(String.format("%.3f", totalPrice));
        totalThicknessLabel.setStyle("-fx-font-weight:bold;");
        totalPriceLabel.setStyle("-fx-font-weight:bold;");
        stackGrid.add(totalLbl, 2, rowIndex);
        stackGrid.add(totalThicknessLabel, 3, rowIndex);
        stackGrid.add(totalPriceLabel, 4, rowIndex);
    }

    private void updateTotals() {
        double totalThickness = rows.stream()
                .mapToDouble(StackRow::getThickness)
                .sum();

        double totalPrice = rows.stream()
                .mapToDouble(StackRow::getPrice)
                .sum();

        totalThicknessLabel.setText(String.format("%.4f", totalThickness));
        totalPriceLabel.setText(String.format("%.3f", totalPrice));
    }

    @FXML
    private void saveStackup() {
        try {
            JSONObject root = new JSONObject();

            // =====================================
            // ROWS
            // =====================================
            JSONArray rowsArray = new JSONArray();

            for (StackRow row : rows) {
                JSONObject obj = new JSONObject();
                obj.put("type", row.getType().name());
                obj.put("displayName", row.getDisplayName());
                obj.put("material", row.getMaterial());
                obj.put("thickness", Double.parseDouble(String.format("%.4f",row.getThickness())));

                
                obj.put("price", Double.parseDouble(String.format("%.3f",row.getPrice())));
                obj.put("layerNumber", row.getLayerNumber());
                obj.put("additional", row.isAdditional());

                rowsArray.put(obj);
            }

            root.put("rows", rowsArray);

            // =====================================
            // DRILLS
            // =====================================
            JSONArray drillArray = new JSONArray();

            for (DrillSpan drill : drills) {
                JSONObject obj = new JSONObject();
                obj.put("startLayer", drill.getStartLayer());
                obj.put("endLayer", drill.getEndLayer());
                obj.put("label", drill.getLabel());
                drillArray.put(obj);
            }

            root.put("drills", drillArray);

            // =====================================
            // SAVE TO DB
            // =====================================
            StackupService.saveStackupJsonToDb(jobData.getRowId(), root.toString(4));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Stackup saved successfully");

            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSavedStackup(String jsonText) {
        try {
            if (rows == null) {
                rows = new ArrayList<>();
            }
            if (drills == null) {
                drills = new ArrayList<>();
            }
            rows.clear();
            drills.clear();

            JSONObject root = new JSONObject(jsonText);
            // =====================================
            // ROWS
            // =====================================
            JSONArray rowsArray = root.getJSONArray("rows");

            for (int i = 0; i < rowsArray.length(); i++) {
                JSONObject obj = rowsArray.getJSONObject(i);
                StackRow row = new StackRow();

                row.setType(RowType.valueOf(obj.getString("type")));
                row.setDisplayName(obj.optString("displayName"));
                row.setMaterial(obj.optString("material"));
                row.setThickness(obj.optDouble("thickness"));
                row.setPrice(obj.optDouble("price"));
                row.setLayerNumber(obj.optInt("layerNumber"));
                row.setAdditional(obj.optBoolean("additional"));

                rows.add(row);
            }

            // =====================================
            // DRILLS
            // =====================================
            JSONArray drillArray = root.getJSONArray("drills");

            for (int i = 0; i < drillArray.length(); i++) {
                JSONObject obj = drillArray.getJSONObject(i);
                DrillSpan drill = new DrillSpan();
                drill.setStartLayer(obj.getInt("startLayer"));
                drill.setEndLayer(obj.getInt("endLayer"));
                drill.setLabel(obj.getString("label"));
                drills.add(drill);
            }
            renderGrid();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
