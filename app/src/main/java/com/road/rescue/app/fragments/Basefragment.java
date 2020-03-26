package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.road.rescue.app.R;
import com.road.rescue.app.activities.BaseActivity;

import java.util.Objects;

class Basefragment extends Fragment {
    View view;
    TextInputLayout passwordinput, cpasswordinput;
    BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = ((BaseActivity) getActivity());
    }

    /*
     * Todo: method for password input field validation
     * */
    void validatePasswordField(Editable s) {
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
    /*
     * Todo: method for password input field validation
     * */

    void validateCPasswordField(Editable s) {
        if (TextUtils.isEmpty(s)) {

            cpasswordinput.setError(getString(R.string.error_field_required));
        } else if (s.toString().length() <= 6) {
            cpasswordinput.setErrorEnabled(true);
            cpasswordinput.setError(getString(R.string.error_invalid_password));


        } else if (s.toString().length() >= 6) {
            cpasswordinput.setError(null);
            cpasswordinput.setErrorEnabled(false);
        }
    }

    //TODO: navigate to fragment
    public void navigateFragment(int id){
        Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(id);
    }
}
