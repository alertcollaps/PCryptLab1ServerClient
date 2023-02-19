package com.company.Server.handler;

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

public class HandlerSKEY implements HttpHandler {
    private String preString = "[S/KEY]:";
    private final int sizeN = 8;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(preString + "Кол-во использований одного случайного числа: " + LastRecord.countN);

        String response = handlePostRequest(exchange);
        System.out.println(preString + "Server get: " + response);

        JSONObject jsonObject = null;
        JSONObject request = new JSONObject();
        Map requestMap = new HashMap<>();

        try {
            jsonObject = new JSONObject(response);

        } catch (JSONException ex){
            ex.printStackTrace();
        }

        int step = jsonObject.getInt("step");
        String name = jsonObject.getString("name");
        if (!Login.userExist(name)){
            handleResponse(exchange, "User incorrect");
            return;
        }
        switch (step) {
            case 0:
                int count = LastRecord.currentCount(name);

                String pass = Login.getPassword(name);

                byte[] N = LastRecord.serverN;
                if (count == 0){
                    N = generateBytes();
                    requestMap.put("N", Hash.bytesToHex(N));
                    LastRecord.serverN = N;
                    LastRecord.lastHash = calcHash(N, pass.getBytes(StandardCharsets.UTF_8));
                } else {
                    requestMap.put("N", Hash.bytesToHex(LastRecord.serverN));
                }

                count = (count + 1) == LastRecord.countN ? 1 : ++count; //Расчет номера транзакции



                LastRecord.lastUserName = name;

                requestMap.put("count", count);
                System.out.println(preString + "Generate N: " + Hash.bytesToHex(N));
                request = new JSONObject(requestMap);
                System.out.println(preString + "Sending to client request: " + request);
                handleResponse(exchange, request.toString());
                break;
            case 1:
                N = LastRecord.serverN;
                String hexHash = jsonObject.getString("hash");
                byte[] hash = Hash.hexStringToByteArray(hexHash);
                Hash hashDig = new Hash();
                byte[] hashCompare = hashDig.getHash(hash);


                if (Arrays.equals(LastRecord.lastHash, hashCompare) && name.equals(LastRecord.lastUserName)) { //Сравнение массивов хешей
                    handleResponse(exchange, "Login success");
                    LastRecord.increaseUser(name); //Увеличение номера транзации
                    LastRecord.lastHash = hash; //Сохранение нового значения хеша
                    return;
                }
                handleResponse(exchange, "Invalid login or password");

            break;
        }

    }

    private byte[] calcHash(byte[] N, byte[] pass){
        System.out.println(preString + "Calc hashes on key N: " + Hash.bytesToHex(N));
        byte[] res = Hash.concatArrays(N, pass);
        byte[] hash = null;
        Hash hashDig = new Hash();
        for (int i = 0; i < LastRecord.countN; i++){
            hash = hashDig.getHash(res);
            res = hash;
            System.out.println(preString + "Hash: " + Hash.bytesToHex(hash));
        }
        return hash;
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
