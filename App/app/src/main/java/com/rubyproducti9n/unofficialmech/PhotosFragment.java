package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeOut;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFaculty;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFemale;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isHOD;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isMale;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment {

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
    List<StoryItem> storyItems;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotosFragment newInstance(String param1, String param2) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photos, container, false);

        // Initialize UI elements
        err = view.findViewById(R.id.err);
        progressBar = view.findViewById(R.id.shimmer);
        recyclerViewStory = view.findViewById(R.id.recyclerViewStories);
        recyclerView = view.findViewById(R.id.recyclerView);
        manager = view.findViewById(R.id.manager);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        MaterialButton btn = view.findViewById(R.id.btn);

        // Set initial visibility
        err.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        manager.setVisibility(View.GONE);

        // Load user role from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userRole = preferences.getString("auth_userole", null);

        handler = new Handler(Looper.getMainLooper());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new PhotosAdapter(getContext(), null);

        // Start background tasks
        new Thread(() -> loadSingleMode(view)).start();
        new Thread(this::optimized).start();

        return view;
    }
    private void optimized(){
        //Thread Optimised code
        ExecutorService exe = Executors.newFixedThreadPool(120);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String userRole = preferences.getString("auth_userole", null);

        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
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
                serviceCheck(getContext(), new ProjectToolkit.ServiceCheckCallBack() {
                    @Override
                    public void onResult(Boolean result) {
                        if (result){
                            if (userRole!=null){
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference postRef = database.getReference("posts");
                                DatabaseReference storyRef = database.getReference("stories");

                                List<Post> items = new ArrayList<>();
                                adapter = (PhotosAdapter) new PhotosAdapter(getContext(), items);

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
                                                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
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
                                adapter = (PhotosAdapter) new PhotosAdapter(getContext(), items);

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
                                        Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
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

    //Optimized by AI
//    private void optimized() {
//    ExecutorService executor = Executors.newFixedThreadPool(10); // Optimized thread pool size
//    FloatingActionButton fab = view.findViewById(R.id.fab);
//    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
//    String userRole = preferences.getString("auth_userole", null);
//
//    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
//    recyclerView.setVisibility(View.GONE);
//    progressBar.setVisibility(View.VISIBLE);
//
//    new Thread(() -> serviceCheck(getContext(), result -> {
//        if (!result) return;
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference postRef = database.getReference("posts");
//        List<Post> items = new ArrayList<>();
//        adapter = new PhotosAdapter(getContext(), items);
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                executor.execute(() -> {
//                    List<Post> fetchedItems = new ArrayList<>();
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        Post postItem = postSnapshot.getValue(Post.class);
//                        if (postItem != null) {
//                            fetchedItems.add(postItem);
//                        }
//                    }
//
//                    Collections.reverse(fetchedItems);
//                    items.clear();
//                    items.addAll(fetchedItems);
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        adapter.notifyDataSetChanged();
//                        recyclerView.setAdapter(adapter);
//                        ProjectToolkit.fadeOut(progressBar);
//                        fadeIn(recyclerView);
//                    });
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        postRef.orderByChild("uploadTime").addValueEventListener(postListener);
//        if (userRole == null) {
//            recyclerViewStory.setVisibility(View.GONE);
//        }
//    })).start();
//}
    private void loadSingleMode(View view)  {
        singleMode = view.findViewById(R.id.singleMode);
        singleLayout = view.findViewById(R.id.singleLayout);
        singleModeSwitch = view.findViewById(R.id.singleSwitch);
        txtSingleTroll = view.findViewById(R.id.trollingTxt);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(requireContext());

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
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(requireContext());
        if (!isFaculty(requireContext()) && !isHOD(requireContext())){
            if (isMale(requireContext())){
                b.setMessage(singleMaleTrolling[randomIndexMale]);
            }
            if (isFemale(requireContext())){
                b.setMessage(flirtingWithFemale[randomeIndexFemale]);
            }
            b.show();
        }
    }
    public static boolean isValentinesDay() {
        LocalDate today = LocalDate.now();
        return today.getMonthValue() == 2 && today.getDayOfMonth() == 14;
    }

    public static class StoryItem{
        private String username;

        public StoryItem(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}