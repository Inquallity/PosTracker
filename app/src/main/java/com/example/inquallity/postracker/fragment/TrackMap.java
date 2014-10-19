package com.example.inquallity.postracker.fragment;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.inquallity.postracker.sqlite.TrackSql;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inquallity on 14.10.2014.
 */
public class TrackMap extends MapFragment implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private List<LatLng> mLocationList = null;
    float LINE_WIDTH = 3.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(1, Bundle.EMPTY, this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMap().setMyLocationEnabled(true);
        getMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getApplicationContext(), TrackSql.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mLocationList = new ArrayList<LatLng>(cursor.getCount());
            do {
                mLocationList.add(new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")),
                        cursor.getDouble(cursor.getColumnIndex("longitude"))));
            } while (cursor.moveToNext());
        }

        if (mLocationList.isEmpty()){
            Log.d("TAG", "List of locations is empty");
        }
        getMap().addPolyline(new PolylineOptions()
                .addAll(mLocationList)
                .width(LINE_WIDTH)
                .color(getResources().getColor(android.R.color.holo_green_light)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
