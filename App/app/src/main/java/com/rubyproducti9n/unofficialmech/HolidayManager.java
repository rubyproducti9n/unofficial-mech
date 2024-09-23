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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HolidayManager extends AppCompatActivity {
    static Context c;
    private static final long MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
    public static List<String> holidayDates = new ArrayList<>();

    public static void initiate(Context context){

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


    public static void checkHolidays(List<String> holidays, Context context) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        for (int month = 0; month < Calendar.DECEMBER; month++) {
            calendar.set(Calendar.YEAR, currentYear);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            int counter = 0;
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && (counter == 0 || counter == 2)) {
                    String dateString = formatDate(calendar.getTime());
                    holidays.add(dateString);
                }
                counter = (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ? counter + 1 : counter;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            String todaysDate = formatDate(Calendar.getInstance().getTime());
            if (holidays.contains(todaysDate)) {
                checkHoliday(todaysDate, context);
                holidays.remove(todaysDate); // Remove processed holiday for the day
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
