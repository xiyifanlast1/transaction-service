package org.banking.service;

import lombok.extern.slf4j.Slf4j;
import org.banking.infra.ICache;
import org.banking.infra.IRepository;
import org.banking.infra.base.ServiceResult;
import org.banking.service.dtos.CreateTransactionInput;
import org.banking.service.dtos.Transaction;
import org.banking.service.dtos.UpdateTransactionInput;
import org.banking.service.entities.TransactionEntity;
import org.banking.service.implement.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TransactionServiceTest {

    @Mock
    private IRepository<TransactionEntity> repository;

    @Mock
    private ICache cache;

    @InjectMocks
    private TransactionService service;

    private static final String TRANSACTION_ID = "123";
    private static final String ACCOUNT = "ACC12345";
    private static final String TYPE = "BUY";
    private static final String PRODUCT = "Apple";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(1000.00);

    private TransactionEntity mockEntity;
    private CreateTransactionInput createInput;
    private UpdateTransactionInput updateInput;

    @BeforeEach
    void setUp() {
        mockEntity = new TransactionEntity();
        mockEntity.setId(TRANSACTION_ID);
        mockEntity.setAccount(ACCOUNT);
        mockEntity.setType(TYPE);
        mockEntity.setProduct(PRODUCT);
        mockEntity.setAmount(AMOUNT);
        mockEntity.setCreatedTime(Instant.now());

        createInput = new CreateTransactionInput();
        createInput.setAccount(ACCOUNT);
        createInput.setType(TYPE);
        createInput.setProduct(PRODUCT);
        createInput.setAmount(AMOUNT);

        updateInput = new UpdateTransactionInput();
        updateInput.setId(TRANSACTION_ID);
        updateInput.setAmount(BigDecimal.valueOf(2000.00));
    }

    // ==================== create() 方法测试 ====================

    @Test
    void create_WithValidInput_ReturnsSuccess() {
        when(repository.insert(any(TransactionEntity.class))).thenReturn(TRANSACTION_ID);
        when(repository.get(TRANSACTION_ID)).thenReturn(mockEntity);

        ServiceResult<Transaction> result = service.create(createInput);

        assertEquals(0,result.getCode());
        assertNotNull(result.getData());
        assertEquals(TRANSACTION_ID, result.getData().getId());
        verify(repository, times(1)).insert(any(TransactionEntity.class));
    }

    @Test
    void create_WithNullInput_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(null);
        });
        assertEquals("create transaction input is null", exception.getMessage());
    }

    @Test
    void create_WithEmptyAccount_ThrowsException() {
        createInput.setAccount("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(createInput);
        });
        assertEquals("transaction account cannot be empty", exception.getMessage());
    }

    @Test
    void create_WithEmptyType_ThrowsException() {
        createInput.setType("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(createInput);
        });
        assertEquals("transaction type cannot be empty", exception.getMessage());
    }

    @Test
    void create_WithEmptyProduct_ThrowsException() {
        createInput.setProduct("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(createInput);
        });
        assertEquals("transaction product cannot be empty", exception.getMessage());
    }

    @Test
    void create_WithNegativeAmount_ThrowsException() {
        createInput.setAmount(BigDecimal.valueOf(-100));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(createInput);
        });
        assertEquals("transaction amount should be positive", exception.getMessage());
    }

    @Test
    void create_WithInsertFailure_ReturnsFailure() {
        when(repository.insert(any(TransactionEntity.class))).thenReturn(null);

        ServiceResult<Transaction> result = service.create(createInput);

        assertEquals(1, result.getCode());
        assertEquals("fail to create transaction", result.getMessage());
        verify(repository, times(1)).insert(any(TransactionEntity.class));
    }

    // ==================== get() 方法测试 ====================

    @Test
    void get_ExistingTransaction_ReturnsSuccess() {
        when(repository.get(TRANSACTION_ID)).thenReturn(mockEntity);

        ServiceResult<Transaction> result = service.get(TRANSACTION_ID);

        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(TRANSACTION_ID, result.getData().getId());
        verify(repository, times(1)).get(TRANSACTION_ID);
    }

    @Test
    void get_NonExistingTransaction_ReturnsFailure() {
        when(repository.get(TRANSACTION_ID)).thenReturn(null);
        ServiceResult<Transaction> result = service.get(TRANSACTION_ID);

        assertEquals(1,result.getCode());
        assertEquals("cannot find transaction: " + TRANSACTION_ID, result.getMessage());
        verify(repository, times(1)).get(TRANSACTION_ID);
    }

    // ==================== find() 方法测试 ====================

    @Test
    void find_WithNegativePage_ShouldUseDefaultPage() {
        when(repository.findIds(0, 10)).thenReturn(Collections.emptyList());

        service.find(-1, 10);

        verify(repository).findIds(0, 10);
    }

    @Test
    void find_WithNegativeSize_ShouldUseDefaultSize() {
        when(repository.findIds(0, 100)).thenReturn(Collections.emptyList());

        service.find(0, -5);

        verify(repository).findIds(0, 100);
    }

    @Test
    void find_WithAllCachedItems() {
        List<String> ids = Arrays.asList("1", "2", "3");
        Map<String, Transaction> cachedItems = new HashMap<>();
        ids.forEach(id -> cachedItems.put("transactions." + id, createMockDto(id)));

        when(repository.findIds(0, 100)).thenReturn(ids);
        when(cache.getMany(anyList(), eq(Transaction.class))).thenReturn(cachedItems);

        ServiceResult<List<Transaction>> result = service.find(0, 100);

        assertEquals(0,result.getCode());
        assertEquals(3, result.getData().size());
        verify(cache, times(1)).getMany(anyList(), eq(Transaction.class));
        verify(repository, never()).getByIds(anyList());
        verify(cache, never()).setMany(anyMap());
    }

    @Test
    void find_WithMixedCacheAndDb() {
        List<String> ids = Arrays.asList("1", "2", "3");
        Map<String, Transaction> cachedItems = new HashMap<>();
        cachedItems.put("transactions.1", createMockDto("1"));

        when(repository.findIds(0, 100)).thenReturn(ids);
        when(cache.getMany(anyList(), eq(Transaction.class))).thenReturn(cachedItems);
        when(repository.getByIds(Arrays.asList("2", "3"))).thenReturn(List.of(createMockEntity("2"), createMockEntity("3")));

        ServiceResult<List<Transaction>> result = service.find(0, 100);

        assertEquals(0,result.getCode());
        assertEquals(3, result.getData().size());
        verify(cache, times(1)).getMany(anyList(), eq(Transaction.class));
        verify(repository, times(1)).getByIds(Arrays.asList("2", "3"));
        verify(cache, times(1)).setMany(anyMap());
    }

    // ==================== update() 方法测试 ====================

    @Test
    void update_ExistingTransaction_ReturnsSuccess() {
        when(repository.exist(TRANSACTION_ID)).thenReturn(true);

        ServiceResult result = service.update(updateInput);

        assertEquals(0,result.getCode());
        verify(repository, times(1)).update(eq(TRANSACTION_ID), any());
        verify(cache, times(1)).remove("transactions." + TRANSACTION_ID);
    }

    @Test
    void update_NonExistingTransaction_ReturnsFailure() {
        when(repository.exist(TRANSACTION_ID)).thenReturn(false);

        ServiceResult result = service.update(updateInput);

        assertEquals(1,result.getCode());
        assertEquals("fail to update transaction as id not exist: " + TRANSACTION_ID, result.getMessage());
        verify(repository, never()).update(anyString(), any());
        verify(cache, never()).remove(anyString());
    }

    @Test
    void update_WithNullInput_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.update(null);
        });
        assertEquals("update transaction input is null", exception.getMessage());
    }

    @Test
    void update_WithEmptyId_ThrowsException() {
        updateInput.setId("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.update(updateInput);
        });
        assertEquals("transaction id cannot be empty", exception.getMessage());
    }

    @Test
    void update_WithNegativeAmount_ThrowsException() {
        updateInput.setAmount(BigDecimal.valueOf(-100));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.update(updateInput);
        });
        assertEquals("transaction amount should be positive", exception.getMessage());
    }

    // ==================== delete() 方法测试 ====================

    @Test
    void delete_ExistingTransaction_ReturnsSuccess() {
        ServiceResult result = service.delete(TRANSACTION_ID);

        assertEquals(0,result.getCode());
        verify(repository, times(1)).delete(TRANSACTION_ID);
        verify(cache, times(1)).remove("transactions." + TRANSACTION_ID);
    }

    // ==================== 辅助方法 ====================

    private Transaction createMockDto(String id) {
        return new Transaction()
                .setId(id)
                .setAccount("ACC" + id)
                .setAmount(BigDecimal.TEN);
    }

    private TransactionEntity createMockEntity(String id) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(id);
        entity.setAccount("ACC" + id);
        entity.setAmount(BigDecimal.TEN);
        return entity;
    }
}