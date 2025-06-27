package org.banking.infra.implement;

import org.banking.infra.IRepository;
import org.banking.infra.base.EntityBase;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class MapRepository<T extends EntityBase> implements IRepository<T> {
    private final Map<String, T> data = new ConcurrentHashMap<>();

    @Override
    public T save(T value) {
        if (value != null) {
            String id = UUID.randomUUID().toString();
            value.setId(id);
            value.setCreatedTime(Instant.now());
            data.put(value.getId(), value);
        }
        return value;
    }

    @Override
    public T get(String id) {
        return data.get(id);
    }

    @Override
    public List<T> find(int page, int size) {
        int start = Math.min(page * size, data.size());
        return data.values().stream()
                .sorted(Comparator.comparing(T::getCreatedTime).reversed())
                .skip(start)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        data.remove(id);
    }
}
