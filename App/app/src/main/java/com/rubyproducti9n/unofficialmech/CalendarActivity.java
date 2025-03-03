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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    List<ItemCalendar> items = new ArrayList<>();
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
        // January 2024
        items.add(new ItemCalendar("2024-01-06", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-01-13", "Makar Sankranti"));
        items.add(new ItemCalendar("2024-01-20", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-01-26", "Republic Day"));

// February 2024
        items.add(new ItemCalendar("2024-02-03", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-02-17", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-02-24", "Mahashivratri"));

// March 2024
        items.add(new ItemCalendar("2024-03-02", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-03-18", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-03-25", "Holi Festival"));

// April 2024
        items.add(new ItemCalendar("2024-04-06", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-04-19", "Good Friday"));
        items.add(new ItemCalendar("2024-04-20", "3rd Saturday Holiday"));

// May 2024
        items.add(new ItemCalendar("2024-05-04", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-05-18", "3rd Saturday Holiday"));

// June 2024
        items.add(new ItemCalendar("2024-06-01", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-06-15", "3rd Saturday Holiday"));

// Odd Sem
        items.add(new ItemCalendar("2024-07-06", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-07-15", "Reporting through ERP Week"));
        items.add(new ItemCalendar("2024-07-20", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-07-21", "Last Day of Reporting"));
        items.add(new ItemCalendar("2024-07-22", "Dept Meeting for Academic Planning"));
        items.add(new ItemCalendar("2024-07-23", "Last Date: Submit MoM to Dean"));
        items.add(new ItemCalendar("2024-07-25", "Finalize & Upload CIA Rubrics"));
        items.add(new ItemCalendar("2024-07-26", "ERP Report Submission to Dean"));
        items.add(new ItemCalendar("2024-07-27", "Director Teaching Status Submission"));
        items.add(new ItemCalendar("2024-07-29", "Classes Commencement"));

        items.add(new ItemCalendar("2024-08-03", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-08-15", "Independence Day"));
        items.add(new ItemCalendar("2024-08-16", "Student Feedback-1 on Teaching"));
        items.add(new ItemCalendar("2024-08-17", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-08-19", "Syllabus Report to Dean"));
        items.add(new ItemCalendar("2024-08-23", "Attendance Communication to Parents"));
        items.add(new ItemCalendar("2024-08-24", "Half Term Student Feedback"));
        items.add(new ItemCalendar("2024-08-29", "Submit Attendance Report"));

        items.add(new ItemCalendar("2024-09-05", "Teacher’s Day Celebration"));
        items.add(new ItemCalendar("2024-09-07", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-09-16", "Parent Meet & Attendance Review"));
        items.add(new ItemCalendar("2024-09-17", "First Sessional & Lab Exam"));
        items.add(new ItemCalendar("2024-09-20", "Last Date: Display Lab Marks"));
        items.add(new ItemCalendar("2024-09-21", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-09-25", "Display Student Feedback & Attendance"));
        items.add(new ItemCalendar("2024-09-28", "Submit MoM & Feedback Report"));

        items.add(new ItemCalendar("2024-10-02", "Gandhi Jayanti"));
        items.add(new ItemCalendar("2024-10-05", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-10-14", "Dean Holiday"));
        items.add(new ItemCalendar("2024-10-17", "Syllabus Report to Dean"));
        items.add(new ItemCalendar("2024-10-19", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-10-22", "Dept Meeting: Student Feedback"));
        items.add(new ItemCalendar("2024-10-25", "Academic Activities Review"));

        items.add(new ItemCalendar("2024-11-02", "1st Saturday Holiday"));
        items.add(new ItemCalendar("2024-11-15", "Final Date for CIA Activities"));
        items.add(new ItemCalendar("2024-11-16", "3rd Saturday Holiday"));
        items.add(new ItemCalendar("2024-11-16", "Course Exit Surveys Start"));
        items.add(new ItemCalendar("2024-11-19", "Final Attendance Generation"));
        items.add(new ItemCalendar("2024-11-22", "Final Attendance Submission"));
        items.add(new ItemCalendar("2024-11-25", "Sessional/Practical Exams Begin"));

        Intent i = new Intent(CalendarActivity.this, LockActivity.class);

        Intent erpIntent = new Intent(Intent.ACTION_VIEW);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        CalendarAdapter adapter = new CalendarAdapter(items, this);
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