/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils;

import javafx.concurrent.Task;
import java.util.function.Consumer;

/**
 *
 * @author Ngn
 */
public class BackgroundTaskRunner {

    public static <T> void run(
            Task<T> task,
            Runnable onStart,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError
    ) {

        if (onStart != null) {
            onStart.run();
        }

        task.setOnSucceeded(e -> {
            if (onSuccess != null) {
                onSuccess.accept(task.getValue());
            }
        });

        task.setOnFailed(e -> {
            if (onError != null) {
                onError.accept(task.getException());
            }
        });

        new Thread(task).start();
    }
}
