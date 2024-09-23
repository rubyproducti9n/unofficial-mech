package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Home2Adapter extends RecyclerView.Adapter<Home2Adapter.BetaViewHolder>{
    private List<HomeItem2> items;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;

    @NonNull
    @Override
    public Home2Adapter.BetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_2, parent, false);
        return new Home2Adapter.BetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Home2Adapter.BetaViewHolder holder, int position) {
        HomeItem2 item = items.get(position);


    }


    @Override
    public int getItemCount() {
        return 0;
    }
    public static class BetaViewHolder extends RecyclerView.ViewHolder {
        private TextView featureName;
        private Activity destinationActivity;


        public TextView getFeatureName(){
            return featureName;
        }
        public Activity getDestinationActivity() {
            return destinationActivity;
        }


        public BetaViewHolder(@NonNull View itemView) {
            super(itemView);
//            featureName = itemView.findViewById(R.id.betaTxt);
//            destinationActivity = itemView.findViewById(R.id.betaCard);
            Activity activity = getDestinationActivity();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), activity.getClass());
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}
