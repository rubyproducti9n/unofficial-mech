package com.rubyproducti9n.unofficialmech;


import android.app.Service;
import android.app.job.JobInfo;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobScheduler extends Service {

    private static final int JOB_ID = 123;
    private static final long REPEAT_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private JobScheduler jobScheduler;
    private Set<String> processedChildKeys = new HashSet<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }


}
