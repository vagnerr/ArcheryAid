package com.vagnerr.android.archeryaid;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vagnerr.android.archeryaid.data.ArcheryContract;
import com.vagnerr.android.archeryaid.data.DBConstantsXmlParser;
import com.vagnerr.android.archeryaid.data.DBRoundDefXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private AdView mAdView;

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // update the totals...

        ContentResolver cp = this.getContentResolver();

        updateArrowCount( cp, R.id.arrowcount_week, 7 );
        updateArrowCount( cp, R.id.arrowcount_month, 30 );
        updateArrowCount( cp, R.id.arrowcount_year, 365 );
        updateArrowCount( cp, R.id.arrowcount_alltime, 0 );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Facebook Stetho integration for debugging TODO: Enclose this in IF DEV BUILD
        Stetho.initializeWithDefaults(this);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // TODO: Only run this when needed
        Log.v(LOG_TAG, "Loading XML Constants into DB");
        new InitialiseDBConstants().execute();
        Log.v(LOG_TAG, "DB DONE");

    }

    private void updateArrowCount(ContentResolver cp, int textView, int days) {
        Cursor data = cp.query( ArcheryContract.ArrowCount.buildArrowCountHistoryUri(days),
                new String[]{ArcheryContract.ArrowCount.COLUMN_COUNT},
                null,
                null,
                null

        );

        if (data != null && data.moveToFirst()) {
            int arrow_count = data.getInt(0); // TODO COLUMN INDEXES HERE
            TextView countDisplay = findViewById(textView);
            countDisplay.setText(Utility.getFormattedArrowCount(this, arrow_count));
        }
        else{
            Log.v(LOG_TAG, "... No data found");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_newround) {
            // Start Scoring a new round
            startActivity(new Intent(this, RoundPickActivity.class));

        } else if (id == R.id.nav_arrowcount) {
            // Call up the simple arrow counter
            startActivity(new Intent(this, ArrowCounterActivity.class));
            //return true;  // Normally we would return true, but wan the drawer closed ---vv

        } else if (id == R.id.nav_scorehistory) {
            // Access the Historical Scores

        } else if (id == R.id.nav_scoreexport) {
            // Export Scores... somewhere
        }





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Boolean loadXMLtoDB() throws XmlPullParserException, IOException {
        Log.v(LOG_TAG, "loadXMLtoDB....");
        InputStream stream = null;
        // Instantiate the parser
        DBConstantsXmlParser DBConstantsXmlParser = new DBConstantsXmlParser();
        HashMap<String, List> entries = null;

//        // Checks whether the user set the preference to include summary text
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean pref = sharedPrefs.getBoolean("summaryPref", false);


        try {
            // TODO: only run this code when its needed

            Log.v(LOG_TAG, "LOADING DATA STREAM 'constants'");

            stream = getResources().openRawResource(R.raw.database_constants);
            Log.v(LOG_TAG, ".... DONE");

            Log.v(LOG_TAG, "stream opened starting parse");
            entries = DBConstantsXmlParser.parse(stream);

            Log.v(LOG_TAG, " parse  completed?");
            Log.v(LOG_TAG, " DB Inserts...");

            // TODO: Refactor this so we are not doing each table by hand.

            ContentResolver db = getContentResolver();

            // Clear these tables first so we don't get insert fails on re-runs
            db.delete(ArcheryContract.ClassificationConst.CONTENT_URI,null,null);
            db.delete(ArcheryContract.SessionStateConst.CONTENT_URI,null,null);
            db.delete(ArcheryContract.ArrowConst.CONTENT_URI,null,null);
            db.delete(ArcheryContract.RulesConst.CONTENT_URI,null,null);
            db.delete(ArcheryContract.TargetTypeConst.CONTENT_URI,null,null);

            List items = entries.get("classification");
            ContentValues[] bulkToInsert = new ContentValues[items.size()];
            items.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.ClassificationConst.CONTENT_URI, bulkToInsert);

            items = entries.get("session_state");
            bulkToInsert = new ContentValues[items.size()];
            items.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.SessionStateConst.CONTENT_URI, bulkToInsert);

            items = entries.get("arrow");
            bulkToInsert = new ContentValues[items.size()];
            items.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.ArrowConst.CONTENT_URI, bulkToInsert);

            items = entries.get("rules");
            bulkToInsert = new ContentValues[items.size()];
            items.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.RulesConst.CONTENT_URI, bulkToInsert);

            items = entries.get("target_type");
            bulkToInsert = new ContentValues[items.size()];
            items.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.TargetTypeConst.CONTENT_URI, bulkToInsert);
            Log.v(LOG_TAG, "... DB Inserts complete");



            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        // Load in round definitions....
        DBRoundDefXmlParser DBRoundDefXmlParser = new DBRoundDefXmlParser();
        List roundentries = null;

        try {
            Log.v(LOG_TAG, "LOADING DATA STREAM 'round definitions'");

            stream = getResources().openRawResource(R.raw.round_definitions);
            Log.v(LOG_TAG, ".... DONE");

            Log.v(LOG_TAG, "stream opened starting parse");
            roundentries = DBRoundDefXmlParser.parse(stream);




            Log.v(LOG_TAG, " parse  completed?");
            Log.v(LOG_TAG, " DB Inserts...");
            Log.v(LOG_TAG, "   ... reformat data");
            // we have [
            //              {
            //                  round => { /round_const data/ },
            //                  round_makeup => { [ / round_makeup data / , ... ]
            //              },
            //              ...
            //          ]
            // round data is good for a bulk insert, but round_makup records need
            // target_type_id to be converted from the target_type_const.code to _id
            // for more efficient DB queries, then they can be bulk inserted.
            List rounds = new ArrayList();
            List round_targets = new ArrayList();
            for (Iterator<HashMap> iterator = roundentries.iterator(); iterator.hasNext(); ){
                HashMap round = iterator.next();

                rounds.add(round.get("round"));

                Log.v(LOG_TAG, "Round: " + round.get("round"));

                ArrayList targets = (ArrayList)round.get("round_makeup");
                for ( Iterator<ContentValues> t_iterator = targets.iterator() ; t_iterator.hasNext() ; ){
                    ContentValues target = t_iterator.next();
                    // change target_type_id from CODE to _id
                    target.put(ArcheryContract.RoundMakeup.COLUMN_TARGET_TYPE_ID,
                                getTargetTypeID(target.getAsString(ArcheryContract.RoundMakeup.COLUMN_TARGET_TYPE_ID)));


                    round_targets.add(target); // Its ok to flatten them all into a single array for bulk import

                    Log.v(LOG_TAG, "   T: "+ target);
                }

            }

            // TODO: DB INSERTS
            ContentResolver db = getContentResolver();

            // Clear these tables first so we don't get insert fails on re-runs
            // Note: round_makeup, then round_const ( due to constraints )
            //       and vice-versa for the inserts
            // TODO: limit deletes to keep user generated Custom entries
            Log.v(LOG_TAG, "   ... clear old data");

            db.delete(ArcheryContract.RoundMakeup.CONTENT_URI,null,null);
            db.delete(ArcheryContract.RoundConst.CONTENT_URI,null,null);
            Log.v(LOG_TAG, "   ... insert round_const data");

            ContentValues[] bulkToInsert = new ContentValues[rounds.size()];
            rounds.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.RoundConst.CONTENT_URI, bulkToInsert);
            Log.v(LOG_TAG, "   ... insert round_makeup data");

            bulkToInsert = new ContentValues[round_targets.size()];
            round_targets.toArray(bulkToInsert);
            db.bulkInsert(ArcheryContract.RoundMakeup.CONTENT_URI, bulkToInsert);

            Log.v(LOG_TAG, "       ... all done");



        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        return true;
    }

    // TODO: Replace this with a (cached ?) DB call in a util class ?
    private Integer getTargetTypeID (String code) {
        if( code == null ) {
            throw new RuntimeException("NULL Target Code:"+code);
        }
        if( code.equals("METRIC")){
            return 1;
        }
        else if ( code.equals("IMPERIAL")){
            return 2;
        }
        else {
            throw new RuntimeException("Unknown Target Code:"+code);
        }
    }

    private class InitialiseDBConstants extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.v(LOG_TAG, "background stuff");
            try {
                return loadXMLtoDB();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error loading XML data IO Exception: " + e );
                return false;
            } catch (XmlPullParserException e) {
                Log.e(LOG_TAG, "Error loading XML Parse error: " + e);
                return false;
            }
        }




    }

}
