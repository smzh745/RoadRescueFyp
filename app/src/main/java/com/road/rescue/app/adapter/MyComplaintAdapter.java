package com.road.rescue.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.model.MyComplaint;

import java.util.List;

public class MyComplaintAdapter extends RecyclerView.Adapter<MyComplaintAdapter.MyHolder> {
    private Context context;
    private List<MyComplaint> myComplaintList;

    public MyComplaintAdapter(Context context, List<MyComplaint> myComplaintList) {
        this.context = context;
        this.myComplaintList = myComplaintList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_complaint_layout, parent, false);

        return new MyHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        MyComplaint myComplaint = myComplaintList.get(position);
        holder.city.setText(myComplaint.getCity());
        holder.complaintType.setText(myComplaint.getComplaintType());
        holder.created.setText("Created at: " + myComplaint.getcDate() + ", " + myComplaint.getcTime());
    }

    @Override
    public int getItemCount() {
        return myComplaintList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        MaterialTextView created, complaintType, city;
        MaterialCardView clickBtn;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            created = itemView.findViewById(R.id.created);
            complaintType = itemView.findViewById(R.id.complaintType);
            clickBtn = itemView.findViewById(R.id.clickBtn);
            city = itemView.findViewById(R.id.city);

        }
    }
}
