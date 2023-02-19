package com.company.Server;

import com.company.Server.handler.HandlerCHAP;
import com.company.Server.handler.HandlerPAP;
import com.company.Server.handler.HandlerSKEY;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/PAP", new HandlerPAP());
            server.createContext("/CHAP", new HandlerCHAP());
            server.createContext("/SKEY", new HandlerSKEY());
            server.start();
            System.out.println("Server started!");
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
