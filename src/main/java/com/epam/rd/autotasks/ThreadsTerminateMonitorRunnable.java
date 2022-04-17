package com.epam.rd.autotasks;

public class ThreadsTerminateMonitorRunnable implements Runnable {
    private final MyThreadUnion threadUnion;

    public ThreadsTerminateMonitorRunnable(MyThreadUnion threadUnion) {
        this.threadUnion = threadUnion;
    }

    @Override
    public void run() {
        while (!threadUnion.isFinished()) {
            if (!threadUnion.isShutdown()) {
                threadUnion.calculationResult();
            }
        }
    }
}
