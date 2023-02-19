package com.company.Server.handler;

import com.company.Server.Login;

import java.util.HashMap;
import java.util.Map;

public class LastRecord {
    public static byte[] serverN = null;
    public static byte[] lastHash = null;
    public static String lastUserName = null;

    public static byte[] userN = null;
    public static String[] users = Login.logPas[0]; //Имена пользователей
    public static int[] currentCount = new int[users.length]; //Текущее значение счетчика

    static final int countN = 5;


    public static int currentCount(String name) {
        int i = 0;
        for (String user : users){
            if (user.equals(name)){
                return currentCount[i];
            }
            i++;
        }
        throw new RuntimeException("No user");
    }

    public static void increaseUser(String name){
        int i = 0;
        for (String user : users){
            if (user.equals(name)){
                currentCount[i] = currentCount[i] < countN - 2 ? ++currentCount[i] : 0;
                return;
            }
            i++;
        }
        throw new RuntimeException("No user");
    }
}
