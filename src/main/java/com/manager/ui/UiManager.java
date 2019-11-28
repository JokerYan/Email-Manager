package com.manager.ui;

import com.manager.application.Main;
import com.manager.model.ModelManager;
import com.manager.util.exception.NetworkException;
import com.manager.util.network.Http;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static com.manager.util.ui.UiFxml.ICON_FXML;
import static com.manager.util.ui.UiFxml.MAIN_WINDOW_FXML;

public class UiManager {
    private static UiManager uiManager;
    private MainScene mainScene;

    private UiManager() {
    }

    public static UiManager getInstance() {
        if (uiManager == null) {
            uiManager = new UiManager();
        }
        return uiManager;
    }

    private Scene loadMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        VBox vb = fxmlLoader.load();
        mainScene = fxmlLoader.getController();
        mainScene.initMainScene();
        return new Scene(vb);
    }

    public void initStage(Stage stage) throws IOException {
        Scene scene = loadMainScene();
        stage.setScene(scene);
        stage.setTitle("Email Manager");
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON_FXML)));
        stage.setMaximized(true);
        stage.show();
    }
}
