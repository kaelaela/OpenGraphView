package me.kaelaela.sample.custom

import android.text.TextUtils
import me.kaelaela.opengraphview.Parser
import me.kaelaela.opengraphview.network.model.OGData
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class JSoupSampleParser : Parser {

    private val DECODE_UTF8 = "UTF-8"
    private val OG_PREF = "og:"
    private val OG_TITLE = OG_PREF + "title"
    private val OG_IMAGE = OG_PREF + "image"
    private val OG_URL = OG_PREF + "url"
    private val OG_DESC = OG_PREF + "description"

    private val TWITTER_PREF = "twitter:"
    private val TWITTER_TITLE = TWITTER_PREF + "title"
    private val TWITTER_IMAGE = TWITTER_PREF + "image"
    private val TWITTER_URL = TWITTER_PREF + "url"
    private val TWITTER_DESC = TWITTER_PREF + "description"

    private val ATTR_CONTENT = "content"
    private val ATTR_PROPERTY = "property"
    private val ATTR_NAME = "name"
    private val VAL_DESC = "description"

    @Throws(IOException::class)
    override fun parse(inputStream: InputStream): OGData {
        val data = OGData()
        val inputStreamReader = InputStreamReader(inputStream, DECODE_UTF8)
        val html = readHtml(BufferedReader(inputStreamReader))
        val doc = Jsoup.parse(html)
        val ogMetas: Elements = doc.getElementsByAttributeValueStarting(ATTR_PROPERTY, OG_PREF)
        ogMetas.let {
            var i = 0
            val size = it.size
            while (i < size) {
                val ogMeta = it[i]
                val content = ogMeta.attr(ATTR_CONTENT)
                when (ogMeta.attr(ATTR_PROPERTY)) {
                    OG_TITLE -> data.setTitle(content)
                    OG_IMAGE -> data.setImage(content)
                    OG_URL -> data.setUrl(content)
                    OG_DESC -> data.setDescription(content)
                }
                i++
            }
        }

        if (TextUtils.isEmpty(data.getTitle()) && !TextUtils.isEmpty(doc.title())) {
            data.setTitle(doc.title())
        }

        val description = doc.getElementsByAttributeValue(ATTR_NAME, VAL_DESC).text()
        if (TextUtils.isEmpty(data.getDescription()) && !TextUtils.isEmpty(description)) {
            data.setDescription(description)
        }

        if (ogMetas.size != 0) {
            return data
        }

        val twiMetas = doc.getElementsByAttributeValueStarting(ATTR_PROPERTY, TWITTER_PREF)
        var i = 0
        val size = twiMetas.size
        while (i < size) {
            val twiMeta = twiMetas[i]
            val content = twiMetas.attr(ATTR_CONTENT)
            when (twiMeta.attr(ATTR_PROPERTY)) {
                TWITTER_TITLE -> if (TextUtils.isEmpty(data.getTitle())) {
                    data.setTitle(content)
                }
                TWITTER_IMAGE -> if (TextUtils.isEmpty(data.getImage())) {
                    data.setImage(content)
                }
                TWITTER_URL -> if (TextUtils.isEmpty(data.getUrl())) {
                    data.setUrl(content)
                }
                TWITTER_DESC -> if (TextUtils.isEmpty(data.getDescription())) {
                    data.setDescription(content)
                }
            }
            i++
        }
        return data
    }

    @Throws(IOException::class)
    private fun readHtml(br: BufferedReader): String {
        val sb = StringBuilder()
        for (line in br.readLine()) {
            sb.append(line).append("\n")
        }
        return sb.toString()
    }
}
