package org.banking.infra.implement;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.banking.infra.ICache;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MapCache implements ICache {

    private final Map<String, TimeObject> data = new ConcurrentHashMap<>();

    @Override
    public void setMany(Map<String, Object> values) {
        for(var val:values.entrySet()) {
            data.put(val.getKey(),new TimeObject().setTime(Instant.now().plusMillis(getRandom())).setData(val.getValue()));
        }
    }
    @Override
    public <T> Map<String, T> getMany(List<String> keys, Class<T> clazz) {
        Map<String, T> result = new HashMap<>();
        for (var key : keys) {
            var value = data.get(key);
            if(value!=null)
            {
                var val=value.getData();
                if (clazz.isInstance(val)) {
                    result.put(key, clazz.cast(val));
                }
            }
        }
        return result;
    }

    @Override
    public void remove(String key) {
        data.remove(key);
    }

    @Override
    public void expire() {
        for(var e:data.entrySet())
        {
            // expire in 10min
            if(e.getValue().getTime().plusSeconds(10*60).isBefore(Instant.now()))
            {
                data.remove(e.getKey());
            }
        }
    }

    private long getRandom()
    {
        long min = 1L;
        long max = 500L;
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    @Data
    @Accessors(chain = true)
    public static class TimeObject
    {
        private Instant time;
        private Object data;
    }
}
