/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service;

import com.amitron.quoteapp.model.*;
import com.amitron.quoteapp.repository.I8JobSubmitRepository;
import java.util.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.json.JSONObject;
/**
 *
 * @author Ngn
 */


public class StackupService {

    // ================= JSON PARSER =================
    public StackupInput parseStackupInput(JSONObject root) {

        StackupInput input = new StackupInput();

        JSONObject basic = root.getJSONObject("basicInformation");
        JSONObject inks = root.getJSONObject("inksCoatings");

        input.setLayerCount(Integer.parseInt(basic.getString("layerCountCombo")));
        input.setMaterial(basic.getString("materialTypeCombo"));
        input.setConstructionType(basic.optString("constructionCombo", "Standard"));

        // Silk
        String legendSide = inks.optString("legendSideCombo", "None");
        input.setSilkTop(legendSide.contains("2") || legendSide.contains("Top"));
        input.setSilkBottom(legendSide.contains("2") || legendSide.contains("Bottom"));

        // Mask
        String maskSide = inks.optString("soldermaskSideCombo", "None");
        input.setMaskTop(maskSide.contains("2") || maskSide.contains("Top"));
        input.setMaskBottom(maskSide.contains("2") || maskSide.contains("Bottom"));

        return input;
    }

    // ================= MAIN GENERATOR =================
    public List<StackRow> generate(StackupInput input) {

        if ("Filler Core".equalsIgnoreCase(input.getConstructionType())) {
            return generateCore(input);
        } else {
            return generateNormal(input);
        }
    }

    // ================= NORMAL =================
    private List<StackRow> generateNormal(StackupInput input) {

        int layerCount = input.getLayerCount();

        List<StackRow> rows = new ArrayList<>();

        if (input.isSilkTop()) rows.add(row(RowType.SILK, "Silk"));
        if (input.isMaskTop()) rows.add(row(RowType.MASK, "Mask"));

        int layer = 1;

        while (layer <= layerCount) {

            StackRow copper = row(RowType.COPPER_LAYER, "Layer " + layer, layer);
            copper.setMaterial(input.getMaterial());
            rows.add(copper);

            if (layer == layerCount) break;

            if (layer % 2 == 1) {
                rows.add(row(RowType.PREPREG, "Prepreg"));
            } else {
                rows.add(row(RowType.CORE, "Core"));
            }

            layer++;
        }

        if (input.isMaskBottom()) rows.add(row(RowType.MASK, "Mask"));
        if (input.isSilkBottom()) rows.add(row(RowType.SILK, "Silk"));

        //rows.add(row(RowType.DRILL, "Drill"));

        return rows;
    }

    // ================= CORE =================
    private List<StackRow> generateCore(StackupInput input) {

        int layerCount = input.getLayerCount();
        List<StackRow> rows = new ArrayList<>();

        int layer = 1;

        while (layer <= layerCount) {

            rows.add(row(RowType.CORE, "Layer " + layer + "-" + (layer + 1)));

            layer += 2;

            if (layer <= layerCount) {
                rows.add(row(RowType.PREPREG, "Prepreg"));
            }
        }

        //rows.add(row(RowType.DRILL, "Drill"));

        return rows;
    }

    // ================= VISUAL =================
    public VBox buildStackVisual(List<StackRow> rows) {

        VBox box = new VBox(2);

        for (StackRow row : rows) {

            Color color;
            double height;

            switch (row.getType()) {
                case SILK: color = Color.YELLOW; height = 12; break;
                case MASK: color = Color.GREEN; height = 14; break;
                case COPPER_LAYER: color = Color.GOLDENROD; height = 18; break;
                case PREPREG: color = Color.ORANGE; height = 16; break;
                case CORE: color = Color.BROWN; height = 22; break;
                case DRILL: color = Color.GRAY; height = 10; break;
                default: color = Color.LIGHTGRAY; height = 15;
            }

            Rectangle rect = new Rectangle(250, height);
            rect.setFill(color);
            rect.setStroke(Color.BLACK);

            Label label = new Label(row.getDisplayName());
            label.setPrefWidth(100);

            HBox h = new HBox(10, label, rect);
            h.setAlignment(Pos.CENTER_LEFT);

            box.getChildren().add(h);
        }

        return box;
    }
    
    //============== Stackup save ================
    public static void saveStackupJsonToDb(int rowId, String toString) {
        I8JobSubmitRepository.saveOrUpdateStackupData(rowId, toString);
    }
    
    public static String loadStackupJsonFromDb(int rowId) {
        return I8JobSubmitRepository.loadStackupData(rowId);
    }

    // ================= HELPER =================
    private StackRow row(RowType type, String name) {
        StackRow r = new StackRow();
        r.setType(type);
        r.setDisplayName(name);
        return r;
    }

    private StackRow row(RowType type, String name, int layerNo) {
        StackRow r = row(type, name);
        r.setLayerNumber(layerNo);
        return r;
    }
}
