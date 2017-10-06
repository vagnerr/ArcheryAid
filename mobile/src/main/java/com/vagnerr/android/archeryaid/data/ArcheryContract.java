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
    // Generic
    public static final String CONTENT_AUTHORITY = "com.vagnerr.android.archeryaid";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Arrow Counting: ArrowCount
    public static final String PATH_ARROWCOUNT = "arrowcount";
    public static final String PATH_ARROWCOUNTHISTORY = "arrowcount/history";

    // Round Definition:
    private static final String PATH_ROUNDMAKEUP        = "roundmakeup";
    private static final String PATH_TARGETTYPECONST    = "targettypeconst";
    private static final String PATH_ROUNDCONST         = "roundconst";
    private static final String PATH_RULESCONST         = "rulesconst";

    // Shooting Session:
    private static final String PATH_END            = "end";
    private static final String PATH_SESSION        = "session";
    private static final String PATH_SESSIONMAKEUP  = "sessionmakeup";


    // Other Constants:
    private static final String PATH_ARROWCONST             = "arrowconst";
    private static final String PATH_CLASSIFICATIONCONST    = "classificationconst";
    private static final String PATH_SESSIONSTATECONST      = "sessionstateconst";

    // To make it easy to query for the exact date items, we normalise dates on certain data
    // ( eg arrow counts per day ) that go into the database to the start of the the Julian day at UTC.
    public static long normaliseDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }



/*

   #                                    #####
  # #   #####  #####   ####  #    #    #     #  ####  #    # #    # ##### # #    #  ####
 #   #  #    # #    # #    # #    #    #       #    # #    # ##   #   #   # ##   # #    #
#     # #    # #    # #    # #    #    #       #    # #    # # #  #   #   # # #  # #
####### #####  #####  #    # # ## #    #       #    # #    # #  # #   #   # #  # # #  ###
#     # #   #  #   #  #    # ##  ##    #     # #    # #    # #   ##   #   # #   ## #    #
#     # #    # #    #  ####  #    #     #####   ####   ####  #    #   #   # #    #  ####

*/
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

/*
######                                 ######
#     #  ####  #    # #    # #####     #     # ###### ###### # #    # # ##### #  ####  #    #
#     # #    # #    # ##   # #    #    #     # #      #      # ##   # #   #   # #    # ##   #
######  #    # #    # # #  # #    #    #     # #####  #####  # # #  # #   #   # #    # # #  #
#   #   #    # #    # #  # # #    #    #     # #      #      # #  # # #   #   # #    # #  # #
#    #  #    # #    # #   ## #    #    #     # #      #      # #   ## #   #   # #    # #   ##
#     #  ####   ####  #    # #####     ######  ###### #      # #    # #   #   #  ####  #    #
 */

    public static final class RoundMakeup implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUNDMAKEUP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUNDMAKEUP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUNDMAKEUP;

        public static final String TABLE_NAME               = "round_makeup";
        public static final String COLUMN_ROUND_ID          = "round_id";               // FK: round_const
        public static final String COLUMN_TARGET_TYPE_ID    = "target_type_id";         // FK: target_type_const
        public static final String COLUMN_END_COUNT         = "end_count";
        public static final String COLUMN_DISTANCE_ORDER    = "distance_order";


    }

    public static final class TargetTypeConst implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TARGETTYPECONST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TARGETTYPECONST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TARGETTYPECONST;

        public static final String TABLE_NAME               = "target_type_const";
        public static final String COLUMN_DISTANCE          = "distance";
        public static final String COLUMN_UNITS             = "units";       // Or units_id as a FK?
        public static final String COLUMN_TARGET_SIZE       = "target_size";
        public static final String COLUMN_ZONE_COUNT        = "zone_count";


    }


    public static final class RoundConst implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUNDCONST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUNDCONST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUNDCONST;

        public static final String TABLE_NAME       = "round_const";
        public static final String COLUMN_NAME      = "name";
        public static final String COLUMN_OFFICIAL  = "official";
        public static final String COLUMN_CUSTOM    = "custom";
        public static final String COLUMN_RULES_ID  = "rules_id";       // FK: rules_const



    }

    public static final class RulesConst implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RULESCONST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RULESCONST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RULESCONST;

        public static final String TABLE_NAME       = "rules_const";
        public static final String COLUMN_NAME      = "name";


    }


/*
 #####                                                 #####
#     # #    #  ####   ####  ##### # #    #  ####     #     # ######  ####   ####  #  ####  #    #
#       #    # #    # #    #   #   # ##   # #    #    #       #      #      #      # #    # ##   #
 #####  ###### #    # #    #   #   # # #  # #          #####  #####   ####   ####  # #    # # #  #
      # #    # #    # #    #   #   # #  # # #  ###          # #           #      # # #    # #  # #
#     # #    # #    # #    #   #   # #   ## #    #    #     # #      #    # #    # # #    # #   ##
 #####  #    #  ####   ####    #   # #    #  ####      #####  ######  ####   ####  #  ####  #    #
 */

    public static final class End implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_END).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_END;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_END;

        public static final String TABLE_NAME           = "end";
        public static final String COLUMN_SESSION_ID    = "session_id";     // FK: session
        public static final String COLUMN_END           = "end";
        public static final String COLUMN_ARROW1        = "arrow1";
        public static final String COLUMN_ARROW2        = "arrow2";
        public static final String COLUMN_ARROW3        = "arrow3";
        public static final String COLUMN_ARROW4        = "arrow4";
        public static final String COLUMN_ARROW5        = "arrow5";
        public static final String COLUMN_ARROW6        = "arrow6";


    }

    public static final class Session implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSION;

        public static final String TABLE_NAME               = "session";
        public static final String COLUMN_DATE              = "date";
        public static final String COLUMN_DATE_FINISHED     = "date_finished";
        public static final String COLUMN_WEATHER           = "weather";
        public static final String COLUMN_ROUND_ID          = "round_id";           // FK: round_const
        public static final String COLUMN_SESSION_STATE_ID  = "session_state_id";   // FK: session_state_const
        public static final String COLUMN_TOTAL_SCORE       = "total_score";
        public static final String COLUMN_TOTAL_X           = "total_x";
        public static final String COLUMN_TOTAL_10          = "total_10";
        public static final String COLUMN_TOTAL_GOLD        = "total_gold";
        public static final String COLUMN_TOTAL_ARROWS      = "total_arrows";
        public static final String COLUMN_TOTAL_HITS        = "total_hits";
        public static final String COLUMN_TOTAL_SIGHTERS    = "total_sighters";
        public static final String COLUMN_ROUND_COMPLETE    = "round_complete";
        public static final String COLUMN_CLASSIFICATION_ID = "classification_id";  // FK: classification_const
        public static final String COLUMN_HANDICAP          = "handicap";


    }

    public static final class SessionMakeup implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSIONMAKEUP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSIONMAKEUP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSIONMAKEUP;

        public static final String TABLE_NAME               = "session_makeup";
        public static final String COLUMN_SESSION_ID        = "session_id";     // FK: session
        public static final String COLUMN_TARGET_TYPE_ID    = "target_type_id"; // FK: target_type_const
        public static final String COLUMN_END_COUNT         = "end_count";
        public static final String COLUMN_DISTANCE_ORDER    = "distance_order";


    }



/*
#######                                #####
#     # ##### #    # ###### #####     #     #  ####  #    #  ####  #####   ##   #    # #####  ####
#     #   #   #    # #      #    #    #       #    # ##   # #        #    #  #  ##   #   #   #
#     #   #   ###### #####  #    #    #       #    # # #  #  ####    #   #    # # #  #   #    ####
#     #   #   #    # #      #####     #       #    # #  # #      #   #   ###### #  # #   #        #
#     #   #   #    # #      #   #     #     # #    # #   ## #    #   #   #    # #   ##   #   #    #
#######   #   #    # ###### #    #     #####   ####  #    #  ####    #   #    # #    #   #    ####
 */

    public static final class ArrowConst implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARROWCONST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARROWCONST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARROWCONST;

        public static final String TABLE_NAME       = "arrow_const";
        public static final String COLUMN_NAME      = "name";
        public static final String COLUMN_VALUE     = "value";
    }

    public static final class ClassificationConst implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSIFICATIONCONST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASSIFICATIONCONST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASSIFICATIONCONST;

        public static final String TABLE_NAME       = "classification_const";
        public static final String COLUMN_NAME      = "name";
    }

    public static final class SessionStateConst implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSIONSTATECONST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSIONSTATECONST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSIONSTATECONST;

        public static final String TABLE_NAME       = "session_state_const";
        public static final String COLUMN_NAME      = "name";
        public static final String COLUMN_OFFICIAL  = "official";
    }


}
