package com.road.rescue.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.road.rescue.app.R;
import com.road.rescue.app.activities.BaseActivity;
import com.road.rescue.app.activities.MainActivity;
import com.road.rescue.app.model.EmergencyContact;
import com.road.rescue.app.utils.Constants;
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.RequestHandler;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.road.rescue.app.utils.Constants.ADD_EMERGENCY_CONTACT;
import static com.road.rescue.app.utils.Constants.TAGI;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyHolder> {
    private List<EmergencyContact> myComplaintList;
    private Context context;
    private boolean isFromContact;

    public ContactAdapter(List<EmergencyContact> myComplaintList, Context context, boolean isFromContact) {
        this.myComplaintList = myComplaintList;
        this.context = context;
        this.isFromContact = isFromContact;
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
        final EmergencyContact emergencyContact = myComplaintList.get(position);
        holder.name.setText(emergencyContact.geteName());
        holder.num.setText(emergencyContact.geteContact());
        holder.clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(emergencyContact.geteName(), emergencyContact.geteContact());

            }
        });
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

    private void addContact(final String geteName, final String geteContact) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (InternetConnection.checkConnection(context)) {
                            addContactE(geteName, geteContact);
                        } else {
                            ((BaseActivity) context).showToast("No internet connection");
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to add this number as emergency contact?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void addContactE(final String geteName, final String geteContact) {
        try {
            ((BaseActivity) context).setProgressDialog("Adding Emergency Contact...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + ADD_EMERGENCY_CONTACT,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(String response) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {


                                        Log.d(TAGI, "e" + obj.getString("message"));
                                        ((BaseActivity) context).showToast(obj.getString("message"));
                                        SharedPrefUtils.saveData(context, "eData", obj.getString("data"));
                                        if (isFromContact) {
                                            ((AppCompatActivity) context).finish();
                                        } else {
                                            SharedPrefUtils.saveData(context, "isContact", true);
                                            ((BaseActivity) context).startNewActivity(new MainActivity());
                                            ((AppCompatActivity) context).finish();
                                        }
                                    } else {
                                        ((BaseActivity) context).showToast(obj.getString("message"));
                                        Log.d(TAGI, "e1" + obj.getString("message"));

                                    }
                                    ((BaseActivity) context).cancelProgressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ((BaseActivity) context).cancelProgressDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                ((BaseActivity) context).cancelProgressDialog();
                                ((BaseActivity) context).showToast(error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("uid", SharedPrefUtils.getStringData(context, "uid"));
                    params.put("ename", geteName);
                    params.put("econtact", geteContact);

                    return params;
                }
            };


            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
