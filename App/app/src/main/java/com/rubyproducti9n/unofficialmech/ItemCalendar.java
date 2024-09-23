package com.rubyproducti9n.unofficialmech;

import android.content.Intent;

public class ItemCalendar {
    private boolean isHoliday;
    private int date;
    private String event;

    public ItemCalendar(boolean isHoliday, int date, String event) {
        this.isHoliday = isHoliday;
        this.date = date;
        this.event = event;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
