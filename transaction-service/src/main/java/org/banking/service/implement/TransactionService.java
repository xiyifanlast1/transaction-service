package org.banking.service.implement;

import lombok.extern.slf4j.Slf4j;
import org.banking.infra.IRepository;
import org.banking.infra.base.ServiceBase;
import org.banking.infra.base.ServiceResult;
import org.banking.service.ITransactionService;
import org.banking.service.dtos.CreateTransactionInput;
import org.banking.service.dtos.Transaction;
import org.banking.service.dtos.UpdateTransactionInput;
import org.banking.service.entities.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
public class TransactionService extends ServiceBase implements ITransactionService {

    @Autowired
    IRepository<TransactionEntity> repository;

    private final String CACHE_PREFIX = "transaction.";

    @Override
    public ServiceResult<Transaction> create(CreateTransactionInput input) {
        var id = repository.insert(new TransactionEntity()
                .setAccount(input.getAccount())
                .setType(input.getType())
                .setProduct(input.getProduct())
                .setAmount(input.getAmount())
                .setRemark(input.getRemark()));
        if (StringUtils.hasText(id)) {
            log.error("succeed to create transaction {} for account: {}; type: {}; product: {}; amount: {}", id, input.getAccount(), input.getType(), input.getProduct(), input.getAmount());
            return success(map(repository.get(id)));
        } else {
            log.error("fail to create transaction for account: {}; type: {}; product: {}; amount: {}", input.getAccount(), input.getType(), input.getProduct(), input.getAmount());
            return fail("fail to create transaction");
        }
    }

    @Override
    public ServiceResult<Transaction> get(String id) {
        var value = repository.get(id);
        if (value != null) {
            return success(map(value));
        }
        return fail("cannot find transaction: " + id);
    }

    @Override
    public ServiceResult<List<Transaction>> find(int page, int size) {
        return success(repository.find(page, size).stream().map(this::map).toList());
    }

    @Override
    public ServiceResult update(UpdateTransactionInput input) {
        if (repository.exist(input.getId())) {
            repository.update(input.getId(), t -> {
                if (input.getAmount() != null) {
                    t.setAmount(input.getAmount());
                }
                if (StringUtils.hasText(input.getType())) {
                    t.setType(input.getType());
                }
                if (StringUtils.hasText(input.getRemark())) {
                    t.setRemark(input.getRemark());
                }
            });
            return success();
        } else {
            return fail("fail to update transaction as id not exist: " + input.getId());
        }
    }

    @Override
    public ServiceResult delete(String id) {
        repository.delete(id);
        return success();
    }

    private Transaction map(TransactionEntity entity) {
        Transaction result = null;
        if (entity != null) {
            result = new Transaction()
                    .setId(entity.getId())
                    .setType(entity.getType())
                    .setAccount(entity.getAccount())
                    .setProduct(entity.getProduct())
                    .setAmount(entity.getAmount())
                    .setRemark(entity.getRemark())
                    .setCreatedTime(entity.getCreatedTime());
        }
        return result;
    }
}
