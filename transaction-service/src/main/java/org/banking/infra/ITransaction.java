package org.banking.infra;

public interface ITransaction {
    void execute(String key, Runnable action);
}
