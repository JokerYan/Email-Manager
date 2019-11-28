package com.manager.model.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class of the email sender containing the name and address.
 */
public class Sender {
    private String name;
    private String address;

    public Sender(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Constructor of the sender class with the json object containing the information.
     *
     * @param senderInfo json object containing the information
     * @throws JSONException exception of failure of parsing
     */
    public Sender(JSONObject senderInfo) throws JSONException {
        this.name = senderInfo.getString("name");
        this.address = senderInfo.getString("address");
    }

    public String toString() {
        return name + " <" + address + ">";
    }

    public String toJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("address", address);
        return json.toString();
    }

    /**
     * Formats sender in a user friendly way to be displayed in WebView.
     *
     * @return a user friendly display of sender
     */
    public String toWebViewString() {
        return name + " &#60;" + address + "&#62;";
    }
}