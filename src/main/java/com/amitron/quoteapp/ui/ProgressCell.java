/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.ui;

import com.amitron.quoteapp.model.SubmitRow;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

/**
 *
 * @author Ngn
 */

public class ProgressCell extends TableCell<SubmitRow, String> {

    @Override
    protected void updateItem(String progress, boolean empty) {
        super.updateItem(progress, empty);

        if (empty || progress == null) {
            setText(null);
            setStyle("");
            return;
        }

        setText(progress);

        // reset style
        setTextFill(Color.BLACK);
        setStyle("-fx-background-color: white;");

        switch (progress) {

            case "Completed":
                setStyle("-fx-background-color: #90EE90;"); // light green
                break;

            case "Running":
                setStyle("-fx-background-color: #FFFF99;"); // light yellow
                break;

            case "Stopped":
                setStyle("-fx-background-color: #FFA0A0;"); // light red
                break;
            case "Killed":
                setStyle("-fx-background-color: #FFA0A0;"); // light red
                break;
            case "Suspended":
                setStyle("-fx-background-color: #FFA0A0;"); // light red
                break;
            case "Error":
                setStyle("-fx-background-color: #FFA0A0;"); // light red
                break;

            default:
                // New / Available → no color
                break;
        }
    }
}
