package com.company.Client.Protocols;

import com.company.Client.URLRequest;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CHAP implements Protocol{
    private String preString = "[CHAP]:";
    private String method = "CHAP";
    private final int sizeN = 8;


    public void startProtocol() {
        Scanner in = new Scanner(System.in);
        System.out.print(preString + "Введите логин: ");
        String name = in.nextLine();

        System.out.print(preString + "Введите пароль: ");
        String pass = in.nextLine();


        startSending(name, pass);
    }


    public void startSending(String name, String password) {
        Map out = new HashMap<>();

        out.put("step", 0);
        out.put("method", method);
        JSONObject request = new JSONObject(out);
        URLRequest urlRequest = new URLRequest();
        System.out.println(preString + "Sending to server: " + request);
        String hexN = urlRequest.sendPOST("/CHAP", request.toString()).getResponse();
        System.out.println(preString + "Get hex N from server: " + hexN);
        byte[] N = Hash.hexStringToByteArray(hexN);
        Hash hashDig = new Hash();
        byte[] hash = hashDig.getHash(Hash.concatArrays(N, password.getBytes(StandardCharsets.UTF_8)));
        out.clear();

        out.put("step", 1);
        out.put("method", method);
        out.put("name", name);
        out.put("hash", Hash.bytesToHex(hash));
        request = new JSONObject(out);
        System.out.println(preString + "Sending to server: " + request);
        String response = urlRequest.sendPOST("/CHAP", request.toString()).getResponse();
        System.out.println(preString + "Get answer from server: " + response);

    }

    private byte[] generateBytes(){
        SecureRandom random = new SecureRandom();
        byte[] N = new byte[sizeN];
        random.nextBytes(N);
        return N;
    }
}
