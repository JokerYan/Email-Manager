package com.manager.ui;

import com.manager.model.entity.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class MainScene extends VBox {
    @FXML
    private ListView<Email> emailListView;

    private EmailListSection emailListSection;

    public void initMainScene() {
        this.emailListSection = new EmailListSection(emailListView);
    }

}
