import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class MyExecutor implements ExecutorService {
    private final LinkedBlockingQueue<Runnable> BlockingQueue;
    private final List<Thread> workers;
    private volatile boolean isShutdown;

    public MyExecutor(int capacity) {
        this.BlockingQueue = new LinkedBlockingQueue<>();
        this.workers = new ArrayList<>();
        this.isShutdown = false;

        // Start worker threads
        for (int i = 0; i < capacity; i++) {
            Thread worker = new Thread(() -> {
                while (!isShutdown || !BlockingQueue.isEmpty()) {
                    try {
                        Runnable task = BlockingQueue.poll(1, TimeUnit.SECONDS);
                        if (task != null) {
                            task.run();
                        }
                    } catch (InterruptedException ignored) {}
                }
            });
            worker.start();
            workers.add(worker);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        throw new UnsupportedOperationException("Unimplemented method 'submit'");
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        throw new UnsupportedOperationException("Unimplemented method 'submit'");
    }

    @Override
    public Future<?> submit(Runnable task) {
        throw new UnsupportedOperationException("Unimplemented method 'submit'");
    }

    @Override
public void execute(Runnable command) {
    if (isShutdown) {
        throw new RejectedExecutionException("Executor has been shut down");
    }
    try {
        BlockingQueue.put(command); 
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}


    public void shutdown() {
    isShutdown = true;
    for (Thread worker : workers) {
        worker.interrupt(); 
    }
}


    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown && BlockingQueue.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        for (Thread worker : workers) {
            long timeLeft = deadline - System.nanoTime();
            if (timeLeft > 0) {
                worker.join(TimeUnit.NANOSECONDS.toMillis(timeLeft));
            }
        }
        return isTerminated();
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        List<Runnable> remainingTasks = new ArrayList<>();
        BlockingQueue.drainTo(remainingTasks);
        return remainingTasks;
    }


    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("Unimplemented method 'invokeAll'");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException("Unimplemented method 'invokeAll'");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("Unimplemented method 'invokeAny'");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("Unimplemented method 'invokeAny'");
    }
}
