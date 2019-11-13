package com.manager.application;

import com.manager.model.ModelManager;
import com.manager.util.exception.NetworkException;
import com.manager.util.network.Http;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        configureStage(primaryStage, loadMainScene());
        primaryStage.show();
        initModelManager();
    }

    private Scene loadMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/mainScene.fxml"));
        VBox vb = fxmlLoader.load();
        return new Scene(vb);
    }

    private void configureStage(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.setTitle("Email Manager");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
    }

    private void initModelManager() {
        try {
            ModelManager.getInstance();
            Http.startAuthProcess();
        } catch (NetworkException e) {
            e.printStackTrace();
        }
    }
}
