package org.banking.infra;

import org.banking.infra.base.EntityBase;

import java.util.List;
import java.util.function.Consumer;

public interface IRepository<T extends EntityBase>{
    T save(T value);
    T get(String id);
    List<T> find(int page, int size);
    void delete(String id);
}
