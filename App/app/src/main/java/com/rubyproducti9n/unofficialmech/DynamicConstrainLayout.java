package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

public class DynamicConstrainLayout extends ConstraintLayout {

    public DynamicConstrainLayout(Context context){
        super(context);
    }
//    public void DynamicConstraintLayout(Context context, AttributeSet attrs){
//        super(context, attrs);
//    }
//    public void DynamicConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr){
//        super(context, attrs, defStyleAttr);
//    }

    public void createConstraintLayout(String title, String description, String imgUrl, String linkText, final String linkUrl){
        //Creating a Title text and configuring
        TextView titleTxt = new TextView(getContext());
        titleTxt.setId(View.generateViewId());
        titleTxt.setText(title);
        titleTxt.setTextSize(18);

        //Similarly for description
        TextView descTxt = new TextView(getContext());
        descTxt.setId(View.generateViewId());
        descTxt.setText(description);
        descTxt.setTextSize(12);

        //Create img view
        ImageView img = new ImageView(getContext());
        img.setId(generateViewId());
        Picasso.get().load(imgUrl).into(img);

        //Link text
        MaterialButton linkBtn = new MaterialButton(getContext());
        linkBtn.setId(generateViewId());
        linkBtn.setText(linkText);
        linkBtn.setTextSize(18);
        linkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                getContext().startActivity(intent);
            }
        });

        this.addView(titleTxt);
        this.addView(descTxt);
        this.addView(img);
        this.addView(linkBtn);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        //Connect all the constraints
        constraintSet.connect(titleTxt.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(titleTxt.getId(), ConstraintSet.START, this.getId(), ConstraintSet.END);

        constraintSet.connect(descTxt.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(descTxt.getId(), ConstraintSet.START, this.getId(), ConstraintSet.END);

        constraintSet.connect(img.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(img.getId(), ConstraintSet.START, this.getId(), ConstraintSet.END);

        constraintSet.connect(linkBtn.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(linkBtn.getId(), ConstraintSet.START, this.getId(), ConstraintSet.END);

        constraintSet.applyTo(this);

    }


}
