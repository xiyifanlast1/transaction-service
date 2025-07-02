package org.banking.infra.implement;

import org.banking.infra.ITransaction;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LocalLockTransaction implements ITransaction {
    private final Map<String, ReentrantLock> locks=new ConcurrentHashMap<>();
    @Override
    public void execute(String key, Runnable action) {
        ReentrantLock itemLock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        itemLock.lock();
        try {
            action.run();
        }
        finally {
            locks.remove(key);
            itemLock.unlock();
        }
    }
}
