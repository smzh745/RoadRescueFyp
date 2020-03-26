package com.road.rescue.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.SharedPrefUtils;
import com.road.rescue.app.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.road.rescue.app.utils.Constants.USER_COMPLAINT;


public class ComplaintSystemFragment extends Basefragment {

    private Spinner complaintType, selectCity;
    private TextInputEditText complaintDetails;
    private ImageView imagePick;
    private final static int PICK_IMAGE = 101;
    private final static int PICK_CAM = 102;
    private Bitmap thumbnail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_complaint_system, container, false);
        complaintType = view.findViewById(R.id.complaintType);
        selectCity = view.findViewById(R.id.selectCity);
        complaintDetails = view.findViewById(R.id.complaintDetails);
        imagePick = view.findViewById(R.id.imagePick);
        MaterialButton send = view.findViewById(R.id.send);


        setSpinner(R.array.complaint_array, complaintType);
        setSpinner(R.array.city_array, selectCity);

        imagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadComplaintDetails();
            }
        });
        return view;
    }


    private void setSpinner(int array, Spinner spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    //choose dialog
    private void showDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        pictureDialog.setTitle("Select Action");

        String[] pictureDialogItems = {
                "Pick Image from Gallery",
                "Take Photo from Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                                break;
                            case 1:
                                try {
                                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent1, PICK_CAM);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    assert data != null;

                    Uri path = Uri.parse(Objects.requireNonNull(data.getData()).toString());
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imagePick.setImageURI(path);

                }
                break;
            case PICK_CAM:
                try {
                    assert data != null;
                    thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    imagePick.setImageBitmap(thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;

        }
    }

    private void uploadComplaintDetails() {
        final String details = Objects.requireNonNull(complaintDetails.getText()).toString();
        final String complaintT = complaintType.getSelectedItem().toString();
        final String city = selectCity.getSelectedItem().toString();
        if (city.equalsIgnoreCase("select city")
                || complaintT.equalsIgnoreCase("Complaint Type") || TextUtils.isEmpty(details) || thumbnail == null) {
            baseActivity.showToast("Please choose all options");
        } else {
            baseActivity.setProgressDialog("Sending your complaint..");
            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, USER_COMPLAINT,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                JSONObject obj = new JSONObject(new String(response.data));
                                baseActivity.showToast(obj.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            baseActivity.cancelProgressDialog();
                            complaintDetails.getText().clear();
                            Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.addimage).into(imagePick);
                            setSpinner(R.array.complaint_array, complaintType);
                            setSpinner(R.array.city_array, selectCity);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            baseActivity.showToast(error.getMessage());
                            baseActivity.cancelProgressDialog();

                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "uid"));
                    params.put("complaintdetails", details);
                    params.put("complainttype", complaintT);
                    params.put("city", city);
                    params.put("cdate", baseActivity.getCurrentDate());
                    params.put("ctime", baseActivity.getCurrentTime());
                    return params;
                }

                /*
                 * Here we are passing image by renaming it with a unique name
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(thumbnail)));
                    return params;
                }
            };

            //adding the request to volley
            Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(volleyMultipartRequest);
        }
    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
