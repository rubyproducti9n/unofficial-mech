package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFaculty;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFemale;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isHOD;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isMale;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestActivity extends BaseActivity {
    private ViewPager2 viewPager;
    //private Handler handler = new Handler();
    private Runnable runnable;
//    private CarouselAdapter adapter;


    ArrayList<Post> cachedPosts = new ArrayList<>();
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //viewPager = findViewById(R.id.viewPager);

        // Image List (Replace with your images)
//        List<String> images = new ArrayList<>();
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/g2.jpg?raw=true");
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/last%20mem/m21.jpg?raw=true");
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/last%20mem/m28.jpg?raw=true");
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/last%20mem/m7.jpg?raw=true");

        // Set Adapter
//        adapter = new CarouselAdapter(images);
//        viewPager.setAdapter(adapter);

        // Add Page Transformer for smooth animation
//        viewPager.setPageTransformer(new DepthPageTransformer());

        // Auto-scroll every 3 seconds
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                int nextItem = viewPager.getCurrentItem() + 1;
//                if (nextItem >= adapter.getItemCount()) {
//                    nextItem = 0;
//                }
//                viewPager.setCurrentItem(nextItem, true);
//                handler.postDelayed(this, 3000);
//            }
//        };

        // Initialize UI elements
        err = findViewById(R.id.err);
        progressBar = findViewById(R.id.shimmer);
        recyclerViewStory = findViewById(R.id.recyclerViewStories);
        recyclerView = findViewById(R.id.recyclerView);
        manager = findViewById(R.id.manager);
        FloatingActionButton fab = findViewById(R.id.fab);
        MaterialButton btn = findViewById(R.id.btn);

        // Set initial visibility
        err.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        manager.setVisibility(View.GONE);

        // Load user role from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userRole = preferences.getString("auth_userole", null);

        handler = new Handler(Looper.getMainLooper());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new PhotosAdapter(this, null);

        // Start background tasks
        new Thread(this::loadSingleMode).start();
        new Thread(this::optimized).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        handler.removeCallbacks(runnable);
    }


    private void optimized(){
        //Thread Optimised code
        ExecutorService exe = Executors.newFixedThreadPool(120);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userRole = preferences.getString("auth_userole", null);

        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(this, 1);
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
                                        Toast.makeText(TestActivity.this, "Error 503", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(TestActivity.this, "Error 503", Toast.LENGTH_SHORT).show();
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

    private void loadSingleMode()  {
        singleMode = findViewById(R.id.singleMode);
        singleLayout = findViewById(R.id.singleLayout);
        singleModeSwitch = findViewById(R.id.singleSwitch);
        txtSingleTroll = findViewById(R.id.trollingTxt);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);

        if (p.getString("auth_userole", null) !=null){
            if (isValentinesDay()){
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        singleLayout.setVisibility(View.GONE);
                        displayRandomSingleLine();
                    }
                });
            }
        }

    }
    private void displayRandomSingleLine() {
        Random random = new Random();
        int randomIndexMale = random.nextInt(singleMaleTrolling.length);
        int randomeIndexFemale = random.nextInt(flirtingWithFemale.length);
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this);
        if (!isFaculty(this) && !isHOD(this)){
            if (isMale(this)){
                b.setMessage(singleMaleTrolling[randomIndexMale]);
            }
            if (isFemale(this)){
                b.setMessage(flirtingWithFemale[randomeIndexFemale]);
            }
            b.show();
        }
    }
    public static boolean isValentinesDay() {
        LocalDate today = LocalDate.now();
        return today.getMonthValue() == 2 && today.getDayOfMonth() == 14;
    }

    //Adapter Inside MainActivity
//    class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
//        private List<String> imageList;
//
//        public CarouselAdapter(List<String> imageList) {
//            this.imageList = imageList;
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            Picasso.get().load(imageList.get(position)).into(holder.imageView);
//            //holder.imageView.setImageResource(imageList.get(position));
//        }
//
//        @Override
//        public int getItemCount() {
//            return imageList.size();
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//            ImageView imageView;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                imageView = itemView.findViewById(R.id.imageView);
//            }
//        }
//    }

    // 🔵 Smooth Transition Effect
//    class DepthPageTransformer implements ViewPager2.PageTransformer {
//        @Override
//        public void transformPage(@NonNull View page, float position) {
//            page.setAlpha(0);
//            if (position <= 1 && position >= -1) {
//                page.setAlpha(1 - Math.abs(position));
//                page.setTranslationX(page.getWidth() * -position);
//            }
//        }
//    }



}
