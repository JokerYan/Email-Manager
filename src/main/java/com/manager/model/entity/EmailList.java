package com.manager.model.entity;

import com.manager.util.exception.DuplicateEmailException;
import com.manager.util.exception.EmailNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.manager.util.CollectionUtil.requireAllNonNull;
import static com.manager.util.message.EmailListMessage.DUPLICATE_EMAIL_ERROR_MESSAGE;
import static com.manager.util.message.EmailListMessage.EMAIL_NOT_FOUND_EXCEPTION;
import static java.util.Objects.requireNonNull;

public class EmailList implements Iterable<Email> {
    private final ObservableList<Email> internalList = FXCollections.observableArrayList();
    private final ObservableList<Email> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    public boolean contains(Email toCheck) {
        return internalList.stream().anyMatch(toCheck::isSameEmail);
    }

    public void add(Email toAdd) throws DuplicateEmailException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL_ERROR_MESSAGE);
        }
        internalList.add(toAdd);
    }

    public void setEmail(Email target, Email editedEmail) throws EmailNotFoundException, DuplicateEmailException {
        requireAllNonNull(target, editedEmail);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new EmailNotFoundException(EMAIL_NOT_FOUND_EXCEPTION);
        }

        if (!target.isSameEmail(editedEmail) && contains(editedEmail)) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL_ERROR_MESSAGE);
        }

        internalList.set(index, editedEmail);
    }

    @Override
    public Iterator<Email> iterator() {
        return null;
    }

    public ArrayList<Email> toList() {
        return new ArrayList<>(this.internalList);
    }
}
