package com.rubyproducti9n.unofficialmech;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventScheduleActivity extends BaseActivity {

    ConstraintLayout createEvent, eventDetailsCard, noEvent;
    CircularProgressIndicator progress;

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText eventTitle, eventDescription, registrationLink;
    private ChipGroup eventTypeGroup, departmentGroup;
    private ImageView eventPoster;
    private Uri imageUri;
    private String selectedEventType = null;
    private ArrayList<String> selectedDepartments = new ArrayList<>();
    private MaterialButton submitButton, attachImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);


        //Views
        createEvent = findViewById(R.id.createEvent);
        eventDetailsCard = findViewById(R.id.evenDetails);
        noEvent = findViewById(R.id.noEvent);
        progress = findViewById(R.id.progress);

        eventTitle = findViewById(R.id.createEventTitle);
        eventDescription = findViewById(R.id.createEventHighlight);
        registrationLink = findViewById(R.id.createEventRegistration);
        eventTypeGroup = findViewById(R.id.eventType);
        departmentGroup = findViewById(R.id.eventDept);
        eventPoster = findViewById(R.id.img);
        submitButton = findViewById(R.id.registerEvent);
        attachImageButton = findViewById(R.id.add_image);

        MaterialButton register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sanjivaniconcert.online/"));
            startActivity(intent);
            //Snackbar.make(register, "Oops! something went wrong, please try again later", Snackbar.LENGTH_SHORT).show();
        });

        ImageView eventImg = findViewById(R.id.eventImg);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView eventDetails = findViewById(R.id.eventDescription);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");

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

            new Handler().postDelayed(() -> {
                ProjectToolkit.fadeIn(eventDetailsCard);
                ProjectToolkit.fadeOut(progress);
            }, 1000);

        }else{
            createEvent.setVisibility(VISIBLE);
            eventDetails.setVisibility(GONE);
            noEvent.setVisibility(GONE);
            Snackbar.make(register, "Event expired!", Snackbar.LENGTH_SHORT).show();
        }


        //Logic
        // Character counter for Event Title (Max 25)
        eventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 25) {
                    eventTitle.setError("Max 25 characters allowed");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Character counter for Event Description (Max 500)
        eventDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 500) {
                    eventDescription.setError("Max 500 characters allowed");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Event Type Selection (Single Selection)
        eventTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip != null) {
                selectedEventType = selectedChip.getText().toString();
            }
        });

        // Department Selection (Multiple Selection)
        for (int i = 0; i < departmentGroup.getChildCount(); i++) {
            Chip chip = (Chip) departmentGroup.getChildAt(i);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String dept = buttonView.getText().toString();
                if (isChecked) {
                    selectedDepartments.add(dept);
                } else {
                    selectedDepartments.remove(dept);
                }
            });
        }

        // Image Selection
        attachImageButton.setOnClickListener(v -> selectImage());

        // Submit Button Click
        submitButton.setOnClickListener(v -> submitEventDetails());

    }

    // Open gallery to select image
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                eventPoster.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Validate and Submit Details
    private void submitEventDetails() {
        String title = eventTitle.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String link = registrationLink.getText().toString().trim();

        if (title.isEmpty() || title.length() > 25) {
            Toast.makeText(this, "Enter a valid Event Title (Max 25 characters)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty() || description.length() > 500) {
            Toast.makeText(this, "Enter a valid Event Description (Max 500 characters)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedEventType == null) {
            Toast.makeText(this, "Select an Event Type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDepartments.isEmpty()) {
            Toast.makeText(this, "Select at least one Department", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Select an Event Poster", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Data for Firebase
        EventModel eventModel = new EventModel(title, description, link, selectedEventType, selectedDepartments, imageUri.toString());

        new MaterialAlertDialogBuilder(EventScheduleActivity.this)
                .setTitle("Schedule Event?")
                        .setMessage(
                                eventModel + "\n"
                                + "Are you sure you want to schedule this event?")
                                .show();

        // TODO: Upload eventModel to Firebase
        Toast.makeText(this, "Event Submitted Successfully!", Toast.LENGTH_SHORT).show();
    }

    public class EventModel {
        private String title;
        private String description;
        private String registrationLink;
        private String eventType;
        private List<String> departments;
        private String eventPosterUri;

        public EventModel() {}

        public EventModel(String title, String description, String registrationLink, String eventType, List<String> departments, String eventPosterUri) {
            this.title = title;
            this.description = description;
            this.registrationLink = registrationLink;
            this.eventType = eventType;
            this.departments = departments;
            this.eventPosterUri = eventPosterUri;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getRegistrationLink() { return registrationLink; }
        public String getEventType() { return eventType; }
        public List<String> getDepartments() { return departments; }
        public String getEventPosterUri() { return eventPosterUri; }
    }
}