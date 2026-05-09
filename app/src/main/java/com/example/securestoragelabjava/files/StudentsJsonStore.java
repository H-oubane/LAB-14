package com.example.securestoragelabjava.files;

import android.content.Context;

import com.example.securestoragelabjava.model.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class StudentsJsonStore {

    public static final String FILE_NAME = "students.json";

    private StudentsJsonStore() {}

    public static void save(Context context, List<Student> students) throws Exception {
        String json = toJson(students);
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static List<Student> load(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(fis, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return fromJson(json);
        } catch (Exception e) {
            return List.of(); // Retourne liste vide si fichier absent/corrompu
        }
    }

    public static boolean delete(Context context) {
        return context.deleteFile(FILE_NAME);
    }

    private static String toJson(List<Student> students) throws Exception {
        JSONArray arr = new JSONArray();
        for (Student s : students) {
            JSONObject obj = new JSONObject();
            obj.put("id", s.id);
            obj.put("name", s.name);
            obj.put("age", s.age);
            arr.put(obj);
        }
        return arr.toString();
    }

    private static List<Student> fromJson(String json) throws Exception {
        JSONArray arr = new JSONArray(json);
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            list.add(new Student(obj.getInt("id"), obj.getString("name"), obj.getInt("age")));
        }
        return list;
    }
}