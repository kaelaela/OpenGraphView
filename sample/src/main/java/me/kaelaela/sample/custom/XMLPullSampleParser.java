package me.kaelaela.sample.custom;

import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import me.kaelaela.opengraphview.Parser;
import me.kaelaela.opengraphview.network.model.OGData;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/*
 * NOTE: XmlPullParser can't parse <!doctype html>
 * ref: https://stackoverflow.com/questions/17761829/doctype-gives-unexpected-error-when-xmlpullparser-is-used
 */
public class XMLPullSampleParser implements Parser {

    private final String NAME_META = "meta";
    private final String NAME_HEAD = "head";
    private final String NAME_TITLE = "title";
    private final String ATTR_NAME = "name";
    private final String ATTR_CONTENT = "content";
    private final String ATTR_PROPERTY = "property";

    private final String OG_TITLE = "og:title";
    private final String OG_IMAGE = "og:image";
    private final String OG_URL = "og:url";
    private final String OG_DESC = "og:description";

    private final String TWITTER_TITLE = "twitter:title";
    private final String TWITTER_IMAGE = "twitter:image";
    private final String TWITTER_URL = "twitter:url";
    private final String TWITTER_DESC = "twitter:description";
    private final String DESC = "description";

    private OGData ogData;

    @Override
    public OGData parse(InputStream inputStream) throws IOException {
        ogData = new OGData();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature(Xml.FEATURE_RELAXED, true);
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inputStream, null);
            int type = xpp.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                Log.d("TAG", "---start parse---");
                if (type == XmlPullParser.START_TAG && xpp.getName().equals(NAME_META)) {
                    parseAttribute(xpp);
                } else if (type == XmlPullParser.START_TAG && xpp.getName().equals(NAME_TITLE)) {
                    Log.d("TAG", "title:" + xpp.nextText());
                    ogData.setTitle(xpp.getText());
                } else if (type == XmlPullParser.END_TAG && xpp.getName().equals(NAME_HEAD)) {
                    Log.d("TAG", "---end parse---");
                    break;
                }
                type = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return ogData;
    }

    private void parseAttribute(XmlPullParser xpp) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            String attrName = xpp.getAttributeName(i);
            String attrValue = xpp.getAttributeValue(i);
            String contentValue = xpp.getAttributeValue("", ATTR_CONTENT);
            if (attrName.equals(ATTR_NAME) && attrValue.equals(DESC) ||
                    attrName.equals(ATTR_PROPERTY) && attrValue.equals(OG_DESC) ||
                    attrName.equals(ATTR_PROPERTY) && attrValue.equals(TWITTER_DESC)) {
                Log.d("TAG", "desc:" + contentValue);
                ogData.setDescription(contentValue);
            } else if (attrName.equals(ATTR_PROPERTY) && attrValue.equals(OG_TITLE) ||
                    attrName.equals(ATTR_PROPERTY) && attrValue.equals(TWITTER_TITLE)) {
                Log.d("TAG", "title:" + contentValue);
                ogData.setTitle(contentValue);
            } else if (attrName.equals(ATTR_PROPERTY) && attrValue.equals(OG_IMAGE) ||
                    attrName.equals(ATTR_PROPERTY) && attrValue.equals(TWITTER_IMAGE)) {
                Log.d("TAG", "image:" + contentValue);
                ogData.setImage(contentValue);
            } else if (attrName.equals(ATTR_PROPERTY) && attrValue.equals(OG_URL) ||
                    attrName.equals(ATTR_PROPERTY) && attrValue.equals(TWITTER_URL)) {
                Log.d("TAG", "url:" + contentValue);
                ogData.setUrl(contentValue);
            }
        }
    }
}
