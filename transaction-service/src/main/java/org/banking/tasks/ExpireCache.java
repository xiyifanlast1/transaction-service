package org.banking.tasks;

import org.banking.infra.ICache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpireCache {

    @Autowired
    ICache cache;

    @Scheduled(fixedRate = 30 * 1000, initialDelay = 30 * 1000)
    public void expire() {
        cache.expire();
    }
}
