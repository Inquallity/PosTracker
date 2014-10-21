package com.example.inquallity.postracker.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackSqlHelper extends SQLiteOpenHelper {

    public TrackSqlHelper(Context context) {
        super(context, TrackSql.DATABASE_NAME, null, TrackSql.DATABASE_VERSION);
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

    public void resetTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TrackSql.TABLE_NAME + ";");
        onCreate(db);
    }
}

