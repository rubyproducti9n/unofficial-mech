package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

//    MaterialButton likeBtn, commentBtn,u1,u2,u3,u4;
    int likeCount = 0;
    int commentCount = 0;
    private static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "Default Channel";
    private static final String CHANNEL_DESCRIPTION = "Main Channel";
    private static final int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final String TAG = "PdfDetails";

    ArrayList<Post> cachedPosts = new ArrayList<>();
    ArrayList<AppointmentItem> cachedAppointments = new ArrayList<>();
    ConstraintLayout singleLayout, singleMode;
    TextView txtSingleTroll;
    public static String[] singleMaleTrolling = {
            "Aaj Valentine's day hai, toh apni akelepan ki selfie post krna mat bhulna! ..😂😅",
            "Tumhe toh Valentine's Day pe romance ke bajaye Netflix n Chill ki invitation milti hogi nai😂",
            "Yeh Valentine's ka toh tumhara agenda hi hota hai -- kuch nahi, bas ek din ka akelapan \uD83D\uDE42",
            "Woh couples ko dekho, aur tumhara plan? Aaj toh Netflix ko hi apna Valentine banalo😂",
            "Valentine's day pe apna dil toh nahi, lekin pizza🍕 aur ice-cream🍦 toh zaroor share kr sakte ho😉 \n\n No Zomato was harmed during this message😂"
    };
    private String[] flirtingWithFemale = {
            "Tu single hai behen?\uD83D\uDE02\uD83D\uDE02"
    };
    private String[] singleLines = {
            "Yeh din apne liye nahi hai mere dost...\nZomato se pizza mangwa aur celebrate kr\n#LoveYourSelf",
            "kya aapki life mein bhi gf nahi hai?\nshukar hai nahi hai..muzhse kya puch raha hai bhai\nmeri khud nahi thi \uD83D\uDE42"
    };
    MaterialSwitch singleModeSwitch;
    private PhotosAdapter adapter;
    private StoryAdapter storyAdapter;
    ShimmerFrameLayout progressBar;
    RecyclerView recyclerView, recyclerViewStory;
    ManagerLayout manager;
    Handler handler;
    List<Post> items;
    ConstraintLayout err;
    View view;
    List<PhotosFragment.StoryItem> storyItems;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        progressBar = findViewById(R.id.shimmer);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);

        handler = new Handler(Looper.getMainLooper());
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        optimized();
                    }
                }
        ).start();

        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();

        // Reference to your PDF in Firebase Storage
        storageReference = firebaseStorage.getReference().child("https://firebasestorage.googleapis.com/v0/b/unofficial-mech.appspot.com/o/ADHAR.pdf?alt=media&token=e42cf2fa-c306-46af-aed3-d09e435c9cb2");

        // Get PDF details
        getPdfDetails();

        findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

//        likeBtn = findViewById(R.id.likeButton);
//        commentBtn = findViewById(R.id.commentBtn);
//        u1 = findViewById(R.id.user1);
//        u2 = findViewById(R.id.user2);
//        u3 = findViewById(R.id.user3);
//        u4 = findViewById(R.id.user4);

//        setAlarm();
//        isLiked();
//        pullComment();
//        likeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isLiked();
//            }
//        });
//        commentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommentsDialog c = new CommentsDialog();
//                c.show(getSupportFragmentManager(), "comments");
//            }
//        });
//
//        u1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUser(6);
//            }
//        });
//
//        u2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUser(2);
//            }
//        });
//
//        u3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUser(3);
//            }
//        });
//
//        u4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUser(4);
//            }
//        });

    }

    private void sendRequest(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests/final/");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sessionTime = currentDateTime.format(dateTimeFormatter);

        String uid = ref.push().getKey();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
        String email = p.getString("auth_email", null);

        AttendanceRequest req = new AttendanceRequest(uid, email, sessionTime, true);
        ref.setValue(req);
    }

    private void receiveRequest(){

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
        String email = p.getString("auth_email", null);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests/final/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = snapshot.child("uid").getValue(String.class);
                String timestamp = snapshot.child("timestamp").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                Boolean isValid = snapshot.child("isValid").getValue(Boolean.class);

                if (Boolean.TRUE.equals(isValid)){
                    Snackbar.make(findViewById(R.id.request), "Request accepted", Snackbar.LENGTH_LONG)
                            .setAction("Accept", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new MaterialAlertDialogBuilder(TestActivity.this)
                                            .setTitle("Accepted")
                                            .setMessage("Congrats! The request was accepted successfully")
                                            .show();
                                }
                            })
                            .setAnchorView(R.id.request).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void optimized(){
        //Thread Optimised code
        ExecutorService exe = Executors.newFixedThreadPool(120);

        FloatingActionButton fab = findViewById(R.id.fab);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userRole = preferences.getString("auth_userole", null);

        GridLayoutManager layoutManager = new GridLayoutManager(TestActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceCheck(TestActivity.this, new ProjectToolkit.ServiceCheckCallBack() {
                    @Override
                    public void onResult(Boolean result) {
                        if (result){
                            if (userRole!=null){
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference postRef = database.getReference("posts");
                                DatabaseReference storyRef = database.getReference("stories");

                                List<Post> items = new ArrayList<>();
                                adapter = (PhotosAdapter) new PhotosAdapter(TestActivity.this, items);

                                postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {items.clear();
                                                long childNumber = snapshot.getChildrenCount();
                                                List<Post> fetchedItems = new ArrayList<>();

                                                String postId;
                                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                    postId = postSnapshot.child("postId").getValue(String.class);
                                                    String uid = postSnapshot.child("uid").getValue(String.class);
                                                    String username = postSnapshot.child("userName").getValue(String.class);
                                                    String div = postSnapshot.child("userDiv").getValue(String.class);
                                                    String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
                                                    String caption = postSnapshot.child("caption").getValue(String.class);
                                                    String anonymous = postSnapshot.child("stateVisibility").getValue(String.class);
                                                    String postUrl = postSnapshot.child("postUrl").getValue(String.class);
                                                    Map<String, Boolean> likes = new HashMap<>();

                                                    Post postItem = new Post(postId, uid, username, postUrl, caption, uploadTime, anonymous, likes);
                                                    cachedPosts.add(postItem);
                                                    fetchedItems.add(postItem);
                                                }
                                                items.clear();
                                                items.addAll(fetchedItems);
                                                Collections.reverse(items);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        recyclerView.setAdapter(adapter);
                                                        ProjectToolkit.fadeOut(progressBar);
                                                        fadeIn(recyclerView);
                                                    }
                                                }, 2000);
                                            }
                                        }).start();

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Snackbar.make(recyclerView, "Error 503", Snackbar.LENGTH_LONG).show();
                                    }
                                });

//                        exe.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                            }
//                        });
//                        exe.shutdown();

                            }else{
                                //When user is not logged in
                                DatabaseReference postRef = database.getReference("posts");
                                List<Post> items = new ArrayList<>();
                                adapter = (PhotosAdapter) new PhotosAdapter(TestActivity.this, items);

                                postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {items.clear();
                                                long childNumber = snapshot.getChildrenCount();
                                                List<Post> fetchedItems = new ArrayList<>();

                                                String postId;
                                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                    postId = postSnapshot.child("postId").getValue(String.class);
                                                    String uid = postSnapshot.child("uid").getValue(String.class);
                                                    String username = postSnapshot.child("userName").getValue(String.class);
                                                    String div = postSnapshot.child("userDiv").getValue(String.class);
                                                    String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
                                                    String caption = postSnapshot.child("caption").getValue(String.class);
                                                    String anonymous = postSnapshot.child("stateVisibility").getValue(String.class);
                                                    String postUrl = postSnapshot.child("postUrl").getValue(String.class);
                                                    Map<String, Boolean> likes = new HashMap<>();

                                                    Post postItem = new Post(postId, uid, username, postUrl, caption, uploadTime, anonymous, likes);
                                                    cachedPosts.add(postItem);
                                                    fetchedItems.add(postItem);
                                                }
                                                items.clear();
                                                items.addAll(fetchedItems);
                                                Collections.reverse(items);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        recyclerView.setAdapter(adapter);
                                                        ProjectToolkit.fadeOut(progressBar);
                                                        fadeIn(recyclerView);
                                                    }
                                                }, 2000);
                                            }
                                        }).start();

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Snackbar.make(recyclerView, "Error 503", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                                recyclerViewStory.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }).start();
    }


    private void getPdfDetails() {
        // Get metadata of the file
        storageReference.getMetadata().addOnSuccessListener(metadata -> {
            String pdfName = metadata.getName();
            long pdfSize = metadata.getSizeBytes(); // Size in bytes


            Log.d(TAG, "PDF Name: " + pdfName);
            Log.d(TAG, "PDF Size: " + pdfSize + " bytes");

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Details")
                    .setMessage("Name: " + pdfName + "\n" +
                     "Size: " + pdfSize).show();

            // Now download the file temporarily to get the number of pages
            try {

                File localFile = File.createTempFile("tempPdf", ".pdf");
                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//                    int totalPages = getPdfPageCount(localFile);
//                    Log.d(TAG, "Total Pages: " + totalPages);
                }).addOnFailureListener(e -> Log.e(TAG, "Failed to download PDF: ", e));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to get metadata: ", e));
    }

    // Get total number of pages in the PDF
//    private int getPdfPageCount(File pdfFile) {
//
//        PdfiumCore pdfiumCore = new PdfiumCore(this);
//        int totalPages = 0;
//        try {
//            PdfDocument pdfDocument = pdfiumCore.newDocument(openFileDescriptor(pdfFile));
//            totalPages = pdfiumCore.getPageCount(pdfDocument);
//            pdfiumCore.closeDocument(pdfDocument); // Remember to close the document
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return totalPages;
//    }

    private ParcelFileDescriptor openFileDescriptor(File file) throws IOException {
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

//    private void setUser(int i){
//
//        if (i>1){
//            pushComment();
//        }else {
//            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void pullLike(){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test/Likes/");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    for (DataSnapshot snap : snapshot.getChildren()){
//                        likeCount++;
//                        if (likeCount>0){
//                            likeBtn.setText(String.valueOf(likeCount));
//                        }else{
//                            likeBtn.setText("Like");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void pushLike(String uid){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test/Likes");
//        ref.child(uid).setValue(true);
//    }
//
//    private void isLiked(){
//        pullLike();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test/Likes");
//        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
//        String uid = p.getString("auth_userId", null);
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (uid!=null){
//                    if (snapshot.exists() && snapshot.hasChild(uid)){
//                    likeBtn.setIcon(getDrawable(R.drawable.heart_active));
//                    likeBtn.setText(String.valueOf(likeCount));
//                }else{
//                    pushLike(uid);
//                }
//                }else{
//                    Toast.makeText(TestActivity.this, "User not found", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void pullComment(){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test/Comments/");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    for (DataSnapshot snap : snapshot.getChildren()){
//                        commentCount++;
//                        if (commentCount>0){
//                            commentBtn.setText(String.valueOf(commentCount));
//                        }else{
//                            commentBtn.setText("Comment");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//
//    private void pushComment(){
//        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test/Comments");
//        String uid = p.getString("auth_uid", null);
//
//        if (uid !=null){
//            ref.child(uid).setValue("some comment");
//        }else{
//            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
//        }
//    }


    public static void createNotificationChannel(Context context) {
        long[] vib = {100, 200, 100};
            NotificationChannel
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
            channel.enableLights(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(vib);
            channel.setDescription(CHANNEL_DESCRIPTION);


            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

    }

    private void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(
                Context.ALARM_SERVICE);

// Create an Intent for your BroadcastReceiver
        Intent intent = new Intent(this, ConnectionReceiver.class);

// Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Set the alarm to repeat every 5 minutes
        long interval = 5 * 60 * 1000; // 5 minutes in milliseconds
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }


public class AttendanceRequest{

        String uid;
        String email;
        String timestamp;
        Boolean isValid;

    public AttendanceRequest(String uid, String email, String timestamp, Boolean isValid) {
        this.uid = uid;
        this.email = email;
        this.timestamp = timestamp;
        this.isValid = isValid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}

}
