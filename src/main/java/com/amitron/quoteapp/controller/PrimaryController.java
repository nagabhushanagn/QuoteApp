package com.amitron.quoteapp.controller;

import com.amitron.quoteapp.App;
import com.amitron.quoteapp.model.FileSelectionModel;
import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.repository.I8JobSubmitRepository;
import com.amitron.quoteapp.scheduler.AutoRefreshScheduler;
import com.amitron.quoteapp.service.FileChooserService;
import com.amitron.quoteapp.service.I8JobSubmitService;
import com.amitron.quoteapp.ui.ProgressCell;
import com.amitron.quoteapp.utils.UIUtils;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import com.amitron.quoteapp.utils.BackgroundTaskRunner;
import com.amitron.quoteapp.utils.I8JobSubmitUtils;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrimaryController {

    @FXML
    private VBox formPane;
    @FXML
    private TextField selectedFile;
    @FXML
    private TextField fdId;
    @FXML
    private TextField custName;
    @FXML
    private TextField custCode;
    @FXML
    private TextField partNo;
    @FXML
    private CheckBox isItar;
    @FXML
    private Button submit;

    @FXML
    private TableView<SubmitRow> table;
    @FXML
    private TableColumn<SubmitRow, Integer> colI8;
    @FXML
    private TableColumn<SubmitRow, Integer> colFD;
    @FXML
    private TableColumn<SubmitRow, String> colCustName;
    @FXML
    private TableColumn<SubmitRow, String> colCustCode;
    @FXML
    private TableColumn<SubmitRow, String> colOrig;
    @FXML
    private TableColumn<SubmitRow, String> colPart;
    @FXML
    private TableColumn<SubmitRow, Boolean> colItar;
    @FXML
    private TableColumn<SubmitRow, String> colProg;
    @FXML
    private TableColumn<SubmitRow, String> colTime;

    private final FileChooserService fileChooserService = new FileChooserService();
    private final FileSelectionModel model = new FileSelectionModel();

    @FXML
    public void initialize() {
        table.getColumns().forEach(col -> col.setReorderable(false));// desable table column reorder
        UIUtils.OnlyInteger(fdId);//make fresh desk Id allow only integer
        //table column initialization
        colI8.setCellValueFactory(new PropertyValueFactory<>("i8Id"));
        colFD.setCellValueFactory(new PropertyValueFactory<>("freshdeskId"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCustCode.setCellValueFactory(new PropertyValueFactory<>("customerCode"));
        colOrig.setCellValueFactory(new PropertyValueFactory<>("originalData"));
        colPart.setCellValueFactory(new PropertyValueFactory<>("partNumber"));
        colItar.setCellValueFactory(new PropertyValueFactory<>("itar"));
        colProg.setCellValueFactory(new PropertyValueFactory<>("progress"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("submitTime"));

        refreshTable();//refresh table

        colProg.setCellFactory(column -> new ProgressCell());//Progress cell coloring

        //Check box for itar column
        colItar.setCellValueFactory(cellData -> cellData.getValue().itarProperty());
        colItar.setCellFactory(CheckBoxTableCell.forTableColumn(colItar));
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void onAddClicked() {
        boolean show = !formPane.isVisible();
        formPane.setVisible(show);
        formPane.setManaged(show);
    }

    @FXML
    private void onSubmitClicked() {
        boolean is_allFieldsOk = UIUtils.validateSubmitFields(selectedFile, custName, custCode, fdId, partNo);

        if (!is_allFieldsOk) {
            return;
        }

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                I8JobSubmitService.login();//login to Integr8tor
                String i8IdString = I8JobSubmitService.submitJob(selectedFile);//submit to Integr8tor
                if (i8IdString == null) {
                    return null;
                }
                int i8Id = Integer.parseInt(i8IdString);

                I8JobSubmitService.insertSubmitData(
                        i8Id,
                        fdId,
                        custName,
                        custCode,
                        selectedFile,
                        partNo,
                        isItar
                );

                I8JobSubmitService.createToQuoteStructure(selectedFile, fdId, isItar);
                return i8Id;
            }
        };

        BackgroundTaskRunner.run(
                task,
                () -> submit.setDisable(true),
                i8Id -> {
                    submit.setDisable(false);
                    selectedFile.clear();
                    custName.clear();
                    custCode.clear();
                    fdId.clear();
                    partNo.clear();
                    isItar.setSelected(false);
                    if (i8Id != null) {
                        refreshTable();
                        UIUtils.showSuccess("Submission successful! I8_ID : " + i8Id);
                    } else {
                        UIUtils.showError("Job submit NOT successful");
                    }
                },
                ex -> {
                    submit.setDisable(false);
                    UIUtils.showError("Submission failed:\n" + ex.getMessage());
                }
        );

        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    // Browse button (already working)
    @FXML
    public void onBrowseClicked() {
        Stage stage = (Stage) selectedFile.getScene().getWindow();
        File file = fileChooserService.chooseArchiveFile(stage);

        handleFile(file);
    }

    // Drag over
    @FXML
    private void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();

        if (db.hasFiles() && fileChooserService.isValidArchive(db.getFiles().get(0))) {
            event.acceptTransferModes(TransferMode.COPY);
        }

        event.consume();
    }

    // Drop
    @FXML
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            handleFile(file);
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private void handleFile(File file) {
        if (file != null && fileChooserService.isValidArchive(file)) {
            model.setSelectedFile(file);
            selectedFile.setText(model.getFileName());
        } else {
            selectedFile.setText("Invalid file type (zip / tgz only)");
        }
    }

    public void refreshTable() {
        try {
            ObservableList<SubmitRow> data = I8JobSubmitRepository.fetchLatestSubmits();

            table.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCopyClicked() throws Exception {
        if (formPane.isVisible()) {
            formPane.setVisible(false);
            formPane.setManaged(false);
        }
        I8JobSubmitService.copyReportService(table);
    }

    @FXML
    private void onDeleteClicked() throws Exception {
        if (formPane.isVisible()) {
            formPane.setVisible(false);
            formPane.setManaged(false);
        }
        I8JobSubmitService.deleteDataService(table);
    }

    @FXML
    private void onViewClicked() {
        try {
            SubmitRow selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                UIUtils.showError("Job not Selected!!!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/amitron/quoteapp/ViewWindow.fxml")
            );

            Parent root = loader.load();

            //GET CONTROLLER
            ViewController controller = loader.getController();

            //PASS DATA
            controller.setJobData(selected);
            Stage stage = new Stage();
            stage.setTitle("View Details: I8- " +selected.i8IdProperty().get());
            stage.setWidth(1300);
            stage.setHeight(960);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
