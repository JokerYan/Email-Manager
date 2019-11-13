package com.manager.logic.parser;


import com.manager.model.entity.Email;
import com.manager.model.entity.Sender;
import com.manager.model.entity.Tag;
import com.manager.util.exception.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.manager.util.TimeUtil.parseEmailDateTime;
import static com.manager.util.TimeUtil.parseTimestamp;

public class FormatParser {

    /**
     * Parses the response of email fetching from Outlook server.
     *
     * @param response response from Outlook server
     * @return a list of emails containing all the parsed email from the response
     * @throws ParseException the exception of the failure of the response parsing
     */
    public static ArrayList<Email> parseFetchResponse(String response) throws ParseException {
        //UI.getInstance().showDebug(response);
        ArrayList<Email> emailList = new ArrayList<Email>();
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray emailJsonArray = responseJson.getJSONArray("value");
            for (int i = 0; i < emailJsonArray.length(); i++) {
                JSONObject emailJson = emailJsonArray.getJSONObject(i);
                Email email = parseComponentsToEmail(emailJson);
                emailList.add(email);
            }
        } catch (JSONException e) {
            throw new ParseException("Email fetch response failed to parse");
        }
        return emailList;
    }

    private static Email parseComponentsToEmail(JSONObject emailJson) throws JSONException {
        String subject = emailJson.getString("subject");
        Sender from = new Sender(emailJson.getJSONObject("from"));
        LocalDateTime dateTime = parseEmailDateTime(emailJson.getString("receivedDateTime"));
        String body = emailJson.getJSONObject("body").getString("content");
        return new Email(subject, from, dateTime, body, emailJson.toString());
    }

    /**
     * Parses an email from a raw json string stored or retrieved from the Outlook server.
     *
     * @param jsonString raw json string of the email to be parsed
     * @return email object as the pars result
     * @throws ParseException thrown when raw json passed in is in wrong format
     */
    public static Email parseRawJson(String jsonString) throws ParseException {
        try {
            JSONObject emailJson = new JSONObject(jsonString);
            return parseComponentsToEmail(emailJson);
        } catch (JSONException e) {
            throw new ParseException("Email raw json failed to parse");
        }
    }

    /**
     * Parses the email information stored in index file in the format of JSON to produce an email.
     *
     * @param jsonString email index json string loaded from index file
     * @return email generated from the index json
     * @throws ParseException when the format of the index json is incorrect
     */
    public static Email parseIndexJson(String jsonString) throws ParseException {
        try {
            JSONObject indexJson = new JSONObject(jsonString);
            String subject = indexJson.getString("subject");
            Sender sender = new Sender(indexJson.getString("sender"));
            LocalDateTime receivedDateTime = parseEmailDateTime(indexJson.getString("receivedDateTime"));
            LocalDateTime updatedOn = parseTimestamp(indexJson.getString("updatedOn"));
            ArrayList<Tag> tags = extractTagsFromJson(indexJson);
            return new Email(subject, sender, receivedDateTime, updatedOn, tags);
        } catch (JSONException e) {
            throw new ParseException("Email index json failed to parse");
        }
    }

    private static ArrayList<Tag> extractTagsFromJson(JSONObject indexJson) throws JSONException {
        JSONArray tagArray = indexJson.getJSONArray("tags");
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i < tagArray.length(); i++) {
            JSONObject tagObject = tagArray.getJSONObject(i);
            tags.add(new Tag(tagObject));
        }
        return tags;
    }
}