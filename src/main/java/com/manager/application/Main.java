package com.manager.application;

import com.manager.model.ModelManager;
import com.manager.ui.UiManager;
import com.manager.util.exception.NetworkException;
import com.manager.util.network.Http;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        initUiManager(primaryStage);
        initModelManager();
    }

    private void initUiManager(Stage primaryStage) throws IOException {
        UiManager.getInstance().initStage(primaryStage);
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
