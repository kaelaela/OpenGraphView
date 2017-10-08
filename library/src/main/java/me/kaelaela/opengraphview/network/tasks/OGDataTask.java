package me.kaelaela.opengraphview.network.tasks;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import me.kaelaela.opengraphview.network.model.OGData;

public class OGDataTask extends BaseTask<OGData> {

    public OGDataTask(@NonNull Callable<OGData> callable, OnLoadListener<OGData> listener) {
        super(callable, listener);
    }

    @Override
    protected void done() {
        super.done();
        OGData data;
        if (isCancelled()) {
            return;
        }
        try {
            data = get();
        } catch (InterruptedException | ExecutionException e) {
            onError(e);
            return;
        }
        if (data == null) {
            onError(new IOException("No cache data."));
            return;
        }
        onSuccess(data);
    }
}
