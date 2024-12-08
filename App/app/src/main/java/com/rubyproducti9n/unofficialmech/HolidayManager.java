package com.rubyproducti9n.unofficialmech;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HolidayManager extends AppCompatActivity {
    static Context c;
    private static final long MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
    public static List<Map.Entry<String, String>> holidayDates = new ArrayList<>();

    public static void initiate(Context context){
        // January 2024
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-01-06", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-01-13", "Makar Sankranti"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-01-20", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-01-26", "Republic Day"));

        // February 2024
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-02-03", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-02-17", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-02-24", "Mahashivratri"));

        // March 2024
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-03-02", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-03-18", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-03-25", "Holi Festival"));

        // April 2024
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-04-06", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-04-19", "Good Friday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-04-20", "3rd Saturday Holiday"));

        // May 2024
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-05-04", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-05-18", "3rd Saturday Holiday"));

        // June 2024
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-06-01", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-06-15", "3rd Saturday Holiday"));

        // Odd Sem
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-06", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-15", "Reporting through ERP Week"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-20", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-21", "Last Day of Reporting"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-22", "Dept Meeting for Academic Planning"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-23", "Last Date: Submit MoM to Dean"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-25", "Finalize & Upload CIA Rubrics"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-26", "ERP Report Submission to Dean"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-27", "Director Teaching Status Submission"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-07-29", "Classes Commencement"));

        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-03", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-15", "Independence Day"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-16", "Student Feedback-1 on Teaching"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-17", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-19", "Syllabus Report to Dean"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-23", "Attendance Communication to Parents"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-24", "Half Term Student Feedback"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-08-29", "Submit Attendance Report"));

        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-05", "Teacher’s Day Celebration"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-07", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-16", "Parent Meet & Attendance Review"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-17", "First Sessional & Lab Exam"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-20", "Last Date: Display Lab Marks"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-21", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-25", "Display Student Feedback & Attendance"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-09-28", "Submit MoM & Feedback Report"));

        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-02", "Gandhi Jayanti"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-05", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-14", "Dean Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-17", "Syllabus Report to Dean"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-19", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-22", "Dept Meeting: Student Feedback"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-10-25", "Academic Activities Review"));

        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-02", "1st Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-15", "Final Date for CIA Activities"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-16", "3rd Saturday Holiday"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-16", "Course Exit Surveys Start"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-19", "Final Attendance Generation"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-22", "Final Attendance Submission"));
        holidayDates.add(new AbstractMap.SimpleEntry<>("2024-11-25", "Sessional/Practical Exams Begin"));

        Date expiryDate = parseDate("2024-11-20");

        checkHolidays(holidayDates, context);
    }

    public static void main(String[] args) {
        //TODO: Testing Playground
    }

    public static void scheduleNotification(Date holidayDate, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(holidayDate);

        Intent intent = new Intent(context, HolidayManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) holidayDate.getTime(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()){

            }
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    public static void checkHolidays(List<Map.Entry<String, String>> holidays, Context context) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        for (int month = 0; month < Calendar.DECEMBER; month++) {
            calendar.set(Calendar.YEAR, currentYear);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            int counter = 0;
            while (calendar.get(Calendar.MONTH) == month) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && (counter == 0 || counter == 2)) {
                    String dateString = formatDate(calendar.getTime());
                    holidays.add(new AbstractMap.SimpleEntry<>(dateString, "Saturday Holiday")); // Adding Saturday holidays
                }
                counter = (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ? counter + 1 : counter;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        // Check if today is a holiday
        String todaysDate = formatDate(Calendar.getInstance().getTime());
        for (Map.Entry<String, String> entry : holidays) {
            if (entry.getKey().equals(todaysDate)) {
                checkHoliday(todaysDate, context);
                holidays.remove(entry); // Remove processed holiday
                break; // Exit loop as we found today's holiday
            }
        }
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static void checkHoliday(String date, Context c) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        if (date.equals(formatDate(tomorrow.getTime()))) {
            showDialog(c, "Tomorrow is Holiday");
        } else if (date.equals(formatDate(Calendar.getInstance().getTime()))) {
            showDialog(c, "It's a holiday today!");
        }
    }

    public static boolean isValidDate(Date date) {
        Calendar currentDate = Calendar.getInstance();
        return currentDate.before(date);
    }

    public static Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void showDialog(Context c, String msg){
        String eMsg = "Disclaimer: Referring to the previous academic calendars, there are high chances of a Holiday";
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(c);
        b.setTitle("Holiday!");
        b.setMessage(msg + "\n\n" + eMsg);
        b.show();
        notifyUser(c);
    }

    public static void notifyUser(Context c){
        NotificationManager notificationManager = (NotificationManager)
        c.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("holiday_channel", "Holiday", NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription("Holiday notifications");
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 100, 100});
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, "holiday_channel")
                .setSmallIcon(R.drawable.ic_unofficial)
                .setContentTitle("HOLIDAY")
                .setContentText("Its a Holiday today!")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager.notify(1,
                builder.build());
    }


}
