package com.rubyproducti9n.unofficialmech;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DayAgoSystem {

    public static String getDayAgo(Date date){

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);


        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        long diff = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        int weeks = (int) (days / 7);
        int years = (int) (weeks / 52);
//        long daysPassed = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//        String result = daysPassed + " days ago";
        String result = "";
        if (seconds < 60){
            result = "Just now";
        }else if (minutes < 60){
            result = minutes + " minutes ago";
        }else if (hours < 24){
            result = hours + " hours ago";
        }else if (days == 1){
            result = "1 day ago";
        } else if (days > 1 && days <= 7) {
            result = days + " days ago";
        }else if (weeks == 1){
            result = "1 week ago";
        } else if (weeks > 1 && weeks <= 52) {
            result = weeks + " weeks ago";
        } else if (years == 1) {
            result = "1 year ago";
        }else {
            result = years + " years ago";
        }
        return result;
    }

    public static int getThreshold(String uploadTime){
        int status = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date uploadDate = null;
        try{
            uploadDate = dateFormat.parse(uploadTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long currentTimeMillis = System.currentTimeMillis();
        long uploadTimeMillis = uploadDate.getTime();
        long timeDifference = currentTimeMillis - uploadTimeMillis;

        long thresholdTime = 6 * 60 * 60 * 1000;

        if (timeDifference <= thresholdTime){
            status = 1;
        }

        return status;
    }

}
