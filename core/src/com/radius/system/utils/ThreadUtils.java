package com.radius.system.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(12);

    private ThreadUtils() {}

    public static void Stop() {
        EXECUTOR.shutdownNow();
    }
}
