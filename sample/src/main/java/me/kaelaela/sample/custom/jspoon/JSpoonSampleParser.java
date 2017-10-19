package me.kaelaela.sample.custom.jspoon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.kaelaela.opengraphview.Parser;
import me.kaelaela.opengraphview.network.model.OGData;
import pl.droidsonroids.jspoon.HtmlAdapter;
import pl.droidsonroids.jspoon.Jspoon;

public class JSpoonSampleParser implements Parser {
    private final String DECODE_UTF8 = "UTF-8";

    @Override
    public OGData parse(InputStream inputStream) throws IOException {
        Jspoon jspoon = Jspoon.create();
        HtmlAdapter<JSpoonOGData> htmlAdapter = jspoon.adapter(JSpoonOGData.class);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, DECODE_UTF8);
        String html = readHtml(new BufferedReader(inputStreamReader));
        JSpoonOGData data = htmlAdapter.fromHtml(html);
        OGData og = new OGData();
        og.setTitle(data.title);
        og.setImage(data.image);
        og.setDescription(data.desc);
        og.setUrl(data.url);
        return og;
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
