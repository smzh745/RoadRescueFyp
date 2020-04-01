package com.road.rescue.app.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.road.rescue.app.R;
import com.road.rescue.app.services.OverlayService;

import java.util.Objects;


public class OverlayFragment extends Basefragment {


    public static OverlayFragment newInstance() {
        return new OverlayFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view = inflater.inflate(R.layout.fragment_overlay, container, false);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).stopService(new Intent(getContext(), OverlayService.class));
                getActivity().finish();
            }
        });

        return view;
    }


}
