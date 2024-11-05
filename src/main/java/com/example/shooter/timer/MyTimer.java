package com.example.shooter.timer;

import javafx.animation.AnimationTimer;

import java.util.*;
import java.util.concurrent.Semaphore;

public class MyTimer extends AnimationTimer {
    private long last;
    private final Vector<Updatable> updatables;

    public MyTimer(Updatable... updatables) {
        this.updatables = new Vector<>();
        this.updatables.addAll(Arrays.asList(updatables));
    }

    @Override
    public synchronized void handle(long now) {
        if (this.last == 0) {
            this.last = now;
        }

        long dns = now - this.last;
        this.last = now;
        this.updatables.removeIf(updatable -> updatable.update(dns));
    }

    public synchronized void add(Updatable updatable) {
        this.updatables.add(updatable);
    }
}
