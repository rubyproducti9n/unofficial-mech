package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
 * Use the {@link BlogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlogFragment extends Fragment {
    private BlogAdapter adapter;

    boolean isChipGroupVisible = true;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlogFragment newInstance(String param1, String param2) {
        BlogFragment fragment = new BlogFragment();
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
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        HorizontalScrollView horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
        horizontalScrollView.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateNotice.class);
                startActivity(intent);
            }
        });

        ShimmerFrameLayout progressBar = view.findViewById(R.id.shimmer);
        progressBar.setVisibility(View.VISIBLE);
//        ProjectToolkit.fadeIn(progressBar);

        ManagerLayout manager = view.findViewById(R.id.manager);
        manager.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
//        ProjectToolkit.fadeOut(recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        serviceCheck(getContext(), new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (result.equals(true)){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notice");
                    databaseReference.orderByChild("uploadTime").limitToLast(9).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<CreateNotice.Notice> blogItems = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String id = dataSnapshot.child("noticeId").getValue(String.class);
                                String username = dataSnapshot.child("authName").getValue(String.class);
                                String userDiv = dataSnapshot.child("authDiv").getValue(String.class);
                                String imgUrl = dataSnapshot.child("imgUrl").getValue(String.class);
                                String date = dataSnapshot.child("uploadTime").getValue(String.class);
                                String txt = dataSnapshot.child("caption").getValue(String.class);
                                String link = dataSnapshot.child("link").getValue(String.class);
                                boolean impNotice = Boolean.TRUE.equals(dataSnapshot.child("impNotice").getValue(Boolean.class));
                                Integer yearValue = dataSnapshot.child("year").getValue(Integer.class);
                                if (yearValue!=null){
                                        int year = yearValue;
                                    CreateNotice.Notice blogItem = new CreateNotice.Notice(id, username, userDiv, imgUrl, txt, link, date, impNotice, year);
                                    blogItems.add(blogItem);
                                }else{
                                    CreateNotice.Notice blogItem = new CreateNotice.Notice(id, username, userDiv, imgUrl, txt, link, date, impNotice, 0);
                                    blogItems.add(blogItem);
                                }

//                    blogItems.addItem(0, new FragmentSponsor());

                            }
                            Collections.reverse(blogItems);
                            adapter = (BlogAdapter) new BlogAdapter(getContext(), blogItems);
                            recyclerView.setAdapter(adapter);
                            ProjectToolkit.fadeOut(progressBar);
                            ProjectToolkit.fadeIn(recyclerView);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    manager.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                }
            }
        });


//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int horizontalViewHeight = horizontalScrollView.getHeight();
//                int translationY = isChipGroupVisible ? horizontalViewHeight : 0;
//
//                if (dy>0 && isChipGroupVisible){
//                    //ProjectToolkit.fadeOut(horizontalScrollView);
//                    ProjectToolkit.fadeOut(horizontalScrollView);
//                    horizontalScrollView.animate().translationY(-translationY).setDuration(200).withEndAction(() ->{
//                        //ProjectToolkit.fadeOut(horizontalScrollView);
//                        horizontalScrollView.setVisibility(View.GONE);
//                        horizontalScrollView.setTranslationY(0);
//                        isChipGroupVisible = false;
//                    });
//                }else if (dy<0 && !isChipGroupVisible){
//                    //ProjectToolkit.fadeIn(horizontalScrollView);
//                    horizontalScrollView.setVisibility(View.VISIBLE);
//                    horizontalScrollView.setTranslationY(-translationY);
//                    //ProjectToolkit.fadeIn(horizontalScrollView);
//                    horizontalScrollView.animate().translationY(0).setDuration(200);
//                    isChipGroupVisible = true;
//                }
//            }
//        });
//        List<BlogItem> items = Arrays.asList(
//                new BlogItem(
//                        "Aishwarya Kumbhar",
//                        "Hello, this is your Class Representative (CR)",
//                        "2023-07-14"),
//                new BlogItem(
//                        "Om Lokhande",
//                        "College life is a time of great change and growth. It is a time to learn new things, meet new people, and figure out who you are as a person. For me, college life has been an amazing experience. I have made lifelong friends, learned a lot about myself, and grown as a person.\n"
//                        + "\n One of the best things about college life is the friends I have made. I met my three best friends, Avni, Riya, and Srishti, in my first year of college. We were all in the same engineering class, and we quickly bonded over our shared love of learning and our desire to make a difference in the world.\n"
//                        + "\n In addition to my friends, I have also learned a lot about myself in college. I have learned that I am capable of more than I ever thought possible. I have learned that I am a hard worker, and I am determined to succeed. I have learned that I am a creative thinker, and I am not afraid to take risks. I have learned that I am compassionate and caring, and I want to make a difference in the world.\n"
//                        + "\n I love you all so much. Thank you for being my friends.\n"
//                        + "\n With Love,"
//                        + "\n Om Lokhande \n",
//                        "2023-05-19"),
//                new BlogItem(
//                        "Om Lokhande",
//                        "Share your best and funny moments to the unofficial community",
//                        "2023-05-19")
//        );
//        adapter = (BlogAdapter) new BlogAdapter(items);
//        recyclerView.setAdapter(adapter);


        return view;
    }
}