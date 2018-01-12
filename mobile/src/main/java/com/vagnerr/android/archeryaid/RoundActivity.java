package com.vagnerr.android.archeryaid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class RoundActivity extends AppCompatActivity {
    private final String LOG_TAG = RoundActivity.class.getSimpleName();

    private AdView mAdView;

    public static final String RULE_ID = "RULE_ID";
    public static final String ROUND_ID = "ROUND_ID";

    private long ruleID = -1;
    private long roundID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ruleID = getIntent().getLongExtra(RULE_ID, -1);
        roundID = getIntent().getLongExtra(ROUND_ID, -1);

        Log.v(LOG_TAG, "Creating new round: rule:" + ruleID + " round:" + roundID);
        //TODO: if rule or round are -1 we need to abort. *should* never happen but you never know

        setContentView(R.layout.activity_round);

        mAdView = (AdView) findViewById(R.id.adViewRound);  // must come after setContentView or you get null
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //POC...

        // TODO:
        //  * Design a "distance" layout.xml ( heading + [ ends ] + distance totals )
        //  * for distance in ( distances )
        //    * inflate distance
        //    * add N ends to that
        //    * add inflated to page
        // SO we have a two stage process build up the ends onto the distance onto the page
        //
        // For POC work with fixed round data. ... then pull the Intent info and work with that.
        // Note: Eventually we don't want to build the page from the static information on the
        //       the round type selected. We want to build it based on the actual rounds
        //       recorded score data ( need to attach the ends to the underlying cursors )
        //         * Build the static then manually "fill out" the values ?
        //         * On initial creation of the round generate "zeros" in the DB for the new round then...
        //           * attach the cursors as we build out the GUI?
        //       Going to need some testing on this especially from a performance perspective.
        // Build out the ends

        // TEMPORARY TEST DATA
        Integer[][] TEST_DATA =new Integer[][]{ // Full Metric Round
                {90, 6},
                {70, 6},
                {50, 6},
                {30, 6},
        };


        LinearLayout distances = (LinearLayout) findViewById(R.id.roundDistances);
        LayoutInflater inflater = getLayoutInflater();
        int my_ids=1;

        for ( int distance = 0; distance < TEST_DATA.length; distance++){
            View distanceview = inflater.inflate(R.layout.content_round_distance, distances, false);
            distanceview.setId(my_ids++);
            distances.addView(distanceview, -1, WRAP_CONTENT);

            LinearLayout ends = distanceview.findViewById(R.id.distance_ends);
            for ( int end = 0; end < TEST_DATA[distance][1] ; end++){
                View endview = inflater.inflate(R.layout.content_round_end, distances, false);
                endview.setId(my_ids++);
                ends.addView(endview,-1,WRAP_CONTENT);
            }
        }

//        Button button = new Button(this);
//        button.setText("Button 1");
//        int id = 1;
//        button.setId(id);
//        //ends.linearLayout
//
//        // False: don't add view straight to layout. if you do that then I think it returns
//        //        a singleton so it cant "add" second one later
//        View endview = inflater.inflate(R.layout.content_round_end, distances, false);
//        endview.setId(2);
//        View endview2 = inflater.inflate(R.layout.content_round_end, distances, false);
//        endview2.setId(3);
//
//        Button button2 = new Button(this);
//        button2.setText("Button 2");
//        int id2 = 3;
//        button2.setId(id2);
//        //ends.linearLayout
//        distances.addView(button2, -1, WRAP_CONTENT);
//
//        distances.addView( endview2, -1, WRAP_CONTENT );  // -1: add to end else the overwrite each other?
//
//        distances.addView(button, -1, WRAP_CONTENT);
//
//        distances.addView( endview, -1, WRAP_CONTENT );


Log.v(LOG_TAG,"Done");



    }


}
