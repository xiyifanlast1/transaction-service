package org.banking.service.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class Transaction {
    private String id;
    private String account;
    private String product;
    private String type;
    private BigDecimal amount;
    private String remark;
    private Instant createdTime;
}
