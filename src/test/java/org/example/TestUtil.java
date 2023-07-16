package org.example;

public class TestUtil {
    static void await(Thread t) {
        t.start();

        while (t.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
