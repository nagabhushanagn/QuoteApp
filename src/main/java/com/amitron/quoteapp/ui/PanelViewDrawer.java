/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.amitron.quoteapp.model.optimizer.*;

/**
 *
 * @author Ngn
 */
public class PanelViewDrawer {

    public static void drawPanel(Canvas canvas,
                             Panel panel,
                             OptimizationResult result) {

    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    if (result == null || result.getPlacements().isEmpty()) {
        return;
    }

    double canvasW = canvas.getWidth();
    double canvasH = canvas.getHeight();

    double scale = Math.min(
            canvasW / panel.getTotalWidth(),
            canvasH / panel.getTotalHeight());

    /* ===== DRAW PANEL ===== */

    double panelW = panel.getTotalWidth() * scale;
    double panelH = panel.getTotalHeight() * scale;

    double panelX = 0;
    double panelY = canvasH - panelH;

    gc.setFill(Color.LIGHTGRAY);
    gc.fillRect(panelX, panelY, panelW, panelH);

    /* ===== DRAW USABLE AREA ===== */

    double usableX = panel.getLeftMargin() * scale;
    double usableY = panel.getBottomMargin() * scale;

    double usableW = panel.getUsableWidth() * scale;
    double usableH = panel.getUsableHeight() * scale;

    double usableCanvasY = canvasH - (panel.getBottomMargin() + panel.getUsableHeight()) * scale;

    gc.setFill(Color.WHITE);
    gc.fillRect(usableX, usableCanvasY, usableW, usableH);

    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);
    gc.strokeRect(panelX, panelY, panelW, panelH);

    gc.setStroke(Color.RED);
    gc.setLineWidth(1.5);
    gc.strokeRect(usableX, usableCanvasY, usableW, usableH);

    /* ===== FIND LAYOUT BOUNDS ===== */

    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double maxY = Double.MIN_VALUE;

    for (Placement p : result.getPlacements()) {

        double w = p.getWidth();
        double h = p.getHeight();

        minX = Math.min(minX, p.getX());
        minY = Math.min(minY, p.getY());
        maxX = Math.max(maxX, p.getX() + w);
        maxY = Math.max(maxY, p.getY() + h);
    }

    /* ===== 🔥 FIXED CENTERING LOGIC ===== */

    double layoutCenterX = (minX + maxX) / 2.0;
    double layoutCenterY = (minY + maxY) / 2.0;

    double panelCenterX = panel.getUsableWidth() / 2.0;
    double panelCenterY = panel.getUsableHeight() / 2.0;

    double offsetX = panelCenterX - layoutCenterX;
    double offsetY = panelCenterY - layoutCenterY;

    /* ===== DRAW BOARDS ===== */

    for (Placement p : result.getPlacements()) {

        double modelX = panel.getLeftMargin() + p.getX() + offsetX;
        double modelY = panel.getBottomMargin() + p.getY() + offsetY;

        double x = modelX * scale;
        double y = canvasH - (modelY + p.getHeight()) * scale;

        double w = p.getWidth() * scale;
        double h = p.getHeight() * scale;

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y, w, h);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, w, h);

        if (p.getArrayDefinition() != null) {
            drawArray(gc, p, p.getArrayDefinition(), scale, canvasH, x, y);
        }
    }
}

    /* ===================================================== */
 /* ARRAY DRAWING                                         */
 /* ===================================================== */
    private static void drawArray(GraphicsContext gc,
        Placement p,
        ArrayDefinition arr,
        double scale,
        double canvasH,
        double startX,
        double startY) {
        
    int cols = arr.getCountX();
    int rows = arr.getCountY();

    double pcbW = arr.getPcbWidth();
    double pcbH = arr.getPcbHeight();

    double gapX = arr.getGapX();
    double gapY = arr.getGapY();

    /* ==============================
       HANDLE ROTATION
       ============================== */
    if (p.isRotated()) {

        double t = pcbW;
        pcbW = pcbH;
        pcbH = t;

        t = gapX;
        gapX = gapY;
        gapY = t;

        int tmp = rows;
        rows = cols;
        cols = tmp;
    }

    /* ==============================
       TOTAL ARRAY SIZE (REAL SIZE)
       ============================== */
    double totalW = cols * pcbW + (cols - 1) * gapX;
    double totalH = rows * pcbH + (rows - 1) * gapY;

    /* ==============================
       🔥 AUTO-FIT (PREVENT DISAPPEARING)
       ============================== */
    double fitScaleX = p.getWidth() / totalW;
    double fitScaleY = p.getHeight() / totalH;

    double fitScale = Math.min(1.0, Math.min(fitScaleX, fitScaleY));

    if (fitScale < 1.0) {
        pcbW *= fitScale;
        pcbH *= fitScale;
        gapX *= fitScale;
        gapY *= fitScale;

        totalW = cols * pcbW + (cols - 1) * gapX;
        totalH = rows * pcbH + (rows - 1) * gapY;
    }

    /* ==============================
       CENTER ARRAY INSIDE BOARD
       ============================== */
    double baseX = startX + ((p.getWidth() - totalW) * scale) / 2.0;
    double baseY = startY + ((p.getHeight() - totalH) * scale) / 2.0;

    double stepX = (pcbW + gapX) * scale;
    double stepY = (pcbH + gapY) * scale;

    /* ==============================
       DRAW ARRAY
       ============================== */
    
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {

            double x = baseX + c * stepX;
            double y = baseY + r * stepY;

            gc.strokeRect(
        x,
        y,
        pcbW * scale,
        pcbH * scale
);
        }
    }
}
}
