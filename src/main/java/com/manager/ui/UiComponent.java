package com.manager.ui;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

import static java.util.Objects.requireNonNull;

public abstract class UiComponent<T> {
    FXMLLoader fxmlLoader = new FXMLLoader();

    protected void loadFxml(String fxml) {
        loadFxml(getFxmlUrl(fxml));
    }

    protected void loadFxml(URL fxml) {
        requireNonNull(fxml);
        try {
            //fxmlLoader.load(MainScene.class.getResource(fxml));
            fxmlLoader.setLocation(fxml);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private URL getFxmlUrl(String fxmlDir) {
        requireNonNull(fxmlDir);
        URL fxmlFileUrl = MainScene.class.getResource(fxmlDir);
        requireNonNull(fxmlFileUrl);
        return fxmlFileUrl;
    }

    public T getRoot() {
        return fxmlLoader.getRoot();
    }
}
