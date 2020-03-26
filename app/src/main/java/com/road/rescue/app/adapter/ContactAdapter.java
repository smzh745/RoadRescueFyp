package com.road.rescue.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.road.rescue.app.R;
import com.road.rescue.app.model.EmergencyContact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyHolder> {
    private List<EmergencyContact> myComplaintList;

    public ContactAdapter(List<EmergencyContact> myComplaintList) {
        this.myComplaintList = myComplaintList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);

        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        EmergencyContact emergencyContact = myComplaintList.get(position);
        holder.name.setText(emergencyContact.geteName());
        holder.num.setText(emergencyContact.geteContact());
    }

    @Override
    public int getItemCount() {
        return myComplaintList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView name, num;
        RelativeLayout clickBtn;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            num = itemView.findViewById(R.id.num);
            clickBtn = itemView.findViewById(R.id.clickBtn);
        }
    }


}
