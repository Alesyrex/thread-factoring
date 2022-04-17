package com.epam.rd.autotasks;

import java.util.concurrent.TimeUnit;

public class ThreadsTerminateMonitorRunnable implements Runnable {
    public static final int TIMEOUT = 10;
    private final MyThreadUnion threadUnion;

    public ThreadsTerminateMonitorRunnable(MyThreadUnion threadUnion) {
        this.threadUnion = threadUnion;
    }

    @Override
    public void run() {
        while (!threadUnion.isFinished()) {

            threadUnion.calculationResult();
            try {
                TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
