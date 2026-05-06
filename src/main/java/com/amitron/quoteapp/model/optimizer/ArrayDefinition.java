/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model.optimizer;

/**
 *
 * @author Ngn
 */
public class ArrayDefinition {

    private double pcbWidth;
    private double pcbHeight;

    private int countX;
    private int countY;

    private double gapX;
    private double gapY;

    private double railLeft;
    private double railRight;
    private double railTop;
    private double railBottom;
    

    public ArrayDefinition(double pcbWidth,
                           double pcbHeight,
                           int countX,
                           int countY,
                           double gapX,
                           double gapY,
                           double railLeft,
                           double railRight,
                           double railTop,
                           double railBottom) {

        this.pcbWidth = pcbWidth;
        this.pcbHeight = pcbHeight;
        this.countX = countX;
        this.countY = countY;
        this.gapX = gapX;
        this.gapY = gapY;
        this.railLeft = railLeft;
        this.railRight = railRight;
        this.railTop = railTop;
        this.railBottom = railBottom;
    }

    /* ========================= */
    /* ARRAY SIZE CALCULATION    */
    /* ========================= */

    public double getArrayWidth() {
        return railLeft + railRight +
                (countX * pcbWidth) +
                ((countX - 1) * gapX);
    }

    public double getArrayHeight() {
        return railTop + railBottom +
                (countY * pcbHeight) +
                ((countY - 1) * gapY);
    }

    /* ========================= */
    /* GETTERS                   */
    /* ========================= */

    public double getPcbWidth() { return pcbWidth; }
    public double getPcbHeight() { return pcbHeight; }
    public int getCountX() { return countX; }
    public int getCountY() { return countY; }
    public double getGapX() { return gapX; }
    public double getGapY() { return gapY; }
    public double getRailLeft() { return railLeft; }
    public double getRailRight() { return railRight; }
    public double getRailTop() { return railTop; }
    public double getRailBottom() { return railBottom; }
    
}
