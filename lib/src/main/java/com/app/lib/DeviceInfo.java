package com.app.lib;

import android.content.Context;
import android.content.pm.PackageManager;

import io.reactivex.Flowable;

/**
 * Created by mohamed ibrahim on 7/31/2017.
 */

public class DeviceInfo {


    public static Flowable<Integer> libVersionCode() {
        return Flowable.just(BuildConfig.VERSION_CODE);
    }

    public static Flowable<Boolean> hasPermission(Context context, String permission) {
        return Flowable.just(context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    

}
