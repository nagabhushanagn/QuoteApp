/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils;

/**
 *
 * @author Ngn
 */
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonFileUtils {

    public static void write(JSONObject json, String outputPath) throws Exception {
        Files.write(
                Path.of(outputPath),
                json.toString(4).getBytes(StandardCharsets.UTF_8)
        );
    }
    
    public static JSONObject load(String path) throws Exception {
        try (InputStream is = new FileInputStream(path)) {
            return new JSONObject(new JSONTokener(is));
        }
    }
    
    public static void update(JSONObject root, String path, Object value) {

        if (value == null) return; // keep template default

        String[] tokens = path.split("\\.");
        Object current = root;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            // layers[0]
            if (token.contains("[")) {
                String key = token.substring(0, token.indexOf("["));
                int index = Integer.parseInt(
                        token.substring(token.indexOf("[") + 1, token.indexOf("]"))
                );

                JSONArray array = ((JSONObject) current)
                        .optJSONArray(key);

                if (array == null) {
                    array = new JSONArray();
                    ((JSONObject) current).put(key, array);
                }

                while (array.length() <= index) {
                    array.put(new JSONObject());
                }

                current = array.get(index);
            }
            // final key
            else if (i == tokens.length - 1) {
                ((JSONObject) current).put(token, value);
            }
            // normal object
            else {
                JSONObject obj = (JSONObject) current;
                if (!obj.has(token) || obj.isNull(token)) {
                    obj.put(token, new JSONObject());
                }
                current = obj.getJSONObject(token);
            }
        }
    }
}

