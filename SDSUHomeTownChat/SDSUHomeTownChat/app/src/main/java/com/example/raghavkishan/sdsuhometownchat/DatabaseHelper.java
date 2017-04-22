package com.example.raghavkishan.sdsuhometownchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by raghavkishan on 4/15/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="people.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+"PEOPLE"+" ("
                +"id"+" INTEGER PRIMARY KEY,"
                +"nickname"+" TEXT,"
                +"city"+" TEXT,"
                +"longitude"+" DOUBLE,"
                +"state"+" TEXT,"
                +"year"+" INTEGER,"
                +"latitude"+" DOUBLE,"
                +"timestamp"+" TEXT,"
                +"country"+" TEXT"
                +");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
