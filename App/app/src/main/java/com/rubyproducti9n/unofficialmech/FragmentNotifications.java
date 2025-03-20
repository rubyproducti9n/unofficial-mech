package com.rubyproducti9n.unofficialmech;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentNotifications#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNotifications extends Fragment {

    private InternshipNotificationAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ConstraintLayout disabledLayout, enabledLayout;
    MaterialButton enableNotificationButton;

    public FragmentNotifications() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNotifications.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNotifications newInstance(String param1, String param2) {
        FragmentNotifications fragment = new FragmentNotifications();
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

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        disabledLayout = view.findViewById(R.id.notification_setting_disabled_layout);
        enabledLayout = view.findViewById(R.id.notification_setting_enabled_layout);
        enableNotificationButton = view.findViewById(R.id.enableNotificationButton);

        //Function calls
        updateNotificationButtonVisibility();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postRef = database.getReference("internships");

        LinearProgressIndicator progressBar = view.findViewById(R.id.progressBar);
//        ProjectToolkit.fadeIn(progressBar);

        ManagerLayout manager = view.findViewById(R.id.manager);
        manager.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        ProjectToolkit.fadeOut(recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String userRole = preferences.getString("auth_userole", null);


        RecyclerView panIndiaRecyclerView = view.findViewById(R.id.panIndiaInternshipsRecyclerView);
        RecyclerView internationalRecyclerView = view.findViewById(R.id.internationalInternshipsRecyclerView);

        InternshipNotificationAdapter panIndiaAdapter = new InternshipNotificationAdapter(requireContext(), InternshipData.getPanIndiaInternships());
        InternshipNotificationAdapter internationalAdapter = new InternshipNotificationAdapter(requireContext(), InternshipData.getInternationalInternships());


        panIndiaRecyclerView.setAdapter(panIndiaAdapter);
        internationalRecyclerView.setAdapter(internationalAdapter);

        panIndiaAdapter.notifyDataSetChanged();
        internationalAdapter.notifyDataSetChanged();

        panIndiaRecyclerView.setVisibility(View.VISIBLE);
        panIndiaRecyclerView.setLayoutManager(layoutManager);
        internationalRecyclerView.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager1 = (GridLayoutManager) new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        internationalRecyclerView.setLayoutManager(layoutManager1);


        // Inflate the layout for this fragment
        return view;
    }

    private void updateNotificationButtonVisibility() {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();

        if (areNotificationsEnabled) {
            disabledLayout.setVisibility(View.GONE);
            enabledLayout.setVisibility(View.VISIBLE);
        } else {
            disabledLayout.setVisibility(View.VISIBLE);
            enabledLayout.setVisibility(View.GONE);
            enableNotificationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
                    startActivity(intent);
                }
            });
        }
    }

    private void initiateInternships() {

        initiatePanIndiaInter();
        initiateInternationalInter();

    }

    private void initiatePanIndiaInter(){
        Map<String, String> intenrships = new HashMap<>();

    }

    private void initiateInternationalInter(){

    }


    @Override
    public void onResume() {
        super.onResume();
        updateNotificationButtonVisibility();
    }
}



