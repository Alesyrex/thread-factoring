package com.epam.rd.autotasks;

public class ThreadUnionExecutor {
    public static ThreadUnion getInstance(String name) {
        MyThreadUnion threadUnion = new MyThreadUnion(name);
        Thread monitor = new Thread(new ThreadsTerminateMonitorRunnable(threadUnion));
        monitor.start();
        return threadUnion;
    }
}
