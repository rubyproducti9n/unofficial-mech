package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UnofficialManager {
    private Activity currentActivity;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;

    // Key constants for SharedPreferences
    private static final String PREF_NAME = "UserDetails";
    private static final String KEY_NAME = "name";
    private static final String KEY_UID = "uid";
    private static final String KEY_PRN = "prn";
    private static final String KEY_ROLL_NO = "roll_no";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SERVER_STATUS = "server_status";

    public UnofficialManager(Activity activity) {
        this.currentActivity = activity;
        this.sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.databaseReference = FirebaseDatabase.getInstance().getReference("serverStatus"); // Adjust path as necessary
    }

    // Start a new activity
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(currentActivity, cls);
        currentActivity.startActivity(intent);
    }

    // Finish the current activity
    public void finishActivity() {
        currentActivity.finish();
    }

    // Show a toast message
    public void showToast(String message) {
        Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
    }

    // Navigate back to the previous activity
    public void navigateBack() {
        currentActivity.onBackPressed();
    }

    // Set result for the current activity
    public void setResult(int resultCode, Intent data) {
        currentActivity.setResult(resultCode, data);
    }

    // Check if a specific permission is granted
    public boolean checkPermission(String permission) {
        return currentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    // Request a specific permission
    public void requestPermission(String permission, int requestCode) {
        currentActivity.requestPermissions(new String[]{permission}, requestCode);
    }

    // Get the current activity context
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    // Save user details to SharedPreferences
    public void saveUserDetails(String name, String uid, String prn, String rollNo, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_PRN, prn);
        editor.putString(KEY_ROLL_NO, rollNo);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Get user details from SharedPreferences
    public String getUserDetail(String key) {
        return sharedPreferences.getString(key, null);
    }

    // Check if the server is online
    public void checkServerStatus(final ServerStatusCallback callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isOnline = dataSnapshot.getValue(Boolean.class);
                callback.onStatusChecked(isOnline != null && isOnline);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onStatusChecked(false); // Handle error
            }
        });
    }

    // Callback interface for server status
    public interface ServerStatusCallback {
        void onStatusChecked(boolean isOnline);
    }
}
