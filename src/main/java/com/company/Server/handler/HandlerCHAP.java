package com.company.Server.handler;

import com.company.Server.handler.Hash;
import com.company.Server.Login;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HandlerCHAP implements HttpHandler {
    private String preString = "[CHAP]:";
    private final int sizeN = 8;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = handlePostRequest(exchange);
        System.out.println(preString + "Server get: " + response);
        String method = "";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            method = jsonObject.getString("method");

        } catch (JSONException ex){
            ex.printStackTrace();
        }
        switch (method) {
            case "CHAP":
                int step = jsonObject.getInt("step");
                switch (step) {
                    case 0:
                        byte[] N = generateBytes();
                        System.out.println(preString + "Generate N: " + Hash.bytesToHex(N));
                        System.out.println(preString + "Sending to client N");
                        LastRecord.serverN = N;
                        handleResponse(exchange, Hash.bytesToHex(N));
                        break;
                    case 1:
                        if (LastRecord.serverN != null) {
                            N = LastRecord.serverN;
                            String hexHash = jsonObject.getString("hash");
                            byte[] hash = Hash.hexStringToByteArray(hexHash);
                            String pass = "";
                            try {
                                pass = Login.getPassword(jsonObject.getString("name"));
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                return;
                            }
                            Hash hashDig = new Hash();
                            byte[] hashPass = hashDig.getHash(Hash.concatArrays(N, pass.getBytes(StandardCharsets.UTF_8)));
                            System.out.println(String.format(preString + "Creating hash from local password -->%s<--: %s", pass, Hash.bytesToHex(hashPass)));


                            if (Arrays.equals(hash, hashPass)) { //Сравнение массивов хешей
                                System.out.println(String.format(preString + "Checking hashes: (from local storage) -->%s<--/\n and (get from user) -->%s<--: %b\n", Hash.bytesToHex(hashPass),Hash.bytesToHex(hash), true));

                                handleResponse(exchange, "Login success");
                                return;
                            }
                            System.out.println(String.format(preString + "Checking hashes: (from local storage) -->%s<-- /\nand (get from user) -->%s<--: %b\n", Hash.bytesToHex(hashPass),Hash.bytesToHex(hash), false));

                            handleResponse(exchange, "Invalid login or password");

                        }
                        break;
                }
                break;
            case "CHAP_Mod":
                step = jsonObject.getInt("step");
                switch (step) {
                    case 0:
                        String hexN1 = jsonObject.getString("N");
                        byte[] N1 = Hash.hexStringToByteArray(hexN1);
                        LastRecord.userN = N1;
                        byte[] serverN = generateBytes();
                        LastRecord.serverN = serverN;
                        String pass = "";
                        try {
                            pass = Login.getPassword("server");
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            return;
                        }
                        Hash hashDig = new Hash();
                        byte[] hashServerPass = hashDig.getHash(Hash.concatArrays(N1, pass.getBytes(StandardCharsets.UTF_8)));
                        System.out.println(String.format(preString + "Creating hash from local password of server -->%s<--: %s", pass, Hash.bytesToHex(hashServerPass)));

                        Map<String, String> answer = new HashMap<>();
                        answer.put("N", Hash.bytesToHex(serverN));
                        answer.put("hash", Hash.bytesToHex(hashServerPass));
                        JSONObject jsnAnswer = new JSONObject(answer);
                        handleResponse(exchange, jsnAnswer.toString());

                        break;
                    case 1:
                        if (LastRecord.serverN != null) {
                            serverN = LastRecord.serverN;
                            String hexHash = jsonObject.getString("hash");
                            byte[] hash = Hash.hexStringToByteArray(hexHash);
                            pass = "";
                            try {
                                pass = Login.getPassword(jsonObject.getString("name"));
                            } catch (RuntimeException e) {
                                System.out.println(preString + e);
                                return;
                            }
                            hashDig = new Hash();
                            byte[] hashPass = hashDig.getHash(Hash.concatArrays(serverN, pass.getBytes(StandardCharsets.UTF_8)));
                            System.out.println(String.format(preString + "Creating hash from local password of user -->%s<--: %s", pass, Hash.bytesToHex(hashPass)));


                            if (Arrays.equals(hash, hashPass)) { //Сравнение массивов хешей
                                System.out.println(String.format(preString + "Checking hashes: (from local storage) -->%s<-- /\nand (get from user) -->%s<--: %b\n", Hash.bytesToHex(hashPass),Hash.bytesToHex(hash), true));

                                handleResponse(exchange, "Login success");
                                return;
                            }
                            System.out.println(String.format(preString + "Checking hashes: (from local storage) -->%s<-- /\nand (get from user) -->%s<--: %b\n", Hash.bytesToHex(hashPass),Hash.bytesToHex(hash), false));

                            handleResponse(exchange, "Invalid login or password");

                        }
                        break;
                }
        }




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

    private byte[] generateBytes(){
        SecureRandom random = new SecureRandom();
        byte[] N = new byte[sizeN];
        random.nextBytes(N);
        return N;
    }



}
