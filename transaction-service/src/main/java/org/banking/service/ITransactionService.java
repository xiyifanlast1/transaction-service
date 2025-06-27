package org.banking.service;

import org.banking.infra.base.ServiceResult;
import org.banking.service.dtos.Transaction;

import java.util.List;

public interface ITransactionService {
    ServiceResult<Transaction> create(Transaction transaction);

    ServiceResult<Transaction> get(String id);

    ServiceResult<List<Transaction>> find(int page, int size);

    ServiceResult update(Transaction transaction);
    ServiceResult delete(String id);
}
