package com.rubyproducti9n.unofficialmech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceSheetAdapter extends RecyclerView.Adapter<AttendanceSheetAdapter.ViewHolder> {
    private List<AttendanceSheetActivity.SheetModel> dataList;

    public AttendanceSheetAdapter(List<AttendanceSheetActivity.SheetModel> dataList){
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AttendanceSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_sheet_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceSheetAdapter.ViewHolder holder, int position) {
        AttendanceSheetActivity.SheetModel dataModel = dataList.get(position);

        Object key = dataModel.getKey();
        Object value = dataModel.getValue();

        if (key!=null && !key.equals("Date") && !key.equals("Division")){
            if (value.equals(true) || value.equals(false) ||value.equals("true") || value.equals("false")){
                try{
                    boolean val = Boolean.parseBoolean((String) value);
                    if (val){
                        holder.attendance.setChecked(true);
                        holder.roll.setText((CharSequence) key);
                    }else{
                        holder.attendance.setChecked(false);
                        holder.roll.setText((CharSequence) key);
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }else{
                holder.attendance.setVisibility(View.GONE);
                holder.roll.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roll;
        CheckBox attendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            roll = itemView.findViewById(R.id.roll);
            attendance = itemView.findViewById(R.id.attendance);

        }
    }

}
