package org.banking.service.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CreateTransactionInput {
    private String account;
    private String product;
    private BigDecimal amount;
    private String type;
    private String remark;
}
