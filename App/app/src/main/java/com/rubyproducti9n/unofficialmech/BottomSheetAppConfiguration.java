package com.rubyproducti9n.unofficialmech;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetAppConfiguration#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetAppConfiguration extends BottomSheetProfileEdit {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataAdValueRef = database.getReference("app-configuration/ad_value");
    DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
    DatabaseReference dataFutureRef = database.getReference("app-configuration/forceUpdate");
    DatabaseReference dataLockdownRef = database.getReference("app-configuration/lockdown");
    DatabaseReference dataNotificationRef = database.getReference("app-configuration/notifications");
    DatabaseReference dataPaymentRef = database.getReference("app-configuration/payments");

    MaterialSwitch lockdownSwitch, uploadSwitch, futureUpdateSwitch, notificationSwitch, adSwitch, paymentSwitch;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomSheetAppConfiguration() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetAppConfiguration.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetAppConfiguration newInstance(String param1, String param2) {
        BottomSheetAppConfiguration fragment = new BottomSheetAppConfiguration();
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
        View view = inflater.inflate(R.layout.bottom_sheet_app_configuration, container, false);

        retrieveRealtimeDatabase(view);

        adSwitch = view.findViewById(R.id.adSwitch);
        adSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataAdValueRef.setValue(b);
            }
        });

        lockdownSwitch = view.findViewById(R.id.lockdownSwitch);
        lockdownSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataLockdownRef.setValue(b);
            }
        });

        uploadSwitch = view.findViewById(R.id.uploadSwitch);
        uploadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataUploadRef.setValue(isChecked);
            }
        });

        futureUpdateSwitch = view.findViewById(R.id.futureSwitch);
        futureUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataFutureRef.setValue(isChecked);
            }
        });

        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataNotificationRef.setValue(isChecked);
            }
        });

        paymentSwitch = view.findViewById(R.id.paymentSwitch);
        paymentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataPaymentRef.setValue(isChecked);
            }
        });


        return view;
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

        dataNotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean NotificationsValue = snapshot.getValue(Boolean.class);

                if(NotificationsValue != null){
                    notificationSwitch.setChecked(NotificationsValue);
                    progressIndicator.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                defaultDialog();
            }
        });

        dataPaymentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean NotificationsValue = snapshot.getValue(Boolean.class);

                if(NotificationsValue != null){
                    paymentSwitch.setChecked(NotificationsValue);
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

        dataAdValueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean adValue = snapshot.getValue(Boolean.class);
                adSwitch.setChecked(Boolean.TRUE.equals(adValue));
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