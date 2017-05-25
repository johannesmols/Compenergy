/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class XMLPullParserHandler {

    private List<Carrier> carriers;
    private Carrier carrier;
    private String output;

    XMLPullParserHandler() {
        carriers = new ArrayList<>();
    }

    public List<Carrier> getCarriers() {
        return carriers;
    }

    List<Carrier> parse(InputStream inputStream) {

        XmlPullParserFactory factory;
        XmlPullParser parser;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("carrier")) {
                            carrier = new Carrier();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        output = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("carrier")) {
                            carriers.add(carrier);
                        } else if (tagname.equalsIgnoreCase("name")) {
                            carrier.set_name(output);
                        } else if (tagname.equalsIgnoreCase("category")) {
                            carrier.set_category(output);
                        } else if (tagname.equalsIgnoreCase("unit")) {
                            carrier.set_unit(output);
                        } else if (tagname.equalsIgnoreCase("energy")) {
                            carrier.set_energy(Long.parseLong(output));
                        } else if (tagname.equalsIgnoreCase("custom")) {
                            carrier.set_custom(Boolean.parseBoolean(output));
                        } else if (tagname.equalsIgnoreCase("favorite")) {
                            carrier.set_favorite(Boolean.parseBoolean(output));
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return carriers;
    }
}
