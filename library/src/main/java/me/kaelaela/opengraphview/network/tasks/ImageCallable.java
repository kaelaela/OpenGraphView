package me.kaelaela.opengraphview.network.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class ImageCallable implements Callable<Bitmap> {
    private final String url;

    public ImageCallable(String url) {
        this.url = url;
    }

    @Override
    public Bitmap call() throws Exception {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Bitmap image;
        try {
            InputStream is = new URL(url).openStream();
            image = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return null;
        }
        return image;
    }
}
