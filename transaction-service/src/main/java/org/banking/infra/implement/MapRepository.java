package org.banking.infra.implement;

import org.banking.infra.IRepository;
import org.banking.infra.ITransaction;
import org.banking.infra.base.EntityBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Repository
public class MapRepository<T extends EntityBase> implements IRepository<T> {

    @Autowired
    ITransaction transaction;
    private final Map<String, T> data = new ConcurrentHashMap<>();
    private String table;

    public void init(String table) {
        this.table = table;
    }

    @Override
    public String insert(T value) {
        String id = null;
        if (value != null) {
            id = UUID.randomUUID().toString();
            value.setId(id);
            value.setCreatedTime(Instant.now());
            data.put(value.getId(), value);
        }
        return id;
    }

    @Override
    public void update(String id, Consumer<T> modify) {
        if (data.containsKey(id)) {
            transaction.execute(table + "." + id, () -> {
                var value = data.get(id);
                if (value != null) {
                    modify.accept(value);
                    data.put(value.getId(), value);
                }
            });
        }
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

    @Override
    public boolean exist(String id) {
        return data.containsKey(id);
    }
}
