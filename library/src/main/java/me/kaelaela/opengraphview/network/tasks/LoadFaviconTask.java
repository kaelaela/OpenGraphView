package me.kaelaela.opengraphview.network.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoadFaviconTask extends AsyncTask<String, Void, Bitmap> {

    private static final String BASE_URL = "http://www.google.com/s2/favicons?domain=";

    public abstract static class OnLoadListener {
        public void onLoadStart() {
        }

        public void onLoadSuccess(Bitmap bitmap) {
        }

        public void onLoadError() {
        }
    }

    private OnLoadListener listener;

    public LoadFaviconTask(OnLoadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... hosts) {
        listener.onLoadStart();
        String host = hosts[0];
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        Bitmap favicon = null;
        try {
            InputStream inputStream = new URL(BASE_URL + host).openStream();
            favicon = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            listener.onLoadError();
            e.printStackTrace();
        }
        return favicon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap == null) {
            listener.onLoadError();
        }
        listener.onLoadSuccess(bitmap);
    }
}
