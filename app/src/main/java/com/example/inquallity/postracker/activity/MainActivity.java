package com.example.inquallity.postracker.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.inquallity.postracker.R;
import com.example.inquallity.postracker.fragment.TrackCollect;
import com.example.inquallity.postracker.service.TrackService;
import com.example.inquallity.postracker.sqlite.TrackSql;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements
        LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApi;
    private LocationRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        startService(new Intent(getApplicationContext(), TrackService.class));
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame, new TrackCollect())
                    .commit();
        }

        startService(new Intent(getApplicationContext(), TrackService.class));

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

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("TAG", "GoogleApi connected");
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi);
        if (location != null) {
            onLocationChanged(location);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi, mRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        retryConnecting();
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
            textAddr = address.getLocality() + ", " + address.getCountryName();
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

    private void retryConnecting() {
        if (!mGoogleApi.isConnecting()) {
            mGoogleApi.connect();
        }
    }
}

