package io.driden.fishtips.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.driden.fishtips.model.TideStation;

public class TideStationXMLParser {

    private static TideStationXMLParser _parser;
    private static final String _nameSpace = null;

    private TideStationXMLParser() {

    }

    public static TideStationXMLParser getInstance() {

        if (_parser == null) {
            synchronized (TideStationXMLParser.class) {
                if (_parser == null) {
                    _parser = new TideStationXMLParser();
                }
            }
        }
        return _parser;
    }

    public synchronized static List<TideStation> parseTideStation(String str) throws XmlPullParserException, IOException {

        System.out.println(str);

        InputStream in = new ByteArrayInputStream(str.getBytes());

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();


            return getTideStation(parser);
        } finally {
            in.close();
        }
    }

    private static List<TideStation> getTideStation(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, _nameSpace, "markers");

        List<TideStation> list = new ArrayList<>();

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            switch (eventType) {

                case XmlPullParser.START_DOCUMENT:

                    break;
                case XmlPullParser.START_TAG:


                    if ("marker".equals(parser.getName())) {

                        TideStation tideStation = new TideStation();

                        tideStation.setName(parser.getAttributeValue(_nameSpace, "name"));
                        tideStation.setType(parser.getAttributeValue(_nameSpace, "type"));
                        tideStation.setLat(Double.parseDouble(parser.getAttributeValue(_nameSpace, "lat")));
                        tideStation.setLng(Double.parseDouble(parser.getAttributeValue(_nameSpace, "lng")));
                        tideStation.setDistance(Double.parseDouble(parser.getAttributeValue(_nameSpace, "distance")));
                        tideStation.setDescription(parser.getAttributeValue(_nameSpace, "description"));

                        list.add(tideStation);
                    }


                    break;

                case XmlPullParser.END_TAG:
            }

            eventType = parser.next();
        }

        return list;
    }

}
