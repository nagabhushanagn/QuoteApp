/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service.optimizer;

import com.amitron.quoteapp.model.optimizer.ArrayDefinition;
import com.amitron.quoteapp.model.optimizer.Board;
import com.amitron.quoteapp.model.optimizer.Panel;
import com.amitron.quoteapp.model.optimizer.OptimizationResult;
import com.amitron.quoteapp.model.optimizer.Placement;
import com.amitron.quoteapp.utils.optimizer.LayoutAnalyzer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ngn
 */
public class PanelOptimizer {

    public OptimizationResult optimize(
            Panel panel,
            Board board,
            ArrayDefinition arrayDef,
            boolean useArray,
            boolean mixedRotation,
            double gapX,
            double gapY,
            double borderX,
            double borderY) {

        double panelW = panel.getUsableWidth();
        double panelH = panel.getUsableHeight();

        if (useArray && arrayDef != null) {
            board = ArrayCalculator.toBoard(arrayDef);
        }

        double bw = board.getWidth();
        double bh = board.getHeight();

        OptimizationResult best = new OptimizationResult();

        /* NORMAL */
        OptimizationResult r1
                = pack(panelW, panelH, bw, bh, gapX, gapY, false, null);
        best = better(best, r1);

        /* ROTATED */
        OptimizationResult r2
                = pack(panelW, panelH, bh, bw, gapX, gapY, true, null);
        best = better(best, r2);

        if (mixedRotation) {

            OptimizationResult r3
                    = mixedPack(panelW, panelH, bw, bh, gapX, gapY, true, null);
            best = better(best, r3);

            OptimizationResult r4
                    = mixedColumn(panelW, panelH, bw, bh, gapX, gapY, null);
            best = better(best, r4);
        }

        /* 🔥 FINAL ARRAY APPLICATION (GLOBAL + SAFE) */
        if (useArray && arrayDef != null) {
            for (Placement p : best.getPlacements()) {
                p.setArrayDefinition(arrayDef);
            }
        }

        return best;
    }

    /* ================= PACK ================= */
    private OptimizationResult pack(
            double panelW,
            double panelH,
            double bw,
            double bh,
            double gapX,
            double gapY,
            boolean rotated,
            ArrayDefinition arrayDef) {

        OptimizationResult result = new OptimizationResult();

        int cols = (int) Math.floor((panelW + gapX) / (bw + gapX));
        int rows = (int) Math.floor((panelH + gapY) / (bh + gapY));

        double gridW = cols * bw + (cols - 1) * gapX;
        double gridH = rows * bh + (rows - 1) * gapY;

        double startX = (panelW - gridW) / 2.0;
        double startY = (panelH - gridH) / 2.0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                double x = startX + c * (bw + gapX);
                double y = startY + r * (bh + gapY);

                Placement p = new Placement(x, y, bw, bh, rotated);
                p.setArrayDefinition(arrayDef); // ✅ FIX
                result.addPlacement(p);
            }
        }

        return result;
    }

    /* ================= MIXED PACK ================= */
    private OptimizationResult mixedPack(
            double panelW,
            double panelH,
            double bw,
            double bh,
            double gapX,
            double gapY,
            boolean allowRotation,
            ArrayDefinition arrayDefinition) {

        OptimizationResult bestLocal = new OptimizationResult();

        if (!allowRotation) {
            return bestLocal;
        }

        double rotatedW = bh;
        double rotatedH = bw;

        int maxCols = (int) Math.floor((panelW + gapX) / (bw + gapX));
        int maxRows = (int) Math.floor((panelH + gapY) / (bh + gapY));

        for (int trialCols : new int[]{maxCols, maxCols - 1}) {
            if (trialCols <= 0) {
                continue;
            }

            for (int trialRows : new int[]{maxRows, maxRows - 1}) {
                if (trialRows <= 0) {
                    continue;
                }

                double gridW = trialCols * bw + (trialCols - 1) * gapX;
                double gridH = trialRows * bh + (trialRows - 1) * gapY;

                /* ================= RIGHT STRIP ================= */
                double remainingW = panelW - gridW;

                if (remainingW + gapX >= rotatedW) {

                    OptimizationResult result = new OptimizationResult();

                    int rotRows = (int) Math.floor((panelH + gapY) / (rotatedH + gapY));

                    double stripX = gridW + gapX;

                    /* ROTATED STRIP */
                    for (int r = 0; r < rotRows; r++) {
                        double rotGridH = rotRows * rotatedH + (rotRows - 1) * gapY;
                        double offsetY = (gridH - rotGridH) / 2.0;
                        double y = offsetY + r * (rotatedH + gapY);

                        if (y + rotatedH <= panelH) {
                            Placement p = new Placement(stripX, y, rotatedW, rotatedH, true);
                            p.setArrayDefinition(arrayDefinition);
                            result.addPlacement(p);
                        }
                    }

                    /* GRID */
                    for (int r = 0; r < trialRows; r++) {
                        for (int c = 0; c < trialCols; c++) {

                            double x = c * (bw + gapX);
                            double y = r * (bh + gapY);

                            Placement p = new Placement(x, y, bw, bh, false);
                            p.setArrayDefinition(arrayDefinition);
                            result.addPlacement(p);
                        }
                    }

                    bestLocal = better(bestLocal, result);
                }

                /* ================= LEFT STRIP ================= */
                if (remainingW + gapX >= rotatedW) {

                    OptimizationResult result = new OptimizationResult();

                    int rotRows = (int) Math.floor((panelH + gapY) / (rotatedH + gapY));

                    double gridStartX = rotatedW + gapX;

                    /* ROTATED STRIP */
                    for (int r = 0; r < rotRows; r++) {
                        double rotGridH = rotRows * rotatedH + (rotRows - 1) * gapY;
                        double offsetY = (gridH - rotGridH) / 2.0;
                        double y = offsetY + r * (rotatedH + gapY);

                        if (y + rotatedH <= panelH) {
                            Placement p = new Placement(0, y, rotatedW, rotatedH, true);
                            p.setArrayDefinition(arrayDefinition);
                            result.addPlacement(p);
                        }
                    }

                    /* GRID */
                    for (int r = 0; r < trialRows; r++) {
                        for (int c = 0; c < trialCols; c++) {

                            double x = gridStartX + c * (bw + gapX);
                            double y = r * (bh + gapY);

                            if (x + bw <= panelW) {
                                Placement p = new Placement(x, y, bw, bh, false);
                                p.setArrayDefinition(arrayDefinition);
                                result.addPlacement(p);
                            }
                        }
                    }

                    bestLocal = better(bestLocal, result);
                }

                /* ================= TOP STRIP ================= */
                double remainingH = panelH - gridH;

                if (remainingH + gapY >= rotatedH) {

                    OptimizationResult result = new OptimizationResult();

                    int rotCols = (int) Math.floor((panelW + gapX) / (rotatedW + gapX));

                    double stripY = gridH + gapY;

                    /* GRID */
                    for (int r = 0; r < trialRows; r++) {
                        for (int c = 0; c < trialCols; c++) {

                            double x = c * (bw + gapX);
                            double y = r * (bh + gapY);

                            Placement p = new Placement(x, y, bw, bh, false);
                            p.setArrayDefinition(arrayDefinition);
                            result.addPlacement(p);
                        }
                    }

                    /* ROTATED STRIP */
                    for (int c = 0; c < rotCols; c++) {
                        double rotGridW = rotCols * rotatedW + (rotCols - 1) * gapX;
                        double offsetX = (gridW - rotGridW) / 2.0;
                        double x = offsetX + c * (rotatedW + gapX);

                        if (x + rotatedW <= panelW) {
                            Placement p = new Placement(x, stripY, rotatedW, rotatedH, true);
                            p.setArrayDefinition(arrayDefinition);
                            result.addPlacement(p);
                        }
                    }

                    bestLocal = better(bestLocal, result);
                }

                /* ================= BOTTOM STRIP ================= */
                if (remainingH + gapY >= rotatedH) {

                    OptimizationResult result = new OptimizationResult();

                    int rotCols = (int) Math.floor((panelW + gapX) / (rotatedW + gapX));

                    double gridStartY = rotatedH + gapY;

                    /* ROTATED STRIP */
                    for (int c = 0; c < rotCols; c++) {
                        double rotGridW = rotCols * rotatedW + (rotCols - 1) * gapX;
                        double offsetX = (gridW - rotGridW) / 2.0;
                        double x = offsetX + c * (rotatedW + gapX);

                        if (x + rotatedW <= panelW) {
                            Placement p = new Placement(x, 0, rotatedW, rotatedH, true);
                            p.setArrayDefinition(arrayDefinition);
                            result.addPlacement(p);
                        }
                    }

                    /* GRID */
                    for (int r = 0; r < trialRows; r++) {
                        for (int c = 0; c < trialCols; c++) {

                            double x = c * (bw + gapX);
                            double y = gridStartY + r * (bh + gapY);

                            if (y + bh <= panelH) {
                                Placement p = new Placement(x, y, bw, bh, false);
                                p.setArrayDefinition(arrayDefinition);
                                result.addPlacement(p);
                            }
                        }
                    }

                    bestLocal = better(bestLocal, result);
                }
            }
        }

        return bestLocal;
    }

    /* ================= TOP STRIP ================= */
    private OptimizationResult mixedTopStrip(
            double panelW,
            double panelH,
            double bw,
            double bh,
            double gapX,
            double gapY,
            ArrayDefinition arrayDefinition) {

        OptimizationResult result = new OptimizationResult();

        double rotatedW = bh;
        double rotatedH = bw;

        int rotCols = (int) Math.floor((panelW + gapX) / (rotatedW + gapX));

        double remainingH = panelH - (rotatedH + gapY);

        if (remainingH <= 0) {
            return result;
        }

        int cols = (int) Math.floor((panelW + gapX) / (bw + gapX));
        int rows = (int) Math.floor((remainingH + gapY) / (bh + gapY));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                double x = c * (bw + gapX);
                double y = r * (bh + gapY);

                Placement p = new Placement(x, y, bw, bh, false);
                p.setArrayDefinition(arrayDefinition); // ✅ FIX
                result.addPlacement(p);
            }
        }

        double stripY = rows * (bh + gapY);

        double gridW = cols * bw + (cols - 1) * gapX;
        double stripW = rotCols * rotatedW + (rotCols - 1) * gapX;

        double offsetX = (gridW - stripW) / 2.0;

        for (int c = 0; c < rotCols; c++) {

            double x = offsetX + c * (rotatedW + gapX);

            Placement p = new Placement(x, stripY, rotatedW, rotatedH, true);
            p.setArrayDefinition(arrayDefinition); // ✅ FIX
            result.addPlacement(p);
        }

        return result;
    }

    /* ================= COLUMN ================= */
    private OptimizationResult mixedColumn(
            double panelW,
            double panelH,
            double bw,
            double bh,
            double gapX,
            double gapY,
            ArrayDefinition arrayDefinition) {

        OptimizationResult result = new OptimizationResult();

        int rowsN = (int) Math.floor((panelH + gapY) / (bh + gapY));

        for (int r = 0; r < rowsN; r++) {

            double x = 0;
            double y = r * (bh + gapY);

            Placement p = new Placement(x, y, bw, bh, false);
            p.setArrayDefinition(arrayDefinition); // ✅ FIX
            result.addPlacement(p);
        }

        double remainW = panelW - (bw + gapX);

        if (remainW >= bh) {

            int rowsR = (int) Math.floor((panelH + gapY) / (bw + gapY));

            for (int r = 0; r < rowsR; r++) {

                double x = bw + gapX;
                double y = r * (bw + gapY);

                Placement p = new Placement(x, y, bh, bw, true);
                p.setArrayDefinition(arrayDefinition); // ✅ FIX
                result.addPlacement(p);
            }
        }

        return result;
    }

    /* ================= BETTER ================= */
    private OptimizationResult better(OptimizationResult a, OptimizationResult b) {
        return b.getTotalBoards() > a.getTotalBoards() ? b : a;
    }
    
    public Map<String, Object> analyzeResult(OptimizationResult result, double panelW, double panelH){
        return LayoutAnalyzer.analyze(result, panelW, panelH);
    }
    
    

}
