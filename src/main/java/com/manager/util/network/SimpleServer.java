package com.manager.util.network;

import com.manager.util.exception.NetworkException;
import com.manager.util.message.NetworkMessage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple local server designed to receive authorization code from Outlook through redirect.
 */
//@@author BalusC
//code adapted from https://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
public class SimpleServer {
    private static final int PORT = 53231;
    private static HttpServer server;
    private static ExecutorService httpThreadPool;

    /**
     * Starts the server on port defined.
     */
    public static void startServer() throws NetworkException {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new MyHandler());
            httpThreadPool = Executors.newFixedThreadPool(1);
            ;
            server.setExecutor(httpThreadPool); // creates a default executor
            server.start();
        } catch (IOException e) {
            throw new NetworkException(NetworkMessage.START_SEVER_ERROR_MESSAGE);
        }
    }

    private static void parseAuthCode(String url) throws NetworkException {
        int index = url.indexOf("code=");
        if (index == -1) {
            throw new NetworkException(NetworkMessage.PARSE_AUTH_CODE_ERROR_MESSAGE);
        }
        index += 5;
        String code = url.substring(index);
        Http.setAuthCode(code);
    }

    /**
     * Stops access to server.
     */
    public static void stopServer() {
        if (server != null) {
            System.out.println("server stopping...");
            server.stop(1);
            httpThreadPool.shutdown();
        }
    }

    public static int getPort() {
        return PORT;
    }

    /**
     * Handler used to handle the response of the http request from Outlook.
     */
    public static class MyHandler implements HttpHandler {
        /**
         * Handles the exchange/response of the http request from Outlook, which contains the authorization
         * code. Stops the server once received the code.
         *
         * @param exchange incoming exchange from the Outlook
         * @throws IOException exception if the exchange fails to receive the information from request
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = NetworkMessage.AUTH_SUCCESS_MESSAGE;
            exchange.sendResponseHeaders(200, response.length());
            writeExchangeResponseBody(exchange, response);
            try {
                parseAuthCode(exchange.getRequestURI().getQuery());
            } catch (NetworkException e) {
                e.printStackTrace();
            }
            stopServer();
        }

        private void writeExchangeResponseBody(HttpExchange exchange, String response) throws IOException {
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
//@@author