package com.vagnerr.android.archeryaid;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.vagnerr.android.archeryaid.data.ArcheryContract;

import java.util.List;

public class RoundPickActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String LOG_TAG = RoundPickActivity.class.getSimpleName();


    Spinner rule_spinner;
    Spinner round_spinner;

    private Button start_button;

    long ruleSelected = -1;
    long roundSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_pick);

        rule_spinner =  findViewById(R.id.ruleSpinner);
        round_spinner = findViewById(R.id.roundSpinner);
        start_button = findViewById(R.id.buttonStartRound);

        ContentResolver db = getContentResolver();

        String[] columns =       { ArcheryContract.RoundConst._ID,ArcheryContract.RoundConst.COLUMN_NAME };
        Cursor rulesc = db.query(
                ArcheryContract.RulesConst.CONTENT_URI,
                columns,
                null,   // where sql
                null,   // where args
                null    // sort order
        );
        String[] from = new String[]{ArcheryContract.RulesConst.COLUMN_NAME};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter raddapt = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item, rulesc,from,to);
        raddapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rule_spinner.setAdapter(raddapt);
        // TODO: find some way to clear the selection so the listener doesnt imediatly trigger. ( setSelection(-1) doesnt work)

        rule_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long ruleID) {
                Log.v(LOG_TAG, "rule spinner selected i="+i+" l="+ruleID );
                // Populate the round spinner
                ruleSelected = ruleID;
                roundSelected = -1; // reset the round, let the later callback re-populate
                populateRoundSpinner(ruleSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ruleSelected = -1;
                roundSelected = -1;  // if rule is reset, then reset round.
            }
        } );

        start_button.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Check we have valid Rule and Round selected and if so start the new activity
                if ( roundSelected < 0 || ruleSelected < 0 ) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), RoundActivity.class);
                intent.putExtra(RoundActivity.RULE_ID, ruleSelected);
                intent.putExtra(RoundActivity.ROUND_ID, roundSelected);
                startActivity(intent);

            }
        });


    }

    private void populateRoundSpinner ( long ruleID ) {

        ContentResolver db = getContentResolver();

        String[] columns =       { ArcheryContract.RoundConst._ID,ArcheryContract.RoundConst.COLUMN_NAME };
        Cursor roundc = db.query(
                ArcheryContract.RoundConst.CONTENT_URI,
                columns,
                ArcheryContract.RoundConst.COLUMN_RULES_ID + "=?" ,   // where sql
                new String[]{ Long.toString(ruleID) },   // where args
                null    // sort order
        );
        String[] from = new String[]{ArcheryContract.RoundConst.COLUMN_NAME};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter raddapt = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item, roundc,from,to);
        raddapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        round_spinner.setAdapter(raddapt);
        // TODO: find some way to clear the selection so the listener doesnt imediatly trigger. ( setSelection(-1) doesnt work)

        // TODO: populate round details visually  on the page on selection.
        round_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long roundID) {
                Log.v(LOG_TAG, "round spinner selected i="+i+" l="+roundID );
                roundSelected = roundID;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                roundSelected = -1;
            }
        } );


    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.v(LOG_TAG,"RPABASE selected: i="+i+" l="+l );
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.v(LOG_TAG,"RPABASE nothing selected");

    }


}
