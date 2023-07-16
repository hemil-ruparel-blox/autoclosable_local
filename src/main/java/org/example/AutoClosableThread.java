package org.example;

import java.util.ArrayList;

public class AutoClosableThread extends Thread {
    public AutoClosableThread(Runnable r) {
        super(r);
    }

    private final ArrayList<Runnable> cleanUps = new ArrayList<>();

    /// returns true if thread is AutoClosableThread otherwise false
    static boolean defer(Runnable r) {
        Thread t = Thread.currentThread();
        if (!(t instanceof AutoClosableThread)) {
            System.err.println("Called defer on non Autoclosable thread. Closers will have no effect");
            return false;
        }
        ((AutoClosableThread)t).cleanUps.add(r);
        return true;
    }

    @Override
    public void run() {
        try {
            super.run();
        } finally {
            for (int i = cleanUps.size() - 1; i >= 0; i--) {
                cleanUps.get(i).run();
            }
        }
    }
}
