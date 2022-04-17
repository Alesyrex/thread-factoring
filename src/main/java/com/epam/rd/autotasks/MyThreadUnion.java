package com.epam.rd.autotasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyThreadUnion implements ThreadUnion {
    public static final String THREAD_NAME_FORMAT = "%s-worker-%s";
    public static final int TIMEOUT = 100;

    private final String threadUnionName;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private List<Thread> threads = new ArrayList<>();
    private final List<FinishedThreadResult> listResult = new ArrayList<>();
    private volatile boolean shutdownProcess;

    public MyThreadUnion(String threadUnionName) {
        this.threadUnionName = threadUnionName;
        Thread.setDefaultUncaughtExceptionHandler((Thread thread, Throwable e) -> {
            synchronized (this) {
                listResult.add(new FinishedThreadResult(thread.getName(), e));
                threads.remove(thread);
            }
        });
    }

    public synchronized void calculationResult() {
        if (!threads.isEmpty()) {
            threads = threads.stream()
                    .flatMap((Thread thread) -> {
                        if (thread.getState() == Thread.State.TERMINATED) {
                            listResult.add(new FinishedThreadResult(thread.getName()));
                        }
                        return Stream.of(thread);
                    })
                    .filter(thread -> thread.getState() != Thread.State.TERMINATED)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public int totalSize() {
        return threadNumber.get();
    }

    @Override
    public synchronized int activeSize() {
        AtomicInteger activeThreadCount = new AtomicInteger(0);
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                activeThreadCount.incrementAndGet();
            }
        }
        return activeThreadCount.get();
    }

    @Override
    public synchronized void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        shutdownProcess = true;
    }

    @Override
    public boolean isShutdown() {
        return shutdownProcess;
    }

    @Override
    public void awaitTermination() {
        while (activeSize() != 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return activeSize() == 0 && isShutdown();
    }

    @Override
    public List<FinishedThreadResult> results() {
        calculationResult();
        return listResult;
    }

    @Override
    public Thread newThread(Runnable runnable) throws IllegalStateException {
        Thread thread;
        if (!isShutdown()) {
            thread = new Thread(runnable);
            thread.setName(String.format(THREAD_NAME_FORMAT, threadUnionName, threadNumber.getAndIncrement()));
            synchronized (this) {
                threads.add(thread);
            }
        } else {
            throw new IllegalStateException("Shutdown in progress");
        }
        return thread;
    }
}
