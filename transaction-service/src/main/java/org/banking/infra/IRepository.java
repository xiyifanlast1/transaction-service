package org.banking.infra;

import org.banking.infra.base.EntityBase;

import java.util.List;
import java.util.function.Consumer;

public interface IRepository<T extends EntityBase>{
    String insert(T value);
    void update(String id, Consumer<T> modify);
    T get(String id);
    List<T> find(int page, int size);
    void delete(String id);
    boolean exist(String id);
}
