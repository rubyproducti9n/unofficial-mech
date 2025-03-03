package com.rubyproducti9n.unofficialmech;

//import static com.rubyproducti9n.DataRetrieval.generateUrl;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.context;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.rubyproducti9n.DataRetrieval;
import com.rubyproducti9n.Example;

import java.util.Calendar;
import java.util.Objects;

public class Algorithms extends BaseActivity {

    public interface PaymentServiceCheckCallBack{
        void onResult(Boolean result);
    }

    public static void paymentServiceCheck(Context context, PaymentServiceCheckCallBack callback){
        final Boolean[] disableService = {Boolean.FALSE, Boolean.TRUE};
        final Boolean[] finalVal = {null};

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("app-configuration/payments");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean val = snapshot.getValue(Boolean.class);
                if(val!=null){
                    callback.onResult(val);
                }else{
                    callback.onResult(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    public static String getYear(String prn){

        if (prn == null || prn.length() < 6){
            return null;
        }

//        String prnNumber = "UME21M1075";
        int admissionYear = extractAdmissionYear(prn);
        char gender = extractGender(prn);

        // Get the current year
        Calendar currentDate = Calendar.getInstance();

        // Calculate the current year of the student
        int studentYear = calculateAcademicYear(admissionYear, currentDate);

        // Determine the student's academic year
        return determineAcademicYear(studentYear);
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

    private static String getOrdinalSuffix(int number) {
        if (number >= 11 && number <= 13) {
            return "th";
        }
        switch (number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

//    public static Integer getPlan(Context context){
//        final Integer plan = -1;
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//        String userId = pref.getString("auth_userId", null);
//        DatabaseReference planRef = FirebaseDatabase.getInstance().getReference();
//        planRef.orderByChild(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        plan = snapshot.getValue(Integer.class);
//                    }
//                }else{
//                    plan= -1;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return plan;
//    }


    public static void setNotification(Context c, String title, String msg, Class a){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, "facultyChannel");

        builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setSmallIcon(R.drawable.ic_unofficial); // Replace with your icon resource ID
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)); // Optional: Expandable notification
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(c, a);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Create a NotificationManager instance and issue the notification
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, builder.build());
    }

    public static String fetchServer(){
//        return generateUrl();
        return null;
    }
    public static boolean isAdmin(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String role = preferences.getString("auth_userole", null);
        if (role!=null && role.equals("Admin")){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isFaculty(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String role = preferences.getString("auth_userole", null);
        if (role!=null && role.equals("Faculty") || Objects.equals(role, "faculty")){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isCR(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String role = preferences.getString("auth_userole", null);
        if (role!=null && role.startsWith("Class Representative")){
            return true;
        }else{
            return false;
        }
    }

    public static void selectYear(Context context){
        String[] items = {"TY.BTech"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Select year")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                selectDiv(context);
                                break;
                        }
                    }
                })
                .setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setTitle("Select division")
                                .setMessage("Select a division from given list, to record attendance with respect to year and division.")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.show();
                    }
                });
        builder.show();
    }
    public static void selectDiv(Context context){
        String[] items = {"A", "B", "C"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Select division")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, AttendenceTrackerActivity.class);
                        switch (i){
                            case 0:
                                intent.putExtra("division", "A");
                                context.startActivity(intent);
                                break;
                            case 1:
                                intent.putExtra("division", "B");
                                context.startActivity(intent);
                                break;
                            case 2:
                                intent.putExtra("division", "C");
                                context.startActivity(intent);
                                break;
                        }
                    }
                })
                .setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setTitle("Select division")
                                .setMessage("Select a division from given list, to record attendance with respect to division.")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.show();
                    }
                });
        builder.show();
    }


}
