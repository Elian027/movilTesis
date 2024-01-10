package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class cargarConfi {
    private static final int CONFIG_FILE_ID = R.raw.config;

    public static String getCorreoR(Context context) {
        Properties properties = new Properties();
        Log.d("ConfigFileId", "Value: " + CONFIG_FILE_ID);
        try (InputStream input = context.getResources().openRawResource(CONFIG_FILE_ID)) {
            properties.load(input);
            return properties.getProperty("correoR");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPasswordR(Context context) {
        Properties properties = new Properties();
        try (InputStream input = context.getResources().openRawResource(CONFIG_FILE_ID)) {
            properties.load(input);
            return properties.getProperty("passwordR");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}