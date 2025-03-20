package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFaculty;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFemale;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isHOD;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isMale;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestActivity extends BaseActivity {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ViewPager2 viewPager;
    //private Handler handler = new Handler();
    private Runnable runnable;
//    private CarouselAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //viewPager = findViewById(R.id.viewPager);

        // Image List (Replace with your images)
//        List<String> images = new ArrayList<>();
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/g2.jpg?raw=true");
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/last%20mem/m21.jpg?raw=true");
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/last%20mem/m28.jpg?raw=true");
//        images.add("https://github.com/rubyproducti9n/mech/blob/main/memories/last%20mem/m7.jpg?raw=true");

        // Set Adapter
//        adapter = new CarouselAdapter(images);
//        viewPager.setAdapter(adapter);

        // Add Page Transformer for smooth animation
//        viewPager.setPageTransformer(new DepthPageTransformer());

        // Auto-scroll every 3 seconds
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                int nextItem = viewPager.getCurrentItem() + 1;
//                if (nextItem >= adapter.getItemCount()) {
//                    nextItem = 0;
//                }
//                viewPager.setCurrentItem(nextItem, true);
//                handler.postDelayed(this, 3000);
//            }
//        };

    }

    @Override
    protected void onResume() {
        super.onResume();
//        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }





}
