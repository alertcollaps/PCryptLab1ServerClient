package com.company.Client.Protocols;

import com.company.Client.URLRequest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PAP implements Protocol{
    private String preString = "[PAP]:";
    public void startProtocol(){
        Scanner in = new Scanner(System.in);
        System.out.print(preString + "Введите логин: ");
        String name = in.nextLine();

        System.out.print(preString + "Введите пароль: ");
        String pass = in.nextLine();


        startSending(name, pass);
    }

    public void startSending(String name, String password){
        Map out = new HashMap<>();
        out.put("name", name);
        out.put("password", password);
        JSONObject request = new JSONObject(out);
        URLRequest urlRequest = new URLRequest();
        System.out.println(preString + "Sending to server: " + request);
        String response = urlRequest.sendPOST("/PAP", request.toString()).getResponse();
        System.out.println(preString + "Answer from server: " + response);


    }
}
