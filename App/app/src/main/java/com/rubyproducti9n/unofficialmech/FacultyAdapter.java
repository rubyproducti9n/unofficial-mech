package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>{

    String facultyId;
    String firstName;
    String lastName;
    String initials;
    String clgEmail;
    String gender;
    String personalEmail;
    String contact;
    String role;
    Boolean status;

    private static final long MORNING_TIME = 10*60*60*1000L;
    private static final long EVENING_TIME = (17*60*60*1000L);
    private List<BottomSheetFacultyAccount.User> items;
    private SharedPreferences sharedPreferences;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;
    Context mContext;

    @NonNull
    @Override
    public FacultyAdapter.FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faculty_item, parent, false);
        return new FacultyAdapter.FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyAdapter.FacultyViewHolder holder, int position) {
        BottomSheetFacultyAccount.User item = items.get(position);

        if(item == null || item.equals("")){
            holder.cardView.setVisibility(View.GONE);
            holder.txt.setVisibility(View.VISIBLE);
        }else{




            holder.cardView.setVisibility(View.VISIBLE);
            holder.txt.setVisibility(View.GONE);

            facultyId  = item.getUserId();
            firstName = item.getFirstName();
            lastName = item.getLastName();
            initials = item.getInitials();
            clgEmail = item.getClgEmail();
            gender = item.getGender();
            personalEmail = item.getPersonalEmail();
            contact = item.getContact();
            role = item.getRole();
            status = item.getStatus();

            if(initials != null){
                String userName = initials + " " + lastName;
                holder.getUsername().setText(userName);
            }else{
                String userName = firstName + " " + lastName;
                holder.getUsername().setText(userName);
            }

//            holder.scheduleStatusUpdate(mContext, status);
//            if (status.equals(true)){
//                holder.getStatusChip().setText("Available");
//                holder.getStatusChip().setChipIconResource(R.drawable.active);
//            }else{
//                holder.getStatusChip().setText("Away");
//                holder.getStatusChip().setChipIconResource(R.drawable.away);
//            }

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String role = sharedPreferences.getString("auth_userole", null);
            if (role!=null){
                holder.callChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initiateCall(contact);
                    }
                });
                holder.waChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initiateWAChat(contact);
                    }
                });
                holder.mailChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initiateMail(personalEmail);
                    }
                });
                holder.appoitnmentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectSlot(mContext);
                    }
                });
            }else{
                holder.callChip.setEnabled(false);
                holder.waChip.setEnabled(false);
                holder.mailChip.setEnabled(false);
                holder.appoitnmentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                        builder.setMessage("You need a account to make an appointment");
                        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent= new Intent(mContext, PasswordActivity.class);
                                mContext.startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }


        }

    }

    public FacultyAdapter(Context context, List<BottomSheetFacultyAccount.User> items){
        mContext = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class FacultyViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView txt;
        TextView username;
        Chip statusChip;
        MaterialButton callChip, waChip, mailChip, appoitnmentBtn;

        private TextView getUsername(){
            return username;
        }
        private Chip getStatusChip(){return statusChip;}
        private MaterialButton getCallChip(){return callChip;}
        private MaterialButton getWaChip(){return waChip;}
        private MaterialButton getMailChip(){return mailChip;}

        public MaterialButton getAppoitnmentBtn() {
            return appoitnmentBtn;
        }

        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txt = itemView.findViewById(R.id.txt);

            username = itemView.findViewById(R.id.username);
            statusChip = itemView.findViewById(R.id.status_chip);
            callChip = itemView.findViewById(R.id.call_chip);
            waChip = itemView.findViewById(R.id.wa_chip);
            mailChip = itemView.findViewById(R.id.mail_chip);
            appoitnmentBtn = itemView.findViewById(R.id.appointment_chip);
        }

        private void serverCheck(){
            MaterialButton call = itemView.findViewById(R.id.call_chip);
            MaterialButton wa = itemView.findViewById(R.id.wa_chip);
            MaterialButton mail = itemView.findViewById(R.id.mail_chip);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("app-configuration/");
            databaseReference.child("uploads").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Boolean status = snapshot.getValue(Boolean.class);
                        if (status){
                            call.setEnabled(true);
                            wa.setEnabled(true);
                            mail.setEnabled(true);
                        }else{
                            call.setEnabled(false);
                            wa.setEnabled(false);
                            mail.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("firebase", "Value not found");
                }
            });
        }

        private void scheduleStatusUpdate(Context context, Boolean status){
            Chip statusChip = itemView.findViewById(R.id.status_chip);
            long currentTime = System.currentTimeMillis();
                serverCheck();
//            if (currentTime >= MORNING_TIME && currentTime < EVENING_TIME){
                if (status){
                    statusChip.setText("Available");
                    statusChip.setChipIconResource(R.drawable.active);
//                    statusChip.setIconResource(R.drawable.active);
//                    statusChip.setIconTint(ColorStateList.valueOf(context.getResources().getColor(R.color.active)));
                }else{
                    statusChip.setText("Away");
                    statusChip.setChipIconResource(R.drawable.away);
//                    statusChip.setIconResource(R.drawable.away);
//                    statusChip.setIconTint(ColorStateList.valueOf(context.getResources().getColor(R.color.away)));
                }
//            }else{
//                statusChip.setText("Away");
//                statusChip.setChipIconResource(R.drawable.away);
//            }

//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    long currentTime = System.currentTimeMillis();
//
//                    if (currentTime >= MORNING_TIME && currentTime < EVENING_TIME){
//                        if (status.equals(true)){
//                            statusChip.setText("Available");
//                            statusChip.setChipIconResource(R.drawable.active);
//                        }else{
//                            statusChip.setText("Away");
//                            statusChip.setChipIconResource(R.drawable.away);
//                        }
//                    }else{
//                        statusChip.setText("Away");
//                        statusChip.setChipIconResource(R.drawable.away);
//                    }
//
//                }
//            }, calculateTimeUntilNextUpdate());
        }

        private long calculateTimeUntilNextUpdate(){
            long currentTime = System.currentTimeMillis();
            long nextMornintTime = MORNING_TIME + 24*60*60*1000;
            long nextEveningTime = EVENING_TIME + 24*60*60*1000;

            if (currentTime < MORNING_TIME){
                return MORNING_TIME - currentTime;
            } else if (currentTime < EVENING_TIME) {
                return EVENING_TIME -currentTime;
            }else{
                return nextMornintTime - currentTime;
            }
        }

    }

    private void selectSlot(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
        builder.setTitle("Select slot")
                .setItems(new String[]{"1st slot (10:00 AM - 12:00 PM)", "2nd slot (12:45 PM - 02:45 PM)", "3rd slot (03:00 PM - 05:00 PM)"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Toast.makeText(context, "1st slot selected!", Toast.LENGTH_SHORT).show();
                                writeCaption(context, "1st");
                                break;
                            case 1:
                                Toast.makeText(context, "2nd slot selected!", Toast.LENGTH_SHORT).show();
                                writeCaption(context, "2nd");
                                break;
                            case 2:
                                Toast.makeText(context, "3rd slot selected!", Toast.LENGTH_SHORT).show();
                                writeCaption(context, "3rd");
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void writeCaption(Context context, String selectedSlot){
        final EditText[] editText = {new EditText(context)};
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText[0].setLayoutParams(layoutParams);

        //Creating a LinearLayout to wrap the EditText
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText[0]);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("About")
                .setView(layout)
                .setPositiveButton("Make appointment", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String caption = Arrays.toString(editText);

                        LocalDateTime currentDateTime = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String uploadTime = currentDateTime.format(dateTimeFormatter);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String username = preferences.getString("auth_name", null);
                        String div = preferences.getString("auth_division", null);
                        if (username!=null && div !=null){
                            makeAppointment(facultyId, username, div, caption, uploadTime, selectedSlot, false);
                        }
                    }
                });
        builder.show();
    }

    private void makeAppointment(String facultyId, String username, String div, String caption, String uploadTime, String appointmentSlot, boolean status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("appointment");
        String appointmentId = reference.push().getKey();

        AppointmentItem appointmentItem = new AppointmentItem(appointmentId, facultyId, username, div, caption, uploadTime, appointmentSlot, status);
        reference.child(appointmentId).setValue(appointmentItem);

    }

    private void initiateCall(String call){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+call));
        mContext.startActivity(intent);
    }
    private void initiateWAChat(String WA){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone="+WA));
        mContext.startActivity(intent);
    }
    private void initiateMail(String mail){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        //intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        //intent.putExtra(Intent.EXTRA_EMAIL, mail);
        intent.setData(Uri.parse("mailto:"+ mail));
        mContext.startActivity(intent);
    }

}
