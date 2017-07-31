package com.app.rxdeviceinfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.app.lib.DeviceInfo;

import io.reactivex.MaybeSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test_lib_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DeviceInfo.hasNetwork(this ,true).map(new Function<Boolean, String>() {
            @Override
            public String apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean ? "Network available" : "Network Not Available";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                log(s);
            }
        });


        DeviceInfo.isWifiEnabled(this).map(new Function<Boolean, String>() {
            @Override
            public String apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean ? "Wifi available" : "Wifi Not Available";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                log(s);
            }
        });


        DeviceInfo.networkType(this).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                log(s);
            }
        });


        DeviceInfo.wifiSpeed(this).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                log(s  +" Mbps");

            }
        });


    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
