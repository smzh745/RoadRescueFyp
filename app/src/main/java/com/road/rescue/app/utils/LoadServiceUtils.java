package com.road.rescue.app.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.road.rescue.app.services.ShakeCounterService;
import com.road.rescue.app.services.ShakeService;

public class LoadServiceUtils {


    public static void startService(Context context) {
        Intent intent = new Intent(context, ShakeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, ShakeService.class));
    }

    public static void startServiceCount(Context context) {
        Intent intent = new Intent(context, ShakeCounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopServiceCount(Context context) {
        context.stopService(new Intent(context, ShakeCounterService.class));
    }
}
