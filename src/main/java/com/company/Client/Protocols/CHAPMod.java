package com.company.Client.Protocols;

import com.company.Client.URLRequest;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CHAPMod implements Protocol{
    private String preString = "[CHAP_Mod]:";
    private String method = "CHAP_Mod";
    private final int sizeN = 8;
    @Override
    public void startProtocol() {
        Scanner in = new Scanner(System.in);
        System.out.print(preString + "Введите логин: ");
        String name = in.nextLine();

        System.out.print(preString + "Введите пароль: ");
        String passUser = in.nextLine();

        System.out.print(preString + "Введите пароль сервера: ");
        String passServer = in.nextLine();


        startSending(name, passUser, passServer);
    }

    public void startSending(String name, String passwordUser, String passwordServer){
        Map out = new HashMap<>();

        out.put("step", 0);
        out.put("method", method);
        Hash hashDig = new Hash();
        byte[] number = generateBytes();
        out.put("N", Hash.bytesToHex(number));
        JSONObject request = new JSONObject(out);
        URLRequest urlRequest = new URLRequest();
        System.out.println(preString + "Sending to server: " + request);
        String jsonResponse = urlRequest.sendPOST("/CHAP", request.toString()).getResponse();
        JSONObject response = new JSONObject(jsonResponse);
        System.out.println(preString + "Get hex N from server: " + jsonResponse);
        byte[] passServer = passwordServer.getBytes(StandardCharsets.UTF_8);
        byte[] hashPassServer = hashDig.getHash(Hash.concatArrays(number, passServer));
        byte[] hashGetServer = Hash.hexStringToByteArray(response.getString("hash"));
        if (Arrays.equals(hashGetServer, hashPassServer)){
            System.out.println(preString + "Server validated");
        } else {
            System.out.println("Server dont validated. Exit...");
            return;
        }

        byte[] serverN = Hash.hexStringToByteArray(response.getString("N"));
        byte[] passUser = passwordUser.getBytes(StandardCharsets.UTF_8);

        byte[] hashPassUser = hashDig.getHash(Hash.concatArrays(serverN, passUser));
        out.clear();

        out.put("step", 1);
        out.put("method", method);
        out.put("name", name);
        out.put("hash", Hash.bytesToHex(hashPassUser));
        request = new JSONObject(out);
        System.out.println(preString + "Sending to server: " + request);
        String responseServer = urlRequest.sendPOST("/CHAP", request.toString()).getResponse();
        System.out.println(preString + "Get answer from server: " + responseServer);
    }
    private byte[] generateBytes(){
        SecureRandom random = new SecureRandom();
        byte[] N = new byte[sizeN];
        random.nextBytes(N);
        return N;
    }
}
