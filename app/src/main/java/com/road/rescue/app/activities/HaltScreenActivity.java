package com.road.rescue.app.activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.road.rescue.app.fragments.OverlayFragment;

public class HaltScreenActivity extends BaseActivity {

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // fragment表示
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentType.OVERLAY.getTag());
        if (fragment == null) {
            fragment = OverlayFragment.newInstance();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment, FragmentType.OVERLAY.getTag());
        ft.commit();
    }

    private enum FragmentType {
        OVERLAY("overlay");
        private String tag;

        FragmentType(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }
}
