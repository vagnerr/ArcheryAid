package com.vagnerr.android.archeryaid;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.vagnerr.android.archeryaid.data.ArcheryContract;

import java.util.List;

public class RoundPickActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String LOG_TAG = RoundPickActivity.class.getSimpleName();


    Spinner rule_spinner;
    Spinner round_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_pick);

        rule_spinner = (Spinner) findViewById(R.id.ruleSpinner);
        round_spinner = (Spinner) findViewById(R.id.roundSpinner);

        ContentResolver db = getContentResolver();

        //db.query();

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


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.v(LOG_TAG,"selected: i="+i+" l="+l );
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.v(LOG_TAG,"nothing selected");

    }
}
