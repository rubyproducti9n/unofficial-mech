package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;

import com.google.android.material.card.MaterialCardView;

public class PluginsActivity extends BaseActivity {

    MaterialCardView backBtn, aiPlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugins);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.matte_black));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ImageView bgImg1 = findViewById(R.id.bgImg1);
        ImageView bgImg2 = findViewById(R.id.bgImg2);


        animateVertically(this, bgImg2, -250, 2000);
        animateVertically(this, bgImg1, 150, 3000);

        backBtn = findViewById(R.id.backBtn);
        aiPlugin = findViewById(R.id.ai_plugin);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        aiPlugin.setTransitionName("test1");

        aiPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.showCustomDialog(
                        PluginsActivity.this,
                        "About plugin",
                        "This plugin will install all the AI features required which is approx. 100 MB in size. \nAre you sure to download the plugin?",
                        "Install",
                        "Cancel",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(PluginsActivity.this, "Downloading", Toast.LENGTH_SHORT).show();

                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(PluginsActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PluginsActivity.this);
//                builder.setTitle("About plugin");
//                builder.setMessage("Describe about plugin here...");
//                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(PluginsActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.show();
            }
        });

//        animate(bgImg1, -250);
    }


}