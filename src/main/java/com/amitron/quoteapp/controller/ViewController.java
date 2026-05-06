/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.controller;

import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.service.I8JobSubmitService;
import com.amitron.quoteapp.utils.XmlContextUtils;
import java.util.Map;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class ViewController {

    private QuoteOptimizerController optimizerController;
    private I8DataController i8DataController;
    private SubmitRow jobData;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private AnchorPane optimizerContainer;
    @FXML
    private Tab panelOptimizeTab;
    @FXML
    private Tab i8DataTab;
    @FXML
    private Tab pdfViewerTab;

    private boolean optimizerLoaded = false;

    @FXML
    public void initialize() { 
        panelOptimizeTab.setOnSelectionChanged(event -> {
            if (panelOptimizeTab.isSelected() && !optimizerLoaded) {
                loadOptimizerUI();
                optimizerLoaded = true;
            }
        });

        i8DataTab.setOnSelectionChanged(event -> {
            if (i8DataTab.isSelected()) {
                loadI8DataUI();
            }
        });
        
        pdfViewerTab.setOnSelectionChanged(event -> {
            if (pdfViewerTab.isSelected() ) {
                loadI8Pdf();
            }
        });
    }

    public void setJobData(SubmitRow jobData) {
        this.jobData = jobData;
        //You can initialize UI here or call another method
        loadOptimizerUI();
        optimizerLoaded = true;
    }

    private void loadData() {
        if (jobData != null) {
            System.out.println("Received: " + jobData.i8IdProperty());

            // Later:
            // label.setText(jobData.getCustName());
        }
    }

    private void loadOptimizerUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/amitron/quoteapp/QuoteOptimizer.fxml")
            );

            Parent ui = loader.load();

            // Set UI
            optimizerContainer.getChildren().clear();
            optimizerContainer.getChildren().add(ui);

            AnchorPane.setTopAnchor(ui, 0.0);
            AnchorPane.setBottomAnchor(ui, 0.0);
            AnchorPane.setLeftAnchor(ui, 0.0);
            AnchorPane.setRightAnchor(ui, 0.0);

            optimizerController = loader.getController();

            if (jobData != null) {
                optimizerController.setJobData(jobData);

                int i8 = jobData.i8IdProperty().get();

                JSONObject qed = I8JobSubmitService.getQedJsonData(i8);

                if (qed != null && !qed.isEmpty()) {
                    optimizerController.setQedJson(qed);
                }
                
                //DELAY JSON LOAD (ensures NOTHING overrides it)
                Platform.runLater(() -> {
                    Map<String, String> dbData = I8JobSubmitService.getQuoteData(jobData.getRowId());
                    
                    if (dbData.get("optimizer") != null && optimizerController != null) {//load optimizer
                        optimizerController.loadOptimizerJson(dbData.get("optimizer"));
                        optimizerController.runOptimizer();// re-run optimizer to render UI
                    }
                });
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadI8DataUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/amitron/quoteapp/I8Data.fxml")
            );

            Parent ui = loader.load();

            i8DataTab.setContent(ui);

            i8DataController = loader.getController();
            if (jobData != null) {
                i8DataController.setJobSubmittedData(jobData);

                //DELAY JSON LOAD (ensures NOTHING overrides it)
                Platform.runLater(() -> {
                    Map<String, String> dbData = I8JobSubmitService.getQuoteData(jobData.getRowId());

                    if (dbData.get("saved") != null) {// load I8Data
                        i8DataController.loadSavedJson(dbData.get("saved"));
                    }
                });

            }
            if (optimizerController != null) {
                i8DataController.setOptimizerReportData(optimizerController.getOptimizerReportData());
            }

            System.out.println("I8 Data UI Loaded");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadI8Pdf() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/amitron/quoteapp/PdfViewer.fxml")
            );

            Parent ui = loader.load();

            pdfViewerTab.setContent(ui);

            PdfViewerController controller = loader.getController();
            
            if (jobData != null) {
                int i8Id = jobData.i8IdProperty().get();
                // passing pdf
                controller.loadPdf("I:/Work/" + i8Id + "/work/reports/I8QuotationReport_" + i8Id + ".pdf");
            }
            System.out.println("I8 pdf Loaded");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
