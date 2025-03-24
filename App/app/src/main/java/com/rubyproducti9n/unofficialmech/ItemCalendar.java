package com.rubyproducti9n.unofficialmech;

import android.content.Intent;

public class ItemCalendar {
    private String date;
    private String event;

    public ItemCalendar(String date, String event) {
        this.date = date;
        this.event = event;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
