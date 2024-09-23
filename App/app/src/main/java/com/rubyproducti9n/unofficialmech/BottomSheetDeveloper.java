package com.rubyproducti9n.unofficialmech;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;


public class BottomSheetDeveloper extends BottomSheetProfileEdit {
    TextInputEditText materialText;
    MaterialButton mdBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_developer, container, false);


        materialText = view.findViewById(R.id.uniquePass);
        mdBtn = view.findViewById(R.id.checkbtn);

        mdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });

        return view;
    }

    public void checkPassword(){
        String Password = materialText.getText().toString();
        if(Password.equals("dev@9693")){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Warning");
            builder.setMessage("Do you want to enter in Developer mode?");
            builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getContext(), DeveloperActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else{
            Intent intent = new Intent(getContext(), AccessDeniedActivity.class);
            startActivity(intent);
        }
    }
}
