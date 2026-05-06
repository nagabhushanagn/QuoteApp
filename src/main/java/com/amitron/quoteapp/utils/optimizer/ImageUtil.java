/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils.optimizer;

import java.awt.image.BufferedImage;
import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/**
 *
 * @author Ngn
 */
public class ImageUtil {

    public static WritableImage capturePanel(Node panelNode) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.WHITE);
        return panelNode.snapshot(params, null);
    }

    public static void saveImage(WritableImage image, String path) throws Exception {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, "png", new File(path));
    }
}
