package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreateLetter extends AppCompatActivity {

    TextInputEditText dateInputTxt, toInputTxt, subjectInputTxt, bodyInputText;
    MaterialButton loadTemplate, submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_letter);

        dateInputTxt = findViewById(R.id.dateInput);
        toInputTxt = findViewById(R.id.toInput);
        subjectInputTxt = findViewById(R.id.subjectInput);
        bodyInputText = findViewById(R.id.bodyInput);

        loadTemplate = findViewById(R.id.loadTemplate);
        submit = findViewById(R.id.postBtn);

        dateInputTxt.setText(getCurrentDate());

        dateInputTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePicker();
            }
        });

        toInputTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateList();
            }
        });

        loadTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTemplate();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValidDateFormat(Objects.requireNonNull(dateInputTxt.getText()).toString());
            }
        });

    }

    private void initiateList(){
        String[] items = {"Dr. P. M. Patare (HOD)", "Dr. S. V. Bhaskar"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("To:")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                toInputTxt.setText(items[0]);
                                break;
                            case 1:
                                toInputTxt.setText(items[1]);
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void initiatePicker(){
        DatePicker datePicker = new DatePicker(this);
        datePicker.setMinDate(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7); // Allow past week
        datePicker.setMaxDate(System.currentTimeMillis()); // Today's date

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = String.format("%02d/%02d/%d", monthOfYear + 1, dayOfMonth, year);
                dateInputTxt.setText(selectedDate);
            }
        });
        datePicker.isShown();
//        datePicker.show();
    }


    private boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return false;
        }

        // Split the string by '/' delimiter
        String[] dateParts = dateStr.split("/");

        // Check if there are exactly 3 parts
        if (dateParts.length != 3) {
            return false;
        }

        // Check if all parts are integers
        for (String part : dateParts) {
            if (!part.matches("\\d+")) {
                return false;
            }
        }

        // Convert to integers
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        // Check for valid day range (1-31)
        if (day < 1 || day > 31) {
            return false;
        }

        // Check for valid month range (1-12)
        if (month < 1 || month > 12) {
            return false;
        }

        // Simple check for leap year (not foolproof but sufficient for format validation)
        if (month == 2 && day > 28 && !isLeapYear(year)) {
            return false;
        }

        submit();
        return true;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private void loadTemplate(){
        String[] items = {"Permission Letter", "Visit Letter"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("To:")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                loadPermissionLetter();
                                break;
                            case 1:
                                loadVisitLetter();
                                break;
                        }
                    }
                });
        builder.show();
    }

    private String getCurrentDate(){
        String formatedDate = null;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formatedDate = format.format(date);
    }

    private void loadPermissionLetter(){
        dateInputTxt.setText("03/02/2004");
        toInputTxt.setText("Dr. P.M. Patare (HOD Mech)");
        subjectInputTxt.setText("Permission letter for [Replace by your subject]");
        bodyInputText.setText("Respected sir,\n ...");
    }
    private void loadVisitLetter(){
        dateInputTxt.setText("03/02/2004");
        toInputTxt.setText("Dr. P.M. Patare (HOD Mech)");
        subjectInputTxt.setText("Permission letter for visit in [Replace by your subject]");
        bodyInputText.setText("Respected sir,\n ...");
    }
    private void submit(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("letters/");
        String id = ref.push().getKey();
        ref.child(id).setValue("let1");

        reset();
    }

    private void reset(){
        dateInputTxt.setText(" ");
        dateInputTxt.setHint("dd/MM/yyyy");
        toInputTxt.setText(" ");
        subjectInputTxt.setText(" ");
        subjectInputTxt.setHint("[Your subject goes here]");
        bodyInputText.setText(" ");
        bodyInputText.setHint("Letter body goes here...");
    }


}