package com.example.proyectotablap;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Conexion {
    public static String llamarJson(Context context, String filename) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), "UTF-8"));
        String content = "";
        String line;
        while ((line = reader.readLine()) != null){
            content = content + line;
        }
        return content;
    }
}
