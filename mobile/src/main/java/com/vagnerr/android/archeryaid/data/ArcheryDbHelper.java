package com.vagnerr.android.archeryaid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.vagnerr.android.archeryaid.data.ArcheryContract.*;

/**
 * Created by Peter on 11/09/2017.
 * Database creation / update routines
 */

public class ArcheryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    private final String LOG_TAG = ArcheryDbHelper.class.getSimpleName();

    static final String DATABASE_NAME = "archeryaid.db";

    public ArcheryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "onCreate: start");
        createArrowCount(sqLiteDatabase);
        createRoundDefinition(sqLiteDatabase);
        createOtherConst(sqLiteDatabase);
        createShootingSession(sqLiteDatabase);
        Log.v(LOG_TAG, "onCreate: end");

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "onUpgrade: start");

        // Version 2: was first version, just the arrow counter
        //            theoretically redundant as there was no version 1, but here for completeness
        if (oldVersion < 2) {
            Log.v(LOG_TAG, "   : <2");

            createArrowCount(sqLiteDatabase);
        }
        // Version 3: Added first cut of Round Definition and Shooting Session tables
        if (oldVersion < 3) {
            Log.v(LOG_TAG, "   : <3");
            createRoundDefinition(sqLiteDatabase);
            createOtherConst(sqLiteDatabase);
            createShootingSession(sqLiteDatabase);
        }
        Log.v(LOG_TAG, "onUpgrade: end");

    }


    private void createRoundDefinition(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createRoundDefinition: start");

        createTargetTypeConst(sqLiteDatabase);
        createRulesConst(sqLiteDatabase);
        createRoundConst(sqLiteDatabase);
        createRoundMakeup(sqLiteDatabase);
        Log.v(LOG_TAG, "createRoundDefinition: end");
    }

    private void createOtherConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createOtherConst: start");
        createArrowConst(sqLiteDatabase);
        createClassificationConst(sqLiteDatabase);
        createSessionStateConst(sqLiteDatabase);
    }

    private void createShootingSession(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createShootingSession: start");
        createSession(sqLiteDatabase);
        createEnd(sqLiteDatabase);
        createSessionMakeup(sqLiteDatabase);
    }


    private void createArrowConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createArrowConst: start");
        final String SQL_CREATE = "CREATE TABLE " + ArrowConst.TABLE_NAME + " (" +
                ArrowConst._ID + " INTEGER PRIMARY KEY, " +
                ArrowConst.COLUMN_NAME + " TEXT NOT NULL, " +
                ArrowConst.COLUMN_VALUE + " INTEGER NOT NULL" +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    private void createClassificationConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createClassificationConst: start");
        final String SQL_CREATE = "CREATE TABLE " + ClassificationConst.TABLE_NAME + " (" +
                ClassificationConst._ID + " INTEGER PRIMARY KEY, " +
                ClassificationConst.COLUMN_NAME + " TEXT NOT NULL " +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    private void createSessionStateConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createSessionStateConst: start");
        final String SQL_CREATE = "CREATE TABLE " + SessionStateConst.TABLE_NAME + " (" +
                SessionStateConst._ID + " INTEGER PRIMARY KEY, " +
                SessionStateConst.COLUMN_NAME + " TEXT NOT NULL, " +
                SessionStateConst.COLUMN_OFFICIAL + " INTEGER NOT NULL "+

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);
    }


    private void createSession(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createSession: start");
        final String SQL_CREATE = "CREATE TABLE " + Session.TABLE_NAME + " (" +
                Session._ID + " INTEGER PRIMARY KEY, " +
                Session.COLUMN_DATE + " INTEGER NOT NULL, " +
                Session.COLUMN_DATE_FINISHED + " INTEGER, " +   // Null if session was recorded simply as a start with now duration timings
                Session.COLUMN_WEATHER + " TEXT, " +
                Session.COLUMN_ROUND_ID + " INTEGER NOT NULL, " +
                Session.COLUMN_SESSION_STATE_ID + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_SCORE + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_X + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_10 + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_GOLD + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_ARROWS + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_HITS + " INTEGER NOT NULL, " +
                Session.COLUMN_TOTAL_SIGHTERS + " INTEGER NOT NULL, " +
                Session.COLUMN_ROUND_COMPLETE + " INTEGER NOT NULL, " +
                Session.COLUMN_CLASSIFICATION_ID + " INTEGER, " +     // For when I get clasification tables
                Session.COLUMN_HANDICAP + " INTEGER, " +              // For when I get handicap tables

                " FOREIGN KEY (" + Session.COLUMN_ROUND_ID + ") REFERENCES " +
                RoundConst.TABLE_NAME + " (" + RoundConst._ID + "), " +
                " FOREIGN KEY (" + Session.COLUMN_SESSION_STATE_ID + ") REFERENCES " +
                SessionStateConst.TABLE_NAME + " (" + SessionStateConst._ID + "), " +
                " FOREIGN KEY (" + Session.COLUMN_CLASSIFICATION_ID + ") REFERENCES " +
                ClassificationConst.TABLE_NAME + " (" + ClassificationConst._ID + ") " +


                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);

    }

    private void createEnd(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createEnd: start");
        final String SQL_CREATE = "CREATE TABLE " + End.TABLE_NAME + " (" +
                End._ID + " INTEGER PRIMARY KEY, " +
                End.COLUMN_SESSION_ID + " INTEGER NOT NULL, " +
                End.COLUMN_END + " INTEGER NOT NULL, " +
                End.COLUMN_ARROW1 + " INTEGER, " +
                End.COLUMN_ARROW2 + " INTEGER, " +
                End.COLUMN_ARROW3 + " INTEGER, " +
                End.COLUMN_ARROW4 + " INTEGER, " +
                End.COLUMN_ARROW5 + " INTEGER, " +
                End.COLUMN_ARROW6 + " INTEGER, " +

                " FOREIGN KEY (" + End.COLUMN_SESSION_ID + ") REFERENCES " +
                Session.TABLE_NAME + " (" + Session._ID + "), " +

                " FOREIGN KEY (" + End.COLUMN_ARROW1 + ") REFERENCES " +
                ArrowConst.TABLE_NAME + " (" + ArrowConst._ID + "), " +
                " FOREIGN KEY (" + End.COLUMN_ARROW2 + ") REFERENCES " +
                ArrowConst.TABLE_NAME + " (" + ArrowConst._ID + "), " +
                " FOREIGN KEY (" + End.COLUMN_ARROW3 + ") REFERENCES " +
                ArrowConst.TABLE_NAME + " (" + ArrowConst._ID + "), " +
                " FOREIGN KEY (" + End.COLUMN_ARROW4 + ") REFERENCES " +
                ArrowConst.TABLE_NAME + " (" + ArrowConst._ID + "), " +
                " FOREIGN KEY (" + End.COLUMN_ARROW5 + ") REFERENCES " +
                ArrowConst.TABLE_NAME + " (" + ArrowConst._ID + "), " +
                " FOREIGN KEY (" + End.COLUMN_ARROW6 + ") REFERENCES " +
                ArrowConst.TABLE_NAME + " (" + ArrowConst._ID + ") " +


                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);

    }

    private void createSessionMakeup(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createSessionMakeup: start");
        final String SQL_CREATE = "CREATE TABLE " + SessionMakeup.TABLE_NAME + " (" +
                SessionMakeup._ID + " INTEGER PRIMARY KEY, " +
                SessionMakeup.COLUMN_SESSION_ID + " INTEGER NOT NULL, " +
                SessionMakeup.COLUMN_TARGET_TYPE_ID + " INTEGER NOT NULL, " +
                SessionMakeup.COLUMN_END_COUNT + " INTEGER NOT NULL, " +
                SessionMakeup.COLUMN_DISTANCE_ORDER + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + SessionMakeup.COLUMN_SESSION_ID + ") REFERENCES " +
                Session.TABLE_NAME + " (" + Session._ID + ")," +
                " FOREIGN KEY (" + SessionMakeup.COLUMN_TARGET_TYPE_ID + ") REFERENCES " +
                TargetTypeConst.TABLE_NAME + " (" + TargetTypeConst._ID + ")" +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);
    }


    private void createTargetTypeConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createTargetTypeConst: start");
        final String SQL_CREATE = "CREATE TABLE " + TargetTypeConst.TABLE_NAME + " (" +
                TargetTypeConst._ID + " INTEGER PRIMARY KEY, " +
                TargetTypeConst.COLUMN_DISTANCE + " INTEGER NOT NULL, " +
                TargetTypeConst.COLUMN_UNITS + " TEXT NOT NULL, " +             // TODO: Do I want this to be an ID?
                TargetTypeConst.COLUMN_TARGET_SIZE + " INTEGER NOT NULL, " +    // In CM TODO: an ID?
                TargetTypeConst.COLUMN_ZONE_COUNT + " INTEGER NOT NULL " +      // TODO: May want to change this to an ID ( const table specific for listing zones ) or a string listing available zones

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    private void createRulesConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createRulesConst: start");
        final String SQL_CREATE = "CREATE TABLE " + RulesConst.TABLE_NAME + " (" +
                RulesConst._ID + " INTEGER PRIMARY KEY, " +
                RulesConst.COLUMN_NAME + " TEXT NOT NULL " +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);
    }


    private void createRoundConst(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createRoundConst: start");
        final String SQL_CREATE = "CREATE TABLE " + RoundConst.TABLE_NAME + " (" +
                RoundConst._ID + " INTEGER PRIMARY KEY, " +
                RoundConst.COLUMN_NAME + " TEXT NOT NULL, " +
                RoundConst.COLUMN_OFFICIAL + " INTEGER NOT NULL, " + // Boolean
                RoundConst.COLUMN_CUSTOM + " INTEGER NOT NULL, " + // Boolean
                RoundConst.COLUMN_RULES_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + RoundConst.COLUMN_RULES_ID + ") REFERENCES " +
                    RulesConst.TABLE_NAME + " (" + RulesConst._ID + ")" +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);

    }

    private void createRoundMakeup(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createRoundMakeup: start");
        final String SQL_CREATE = "CREATE TABLE " + RoundMakeup.TABLE_NAME + " (" +
                RoundMakeup._ID + " INTEGER PRIMARY KEY, " +
                RoundMakeup.COLUMN_ROUND_ID + " INTEGER NOT NULL, " +
                RoundMakeup.COLUMN_TARGET_TYPE_ID + " INTEGER NOT NULL, " +
                RoundMakeup.COLUMN_END_COUNT + " INTEGER NOT NULL, " +
                RoundMakeup.COLUMN_DISTANCE_ORDER + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + RoundMakeup.COLUMN_ROUND_ID + ") REFERENCES " +
                    RoundConst.TABLE_NAME + " (" + RoundConst._ID + ") ," +
                " FOREIGN KEY (" + RoundMakeup.COLUMN_TARGET_TYPE_ID + ") REFERENCES " +
                    TargetTypeConst.TABLE_NAME + " (" + TargetTypeConst._ID + ")" +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE);

    }

    private void createArrowCount(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "createArrowCount: start");
        final String SQL_CREATE_ARROWCOUNT_TABLE = "CREATE TABLE " + ArrowCount.TABLE_NAME + " (" +
                ArrowCount._ID + " INTEGER PRIMARY KEY, " +
                ArrowCount.COLUMN_DATE + " INTEGER UNIQUE NOT NULL, "+
                ArrowCount.COLUMN_COUNT + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_ARROWCOUNT_TABLE);
    }



}
