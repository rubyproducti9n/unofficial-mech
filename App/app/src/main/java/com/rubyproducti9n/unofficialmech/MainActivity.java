package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.Callbacks.getAdValue;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getPreloadedInterstitial;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isStudent;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadBannerAd;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.startF;


//import com.dcastalia.localappupdate.DownloadApk;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubyproducti9n.smartmech.AlgorithmEngine;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "InAppReviewHelper";
    private ReviewManager reviewManager;
    private TaskCompletionSource<ReviewInfo> taskCompletionSource;

    FirebaseDatabase firebaseDatabase;

    private SearchBar searchBar;
    private SearchView searchView;

    private RecyclerView userListRecyclerView;
    private SearchAdapter userAdapter;
    private DatabaseReference databaseReference;
    private List<SearchAdapter.SearchItem> userList = new ArrayList<>();

    BottomNavigationView bottomNavigationView;
    MaterialToolbar toolbar;


    private MenuItem prevMenuItem;
    BroadcastReceiver broadcastReceiver;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    private ViewPager viewPager;
    DownloadManager.Request request;
GestureDetector gesture;
    Integer value;
    Manager manage;


    long downloadId;

    boolean isNetworkRegistered;
    private int lastSelectedItemId;

    private ActivityResultLauncher<Intent> startActivityForResult;
//    ActivityMainBinding binding;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("lastSelectedItemId", lastSelectedItemId);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        lastSelectedItemId = savedInstanceState.getInt("lastSelectedItemId");
        bottomNavigationView.setSelectedItemId(lastSelectedItemId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setAllowEnterTransitionOverlap(true);

//        goPremium();

        reviewManager = ReviewManagerFactory.create(this);


        userListRecyclerView = findViewById(R.id.userListRecyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        ImageView gemini = findViewById(R.id.gemini);
        gemini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtificialIntelligenceActivity gem = new ArtificialIntelligenceActivity();
                gem.show(getSupportFragmentManager(), "tag");
//                startActivity(new Intent(MainActivity.this, ArtificialIntelligenceActivity.class));
            }
        });

        searchBar = findViewById(R.id.search_bar);
        searchView = findViewById(R.id.searchView);
        loadUsers();
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    loadUsers();
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
            }
        });

        userAdapter = new SearchAdapter(MainActivity.this, userList);
        GridLayoutManager layoutManager1 = (GridLayoutManager) new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.VERTICAL, false);
        userListRecyclerView.setLayoutManager(layoutManager1);
        userListRecyclerView.setAdapter(userAdapter);
//        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchView.setAnimatedNavigationIcon(true);
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

//        MyAds.showAds(MainActivity.this);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (pref(MainActivity.this).getString("auth_userId", null) !=null){
//                    if(isStudent(MainActivity.this)){
//                        if (Math.random() < 0.95){
//                            initiateAd();
//                        }
//                    }
//                }else{
//                    initiateAd();
//                }
//            }
//        }).start();

        FloatingActionButton fab = findViewById(R.id.fab0);
        fab.setOnClickListener(view -> ProjectToolkit.initiatePanicMode(MainActivity.this));

        ImageView bgImg = findViewById(R.id.bgView);

        toolbar = findViewById(R.id.toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.matte_black));

        startF(MainActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceCheck(MainActivity.this, new ProjectToolkit.ServiceCheckCallBack() {
                    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                    @Override
                    public void onResult(Boolean result) {
                        if (result){
                            Intent receivedIntent = getIntent();
                            String action = receivedIntent.getAction();
                            String type = receivedIntent.getType();
                            if (Intent.ACTION_SEND.equals(action) && type!=null){
                                if ("text/plain".equals(type) || "image/*".equals(type)){
                                    String sharedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                                    Uri imageUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

                                    if (sharedText!=null || imageUri!=null){
                                        String[] items = {"Make post", "Publish notice"};

                                        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this)
                                                .setTitle("Share via ...")
                                                .setItems(items, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        switch (i) {
                                                            case 0:
                                                                Intent postIntent = new Intent(MainActivity.this, CreatePost.class);
                                                                postIntent.putExtra("sharedText", sharedText);
                                                                postIntent.putExtra("sharedImage", imageUri.toString());
                                                                startActivity(postIntent);
                                                                break;
                                                            case 1:
                                                                Intent noticeIntent = new Intent(MainActivity.this, CreateNotice.class);
                                                                noticeIntent.putExtra("sharedText", sharedText);
                                                                noticeIntent.putExtra("sharedImage", imageUri.toString());
                                                                startActivity(noticeIntent);
                                                                break;
                                                        }
                                                    }
                                                });
                                        materialAlertDialogBuilder.show();

//                    Intent chooserIntent = Intent.createChooser(postIntent, "Post or Notice");
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{noticeIntent});

                                    }
                                }
                            }

                            broadcastReceiver = new ConnectionReceiver();
                            registerNetworkBroadcast();
                            isNetworkRegistered = true;

                            authUser();
                            showTermsAndConditions();
//                            showRememberDialog();
                            manage = new Manager();
                            Manager.HolidayManager(MainActivity.this);

                            registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    FirebaseDatabase fdForceUpdate = FirebaseDatabase.getInstance();
                                    DatabaseReference forceUpdateRef = fdForceUpdate.getReference("app-configuration/forceUpdate");
                                    forceUpdateRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Boolean forceUpdateValue = snapshot.getValue(Boolean.class);
                                            if (forceUpdateValue){
                                                checkForUpdates();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "An error occurred while checking for updates", Toast.LENGTH_SHORT).show());
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            bgImg.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();

        FloatingActionButton createButton = findViewById(R.id.fab);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetUploadOptions bottomSheetUploadOption = new BottomSheetUploadOptions();
                bottomSheetUploadOption.show(getSupportFragmentManager(), "BottomSheet");
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.nav_settings){
                    settings();
                } else if (item.getItemId() == R.id.nav_career) {
                    career();
                }  else if (item.getItemId() == R.id.nav_feedback) {
                    feedback();
                } else if (item.getItemId() == R.id.whatsapp) {
                    whatsapp();
                } else if (item.getItemId() == R.id.instagram) {
                    instagram();
                } else if (item.getItemId() == R.id.youtube) {
                    youtube();
                } else if (item.getItemId() == R.id.telegram) {
                    telegram();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        initiatePager();





    }

    private void initiatePager(){

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
                                searchBar.setHint("UNOFFICIAL");
                                break;
                            case R.id.action_notice:
                                viewPager.setCurrentItem(1);
                                searchBar.setHint("NOTICE BOARD");
                                break;
                            case R.id.action_notifications:
                                viewPager.setCurrentItem(2);
                                searchBar.setHint("INTERNSHIPS");
                                break;
                            case R.id.action_library:
                                viewPager.setCurrentItem(3);
                                searchBar.setHint("LIBRARY");
                                break;
                        }
                        return false;
                    }
                }
        );

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                int adjustedPosition = position < 2 ? position : position + 1;
                bottomNavigationView.getMenu().getItem(adjustedPosition).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(adjustedPosition);
                switch (position) {
                    case 0:
                        toolbar.setTitle("UNOFFICIAL");
                        break;
                    case 1:
                        toolbar.setTitle("NOTICE BOARD");
                        break;
                    case 2:
                        toolbar.setTitle("INTERNSHIPS");
                        break;
                    case 3:
                        toolbar.setTitle("LIBRARY");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PhotosFragment());
        adapter.addFragment(new BlogFragment());
        adapter.addFragment(new FragmentNotifications());
        adapter.addFragment(new FragmentLibrary());
        viewPager.setAdapter(adapter);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    protected void registerNetworkBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("android.app.action.SERVICE_DESTROYED");
        registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }
    protected void unregisteredNetwork(BroadcastReceiver broadcastReceiver){
        try {
//            unregisteredNetwork(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isNetworkRegistered){
//            unregisteredNetwork(broadcastReceiver);
//        }
        unregisteredNetwork(broadcastReceiver);
        //overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem profileMenuItem = menu.findItem(R.id.action_account);
        ImageView profileImage = (ImageView) profileMenuItem.getActionView().findViewById(R.id.profile_image);
        ImageView badge = profileMenuItem.getActionView().findViewById(R.id.badge);
        badge.setVisibility(View.GONE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String avatarUrl = preferences.getString("avatar_url", "");
        String authEmail = preferences.getString("auth_email", "");
        String authGender = preferences.getString("auth_gender", "");

        serviceCheck(MainActivity.this, new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (result){
                    if (authEmail!=null){
                        DatabaseReference avatarRef = FirebaseDatabase.getInstance().getReference("users");
                        avatarRef.orderByChild("personalEmail").equalTo(authEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()){
                                        String avatar = userSnapshot.child("avatar").getValue(String.class);
                                        if (avatar != null){
                                            Picasso.get().load(avatar).into(profileImage);
                                        }else{
                                            if (authGender.equals("Male")){
                                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").placeholder(R.drawable.round_account_circle_24).into(profileImage);
                                            }else if (authGender.equals("Female")){
                                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").placeholder(R.drawable.round_account_circle_24).into(profileImage);
                                            }else{
                                                profileImage.setImageResource(R.drawable.round_account_circle_24);
                                                badge.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("firebase error", "error");
                                if (authGender.equals("Male")){
                                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").placeholder(R.drawable.round_account_circle_24).into(profileImage);
                                }else if (authGender.equals("Female")){
                                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").placeholder(R.drawable.round_account_circle_24).into(profileImage);
                                }else{
                                    profileImage.setImageResource(R.drawable.round_account_circle_24);
                                    badge.setVisibility(View.GONE);
                                }
                            }
                        });
                    }else{
                        profileImage.setImageResource(R.drawable.round_account_circle_24);
                        badge.setVisibility(View.GONE);
                    }
                }else{
                    profileImage.setImageResource(R.drawable.round_account_circle_24);
                    badge.setVisibility(View.GONE);
                }
            }
        });


        if (authEmail.equals("om.lokhande34@gmail.com")){
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg").placeholder(R.drawable.round_account_circle_24).into(profileImage);
            badge.setVisibility(View.VISIBLE);
        }else if(authEmail.equals("aishwaryakumbhar1234@gmail.com")){
            badge.setVisibility(View.VISIBLE);
        }else if(authEmail.equals("mechanical.official73@gmail.com")){
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/unofficial.png").placeholder(R.drawable.round_account_circle_24).into(profileImage);
            badge.setVisibility(View.VISIBLE);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account();
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account:
                account();
//                Intent intent = new Intent(MainActivity.this, AccountInfo.class);
//                startActivity(intent);
                return true;
            case R.id.upload:
                PopupMenu popupMenu = new PopupMenu(this, findViewById(item.getItemId()));
                popupMenu.getMenuInflater().inflate(R.menu.upload_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(item.getItemId()){
                            case R.id.action_post:
                                Toast.makeText(MainActivity.this, "New post created!", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.action_notice:
                                Toast.makeText(MainActivity.this, "New notice created!", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.action_memories:
                                Toast.makeText(MainActivity.this, "New memory created!", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showRememberDialog(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        value = preferences.getInt("remember", 0);

        if (value == null || value == 0){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle("Remember");
            builder.setMessage(R.string.remember);
            builder.setIcon(R.drawable.round_bolt_24);
            builder.setCancelable(false);
            builder.setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    value = 1;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("remember", value);
                    editor.apply();
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

    }

    private void showTermsAndConditions(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        value = preferences.getInt("TC", 0);

        if (value == null || value == 0){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle("Terms and Conditions");
            builder.setMessage(R.string.termNconditions);
            builder.setIcon(R.drawable.round_supervisor_account_24);
            builder.setCancelable(false);
            builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    value = 1;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("TC", value);
                    editor.apply();
                }
            });
            builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finishAffinity();
                }
            });
            builder.show();
        }

    }
    private void checkForUpdates(){

        FirebaseDatabase versionDatabase = FirebaseDatabase.getInstance();
        DatabaseReference versionRef = versionDatabase.getReference("app-configuration/version");

        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String latestVersion = snapshot.getValue(String.class);
                String currentVersion = BuildConfig.VERSION_NAME;

                if(latestVersion != null && !latestVersion.equals(currentVersion)){
                    runOnUiThread(() -> customDialog("Update Available", "A new version of the app is available. Please update to continue using the app.", "Update"));
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "An error occured while checking for updates", Toast.LENGTH_SHORT).show());
            }
        });

    }

    private boolean dialogShown = false;
    private void customDialog(String title, String msg, String positiveBtn) {

        if (!dialogShown) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//        String apkUrl = "https://rubyproducti9n.github.io/mech/app-release/app-debug.apk";
                String apkUrl = "https://rubyproducti9n.github.io/concertera/Equalizer_base.apk";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    downloadAndInstallApk(apkUrl);
                    autoUpdate();
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
            dialogShown = true;
        }
    }

    private void autoUpdate(){
        deleteApkFiles(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Updates/")));
        DownloadApk.startDownloadingApk(MainActivity.this, "https://github.com/rubyproducti9n/group-3/raw/main/Unofficial%20Mech/updates/update_package.apk", "update_package.apk");
    }

    private void deleteApkFiles(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            Log.w("DeleteApkFiles", "Directory does not exist: " + directoryPath);
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".apk")) {
                    if (file.delete()) {
                        Log.i("DeleteApkFiles", "Deleted APK file: " + file.getName());
                    } else {
                        Log.w("DeleteApkFiles", "Failed to delete APK file: " + file.getName());
                    }
                }
            }
        } else {
            Log.w("DeleteApkFiles", "Failed to list files in directory: " + directoryPath);
        }
    }

    private void initiateAd(){
        startActivity(new Intent(MainActivity.this, MyAds.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void downloadAndInstallApk(String apkUrl) {
        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();

        //Custom dir
        String downloadPath = getExternalFilesDir(null) + "/Updates/";
        File downloadDir = new File(downloadPath);

        String apkName = "app_debug.apk";
        String apkFilePath = downloadPath + apkName;

        if (!downloadDir.exists()){
            downloadDir.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Unofficial Mech");
        request.setDescription("Downloading latest version");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app_update.apk");
        request.setDestinationUri(Uri.fromFile(new File(apkFilePath)));

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    installApk(context, downloadId);
                    unregisterReceiver(this);

                    //Delete the file when installed
                    File apkFile = new File(apkFilePath);
                    if (apkFile.exists() && isApkInstalled(MainActivity.this, apkFilePath)){
                        apkFile.delete();
                    }
                }else{
                    Toast.makeText(context, "Error installing file", Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
    }

    public boolean isApkInstalled(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void installApk(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri apkUri = downloadManager.getUriForDownloadedFile(downloadId);
//        ActivityResultLauncher<Intent> packageInstallerLauncher;
//        Intent installIntent;

        if (apkUri != null) {
            String apkFilePath = apkUri.getPath();
            //Toast.makeText(context, apkFilePath, Toast.LENGTH_SHORT).show();

            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ActivityResultLauncher<Intent> packageInstallerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                    if (result.getResultCode() == Activity.RESULT_OK){
                        context.startActivity(installIntent);
                    }else{
                        Toast.makeText(context, "Permission denied, cannot install app", Toast.LENGTH_SHORT).show();
                    }
                });

                if (getPackageManager().canRequestPackageInstalls()){
                    //If permission is already granted with installation
                    context.startActivity(installIntent);
                }else{
                    //Request installation permission from the user
                    packageInstallerLauncher.launch(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName())));
                }

                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(Objects.requireNonNull(apkUri.getPath())));
                installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }else {
                //For older Android versions, installation dialog will show automatically
                context.startActivity(installIntent);
            }
        } else {
            Toast.makeText(context, "Installation failed", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(downloadId != -1){
                //Installing the apk
                installApk(context, downloadId);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void authUser(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Use below method to retrieve user data in other activities
        String authName = preferences.getString("auth_name", null);
        String authEmail = preferences.getString("auth_email", null);
        String authPrn = preferences.getString("auth_prn", null);
        String authUserRole = preferences.getString("auth_userole", null);

        if (authName == null){
            //Toast.makeText(this, "You were logged out", Toast.LENGTH_SHORT).show();
            //loginActivity();
        }
    }

    public void loginActivity(){
        Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
    public void account(){
        Intent intent = new Intent(MainActivity.this, AccountInfo.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
    public void settings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
    public void career(){
        String url = "https://forms.gle/aEmMbub1GRnSD3zb6";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void feedback(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
        intent.setPackage("com.android.vending");
        startActivity(intent);
    }
    public void about(){
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }
    public void whatsapp(){
        String url = "https://chat.whatsapp.com/CAEQCEmA6cx6egZtNsQN8J";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void instagram(){
        String url = "https://instagram.com/unofficial.mech?igshid=YmMyMTA2M2Y=";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void youtube(){
        String url = "https://youtube.com/@unofficialmech";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void telegram(){
        String url = "https://t.me/+dRJBOb3AVhdlNmFl";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void report(){
        String url = "https://forms.gle/Ssj2owNYxWtfEgFG8";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }


    public void requestInAppReview(Activity activity) {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();

                launchReviewFlow(activity, reviewInfo);
            } else {
                Log.e(TAG, "There was no review info available to show.");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }
        });
    }

    private void launchReviewFlow(Activity activity, ReviewInfo reviewInfo) {
        Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
        flow.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "In-app review flow completed.");
            } else {
                Log.e(TAG, "There was an error launching the in-app review flow.", task.getException());
                Log.e(TAG, "There was no review info available to show.");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }
        });
    }

    private void loadUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String dept = snapshot.child("dept").getValue(String.class);

                    SearchAdapter.SearchItem i = new SearchAdapter.SearchItem(firstName + " " + lastName, dept);
                    userList.add(i);
                }
                Collections.shuffle(userList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading users: " + databaseError.getMessage());
            }
        });
    }

    private void goPremium(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        value = preferences.getInt("TC", 0);
        if (value!=0){
            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this)
                    .setTitle("Go Premium")
                    .setMessage("Tired of ads? Upgrade to Premium and enjoy an uninterrupted experience.")
                    .setPositiveButton("See plans", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
                        }
                    })
                    .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            if (show(0.90)){
                b.show();
            }
        }
    }

    private boolean show(double rate){
        Random random = new Random();
        return random.nextDouble() < rate;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}