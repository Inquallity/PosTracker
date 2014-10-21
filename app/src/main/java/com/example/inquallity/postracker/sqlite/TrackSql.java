package com.example.inquallity.postracker.sqlite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.inquallity.postracker.BuildConfig;

public class TrackSql extends ContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static int mDbVersion = 1;
    public static final String DATABASE_NAME = "tracker.db";
    public static final String TABLE_NAME = "location_table";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private static TrackSqlHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new TrackSqlHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        final Cursor cursor =
                mHelper.getReadableDatabase()
                        .query(uri.getLastPathSegment(), columns, where, whereArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final long lastRowId = mHelper.getWritableDatabase()
                .insert(uri.getLastPathSegment(), BaseColumns._ID, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, lastRowId);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final int affectedRows = mHelper.getWritableDatabase()
                .delete(uri.getLastPathSegment(), where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final int affectedRows = mHelper.getWritableDatabase()
                .update(uri.getLastPathSegment(), values, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    private class TrackSqlHelper extends SQLiteOpenHelper {

        public TrackSqlHelper(Context context) {
            super(context, TrackSql.DATABASE_NAME, null, TrackSql.mDbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TrackSql.TABLE_NAME +
                    "(_id INTEGER PRIMARY KEY, position TEXT, latitude REAL, longitude REAL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TrackSql.TABLE_NAME + ";");
            onCreate(db);
        }
    }
}
