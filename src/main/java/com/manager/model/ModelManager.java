package com.manager.model;

import com.manager.model.entity.Email;
import com.manager.model.entity.EmailList;
import com.manager.storage.EmailStorage;
import com.manager.util.exception.DuplicateEmailException;
import com.manager.util.exception.StorageException;

import java.util.List;

public class ModelManager {
    private static ModelManager modelManager;
    private final EmailList emailList;

    private ModelManager() {
        emailList = new EmailList();
        try {
            addEmails(EmailStorage.getInstance().readAllEmailsFromFile(""));
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    public static ModelManager getInstance() {
        if (modelManager == null) {
            modelManager = new ModelManager();
        }
        return modelManager;
    }

    public void addEmail(Email toAdd) throws DuplicateEmailException {
        emailList.add(toAdd);
        updateEmailStorage();
    }

    public void addEmails(List<Email> toAddList) {
        toAddList.forEach(toAdd -> {
            try {
                emailList.add(toAdd);
            } catch (DuplicateEmailException e) {
                //ignore
            }
        });
        updateEmailStorage();
    }

    private void updateEmailStorage() {
        try {
            EmailStorage.getInstance().saveEmails(emailList.toList());
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }
}
