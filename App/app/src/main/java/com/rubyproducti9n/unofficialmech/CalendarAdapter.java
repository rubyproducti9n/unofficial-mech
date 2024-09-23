package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DashboardViewHolder> {
    Context c;
    private List<String> holidayDates;

    public CalendarAdapter(List<String> itemList, Context c) {
        this.holidayDates = itemList;
        this.c = c;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {
//        String item = holidayDates.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, position);

        // Format the date as "dd MMM"
        DateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        // Set the date in the TextView
        holder.date.setText(formattedDate);

        // Check if the date is a holiday
        boolean isHoliday = isHoliday(calendar.getTime());

        // Set the image based on holiday status
        if (isHoliday) {
            holder.mc.setCardElevation(1);
            holder.imageView.setImageResource(R.drawable.sentiment_very_satisfied_24dp_e8eaed_fill0_wght400_grad0_opsz24); // Replace with your smile emoji resource ID
            holder.event.setText("Holiday");
        } else {
            holder.mc.setCardElevation(6);
            holder.imageView.setImageResource(R.drawable.school_24dp_e8eaed_fill0_wght400_grad0_opsz24); // Replace with your school image resource ID
            holder.event.setText("Working Day");
        }
    }

    @Override
    public int getItemCount() {
        return 365;
    }

    private boolean isHoliday(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        return holidayDates.contains(formattedDate);
    }
    // ViewHolder Class
    public class DashboardViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mc;
        ImageView imageView;
        TextView date, event;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            mc = itemView.findViewById(R.id.mc);
            imageView = itemView.findViewById(R.id.img);
            date = itemView.findViewById(R.id.date);
            event = itemView.findViewById(R.id.event);
        }

    }
}

