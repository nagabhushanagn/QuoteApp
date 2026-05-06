/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.controller;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author Ngn
 */
public class PdfViewerController {

    @FXML
    private WebView webView;

    private WebEngine engine;

    @FXML
    public void initialize() {
        engine = webView.getEngine();
    }

    /**
     * Load PDF from resources
     */
    public void loadPdf(String pdfPath) {
        try {
            engine.setJavaScriptEnabled(true);

            // Load viewer from external folder (NOT JAR)
            File viewerFile = new File("app/pdfjs/web/viewer.html");

            if (!viewerFile.exists()) {
                System.out.println("Viewer not found: " + viewerFile.getAbsolutePath());
                return;
            }

            String pdfUrl = new File(pdfPath).toURI().toString();
            System.out.println("PDF URL: " + pdfUrl);

            String fullUrl = viewerFile.toURI().toString() + "?file=" + pdfUrl;

            System.out.println("Loading Viewer: " + fullUrl);

            engine.load(fullUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*public void loadPdf(String pdfPath) {
        try {
            String viewerUrl = getClass()
                    .getResource("/com/amitron/quoteapp/pdfjs/web/viewer.html")
                    .toExternalForm();

            // Convert to proper file URL
            String pdfUrl = new java.io.File(pdfPath)
                    .toURI()
                    .toString();

            System.out.println("PDF URL: " + pdfUrl);

            engine.setJavaScriptEnabled(true);

            //NO ENCODING HERE
            engine.load(viewerUrl + "?file=" + pdfUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
