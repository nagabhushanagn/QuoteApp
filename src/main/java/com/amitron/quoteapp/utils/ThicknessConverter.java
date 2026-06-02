/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils;

/**
 *
 * @author Ngn
 */
public class ThicknessConverter {

    public static double toInch(String input) {

        if (input == null || input.trim().isEmpty()) {
            return 0;
        }

        String value = input
                .trim()
                .toLowerCase();

        value = value.replaceAll("\\s+", "");

        try {

            // =========================
            // MIL
            // =========================
            if (value.endsWith("mil")) {

                double v = Double.parseDouble(
                        value.replace("mil", "")
                );

                return v * 0.001;
            }

            // =========================
            // MM
            // =========================
            if (value.endsWith("mm")) {

                double v = Double.parseDouble(
                        value.replace("mm", "")
                );

                return v * 0.0393701;
            }

            // =========================
            // OZ
            // =========================
            if (value.endsWith("oz")) {

                double v = Double.parseDouble(
                        value.replace("oz", "")
                );

                // 1 oz copper = 1.37 mil
                return v * 0.001378;
            }

            // =========================
            // INCH
            // =========================
            if (value.endsWith("inch")) {

                return Double.parseDouble(
                        value.replace("inch", "")
                );
            }

            if (value.endsWith("in")) {

                return Double.parseDouble(
                        value.replace("in", "")
                );
            }

            // =========================
            // DEFAULT = INCH
            // =========================

            return Double.parseDouble(value);

        } catch (Exception ex) {

            ex.printStackTrace();

            return 0;
        }
    }
}
