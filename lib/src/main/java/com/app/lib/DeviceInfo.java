package com.app.lib;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.internal.operators.flowable.FlowableFromCallable;

/**
 * Created by mohamed ibrahim on 7/31/2017.
 */

public class DeviceInfo {


    public static Flowable<Integer> libVersionCode() {
        return Flowable.just(BuildConfig.VERSION_CODE);
    }

    public static Observable<Boolean> hasPermission(Context context, String permission) {
        return Observable.just(context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET})
    public static Observable<Boolean> hasNetwork(Context context) {
        return hasNetwork(context, false);
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET})
    public static Observable<Boolean> hasNetwork(final Context context, final boolean register) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Boolean> subscriber) {
                try {

                    if (register) {
                        final IntentFilter filter = new IntentFilter();
                        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                        context.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                                subscriber.onNext(netInfo != null && netInfo.isConnected());
                            }
                        }, filter);


                    }

                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    subscriber.onNext(netInfo != null && netInfo.isConnected());


                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });


    }


    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    public static Observable<Boolean> isWifiEnabled(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {

                try {
                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    subscriber.onNext(wifiManager.isWifiEnabled());
                } catch (Throwable e) {
                    subscriber.onError(e);
                }

            }
        });
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE})
    public static Observable<Integer> wifiSpeed(final Context context) {

        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Exception {

                try {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();


                    if (networkInfo != null && networkInfo.isConnected()) {
                        final WifiManager wifiManager =
                                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                        if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                            subscriber.onNext(connectionInfo.getLinkSpeed());

                        }
                    } else {
                        subscriber.onNext(-1);
                    }


                } catch (Throwable e) {
                    subscriber.onError(e);
                }


            }
        });


    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET})
    public static Observable<String> networkType(final Context context) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> subscriber) {

                try {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork == null) {
                        subscriber.onNext("Unknown");

                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        subscriber.onNext("WIFI");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX) {
                        subscriber.onNext("WIMAX");

                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        if (manager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                            switch (manager.getNetworkType()) {


                                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                                    subscriber.onNext("Unknown");
                                    break;

                                case TelephonyManager.NETWORK_TYPE_EDGE:
                                case TelephonyManager.NETWORK_TYPE_GPRS:
                                case TelephonyManager.NETWORK_TYPE_CDMA:
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                    subscriber.onNext("2G");
                                    break;
                                // Cellular Data 3G
                                case TelephonyManager.NETWORK_TYPE_UMTS:
                                case TelephonyManager.NETWORK_TYPE_HSDPA:
                                case TelephonyManager.NETWORK_TYPE_HSPA:
                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                case TelephonyManager.NETWORK_TYPE_HSUPA:
                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                    subscriber.onNext("3G");
                                    break;
                                // Cellular Data 4G
                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    subscriber.onNext("4G");
                                    break;
                                // Cellular Data Unknown Generation
                                default:
                                    subscriber.onNext("Unknown");
                                    break;
                            }
                        }
                    }


                } catch (Throwable e) {
                    subscriber.onError(e);
                }


            }
        });
    }

}

