package com.vagnerr.android.archeryaid.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Peter on 11/09/2017.
 */

public class ArcheryContract {
    public static final String CONTENT_AUTHORITY = "com.vagnerr.android.archeryaid";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ARROWCOUNT = "arrowcount";
    public static final String PATH_ARROWCOUNTHISTORY = "arrowcount/history";
    //public static final String PATH_LOCATION = "location";


    // To make it easy to query for the exact date items, we normalise dates on certain data
    // ( eg arrow counts per day ) that go into the database to the start of the the Julian day at UTC.
    public static long normaliseDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }




    /*  Arrow counts table recording simple number of arrows per day, Current count
        will be the record for "today's date"
     */
    public static final class ArrowCount implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARROWCOUNT).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARROWCOUNT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARROWCOUNT;

        public static final String TABLE_NAME = "arrow_count";
        public static final String COLUMN_DATE  = "date";
        public static final String COLUMN_COUNT = "count";

        public static final String HISTORY = "history";

        public static Uri buildArrowCountUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildArrowCountUriBase(){
            return (Uri)CONTENT_URI;
        }

        public static int getDaysFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));

        }

        public static Uri buildArrowCountHistoryUri(int i) {
            return CONTENT_URI
                        .buildUpon()
                        .appendPath(HISTORY)
                        .appendPath(Integer.toString(i))
                        .build();

        }
    }


}
