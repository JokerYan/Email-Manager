package com.manager.model.entity;

import com.manager.util.TimeUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {
    protected ArrayList<Tag> tags = new ArrayList<>();
    private String subject;
    private Sender sender;
    private LocalDateTime receivedDateTime;
    private LocalDateTime updatedOn;
    private String body;
    private String rawJson;

    public Email(String subject) {
        this.subject = subject;
    }

    /**
     * Detailed constructor of Email class with more parameters.
     *
     * @param subject          subject of the email
     * @param sender           the sender of the email
     * @param receivedDateTime the date and time when the email is received
     * @param body             the body of the email
     * @param rawJson          the raw json of the email when retrieved from the Outlook server
     */
    public Email(String subject, Sender sender, LocalDateTime receivedDateTime, String body,
                 String rawJson) {
        this.subject = subject;
        this.sender = sender;
        this.receivedDateTime = receivedDateTime;
        this.body = body;
        this.rawJson = rawJson;
    }

    /**
     * Alternative constructor for Email, used with the information retrieved from the index file.
     *
     * @param subject          subject of the
     * @param sender           the sender of the email
     * @param receivedDateTime the date and time when the email is received
     * @param updatedOn        the time when the email keywords are last updated
     * @param tags             list of tags of the email
     */
    public Email(String subject, Sender sender, LocalDateTime receivedDateTime,
                 LocalDateTime updatedOn, ArrayList<Tag> tags) {
        this.subject = subject;
        this.sender = sender;
        this.receivedDateTime = receivedDateTime;
        this.updatedOn = updatedOn;
        this.tags = tags;
    }


    public boolean isSameEmail(Email toCompare) {
        return subject.equals(toCompare.subject) && body.equals(toCompare.body)
                && (receivedDateTime.compareTo(toCompare.receivedDateTime) == 0);
    }

    /**
     * Get title of this email.
     *
     * @return title of this email.
     */
    public String getSubject() {
        return this.subject;
    }

    public LocalDateTime getReceivedDateTime() {
        return this.receivedDateTime;
    }

    public ArrayList<Tag> getTags() {
        return this.tags;
    }

    /**
     * Sets the updatedOn to current time.
     */
    public void updateTimestamp() {
        updatedOn = TimeUtil.getCurrentDateTime();
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Add tag from string if not exist.
     *
     * @param keyword keyword of the tag
     * @return tag added
     */
    public boolean addTag(String keyword) {
        if (tagExist(keyword)) {
            return false;
        }
        this.tags.add(new Tag(keyword));
        return true;
    }

    public boolean tagExist(String keyword) {
        for(Tag tag : tags) {
            if (tag.sameKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add tag from keywordPair if not exist.
     *
     * @param keywordPair keywordPair of the tag
     * @param relevance   relevance of the tag
     * @return keyword
     */
    public boolean addTag(KeywordPair keywordPair, int relevance) {
        for (Tag tag : tags) {
            if (tag.sameKeyword(keywordPair)) {
                return false;
            }
        }
        this.tags.add(new Tag(keywordPair, relevance));
        return true;
    }

    /**
     * Add tag to tag list if keyword not exist.
     *
     * @param newTag the new tag to be added to the list
     */
    public boolean addTag(Tag newTag) {
        for (Tag tag : tags) {
            if (tag.sameKeyword(newTag)) {
                return false;
            }
        }
        this.tags.add(newTag);
        return true;
    }

    /**
     * Removes the tag with the given keyword in the keyword pair.
     *
     * @param keyword contained by the deleted tag
     */
    public void removeTag(String keyword) {
        for (Tag tag : tags) {
            if (tag.getKeywordPair().getKeyword().equals(keyword)) {
                this.tags.remove(tag);
                return;
            }
        }
    }

    /**
     * Highlights the email with all the tags. Also, longer expression will have a higher priority to be
     * colored currently.
     *
     * @return email body after the coloring
     */
    public String highlightOnTag() {
        ArrayList<String> expressions = getAllExpressions();
        String output = toWebViewString();
        expressions.sort((ex1, ex2) -> ex1.length() >= ex2.length() ? -1 : 1);
        output = addHighlightToExpressions(output, expressions);
        return output;
    }

    private String toWebViewString() {
        String output = "";
        output += "<h3>" + this.subject + "</h3>";
        output += "<h5 style=\"color:gray\">" + this.sender.toWebViewString() + "</h5>";
        output += "<h5 style=\"color:gray\">" + this.getReceivedDateTime() + "</h5>";
        output += bodyWithoutAttachmentImage();
        return output;
    }

    private String bodyWithoutAttachmentImage() {
        String newBody = body.replaceAll("<img",
                "<img style=\"display: none;\"");
        System.out.println(newBody);
        return newBody;
    }

    private ArrayList<String> getAllExpressions() {
        ArrayList<String> expressions = new ArrayList<>();
        for (Tag tag : this.tags) {
            if (tag.getKeywordPair().getKeyword().equals("Spam")) {
                continue;
            }
            for (String expression : tag.getKeywordPair().getExpressions()) {
                expressions.add(expression);
            }
        }
        return expressions;
    }

    private String addHighlightToExpressions(String emailContent, ArrayList<String> expressions) {
        String content = emailContent;
        for (String expression : expressions) {
            Pattern colorPattern = Pattern.compile("(^|\\W)(?!<mark style=\"color:black;"
                            + "background-color:yellow\">)(" + expression + ")(?!</mark>)(\\W|$)",
                    Pattern.CASE_INSENSITIVE);
            Matcher colorMatcher = colorPattern.matcher(content);
            while (colorMatcher.find()) {
                content = colorMatcher.replaceFirst("$1<mark style=\"color:black;background-color:yellow\">"
                        + expression + "</mark>$3");
                colorMatcher = colorPattern.matcher(content);
            }
            //content = colorMatcher.replaceAll("$1<mark style=\"color:black;background-color:yellow\">"
            //        + expression + "</mark>$3");
        }
        return content;
    }

    public String getRawJson() {
        return this.rawJson;
    }

    public String getSenderString() {
        return this.sender.toString();
    }

    public String toFilename() {
        String filename = shaHash(this.subject) + "-" + this.getDateTimePlainString() + ".htm";
        return filename;
    }

    /**
     * Formats the email object to a json object to be saved to index file.
     *
     * @return a json object containing all the parsed information of the email object
     */
    public JSONObject toIndexJson() throws JSONException {
        JSONObject indexJson = new JSONObject();
        indexJson.put("subject", this.subject);
        indexJson.put("sender", this.sender.toJsonString());
        indexJson.put("receivedDateTime", this.getDateTimeString());
        indexJson.put("updatedOn", TimeUtil.formatTimestamp(this.updatedOn));
        JSONArray tagArray = prepareTagJsonArray();
        indexJson.put("tags", tagArray);
        return indexJson;
    }

    private JSONArray prepareTagJsonArray() throws JSONException {
        JSONArray tagArray = new JSONArray();
        for (Tag tag : this.tags) {
            tagArray.put(tag.toJsonObject());
        }
        return tagArray;
    }

    private String getDateTimeString() {
        return TimeUtil.formatEmailDateTime(receivedDateTime);
    }

    private String getDateTimePlainString() {
        return TimeUtil.formatEmailDateTimePlain(receivedDateTime);
    }

    /**
     * Helper function for the email to be printed in command line.
     *
     * @return a string capturing the email info
     */
    public String toCliString() {
        String output = this.subject + System.lineSeparator() + "\t" + "From: " + this.sender.toString()
                + System.lineSeparator() + "\tReceivedDateTime: " + getDateTimeString()
                + System.lineSeparator() + "\t" + "Body: " + body.substring(0, 30) + "..."
                + System.lineSeparator();
        return output;
    }

    public String getBody() {
        return body;
    }

    public String getShaHash() {
        return shaHash(this.subject);
    }

    private String shaHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Change this to UTF-16 if needed
            //md.update(input.getBytes(StandardCharsets.UTF_16));
            md.update(input.getBytes());
            byte[] digest = md.digest();

            String hex = String.format("%064x", new BigInteger(1, digest));
            return hex;
        } catch (NoSuchAlgorithmException e) {
            return input;
        }
    }

    /**
     * Converts information about email to string that will be displayed to user.
     *
     * @return string that will be displayed in GUI
     */
    public String toGuiString() {
        String guiStr = this.subject;
        if (tags.size() > 0) {
            guiStr += System.lineSeparator();
            for (Tag tag : tags) {
                guiStr += " #" + tag.getKeywordPair().getKeyword();
            }
        }
        return guiStr;
    }

}
