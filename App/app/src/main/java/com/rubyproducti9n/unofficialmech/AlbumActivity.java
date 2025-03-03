package com.rubyproducti9n.unofficialmech;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;


public class AlbumActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver;
    private ImageView imgView;
    private ImageAdapter adapter;

    AppBarLayout appBarLayout;
    boolean isToolbarCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //IMP: DO NOT DELETE ==========================================
        broadcastReceiver = new ConnectionReceiver();
        registerNetworkBroadcast();

        //Screenshot Security
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgView = findViewById(R.id.fullscreen_img_view);
        String imgUrl = getIntent().getStringExtra("imageUrl");
        Picasso.get().load(imgUrl).into(imgView);

        appBarLayout = findViewById(R.id.appBar);


        EdgeToEdge.enable(this);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab photos = tabLayout.newTab().setIcon(R.drawable.round_notifications_24);
        TabLayout.Tab blog = tabLayout.newTab().setIcon(R.drawable.round_notifications_24);
        TabLayout.Tab audio = tabLayout.newTab().setIcon(R.drawable.round_notifications_24);
        ViewPager viewPager = findViewById(R.id.view_pager);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                boolean isCollapsible = shouldCollapsedAppBar(position);
                appBarLayout.setExpanded(isCollapsible, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
//==============================================================
    }

    private boolean shouldCollapsedAppBar(int position){
        return position == 1;
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Photos";
                case 1:
                    return "Videos";
                default:
                    return null;
            }
        }

        public MyPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new FragmentAlbum();
                case 1:
                    return new FragmentHome();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_report:
                report();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void report(){
        String url = "https://forms.gle/Ssj2owNYxWtfEgFG8";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }

    protected void registerNetworkBroadcast(){
        ConnectionReceiver receiver = new ConnectionReceiver();
        registerReceiver(receiver, new IntentFilter());
    }
    protected void unregisteredNetwork(BroadcastReceiver broadcastReceiver){
        try {
            unregisteredNetwork(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisteredNetwork(broadcastReceiver);
    }
}