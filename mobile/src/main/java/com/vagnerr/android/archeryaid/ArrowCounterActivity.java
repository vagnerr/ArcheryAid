package com.vagnerr.android.archeryaid;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vagnerr.android.archeryaid.data.ArcheryContract;
import com.vagnerr.android.archeryaid.data.ArcheryContract.ArrowCount;

public class ArrowCounterActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] COUNT_COLUMNS = {
            ArrowCount._ID,
            ArrowCount.COLUMN_DATE,
            ArrowCount.COLUMN_COUNT
    };
    public static final int COL_ARROWCOUNT_ID       = 0;
    public static final int COL_ARROWCOUNT_DATE     = 1;
    public static final int COL_ARROWCOUNT_COUNT    = 2;


    private static  final int ARROWCOUNT_LOADER=0;

    int mVolley;

    private final String LOG_TAG = ArrowCounterActivity.class.getSimpleName();
    private ToggleButton mVolButton1;
    private ToggleButton mVolButton3;
    private ToggleButton mVolButton6;
    private TextView mArrowDisp;

    private int arrow_count = 0;

    private Uri mUri;
    private static final String sArrowCountDaySelection =
            ArrowCount.TABLE_NAME+"."+ ArrowCount.COLUMN_DATE+ " = ? ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: we want a "current" choice as well as the default (db backed) or we will reset when the activity is recreated.
        mVolley = Utility.getPrefferedVolley(this);

        setContentView(R.layout.activity_arrow_counter);

        // NOTE: Must come after setContentView else all will be null :-)
        mVolButton1 = findViewById(R.id.toggleVolley1);
        mVolButton3 = findViewById(R.id.toggleVolley3);
        mVolButton6 = findViewById(R.id.toggleVolley6);
        mArrowDisp = findViewById(R.id.text_arrowcount_total);
        setActiveVolley(mVolley);
        getSupportLoaderManager().initLoader(ARROWCOUNT_LOADER,null,this);

        mVolButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveVolley(1);
            }
        });
        mVolButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveVolley(3);
            }
        });
        mVolButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveVolley(6);
            }
        });

        Log.v(LOG_TAG, "Volley size [" + mVolley + "]");

        findViewById(R.id.button_countClick)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        incrementArrowCount(mVolley);
                    }
                });

        findViewById(R.id.button_countUndo)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        incrementArrowCount(-mVolley);
                    }
                });

        // If this were relivent we would be pulling out uri out of a Bundle / saved instance state
        // but for this activity we are only interested in one thing which is the arrow
        // count for "today" so we can manufacture our own simple Uri, working with
        // todays date later
        // TODO: maybe have an explicit TODAY uri returning a single count item
        mUri = ArcheryContract.ArrowCount.buildArrowCountUriBase();   //   ArcheryContract.ArrowCount.CONTENT_URI;

        mArrowDisp.setText(Utility.getFormattedArrowCount(this, arrow_count));

    }

    private void incrementArrowCount(int count) {
        arrow_count = arrow_count + count;
        if ( arrow_count < 0 ){
            // no lower than zero
            arrow_count = 0;
        }

        mArrowDisp.setText(Utility.getFormattedArrowCount(this, arrow_count));
        Log.v(LOG_TAG, "Arrow count: " + arrow_count);

        // Save to DB
        ContentValues content = new ContentValues();
        content.put(ArrowCount.COLUMN_DATE, Utility.getJulianStartTime(0));
        content.put(ArrowCount.COLUMN_COUNT, arrow_count);
        ContentResolver cp = this.getContentResolver();

        int rows = cp.update(    ArrowCount.CONTENT_URI,
                            content,
                            sArrowCountDaySelection,
                            new String[]{Long.toString(Utility.getJulianStartTime(0))}
                        );
        if ( rows == 0 ){
            // no current record so make one....
            cp.insert( ArrowCount.CONTENT_URI, content);
        }
    }

    private void setArrowCount(int count) {
        arrow_count = count;
        if ( arrow_count < 0 ){
            // no lower than zero
            arrow_count = 0;
        }
                //TODO Tie in with the increment both should be doing the same things with the db
        mArrowDisp.setText(Utility.getFormattedArrowCount(this, arrow_count));
        Log.v(LOG_TAG, "Arrow count: " + arrow_count);
    }



    private void setActiveVolley(int volley) {
        // Need to activate the correct toggle to show current volley size
        mVolley = volley;

        Log.v(LOG_TAG, "setActiveVolley: "+ volley);

        switch (volley) {
            case 1:
                toggleVolleyButton( mVolButton1, true );
                toggleVolleyButton( mVolButton3, false );
                toggleVolleyButton( mVolButton6, false );
                break;
            case 3:
                toggleVolleyButton( mVolButton1, false );
                toggleVolleyButton( mVolButton3, true );
                toggleVolleyButton( mVolButton6, false );
                break;
            case 6:
                toggleVolleyButton( mVolButton1, false );
                toggleVolleyButton( mVolButton3, false );
                toggleVolleyButton( mVolButton6, true );
                break;
            default:
                Log.w(LOG_TAG, "No Matching Volley found: only 1,3,6 supported [" + mVolley + "]");
                toggleVolleyButton( mVolButton1, false );
                toggleVolleyButton( mVolButton3, false );
                toggleVolleyButton( mVolButton6, false );
        }
    }

    // Method to do null protection
    private void toggleVolleyButton(ToggleButton vButton, boolean active) {
        if ( vButton != null ){
            vButton.setChecked(active);
        }
        else {
            Log.w( LOG_TAG, "toggleVolleyButton passed a null!");
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader.......");

        // We want "today" for our clicker
        long dateTime = Utility.getJulianStartTime(0);

        if (null != mUri){
            return new CursorLoader(
                    this,
                    mUri,
                    COUNT_COLUMNS,
                    sArrowCountDaySelection,
                    new String[]{Long.toString(dateTime)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished");
        if (data != null && data.moveToFirst()) {
            arrow_count = data.getInt(COL_ARROWCOUNT_COUNT);
            mArrowDisp.setText(Utility.getFormattedArrowCount(this, arrow_count));
            Log.v(LOG_TAG, "... TextSet");
        }
        else{
            Log.v(LOG_TAG, "... No data found");
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
