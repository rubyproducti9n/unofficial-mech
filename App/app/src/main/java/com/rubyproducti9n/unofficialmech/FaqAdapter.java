package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class FaqAdapter extends RecyclerView .Adapter<FaqAdapter.FaqViewHolder>{

    private List<ItemFaq> faqList;
    Context context;

    public FaqAdapter(List<ItemFaq> faqList, Context c) {
        this.faqList = faqList;
        this.context = c;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqAdapter.FaqViewHolder holder, int position) {
        ItemFaq item = faqList.get(position);

        holder.questionTxt.setText("Q. " + item.getQuestion());
        holder.answerTxt.setText(item.getAnswer());

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        if (item.getAnswer().contains("mechanical.official73@gmail.com")){
            holder.btn.setVisibility(View.VISIBLE);
            holder.btn.setText("Contact Us");
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_SENDTO);
                        i.setData(Uri.parse("mailto:"+ "mechanical.official73@gmail.com"));
                        context.startActivity(i);
                }
            });
        }

        if (item.getQuestion().contains("change the password?")){
            holder.btn.setVisibility(View.VISIBLE);
            holder.btn.setText("Change Password");
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (p.getString("auth_name", null) != null){
                        Intent i = new Intent(context, AccountInfo.class);
                        i.putExtra("user_details", "My details");
                        context.startActivity(i);
                    }else{
                        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(context)
                                .setTitle("Manager")
                                .setMessage("This feature can be used only if you are logged in. If you have issue regarding your password please contact us on mechanical.official73@gmail.com requesting for a password change.");
                        b.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public class FaqViewHolder extends RecyclerView.ViewHolder{

        private TextView questionTxt;
        private TextView answerTxt;
        private MaterialButton btn;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTxt = itemView.findViewById(R.id.questionTxt);
            answerTxt = itemView.findViewById(R.id.answerTxt);
            btn = itemView.findViewById(R.id.btn);



        }
    }
}
