package me.kaelaela.opengraphview.network.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import me.kaelaela.opengraphview.OGParser;
import me.kaelaela.opengraphview.Parser;
import me.kaelaela.opengraphview.network.model.OGData;

public class OGDataCallable implements Callable<OGData> {

    private final String url;
    private final Parser parser;

    public OGDataCallable(String url, Parser parser) {
        this.url = url;
        this.parser = parser == null ? new OGParser() : parser;
    }

    @Override
    public OGData call() throws Exception {
        InputStream is = null;
        OGData data = null;
        try {
            is = downloadUrl(url);
            data = parser.parse(is);
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //nop
                }
            }
        }
        return data;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}
