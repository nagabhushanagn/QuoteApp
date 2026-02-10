/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
/**
 *
 * @author Ngn
 */
public class FileChooserService {
    
    public File chooseArchiveFile(Window ownerWindow) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select TGZ or ZIP file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archive Files (*.zip, *.tgz)", "*.zip", "*.tgz"),
                new FileChooser.ExtensionFilter("ZIP Files (*.zip)", "*.zip"),
                new FileChooser.ExtensionFilter("TGZ Files (*.tgz)", "*.tgz")
        );

        return fileChooser.showOpenDialog(ownerWindow);
    }
    
    public boolean isValidArchive(File file) {
        if (file == null) return false;

        String name = file.getName().toLowerCase();
        return name.endsWith(".zip") || name.endsWith(".tgz");
    }
}
