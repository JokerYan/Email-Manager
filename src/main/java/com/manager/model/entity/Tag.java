package com.manager.model.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Tag of an email with both a keyword pair and a score indicating its relevance.
 */
public class Tag {
    private static final int INFINITY = 0x3f3f3f;
    private KeywordPair keywordPair;
    private int relevance = INFINITY;

    public Tag(KeywordPair keywordPair, int relevance) {
        this.keywordPair = keywordPair;
        this.relevance = relevance;
    }

    public Tag(String keyword) {
        this.keywordPair = new KeywordPair(keyword);
    }

    /**
     * Initialize from a json object in the same structure as the json output.
     *
     * @param json json object containing the information of this tag, in the same format as the json
     *             output from toJsonObject()
     */
    public Tag(JSONObject json) throws JSONException {
        String keyword = json.getString("keyword");
        JSONArray expressions = json.getJSONArray("expressions");
        ArrayList<String> expressionList = new ArrayList<>();
        for (int i = 0; i < expressions.length(); i++) {
            expressionList.add(expressions.getString(i));
        }
        int relevance = json.getInt("relevance");

        this.keywordPair = new KeywordPair(keyword, expressionList);
        this.relevance = relevance;
    }

    public KeywordPair getKeywordPair() {
        return this.keywordPair;
    }

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    public boolean sameKeyword(String keyword) {
        return keywordPair.getKeyword().toUpperCase().equals(keyword.toUpperCase());
    }

    public boolean sameKeyword(KeywordPair keywordPair) {
        return keywordPair.getKeyword().toUpperCase().equals(keywordPair.getKeyword().toUpperCase());
    }

    public boolean sameKeyword(Tag tag) {
        return keywordPair.getKeyword().toUpperCase().equals(tag.keywordPair.getKeyword().toUpperCase());
    }

    /**
     * Converts tag to a json object for storage purpose.
     *
     * @return formatting result in JSONObject
     */
    public JSONObject toJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("keyword", this.keywordPair.getKeyword());
        JSONArray expressionArray = new JSONArray();
        for (String expression : this.keywordPair.getExpressions()) {
            expressionArray.put(expression);
        }
        json.put("expressions", expressionArray);
        json.put("relevance", this.relevance);
        return json;
    }
}
