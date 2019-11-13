package com.manager.storage;

import com.manager.logic.parser.FormatParser;
import com.manager.model.entity.Email;
import com.manager.model.entity.Tag;
import com.manager.util.exception.ParseException;
import com.manager.util.exception.StorageException;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.manager.util.message.StorageMessage.EMAIL_INDEX_FILE_FORMAT_ERROR_MESSAGE;
import static com.manager.util.message.StorageMessage.EMAIL_INDEX_FILE_PARSE_ERROR_MESSAGE;
import static com.manager.util.message.StorageMessage.READ_EMAIL_FILE_IO_ERROR_MESSAGE;
import static com.manager.util.message.StorageMessage.SAVE_EMAIL_FILE_IO_ERROR_MESSAGE;
import static com.manager.util.message.StorageMessage.SAVE_REFRESH_TOKEN_ERROR_MESSAGE;
import static com.manager.util.message.StorageMessage.USER_INFO_FILE_IO_ERROR_MESSAGE;
import static com.manager.util.message.StorageMessage.USER_INFO_FILE_NOT_FOUND_ERROR_MESSAGE;
import static com.manager.storage.StorageUtil.constructDataDirectory;

public class EmailStorage {
    private static EmailStorage emailStorage;
    private static String USER_EMAIL_FILENAME = "user.txt";
    private static String INDEX_FILE_NAME = "email.txt";

    private EmailStorage() {
        constructDataDirectory();
    }

    public static EmailStorage getInstance() {
        if (emailStorage == null) {
            emailStorage = new EmailStorage();
        }
        return emailStorage;
    }

    /**
     * To save the information for the emailList including subject and tags(not implemented yet) for each
     * email before exiting the app.
     *
     * @param emailList the emailList to be saved before exiting the app.
     */
    public void saveEmails(ArrayList<Email> emailList) throws StorageException {
        try {
            saveEmailListToIndex(emailList);
            saveEmailListToFolder(emailList);
        } catch (IOException e) {
            throw new StorageException(SAVE_EMAIL_FILE_IO_ERROR_MESSAGE);
        } catch (JSONException e) {
            throw new StorageException(EMAIL_INDEX_FILE_FORMAT_ERROR_MESSAGE);
        }
    }

    private void saveEmailListToFolder(ArrayList<Email> emailList) throws IOException {
        for (Email email : emailList) {
            if (email.getBody() != null) {
                saveEmailToFolder(email);
            }
        }
    }

    private void saveEmailToFolder(Email email) throws IOException {
        Path emailPath = StorageUtil.prepareEmailPath(email.toFilename());
        StorageUtil.saveToFile(emailPath, email.getRawJson());
    }

    private void saveEmailListToIndex(ArrayList<Email> emailList) throws IOException, JSONException {
        String content = prepareEmailListIndexString(emailList);
        Path indexPath = StorageUtil.prepareDataPath(INDEX_FILE_NAME);
        StorageUtil.saveToFile(indexPath, content);
    }

    private String prepareEmailListIndexString(ArrayList<Email> emailList) throws JSONException {
        String content = "";
        for (Email email : emailList) {
            content += email.toIndexJson().toString() + System.lineSeparator();
        }
        return content;
    }

    /**
     * Get emailList according to previously saved information about emails from the data/email.txt at the
     * start of the app.
     *
     * @param indexDir directory of index file. Pass in empty string if using default.
     * @return EmailList created from data/email.txt.
     */
    public ArrayList<Email> readAllEmailsFromFile(String indexDir) throws StorageException {
        ArrayList<Email> emailList = new ArrayList<>();
        try {
            Path indexPath = StorageUtil.prepareDataPath(assignIndexDirIfNotExist(indexDir));
            List<String> emailStringList = StorageUtil.readLinesFromFile(indexPath);
            for (String emailString : emailStringList) {
                readAndAddEmailWithIndexString(emailList, emailString);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException(READ_EMAIL_FILE_IO_ERROR_MESSAGE);
        } catch (ParseException e) {
            throw new StorageException(EMAIL_INDEX_FILE_PARSE_ERROR_MESSAGE);
        }
        return emailList;
    }

    private String assignIndexDirIfNotExist(String indexDir) {
        if ("".equals(indexDir)) {
            return INDEX_FILE_NAME;
        }
        return indexDir;
    }

    private void readAndAddEmailWithIndexString(ArrayList<Email> emailList, String input)
            throws ParseException, StorageException {
        Email indexEmail = FormatParser.parseIndexJson(input);
        String emailFilename = indexEmail.toFilename();
        try {
            Email fileEmail = readEmailFromFolder(indexEmail, emailFilename);
            emailList.add(fileEmail);
        } catch (IOException e) {
            throw new StorageException(READ_EMAIL_FILE_IO_ERROR_MESSAGE);
        }
    }

    private Email readEmailFromFolder(Email indexEmail, String emailFilename)
            throws IOException, ParseException {
        Path emailPath = StorageUtil.prepareEmailPath(emailFilename);
        String emailContent = StorageUtil.readFromFile(emailPath);
        Email fileEmail = parseEmailFromFolder(indexEmail, emailContent);
        return fileEmail;
    }

    private Email parseEmailFromFolder(Email indexEmail, String emailContent)
            throws ParseException {
        Email fileEmail = FormatParser.parseRawJson(emailContent);
        for (Tag tag : indexEmail.getTags()) {
            fileEmail.addTag(tag);
        }
        fileEmail.setUpdatedOn(indexEmail.getUpdatedOn());
        return fileEmail;
    }

    /**
     * Saves authorisation token for user account.
     *
     * @param token Authorisation token
     */
    public void saveRefreshToken(String token) throws StorageException {
        try {
            Path userPath = StorageUtil.prepareDataPath(USER_EMAIL_FILENAME);
            StorageUtil.saveToFile(userPath, token);
        } catch (IOException e) {
            throw new StorageException(SAVE_REFRESH_TOKEN_ERROR_MESSAGE);
        }
    }

    /**
     * Read token from info file.
     *
     * @return refresh token
     */
    public String readRefreshToken() throws StorageException {
        try {
            Path userPath = StorageUtil.prepareDataPath(USER_EMAIL_FILENAME);
            return StorageUtil.readFromFile(userPath);
        } catch (FileNotFoundException e) {
            throw new StorageException(USER_INFO_FILE_NOT_FOUND_ERROR_MESSAGE);
        } catch (IOException e) {
            throw new StorageException(USER_INFO_FILE_IO_ERROR_MESSAGE);
        }
    }
}
