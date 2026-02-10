/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author Ngn
 */
public class UIUtils {

    public static void OnlyInteger(TextField field) {
        // Allow only positive integers
        TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("\\d*")) {   // only digits
                return change;
            }
            return null;
        });

        field.setTextFormatter(formatter);
    }

    public static boolean validateSubmitFields(TextField selectedFile, TextField custName, 
            TextField custCode, TextField fdId, TextField partNo) {
        
        if (selectedFile.getText().trim().isEmpty()) {
            showError("Please select a ZIP or TGZ file.");
            selectedFile.requestFocus();
            return false;
        }
        
        if (custCode.getText().trim().isEmpty()) {
            showError("Customer Code is required.");
            custCode.requestFocus();
            return false;
        }

        if (custName.getText().trim().isEmpty()) {
            showError("Customer Name is required.");
            custName.requestFocus();
            return false;
        }

        if (fdId.getText().trim().isEmpty()) {
            showError("FreshDesk ID is required.");
            fdId.requestFocus();
            return false;
        }
        
        if (partNo.getText().trim().isEmpty()) {
            showError("Part Number is required.");
            partNo.requestFocus();
            return false;
        }

        return true; // all good
    }
    
    public static void alertMessage(AlertType alertType, String title, String msg){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    public static void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("INFORMATION");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    public static void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    public static ButtonType showConfirmation(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        /*
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        ButtonType result = alert.showAndWait().orElse(noBtn);
        */
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result;
    }
}
