package me.kaelaela.opengraphview.network.tasks;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import me.kaelaela.opengraphview.OGParser;
import me.kaelaela.opengraphview.network.model.OGData;

public class LoadOGDataTask extends AsyncTask<String, Void, OGData> {

    public abstract static class OnLoadListener {
        public void onLoadStart() {
        }

        public void onLoadSuccess(OGData ogData) {
        }

        public void onLoadError() {
        }
    }

    private OnLoadListener listener;
    private OGData data = new OGData();

    public LoadOGDataTask(OnLoadListener listener) {
        this.listener = listener;
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

    @Override
    protected OGData doInBackground(String... urls) {
        listener.onLoadStart();
        InputStream inputStream = null;
        try {
            inputStream = downloadUrl(urls[0]);
            data = OGParser.parse(inputStream);
        } catch (IOException e) {
            listener.onLoadError();
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    listener.onLoadError();
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    @Override
    protected void onPostExecute(OGData og) {
        super.onPostExecute(og);
        if (og == null) {
            listener.onLoadError();
            return;
        }
        listener.onLoadSuccess(og);
    }
}
