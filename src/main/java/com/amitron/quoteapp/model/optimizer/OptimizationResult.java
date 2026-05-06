/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model.optimizer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ngn
 */
public class OptimizationResult {

    private int totalBoards;
    private double usedWidth;
    private double usedHeight;
    private List<Placement> placements;
    private double utilizationPercent = 0.0;

    // Default constructor
    public OptimizationResult() {
        this.totalBoards = 0;
        this.usedWidth = 0;
        this.usedHeight = 0;
        this.placements = new ArrayList<>();
    }

    public OptimizationResult(int totalBoards,
                              double usedWidth,
                              double usedHeight,
                              List<Placement> placements) {

        this.totalBoards = totalBoards;
        this.usedWidth = usedWidth;
        this.usedHeight = usedHeight;
        this.placements = placements;
    }

    public int getTotalBoards() {
        return placements.size();
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public double getUsedWidth() {
        return usedWidth;
    }

    public double getUsedHeight() {
        return usedHeight;
    }

    public double getUtilizationPercent() {
        return utilizationPercent;
    }

    public void setUtilizationPercent(double v) {
        this.utilizationPercent = v;
    }
/*
    public void addPlacement(Placement p) {
        placements.add(p);
        totalBoards = placements.size();
    }
    */
    public void addPlacement(Placement p) {
        placements.add(p);
    }
}