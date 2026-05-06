/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils.optimizer;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.util.Map;

/**
 *
 * @author Ngn
 */
public class PdfUtil {

    public static void createPdf(String pdfPath, String imagePath, Map<String, Object> data) throws Exception {

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Panel Optimization Report - I8 No: " + data.get("I8Id"))
                .setBold()
                .setFontSize(16));

        // Image
        ImageData imageData = ImageDataFactory.create(imagePath);
        Image img = new Image(imageData);

        img.scaleToFit(500, 400);
        img.setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(img);

        document.add(new Paragraph("\n"));
        //details
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createCell("Size:\n" + data.get("SizeText")));

        table.addCell(createCell("Panel Yield:\n" + data.get("YieldText")));
        table.addCell(createCell("Matrix:\n" + data.get("MatrixText")));

        table.addCell(createCell("Spacing:\n" + data.get("SpacingText")));
        table.addCell(createCell("Panel Border:\n" + data.get("PanelBorderText")));

        table.addCell(createCell("Array Border:\n" + data.get("ArrayBorderText")));
        table.addCell(createCell("Layout: " + data.get("UnitText")));
        table.addCell(createCell("Utilization: " + data.get("UtilizationText")));

        document.add(table);
        
        document.close();
    }

    private static Cell createCell(String text) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text));
        cell.setPadding(3);
        cell.setBorder(Border.NO_BORDER);
        return cell;
    }
}
