/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model;

/**
 *
 * @author Ngn
 */
public class StackupInput {

    private int layerCount;
    private String material;
    private String constructionType;

    private boolean silkTop;
    private boolean silkBottom;

    private boolean maskTop;
    private boolean maskBottom;

    // Getters & Setters

    public int getLayerCount() {
        return layerCount;
    }

    public void setLayerCount(int layerCount) {
        this.layerCount = layerCount;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getConstructionType() {
        return constructionType;
    }

    public void setConstructionType(String constructionType) {
        this.constructionType = constructionType;
    }

    public boolean isSilkTop() {
        return silkTop;
    }

    public void setSilkTop(boolean silkTop) {
        this.silkTop = silkTop;
    }

    public boolean isSilkBottom() {
        return silkBottom;
    }

    public void setSilkBottom(boolean silkBottom) {
        this.silkBottom = silkBottom;
    }

    public boolean isMaskTop() {
        return maskTop;
    }

    public void setMaskTop(boolean maskTop) {
        this.maskTop = maskTop;
    }

    public boolean isMaskBottom() {
        return maskBottom;
    }

    public void setMaskBottom(boolean maskBottom) {
        this.maskBottom = maskBottom;
    }
}
