package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity {

    private ProjectAdapter adapter;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authName = sharedPreferences.getString("auth_name", null);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (authName!=null){
            fab.setVisibility(View.GONE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProjectActivity.this, CreateProject.class);
                    startActivity(intent);
                }
            });
        }else{
            fab.setVisibility(View.GONE);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postRef = database.getReference("projects");

        Map<String, Boolean> likes = new HashMap<>();
        TextView emptyTxt = findViewById(R.id.emptyTxt);
        emptyTxt.setVisibility(View.GONE);

        CircularProgressIndicator progressBar = findViewById(R.id.progressBar);
        ProjectToolkit.fadeIn(progressBar);

        ManagerLayout manager = findViewById(R.id.manager);
        manager.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProjectToolkit.fadeOut(recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        serviceCheck(this, new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (result.equals(true)){
//        List<Post> items = Arrays.asList(
//                new Post(
//                        "project1",
//                        "Om Lokhande",
//                        "B",
//                        "https://rubyproducti9n.github.io/mech/memories/ahmed sir/a1.jpg",
//                        "caption goes here",
//                        "2023-05-25 04:02:00",
//                        "private",
//                        likes)
//        );

                List<ProjectItem> items = new ArrayList<>();
                adapter = (ProjectAdapter) new ProjectAdapter(ProjectActivity.this, items);
                postRef.orderByChild("uploadTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        items.clear();
                        long childNumber = snapshot.getChildrenCount();

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String projectId = postSnapshot.child("projectId").getValue(String.class);
                            String username = postSnapshot.child("user_name").getValue(String.class);
                            String div = postSnapshot.child("div").getValue(String.class);
                            String uploadTime = postSnapshot.child("uploadTime").getValue(String.class);
                            String caption = postSnapshot.child("caption").getValue(String.class);
                            String postUrl = postSnapshot.child("imgUrl").getValue(String.class);
                            Boolean patent = postSnapshot.child("patent").getValue(Boolean.class);
                            String resources = postSnapshot.child("resources").getValue(String.class);

                            if (projectId!=null){
                                emptyTxt.setVisibility(View.GONE);
                                ProjectItem postItem = new ProjectItem(projectId, username, div, postUrl, caption, uploadTime, patent, resources);
                                items.add(postItem);
                            }else{
                                ProjectToolkit.fadeOut(progressBar);
                                recyclerView.setVisibility(View.GONE);
                                emptyTxt.setVisibility(View.VISIBLE);
                            }

                        }
                        Collections.reverse(items);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ProjectToolkit.fadeOut(progressBar);
                                ProjectToolkit.fadeIn(recyclerView);
                            }
                        }, 500);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProjectActivity.this, "Error 503", Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }else{
                manager.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }
            }
        });

    }
}