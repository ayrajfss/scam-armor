package com.example.scamarmor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 2; // Increment the database version

    // Table name
    public static final String TABLE_COMMENTS = "comments";

    // Table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_TIMESTAMP = "timestamp"; // New column for timestamp

    // Create table SQL query
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_COMMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COMMENT + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " INTEGER NOT NULL);"; // Add timestamp column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_COMMENTS + " ADD COLUMN " + COLUMN_TIMESTAMP + " INTEGER NOT NULL DEFAULT 0;");
        }
    }
}
