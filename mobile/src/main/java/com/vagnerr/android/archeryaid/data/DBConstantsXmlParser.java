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
            if (name.equals("entry")) {
                //TODO: Example code only. to be removed
                entries.add(readEntry(parser));
            } else if (name.equals("classification")) {
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
                Arrow ar = readArrowRecord(parser);
                arrows.add(ar);
                Log.v(LOG_TAG, "Arrow: " + ar);
            } else {
                Log.v(LOG_TAG, "SKIP4:" + name);

                skip(parser);
            }
        }
        return arrows;
    }

    private Arrow readArrowRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();

        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        Integer value = Integer.valueOf(parser.getAttributeValue(null, "value"));


        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        return new Arrow(id,name,value);

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
                SessionState ss = readSessionStateRecord(parser);
                session_states.add(ss);
                Log.v(LOG_TAG, "SessionState: " + ss);
            } else {
                Log.v(LOG_TAG, "SKIP2:" + name);

                skip(parser);
            }
        }
        return session_states;
    }

    private SessionState readSessionStateRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();

        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        Boolean official = Boolean.valueOf(parser.getAttributeValue(null, "official"));


        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        return new SessionState(id,name,official);

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
                Classification cl = readClassificationRecord(parser);
                classifications.add(cl);


                Log.v(LOG_TAG, "Classification: " + cl);
            } else {
                Log.v(LOG_TAG, "SKIP3:" + name);

                skip(parser);
            }
        }
        return classifications;
    }


    private Classification readClassificationRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        //Integer id = 0;
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();
        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        return new Classification(id,name);

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
                Rule rule = readRuleRecord(parser);
                rules.add(rule);
                Log.v(LOG_TAG, "Rule: " + rule);
            } else {
                Log.v(LOG_TAG, "SKIP5:" + name);

                skip(parser);
            }
        }
        return rules;
    }


    private Rule readRuleRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
        //Integer id = 0;
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String tag = parser.getName();
        Integer id = Integer.valueOf(parser.getAttributeValue(null, "_id"));
        String name = parser.getAttributeValue(null, "name");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "record");

        return new Rule(id,name);

    }


    private List readTargetType(XmlPullParser parser) throws  XmlPullParserException, IOException {
        List targets = new ArrayList();
Log.v(LOG_TAG, "readTargetType...");
        parser.require(XmlPullParser.START_TAG, ns, "target_type");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("record")) {
                TargetType target = readTargetTypeRecord(parser);
                targets.add(target);
                Log.v(LOG_TAG, "TargetT: " + target);
            } else {
                Log.v(LOG_TAG, "SKIP5:" + name);

                skip(parser);
            }
        }
        return targets;
    }


    private TargetType readTargetTypeRecord(XmlPullParser parser) throws IOException, XmlPullParserException {
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

        return new TargetType(id,name,code,zones);

    }






    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }


    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.v(LOG_TAG, "readEntry() start");

        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("summary")) {
                summary = readSummary(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(title, summary, link);

    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }



    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
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




    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;

        private Entry(String title, String summary, String link) {
            this.title = title;
            this.summary = summary;
            this.link = link;
        }
    }
    public static class Classification {
        public final Integer id;
        public final String name;

        private Classification(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Classification("+this.id+","+this.name+")";
        }
    }
    public static class SessionState {
        public final Integer    id;
        public final String     name;
        public final Boolean    official;

        private SessionState(Integer id, String name, Boolean official) {
            this.id         = id;
            this.name       = name;
            this.official   = official;
        }

        @Override
        public String toString() {
            return "SessionState("+this.id+","+this.name+","+this.official+")";
        }
    }
    public static class Arrow {
        public final Integer    id;
        public final String     name;
        public final Integer    value;

        private Arrow(Integer id, String name, Integer value) {
            this.id         = id;
            this.name       = name;
            this.value      = value;
        }

        @Override
        public String toString() {
            return "Arrow("+this.id+","+this.name+"="+this.value+" points)";
        }
    }
    public static class Rule {
        public final Integer id;
        public final String name;

        private Rule(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Rule("+this.id+","+this.name+")";
        }
    }
    public static class TargetType {
        public final Integer id;
        public final String name;
        public final String code;
        public final String zones;

        private TargetType(Integer id, String name, String code, String zones) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.zones = zones;
        }

        @Override
        public String toString() {
            return "Rule("+this.id+","+this.name+"("+this.code+") = \""+this.zones+"\")";
        }
    }

}

