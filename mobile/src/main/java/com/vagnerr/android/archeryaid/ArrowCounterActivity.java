package com.vagnerr.android.archeryaid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ArrowCounterActivity extends AppCompatActivity {

    int mVolley;

    private final String LOG_TAG = AppCompatActivity.class.getSimpleName();
    private ToggleButton mVolButton1;
    private ToggleButton mVolButton3;
    private ToggleButton mVolButton6;
    private TextView mArrowDisp;

    private int arrow_count = 0;

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
                        updateArrowCount(mVolley);
                    }
                });

        findViewById(R.id.button_countUndo)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateArrowCount(-mVolley);
                    }
                });


        mArrowDisp.setText(Utility.getFormattedArrowCount(this, arrow_count));

    }

    private void updateArrowCount(int mVolley) {
        arrow_count = arrow_count + mVolley;
        if ( arrow_count < 0 ){
            // no lower than zero
            arrow_count = 0;
        }

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
}
