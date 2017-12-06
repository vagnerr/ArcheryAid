package com.vagnerr.android.archeryaid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class RoundActivity extends AppCompatActivity {
    private final String LOG_TAG = RoundActivity.class.getSimpleName();


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
    }
}
