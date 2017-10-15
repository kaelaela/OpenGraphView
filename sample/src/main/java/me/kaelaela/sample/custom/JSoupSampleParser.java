package me.kaelaela.sample.custom;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.kaelaela.opengraphview.Parser;
import me.kaelaela.opengraphview.network.model.OGData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupSampleParser implements Parser {

    private final String DECODE_UTF8 = "UTF-8";
    private final String OG_PREF = "og:";
    private final String OG_TITLE = OG_PREF + "title";
    private final String OG_IMAGE = OG_PREF + "image";
    private final String OG_URL = OG_PREF + "url";
    private final String OG_DESC = OG_PREF + "description";

    private final String TWITTER_PREF = "twitter:";
    private final String TWITTER_TITLE = TWITTER_PREF + "title";
    private final String TWITTER_IMAGE = TWITTER_PREF + "image";
    private final String TWITTER_URL = TWITTER_PREF + "url";
    private final String TWITTER_DESC = TWITTER_PREF + "description";

    private final String ATTR_CONTENT = "content";
    private final String ATTR_PROPERTY = "property";
    private final String ATTR_NAME = "name";
    private final String VAL_DESC = "description";

    @Override
    public OGData parse(InputStream inputStream) throws IOException {
        OGData data = new OGData();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, DECODE_UTF8);
        String html = readHtml(new BufferedReader(inputStreamReader));
        Document doc = Jsoup.parse(html);
        Elements ogMetas = doc.getElementsByAttributeValueStarting(ATTR_PROPERTY, OG_PREF);
        for (int i = 0, size = ogMetas.size(); i < size; i++) {
            Element ogMeta = ogMetas.get(i);
            String content = ogMeta.attr(ATTR_CONTENT);
            switch (ogMeta.attr(ATTR_PROPERTY)) {
                case OG_TITLE:
                    data.setTitle(content);
                    break;
                case OG_IMAGE:
                    data.setImage(content);
                    break;
                case OG_URL:
                    data.setUrl(content);
                    break;
                case OG_DESC:
                    data.setDescription(content);
                    break;
            }
        }

        if (TextUtils.isEmpty(data.getTitle()) && !TextUtils.isEmpty(doc.title())) {
            data.setTitle(doc.title());
        }

        String description = doc.getElementsByAttributeValue(ATTR_NAME, VAL_DESC).text();
        if (TextUtils.isEmpty(data.getDescription()) && !TextUtils.isEmpty(description)) {
            data.setDescription(description);
        }

        if (ogMetas.size() != 0) {
            return data;
        }

        Elements twiMetas = doc.getElementsByAttributeValueStarting(ATTR_PROPERTY, TWITTER_PREF);
        for (int i = 0, size = twiMetas.size(); i < size; i++) {
            Element twiMeta = twiMetas.get(i);
            String content = twiMetas.attr(ATTR_CONTENT);
            switch (twiMeta.attr(ATTR_PROPERTY)) {
                case TWITTER_TITLE:
                    if (TextUtils.isEmpty(data.getTitle())) {
                        data.setTitle(content);
                    }
                    break;
                case TWITTER_IMAGE:
                    if (TextUtils.isEmpty(data.getImage())) {
                        data.setImage(content);
                    }
                    break;
                case TWITTER_URL:
                    if (TextUtils.isEmpty(data.getUrl())) {
                        data.setUrl(content);
                    }
                    break;
                case TWITTER_DESC:
                    if (TextUtils.isEmpty(data.getDescription())) {
                        data.setDescription(content);
                    }
                    break;
            }
        }
        return data;
    }

    private String readHtml(BufferedReader br) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
