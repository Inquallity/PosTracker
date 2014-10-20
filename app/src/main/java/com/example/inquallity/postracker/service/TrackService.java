package com.example.inquallity.postracker.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.inquallity.postracker.sqlite.TrackSql;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
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
        Log.d(TAG, "Service has been started");
        Toast.makeText(getApplicationContext(), "Service has been started", Toast.LENGTH_LONG).show();

        if (mGoogleApi == null) {
            mGoogleApi = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mRequest = LocationRequest.create();
        mRequest.setInterval(5000);
        mRequest.setFastestInterval(1000);
        mGoogleApi.connect();
    }

    //Обработка некорректного завершения сервиса
    //Чаще всего используют, чтобы создать и запустить отдельный поток для сервиса
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "On starting...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "I am a service and i doing that shit into Log.d");
        /*stopSelf(startId);*/

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service has been stopped");
//        Toast.makeText(getApplicationContext(), "Service has been stopped", Toast.LENGTH_LONG).show();

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
            Log.d("TAG", "Addresses is null");

        } else if (addresses.size() == 0) {
            Log.d("TAG", "List of the addresses is empty");
        } else {
            Address address = addresses.get(0);
            textAddr = address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getCountryName();
        }
        cv.put("position", textAddr);
        cv.put("latitude", location.getLatitude());
        cv.put("longitude", location.getLongitude());
        getContentResolver().insert(TrackSql.URI, cv);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        retryConnecting();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("TAG", "GoogleApi connected");
        /*final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi);
        if (location != null) {
            onLocationChanged(location);
        }*/
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
