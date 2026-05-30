package com.focusnode.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {
    // Java 21 Virtual Threads
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static void execute(Runnable task) {
        EXECUTOR.submit(task);
    }
    
    public static void shutdown() {
        EXECUTOR.shutdown();
    }
}
