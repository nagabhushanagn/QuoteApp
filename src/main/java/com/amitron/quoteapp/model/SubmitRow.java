/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.model;

import javafx.beans.property.*;

/**
 *
 * @author Ngn
 */
public class SubmitRow {

    private final IntegerProperty rowId = new SimpleIntegerProperty();
    private final IntegerProperty i8Id = new SimpleIntegerProperty();
    private final ObjectProperty<Integer> freshdeskId = new SimpleObjectProperty<>();
    private final StringProperty customerName = new SimpleStringProperty();
    private final StringProperty customerCode = new SimpleStringProperty();
    private final StringProperty originalData = new SimpleStringProperty();
    private final StringProperty partNumber = new SimpleStringProperty();
    private final BooleanProperty itar = new SimpleBooleanProperty();
    private final StringProperty progress = new SimpleStringProperty();
    private final StringProperty submitTime = new SimpleStringProperty();

    public SubmitRow(int rowId, int i8Id, Integer freshdeskId, String customerName,
                     String customerCode, String originalData,
                     String partNumber, boolean itar,
                     String progress, String submitTime) {

        this.rowId.set(rowId);
        this.i8Id.set(i8Id);
        this.freshdeskId.set(freshdeskId);
        this.customerName.set(customerName);
        this.customerCode.set(customerCode);
        this.originalData.set(originalData);
        this.partNumber.set(partNumber);
        this.itar.set(itar);
        this.progress.set(progress);
        this.submitTime.set(submitTime);
    }

    public IntegerProperty rowIdProperty() { return rowId; }
    public IntegerProperty i8IdProperty() { return i8Id; }
    public ObjectProperty<Integer> freshdeskIdProperty() { return freshdeskId; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty customerCodeProperty() { return customerCode; }
    public StringProperty originalDataProperty() { return originalData; }
    public StringProperty partNumberProperty() { return partNumber; }
    public BooleanProperty itarProperty() { return itar; }
    public StringProperty progressProperty() { return progress; }
    public StringProperty submitTimeProperty() { return submitTime; }
    
    public int getRowId() { return rowId.get(); }
}
