package com.road.rescue.app.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.AESUtils;
import com.road.rescue.app.utils.Constants;
import com.road.rescue.app.utils.GMailSender;
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.TAGI;
import static com.road.rescue.app.utils.Constants.USER_FORGET_PASS;

public class ForgetPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        email = findViewById(R.id.email);
        emailinput = findViewById(R.id.emailinput);
        MaterialTextView titleText = findViewById(R.id.titleText);
        titleText.setText(getString(R.string.enter_email_to_reset));
        /*
         * email input validation
         * using input layoout and regex*/
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmailfield(s);
            }
        });
        //checking email input
        email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmailfield(((EditText) v).getText());
            }
        });


    }

    public void resetPass(View view) {
        String mail = Objects.requireNonNull(email.getText()).toString();
        if (TextUtils.isEmpty(mail)) {
            showToast("Please fill the field");
        } else if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            if (InternetConnection.checkConnection(ForgetPasswordActivity.this)) {

                resetIt(mail);
            } else {
                showToast("No internet connection");
            }
        } else {
            showToast("Check error before preceding");
        }
    }

    private void resetIt(final String mail) {
        try {
            setProgressDialog("Authenticating user...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + USER_FORGET_PASS,
                    response -> {
                        try {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {

                                    Log.d(TAGI, "e" + obj.getString("message"));

                                    try {
                                        GMailSender sender = new GMailSender("appexvalleylostphone@gmail.com", "appexvalleylostphone1234");
                                        sender.sendMail("Road Rescue Password",
                                                "This is your password for  Road Rescue App  you forgot it. \n\n" +
                                                        "Password is: " + AESUtils.decrypt(obj.getString("message")) + "\n\n" +
                                                        "Regards \n" +
                                                        "Road Rescue Team",
                                                "appexvalleylostphone@gmail.com",
                                                mail);
                                    } catch (Exception e) {
                                        Log.d(TAGI, e.getMessage(), e);
                                    }
                                    showToast("We have sent you your password on your registered email address.");
                                } else {
                                    showToast(obj.getString("message"));
                                    Log.d(TAGI, "e1" + obj.getString("message"));
                                }
                                cancelProgressDialog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                cancelProgressDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        try {
                            cancelProgressDialog();
                            showToast(error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("email", mail);

                    return params;
                }
            };


            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backLogin(View view) {
        startNewActivity(new LoginActivity());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
