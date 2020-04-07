package com.road.rescue.app.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.model.EmergencyContact;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.MyHolder> {
    private Context context;
    private List<EmergencyContact> myComplaintList;

    public EmergencyContactAdapter(Context context, List<EmergencyContact> myComplaintList) {
        this.context = context;
        this.myComplaintList = myComplaintList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_emergency_layout, parent, false);

        return new MyHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final EmergencyContact myComplaint = myComplaintList.get(position);
        holder.city.setText(myComplaint.geteContact());
        holder.complaintType.setText(myComplaint.geteName());
        holder.call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + myComplaint.geteContact()));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return myComplaintList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        MaterialTextView complaintType, city;
        MaterialCardView clickBtn;
        ImageView call;
        RelativeLayout viewBackground, viewForeground;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            call = itemView.findViewById(R.id.call);
            complaintType = itemView.findViewById(R.id.complaintType);
            clickBtn = itemView.findViewById(R.id.clickBtn);
            city = itemView.findViewById(R.id.city);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position) {
        myComplaintList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(EmergencyContact item, int position) {
        myComplaintList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
