package com.rubyproducti9n.unofficialmech;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPostsDev#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPostsDev extends Fragment {

    RecyclerView recyclerView;
    ApplicationRoleReceiverAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentPostsDev() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPostsDev.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPostsDev newInstance(String param1, String param2) {
        FragmentPostsDev fragment = new FragmentPostsDev();
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
        View view = inflater.inflate(R.layout.fragment_posts_dev, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("roleApplications");

        List<RolesApplicationActivity.RoleDataStructure> items = new ArrayList<>();
        adapter = new ApplicationRoleReceiverAdapter(items);
        postRef.orderByChild("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();
                long childNumber = snapshot.getChildrenCount();

                if (snapshot.exists()){
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        String applicationId = postSnapshot.child("appointmentId").getValue(String.class);
                        String userId = postSnapshot.child("userId").getValue(String.class);
                        String role = postSnapshot.child("appliedRole").getValue(String.class);
                        Integer status = postSnapshot.child("status").getValue(Integer.class);

                        RolesApplicationActivity.RoleDataStructure roleItem = new RolesApplicationActivity.RoleDataStructure(userId, status, role);
//                        cachedAppointments.add(appointmentItem);
                        items.add(roleItem);
                    }
                }else{
//                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
//                    lottieAnimationView.setAnimation(R.raw.coming_soon);
//                    lottieAnimationView.loop(true);
//                    lottieAnimationView.playAnimation();
                }
                Collections.reverse(items);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }
}