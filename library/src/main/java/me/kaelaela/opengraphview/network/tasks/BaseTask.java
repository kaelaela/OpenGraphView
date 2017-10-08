package me.kaelaela.opengraphview.network.tasks;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public abstract class BaseTask<T> extends FutureTask<T> {

    private Handler handler = new Handler(Looper.getMainLooper());
    private OnLoadListener<T> listener;

    public interface OnLoadListener<T> {
        void onLoadSuccess(T t);

        void onLoadError(Throwable e);
    }

    BaseTask(@NonNull Callable<T> callable, OnLoadListener<T> listener) {
        super(callable);
        this.listener = listener;
    }

    void onSuccess(final T t) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onLoadSuccess(t);
            }
        });
    }

    void onError(final Throwable e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onLoadError(e);
            }
        });
    }
}
