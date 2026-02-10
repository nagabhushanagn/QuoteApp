/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model;

import java.io.File;

/**
 *
 * @author Ngn
 */
public class FileSelectionModel {
    private File selectedFile;

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getFileName() {
        return selectedFile != null ? selectedFile.getAbsolutePath() : "";
    }
}
