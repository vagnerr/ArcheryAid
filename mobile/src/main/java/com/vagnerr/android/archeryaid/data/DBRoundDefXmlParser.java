package com.vagnerr.android.archeryaid.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Peter on 10/10/2017.
 *
 * Parse round_definitions.xml data and fill out db tables
 *    round_const
 *    round_makeup
 *
 * Other Round Definition tables ( more "pure" constants are done in DBConstantsXMLParser )
 * Mostly static, but additional rounds may be added in the app by defining custom rounds
 * in the range 100,001 -> 100,999, hopefully 999 entries should be enough ;-). Though there
 * is no "upper limits" all the defined official rounds are currently below 10,000
 * Idea is that, should the round definitions be updated in a future update ( likely
 * considering how many options there are, it will be possibly to delete all rounds in the
 * DB below 100,000 and recreate them.
 * However
 *    ALL PREVIOUS OFFICIAL ROUND IDS ARE IMMUTABLE
 *    AS THEY WILL POTENTIALLY BE USED IN RECORDED SESSIONS
 *
 */

public class DBRoundDefXmlParser {
    private final String LOG_TAG = DBConstantsXmlParser.class.getSimpleName();
    private static final String ns = null;

    // Parse the round_definitions.xml return a List of HashMap objects ( one for each
    // round_const record, with "round" for the round_const data and "round_makeup"
    // for the associated round_makeup (target) records
    public List parse(InputStream in ) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRounds(parser);
        } finally {
            in.close();
        }
    }

    private List readRounds(XmlPullParser parser)  throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        List session_states = new ArrayList();
        List arrows = new ArrayList();
        List target_types = new ArrayList();

        List rule = new ArrayList();
        List rules = new ArrayList();


        // HashMap<String,List> rules = new HashMap<String, List>();

        Log.v(LOG_TAG, "readRounds() start");
        parser.require(XmlPullParser.START_TAG, ns, "rounds");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("rule")) {
                rule = readRule(parser);
                rules.addAll( rule ); // Flatten the List returned into this list.
            }
            else {
                Log.v(LOG_TAG, "SKIP1:" + name);
                skip(parser);
            }
        }

        Log.v(LOG_TAG, "FINAL RESULTS");
        Log.v(LOG_TAG, "CL:" + rules);
        return rules;
    }

    private List readRule(XmlPullParser parser) throws  XmlPullParserException, IOException {
        Log.v(LOG_TAG,"readRule();");
        List rounds = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "rule");

        Integer rules_id = Integer.valueOf(parser.getAttributeValue(null, "rules_id"));

        String rules_name = parser.getAttributeValue(null, "name");
        Boolean rules_official = booleanOrNull((parser.getAttributeValue(null,"official")));
        Boolean rules_custom = booleanOrNull(parser.getAttributeValue(null,"custom"));
        String rules_season = parser.getAttributeValue(null, "season");
        String rules_units = parser.getAttributeValue(null, "units");
        Integer rules_arrows = integerOrNull(parser.getAttributeValue(null, "arrows")); // force empty if not there so valueOf wont throw exception
        String rules_scoring = parser.getAttributeValue(null, "scoring");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("round")) {
                HashMap round_data = readRound(parser);

                // Populate missing data from the "Defaults" on the rule
                ContentValues round = (ContentValues)round_data.get("round");
                round.put(ArcheryContract.RoundConst.COLUMN_CUSTOM, rules_custom);
                round.put(ArcheryContract.RoundConst.COLUMN_RULES_ID, rules_id);
                if ( round.getAsBoolean(ArcheryContract.RoundConst.COLUMN_OFFICIAL) == null ){
                    round.put(ArcheryContract.RoundConst.COLUMN_OFFICIAL, rules_official );
                }

                List targets = (List)round_data.get("round_makeup");
                Iterator<ContentValues> iterator = targets.iterator();
                while(iterator.hasNext()){
                    ContentValues target = iterator.next();
                    if ( target.getAsInteger(ArcheryContract.RoundMakeup.COLUMN_ARROWS) == null ){
                        target.put(ArcheryContract.RoundMakeup.COLUMN_ARROWS, rules_arrows);
                    }
                    if ( target.getAsString(ArcheryContract.RoundMakeup.COLUMN_TARGET_TYPE_ID) == null ){
                        // NOTE: data here is the target_type_const.code string not the numerical id
                        //       this needs to be converted at DB insert back in ActivityMain when
                        //       we have a handle on the db providerr for the lookups
                        target.put(ArcheryContract.RoundMakeup.COLUMN_TARGET_TYPE_ID, rules_scoring);
                        Log.e(LOG_TAG, "replaceing ttIP with:"+rules_scoring);
                        Log.e(LOG_TAG, "         target:"+target);

                    }
                }


                rounds.add(round_data);



                Log.v(LOG_TAG, "Round: " + round_data);
            } else {
                Log.v(LOG_TAG, "SKIP3:" + name);

                skip(parser);
            }
        }

        return rounds;
    }




    private HashMap readRound(XmlPullParser parser) throws  XmlPullParserException, IOException {
        Log.v(LOG_TAG, "readRound()");
        parser.require(XmlPullParser.START_TAG, ns, "round");
        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        Boolean official = booleanOrNull(parser.getAttributeValue(null,"official"));
        String season = parser.getAttributeValue(null, "season");
        String units = parser.getAttributeValue(null, "units");
        String scoring = parser.getAttributeValue(null, "scoring");
        Integer arrows = integerOrNull(parser.getAttributeValue(null, "arrows"));


        List targets = new ArrayList();

        Integer distance_order = 1;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            if (tagname.equals("target")) {
                ContentValues round_makeup = readTarget(parser);

                round_makeup.put(ArcheryContract.RoundMakeup.COLUMN_ROUND_ID, id);
                round_makeup.put(ArcheryContract.RoundMakeup.COLUMN_DISTANCE_ORDER, distance_order++);
                round_makeup.put(ArcheryContract.RoundMakeup.COLUMN_ARROWS, arrows);  // TODO: Might need to pass value in from caller
                round_makeup.put(ArcheryContract.RoundMakeup.COLUMN_TARGET_TYPE_ID, scoring); // TODO: """  Also this needs to be replaced by the target_type_const lookup
                targets.add(round_makeup);


                Log.v(LOG_TAG, "Target: " + round_makeup);
            } else {
                Log.v(LOG_TAG, "SKIP4:" + tagname);

                skip(parser);
            }
        }

        ContentValues round = new ContentValues();
        round.put(ArcheryContract.RoundConst._ID,id);
        round.put(ArcheryContract.RoundConst.COLUMN_NAME, name);
        round.put(ArcheryContract.RoundConst.COLUMN_OFFICIAL, official); // TODO:


        //round.put(ArcheryContract.RoundConst.COLUMN_RULES_ID);
        HashMap round_data = new HashMap();
        round_data.put("round", round);
        round_data.put("round_makeup", targets);


        return round_data;

    }

    private ContentValues readTarget(XmlPullParser parser)  throws  XmlPullParserException, IOException {
        Log.v(LOG_TAG,"readTarget();");

        parser.require(XmlPullParser.START_TAG, ns, "target");
        Integer distance = Integer.valueOf(parser.getAttributeValue(null, "distance"));
        Integer size = Integer.valueOf(parser.getAttributeValue(null, "size"));
        Integer ends = Integer.valueOf(parser.getAttributeValue(null, "ends"));


        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "target");

        ContentValues cv = new ContentValues();

        // Rest of table is built up by the calling function.
        cv.put(ArcheryContract.RoundMakeup.COLUMN_DISTANCE, distance);
        cv.put(ArcheryContract.RoundMakeup.COLUMN_TARGET_SIZE, size);
        cv.put(ArcheryContract.RoundMakeup.COLUMN_END_COUNT, ends);

        return cv;


    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /* If parser.getAttribute fails it returns null which is fine
     * for my needs, but parsing that to Integer.valueOf would
     * through an exception so _bodge_ it :)  ( much easier in perl ;-) )
     * Only use when the attrib is optional that way we get a free validity
     * check on the others
     */
    private Integer integerOrNull(String attribute){
        if ( attribute == null ){
            return null;
        }
        else {
            return Integer.valueOf(attribute);
        }

    }
    /* same goes for booleans */
    private Boolean booleanOrNull(String attribute){
        if ( attribute == null ){
            return null;
        }
        else {
            return Boolean.valueOf(attribute);
        }

    }
}
