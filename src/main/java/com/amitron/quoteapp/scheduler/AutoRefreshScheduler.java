/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Ngn
 */
public class AutoRefreshScheduler {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public void start(Runnable task) {

        scheduler.scheduleAtFixedRate(
                task,        // task to run
                0,           // start immediately
                1,           // repeat every
                TimeUnit.MINUTES
        );
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
