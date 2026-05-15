package com.example.factory_rent_car.Util;

import javafx.concurrent.Task;
import javafx.application.Platform;

public class AsyncTask {

    public static void ejecutar(Runnable dbWork, Runnable onSuccess) {
        ejecutar(dbWork, onSuccess, null);
    }

    public static void ejecutar(Runnable dbWork, Runnable onSuccess, Runnable onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                dbWork.run();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.run();
        });
        task.setOnFailed(e -> {
            Throwable err = task.getException();
            if (err != null) err.printStackTrace();
            if (onError != null) onError.run();
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
