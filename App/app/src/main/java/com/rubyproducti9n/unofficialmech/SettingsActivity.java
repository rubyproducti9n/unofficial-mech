package com.rubyproducti9n.unofficialmech;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;


public class SettingsActivity extends BaseActivity {

    TextView versionTxt;
    MaterialSwitch notificationSwitch, albumShuffleSwitch, dashboardSwitch, themeSwitch;
    String version;
    private int selectedModel;
    private static final String KEY_SELECTED_MODEL = "selected_model";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setAllowEnterTransitionOverlap(true);

        // Initialize SharedPreferences
        SharedPreferences modelPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

        // Get stored value or default to 0
        selectedModel = modelPref.getInt(KEY_SELECTED_MODEL, 0);

        // Find the dropdown menu
        AutoCompleteTextView dropdownMenu = findViewById(R.id.dropdown_menu);

        // Get string array from resources
        String[] items = getResources().getStringArray(R.array.dropdown_items);

        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);

        // Set adapter to AutoCompleteTextView
        dropdownMenu.setAdapter(adapter);

        // Set default selected item in dropdown
        dropdownMenu.setText(items[selectedModel], false);

        // Handle user selection
        dropdownMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedModel = position; // Store selected index (0, 1, or 2)

                SharedPreferences.Editor editor = modelPref.edit();
                editor.putInt(KEY_SELECTED_MODEL, position);
                editor.apply(); // Save to SharedPreferences
            }
        });

        MaterialCardView uploadQualityCrdView = findViewById(R.id.uploadQuality);
        uploadQualityCrdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                int storedValue = preferences.getInt("quality_setting", -1);

                RadioGroup radioGroup = new RadioGroup(SettingsActivity.this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);

                String[] options = {"Best quality"};
                for (int i = 0; i<options.length; i++){
                    RadioButton radioButton = new RadioButton(SettingsActivity.this);
                    radioButton.setText(options[i]);
                    radioButton.setId(i);
                    radioButton.setChecked(true);

                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.MATCH_PARENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                    );
                    int marginInDp = 8;
                    int marginInPx =  (int) (marginInDp* getResources().getDisplayMetrics().density);

                    layoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
                    radioButton.setLayoutParams(layoutParams);

                    radioButton.setPadding(28, 28, 28,28);
                    radioGroup.addView(radioButton);
                }

                if(storedValue>=0 && storedValue<radioGroup.getChildCount()){
                    radioGroup.check(storedValue);
                }

                MaterialAlertDialogBuilder updateDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                updateDialog.setTitle("Upload quality");
                //updateDialog.setMessage("Best quality: Photos are larger and can take longer to upload");
//                updateDialog.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
                updateDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedRadioButton = radioGroup.getCheckedRadioButtonId();
                        if(selectedRadioButton != -1){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("quality_setting", selectedRadioButton);
                            editor.apply();
                            dialogInterface.dismiss();
                        }
                    }
                });
                updateDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                updateDialog.setView(radioGroup);
                updateDialog.show();
            }
        });

//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Functions.show(SettingsActivity.this, "Select", new String[]{"Option 1", "Low quality"}, new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                        Toast.makeText(SettingsActivity.this, "Option selected", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

        getServerStatus();

        dashboardSwitch = findViewById(R.id.dashboardSwitch);
        themeSwitch = findViewById(R.id.themeSwitch);
        SharedPreferences pref0 = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dash = pref0.getBoolean("dashboard", false);
        boolean theme = pref0.getBoolean("theme", true);
        String userId = pref0.getString("auth_userId", null);

        themeSwitch.setChecked(theme);
        if (userId!=null){
            themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = pref0.edit();
                    editor.putBoolean("theme", isChecked);
                    editor.apply();
                    themeSwitch.setChecked(isChecked);
                }
            });
        }else{
            themeSwitch.setEnabled(false);
            themeSwitch.setChecked(true);
        }

        dashboardSwitch.setChecked(dash);
        if (userId!=null){
            dashboardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = pref0.edit();
                    editor.putBoolean("dashboard", isChecked);
                    editor.apply();
                    dashboardSwitch.setChecked(isChecked);
                }
            });
        }else{
            dashboardSwitch.setChecked(false);
            dashboardSwitch.setEnabled(false);
        }

        albumShuffleSwitch = findViewById(R.id.albumShuffleSwitch);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shuffle = pref.getBoolean("shuffle", true);
        albumShuffleSwitch.setChecked(shuffle);
        albumShuffleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("shuffle", isChecked);
                editor.apply();
                albumShuffleSwitch.setChecked(isChecked);
            }
        });


        notificationSwitch = findViewById(R.id.notificationSwitch);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        boolean areNotificationsEnabled;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            areNotificationsEnabled = notificationManager.getNotificationChannel("Service") != null;
        } else {
            areNotificationsEnabled = notificationManager.areNotificationsEnabled();
        }
        notificationSwitch.setChecked(areNotificationsEnabled);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // When switch is turned ON
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Redirect to system notification settings for Oreo+
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);
                    } else {
                        // Create notification channel for pre-Oreo versions
                        createNotificationChannel();
                    }
                } else {
                    // When switch is turned OFF
                    // (Keep existing functionality - cannot disable programmatically)
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                    Toast.makeText(SettingsActivity.this, "Turn off the switch manually", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MaterialCardView autoUpdateCard = findViewById(R.id.autoUpdate);
        autoUpdateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder updateDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                updateDialog.setTitle("Update mode");
                updateDialog.setMessage("Automatic: Downloads the latest version of the app and install automatically");
                updateDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                updateDialog.show();
            }
        });

        MaterialCardView serverStatusCard = findViewById(R.id.serverStatusCard);
        serverStatusCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder updateDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                updateDialog.setTitle("Server status");
                updateDialog.setMessage("Online: All features are available  \n \nOffline: Some feature might be unavailable");
                updateDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                updateDialog.show();
            }
        });


        versionTxt = findViewById(R.id.version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            versionTxt.setText(version);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        MaterialCardView versionCard = findViewById(R.id.versionCard);
        versionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder updateDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                updateDialog.setTitle("Version");
                updateDialog.setMessage( "Current version: " + version + "\n \n" + getString(R.string.changeLog));
                updateDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                updateDialog.setPositiveButton("Check for updates", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkForUpdates();
                    }
                });
                updateDialog.show();
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String adminEmailCheck = preferences.getString("admin_email", "");
        String betaEmailCheck = preferences.getString("beta_email", "");
        String authEmailCheck = preferences.getString("auth_email", "");

        //Use below method to retrieve user data in other activities
        String authName = preferences.getString("auth_name", "User not found");
        String authEmail = preferences.getString("auth_email", "");
        String authPrn = preferences.getString("auth_prn", "");
        String authUserRole = preferences.getString("auth_userole", "");


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_report:
                report();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getServerStatus(){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("app-configuration/uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean serverStatus = snapshot.getValue(Boolean.class);
                TextView txtStatus = findViewById(R.id.serverStatus);

                if (serverStatus == true){
                    txtStatus.setText("Online");
                }else{
                    txtStatus.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("new", "name it", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Enabled");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }else{
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
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
                    //performing actions for updateing the app
                    customDialog("Update Available", "A new version of the app is available. Please update to continue using the app.", "Update");
                }else{
                    Toast.makeText(SettingsActivity.this, "The current version is up to date", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Handle the errors
                Toast.makeText(SettingsActivity.this, "An error occurred while checking for updates", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean dialogShown = false;
    private void customDialog(String title, String msg, String positiveBtn) {

        if (!dialogShown) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String apkUrl = "https://rubyproducti9n.github.io/mech/app-release/app-debug.apk";
                    ProjectToolkit.autoUpdate(SettingsActivity.this, apkUrl);
                    //downloadAndInstallApk(apkUrl);
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
            dialogShown = true;
        }
    }
    private void downloadAndInstallApk(String apkUrl) {
        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Unofficial Mech");
        request.setDescription("Downloading latest version");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app_update.apk");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    installApk(context, downloadId);
                    unregisterReceiver(this);
                }else{
                    Toast.makeText(context, "Error installing file", Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onResume() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        boolean areNotificationsEnabled;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            areNotificationsEnabled = notificationManager.getNotificationChannel("Service") != null;
        } else {
            areNotificationsEnabled = notificationManager.areNotificationsEnabled();
        }
        notificationSwitch.setChecked(areNotificationsEnabled);
        super.onResume();
    }

    private void installApk(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri apkUri = downloadManager.getUriForDownloadedFile(downloadId);

        if (apkUri != null) {
            String apkFilePath = apkUri.getPath();
            Toast.makeText(context, apkFilePath, Toast.LENGTH_SHORT).show();

            Intent installIntent = new Intent(Intent.ACTION_VIEW);
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
                    Toast.makeText(context, "Installed Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    //Request installation permission from the user
                    packageInstallerLauncher.launch(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName())));
                }

                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(apkUri.getPath()));
                installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }else {
                //For older Android versions, installation dialog will show automatically
                context.startActivity(installIntent);
                Toast.makeText(context, "Installed Successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Installation failed", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(downloadId != -1){
                //Instaling the apk
                installApk(context, downloadId);
            }

        }
    };


    public void report(){
        String url = "https://forms.gle/Ssj2owNYxWtfEgFG8";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void suggestFeedback(){
        String url = "https://forms.gle/vj47o28p7dvaB2gY9";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
}