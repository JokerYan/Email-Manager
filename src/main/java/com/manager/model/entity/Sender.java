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
        this.name = senderInfo.getJSONObject("emailAddress").getString("name");
        this.address = senderInfo.getJSONObject("emailAddress").getString("address");
    }

    /**
     * Constructor of the sender based on the string output of a sender.
     *
     * @param senderString the string of sender toString() output used to parse a sender
     */
    public Sender(String senderString) {
        String name = senderString.split("=>")[0].strip();
        String address = senderString.split("=>")[1].strip();
        this.name = name;
        this.address = address;
    }

    public String toString() {
        return name + " => " + address;
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