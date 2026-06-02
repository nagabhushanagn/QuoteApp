/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service;

import com.amitron.quoteapp.constants.Constants;
import com.amitron.quoteapp.constants.I8Progress;
import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.repository.I8JobSubmitRepository;
import com.amitron.quoteapp.utils.I8JobSubmitUtils;
import com.amitron.quoteapp.utils.UIUtils;
import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.sql.SQLException;
import java.util.Map;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class I8JobSubmitService implements Constants {

    public static String submitJob(TextField selectedFilePath) throws Exception {
        File selectedFile = new File(selectedFilePath.getText());
        return I8JobSubmitUtils.submitJob(selectedFile);
    }

    public static void insertSubmitData(int i8Id, TextField freshdeskId, TextField custName,
            TextField custCode, TextField selectedFilePath, TextField partNo, CheckBox itar) throws SQLException {

        String customerName = custName.getText();
        String customerCode = custCode.getText();
        Integer fdId = Integer.parseInt(freshdeskId.getText());
        File selectedFile = new File(selectedFilePath.getText());
        String originalData = selectedFile.getName();
        String partNm = partNo.getText();
        boolean is_itar = itar.isSelected();
        I8Progress progress = I8Progress.NEW;

        I8JobSubmitRepository.insertSubmitData(i8Id, fdId, customerName, customerCode,
                originalData, progress, partNm, is_itar);

    }

    public static void createToQuoteStructure(TextField selectedFile, TextField fdId, CheckBox itar) throws Exception {
        I8JobSubmitUtils.createToQuoteStructure(new File(selectedFile.getText()),
                Integer.parseInt(fdId.getText()),
                itar.isSelected());
    }

    public static void login() throws Exception {
        I8JobSubmitUtils.login();
    }

    public static void copyReportService(TableView<SubmitRow> table) throws Exception {
        SubmitRow quoteData = table.getSelectionModel().getSelectedItem();
        if (quoteData == null) {
            UIUtils.showError("No Job Selected in the table!!!");
            return;
        }
        String status = quoteData.progressProperty().get();
        if (status.matches(I8Progress.COMPLETED.getDisplayName())) {
            int result = I8JobSubmitUtils.copyQedReport(quoteData);
            if (result == 0) {
                UIUtils.showError("Report could not able to copy!!!");
            } else if (result == 1) {
                UIUtils.showWarning("QED pdf Report copied!!!");
            } else if (result == 2) {
                UIUtils.showWarning("XML file copied!!!");
            } else if (result == 3) {
                UIUtils.showSuccess("QED pdf and XML copied");
            }
        } else {
            UIUtils.showError("Can not Copy since progress not Completed!!!");
        }
    }

    public static void deleteDataService(TableView<SubmitRow> table) throws Exception {
        SubmitRow quoteData = table.getSelectionModel().getSelectedItem();
        if (quoteData == null) {
            UIUtils.showError("Please select a row to delete!!!");
            return;
        }

        int i8Id = quoteData.i8IdProperty().get();
        ButtonType confirm = UIUtils.showConfirmation("Delete record with i8_id = " + i8Id + "?");

        if (confirm == ButtonType.OK) {
            boolean deleted = I8JobSubmitRepository.deleteRow(i8Id);
            if (deleted) {
                table.getItems().remove(quoteData);
                UIUtils.showSuccess("Record deleted successfully");
            } else {
                UIUtils.showError("Delete failed or record not found");
            }
        }

    }//JSONObject

    public static JSONObject getQedJsonData(int i8) throws SQLException {
        return I8JobSubmitRepository.getQedJsonFromDb(i8);

    }

    //to save quote data in to quote_data table
    public static void saveOrUpdateQuoteData(int submitId, String optimizerData, String savedData) {
        I8JobSubmitRepository.saveOrUpdateQuoteData(submitId, optimizerData, savedData);
    }

    //get saved data back to UI
    public static Map<String, String> getQuoteData(int submitId) {
        return I8JobSubmitRepository.getQuoteData(submitId);
    }

    public static void saveOrUpdateOptimizerData(int rowId, String optimizerData) {
        I8JobSubmitRepository.saveOrUpdateOptimizerData(rowId, optimizerData);
    }
    
    public static Map<String, String> getCalculationSourceData(int submitId) {
        return I8JobSubmitRepository.getCalculationSourceData(submitId);
    }
    
}
