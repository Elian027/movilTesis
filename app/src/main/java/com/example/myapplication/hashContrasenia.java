package com.example.myapplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class hashContrasenia {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convertir los bytes hash a una representación hexadecimal
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Manejar la excepción de algoritmo no encontrado según tus necesidades
            return null;
        }
    }
}
