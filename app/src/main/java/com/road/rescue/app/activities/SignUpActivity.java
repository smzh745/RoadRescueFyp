package com.road.rescue.app.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.TAGI;
import static com.road.rescue.app.utils.Constants.USER_REG;

public class SignUpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        MaterialTextView titleText = findViewById(R.id.titleText);
        titleText.setText(getString(R.string.sign_to_your_account));
        pass = findViewById(R.id.pass);
        email = findViewById(R.id.email);
        cnic = findViewById(R.id.cnic);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        emailinput = findViewById(R.id.emailinput);
        passwordinput = findViewById(R.id.passwordinput);
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


    @Override
    public void onBackPressed() {
        finish();
    }

    public void signUp1(View view) {
        String password = Objects.requireNonNull(pass.getText()).toString();
        String mail = Objects.requireNonNull(email.getText()).toString();
        String cnic1 = Objects.requireNonNull(cnic.getText()).toString();
        String num = Objects.requireNonNull(phone.getText()).toString();
        String name1 = Objects.requireNonNull(name.getText()).toString();
        try {
            encrypted = AESUtils.encrypt(password);
            Log.d(TAGI, "pass: " + encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(mail)
                || TextUtils.isEmpty(cnic1) || TextUtils.isEmpty(num)
                || TextUtils.isEmpty(name1)) {
            showToast("Please fill the field");
        } else if (Patterns.EMAIL_ADDRESS.matcher(mail).matches() && password.length() >= 6) {
            if (InternetConnection.checkConnection(SignUpActivity.this)) {

                signUpUser(mail, encrypted, cnic1, num, name1);
            } else {
                showToast("No internet connection");
            }
        } else {
            showToast("Check error before preceding");
        }
    }

    private void signUpUser(final String mail, final String encrypted, final String cnic1, final String num, final String name1) {
        try {
            setProgressDialog("Registering user...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + USER_REG,
                    response -> {
                        try {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {

                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finishAffinity();
                                    Log.d(TAGI, "e" + obj.getString("message"));
                                    showToast(obj.getString("message"));
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
                    params.put("password", encrypted);
                    params.put("name", name1);
                    params.put("phonenumber", num);
                    params.put("cnic", cnic1);

                    return params;
                }
            };


            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(View view) {
        startNewActivity(new LoginActivity());
    }
}
