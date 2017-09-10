package com.vagnerr.android.archeryaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Peter on 10/09/2017.
 */

public class Utility {
    public static int getPrefferedVolley(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return 1; //prefs.getInt()
    }

    public static String getFormattedArrowCount(Context context, int arrow_count) {
        int format = R.string.format_arrow_click_count;
        return String.format(context.getString(format), arrow_count);
    }
}
