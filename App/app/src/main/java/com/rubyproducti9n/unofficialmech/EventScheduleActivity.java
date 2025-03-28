package com.rubyproducti9n.unofficialmech;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventScheduleActivity extends BaseActivity {

    ConstraintLayout createEvent, eventDetailsCard, noEvent;
    CircularProgressIndicator progress;
    LinearProgressIndicator progressBar;

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText eventTitle, eventDescription, registrationLink;
    private ChipGroup eventTypeGroup, departmentGroup;
    private ImageView eventPoster;
    private Uri imageUri;
    private String selectedEventType = null;
    private ArrayList<String> selectedDepartments = new ArrayList<>();
    private MaterialButton submitButton, attachImageButton;

    private EditText eventDateTime;
    private String selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);

// Open Date-Time Picker when clicking on EditText
        eventDateTime = findViewById(R.id.eventDateTime);
        eventDateTime.setOnClickListener(v -> showDateTimePicker());

        //Views
        createEvent = findViewById(R.id.createEvent);
        eventDetailsCard = findViewById(R.id.evenDetails);
        noEvent = findViewById(R.id.noEvent);
        progress = findViewById(R.id.progress);
        progressBar = findViewById(R.id.progressBar);

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
            //Snackbar.make(register, "Event expired!", Snackbar.LENGTH_SHORT).show();
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
        eventTypeGroup.setSelectionRequired(true); // Ensures at least one selection
        eventTypeGroup.setSingleSelection(true); // Enables single selection mode

        eventTypeGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (!checkedIds.isEmpty()) {
                    Chip selectedChip = findViewById(checkedIds.get(0));
                    if (selectedChip != null) {
                        selectedEventType = selectedChip.getText().toString();
                    }
                } else {
                    selectedEventType = null; // No selection
                }
            }
        });

        // Department Selection (Multiple Selection)
        departmentGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                selectedDepartments.clear(); // Clear previous selections
                for (int id : checkedIds) {
                    Chip chip = findViewById(id);
                    if (chip != null) {
                        selectedDepartments.add(chip.getText().toString());
                    }
                }
            }
        });

        // Image Selection
        attachImageButton.setOnClickListener(v -> selectImage());

        // Submit Button Click
        submitButton.setOnClickListener(v -> submitEventDetails());

    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, selectedHour, selectedMinute) -> {
                                // Format selected date & time
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                                selectedDateTime = dateFormat.format(selectedCalendar.getTime());

                                // Set formatted date in EditText
                                eventDateTime.setText(selectedDateTime);
                            }, hour, minute, true);

                    timePickerDialog.show();
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // Prevent past dates
        datePickerDialog.show();
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
        String title = Objects.requireNonNull(eventTitle.getText()).toString().trim();
        String description = Objects.requireNonNull(eventDescription.getText()).toString().trim();
        String link = Objects.requireNonNull(registrationLink.getText()).toString().trim();

        if (title.isEmpty() || title.length() > 25) {
            Toast.makeText(this, "Enter a valid Event Title (Max 25 characters)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty() || description.length() > 500) {
            Toast.makeText(this, "Enter a valid Event Description (Max 500 characters)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (link.isEmpty()) {
            Toast.makeText(this, "Please enter registration link", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Registration Link Format
        if (!link.startsWith("https://")) {
            Toast.makeText(this, "Registration link must start with https://", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDateTime == null || selectedDateTime.isEmpty()) {
            Toast.makeText(this, "Please select an event date & time!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the selected date and check if it's in the future
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        try {
            Date eventDate = dateFormat.parse(selectedDateTime);
            Date currentDate = new Date();

            if (eventDate != null && eventDate.before(currentDate)) {
                Toast.makeText(this, "Event date must be in the future!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid event date format!", Toast.LENGTH_SHORT).show();
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

        // Generate a unique filename
        String fileName = "event_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("events/poster/" + fileName);

        // Upload image
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL after successful upload
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveEventToDatabase(imageUrl); // Save event details with image URL
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show()
                );



    }

    private void saveEventToDatabase(String imageUrl) {
        // Prepare Data for Firebase
        EventModel eventModel = new EventModel(eventTitle.getText().toString().trim(), eventDescription.getText().toString().trim(), registrationLink.getText().toString().trim(), selectedEventType, selectedDepartments, imageUrl, selectedDateTime);

        new MaterialAlertDialogBuilder(EventScheduleActivity.this)
                .setTitle("Schedule Event?")
                .setMessage(
                        "Event Details:" + "\n" +
                                eventTitle.getText().toString().trim() + "\n" +
                                eventDescription.getText().toString().trim() + "\n" +
                                selectedEventType + "\n" +
                                selectedDepartments + "\n" +
                                imageUri + "\n"
                                + "Are you sure you want to schedule this event?")
                .setPositiveButton("Schedule", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(VISIBLE);
                        disableAllInputs();
                        // Proceed to store data in Firebase
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").push();
                        eventRef.setValue(eventModel)
                                .addOnSuccessListener(aVoid ->
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //TODO: Push to Firebase
                                                Toast.makeText(EventScheduleActivity.this, "Event Scheduled Successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                progressBar.setVisibility(GONE);
                                        }
                                    }, 1000)
                                )
                                .addOnFailureListener(e ->
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //TODO: Push to Firebase
                                                Snackbar.make(registrationLink, "Oops! Something went wrong, try again later", Snackbar.LENGTH_SHORT).show();
                                                progressBar.setVisibility(GONE);
                                            }
                                        }, 8000)
                                );

                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void disableAllInputs() {
        // Disable EditText fields
        eventTitle.setEnabled(false);
        eventDescription.setEnabled(false);
        registrationLink.setEnabled(false);

        // Disable Event Type Chips (Single Selection)
        for (int i = 0; i < eventTypeGroup.getChildCount(); i++) {
            View child = eventTypeGroup.getChildAt(i);
            if (child instanceof Chip) {
                child.setEnabled(false);
            }
        }

        // Disable Department Selection Chips (Multi-Selection)
        for (int i = 0; i < departmentGroup.getChildCount(); i++) {
            View child = departmentGroup.getChildAt(i);
            if (child instanceof Chip) {
                child.setEnabled(false);
            }
        }

        // Disable Upload Poster Button
        attachImageButton.setEnabled(false);

        // Disable Submit Button
        submitButton.setEnabled(false);
    }

    public class EventModel {
        private String title;
        private String description;
        private String registrationLink;
        private String eventType;
        private List<String> departments;
        private String eventPosterUri;
        private String date;

        public EventModel() {}

        public EventModel(String title, String description, String registrationLink, String eventType, List<String> departments, String eventPosterUri, String eventDate) {
            this.title = title;
            this.description = description;
            this.registrationLink = registrationLink;
            this.eventType = eventType;
            this.departments = departments;
            this.eventPosterUri = eventPosterUri;
            this.date = eventDate;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getRegistrationLink() { return registrationLink; }
        public String getEventType() { return eventType; }
        public List<String> getDepartments() { return departments; }
        public String getEventPosterUri() { return eventPosterUri; }

        public String getDate() {
            return date;
        }
    }
}