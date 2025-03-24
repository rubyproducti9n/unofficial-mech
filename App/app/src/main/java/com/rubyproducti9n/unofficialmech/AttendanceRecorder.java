package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttendanceRecorder extends AppCompatActivity {
    public static Map<Object, Object> rollData =  new HashMap<>();
    public static String currentTime;
    public static JSONObject jsonObject;
    public static boolean isSessionStarted= false;

    public static void startSession(String year, String division){
        currentTime = generateFilename(year, division);
        isSessionStarted = true;
        recordSessionDetails(division);
    }

    public static void record(Context context,  int roll, boolean value){
        rollData.put(roll, value);
        updateJsonFile(context, currentTime);
    }

    public static void recordSessionDetails(String division){
        rollData.put("Division", division);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        rollData.put("Date", sdf.format(new Date()));
    }
    private static void updateJsonFile(Context context, String filename) {
        jsonObject = new JSONObject();
        for(Map.Entry<Object, Object> entry : rollData.entrySet()){
            try{
                jsonObject.put(String.valueOf(entry.getKey()), entry.getValue());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Unofficial Mech/Attendance");
        if (!directory.exists()){
            directory.mkdirs();
            //Toast.makeText(context, "Successfully created directory", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show();
        }

        File file = new File(directory, filename);

        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(jsonObject.toString());
            //Toast.makeText(context, "Data created!", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String generateFilename(String year, String division){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return year + "_" + division + "_" + "data_" + sdf.format(new Date()) + ".json";
    }

    public static void flush(){
        rollData.clear();
        currentTime = null;
        isSessionStarted = false;
    }

}
