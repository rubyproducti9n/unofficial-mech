package com.rubyproducti9n.unofficialmech;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.viewpager2.widget.ViewPager2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
