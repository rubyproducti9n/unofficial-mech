package com.rubyproducti9n.unofficialmech;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class EventScheduleActivity extends BaseActivity {

    ConstraintLayout createEvent, eventDetailsCard, noEvent;
    CircularProgressIndicator progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);

        //Views
        createEvent = findViewById(R.id.createEvent);
        eventDetailsCard = findViewById(R.id.evenDetails);
        noEvent = findViewById(R.id.noEvent);
        progress = findViewById(R.id.progress);

        MaterialButton register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sanjivaniconcert.online/"));
            startActivity(intent);
            //Snackbar.make(register, "Oops! something went wrong, please try again later", Snackbar.LENGTH_SHORT).show();
        });

        ImageView eventImg = findViewById(R.id.eventImg);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView eventDetails = findViewById(R.id.eventDescription);

        Intent i = getIntent();
        String eventId = i.getStringExtra("eventId");

        if (eventId == null){
            createEvent.setVisibility(VISIBLE);
            eventDetails.setVisibility(GONE);
            noEvent.setVisibility(GONE);
        }

        if (eventId!=null  && eventId.equals("Shirley Setia Concert")){
            createEvent.setVisibility(GONE);
            noEvent.setVisibility(GONE);
            Picasso.get().load("https://instagram.fnag6-2.fna.fbcdn.net/v/t51.29350-15/479492605_1365822141496718_2075561964178648938_n.heic?stp=dst-jpg_e35_tt6&efg=eyJ2ZW5jb2RlX3RhZyI6ImltYWdlX3VybGdlbi4xNDQweDE4MDAuc2RyLmYyOTM1MC5kZWZhdWx0X2ltYWdlIn0&_nc_ht=instagram.fnag6-2.fna.fbcdn.net&_nc_cat=102&_nc_oc=Q6cZ2AGnLvOMH4hqyApQwuQtAr0Zs0vAc0xRQ8Q2PB_kzNPLll-XXtJX2WEU7FFV7xM0_HI8GlrcnC6VzotVTJG4Bsn0&_nc_ohc=76HFKHiXNPEQ7kNvgGPklJQ&_nc_gid=9b221b3f1bc74398acfb62264226e4e8&edm=AP4sbd4BAAAA&ccb=7-5&ig_cache_key=MzU2OTMwMTg4NzEwOTY4NDU2MA%3D%3D.3-ccb7-5&oh=00_AYDJULNl01LEF7Iis8fRnaR-mBUUBV0H5DARxpM4Lg62Eg&oe=67CB6ADC&_nc_sid=7a9f4b").into(eventImg);
            eventTitle.setText("Shirley Setia Concert");
            eventDetails.setText("Book your tickets for DJ night in Sanjivani University for first time in history of Snjivani.");
            register.setText("Book your ticket");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ProjectToolkit.fadeIn(eventDetailsCard);
                    ProjectToolkit.fadeOut(progress);
                }
            }, 1000);

        }else{
            createEvent.setVisibility(VISIBLE);
            eventDetails.setVisibility(GONE);
            noEvent.setVisibility(GONE);
            Snackbar.make(register, "Event expired!", Snackbar.LENGTH_SHORT).show();
        }

    }
}