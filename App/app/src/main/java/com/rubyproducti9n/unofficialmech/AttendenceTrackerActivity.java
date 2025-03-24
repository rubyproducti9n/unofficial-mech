package com.rubyproducti9n.unofficialmech;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttendenceTrackerActivity extends BaseActivity {

    int currentNumber;
    int endNumber;
    private float x1, x2;
    static final int MIN_DISTANCE = 1;

    int s1 = 10;
    int aStart = 1;
    int aEnd = 74;
    int bStart = 75;
    int bEnd = 146;
    int cStart = 147;
    int cEnd = 216;
    TextView roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_tracker);

        getWindow().setStatusBarColor(Color.parseColor("#28282B"));

        roll = findViewById(R.id.roll);
        MaterialButton sheetBtn = findViewById(R.id.sheets);
        sheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileListDialog();
            }
        });

        Intent intent = getIntent();
        String getDivision = intent.getStringExtra("division");
        if (getDivision != null) {
            if (getDivision.equals("A")){
                currentNumber = aStart;
                endNumber = aEnd;
            }else if(getDivision.equals("B")){
                currentNumber = bStart;
                endNumber = bEnd;
            }else if (getDivision.equals("C")){
                currentNumber = cStart;
                endNumber = cEnd;
            }
        }
        AttendanceRecorder.startSession("TY", getDivision);
        updateRoll();

        MaterialButton attBtn = findViewById(R.id.makeAtt);
        attBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Boolean swipeValue = null;

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float currentX = motionEvent.getX();
                        float deltaX = currentX - x1;

                        if ((Math.abs(deltaX) > MIN_DISTANCE)){
                            attBtn.animate()
                                    .translationX(deltaX)
                                    //.setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setDuration(0)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        x2 = motionEvent.getX();
                        float finalX = x2-x1;
                        if (Math.abs(finalX) > MIN_DISTANCE){
                            //Swipe to the right
                            if (x2 > x1){
                                //Toast.makeText(AttendenceTrackerActivity.this, "Swiped right!", Toast.LENGTH_SHORT).show();
                                attBtn.animate()
                                        .translationX(0)
                                        .setDuration(500)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .start();
                                swipeValue = true;
                            }else{
//                                attBtn.setBackgroundResource(R.drawable.away);
                                attBtn.animate()
                                        .translationX(0)
                                        .setDuration(500)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .start();
                                swipeValue = false;
                                //Toast.makeText(AttendenceTrackerActivity.this, "Swiped left!", Toast.LENGTH_SHORT).show();
                            }
                            AttendanceRecorder.record(AttendenceTrackerActivity.this, currentNumber, swipeValue);
                            makeAttendence(currentNumber, endNumber);
                        }else{
                            attBtn.animate()
                                    .translationX(0)
                                    .setDuration(200)
                                    .start();
                        }
                        return true;
                }

                return false;
            }
        });
    }
    private void showFileListDialog(){
        File directory = new File(Environment.getExternalStorageDirectory(), "Android/data/com.rubyproducti9n.unofficialmech/files/Download/Unofficial Mech/Attendance/");
        final List<String> fileList = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()){
            File[] files = directory.listFiles();
            if (files!=null){
                for(File file : files){
                    if (file.isFile() && file.getName().endsWith(".json")){
                        fileList.add(file.getName());
                    }
                }
            }
        }

        if (fileList.isEmpty()){
            Toast.makeText(this, "No sheets found", Toast.LENGTH_SHORT).show();
            return;
        }

        final CharSequence[] items = fileList.toArray(new CharSequence[fileList.size()]);


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendenceTrackerActivity.this);
        builder.setTitle("Choose sheet")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedFile = fileList.get(i);
                        startActivity(selectedFile);
                    }
                });
        builder.show();

    }

    private void startActivity(String selectedFile){
        Intent intent = new Intent(this, AttendanceSheetActivity.class);
        intent.putExtra("attendance_sheet", Environment.getExternalStorageDirectory()+ "/Android/data/com.rubyproducti9n.unofficialmech/files/Download/Unofficial Mech/Attendance/" + selectedFile);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendenceTrackerActivity.this);
        builder.setTitle("Select division")
                .setMessage("Have you saved attendance? Save and convert to Google sheets now")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AttendanceRecorder.flush();
                        Intent intent = new Intent(AttendenceTrackerActivity.this, BetaActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false);
        builder.show();
    }

    private void animator(View view, float translationX){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", translationX);
        animator.setDuration(200);
        animator.start();
    }

    private void makeAttendence(int start, int end){
        if(currentNumber < end){
            currentNumber++;
        }else{
            currentNumber = start;
        }
        updateRoll();
    }

    private void updateRoll(){
        roll.setText(String.valueOf(currentNumber));
    }

}