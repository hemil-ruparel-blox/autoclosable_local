package org.example;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.example.AutoClosableThread.defer;

public class AutoClosableThreadLocal<T> {
    final Supplier<T> factory;
    final Consumer<T> closer;
    final ThreadLocal<T> threadLocal;
    final Set<Thread> added = ConcurrentHashMap.newKeySet();

    public AutoClosableThreadLocal(Supplier<T> factory, Consumer<T> closer) {
        this.factory = factory;
        this.closer = closer;
        this.threadLocal = ThreadLocal.withInitial(factory);
    }

    T get() {
        Thread current = Thread.currentThread();
        if (!added.contains(current)) {
            added.add(current);
            defer(() -> closer.accept(get()));
        }
        return threadLocal.get();
    }
}
