package com.vagnerr.android.archeryaid.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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


    static final int ARROW_COUNT = 100;
    static final int ARROW_COUNT_HISTORY = 110;

    //private static final SQLiteQueryBuilder sArcheryQueryBuilder;

    private static UriMatcher buildUriMatcher() {

        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = ArcheryContract.CONTENT_AUTHORITY;
        sURIMatcher.addURI(authority, ArcheryContract.PATH_ARROWCOUNT , ARROW_COUNT);
        sURIMatcher.addURI(authority, ArcheryContract.PATH_ARROWCOUNTHISTORY + "/#", ARROW_COUNT_HISTORY);
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
                    returnUri = ArcheryContract.ArrowCount.buildArrowCountUri(_id);
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
