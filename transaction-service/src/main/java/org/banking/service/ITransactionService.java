package org.banking.service;

import org.banking.infra.base.ServiceResult;
import org.banking.service.dtos.CreateTransactionInput;
import org.banking.service.dtos.Transaction;
import org.banking.service.dtos.UpdateTransactionInput;

import java.util.List;

public interface ITransactionService {
    ServiceResult<Transaction> create(CreateTransactionInput input);

    ServiceResult<Transaction> get(String id);

    ServiceResult<List<Transaction>> find(int page, int size);

    ServiceResult update(UpdateTransactionInput input);
    ServiceResult delete(String id);
}
