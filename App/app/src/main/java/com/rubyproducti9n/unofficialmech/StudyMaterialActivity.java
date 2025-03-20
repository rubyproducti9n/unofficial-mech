package com.rubyproducti9n.unofficialmech;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class StudyMaterialActivity extends BaseActivity {
    private ViewFlipper viewFlipper;
    private FileAdapter fileAdapter;
    private List<FileItem> fileList;
    private String currentPath = "https://api.github.com/repos/rubyproducti9n/mech/contents/study%20material/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewFlipper = findViewById(R.id.flipper);

        // Initialize the root folder
        fetchFiles(currentPath);
    }
    private void fetchFiles(String url) {
        OkHttpClient client = new OkHttpClient();

        // Manually encode only the path segments (after the base URL)
        String baseUrl = "https://api.github.com/repos/rubyproducti9n/mech/contents/";
        String encodedPath = url.replace(baseUrl, "");  // Extract the path part of the URL
        String safePath = Uri.encode(encodedPath, "/"); // Encode the path, keeping slashes intact
        String finalUrl = baseUrl + safePath;  // Reconstruct the full URL
        Log.d("MainActivity", "Fetching URL: " + finalUrl);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(StudyMaterialActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String responseData = response.body().string();  // Read the response body
                    runOnUiThread(() -> {
                        parseJSON(responseData);  // Pass the response data to the parseJSON method
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Close the response body to avoid connection leaks
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    private void parseJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            fileList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String type = jsonObject.getString("type");
                String downloadUrl = type.equals("file") ? jsonObject.getString("download_url") : null;
                String path = jsonObject.getString("path");

                // Add files and folders
                fileList.add(new FileItem(name, downloadUrl, type, path));
            }
            setupRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(fileList, this::onFileClick);
        recyclerView.setAdapter(fileAdapter);

        viewFlipper.addView(recyclerView);
        viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);  // Show the last added view
    }

    private void onFileClick(FileItem fileItem) {
        if (fileItem.getType().equals("dir")) {
            // Construct new URL for the folder and URL-encode it
            String newUrl = "https://api.github.com/repos/rubyproducti9n/mech/contents/" + Uri.encode(fileItem.getPath(), "/:?&=");
            Log.d("MainActivity", "Navigating to folder: " + newUrl);  // For debugging
            fetchFiles(newUrl);
        } else if (fileItem.getType().equals("file") && fileItem.getName().endsWith(".pdf")) {
            // Open PDF externally
            openPDFExternally(fileItem.getDownloadUrl());
        }
    }

    private void openPDFExternally(String downloadUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(downloadUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }

    // FileItem class
    public static class FileItem {
        private final String name;
        private final String downloadUrl;
        private final String type;
        private final String path;

        public FileItem(String name, String downloadUrl, String type, String path) {
            this.name = name;
            this.downloadUrl = downloadUrl;
            this.type = type;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getType() {
            return type;
        }

        public String getPath() {
            return path;
        }
    }

    // FileAdapter class
    public static class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
        private final List<FileItem> fileList;
        private final OnFileClickListener listener;

        public FileAdapter(List<FileItem> fileList, OnFileClickListener listener) {
            this.fileList = fileList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            FileItem fileItem = fileList.get(position);
            holder.bind(fileItem, listener);
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public static class FileViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public FileViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            public void bind(FileItem fileItem, OnFileClickListener listener) {
                textView.setText(fileItem.getName());
                Log.d("MainActivity", "Item clicked: " + fileItem.getName());
                itemView.setOnClickListener(v -> listener.onFileClick(fileItem));
            }
        }

    }
    public interface OnFileClickListener {
        void onFileClick(FileItem fileItem);
    }
}