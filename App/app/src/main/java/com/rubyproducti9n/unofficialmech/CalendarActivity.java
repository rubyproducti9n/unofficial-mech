package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.HolidayManager.holidayDates;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Request the permission
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.SET_ALARM}, PERMISSION_REQUEST_CODE);
//        } else {
////            openAppSettings();
//            // Permission already granted, you can set alarms
////            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
//        }
        requestPermissions();
        //Even Sem
        holidayDates.add("2024-01-26");
        holidayDates.add("2024-02-19");
        holidayDates.add("2024-03-08");
        holidayDates.add("2024-04-09");
        holidayDates.add("2024-04-11");

        //Odd Sem
        holidayDates.add("2024-07-17");
        holidayDates.add("2024-08-15");
        holidayDates.add("2024-09-17");
        holidayDates.add("2024-10-2");
        holidayDates.add("2024-10-12");

        Intent i = new Intent(CalendarActivity.this, LockActivity.class);

        Intent erpIntent = new Intent(Intent.ACTION_VIEW);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        List<ItemCalendar> items = new ArrayList<>();
        items.add(new ItemCalendar(true, 2, "Ganesh Chaturthi"));

        CalendarAdapter adapter = new CalendarAdapter(holidayDates, this);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(CalendarActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SET_ALARM)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SET_ALARM}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now set alarms
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}