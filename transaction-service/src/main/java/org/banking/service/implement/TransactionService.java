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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class TransactionService extends ServiceBase implements ITransactionService {

    @Autowired
    IRepository<TransactionEntity> repository;

    @Override
    public ServiceResult<Transaction> create(CreateTransactionInput input) {
        if (input == null) {
            throw new IllegalArgumentException("create transaction input is null");
        }
        if (!StringUtils.hasText(input.getAccount())) {
            throw new IllegalArgumentException("transaction account cannot be empty");
        }
        if (!StringUtils.hasText(input.getType())) {
            throw new IllegalArgumentException("transaction type cannot be empty");
        }
        if (!StringUtils.hasText(input.getProduct())) {
            throw new IllegalArgumentException("transaction product cannot be empty");
        }
        if (input.getAmount() == null || input.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("transaction amount should be positive");
        }
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
    @Cacheable(value = "transactions", key = "#id")
    public ServiceResult<Transaction> get(String id) {
        var value = repository.get(id);
        if (value != null) {
            return success(map(value));
        }
        return fail("cannot find transaction: " + id);
    }

    @Override
    public ServiceResult<List<Transaction>> find(int page, int size) { // page 0 is the first page, size default 100
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 100;
        }
        return success(repository.find(page, size).stream().map(this::map).toList());
    }

    @Override
    @CacheEvict(value = "transactions", key = "#input.id")
    public ServiceResult update(UpdateTransactionInput input) {
        if (input == null) {
            throw new IllegalArgumentException("update transaction input is null");
        }
        if (!StringUtils.hasText(input.getId())) {
            throw new IllegalArgumentException("transaction id cannot be empty");
        }
        if (input.getAmount() != null && input.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("transaction amount should be positive");
        }
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
    @CacheEvict(value = "transactions", key = "#id")
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
