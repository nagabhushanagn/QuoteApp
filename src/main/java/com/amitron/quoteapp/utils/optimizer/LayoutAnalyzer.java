/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils.optimizer;

import com.amitron.quoteapp.model.optimizer.OptimizationResult;
import com.amitron.quoteapp.model.optimizer.Placement;
import java.util.*;

/**
 *
 * @author Ngn
 */

public class LayoutAnalyzer {

    private static final double TOL = 0.01;

    public static Map<String, Object> analyze(
            OptimizationResult result,
            double panelW,
            double panelH) {

        Map<String, Object> map = new HashMap<>();

        List<Placement> all = result.getPlacements();
        if (all == null || all.isEmpty()) return map;

        /* ===== SPLIT ROTATED / NON-ROTATED ===== */
        List<Placement> normal = new ArrayList<>();
        List<Placement> rotated = new ArrayList<>();

        for (Placement p : all) {
            if (p.isRotated()) rotated.add(p);
            else normal.add(p);
        }

        boolean hasRot = !rotated.isEmpty();
        boolean hasNorm = !normal.isEmpty();
        map.put("IsMixedRotation", hasRot && hasNorm);

        /* ===== BORDER (SIMPLE SYMMETRIC) ===== */
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Placement p : all) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX() + p.getWidth());

            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY() + p.getHeight());
        }

        double totalW = maxX - minX;
        double totalH = maxY - minY;

        double gapX = Math.max(0, (panelW - totalW) / 2.0);
        double gapY = Math.max(0, (panelH - totalH) / 2.0);

        map.put("LeftOutPanelBorderLeft", round(gapX));
        map.put("LeftOutPanelBorderRight", round(gapX));
        map.put("LeftOutPanelBorderTop", round(gapY));
        map.put("LeftOutPanelBorderBottom", round(gapY));

        /* ===== FOR MIXED ROTATIONS ===== */
        if(hasRot && hasNorm){
            /* ===== NON-ROTATED GRID ===== */
            if (hasNorm) {
                int gridX = countGroups(getXList(normal));
                int gridY = countGroups(getYList(normal));

                map.put("GridMatrixX", gridX);
                map.put("GridMatrixY", gridY);
            } else {
                map.put("GridMatrixX", 0);
                map.put("GridMatrixY", 0);
            }

            /* ===== ROTATED GRID ===== */
            if (hasRot) {
                int rotX = countGroups(getXList(rotated));
                int rotY = countGroups(getYList(rotated));

                map.put("RotatedMatrixX", rotX);
                map.put("RotatedMatrixY", rotY);
            } else {
                map.put("RotatedMatrixX", 0);
                map.put("RotatedMatrixY", 0);
            }
        }else{//FOR NO MIX RORATION(SINGLE GRID)
            if (hasNorm) {
                int gridX = countGroups(getXList(normal));
                int gridY = countGroups(getYList(normal));

                map.put("GridMatrixX", gridX);
                map.put("GridMatrixY", gridY);
            } else {
                int rotX = countGroups(getXList(rotated));
                int rotY = countGroups(getYList(rotated));

                map.put("GridMatrixX", rotX);
                map.put("GridMatrixY", rotY);
            }
        }
            
        return map;
    }

    /* ===== HELPERS ===== */

    private static List<Double> getXList(List<Placement> list) {
        List<Double> xList = new ArrayList<>();
        for (Placement p : list) {
            xList.add(p.getX());
        }
        Collections.sort(xList);
        return xList;
    }

    private static List<Double> getYList(List<Placement> list) {
        List<Double> yList = new ArrayList<>();
        for (Placement p : list) {
            yList.add(p.getY());
        }
        Collections.sort(yList);
        return yList;
    }

    private static int countGroups(List<Double> list) {
        if (list.isEmpty()) return 0;

        int count = 1;
        double prev = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(list.get(i) - prev) > TOL) {
                count++;
                prev = list.get(i);
            }
        }

        return count;
    }

    private static double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}