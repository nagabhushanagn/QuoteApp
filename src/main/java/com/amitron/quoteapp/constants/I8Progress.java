/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.amitron.quoteapp.constants;

/**
 *
 * @author Ngn
 */
public enum I8Progress {

    NEW("N", "New"),
    AVAILABLE("A", "Available"),
    COMPLETED("C", "Completed"),
    RUNNING("R", "Running"),
    STOPPED("S", "Stopped"),
    KILLED("K", "Killed"),
    ERROR("E", "Error"),
    SUSPENDED("P", "Suspended");

    private final String code;      // what Integr8tor sends
    private final String label;     // human readable

    I8Progress(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /** Value stored in DB */
    public String getDbValue() {
        return code;   // store single char in DB
    }

    /** Display value (JTable, UI) */
    public String getDisplayName() {
        return label;
    }

    /** Convert Integr8tor response → Enum */
    public static I8Progress fromCode(String code) {
        if (code == null) return null;

        for (I8Progress p : values()) {
            if (p.code.equalsIgnoreCase(code.trim())) {
                return p;
            }
        }
        return null; // or throw exception if you prefer
    }
}

