package me.kaelaela.opengraphview.network.tasks;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class FaviconTask extends BaseTask<Bitmap> {

    public FaviconTask(@NonNull Callable<Bitmap> callable, OnLoadListener<Bitmap> listener) {
        super(callable, listener);
    }

    @Override
    protected void done() {
        super.done();
        Bitmap bm;
        if (isCancelled()) {
            return;
        }
        try {
            bm = get();
        } catch (InterruptedException | ExecutionException e) {
            onError(e);
            return;
        }
        if (bm == null) {
            onError(new IOException("Favicon is null."));
            return;
        }
        onSuccess(bm);
    }
}
