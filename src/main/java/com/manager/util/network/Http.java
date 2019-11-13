package com.manager.util.network;

import com.manager.model.ModelManager;
import com.manager.model.entity.Email;
import com.manager.util.exception.NetworkException;
import com.manager.util.exception.ParseException;
import com.manager.util.exception.StorageException;
import com.manager.logic.parser.FormatParser;
import com.manager.storage.EmailStorage;
import javafx.application.Platform;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import static com.manager.util.message.NetworkMessage.ACCESS_CODE_RESPONSE_FORMAT_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.ACCESS_CODE_URL_CONNECTION_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.ACCESS_CODE_URL_FORMAT_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.API_PARAMETER_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.BROWSER_URI_FORMAT_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.EMAIL_API_PROTOCOL_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.EMAIL_API_SERIALIZING_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.EMAIL_API_URL_FORMAT_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.HTTP_START_CONNECTION_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.REFRESH_ACCESS_RESPONSE_FORMAT_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.REFRESH_ACCESS_URL_CONNECTION_ERROR_MESSAGE;
import static com.manager.util.message.NetworkMessage.REFRESH_ACCESS_URL_FORMAT_ERROR_MESSAGE;

/**
 * A class containing helper functions related to Http request of calling Email API.
 */
public class Http {
    private static String authCode = null;
    private static String accessToken = null;
    private static String refreshToken = null;
    private static String clientId = "feacc09e-5364-4386-92e5-78ee25d2188d";
    private static String clientSecret = "8dhu0-v80Ic-ZrQpACgWLEPg:??1MGkc";
    private static String redirect = "http://localhost:" + SimpleServer.getPort();
    private static String scope = "openid+Mail.Read+offline_access";

    /**
     * Starts process to obtain authorisation token from user account.
     */
    public static void startAuthProcess() throws NetworkException {
        try {
            refreshToken = EmailStorage.getInstance().readRefreshToken();
        } catch (StorageException e) {
            throw new NetworkException(e.getMessage());
        }
        if ("".equals(refreshToken)) {
            getAuth();
        } else {
            refreshAccess();
        }
    }

    /**
     * Sets the Authorization Code and then call the function to get the Access Token from Outlook.
     *
     * @param code teh new authentication code
     */
    public static void setAuthCode(String code) throws NetworkException {
        //UI.getInstance().showDebug("Auth Code Set: " + code);
        authCode = code;
        getAccess();
    }

    /**
     * Sets the Access Token and call the fetch email API.
     *
     * @param token the new access token
     */
    private static void setAccessToken(String token) {
        accessToken = token;
        //Solves the problem of HTTP not on the same thread as the FX
        Platform.runLater(() -> {
            try {
                ModelManager.getInstance().addEmails(fetchEmail(60));
            } catch (NetworkException e) {
                e.printStackTrace();
            }
        });
    }

    private static void setRefreshToken(String token) throws StorageException {
        refreshToken = token;
        EmailStorage.getInstance().saveRefreshToken(token);
    }

    /**
     * Fetches email from Outlook serer.
     *
     * @param limit the limit of number of emails to be fetched. 0 means all emails are fetched.
     * @return the list of emails fetched
     */
    public static ArrayList<Email> fetchEmail(int limit) throws NetworkException {
        JSONObject apiParams = prepareParams(limit);
        return callFetchEmailWithParams(apiParams);
    }

    private static ArrayList<Email> callFetchEmailWithParams(JSONObject apiParams) throws NetworkException {
        try {
            String httpResponse = callEmailApi(apiParams);
            return FormatParser.parseFetchResponse(httpResponse);
        } catch (ParseException e) {
            throw new NetworkException(e.getMessage());
        }
    }

    private static JSONObject prepareParams(int limit) throws NetworkException {
        JSONObject apiParams = new JSONObject();
        try {
            apiParams.put("select", "subject,from,body,receivedDateTime");
            if (limit > 0) {
                apiParams.put("top", Integer.toString(limit));
            }
            apiParams.put("orderby", "receivedDateTime%20desc");
        } catch (JSONException e) {
            throw new NetworkException(API_PARAMETER_ERROR_MESSAGE);
        }
        return apiParams;
    }

    /**
     * Fetches a new Authorization Code from Outlook. It also calls to start the server to prepare receiving
     * the code from Outlook redirection.
     */
    private static void getAuth() throws NetworkException {
        SimpleServer.startServer();
        openBrowser(prepareAuthUrl());
    }

    private static String prepareAuthUrl() {
        return "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id="
                + clientId + "&response_type=code"
                + "&redirect_uri=" + redirect + "&scope=" + scope;
    }

    /**
     * Fetches the Access Token from Outlook.
     */
    //@@author Navoneel Talukdar & Stunner
    //function adapted from https://stackoverflow
    // .com/questions/40574892/how-to-send-post-request-with-x-www-form-urlencoded-body
    private static void getAccess() throws NetworkException {
        try {
            HttpURLConnection conn = setupAccessConnection();

            String response = getConnectionResponse(conn);
            setTokensFromResponse(response);
        } catch (MalformedURLException e) {
            throw new NetworkException(ACCESS_CODE_URL_FORMAT_ERROR_MESSAGE);
        } catch (IOException e) {
            throw new NetworkException(ACCESS_CODE_URL_CONNECTION_ERROR_MESSAGE);
        } catch (JSONException e) {
            throw new NetworkException(ACCESS_CODE_RESPONSE_FORMAT_ERROR_MESSAGE);
        } catch (StorageException e) {
            throw new NetworkException(e.getMessage());
        }
    }

    private static void setTokensFromResponse(String response) throws JSONException, StorageException {
        JSONObject json = new JSONObject(response);
        setAccessToken(json.getString("access_token"));
        setRefreshToken(json.getString("refresh_token"));
    }
    //@@author

    private static void refreshAccess() throws NetworkException {
        try {
            HttpURLConnection conn = setupRefreshConnection();
            String response = getConnectionResponse(conn);
            setTokensFromResponse(response);
        } catch (MalformedURLException e) {
            throw new NetworkException(REFRESH_ACCESS_URL_FORMAT_ERROR_MESSAGE);
        } catch (IOException e) {
            throw new NetworkException(REFRESH_ACCESS_URL_CONNECTION_ERROR_MESSAGE);
        } catch (JSONException e) {
            throw new NetworkException(REFRESH_ACCESS_RESPONSE_FORMAT_ERROR_MESSAGE);
        } catch (StorageException e) {
            throw new NetworkException(e.getMessage());
        }
    }

    /**
     * Calls the respective Email API based on the parameters given.
     *
     * @param params the parameters regarding the specification of this email api call in JSON form
     * @return the result of email api call in string
     */
    //@@author baeldung
    //This function is adapted from code on https://www.baeldung.com/java-http-request
    private static String callEmailApi(JSONObject params) throws NetworkException {
        String url = "";
        try {
            url = prepareApiUrl(params);
            //UI.getInstance().showDebug(url);
            HttpURLConnection conn = setupEmailConnection(url);
            String content = getConnectionResponse(conn);
            return content;
        } catch (JSONException e) {
            throw new NetworkException(EMAIL_API_SERIALIZING_ERROR_MESSAGE);
        } catch (MalformedURLException e) {
            throw new NetworkException(EMAIL_API_URL_FORMAT_ERROR_MESSAGE);
        } catch (ProtocolException e) {
            throw new NetworkException(EMAIL_API_PROTOCOL_ERROR_MESSAGE);
        } catch (IOException e) {
            throw new NetworkException(HTTP_START_CONNECTION_ERROR_MESSAGE);
        }
    }
    //@@author

    //convert the parameters for email api call in json to a url in string
    private static String prepareApiUrl(JSONObject params) throws JSONException {
        String url = "https://graph.microsoft.com/v1.0/me/mailfolders/inbox/messages?";
        Iterator<String> keys = params.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            url += "$" + key + "=" + params.getString(key);
            if (keys.hasNext()) {
                url += "&";
            }
        }
        return url;
    }

    private static HttpURLConnection setupEmailConnection(String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        configureEmailConnection(conn);
        return conn;
    }

    private static void configureEmailConnection(HttpURLConnection conn) throws ProtocolException {
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setInstanceFollowRedirects(false);
    }

    private static HttpURLConnection setupAccessConnection() throws IOException {
        String requestUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        String params = "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + authCode
                + "&redirect_uri=" + redirect + "&grant_type=authorization_code";
        return setupPostRequestConnection(requestUrl, params);
    }

    private static HttpURLConnection setupRefreshConnection() throws IOException {
        String requestUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        String params = "client_id=" + clientId + "&client_secret=" + clientSecret
                + "&refresh_token=" + refreshToken + "&scope=" + scope + "&grant_type=refresh_token";
        return setupPostRequestConnection(requestUrl, params);
    }

    private static HttpURLConnection setupPostRequestConnection(String requestUrl, String params) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        configurePostRequestConnection(conn, params);
        return conn;
    }

    private static void configurePostRequestConnection(HttpURLConnection conn, String params) throws IOException {
        byte[] postData = params.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        conn.setConnectTimeout(5000);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(postData);
    }

    private static String getConnectionResponse(HttpURLConnection conn) throws IOException {
        BufferedReader in = prepareBufferedReader(conn);
        StringBuffer response = readFromBuffer(in);
        conn.disconnect();
        return response.toString();
    }

    private static StringBuffer readFromBuffer(BufferedReader in) throws IOException {
        StringBuffer response = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }

    private static BufferedReader prepareBufferedReader(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        Reader streamReader;
        if (status > 299) {
            streamReader = new InputStreamReader(conn.getErrorStream());
        } else {
            streamReader = new InputStreamReader(conn.getInputStream());
        }
        return new BufferedReader(streamReader);
    }

    /**
     * Opens the system browser for user authorization process.
     *
     * @param link a url to which the browser will be directed
     * @return a flag whether the operation is successfully executed
     */
    //This function is adapted from https://stackoverflow
    // .com/questions/10967451/open-a-link-in-browser-with-java-button
    public static boolean openBrowser(String link) throws NetworkException {
        try {
            URI url = new URI(link);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(url);
                    return true;
                } catch (Exception e) {
                    throw new NetworkException(e.getMessage());
                }
            }
        } catch (URISyntaxException e) {
            throw new NetworkException(BROWSER_URI_FORMAT_ERROR_MESSAGE);
        }
        return false;
    }
}
