package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getServerUrl;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isAdmin;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class DeveloperActivity extends BaseActivity {
    Context mcontext;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    private int progressStatus = 0;

    ConstraintLayout dev;
    MaterialToolbar toolbar;
    BottomNavigationView bottomNavigationView;

    private static DeveloperActivity instance;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
    DatabaseReference dataFutureRef = database.getReference("app-configuration/forceUpdate");

//    MaterialSwitch uploadSwitch, futureUpdateSwitch, darkSwitch;
    private static final String DARK_MODE_KEY = "DarkMode";
    SharedPreferences sharedPreferences;


    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

// Initialize the BillingClient
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        // Connect to Google Play Billing
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing service connected, query purchases
                    queryCurrentSubscription();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Handle disconnect
                Toast.makeText(DeveloperActivity.this, "Billing service disconnected", Toast.LENGTH_SHORT).show();
            }
        });

        AdManager manager = new AdManager(this);
        manager.loadInterstitialAd(this);

        BillingHelper helper = new BillingHelper(this);

        if (helper.isPlanActive("basic_plan")){
            Toast.makeText(this, "Basic plan!", Toast.LENGTH_SHORT).show();
        }

        dev = findViewById(R.id.dev);
        dev.setVisibility(View.GONE);
        instance = this;
        FloatingActionButton fab = findViewById(R.id.server);
        String response = sendHttpRequest("http://192.168.23.78/dashboard/");

        if (pref(DeveloperActivity.this).getString("auth_userId", null) !=null){
            if (isAdmin(DeveloperActivity.this)){
                Handler handler = new Handler();
                LinearProgressIndicator progressInd = findViewById(R.id.progress);
                progressInd.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(progressStatus < 100){
                            progressStatus += 1;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressInd.setProgress(progressStatus, true);
                                }
                            });
                            try {
                                Thread.sleep(15);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ProjectToolkit.fadeOut(progressInd);
                                ProjectToolkit.fadeIn(dev);
                            }
                        }, 500);
                    }
                }).start();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateDialog();
                    }
                });

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String role = sharedPreferences.getString("auth_userole", null);
                if (role==null && !role.equals("Admin")){
                    Toast.makeText(mcontext, "Unauthorized access! This action is not allowed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(mcontext, "Reinitializing....", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DeveloperActivity.this, MainActivity.class));
                }else{
                    retrieveRealtimeDatabase();
                }

                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                drawerLayout = findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);
                drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
                drawerLayout.addDrawerListener(drawerToggle);
                drawerToggle.syncState();
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        if(item.getItemId() == R.id.nav_album){
                            Intent intent = new Intent(DeveloperActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else if (item.getItemId() == R.id.nav_about) {
                            Toast.makeText(DeveloperActivity.this, "Indevelopment", Toast.LENGTH_SHORT).show();
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });

                bottomNavigationView = findViewById(R.id.bottom_navigation);
                replaceFragment(new FragmentAccountsDev());
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    switch(item.getItemId()){
                        case R.id.action_accounts:
                            replaceFragment(new FragmentAccountsDev());
                            break;
                        case R.id.action_posts:
                            replaceFragment(new FragmentPostsDev());
                            break;
                        case R.id.action_panel:
                            replaceFragment(new FragmentPanel());
                            break;
                    }
                    return true;
                });
            }



        }
        else{
            dev.setVisibility(View.GONE);
            startActivity(new Intent(DeveloperActivity.this, PasswordActivity.class));
            Toast.makeText(DeveloperActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();
            saveUserInfo();
            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this);
            b.setTitle("Security Alert")
                    .setMessage("Our system detected an Unauthorized activity. Revoking all the access \n\n Your access will be revoked from the app as our system detected an security breach. Don't worry you can still login to your existing but with some restrictions.")
                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MaterialAlertDialogBuilder w = new MaterialAlertDialogBuilder(DeveloperActivity.this);
                            w.setTitle("WARNING!")
                                    .setMessage("Breaching our security rules will result in account suspension. Please familiarize yourself with our guidelines to keep your account safe")
                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            w.show();
                        }
                    });
            b.show();
        }




    }
    private void queryCurrentSubscription() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (!list.isEmpty()) {
                        // User has an active subscription
                        for (Purchase purchase : list) {
                            // Check the purchase SKU and show appropriate Toast
                            String sku = purchase.getProducts().toString();
                            if (sku.equals("basic_plan_sku")) {
                                showToast("You have the Basic Plan");
                            } else if (sku.equals("standard_plan_sku")) {
                                showToast("You have the Standard Plan");
                            } else if (sku.equals("premium_plan_sku")) {
                                showToast("You have the Premium Plan");
                            }
                        }
                    } else {
                        // No active subscriptions
                        showToast("No active subscriptions");
                    }
                } else {
                    showToast("Failed to retrieve subscription info");
                }
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
            // Handle purchase updates if needed
        }
    };
    private void saveUserInfo(){
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("breach");
        String bid = r.push().getKey();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(formatter);
        r.child(bid).setValue(getUserInfo() + " breached security at " + formattedTime);
    }

    private String getUserInfo(){
        String uid = pref(DeveloperActivity.this).getString("auth_userId", null);
        String n = pref(DeveloperActivity.this).getString("auth_name", null);
        String e = pref(DeveloperActivity.this).getString("auth_personalEmail", null);
        String m = pref(DeveloperActivity.this).getString("auth_mNum", null);
        return "User: " + n + " with UID: " + uid + " Email: " + e + " Contact: " + m;
    }

    private void initiateDialog(){
        String[] items = {
                "Unofficial Server",
        "Apply for Position", "FAQs", "Virtual ID", "Subscription", "Blueprint", "Study Material"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent(DeveloperActivity.this, WebViewActivity.class);
                        intent.putExtra("link", getServerUrl());
//                        intent.putExtra("link", "https://google.com");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(DeveloperActivity.this, RolesApplicationActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(DeveloperActivity.this, FAQActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(DeveloperActivity.this, VirtualIDCardActivity.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent(DeveloperActivity.this, PaymentActivity.class);
                        startActivity(intent4);
                        break;
                    case 5:
                        Intent intent5 = new Intent(DeveloperActivity.this, BlueprintActivity.class);
                        startActivity(intent5);
                        break;
                    case 6:
                        Intent intent6 = new Intent(DeveloperActivity.this, StudyMaterialActivity.class);
                        startActivity(intent6);
                        break;
                }
            }
        });
        builder.show();
    }
    public static String sendHttpRequest(String urlString) {
        try {
            URL url = new URL(urlString); // Replace with your XAMPP server URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Change to "POST" if sending data

            // Optional: Add request headers if needed (e.g., for authentication)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                return response.toString();
            } else {
                Log.e("Unofficial Mech", "Error: HTTP request failed with code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e("Unofficial Mech", "Error: " + e.getMessage());
            return null;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public static void applyTheme(boolean isDarkMode){
        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void showPermissionDenied(){
        MaterialAlertDialogBuilder materialAlert = new MaterialAlertDialogBuilder(this);
        materialAlert.setTitle("Warning");
        materialAlert.setMessage("This app requires media permission to function properly, Please the grant permission from the app settings.");
        materialAlert.setPositiveButton("App settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
        materialAlert.setCancelable(false);
        materialAlert.show();
    }

    private void openAppSettings(){

        ActivityResultLauncher<Intent> startActivityForResult = null;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult.launch(intent);
    }

    public static DeveloperActivity getInstance(){
        return instance;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        SharedPreferences sharedPreferences = getSharedPreferences("my_switch_state", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("switch_state", switchState);
//        editor.apply();
    }

    public void getServerStatus(){
//        FirebaseRemoteConfig firebaseRemoteConfig;
//        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setMinimumFetchIntervalInSeconds(0) //Set it to 3600 to fetch data every hour
//                .build();
//        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
//
//        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
//            @Override
//            public void onComplete(@NonNull Task<Boolean> task) {
//                if(task.isSuccessful()){
//                    boolean updated = task.getResult();
//                    Log.d("Developer Activity", "Config fetched and activated: " + updated);
//                    int serverStatus = (int) firebaseRemoteConfig.getLong("server_status");
//
//                    TextView txtStatus = findViewById(R.id.serverStatus);
//
//                    if (serverStatus == 1){
//                        txtStatus.setText("Online");
//                    }else{
//                        txtStatus.setText("Offline");
//                    }
//                }
//                else{
//                    Log.d("Developer Activity", "Error fetching config: " + task.getException());
//                }
//            }
//        });
    }
    private void retrieveRealtimeDatabase(){

        LinearProgressIndicator progressIndicator = findViewById(R.id.progress);
        //ConstraintLayout mainLayout = findViewById(R.id.main_layout);

        //This is set to only one switch that is Future Update due to some limitations
//        progressIndicator.setVisibility(View.VISIBLE);
        //mainLayout.setVisibility(View.GONE);

        dataFutureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean futureUpdateValue = snapshot.getValue(Boolean.class);

                if(futureUpdateValue != null){
                    //futureUpdateSwitch.setChecked(futureUpdateValue);
//                    progressIndicator.setVisibility(View.GONE);
                    //mainLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                defaultDialog();
            }
        });
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue != null){
                    //uploadSwitch.setChecked(uploadsValue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                defaultDialog();
            }
        });

    }

    public void defaultDialog(){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DeveloperActivity.this);
        builder.setTitle("Error!");
        builder.setMessage("Oops! something went wrong, please try again later.");
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dev_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_appStore:
                Intent intentAppStore = new Intent(this, AppStoreActivity.class);
                startActivity(intentAppStore);
                return true;
            case R.id.activity_beta:
                Intent intent1 = new Intent(this, BetaActivity.class);
                startActivity(intent1);
                return true;
            case R.id.activity_uploads:
                Intent intent6 = new Intent(this, UploadsActivity.class);
                startActivity(intent6);
                return true;
            case R.id.activity_access_denied:
                Intent intent2 = new Intent(this, AccessDeniedActivity.class);
                startActivity(intent2);
                return true;
            case R.id.activity_password:
                Intent intent3 = new Intent(this, PasswordActivity.class);
                startActivity(intent3);
                return true;
            case R.id.activity_splash:
                Intent intent4 = new Intent(this, SplashActivity.class);
                startActivity(intent4);
                return true;
            case R.id.activity_ai:
                Intent intent5 = new Intent(this, ArtificialIntelligenceActivity.class);
                startActivity(intent5);
                return true;
            case R.id.activity_event:
                Intent intent8 = new Intent(this, EventScheduleActivity.class);
                startActivity(intent8);
                return true;
            case R.id.activity_aitool:
                Intent intent7 = new Intent(this, AIToolsActivity.class);
                startActivity(intent7);
                return true;
            case R.id.activity_video:
                Intent intent11 = new Intent(this, VideosActivity.class);
                startActivity(intent11);
                return true;
            case R.id.activity_test:
                Intent intent9 = new Intent(this, TestActivity.class);
                startActivity(intent9);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
