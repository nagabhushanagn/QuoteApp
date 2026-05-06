/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model.optimizer;

/**
 *
 * @author Ngn
 */
public class Placement {

    private double x;
    private double y;
    private double width;
    private double height;
    private boolean rotated;
    private int rows;
    private int cols;
    private ArrayDefinition arrayDefinition; // nullable

    public Placement(double x, double y,
            double width, double height,
            boolean rotated) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotated = rotated;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isRotated() {
        return rotated;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public ArrayDefinition getArrayDefinition() {
        return arrayDefinition;
    }

    public void setArrayDefinition(ArrayDefinition arrayDefinition) {
        this.arrayDefinition = arrayDefinition;
    }

}
