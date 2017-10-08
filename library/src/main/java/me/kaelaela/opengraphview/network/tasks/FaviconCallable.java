package me.kaelaela.opengraphview.network.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class FaviconCallable implements Callable<Bitmap> {
    private static final String BASE_URL = "http://www.google.com/s2/favicons?domain=";
    private final String host;

    public FaviconCallable(String host) {
        this.host = host;
    }

    @Override
    public Bitmap call() throws Exception {
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        Bitmap favicon;
        try {
            InputStream is = new URL(BASE_URL + host).openStream();
            favicon = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return null;
        }
        return favicon;
    }
}
