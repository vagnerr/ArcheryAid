package com.vagnerr.android.archeryaid.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Peter on 08/10/2017.
 */

public class DBConstantsXmlParser {
    private final String LOG_TAG = DBConstantsXmlParser.class.getSimpleName();
    private static final String ns = null;

    public HashMap<String, List> parse(InputStream in ) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readConstants(parser);
        } finally {
            in.close();
        }        
    }

    private HashMap<String, List> readConstants(XmlPullParser parser)  throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        List classifications = new ArrayList();
        List session_states = new ArrayList();
        List arrows = new ArrayList();
        List rules = new ArrayList();
        List target_types = new ArrayList();

        HashMap<String,List> constants = new HashMap<String, List>();

        Log.v(LOG_TAG, "readConstants() start");
        parser.require(XmlPullParser.START_TAG, ns, "constants");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("classification")) {
                classifications = readClassification(parser);
                constants.put("classification", classifications);
            } else if (name.equals("session_state")) {
                session_states = readSessionState(parser);
                constants.put("session_state", session_states);
            } else if (name.equals("arrow")) {
                arrows = readArrows(parser);
                constants.put("arrow", arrows);
            } else if (name.equals("rules")) {
                rules = readRules(parser);
                constants.put("rules", rules);
            } else if (name.equals("target_type")) {
                target_types = readTargetType(parser);
                constants.put("target_type",target_types);

            } else {
                Log.v(LOG_TAG, "SKIP1:" + name);
                skip(parser);
            }
        }

        Log.v(LOG_TAG, "FINAL RESULTS");
        Log.v(LOG_TAG, "CL:" + classifications);
        Log.v(LOG_TAG, "SS:" + session_states);
        Log.v(LOG_TAG, "AR:" + arrows);
        Log.v(LOG_TAG, "RL:" + rules);
        Log.v(LOG_TAG, "TT:" + target_types);
        return constants;
    }


    private List readArrows(XmlPullParser parser) throws  XmlPullParserException, IOException  {
        List arrows = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "arrow");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("record")) {
                ContentValues ar = readArrowRecord(parser);
                arrows.add(ar);
                Log.v(LOG_TAG, "Arrow: " + ar);
            } else {
                Log.v(LOG_TAG, "SKIP4:" + name);

                skip(parser);
            }
        }
        return arrows;
    }

    private ContentValues readArrowRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();

        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        Integer value = Integer.valueOf(parser.getAttributeValue(null, "value"));


        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        ContentValues cv = new ContentValues();
        cv.put(ArcheryContract.ArrowConst._ID, id);
        cv.put(ArcheryContract.ArrowConst.COLUMN_NAME, name);
        cv.put(ArcheryContract.ArrowConst.COLUMN_VALUE, value);
        return cv;
    }

    private List readSessionState(XmlPullParser parser) throws  XmlPullParserException, IOException  {
        List session_states = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "session_state");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("record")) {
                ContentValues ss = readSessionStateRecord(parser);
                session_states.add(ss);
                Log.v(LOG_TAG, "SessionState: " + ss);
            } else {
                Log.v(LOG_TAG, "SKIP2:" + name);

                skip(parser);
            }
        }
        return session_states;
    }

    private ContentValues readSessionStateRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();

        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        Boolean official = Boolean.valueOf(parser.getAttributeValue(null, "official"));


        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        ContentValues cv = new ContentValues();
        cv.put(ArcheryContract.SessionStateConst._ID, id);
        cv.put(ArcheryContract.SessionStateConst.COLUMN_NAME, name);
        cv.put(ArcheryContract.SessionStateConst.COLUMN_OFFICIAL, official);
        return cv;
    }


    private List readClassification(XmlPullParser parser) throws  XmlPullParserException, IOException {
        List classifications = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "classification");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("record")) {
                ContentValues cl = readClassificationRecord(parser);
                classifications.add(cl);


                Log.v(LOG_TAG, "Classification: " + cl);
            } else {
                Log.v(LOG_TAG, "SKIP3:" + name);

                skip(parser);
            }
        }
        return classifications;
    }


    private ContentValues readClassificationRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        //Integer id = 0;
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();
        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        ContentValues cv = new ContentValues();
        cv.put(ArcheryContract.ClassificationConst._ID,id);
        cv.put(ArcheryContract.ClassificationConst.COLUMN_NAME, name);
        return cv;
    }



    private List readRules(XmlPullParser parser) throws  XmlPullParserException, IOException {
        List rules = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "rules");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("record")) {
                ContentValues rule = readRuleRecord(parser);
                rules.add(rule);
                Log.v(LOG_TAG, "Rule: " + rule);
            } else {
                Log.v(LOG_TAG, "SKIP5:" + name);

                skip(parser);
            }
        }
        return rules;
    }


    private ContentValues readRuleRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        //Integer id = 0;
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();
        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        ContentValues cv = new ContentValues();
        cv.put(ArcheryContract.RulesConst._ID, id);
        cv.put(ArcheryContract.RulesConst.COLUMN_NAME, name);
        return cv;
    }


    private List readTargetType(XmlPullParser parser) throws  XmlPullParserException, IOException {
        List targets = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "target_type");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("record")) {
                ContentValues target = readTargetTypeRecord(parser);
                targets.add(target);
                Log.v(LOG_TAG, "TargetT: " + target);
            } else {
                Log.v(LOG_TAG, "SKIP5:" + name);

                skip(parser);
            }
        }

        return targets;
    }


    private ContentValues readTargetTypeRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        //Integer id = 0;
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();
        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        String code = parser.getAttributeValue(null, "code");
        String zones = parser.getAttributeValue(null, "zones");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        ContentValues cv = new ContentValues();
        cv.put(ArcheryContract.TargetTypeConst._ID,id);
        cv.put(ArcheryContract.TargetTypeConst.COLUMN_NAME, name);
        cv.put(ArcheryContract.TargetTypeConst.COLUMN_CODE, code);
        cv.put(ArcheryContract.TargetTypeConst.COLUMN_ZONES, zones);
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





}

