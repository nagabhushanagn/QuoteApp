package com.amitron.quoteapp;

import com.amitron.quoteapp.controller.PrimaryController;
import com.amitron.quoteapp.scheduler.AutoRefreshScheduler;
import com.amitron.quoteapp.service.I8JobSubmitService;
import com.amitron.quoteapp.utils.I8JobSubmitUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import javafx.application.Platform;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private PrimaryController primaryController;
    private AutoRefreshScheduler autoRefreshScheduler;

    @Override
    public void start(Stage stage) throws IOException {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        autoRefreshScheduler = new AutoRefreshScheduler();
        FXMLLoader fxmlLoader
                = new FXMLLoader(App.class.getResource("/com/amitron/quoteapp/primary.fxml"));

        Parent loader = fxmlLoader.load();
        primaryController = fxmlLoader.getController();

        scene = new Scene(loader);

        //ADD CSS HERE
        scene.getStylesheets().add(
                getClass().getResource("/com/amitron/quoteapp/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Quote Application V1.11");
        stage.show();

        //Auto refresh
        autoRefreshScheduler.start(() -> {

            try {
                I8JobSubmitService.login();
                boolean updated = I8JobSubmitUtils.updateRunningJobStatuses();

                // UI update must run on FX thread
                javafx.application.Platform.runLater(() -> primaryController.refreshTable());

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        stage.setOnCloseRequest(e -> {
            Platform.exit();     // closes JavaFX
            System.exit(0);      // stops JVM (important)
        });
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/amitron/quoteapp/" + fxml + ".fxml")
        );
        return loader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {

        if (autoRefreshScheduler != null) {
            autoRefreshScheduler.stop();
        }
    }

}
