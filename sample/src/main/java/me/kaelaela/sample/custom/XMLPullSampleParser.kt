package me.kaelaela.sample.custom

import android.util.Log
import android.util.Xml
import me.kaelaela.opengraphview.Parser
import me.kaelaela.opengraphview.network.model.OGData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

/*
 * NOTE: XmlPullParser can't parse <!doctype html>
 * ref: https://stackoverflow.com/questions/17761829/doctype-gives-unexpected-error-when-xmlpullparser-is-used
 */
class XMLPullSampleParser : Parser {

    private val NAME_META = "meta"
    private val NAME_HEAD = "head"
    private val NAME_TITLE = "title"
    private val ATTR_NAME = "name"
    private val ATTR_CONTENT = "content"
    private val ATTR_PROPERTY = "property"

    private val OG_TITLE = "og:title"
    private val OG_IMAGE = "og:image"
    private val OG_URL = "og:url"
    private val OG_DESC = "og:description"

    private val TWITTER_TITLE = "twitter:title"
    private val TWITTER_IMAGE = "twitter:image"
    private val TWITTER_URL = "twitter:url"
    private val TWITTER_DESC = "twitter:description"
    private val DESC = "description"

    private val ogData: OGData = OGData()

    @Throws(IOException::class)
    override fun parse(inputStream: InputStream): OGData {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isValidating = false
            factory.setFeature(Xml.FEATURE_RELAXED, true)
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(inputStream, null)
            var type = xpp.eventType
            while (type != XmlPullParser.END_DOCUMENT) {
                Log.d("TAG", "---start parse---")
                if (type == XmlPullParser.START_TAG && xpp.name == NAME_META) {
                    parseAttribute(xpp)
                } else if (type == XmlPullParser.START_TAG && xpp.name == NAME_TITLE) {
                    Log.d("TAG", "title:" + xpp.nextText())
                    ogData.setTitle(xpp.text)
                } else if (type == XmlPullParser.END_TAG && xpp.name == NAME_HEAD) {
                    Log.d("TAG", "---end parse---")
                    break
                }
                type = xpp.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return ogData
    }

    private fun parseAttribute(xpp: XmlPullParser) {
        for (i in 0 until xpp.attributeCount) {
            val attrName = xpp.getAttributeName(i)
            val attrValue = xpp.getAttributeValue(i)
            val contentValue = xpp.getAttributeValue("", ATTR_CONTENT)
            if (attrName == ATTR_NAME && attrValue == DESC ||
                    attrName == ATTR_PROPERTY && attrValue == OG_DESC ||
                    attrName == ATTR_PROPERTY && attrValue == TWITTER_DESC) {
                Log.d("TAG", "desc:" + contentValue)
                ogData.setDescription(contentValue)
            } else if (attrName == ATTR_PROPERTY && attrValue == OG_TITLE ||
                    attrName == ATTR_PROPERTY && attrValue == TWITTER_TITLE) {
                Log.d("TAG", "title:" + contentValue)
                ogData.setTitle(contentValue)
            } else if (attrName == ATTR_PROPERTY && attrValue == OG_IMAGE ||
                    attrName == ATTR_PROPERTY && attrValue == TWITTER_IMAGE) {
                Log.d("TAG", "image:" + contentValue)
                ogData.setImage(contentValue)
            } else if (attrName == ATTR_PROPERTY && attrValue == OG_URL ||
                    attrName == ATTR_PROPERTY && attrValue == TWITTER_URL) {
                Log.d("TAG", "url:" + contentValue)
                ogData.setUrl(contentValue)
            }
        }
    }
}
