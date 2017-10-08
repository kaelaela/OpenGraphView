package me.kaelaela.opengraphview.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import me.kaelaela.opengraphview.network.tasks.BaseTask;

public class DefaultTaskManager {

    private static DefaultTaskManager manager;
    private ExecutorService executor = new ThreadPoolExecutor(
            Math.max(Runtime.getRuntime().availableProcessors(), 4), Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    private BaseTask currentTask;

    public static DefaultTaskManager getInstance() {
        if (manager == null) {
            manager = new DefaultTaskManager();
        }
        return manager;
    }

    /*
    * Set your executor.
    */
    public void setExecutor(ExecutorService executor) {
        this.executor.shutdown();
        this.executor = executor;
    }

    private DefaultTaskManager() {
    }

    public void execute(BaseTask task) {
        currentTask = task;
        executor.execute(task);
    }

    public void cancelTask() {
        if (currentTask.isCancelled() || currentTask.isDone()) {
            return;
        }
        currentTask.cancel(false);
    }
}
