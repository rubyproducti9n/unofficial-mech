package com.rubyproducti9n.unofficialmech;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetUploadOptions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetUploadOptions extends BottomSheetProfileEdit {

    MaterialCardView post, notice, memory, project ,internship, letter, eventScheduler;
    ConstraintLayout mainLayout, noUserLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomSheetUploadOptions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetUploadOptions.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetUploadOptions newInstance(String param1, String param2) {
        BottomSheetUploadOptions fragment = new BottomSheetUploadOptions();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_upload_options, container, false);

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.CustomBottomSheetAnim);

        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mainLayout = view.findViewById(R.id.main_layout);
        noUserLayout = view.findViewById(R.id.no_user_layout);

        post = view.findViewById(R.id.new_post);
        notice = view.findViewById(R.id.new_notice);
        memory = view.findViewById(R.id.new_memory);
        project = view.findViewById(R.id.new_project);
        internship = view.findViewById(R.id.new_internship);
        letter = view.findViewById(R.id.letterAssistant);
        eventScheduler = view.findViewById(R.id.eventManager);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String role = preferences.getString("auth_userole", null);
        if (role !=null){
            mainLayout.setVisibility(View.VISIBLE);
            noUserLayout.setVisibility(View.GONE);
            if (role.equals("Faculty")){
                post.setVisibility(View.GONE);
                memory.setVisibility(View.GONE);
            }
        }else{

            mainLayout.setVisibility(View.GONE);
            noUserLayout.setVisibility(View.VISIBLE);

            post.setEnabled(false);
            post.setAlpha(0.5f);

            notice.setEnabled(false);
            notice.setAlpha(0.5f);

            memory.setEnabled(false);
            memory.setAlpha(0.5f);

            project.setEnabled(false);
            project.setAlpha(0.5f);

            internship.setEnabled(false);
            internship.setAlpha(0.5f);

            eventScheduler.setEnabled(false);
            eventScheduler.setAlpha(0.5f);
        }

//        boolean specialAccess = checkSpecialUser(getContext());
//        if (!specialAccess){
//            post.setEnabled(false);
//            post.setAlpha(0.5f);
//
//            notice.setEnabled(false);
//            notice.setAlpha(0.5f);
//
//            memory.setEnabled(false);
//            memory.setAlpha(0.5f);
//
//            project.setEnabled(false);
//            project.setAlpha(0.5f);
//
//            internship.setEnabled(false);
//            internship.setAlpha(0.5f);
//        }

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreatePost.class);
                startActivity(intent);
            }
        });

        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateNotice.class);
                startActivity(intent);
            }
        });

        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UploadMemories.class);
                startActivity(intent);
            }
        });

        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateProject.class);
                startActivity(intent);
            }
        });

        internship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateInternshipNotification.class);
                startActivity(intent);
            }
        });

        letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateLetter.class);
                startActivity(intent);
            }
        });

        eventScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EventScheduleActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}