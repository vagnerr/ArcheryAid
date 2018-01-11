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

        Log.v(LOG_TAG, "Creating new round: rule:" + ruleID + " round:" +roundID);
        //TODO: if rule or round are -1 we need to abort. *should* never happen but you never know

        setContentView(R.layout.activity_round);

        mAdView = (AdView)findViewById(R.id.adViewRound);  // must come after setContentView or you get null
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Build out the ends
        LinearLayout ends = (LinearLayout) findViewById(R.id.roundDistances);
        //POC...


        LayoutInflater inflater = getLayoutInflater();
        Button button = new Button(this);
        button.setText("Button 1");
        int id = 1;
        button.setId(id);
        //ends.linearLayout

        // False: don't add view straight to layout. if you do that then I think it returns
        //        a singleton so it cant "add" second one later
        View endview = inflater.inflate(R.layout.content_round_end, ends, false);
        endview.setId(2);
        View endview2 = inflater.inflate(R.layout.content_round_end, ends, false);
        endview2.setId(3);

        Button button2 = new Button(this);
        button2.setText("Button 2");
        int id2 = 3;
        button2.setId(id2);
        //ends.linearLayout
        ends.addView(button2, -1, WRAP_CONTENT);

        ends.addView( endview2, -1, WRAP_CONTENT );  // -1: add to end else the overwrite each other?

        ends.addView(button, -1, WRAP_CONTENT);

        ends.addView( endview, -1, WRAP_CONTENT );
Log.v(LOG_TAG,"Done");



    }


}
