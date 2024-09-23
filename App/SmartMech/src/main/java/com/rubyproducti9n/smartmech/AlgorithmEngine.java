package com.rubyproducti9n.smartmech;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class AlgorithmEngine extends AppCompatActivity {
    public static SharedPreferences p;
    public static String parent = "app-configuration/";
//    public static DatabaseReference ref;


    public static String getYear(String prn){
//        String prnNumber = "UME21M1075";
        int admissionYear = extractAdmissionYear(prn);
        char gender = extractGender(prn);

        // Get the current year
        Calendar currentDate = Calendar.getInstance();

        // Calculate the current year of the student
        int studentYear = calculateAcademicYear(admissionYear, currentDate);

        // Determine the student's academic year
        String academicYear = determineAcademicYear(studentYear);
        return academicYear;
    }

    private static int extractAdmissionYear(String prnNumber) {
        // Extract the year part from the PRN number
        String yearPart = prnNumber.substring(3, 5);
        return 2000 + Integer.parseInt(yearPart);
    }

    private static char extractGender(String prnNumber) {
        // Extract the gender part from the PRN number
        return prnNumber.charAt(5);
    }
    private static int calculateAcademicYear(int admissionYear, Calendar currentDate) {
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);

        // Adjust academic year based on the end of semester (assuming June/July)
        int studentYear = currentYear - admissionYear;
        if (currentMonth >= Calendar.JUNE) {
            studentYear++;
        }

        return studentYear;
    }
    private static String determineAcademicYear(int studentYear) {
        if (studentYear <= 0) {
            return "Invalid";
        } else if (studentYear == 1) {
            return "F.Y.";
        } else if (studentYear == 2) {
            return "S.Y.";
        } else if (studentYear == 3) {
            return "T.Y.";
        } else if (studentYear <= 4) {
            return "B.Tech";
        } else {
            return "Alumni";
        }
    }


}
