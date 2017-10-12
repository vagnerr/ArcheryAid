package com.vagnerr.android.archeryaid.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vagnerr.android.archeryaid.Utility;

/**
 * Created by Peter on 11/09/2017.
 */

public class ArcheryProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArcheryDbHelper mOpenHelper;


    static final int ARROW_COUNT            = 100;
    static final int ARROW_COUNT_HISTORY    = 110;

    static final int CLASSIFICATIONCONST    = 200;
    static final int SESSIONSTATECONST      = 210;
    static final int ARROWCONST             = 220;
    static final int RULESCONST             = 230;
    static final int TARGETTYPECONST        = 240;
    static final int ROUNDCONST             = 250;
    static final int ROUNDMAKEUP            = 260;


    //private static final SQLiteQueryBuilder sArcheryQueryBuilder;

    private static UriMatcher buildUriMatcher() {

        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = ArcheryContract.CONTENT_AUTHORITY;

        sURIMatcher.addURI(authority, ArcheryContract.PATH_ARROWCOUNT , ARROW_COUNT);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_ARROWCOUNTHISTORY + "/#", ARROW_COUNT_HISTORY);

        sURIMatcher.addURI(authority, ArcheryContract.PATH_CLASSIFICATIONCONST, CLASSIFICATIONCONST);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_SESSIONSTATECONST, SESSIONSTATECONST);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_ARROWCONST, ARROWCONST);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_RULESCONST, RULESCONST);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_TARGETTYPECONST, TARGETTYPECONST);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_ROUNDCONST, ROUNDCONST);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_ROUNDMAKEUP, ROUNDMAKEUP);

        return sURIMatcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ArcheryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case ARROW_COUNT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.ArrowCount.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ARROW_COUNT_HISTORY: {
                retCursor = getArrowCountHistory(uri, projection, sortOrder);
                break;
            }
            case CLASSIFICATIONCONST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.ClassificationConst.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SESSIONSTATECONST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.SessionStateConst.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ARROWCONST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.ArrowConst.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RULESCONST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.RulesConst.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TARGETTYPECONST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.TargetTypeConst.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ROUNDCONST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.RoundConst.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ROUNDMAKEUP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArcheryContract.RoundMakeup.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    private static final SQLiteQueryBuilder sArrowCountQueryBuilder;

    static{
        sArrowCountQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sArrowCountQueryBuilder.setTables(
                ArcheryContract.ArrowCount.TABLE_NAME
        );
    }    
    private static String sArrowHistoryDateSelection =
        ArcheryContract.ArrowCount.COLUMN_DATE + " >= ? ";

    private Cursor getArrowCountHistory(Uri uri, String[] projection, String sortOrder) {
        int days = ArcheryContract.ArrowCount.getDaysFromUri(uri);

        if ( days > 0 ) {
            long date = Utility.getJulianStartTime(-days);
            return sArrowCountQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    new String[]{"SUM(" + ArcheryContract.ArrowCount.COLUMN_COUNT + ")"},
                    sArrowHistoryDateSelection, //selection,
                    new String[]{Long.toString(date)}, //selectionArgs,
                    null, //groupby,
                    null,//having,
                    sortOrder,
                    null //limit
            );
        }
        else {
            // "0" means all time numbers
            return sArrowCountQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    new String[]{"SUM(" + ArcheryContract.ArrowCount.COLUMN_COUNT + ")"},
                    null,   //selection,
                    null,   //selectionArgs,
                    null,   //groupby,
                    null,   //having,
                    sortOrder,
                    null //limit
            );

        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARROW_COUNT:
                return ArcheryContract.ArrowCount.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ARROW_COUNT: {
                normaliseDate(values);
                long _id = db.insert(ArcheryContract.ArrowCount.TABLE_NAME, null, values);

                if( _id > 0 )
                    returnUri = ArcheryContract.ArrowCount.buildArrowCountUri(_id);   // TODO: Refactor to buildUri so that we can refactor to be more polymorphic ?
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CLASSIFICATIONCONST: {
                long _id = db.insert(ArcheryContract.ClassificationConst.TABLE_NAME, null, values);
                if( _id > 0 )
                    returnUri = ArcheryContract.ClassificationConst.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new android.database.SQLException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int inserted = 0;
        String table;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CLASSIFICATIONCONST:
                table = ArcheryContract.ClassificationConst.TABLE_NAME;
                break;
            case SESSIONSTATECONST:
                table = ArcheryContract.SessionStateConst.TABLE_NAME;
                break;
            case ARROWCONST:
                table = ArcheryContract.ArrowConst.TABLE_NAME;
                break;
            case RULESCONST:
                table = ArcheryContract.RulesConst.TABLE_NAME;
                break;
            case TARGETTYPECONST:
                table = ArcheryContract.TargetTypeConst.TABLE_NAME;
                break;
            case ROUNDCONST:
                table = ArcheryContract.RoundConst.TABLE_NAME;
                break;
            case ROUNDMAKEUP:
                table = ArcheryContract.RoundMakeup.TABLE_NAME;
                break;
            default:
                throw new android.database.SQLException("Unknown/unsupported uri for bulkInsert:" + uri);
        }

        db.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long _id = db.insertOrThrow(table,null,cv);
                if ( _id <= 0 ){
                    throw new SQLException("Failed to insert row into " +uri+" Values:" + cv);
                }
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri,null);
            inserted = values.length;
        }
        finally {
            db.endTransaction();
        }

        return inserted;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if ( null == selection ) selection = "1";
        int rowsDeleted = 0;
        switch (match){
            case ARROW_COUNT: {
                rowsDeleted = db.delete(ArcheryContract.ArrowCount.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CLASSIFICATIONCONST:
                rowsDeleted = db.delete(ArcheryContract.ClassificationConst.TABLE_NAME, selection, selectionArgs);
                break;
            case SESSIONSTATECONST:
                rowsDeleted = db.delete(ArcheryContract.SessionStateConst.TABLE_NAME, selection, selectionArgs);
                break;
            case ARROWCONST:
                rowsDeleted = db.delete(ArcheryContract.ArrowConst.TABLE_NAME, selection, selectionArgs);
                break;
            case RULESCONST:
                rowsDeleted = db.delete(ArcheryContract.RulesConst.TABLE_NAME, selection, selectionArgs);
                break;
            case TARGETTYPECONST:
                rowsDeleted = db.delete(ArcheryContract.TargetTypeConst.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUNDCONST:
                rowsDeleted = db.delete(ArcheryContract.RoundConst.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUNDMAKEUP:
                rowsDeleted = db.delete(ArcheryContract.RoundMakeup.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " MTCH: " + match);
        }
        if (rowsDeleted != 0 || selection == null){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    private void normaliseDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ArcheryContract.ArrowCount.COLUMN_DATE)) {
            long dateValue = values.getAsLong(ArcheryContract.ArrowCount.COLUMN_DATE);
            values.put(ArcheryContract.ArrowCount.COLUMN_DATE, ArcheryContract.normaliseDate(dateValue));
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if ( null == selection ) selection = "1";
        int rowsUpdated = 0;
        switch (match){
            case ARROW_COUNT: {
                rowsUpdated = db.update(ArcheryContract.ArrowCount.TABLE_NAME, values, selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " MTCH:" + match);
        }
        if ( rowsUpdated != 0 || selection == null){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
