package com.company.Client;

import com.company.Client.Protocols.*;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Protocol protocol = null;



        while (true){
            System.out.println("\n\n1 PAP\n2 CHAP\n3 CHAPMod\n4 SKEY");
            System.out.print("Введите номер протокола или exit для выхода: ");
            Scanner in = new Scanner(System.in);
            String protocolStr = in.nextLine();
            switch (protocolStr) {
                case "exit" -> {
                    in.close();
                    return;
                }
                case "1" -> protocol = new PAP();
                case "2" -> protocol = new CHAP();
                case "3" -> protocol = new CHAPMod();
                case "4" -> protocol = new SKEY();
                default -> {
                    System.out.print("Неверное значение");
                    continue;
                }
            }
            System.out.println();
            protocol.startProtocol();
        }




    }
}
