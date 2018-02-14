package com.example.android.trackyourrun.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.trackyourrun.data.RunContract.RunEntry;

/**
 * Database helper for the app. Manages database creation and version management.
 */
public class RunDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "runs.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Context passed in through the constructor.
     */
    private Context mContext;

    /**
     * Constructs a new instance of {@link RunDbHelper}.
     *
     * @param context of the app.
     */
    public RunDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the runs table
        String SQL_CREATE_RUNS_TABLE = "CREATE TABLE " + RunEntry.TABLE_NAME + " ("
                + RunEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RunEntry.COLUMN_RUN_DATE + " TEXT NOT NULL, "
                + RunEntry.COLUMN_RUN_TIME + " TEXT NOT NULL, "
                + RunEntry.COLUMN_RUN_DURATION + " TEXT NOT NULL, "
                + RunEntry.COLUMN_RUN_DISTANCE + " REAL NOT NULL DEFAULT 0,"
                + RunEntry.COLUMN_RUN_DISTANCE_UNIT + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_RUNS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's just recreate the database.
        mContext.deleteDatabase(RunEntry.TABLE_NAME);
        onCreate(db);
    }
}
