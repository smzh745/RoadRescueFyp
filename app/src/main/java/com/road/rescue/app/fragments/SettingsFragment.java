package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.LoadServiceUtils;
import com.road.rescue.app.utils.SharedPrefUtils;

import java.util.Objects;


public class SettingsFragment extends Basefragment {
    private SwitchMaterial enableHelp, enableCall;
    private MaterialTextView enableCallText, enableHelpText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        SwitchMaterial enableShake = view.findViewById(R.id.enableShake);
        enableCall = view.findViewById(R.id.enableCall);
        enableHelp = view.findViewById(R.id.enableHelp);
        enableHelpText = view.findViewById(R.id.enableHelpText);
        enableCallText = view.findViewById(R.id.enableCallText);
        checkShakeEnabled();
        enableShake.setChecked(SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isShake"));
        enableHelp.setChecked(SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isHelp"));

        enableShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isShake", true);
                    LoadServiceUtils.startService(getActivity());
                    checkShakeEnabled();
                } else {
                    SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isShake", false);
                    LoadServiceUtils.stopService(getActivity());
                    checkShakeEnabled();
                }
            }
        });
        enableHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isHelp", true);
                } else {
                    SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isHelp", false);

                }
            }
        });
        return view;
    }

    private void checkShakeEnabled() {
        if (!SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isShake")) {
            enableCall.setClickable(false);
            enableHelp.setClickable(false);
            enableCall.setTextColor(getResources().getColor(R.color.colorGainsboro));
            enableHelp.setTextColor(getResources().getColor(R.color.colorGainsboro));
            enableHelpText.setTextColor(getResources().getColor(R.color.colorGainsboro));
            enableCallText.setTextColor(getResources().getColor(R.color.colorGainsboro));
        } else {
            enableCall.setClickable(true);
            enableHelp.setClickable(true);
            enableCall.setTextColor(getResources().getColor(R.color.colorText));
            enableHelp.setTextColor(getResources().getColor(R.color.colorText));
            enableHelpText.setTextColor(getResources().getColor(R.color.colorAccent));
            enableCallText.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }
}
