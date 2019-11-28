package com.manager.ui;

import com.manager.model.entity.Email;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import static com.manager.util.ui.UiFxml.EMAIL_CARD_FXML;

public class EmailCard extends UiComponent<Region> {
    @FXML
    Label emailSubjectLabel;

    @FXML
    Label emailSenderLabel;

    public final Email email;

    public EmailCard(Email email) {
        super();
        loadFxml(EMAIL_CARD_FXML);
        this.email = email;
        fillEmailDetails();
    }

    private void fillEmailDetails() {
        emailSubjectLabel.setText(email.getSubject());
        emailSenderLabel.setText(email.getSenderString());
    }
}
