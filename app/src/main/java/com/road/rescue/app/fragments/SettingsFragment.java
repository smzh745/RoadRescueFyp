package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.LoadServiceUtils;
import com.road.rescue.app.utils.SharedPrefUtils;

import java.util.Objects;


public class SettingsFragment extends Basefragment {
    private SwitchMaterial enableHelp;
    private MaterialTextView enableHelpText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        SwitchMaterial enableShake = view.findViewById(R.id.enableShake);
        enableHelp = view.findViewById(R.id.enableHelp);
        enableHelpText = view.findViewById(R.id.enableHelpText);
        checkShakeEnabled();
        enableShake.setChecked(SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isShake"));
        enableHelp.setChecked(SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isHelp"));

        enableShake.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isShake", true);
                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isHelp", true);
                enableHelp.setChecked(true);
                LoadServiceUtils.startService(getActivity());
                checkShakeEnabled();
            } else {
                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isShake", false);
                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isHelp", false);
                enableHelp.setChecked(false);
                LoadServiceUtils.stopService(getActivity());
                checkShakeEnabled();
            }
        });
        enableHelp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isHelp", true);
            } else {
                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isHelp", false);

            }
        });

        return view;
    }

    private void checkShakeEnabled() {
        if (!SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isShake")) {

            enableHelp.setClickable(false);
            enableHelp.setTextColor(getResources().getColor(R.color.colorGainsboro));
            enableHelpText.setTextColor(getResources().getColor(R.color.colorGainsboro));
        } else {
            enableHelp.setClickable(true);
            enableHelp.setTextColor(getResources().getColor(R.color.colorText));
            enableHelpText.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }
}
