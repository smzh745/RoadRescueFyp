package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.AESUtils;
import com.road.rescue.app.utils.Constants;
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.RequestHandler;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.TAGI;
import static com.road.rescue.app.utils.Constants.USER_CHANGE_PASS;


public class ChangePasswordFragment extends Basefragment {


    private TextInputEditText pass, cpass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        passwordinput = view.findViewById(R.id.passwordinput);
        cpasswordinput = view.findViewById(R.id.cpasswordinput);
        pass = view.findViewById(R.id.pass);
        cpass = view.findViewById(R.id.cpass);
        MaterialButton resetPass = view.findViewById(R.id.resetPass);

        //password field validation
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordField(s);
            }
        });
        //checking password input
        passwordinput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePasswordField(((EditText) v).getText());
            }
        });
        //password field validation
        cpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateCPasswordField(s);
            }
        });
        //checking password input
        cpasswordinput.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        validateCPasswordField(((EditText) v).getText());
                    }
                });

        resetPass.setOnClickListener(v -> resetPassword());
        return view;
    }

    private void resetPassword() {
        String password = Objects.requireNonNull(pass.getText()).toString();
        String cPassword = Objects.requireNonNull(cpass.getText()).toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)) {
            baseActivity.showToast("Please fill the field");
        } else if (password.equalsIgnoreCase(cPassword)) {
            if (password.length() >= 6) {
                if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {

                    changePass(password);
                } else {
                    baseActivity.showToast("No internet connection");
                }
            } else {
                baseActivity.showToast("Check error before preceding");
            }
        } else {
            baseActivity.showToast("Password must be same");
        }

    }

    private void changePass(final String password) {
        try {
            baseActivity.setProgressDialog("Changing Password...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + USER_CHANGE_PASS,
                    response -> {
                        try {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {

                                    baseActivity.showToast(obj.getString("message"));
                                } else {
                                    baseActivity.showToast(obj.getString("message"));
                                    Log.d(TAGI, "e1: " + obj.getString("message"));
                                }
                                baseActivity.cancelProgressDialog();
                                Objects.requireNonNull(cpass.getText()).clear();
                                Objects.requireNonNull(pass.getText()).clear();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                baseActivity.cancelProgressDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        try {
                            baseActivity.cancelProgressDialog();
                            baseActivity.showToast(error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("id", SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "uid"));
                    try {
                        params.put("password", AESUtils.encrypt(password));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return params;
                }
            };


            RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
