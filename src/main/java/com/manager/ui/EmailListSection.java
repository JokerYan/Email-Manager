package com.manager.ui;

import com.manager.model.ModelManager;
import com.manager.model.entity.Email;
import com.manager.model.entity.EmailList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class EmailListSection {
    private ListView<Email> emailListView;

    public EmailListSection(ListView<Email> emailListView) {
        this.emailListView = emailListView;
        loadEmailCards(ModelManager.getInstance().getEmailList());
    }

    public void loadEmailCards(EmailList emailList) {
        emailListView.setItems(emailList.asUnmodifiableList());
        emailListView.setCellFactory(listView -> new EmailListViewCell());
    }

    class EmailListViewCell extends ListCell<Email> {
        @Override
        protected void updateItem(Email email, boolean empty) {
            super.updateItem(email, empty);

            if (empty || email == null) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(new EmailCard(email).getRoot());
            }
        }
    }
}
