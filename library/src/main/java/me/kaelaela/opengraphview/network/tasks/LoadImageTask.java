package me.kaelaela.opengraphview.network.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    public abstract static class OnLoadListener {
        public void onLoadStart() {
        }

        public void onLoadSuccess(Bitmap bitmap) {
        }

        public void onLoadError() {
        }
    }

    private OnLoadListener listener;

    public LoadImageTask(OnLoadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        listener.onLoadStart();
        String url = urls[0];
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Bitmap image = null;
        try {
            InputStream inputStream = new URL(url).openStream();
            image = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            listener.onLoadError();
            e.printStackTrace();
        }
        return image;
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
