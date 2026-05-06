/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model.optimizer;

/**
 *
 * @author Ngn
 */
public class Panel {

    private double totalWidth;
    private double totalHeight;

    private double leftMargin;
    private double rightMargin;
    private double topMargin;
    private double bottomMargin;

    public Panel(double totalWidth, double totalHeight,
            double leftMargin, double rightMargin,
            double topMargin, double bottomMargin) {

        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    public double getTotalWidth() {
        return totalWidth;
    }

    public double getTotalHeight() {
        return totalHeight;
    }

    public double getUsableWidth() {
        return totalWidth - leftMargin - rightMargin;
    }

    public double getUsableHeight() {
        return totalHeight - topMargin - bottomMargin;
    }

    public double getLeftMargin() {
        return leftMargin;
    }

    public double getRightMargin() {
        return rightMargin;
    }

    public double getTopMargin() {
        return topMargin;
    }

    public double getBottomMargin() {
        return bottomMargin;
    }
}
