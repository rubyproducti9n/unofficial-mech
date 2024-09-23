package com.rubyproducti9n.unofficialmech;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class CustomDialog {
    private static AlertDialog customDialog;

    public static void showCustomDialog(
            Context context,
            String title,
            String message,
            String positiveButtonText,
            String negativeButtonText,
            View.OnClickListener positiveButtonClickListener,
            View.OnClickListener negativeButtonClickListener
    ) {
        // Inflate the custom layout
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null);
        // Get references to views in the custom layout
        TextView titleTextView = customView.findViewById(R.id.dialog_title);
        TextView messageTextView = customView.findViewById(R.id.dialog_message);
        Button positiveButton = customView.findViewById(R.id.positive_button);
        Button negativeButton = customView.findViewById(R.id.negative_button);

        // Set your custom text or visibility based on your conditions
        if (title != null) {
            titleTextView.setText(title);
            titleTextView.setVisibility(View.VISIBLE);
        } else {
            titleTextView.setVisibility(View.GONE);
        }

        if (message != null) {
            messageTextView.setText(message);
            messageTextView.setVisibility(View.VISIBLE);
        } else {
            messageTextView.setVisibility(View.GONE);
        }

        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
            positiveButton.setVisibility(View.VISIBLE);
            if (positiveButtonClickListener != null) {
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButtonClickListener.onClick(v);
                        customDialog.dismiss();
                    }
                });
            }
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        if (negativeButtonText != null) {
            negativeButton.setText(negativeButtonText);
            negativeButton.setVisibility(View.VISIBLE);
            if (negativeButtonClickListener != null) {
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeButtonClickListener.onClick(v);

                        customDialog.dismiss();
                    }
                });
            }
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        // Optionally, set other properties of the dialog (e.g., title, icon, etc.)
//        if (title != null) {
//            builder.setTitle(title);
//        }

        // Create and show the AlertDialog
        customDialog = builder.create();

        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.getWindow().getDecorView().setAlpha(1.0f);

        customDialog.show();
    }

}
