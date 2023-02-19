package com.company.Server;

public class Login { //Логины и пароли
    public static String[][] logPas = {{"alex", "trash", "server"}, {"qwerty", "123AAA", "server_pass"}};
    public static String getPassword(String name) throws RuntimeException{
        for (int i = 0; i < logPas[0].length; i++){
            if (logPas[0][i].equals(name)){
                return logPas[1][i];
            }
        }
        throw new RuntimeException("No user");
    }

    public static boolean userExist(String name){
        for (int i = 0; i < logPas[0].length; i++){
            if (logPas[0][i].equals(name)){
                return true;
            }
        }
        return false;
    }


}
