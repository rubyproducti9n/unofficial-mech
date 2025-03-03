package com.rubyproducti9n.unofficialmech;

import static com.google.firebase.database.core.utilities.Utilities.tryParseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AttendanceSheetActivity extends BaseActivity {

    TextView txt;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        getWindow().setStatusBarColor(Color.parseColor("#28282B"));

        txt = findViewById(R.id.txt);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();
        String filePath = intent.getStringExtra("attendance_sheet");

        if (filePath != null){
            getData(filePath);
        }else{
            fileNotFound();
        }

    }

    private JSONObject readFile(String filePath){
        try{
            FileInputStream inputStream = new FileInputStream(new File(filePath));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) !=null){
                stringBuilder.append(line);
            }

            inputStream.close();
            return new JSONObject(stringBuilder.toString());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getData(String filePath){
        JSONObject jsonObject = readFile(filePath);

        if (jsonObject!=null){

//            List<SheetModel> dataList = new ArrayList<>();
//
//            Iterator<String> keys = jsonObject.keys();
//            while(keys.hasNext()){
//                String key = keys.next();
//                try{
//                    dataList.add(new SheetModel(key, jsonObject.get(key)));
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            AttendanceSheetAdapter adapter = new AttendanceSheetAdapter(dataList);
//            recyclerView.setAdapter(adapter);

            try{
                String date = jsonObject.getString("Date");
                String division = jsonObject.getString("Division");
//                String roll = jsonObject.getBoolean("");
                // Create a TreeMap to store key-value pairs sorted by key (ascending)
                Map<Integer, Boolean> attendanceData = new TreeMap<>();

                // Iterate through keys and store in the map
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (tryParseInt(key)) {
                        int rollNumber = Integer.parseInt(key); // Convert key to integer
                        boolean rollValue = jsonObject.getBoolean(key);
                        attendanceData.put(rollNumber, rollValue);
                    }
                }

                // Create a LinearLayout for holding checkboxes dynamically
                LinearLayout layout = findViewById(R.id.checkbox_layout);
                layout.removeAllViews();

                // Iterate through sorted key-value pairs and create checkboxes
                for (Map.Entry<Integer, Boolean> entry : attendanceData.entrySet()) {
                    int rollNumber = entry.getKey();
                    boolean rollValue = entry.getValue();

                    // Create a checkbox
                    MaterialCheckBox checkBox = new MaterialCheckBox(this);
                    String attendance = null;
                    if (rollValue){
                        attendance = "Present";
                    }else{
                        attendance = "Absent";
                    }
                    checkBox.setText("Roll Number: " + rollNumber + " (" + attendance + ")");
                    checkBox.setChecked(rollValue); // Set initial checked state
                    checkBox.setEnabled(false);
                    checkBox.setUseMaterialThemeColors(true);
                    checkBox.setPadding(8,8,8,8);

                    // Add checkbox to the layout
                    layout.addView(checkBox);
                }

                txt.setText("Date & Time: " + date + "\n" +  division + " section");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private boolean tryParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void fileNotFound(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendanceSheetActivity.this);
        builder.setTitle("Error 404")
                .setMessage("File not found, try searching in recycle bin")
                .setCancelable(false)
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(AttendanceSheetActivity.this, AttendenceTrackerActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
        builder.show();
    }

    public class SheetModel{
        private Object key;
        private Object value;

        public SheetModel(Object key, Object value){
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

}

