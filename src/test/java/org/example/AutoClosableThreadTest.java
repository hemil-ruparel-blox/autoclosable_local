package org.example;

import org.junit.jupiter.api.Test;

import static org.example.AutoClosableThread.defer;
import static org.example.TestUtil.await;
import static org.junit.jupiter.api.Assertions.*;

class AutoClosableThreadTest {
    @Test
    void deferReturnsTrueIfThreadIsAutoClosable() {
        Thread t = new AutoClosableThread(() -> assertTrue(defer(() -> {})));
        await(t);
    }

    @Test
    void deferReturnsFalseIfThreadIsNotAutoClosable() {
        Thread t = new Thread(() -> assertFalse(defer(() -> {})));
        await(t);
    }
}