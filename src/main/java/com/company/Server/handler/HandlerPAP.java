package com.company.Server.handler;

import com.company.Server.Login;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HandlerPAP implements HttpHandler {
    private String preString = "[PAP]:";
    private class User{
        String name = null;
        String pass = null;
        User(){

        }
        User(String name, String pass){
            this.name = name;
            this.pass = pass;
        }
    }
    String request = null;
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String response = handlePostRequest(exchange);
        System.out.println(preString + "Server get request from user: " + response);
        User user = new User();
        try {
            JSONObject jsonObject = new JSONObject(response);
            user = new User(jsonObject.getString("name"), jsonObject.getString("password"));

        } catch (JSONException ex){
            ex.printStackTrace();
        }

        boolean passTrue = false;
        try {

            passTrue = user.pass.equals(Login.getPassword(user.name));
            System.out.println(String.format(preString + "Checking passwords: (from local storage) -->%s<-- and (get from user) -->%s<--: %b", Login.getPassword(user.name), user.pass, passTrue));

        } catch (RuntimeException ignored){

        }
        if (passTrue){
            handleResponse(exchange, "Password correct");
            return;
        }
        handleResponse(exchange, "Password or user incorrect");



    }

    protected void handleResponse(HttpExchange httpExchange, String answer) throws IOException {
        System.out.println(preString + "Server send to user answer: " + answer);
        OutputStream outputStream = httpExchange.getResponseBody();
        httpExchange.sendResponseHeaders(200, answer.length());

        outputStream.write(answer.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    protected String handlePostRequest(HttpExchange httpExchange){
        String responseString = "";

        try (InputStream inputStream = httpExchange.getRequestBody()) {
            byte[] response = inputStream.readAllBytes();
            responseString = new String(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }
}
