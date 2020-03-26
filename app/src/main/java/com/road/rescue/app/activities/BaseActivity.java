package com.road.rescue.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    TextInputEditText pass, email, name, phone, cnic;
    TextInputLayout passwordinput, emailinput;
    String encrypted;
    String userName, userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(BaseActivity.this);

        try {
            JSONObject jsonObject = new JSONObject(SharedPrefUtils.getStringData(this, "userData"));
            userEmail = jsonObject.getString("email");
            userName = jsonObject.getString("name");
            SharedPrefUtils.saveData(this, "uid", jsonObject.getString("id"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //TODO: show progress dialog
    public void setProgressDialog(String message) {
        progressDialog.setTitle("Please wait");
        progressDialog.setIcon(R.drawable.unnamed);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //TODO: cancel progress dialog
    public void cancelProgressDialog() {

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    //toDO: show toast message
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //TODO: start new activity
    public void startNewActivity(Activity activity) {
        startActivity(new Intent(BaseActivity.this, activity.getClass()));
    }

    /*
     * TODO: method for email input field validation
     * */
    public void validateEmailfield(Editable s) {
        if (TextUtils.isEmpty(s)) {
            emailinput.setError(getString(R.string.error_field_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
            emailinput.setErrorEnabled(true);
            emailinput.setError(getString(R.string.error_invalid_email));

        } else if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
            emailinput.setError(null);
            emailinput.setErrorEnabled(false);
        }
    }

    /*
     * Todo: method for password input field validation
     * */
    public void validatePasswordField(Editable s) {
        if (TextUtils.isEmpty(s)) {

            passwordinput.setError(getString(R.string.error_field_required));
        } else if (s.toString().length() <= 6) {
            passwordinput.setErrorEnabled(true);
            passwordinput.setError(getString(R.string.error_invalid_password));


        } else if (s.toString().length() >= 6) {
            passwordinput.setError(null);
            passwordinput.setErrorEnabled(false);
        }
    }

    //TODO: current time
    public String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    //TODO: current date
    public String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }
}
