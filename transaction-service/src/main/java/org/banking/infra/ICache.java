package org.banking.infra;

import java.util.List;
import java.util.Map;

public interface ICache {
    void setMany(Map<String, Object> values);
    <T> Map<String, T> getMany(List<String> keys, Class<T> clazz);
    void remove(String key);
    void expire();
}
