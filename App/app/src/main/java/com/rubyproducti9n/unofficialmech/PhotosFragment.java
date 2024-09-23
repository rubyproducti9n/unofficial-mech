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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_photos, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadSingleMode(view);
            }
        }).start();
        err = view.findViewById(R.id.err);
        err.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.shimmer);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewStory = (RecyclerView) view.findViewById(R.id.recyclerViewStories);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
         manager = view.findViewById(R.id.manager);
        manager.setVisibility(View.GONE);
        MaterialButton btn = view.findViewById(R.id.btn);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userRole = preferences.getString("auth_userole", null);


        handler = new Handler(Looper.getMainLooper());


        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = (PhotosAdapter) new PhotosAdapter(getContext(), null);


        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        optimized();
                    }
                }
        ).start();

//        serviceCheck(getContext(), new ProjectToolkit.ServiceCheckCallBack() {
//            @Override
//            public void onResult(Boolean result) {
//                if (result.equals(true)){
//                    if (userRole!=null && userRole.equals("Faculty")){
//                        fab.setVisibility(View.GONE);
//                        manager.setVisibility(View.GONE);
//                        ProjectToolkit.fadeIn(recyclerView);
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference postRef = database.getReference("appointment");
//
//                        ProjectToolkit.fadeIn(progressBar);
//
//                        ProjectToolkit.fadeOut(recyclerView);
//                        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
//                        recyclerView.setLayoutManager(layoutManager);
//
//                        List<AppointmentItem> items = new ArrayList<>();
//                        AppointmentAdapter adapter1 = (AppointmentAdapter) new AppointmentAdapter(getContext(), items);
//                        postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                items.clear();
//                                long childNumber = snapshot.getChildrenCount();
//
//                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                    String appointmentId = postSnapshot.child("appointmentId").getValue(String.class);
//                                    String facultyId = postSnapshot.child("facultyId").getValue(String.class);
//                                    String username = postSnapshot.child("user_name").getValue(String.class);
//                                    String div = postSnapshot.child("div").getValue(String.class);
//                                    String caption = postSnapshot.child("caption").getValue(String.class);
//                                    String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
//                                    String appointmentSlot = postSnapshot.child("appointmentTime").getValue(String.class);
//                                    boolean status = Boolean.TRUE.equals(postSnapshot.child("status").getValue(Boolean.class));
//
//                                    AppointmentItem appointmentItem = new AppointmentItem(appointmentId, facultyId, username, div, caption, uploadTime, appointmentSlot, status);
//                                    cachedAppointments.add(appointmentItem);
//                                    items.add(appointmentItem);
//                                }
//                                Collections.reverse(items);
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ProjectToolkit.fadeOut(progressBar);
//                                        ProjectToolkit.fadeIn(recyclerView);
//                                    }
//                                }, 250);
//                                adapter1.notifyDataSetChanged();
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        recyclerView.setAdapter(adapter1);
//                    }else if (userRole==null){
//                        manager.setVisibility(View.VISIBLE);
//                        recyclerView.setVisibility(View.GONE);
//                        progressBar.setVisibility(View.GONE);
//                        fab.setVisibility(View.GONE);
//                    }
//                    else{
//
//                        manager.setVisibility(View.GONE);
//                        ProjectToolkit.fadeIn(recyclerView);
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference postRef = database.getReference("posts");
//
//                        ProjectToolkit.fadeIn(progressBar);
//
//                        ProjectToolkit.fadeOut(recyclerView);
//                        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
//                        recyclerView.setLayoutManager(layoutManager);
//
//                        List<Post> items = new ArrayList<>();
//                        adapter = (PhotosAdapter) new PhotosAdapter(getContext(), items);
//                        postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                items.clear();
//                                long childNumber = snapshot.getChildrenCount();
//
//                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                    String postId = postSnapshot.child("postId").getValue(String.class);
//                                    String username = postSnapshot.child("userName").getValue(String.class);
//                                    String div = postSnapshot.child("userDiv").getValue(String.class);
//                                    String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
//                                    String caption = postSnapshot.child("caption").getValue(String.class);
//                                    String postUrl = postSnapshot.child("postUrl").getValue(String.class);
//                                    Map<String, Boolean> likes = new HashMap<>();
//
//                                    Post postItem = new Post(postId, username, div, postUrl, caption, uploadTime, "public", likes);
//                                    cachedPosts.add(postItem);
//                                    items.add(postItem);
//                                }
//                                Collections.reverse(items);
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ProjectToolkit.fadeOut(progressBar);
//                                        ProjectToolkit.fadeIn(recyclerView);
//                                    }
//                                }, 500);
//                                adapter.notifyDataSetChanged();
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        recyclerView.setAdapter(adapter);
//                        fab.setVisibility(View.GONE);
//                        fab.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(getActivity(), CreatePost.class);
//                                startActivity(intent);
//                            }
//                        });
//                    }
//                }
//                else{
//                    manager.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                    fab.setVisibility(View.GONE);
//                }
//            }
//        });



        return view;
    }

    private void load(View view){
        //Poor Thread management
        ExecutorService exe = Executors.newFixedThreadPool(9);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String userRole = preferences.getString("auth_userole", null);

        serviceCheck(getContext(), new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (result.equals(true)){
                    if (userRole!=null && userRole.equals("Faculty")){
                        recyclerViewStory.setVisibility(View.GONE);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fab.setVisibility(View.GONE);
                                manager.setVisibility(View.GONE);
                                ProjectToolkit.fadeIn(recyclerView);
                            }
                        });

                        DatabaseReference postRef = database.getReference("appointment");
                        ProjectToolkit.fadeIn(progressBar);
                        ProjectToolkit.fadeOut(recyclerView);
                        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
                        recyclerView.setLayoutManager(layoutManager);

                        List<AppointmentItem> items = new ArrayList<>();
                        AppointmentAdapter adapter1 = (AppointmentAdapter) new AppointmentAdapter(getContext(), items);
                        exe.execute(new Runnable() {
                            @Override
                            public void run() {
                                postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        items.clear();
                                        long childNumber = snapshot.getChildrenCount();

                                        if (snapshot.exists()){
                                            err.setVisibility(View.GONE);
                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                String appointmentId = postSnapshot.child("appointmentId").getValue(String.class);
                                                String facultyId = postSnapshot.child("facultyId").getValue(String.class);
                                                String username = postSnapshot.child("user_name").getValue(String.class);
                                                String div = postSnapshot.child("div").getValue(String.class);
                                                String caption = postSnapshot.child("caption").getValue(String.class);
                                                String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
                                                String appointmentSlot = postSnapshot.child("appointmentTime").getValue(String.class);
                                                boolean status = Boolean.TRUE.equals(postSnapshot.child("status").getValue(Boolean.class));

                                                AppointmentItem appointmentItem = new AppointmentItem(appointmentId, facultyId, username, div, caption, uploadTime, appointmentSlot, status);
                                                cachedAppointments.add(appointmentItem);
                                                items.add(appointmentItem);
                                            }

                                        }else{
                                            LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
                                            lottieAnimationView.setAnimation(R.raw.coming_soon);
                                            lottieAnimationView.loop(true);
                                            lottieAnimationView.playAnimation();
                                            err.setVisibility(View.VISIBLE);
                                        }
                                        Collections.reverse(items);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ProjectToolkit.fadeOut(progressBar);
                                                        ProjectToolkit.fadeIn(recyclerView);
                                                    }
                                                });
                                            }
                                        }, 250);
                                        adapter1.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        recyclerView.setAdapter(adapter1);
                        exe.shutdown();
                    }else if (userRole==null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                err.setVisibility(View.VISIBLE);
                                manager.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                fab.setVisibility(View.GONE);
                            }
                        });
                    }
                    else{
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                manager.setVisibility(View.GONE);
                                ProjectToolkit.fadeIn(recyclerView);
                            }
                        });
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference postRef = database.getReference("posts");
                        DatabaseReference storyRef = database.getReference("stories");


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ProjectToolkit.fadeIn(progressBar);
                                ProjectToolkit.fadeOut(recyclerView);
                            }
                        });

                        ExecutorService ex = Executors.newFixedThreadPool(30);
                        ex.execute(new Runnable() {
                            @Override
                            public void run() {
                                GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.setLayoutManager(layoutManager);
                                    }
                                },2000);

                                List<Post> items = new ArrayList<>();
                                adapter = (PhotosAdapter) new PhotosAdapter(getContext(), items);
                                postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        items.clear();
                                        long childNumber = snapshot.getChildrenCount();

                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            String postId = postSnapshot.child("postId").getValue(String.class);
                                            String uid = postSnapshot.child("uid").getValue(String.class);
                                            String username = postSnapshot.child("userName").getValue(String.class);
                                            String div = postSnapshot.child("userDiv").getValue(String.class);
                                            String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
                                            String caption = postSnapshot.child("caption").getValue(String.class);
                                            String postUrl = postSnapshot.child("postUrl").getValue(String.class);
                                            String anon = postSnapshot.child("stateVisibility").getValue(String.class);
                                            Map<String, Boolean> likes = new HashMap<>();


                                            Post postItem = new Post(postId, uid, username, postUrl, caption, uploadTime, anon, likes);
                                            cachedPosts.add(postItem);
                                            items.add(postItem);
                                        }
                                        Collections.reverse(items);
                                        ExecutorService exec = Executors.newSingleThreadExecutor();
                                        exec.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                recyclerView.setAdapter(adapter);
                                            }
                                        });
                                        exec.shutdown();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ProjectToolkit.fadeIn(recyclerView);
                                                        ProjectToolkit.fadeOut(progressBar);
                                                    }
                                                });
                                            }
                                        },2000);
                                        adapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        ex.shutdown();


                        storyItems = new ArrayList<>();
                        storyAdapter = (StoryAdapter) new StoryAdapter(storyItems);
                        storyRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                storyItems.clear();

                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    String storyId = postSnapshot.child("storyId").getValue(String.class);
                                    String username = postSnapshot.child("username").getValue(String.class);


                                    StoryItem storyItem = new StoryItem(username);
//                                                    cachedPosts.add(storyItem);
                                    storyItems.add(storyItem);
                                }
                                Collections.reverse(storyItems);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                recyclerViewStory.setAdapter(storyAdapter);
                                                ProjectToolkit.fadeOut(progressBar);
                                                ProjectToolkit.fadeIn(recyclerViewStory);
                                            }
                                        });
                                    }
                                }, 2000);
                                storyAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                            }
                        });
                        fab.setVisibility(View.GONE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), CreatePost.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
                else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            manager.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            fab.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private class LoadDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
//            optimized();


            return "";
        }

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

    private void loadSingleMode(View view){
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
//                    singleMode.setVisibility(View.VISIBLE);
                    displayRandomSingleLine();
//                    singleModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            if (isChecked){
//                                recyclerView.setVisibility(View.GONE);
//                                displayRandomSingleLine();
//                                singleLayout.setVisibility(View.VISIBLE);
//                            }else{
//                                recyclerView.setVisibility(View.VISIBLE);
//                                singleLayout.setVisibility(View.GONE);
//                            }
//                        }
//                    });
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