/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service.optimizer;

import com.amitron.quoteapp.model.SubmitRow;
import com.amitron.quoteapp.utils.I8JobSubmitUtils;
import com.amitron.quoteapp.utils.UIUtils;
import com.amitron.quoteapp.utils.optimizer.ImageUtil;
import com.amitron.quoteapp.utils.optimizer.PdfUtil;
import java.io.File;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Ngn
 */
public class ReportService {

    public void generateReport(Node panelNode, Map<String, Object> data, SubmitRow jobData) {

        try {
            File techDataFold = I8JobSubmitUtils.getTechnicalDataFolder(jobData);
            if(techDataFold.exists()){
                String imagePath = techDataFold.getAbsolutePath() + File.separator + "utilization.png";
                String pdfPath = techDataFold.getAbsolutePath() + File.separator + 
                        "panel_utilization_report_" + jobData.i8IdProperty().get() + ".pdf";

                // Step 1: Capture image
                WritableImage image = ImageUtil.capturePanel(panelNode);

                // Step 2: Save image
                ImageUtil.saveImage(image, imagePath);

                // Step 3: Create PDF
                PdfUtil.createPdf(pdfPath, imagePath, data);
                new File(imagePath).deleteOnExit();//Delete image file
            }else{
                UIUtils.showError("Job Technical folder Not found");
            }
                

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
