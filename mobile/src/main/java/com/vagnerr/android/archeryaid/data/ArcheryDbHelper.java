package com.vagnerr.android.archeryaid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vagnerr.android.archeryaid.data.ArcheryContract.*;

/**
 * Created by Peter on 11/09/2017.
 */

public class ArcheryDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "archeryaid.db";

    public ArcheryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ARROWCOUNT_TABLE = "CREATE TABLE " + ArrowCount.TABLE_NAME + " (" +
                ArrowCount._ID + " INTEGER PRIMARY KEY, " +
                ArrowCount.COLUMN_DATE + " INTEGER UNIQUE NOT NULL, "+
                ArrowCount.COLUMN_COUNT + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_ARROWCOUNT_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // In development we don't need to retain data
        // TODO: rewrite this on (2nd) release.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArrowCount.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
