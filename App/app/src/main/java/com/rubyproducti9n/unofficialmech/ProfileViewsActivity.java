package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.Algorithms.getYear;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeOut;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ProfileViewsActivity extends BaseActivity {
    TextView un, det;
    ImageView img;
    ConstraintLayout layout;
//    CircularProgressIndicator shimmer;
    MaterialToolbar toolbar;
    MaterialCardView mc;
    RecyclerView recyclerView;

    private CircularProgressIndicator progressInd;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());


    private DevAccountsAdapter adapter;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_views);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mc = findViewById(R.id.materialCardView23);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        img = findViewById(R.id.imageView4);

        progressInd = findViewById(R.id.progress);
        layout = findViewById(R.id.layout);
        recyclerView =findViewById(R.id.recyclerView);

//        shimmer.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        un = findViewById(R.id.username);
        det = findViewById(R.id.details);
        String fn = null;
        if (username!=null){
            fn = extractFirstName(username);
        }else{
            finish();
//            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }


        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        String uid = p.getString("auth_userId", null);
        if (uid!=null){
            fetchUserDetails(ProfileViewsActivity.this, fn);
            loadProfiles();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(() -> fadeIn(progressInd));
                    while(progressStatus < 100){
                        progressStatus += 1;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressInd.setProgress(progressStatus, true);
                            }
                        });
                        try {
                            Thread.sleep(20);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fadeIn(recyclerView);
                                    fadeIn(layout);
                                    fadeOut(progressInd);
                                }
                            });
                        }
                    }, 500);
                }
            }).start();

            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        else {
            layout.setVisibility(View.GONE);
            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this);
            b.setTitle("Security Manager")
                            .setMessage("You need to login first to view other user profiles. Please login and try again.")
                                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
            b.show();
        }

    }

    public String extractFirstName(String fullName) {
        String[] words = fullName.split(" ");

        if (words.length > 0) {
            return words[0];
        } else {
            return "";
        }
    }

    public static String getCurrentYearOfStudy(String prn) {
        // Check if PRN is valid and in the expected format
        if (prn.length() != 9 || !Character.isDigit(prn.charAt(2)) || !Character.isDigit(prn.charAt(3))) {
            return "Invalid PRN format";
        }

        // Extract the year of admission
        int yearOfAdmission = Integer.parseInt(prn.substring(2, 4));

        // Calculate current year
        int currentYear = Year.now().getValue();

        // Calculate year of study
        int yearOfStudy = currentYear - yearOfAdmission;

        // Handle cases for first year and beyond
        if (yearOfStudy == 1) {
            return "1st Year";
        } else if (yearOfStudy > 1) {
            return yearOfStudy + "nd Year"; // Assuming 2nd year is correct
        } else {
            return "Invalid Year"; // This is for the case when yearOfStudy is less than 1 (not possible normally)
        }
    }

    private void fetchUserDetails(Context context, String username){
        ref.orderByChild("firstName").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        String avatar = snap.child("avatar").getValue(String.class);
                        String firstName = snap.child("firstName").getValue(String.class);
                        String lastName = snap.child("lastName").getValue(String.class);
                        String officialEmail = snap.child("clgEmail").getValue(String.class);
                        String personalEmail = snap.child("personalEmail").getValue(String.class);
                        String prn = snap.child("prn").getValue(String.class);
                        String year;
                        if (prn==null || prn.equals("null")){
                            year = null;
                        }else{
                            year = getYear(prn);
                        }
                        String contact = snap.child("contact").getValue(String.class);
                        String division = snap.child("div").getValue(String.class);
                        String gender = snap.child("gender").getValue(String.class);
                        String roll = snap.child("rollNo").getValue(String.class);
                        String role = snap.child("role").getValue(String.class);
                        String paymentDate = snap.child("lastPaymentDate").getValue(String.class);
                        String name = firstName +  " " + lastName;

                        String initials = snap.child("initials").getValue(String.class);
                        Boolean rawStat = snap.child("status").getValue(Boolean.class);


                        String details;
                        if (prn==null || prn.equals("null")){
                            details = "Creator Account";
                        }else{
                            details =
                                    "• PRN: " + prn + "\n\n" +
                                            "• Year: " + year + "\n\n" +
                                            "• Section: " + division + "\n\n" +
                                            "• Roll No.: " + roll;
                        }

                        if (avatar!=null){
                            Picasso.get().load(avatar).into(img);
                            if (firstName.equals("Unofficial")){
                                img.setImageDrawable(getDrawable(R.drawable.ic_unofficial));
                            }
                        } else{
                            img.setImageResource(R.drawable.round_account_circle_24);
                        }


                        if (role.equals("Faculty")){
                            String Status = "";
                            if (rawStat!=null && rawStat){
                                Status = "->> Available in Cabin";
                                mc.setStrokeColor(getResources().getColor(R.color.active));
                            }else if (!rawStat){
                                Status = "->> Busy";
                                mc.setStrokeColor(getResources().getColor(R.color.away));
                            }
                            toolbar.setTitle(lastName + " Sir");
                            details = "Faculty Account" +
                            "\n" + personalEmail +
                            "\n" + officialEmail +
                                    "\n" + contact +
                            "\n\n" + Status;
                            name = initials + " " + lastName + " Sir";
                            if (gender.equals("Male")) {
                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(img);
                            } else if (gender.equals("Female")) {
                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(img);
                            }
                        }else{
                            name = firstName + " " + lastName;
                        }
                        toolbar.setTitle(firstName + " " + lastName);
                        toolbar.setSubtitle(role);
                        un.setText(name);
                        det.setText(details);



                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                                .setTitle("Details")
                                .setMessage(details)
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Contact", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });


                        MaterialButton call = findViewById(R.id.call);
                        MaterialButton mail = findViewById(R.id.email);

                        if (isFaculty() || role.equals("Admin")){
                            if (contact!=null){
                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        initiateCall(contact);
                                    }
                                });
                            }else{
                                call.setEnabled(false);
                            }
                            if (personalEmail!=null){
                                mail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        initiateMail(personalEmail);
                                    }
                                });
                            }else{
                                mail.setEnabled(false);
                            }
                        }else{
                            call.setVisibility(View.GONE);
                            mail.setVisibility(View.GONE);
                        }


//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!firstName.equals(null)){
//                                new Task().execute();
//                            }
//                            }
//                        },3000);

                    }


                }else{
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Database access denied!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadProfiles(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postRef = database.getReference("users");
        Query qr = (Query) postRef.orderByChild("role").equalTo("Faculty");

//        CircularProgressIndicator progressBar =findViewById(R.id.progressBar);
//        ProjectToolkit.fadeIn(progressBar);

        ProjectToolkit.fadeOut(recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(ProfileViewsActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        List<DevAccountsItems> items = new ArrayList<>();
//        TextView errTxt = findViewById(R.id.err);
//        errTxt.setVisibility(View.GONE);
        adapter = new DevAccountsAdapter(ProfileViewsActivity.this, items, 0);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();
                long childNumber = snapshot.getChildrenCount();

                if (snapshot.exists()){
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String facultyId = postSnapshot.child("facultyId").getValue(String.class);
                        String avatar = postSnapshot.child("avatar").getValue(String.class);
                        String firstName = postSnapshot.child("firstName").getValue(String.class);
                        String lastName = postSnapshot.child("lastName").getValue(String.class);
                        String div = postSnapshot.child("div").getValue(String.class);
                        String initials = postSnapshot.child("initials").getValue(String.class);
                        String clgEmail = postSnapshot.child("clgEmail").getValue(String.class);
                        String gender = postSnapshot.child("gender").getValue(String.class);
                        String personalEmail = postSnapshot.child("personalEmail").getValue(String.class);
                        String contact = postSnapshot.child("contact").getValue(String.class);
                        String password = null;
                        String role = postSnapshot.child("role").getValue(String.class);
                        String year = null;

//                        if (firstName.equals("Unofficial")){
//                            avatar = String.valueOf(R.drawable.ic_unofficial);
//                        }
//                        if (firstName.equals("Om") && lastName.equals("Lokhande") && personalEmail.equals("om.lokhande34@gmail.com")){
//                            avatar = "https://github.com/rubyproducti9n/mech/blob/main/avatar/dev_avatar.jpg?raw=true";
//                        }

                        if (role!=null && role.equals("Faculty")){
                            year = role;
                        }else if(role!=null && role.equals("Admin")){
                            year = "Admin";
                        }else{
                            String prn = postSnapshot.child("prn").getValue(String.class);
                            if (prn==null || prn.equals("null")){
                                year = null;
                            }else{
//                                year = getYear(prn);
                                year = "B.Tech";
                            }
                        }
                        Boolean status = postSnapshot.child("status").getValue(Boolean.class);
                        String altPassword = null;
                        boolean suspended = Boolean.TRUE.equals(postSnapshot.child("suspended").getValue(Boolean.class));
                        String dateCreated = null;
                        String lastPaymentDate = null;
                        String name = firstName + " " + lastName;



                        DevAccountsItems postItem = new DevAccountsItems(facultyId, avatar, name, div, year, role, clgEmail, false, suspended);
                        items.add(postItem);
                    }
                    Collections.shuffle(items);
//                    ProjectToolkit.fadeOut(progressBar);
                }else{
                    recyclerView.setVisibility(View.GONE);
//                    errTxt.setVisibility(View.VISIBLE);
//                    ProjectToolkit.fadeOut(progressBar);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileViewsActivity.this, "Error 503", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void initiateCall(String call){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+call));
        startActivity(intent);
    }
    private void initiateWAChat(String WA){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone="+WA));
        startActivity(intent);
    }
    private void initiateMail(String mail){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        //intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        //intent.putExtra(Intent.EXTRA_EMAIL, mail);
        intent.setData(Uri.parse("mailto:"+ mail));
        startActivity(intent);
    }

    private Boolean isFaculty(){
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(ProfileViewsActivity.this);
        String role = s.getString("auth_userole", null);
        if (role!=null){
            return role.equals("Faculty");
        }else {
            return false;
        }
    }


    private class Task extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ProjectToolkit.fadeOut(shimmer);
                    ProjectToolkit.fadeIn(layout);
                }
            });
            return "";
        }
    }


}