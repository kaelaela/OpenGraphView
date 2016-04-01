package me.kaelaela.opengraphview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import me.kaelaela.opengraphview.network.model.OGData;

public class OGParser {

    private static final String DECODE_UTF8 = "UTF-8";
    private static final String TITLE = "og:title";
    private static final String IMAGE = "\"og:image\"";
    private static final String URL = "og:url";
    private static final String DESC = "og:description";
    private static final String HEAD_START_TAG = "<head";
    private static final String HEAD_END_TAG = "</head>";
    private static final String META_START_TAG = "<meta";
    private static final String CONTENT_PROPERTY = "content=\"";

    private static OGData ogData;

    public static OGData parse(InputStream inputStream) throws IOException {
        ogData = new OGData();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, DECODE_UTF8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String headContents = "";
        String metaTags = "";
        String sourceTextLine;
        boolean readingHead = false;
        while ((sourceTextLine = bufferedReader.readLine()) != null) {
            int headStart;
            int headEnd;
            if (sourceTextLine.contains(HEAD_START_TAG)) {
                headStart = sourceTextLine.indexOf(">", sourceTextLine.indexOf(HEAD_START_TAG));
                if (headStart < sourceTextLine.length()) {
                    headContents = headContents + sourceTextLine.substring(headStart + 1);
                }
                readingHead = true;
            } else if (sourceTextLine.contains(HEAD_END_TAG)) {
                headEnd = sourceTextLine.indexOf(HEAD_END_TAG);
                if (headEnd != 0) {
                    sourceTextLine = sourceTextLine.trim();
                    headContents = headContents + sourceTextLine.substring(0, headEnd);
                    String meta = formattingMetaTags(headContents).replace("\'", "\"");
                    BufferedReader stringReader = new BufferedReader(new StringReader(meta));
                    String metaTagLine;
                    while ((metaTagLine = stringReader.readLine()) != null) {
                        setOGData(metaTagLine);
                    }
                }
                break;
            } else if (readingHead) {
                headContents = headContents + sourceTextLine.trim();
            }

            if (readingHead && sourceTextLine.contains(META_START_TAG)) {
                metaTags = metaTags + sourceTextLine + "\n";
            }
        }
        bufferedReader.close();
        return ogData;
    }

    private static String formattingMetaTags(String headText) {
        String formattedText = "";
        int start = headText.indexOf(META_START_TAG);
        int end = headText.indexOf(">", start) + 1;
        formattedText = formattedText + headText.substring(start, end) + "\n";
        int length = headText.length();
        while (end < length) {
            start = headText.indexOf(META_START_TAG, end);
            end = headText.indexOf(">", start) + 1;
            if (start >= 0 && start < length) {
                formattedText = formattedText + headText.substring(start, end) + "\n";
            } else {
                return formattedText;
            }
        }
        return formattedText;
    }

    private static void setOGData(String line) throws IOException {
        int start = line.indexOf(CONTENT_PROPERTY) + CONTENT_PROPERTY.length();
        int end = line.indexOf("\"", start);
        if (line.contains(TITLE)) {
            ogData.setTitle(line.substring(start, end));
        } else if (line.contains(IMAGE)) {
            ogData.setImage(line.substring(start, end));
        } else if (line.contains(URL)) {
            ogData.setUrl(line.substring(start, end));
        } else if (line.contains(DESC)) {
            ogData.setDescription(line.substring(start, end));
        }
    }
}
