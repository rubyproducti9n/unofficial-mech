package com.rubyproducti9n.unofficialmech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

public class AIToolsActivity extends AppCompatActivity {
    ConstraintLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aitools);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.matte_black));
        }

        MaterialCardView mc1 = findViewById(R.id.mc1);
        ImageView img1 = findViewById(R.id.img1);
        TextView t1 = findViewById(R.id.t1);
        TextView st1 = findViewById(R.id.st1);
        String u1 = "https://www.humata.ai/";
        //Picasso.get().load("").into(img1);
        t1.setText("Humata AI");
        st1.setText("Your homework solution");
        mc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl(u1);
            }
        });

        MaterialCardView mc2 = findViewById(R.id.mc2);
        ImageView img2 = findViewById(R.id.img2);
        TextView t2 = findViewById(R.id.t2);
        TextView st2 = findViewById(R.id.st2);
        String u2 = "https://tldv.io";
        //Picasso.get().load("").into(img2);
        t2.setText("Super Grow");
        st2.setText("Grow your LinkedIn with AI");
        mc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl(u2);
            }
        });

        MaterialCardView mc3 = findViewById(R.id.mc3);
        ImageView img3 = findViewById(R.id.img3);
        TextView t3 = findViewById(R.id.t3);
        TextView st3 = findViewById(R.id.st3);
        String u3 = "https://temp-mail.org";
        //Picasso.get().load("").into(img3);
        t3.setText("Temp Mail");
        st3.setText("Generate temperory emails for free");
        mc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl(u3);
            }
        });

        MaterialCardView mc4 = findViewById(R.id.mc4);
        ImageView img4 = findViewById(R.id.img4);
        TextView t4 = findViewById(R.id.t4);
        TextView st4 = findViewById(R.id.st4);
        String u4 = "https://scholar.google.com/";
        //Picasso.get().load("").into(img4);
        t4.setText("Research papers");
        st4.setText("Get genuine research papers");
        mc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl(u4);
            }
        });

        MaterialCardView mc5 = findViewById(R.id.mc5);
        ImageView img5 = findViewById(R.id.img5);
        TextView t5 = findViewById(R.id.t5);
        TextView st5 = findViewById(R.id.st5);
        String u5 = "https://www.automateexcel.com/";
        //Picasso.get().load("").into(img5);
        t5.setText("Excel at ease");
        st5.setText("Generate formulas and more");
        mc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl(u5);
            }
        });

        MaterialCardView mc6 = findViewById(R.id.mc6);
        ImageView img6 = findViewById(R.id.img6);
        TextView t6 = findViewById(R.id.t6);
        TextView st6 = findViewById(R.id.st6);
        String u6 = "https://mymap.ai";
        //Picasso.get().load("").into(img6);
        t6.setText("My Map");
        st6.setText("Creating presentations using AI");
        mc6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl(u6);
            }
        });

//        createCard(container, "Title", "about the model", "https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg");
//        createCard(container, "Title", "about the model (Alphabet model)", "https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg");
    }

    private void parseUrl(String url){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("link", url);
        startActivity(intent);
    }

//    private void createCard(ConstraintLayout constraintLayout, String title, String description, String url){
//        View cardView = LayoutInflater.from(this).inflate(R.layout.ai_tool_item, null);
//
//        MaterialCardView materialCardView = cardView.findViewById(R.id.materialCard);
//        TextView titleTextView = cardView.findViewById(R.id.title);
//        TextView descriptionTextView = cardView.findViewById(R.id.description);
//
//        materialCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openUrl(url);
//            }
//        });
//        int id = (int) System.currentTimeMillis();
//        materialCardView.setId(id);
//        materialCardView.setTag(id);
//        titleTextView.setText(title);
//        descriptionTextView.setText(description);
//        constraintLayout.addView(cardView);
//
////        MaterialCardView card = new MaterialCardView(this);
////        card.setLayoutParams(new ViewGroup.LayoutParams(
////                ViewGroup.LayoutParams.MATCH_PARENT,
////                ViewGroup.LayoutParams.WRAP_CONTENT
////        ));
////        card.setUseCompatPadding(true);
////
////        LinearLayout cardContent = new LinearLayout(this);
////        cardContent.setLayoutParams(new ViewGroup.LayoutParams(
////                LinearLayout.LayoutParams.MATCH_PARENT,
////                ViewGroup.LayoutParams.WRAP_CONTENT
////        ));
////
////        TextView textView = new TextView(this);
////        textView.setLayoutParams(new ViewGroup.LayoutParams(
////                ViewGroup.LayoutParams.MATCH_PARENT,
////                ViewGroup.LayoutParams.WRAP_CONTENT
////        ));
////        textView.setText(title);
////
////        ImageView img = new ImageView(this);
////        img.setLayoutParams(new ViewGroup.LayoutParams(
////                ViewGroup.LayoutParams.MATCH_PARENT,
////                ViewGroup.LayoutParams.WRAP_CONTENT
////        ));
////        Picasso.get().load(ic).into(img);
////
////        cardContent.addView(textView);
////        cardContent.addView(img);
////
////        card.addView(cardContent);
////
////        container.addView(card);
//    }

    private void openUrl(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        this.startActivity(intent);
    }
}