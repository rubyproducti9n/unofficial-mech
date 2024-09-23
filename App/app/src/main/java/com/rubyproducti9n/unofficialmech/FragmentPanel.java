package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPanel#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPanel extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    Context mcontext;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    private static DeveloperActivity instance;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
    DatabaseReference dataFutureRef = database.getReference("app-configuration/forceUpdate");
    DatabaseReference dataLockdownRef = database.getReference("app-configuration/lockdown");

    MaterialSwitch lockdownSwitch, uploadSwitch, futureUpdateSwitch, paymentSwitch;
    private static final String DARK_MODE_KEY = "DarkMode";
    SharedPreferences sharedPreferences;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentPanel() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPanel.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPanel newInstance(String param1, String param2) {
        FragmentPanel fragment = new FragmentPanel();
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
        View view = inflater.inflate(R.layout.fragment_panel, container, false);

        MaterialCardView appConfiguration = view.findViewById(R.id.appConfiguration);
        appConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetAppConfiguration bottomSheetFragment = new BottomSheetAppConfiguration();
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), "BottomSheet");
            }
        });

//        retrieveRealtimeDatabase(view);
//
//        lockdownSwitch = view.findViewById(R.id.lockdownSwitch);
//        lockdownSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                dataLockdownRef.setValue(b);
//            }
//        });
//
//        uploadSwitch = view.findViewById(R.id.uploadSwitch);
//        uploadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                dataUploadRef.setValue(isChecked);
//            }
//        });
//
//        futureUpdateSwitch = view.findViewById(R.id.futureSwitch);
//        futureUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                dataFutureRef.setValue(isChecked);
//            }
//        });

//        darkSwitch = findViewById(R.id.darkSwitch);
//        darkSwitch.setEnabled(false);
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DeveloperActivity.this);
//        darkSwitch.setChecked(sharedPreferences.getBoolean(DARK_MODE_KEY, false));
//        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor darkPref = sharedPreferences.edit();
//                darkPref.putBoolean(DARK_MODE_KEY, isChecked);
//                darkPref.apply();
//
//                applyTheme(isChecked);
//            }
//        });
//        applyTheme(darkSwitch.isChecked());

        MaterialCardView autoMail = view.findViewById(R.id.mailConfiguration);
        autoMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> emailList= new ArrayList<>();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot emailSnapshot : snapshot.getChildren()){
                            String email = emailSnapshot.getValue(String.class);
                            if (email!= null && !email.isEmpty()){
                                emailList.add(email);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                String[] recipients = emailList.toArray(new String[0]);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                intent.setDataAndType(Uri.parse("email"), "message/rfc822");
                intent.setData(Uri.parse("mailto:"));
                String[] email = {"om.lokhande34@gmail.com", "aishwaryakumbharmech@sanjivanicoe.org.in"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject goes here");
                intent.putExtra(Intent.EXTRA_TEXT, "Text goes here");
                requireContext().startActivity(Intent.createChooser(intent, "Launch Email"));
            }
        });

        MaterialCardView notify = view.findViewById(R.id.notification_beta);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(mcontext, "Allow all permission to let app work with all functionality", Toast.LENGTH_SHORT).show();
                    openAppSettings();
                }else{
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "default")
                            .setSmallIcon(R.drawable.notification_img)
                            .setContentTitle("Test Notification")
                            .setContentText("This is a test notification message")
                            .setPriority(NotificationCompat.PRIORITY_MAX);
                    notificationManagerCompat.notify(0, builder.build());
                }
                return;
            }
        });


        return view;
    }

    public static void applyTheme(boolean isDarkMode){
        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private void openAppSettings(){

        ActivityResultLauncher<Intent> startActivityForResult = null;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult.launch(intent);
    }

    private void retrieveRealtimeDatabase(View view){

        LinearProgressIndicator progressIndicator = view.findViewById(R.id.progress);
        ConstraintLayout mainLayout = view.findViewById(R.id.main_layout);

        //This is set to only one switch that is Future Update due to some limitations
        progressIndicator.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);

        dataLockdownRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean lockdownValue = snapshot.getValue(Boolean.class);
                lockdownSwitch.setChecked(lockdownValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                defaultDialog();
            }
        });

        dataFutureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean futureUpdateValue = snapshot.getValue(Boolean.class);

                if(futureUpdateValue != null){
                    futureUpdateSwitch.setChecked(futureUpdateValue);
                    progressIndicator.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
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
                    uploadSwitch.setChecked(uploadsValue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                defaultDialog();
            }
        });

    }

    public void defaultDialog(){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
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
}