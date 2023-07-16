package org.example;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.example.TestUtil.await;
import static org.junit.jupiter.api.Assertions.*;

class AutoClosableThreadLocalTest {
    @Test
    void callingGetCallsSupplierOnlyOnce() {
        int[] counters = new int[2];
        Thread t = new AutoClosableThread(() -> {
            AutoClosableThreadLocal<Integer> v = new AutoClosableThreadLocal<>(
                    () -> ++counters[0],
                    (Integer i) -> {
                        assertEquals(i, 1);
                        ++counters[1];
                    }
            );

            v.get();
            v.get();
            v.get();
        });
        await(t);

        assertEquals(counters[0], 1);
        assertEquals(counters[1], 1);
    }

    @Test
    void callingGetCallsSupplierOnlyOncePerThread() {
        AtomicInteger factory = new AtomicInteger();
        AtomicInteger closer = new AtomicInteger();

        Runnable r = () -> {
            AutoClosableThreadLocal<Integer> v = new AutoClosableThreadLocal<>(
                    factory::incrementAndGet,
                    (Integer i) -> {
                        /// One thread will get value 1 other will get 2
                        /// because of incrementAndGet
                        assertTrue(i == 1 || i == 2);
                        closer.incrementAndGet();
                    }
            );

            v.get();
            v.get();
            v.get();
        };
        Thread t1 = new AutoClosableThread(r);
        Thread t2 = new AutoClosableThread(r);
        await(t1);
        await(t2);

        /// Ensure both factory and closer are called twice
        assertEquals(factory.get(), 2);
        assertEquals(closer.get(), 2);
    }

    @Test
    void testFactoryIsNotCalledIfGetIsNotCalled() {
        Thread t = new AutoClosableThread(
                () -> new AutoClosableThreadLocal<>(
                        () -> {
                            fail();
                            return 0;
                        },
                        (Integer i) -> fail()
                )
        );
        await(t);
    }
}