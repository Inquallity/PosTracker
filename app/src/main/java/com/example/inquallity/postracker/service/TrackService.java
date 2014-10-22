package com.example.inquallity.postracker.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.example.inquallity.postracker.sqlite.TrackSql;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "TAG_OF_SERVICE";
    private GoogleApiClient mGoogleApi;
    private LocationRequest mRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        if (mGoogleApi == null) {
            mGoogleApi = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mRequest = LocationRequest.create();
        mRequest.setFastestInterval(DateUtils.MINUTE_IN_MILLIS * 8);
        mRequest.setInterval(DateUtils.MINUTE_IN_MILLIS * 20);
        mGoogleApi.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "On starting...");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mGoogleApi.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApi, this);
            Log.d(TAG, "GoogleApiService has been stopped");
            mGoogleApi.disconnect();
            Log.d(TAG, "Service has been stopped");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        ContentValues cv = new ContentValues();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String textAddr = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            Log.d("TAG", "EXCEPTION ");
            e.printStackTrace();
        }
        if (addresses == null) {
            Log.d("TAG", "Addresses is empty");

        } else {
            Address address = addresses.get(0);
            textAddr = address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getCountryName();
        }
        if (location.getAccuracy() < 100.f) {
            cv.put("position", textAddr);
            cv.put("latitude", location.getLatitude());
            cv.put("longitude", location.getLongitude());
            getContentResolver().insert(TrackSql.URI, cv);
        } else {
            Calendar calendar = Calendar.getInstance();
            cv.put("position", String.valueOf(calendar.getTime()) + "::" + "Дикий пиздец по координатам --->");
            cv.put("latitude", location.getLatitude());
            cv.put("longitude", location.getLongitude());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        retryConnecting();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("TAG", "GoogleApi connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi, mRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        retryConnecting();
    }

    private void retryConnecting() {
        if (!mGoogleApi.isConnecting()) {
            mGoogleApi.connect();
        }
    }
}
