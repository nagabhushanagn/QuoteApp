/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model;

import javafx.beans.property.*;
/**
 *
 * @author Ngn
 */
public class QuantityRow {

    private final IntegerProperty quantity =
            new SimpleIntegerProperty();

    private final StringProperty delivery =
            new SimpleStringProperty();

    private final DoubleProperty launchingPanel =
            new SimpleDoubleProperty();

    private final DoubleProperty unitPriceA =
            new SimpleDoubleProperty();

    private final DoubleProperty nrcB =
            new SimpleDoubleProperty();

    private final DoubleProperty calculatedPrice =
            new SimpleDoubleProperty();

    private final DoubleProperty discount =
            new SimpleDoubleProperty();

    private final DoubleProperty finalPrice =
            new SimpleDoubleProperty();

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int value) {
        quantity.set(value);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public String getDelivery() {
        return delivery.get();
    }

    public void setDelivery(String value) {
        delivery.set(value);
    }

    public StringProperty deliveryProperty() {
        return delivery;
    }

    public double getLaunchingPanel() {
        return launchingPanel.get();
    }

    public void setLaunchingPanel(double value) {
        launchingPanel.set(value);
    }

    public DoubleProperty launchingPanelProperty() {
        return launchingPanel;
    }

    public double getUnitPriceA() {
        return unitPriceA.get();
    }

    public void setUnitPriceA(double value) {
        unitPriceA.set(value);
    }

    public DoubleProperty unitPriceAProperty() {
        return unitPriceA;
    }

    public double getNrcB() {
        return nrcB.get();
    }

    public void setNrcB(double value) {
        nrcB.set(value);
    }

    public DoubleProperty nrcBProperty() {
        return nrcB;
    }

    public double getCalculatedPrice() {
        return calculatedPrice.get();
    }

    public void setCalculatedPrice(double value) {
        calculatedPrice.set(value);
    }

    public DoubleProperty calculatedPriceProperty() {
        return calculatedPrice;
    }

    public double getDiscount() {
        return discount.get();
    }

    public void setDiscount(double value) {
        discount.set(value);
    }

    public DoubleProperty discountProperty() {
        return discount;
    }

    public double getFinalPrice() {
        return finalPrice.get();
    }

    public void setFinalPrice(double value) {
        finalPrice.set(value);
    }

    public DoubleProperty finalPriceProperty() {
        return finalPrice;
    }
}
