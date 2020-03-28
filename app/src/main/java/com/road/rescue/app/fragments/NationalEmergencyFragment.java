package com.road.rescue.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.road.rescue.app.R;


public class NationalEmergencyFragment extends Basefragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);
        view.findViewById(R.id.addEmergencyContact).setVisibility(View.GONE);
        return view;
    }
}
