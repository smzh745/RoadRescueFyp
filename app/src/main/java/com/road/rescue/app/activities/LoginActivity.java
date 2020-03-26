package com.road.rescue.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.AESUtils;
import com.road.rescue.app.utils.Constants;
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.PermissionsUtils;
import com.road.rescue.app.utils.RequestHandler;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.TAGI;
import static com.road.rescue.app.utils.Constants.USER_LOGIN;

public class LoginActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (SharedPrefUtils.getBooleanData(this, "isLogin")) {
            startNewActivity(new MainActivity());
        } else {
            pass = findViewById(R.id.pass);
            email = findViewById(R.id.email);
            emailinput = findViewById(R.id.emailinput);
            passwordinput = findViewById(R.id.passwordinput);
            MaterialTextView titleText = findViewById(R.id.titleText);
            titleText.setText(getString(R.string.login_to_your_account));
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
            passwordinput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        validatePasswordField(((EditText) v).getText());
                    }
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
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        validateEmailfield(((EditText) v).getText());
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= 23) {
                PermissionsUtils permissionsUtils = PermissionsUtils.getInstance(this);
                if (permissionsUtils.isAllPermissionAvailable()) {
                    Log.d(TAGI, "onCreate: permission accepted");
                } else {
                    permissionsUtils.setActivity(this);
                    permissionsUtils.requestPermissionsIfDenied();
                }
            }
        }
    }


    public void login(View view) {
        try {
            String password = Objects.requireNonNull(pass.getText()).toString();
            String mail = Objects.requireNonNull(email.getText()).toString();
            try {
                encrypted = AESUtils.encrypt(password);
                Log.d(TAGI, "pass: " + encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(mail)) {
                showToast("Please fill the field");
            } else if (Patterns.EMAIL_ADDRESS.matcher(mail).matches() && password.length() >= 6) {
                if (InternetConnection.checkConnection(LoginActivity.this)) {

                    loginUser(mail, encrypted);
                } else {
                    showToast("No internet connection");
                }
            } else {
                showToast("Check error before preceding");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forgetPass(View view) {
        startNewActivity(new ForgetPasswordActivity());
    }

    public void signUp(View view) {
        startNewActivity(new SignUpActivity());
    }

    private void loginUser(final String mail, final String password) {
        try {
            setProgressDialog("Authenticating user...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + USER_LOGIN,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(String response) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finishAffinity();
                                        Log.d(TAGI, "e: " + obj.getString("message"));
                                        SharedPrefUtils.saveData(LoginActivity.this, "isLogin", true);
                                        SharedPrefUtils.saveData(LoginActivity.this, "userData", obj.getString("message"));
                                        showToast("Login successfully");
                                    } else {
                                        showToast(obj.getString("message"));
                                        Log.d(TAGI, "e1: " + obj.getString("message"));
                                    }
                                    cancelProgressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    cancelProgressDialog();
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
                                cancelProgressDialog();
                                showToast(error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("email", mail);
                    params.put("password", password);

                    return params;
                }
            };


            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
