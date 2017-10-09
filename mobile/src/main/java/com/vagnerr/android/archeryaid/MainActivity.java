package com.vagnerr.android.archeryaid;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vagnerr.android.archeryaid.data.ArcheryContract;
import com.vagnerr.android.archeryaid.data.DBConstantsXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        Log.v(LOG_TAG, "Time to do the XML");
        new DownloadXmlTask().execute("https://www.vagnerr.com/foobar.xml");
        Log.v(LOG_TAG, "XML DONE");

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

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        Log.v(LOG_TAG, "loadXmlFromNetwork....");
        InputStream stream = null;
        // Instantiate the parser
        DBConstantsXmlParser DBConstantsXmlParser = new DBConstantsXmlParser();
        List<DBConstantsXmlParser.Entry> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        // Checks whether the user set the preference to include summary text
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        //htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
        //htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
        //        formatter.format(rightNow.getTime()) + "</em>");

        try {
            //TODO: LOAD FILE FROM RESOURSE
            Log.v(LOG_TAG, "DOWNLOADING URL");
            stream = downloadUrl(urlString);
            Log.v(LOG_TAG, ".... DONE");

            Log.v(LOG_TAG, "url downloaded starting parse");
            entries = DBConstantsXmlParser.parse(stream);
            Log.v(LOG_TAG, " parse  completed?");
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // StackOverflowXmlParser returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
//TODO: push into database
//            for (Entry entry : entries) {
//                htmlString.append("<p><a href='");
//                htmlString.append(entry.link);
//                htmlString.append("'>" + entry.title + "</a></p>");
//                // If the user set the preference to include summary text,
//                // adds it to the display.
//                if (pref) {
//                    htmlString.append(entry.summary);
//                }
//            }
        return htmlString.toString();
    }


    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.v(LOG_TAG, "background stuff");
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "OOOOOO NETWORK CONNECTION ERRROE"; // getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return "OOOOO XML ERRROR"; // getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
           // setContentView(R.layout.main);
           // // Displays the HTML string in the UI via a WebView
           // WebView myWebView = (WebView) findViewById(R.id.webview);
           // myWebView.loadData(result, "text/html", null);
        }

    }

}
