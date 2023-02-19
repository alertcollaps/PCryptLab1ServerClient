package com.company.Client.Protocols;


import com.company.Client.URLRequest;
import com.company.Server.handler.LastRecord;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SKEY implements Protocol{
    static final int countN = 5;
    private String preString = "[S/KEY]:";


    @Override
    public void startProtocol() {
        Scanner in = new Scanner(System.in);
        System.out.print(preString + "Введите логин: ");
        String name = in.nextLine();

        System.out.print(preString + "Введите пароль: ");
        String passUser = in.nextLine();


        startSending(name, passUser);
    }


    public void startSending(String name, String password) {
        Map out = new HashMap<>();

        out.put("step", 0);
        out.put("name", name);
        JSONObject request = new JSONObject(out);
        URLRequest urlRequest = new URLRequest();
        System.out.println(preString + "Sending to server: " + request);
        String req = urlRequest.sendPOST("/SKEY", request.toString()).getResponse();
        System.out.println(preString + "Get request from server: " + req);
        JSONObject reqJsn = new JSONObject(req);

        byte[] N = null;
        int count = reqJsn.getInt("count");
        byte[] hash = null;

        N = Hash.hexStringToByteArray(reqJsn.getString("N"));
        SKEYsave.arrayHashes = calcHash(N, password.getBytes(StandardCharsets.UTF_8));

        hash = SKEYsave.arrayHashes[countN-1-count];



        out.clear();

        out.put("step", 1);
        out.put("name", name);
        out.put("hash", Hash.bytesToHex(hash));
        request = new JSONObject(out);
        System.out.println(preString + "Sending to server: " + request);
        String response = urlRequest.sendPOST("/SKEY", request.toString()).getResponse();
        System.out.println(preString + "Get answer from server: " + response);


    }

    private byte[][] calcHash(byte[] N, byte[] pass){
        byte[] res = com.company.Server.handler.Hash.concatArrays(N, pass);
        byte[][] hash = new byte[countN][];
        Hash hashDig = new Hash();
        for (int i = 0; i < countN; i++){
            hash[i] = hashDig.getHash(res);
            res = hash[i];
        }
        return hash;
    }
}
