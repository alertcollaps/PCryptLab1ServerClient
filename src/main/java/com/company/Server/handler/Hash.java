package com.company.Server.handler;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class Hash {
    MessageDigest messageDigest;
    Hash(){
        Security.addProvider(new BouncyCastleProvider());
        try {
            messageDigest = MessageDigest.getInstance("GOST3411");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] getHash(String message){
        byte[] out = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));
        return out;
    }
    public byte[] getHash(byte[] message){
        byte[] out = messageDigest.digest(message);
        return out;
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] concatArrays(byte[] ... arrays){ //Лучшая функция в Рунете (МОЯ) (Работает)
        int length = 0;
        for (byte[] array : arrays){
            length += array.length;
        }
        byte[] out = new byte[length];
        length = 0;
        for (byte[] array : arrays){
            System.arraycopy(array, 0, out, length, array.length);
            length += array.length;
        }
        return out;
    }
}
